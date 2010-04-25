package co.uk.gauntface.android.mobileeye;

import java.util.ArrayList;

import co.uk.gauntface.android.mobileeye.imageprocessing.AreaExtraction;
import co.uk.gauntface.android.mobileeye.imageprocessing.ImagePackage;
import co.uk.gauntface.android.mobileeye.imageprocessing.Peak;
import co.uk.gauntface.android.mobileeye.imageprocessing.QuickSegment;
import co.uk.gauntface.android.mobileeye.imageprocessing.QuickSegmentFactory;
import co.uk.gauntface.android.mobileeye.imageprocessing.RegionGroup;
import co.uk.gauntface.android.mobileeye.imageprocessing.Utility;
import co.uk.gauntface.android.mobileeye.imageprocessing.YUVPixel;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.text.format.Time;
import android.util.Log;

public class ImageProcessingThread extends Thread
{
	private double ROTATE_LEFT_RIGHT_MAX = 20.66;
	
	private Size mImageSize;
	private byte[] mData;
	private boolean mLogData;
	
	public ImageProcessingThread(Size imageSize, byte[] data, boolean logData)
	{
		mImageSize = imageSize;
		mData = data;
		mLogData = logData;
	}
	
	public void run()
	{
		mData = mData.clone();
		
		int scaleDownFactor = 3;
		
		
		int targetWidth = mImageSize.width;
		int targetHeight = mImageSize.height;
		boolean targetSizeScaled = false;
		int topOffset = 0;
		int leftOffset = 0;
		
		if(Singleton.getApplicationState() == Singleton.STATE_SETTING_UP_PROJECTION)
		{
			RegionGroup r = Singleton.getLastProjectedArea();
			targetWidth = r.getBottomRightX() - r.getTopLeftX();
			targetHeight = r.getBottomRightY() - r.getTopLeftY();
			targetSizeScaled = true;
			topOffset = r.getTopLeftY();
			leftOffset = r.getTopLeftX();
		}
		
		
		
		YUVPixel yuvPixel = new YUVPixel(mData, mImageSize.width, mImageSize.height, leftOffset, topOffset, targetWidth, targetHeight, scaleDownFactor, targetSizeScaled);
		
		QuickSegment quickSegment = QuickSegmentFactory.getQuickSegment();
		ImagePackage imgPackage = quickSegment.segmentImage(yuvPixel.getPixels(), 
				mLogData,
				yuvPixel.getImgWidth(),
				yuvPixel.getImgHeight());
		
		// segmentImage returns null when there is no extractable region
		if(imgPackage != null)
		{
			if(Singleton.getApplicationState() == Singleton.STATE_FINDING_AREA)
			{
				imgPackage = AreaExtraction.getExtraction(imgPackage);
				RegionGroup areaExtraction = imgPackage.getExtractionArea();
				
				if(foundGoodArea(areaExtraction, imgPackage.getImgWidth(), imgPackage.getImgHeight()) == true)
				{
					Singleton.setLastProjectedArea(imgPackage.getExtractionArea());
					Singleton.setLastProjectedImgWidth(imgPackage.getImgWidth());
					Singleton.setLastProjectedImgHeight(imgPackage.getImgHeight());
					Singleton.setLastProjectedAreaAverage(imgPackage.getAveragePixelValue());
					
					Singleton.setApplicationState(Singleton.STATE_SETTING_UP_PROJECTION);
					Singleton.setStableAreaCount(0);
				}
				
				//Bitmap b = Utility.renderBitmap(imgPackage.getRegionGroupPixels(), imgPackage.getImgWidth(), imgPackage.getImgHeight(), true);
				
				Bitmap b = Utility.renderBitmap(imgPackage.getAreaExtractionPixels(), imgPackage.getImgWidth(), imgPackage.getImgHeight(), true);
				Singleton.updateImageView = b;
			}
			else if(Singleton.getApplicationState() == Singleton.STATE_SETTING_UP_PROJECTION)
			{
				RegionGroup lastExtraction = Singleton.getLastProjectedArea();
				imgPackage.setRegionGroup(lastExtraction);
				imgPackage.setAveragePixelValue(yuvPixel.getAveragePixelValue());
				imgPackage.setExtractionArea(lastExtraction);
				double prevAvg = Singleton.getLastProjectedAreaAverage();
				
				int stableAreaCount = Singleton.getStableAreaCount();
				
				if(stableAreaCount > (3*10^9))
				{
					// Display the markers
					if(Singleton.hasVoiceCommandBeenSent() == false)
					{
						//Log.d("mobileeye", "Top (x, y) - ("+lastExtraction.getTopLeftX()+","+lastExtraction.getTopLeftY()+")");
						//Log.d("mobileeye", "Bottom (x, y) - ("+lastExtraction.getBottomRightX()+","+lastExtraction.getBottomRightY()+")");
						// Using the previous iterations extraction area
						int centerX = lastExtraction.getTopLeftX() +
							((lastExtraction.getBottomRightX() - lastExtraction.getTopLeftX()) / 2);
						int centerY = lastExtraction.getTopLeftY() +
							((lastExtraction.getBottomRightY() - lastExtraction.getTopLeftY()) / 2);
						
						double rLeftRight = 0;
						double rUpDown = 0;
						
						double halfImgWidth = Singleton.getLastProjectedImgWidth() / 2.0;
						//Log.d("mobileeye", "Half Img Width - " + halfImgWidth);
						
						// Offset to center of image = 0 degrees
						double relativeX = centerX - halfImgWidth;
						relativeX = relativeX / halfImgWidth;
						//Log.d("mobileeye", "RelativeX / halfImgWidth - " + relativeX);
						
						double rotateLeftRight = relativeX * ROTATE_LEFT_RIGHT_MAX;
						//Log.d("mobileeye", "RotateLeftRight - " + rotateLeftRight);
						
						// Round up by ten then make int then divide by 10
						rotateLeftRight = rotateLeftRight * 10;
						int temp = (int) rotateLeftRight;
						rotateLeftRight = ((double) temp) / 10;
						
						Message msg = CameraWrapper.mHandler.obtainMessage();
						msg.arg1 = CameraActivity.ROTATE_PROJECTOR_VIEW;
						
						Bundle data = new Bundle();
						data.putDouble(CameraActivity.ROTATE_LEFT_RIGHT_KEY, rotateLeftRight);
						data.putDouble(CameraActivity.ROTATE_UP_DOWN_KEY, 0);
						
						msg.setData(data);
						
						CameraWrapper.mHandler.dispatchMessage(msg);
						
						Singleton.voiceCommandSent();
						
						Singleton.setApplicationState(Singleton.STATE_PROJECTING_MARKERS);
					}
				}
				if(averagesApproximatelyMatch(prevAvg, yuvPixel.getAveragePixelValue()))
				{
					stableAreaCount = stableAreaCount + 1;
					Singleton.setStableAreaCount(stableAreaCount);
					
					/**else
					{
						long elapsed = Singleton.timeElapsed(System.nanoTime());
						
						if(elapsed >= 10)
						{
							Singleton.setApplicationState(Singleton.STATE_FINDING_AREA);
						}
					}**/
					
				}
				else
				{
					imgPackage = AreaExtraction.getExtraction(imgPackage);
					RegionGroup newAreaExtraction = imgPackage.getExtractionArea();
					
					if(newAreaExtraction.getRegionSize() >= (lastExtraction.getRegionSize() * 0.7))
					{
						stableAreaCount = stableAreaCount + 1;
						Singleton.setStableAreaCount(stableAreaCount);
						
						int newTopLeftX = lastExtraction.getTopLeftX() + newAreaExtraction.getTopLeftX();
						int newTopLeftY = lastExtraction.getTopLeftY() + newAreaExtraction.getTopLeftY();
						
						int newBottomRightX = newTopLeftX + newAreaExtraction.getBottomRightX();
						int newBottomRightY = newTopLeftY + newAreaExtraction.getBottomRightY();
						
						Log.d("mobileeye", "Old - ("+lastExtraction.getTopLeftX()+","+lastExtraction.getTopLeftY()+") ("
								+lastExtraction.getBottomRightX()+","+lastExtraction.getBottomRightX()+")");
						Log.d("mobileeye", "New - ("+newTopLeftX+","+newTopLeftY+") ("
								+newBottomRightX+","+newBottomRightY+")");
						
						RegionGroup r = new RegionGroup(newTopLeftX, newTopLeftY, newBottomRightX, newBottomRightY);
						Singleton.setLastProjectedArea(r);
						Singleton.setLastProjectedAreaAverage(yuvPixel.getAveragePixelValue());
					}
					else
					{
						Singleton.setApplicationState(Singleton.STATE_FINDING_AREA);
						
						Message msg = CameraWrapper.mHandler.obtainMessage();
						msg.arg1 = CameraActivity.APPLICATION_STATE_CHANGED;
						
						CameraWrapper.mHandler.dispatchMessage(msg);
					}
				}
			}
			/**if(extractionArea != null)
			{
				// Using the previous iterations extraction area
				int centerX = extractionArea.getTopLeftX() +
					((extractionArea.getBottomRightX() - extractionArea.getTopLeftX()) / 2);
				int centerY = extractionArea.getTopLeftY() +
					((extractionArea.getBottomRightY() - extractionArea.getTopLeftY()) / 2);
				
				double rLeftRight = 0;
				double rUpDown = 0;
				
				double halfImgWidth = imgPackage.getImgWidth() / 2.0;
				
				double relativeX = centerX - halfImgWidth;
				relativeX= relativeX / halfImgWidth;
				
				double rotateLeftRight = relativeX * ROTATE_LEFT_RIGHT_MAX;
				
				rotateLeftRight = rotateLeftRight * 10;
				int temp = (int) rotateLeftRight;
				rotateLeftRight = ((double) temp) / 10;
				
				Message msg = CameraWrapper.mHandler.obtainMessage();
				msg.arg1 = CameraActivity.ROTATE_PROJECTOR_VIEW;
				
				Bundle data = new Bundle();
				data.putDouble(CameraActivity.ROTATE_LEFT_RIGHT_KEY, rotateLeftRight);
				data.putDouble(CameraActivity.ROTATE_UP_DOWN_KEY, 0);
				
				msg.setData(data);
				
				CameraWrapper.mHandler.dispatchMessage(msg);
			}**/
			
			if(mLogData == true)
			{
				logData(yuvPixel, imgPackage);
			}
			
			Message msg = CameraWrapper.mHandler.obtainMessage();
			msg.arg1 = CameraActivity.DRAW_IMAGE_PROCESSING;
			
			CameraWrapper.mHandler.dispatchMessage(msg);
		}
	}
	
	private boolean averagesApproximatelyMatch(double prevAvg, double averagePixelValue)
	{
		if(Math.abs(prevAvg - averagePixelValue) < 8)
		{
			return true;
		}
		//Log.d("mobileeye", "BAD BAD BAD AVG");
		//Log.d("mobileeye", "prevAvg = "+prevAvg+" newAvg = "+averagePixelValue+" Average Match - " + Math.abs(prevAvg - averagePixelValue));
		return false;
	}

	private boolean foundGoodArea(RegionGroup areaExtraction, int imgWidth, int imgHeight)
	{
		if(areaExtraction.getRegionSize() > ((imgWidth * imgHeight) * 0.2))
		{
			return true;
		}
		return false;
	}

	private void logData(YUVPixel yuvPixel, ImagePackage imgPackage)
	{
		Time time = new Time();
		time.setToNow();
		
		String currentTime = time.format("%Y%m%d%H%M%S");
		currentTime = currentTime + "_";
		
		Utility.setFilePrePend(currentTime);
		
		Bitmap temp = Utility.renderBitmap(yuvPixel.getPixels(),
				yuvPixel.getImgWidth(),
				yuvPixel.getImgHeight(),
				true);
		Utility.saveImageToSDCard(temp, "1_B&W.png");
		
		String s = new String();
		int[] hist = imgPackage.getHistogram();
		for(int i = 0; i < hist.length; i++)
		{
			s = s + hist[i]+"\n";
		}
		
		Utility.saveTextToSDCard(s, "2_HistogramData.txt");
		
		ArrayList<Peak> pixelGroups = imgPackage.getInitPixelGroups();
		s = "<Min> <Peak> <Max> <PeakSize>\n";
		for(int i = 0; i < pixelGroups.size(); i++)
		{
			Peak p = pixelGroups.get(i);
			s = s + p.getMinIndex()+" "+p.getPeakIndex()+" "+p.getMaxIndex()+" "+p.getPeakSize()+"\n";
		}
		
		Utility.saveTextToSDCard(s, "3_InitPixelGroups.txt");
		
		Peak[] finalPixelGroups = imgPackage.getFinalPixelGroups();
		s = "<Min> <Peak> <Max> <PeakSize>\n";
		for(int i = 0; i < finalPixelGroups.length; i++)
		{
			Peak p = finalPixelGroups[i];
			s = s + p.getMinIndex()+" "+p.getPeakIndex()+" "+p.getMaxIndex()+" "+p.getPeakSize()+"\n";
		}
		
		Utility.saveTextToSDCard(s, "4_FinalPixelGroups.txt");
		
		Peak usedPixelGroup = imgPackage.getUsedPixelGroup();
		s = "<Min> <Peak> <Max> <PeakSize>\n";
		s = s + usedPixelGroup.getMinIndex() + " " + usedPixelGroup.getPeakIndex() + " " + usedPixelGroup.getMaxIndex() + " " + usedPixelGroup.getPeakSize();
		
		Utility.saveTextToSDCard(s, "5_UsedPixelGroup.txt");
		
		temp = Utility.renderBitmap(imgPackage.getRegionGroupPixels(),
				imgPackage.getImgWidth(),
				imgPackage.getImgHeight(),
				true);
		Utility.saveImageToSDCard(temp, "6_Segment.png");
		
		RegionGroup r = imgPackage.getRegionGroup();
		s = "("+r.getTopLeftX()+","+r.getTopLeftY()+") ("+r.getBottomRightX()+","+r.getBottomRightY()+")\n";
		s = s + "center: ("+r.getWeightedCenter().getArg1()+","+r.getWeightedCenter().getArg2()+")";
		Utility.saveTextToSDCard(s, "7_FinalRegion");
		
		r = imgPackage.getExtractionArea();
		s = "("+r.getTopLeftX()+","+r.getTopLeftY()+") ("+r.getBottomRightX()+","+r.getBottomRightY()+")";
		Utility.saveTextToSDCard(s, "8_ExtractionArea");
		
		temp = Utility.renderBitmap(imgPackage.getAreaExtractionPixels(),
				imgPackage.getImgWidth(), imgPackage.getImgHeight(), true);
		Utility.saveImageToSDCard(temp, "9_Area.png");
	}
}
