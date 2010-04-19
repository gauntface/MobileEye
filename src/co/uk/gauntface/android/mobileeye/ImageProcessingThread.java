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
		
		Bitmap b = null;
		
		int scaleDownFactor = 3;
		
		int targetWidth = mImageSize.width;
		int targetHeight = mImageSize.height;
		boolean targetSizeScaled = false;
		int topOffset = 0;
		int leftOffset = 0;
		
		
		
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
					Singleton.setApplicationState(Singleton.STATE_SETTING_UP_PROJECTION);
				}
			}
			else if(Singleton.getApplicationState() == Singleton.STATE_SETTING_UP_PROJECTION)
			{
				double prevAvg = Singleton.getLastProjectedAreaAverage();
				if(prevAvg < 0)
				{
					Singleton.setLastProjectedAreaAverage(yuvPixel.getAveragePixelValue());
				}
				else if(averagesApproximatelyMatch(prevAvg, yuvPixel.getAveragePixelValue()))
				{
					Singleton.setLastProjectedAreaAverage(yuvPixel.getAveragePixelValue());
				}
				else
				{
					Log.d("mobileeye", "NEED TO CHANGE STATE BACK!!!!");
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
			
			b = Utility.renderBitmap(imgPackage.getAreaExtractionPixels(), imgPackage.getImgWidth(), imgPackage.getImgHeight(), true);
			
			if(mLogData == true)
			{
				logData(yuvPixel, imgPackage);
			}
				
			Singleton.updateImageView = b;
			
			Message msg = CameraWrapper.mHandler.obtainMessage();
			msg.arg1 = CameraActivity.DRAW_IMAGE_PROCESSING;
			
			CameraWrapper.mHandler.dispatchMessage(msg);
		}
	}
	
	private boolean averagesApproximatelyMatch(double prevAvg, double averagePixelValue)
	{
		if(Math.abs(prevAvg - averagePixelValue) < 20)
		{
			return true;
		}
		return false;
	}

	private boolean foundGoodArea(RegionGroup areaExtraction, int imgWidth, int imgHeight)
	{
		if(areaExtraction.getRegionSize() > ((imgWidth * imgHeight) * 0.1))
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
		s = "<Min> <Peak> <Max>\n";
		for(int i = 0; i < pixelGroups.size(); i++)
		{
			Peak p = pixelGroups.get(i);
			s = s + p.getMinIndex()+" "+p.getPeakIndex()+" "+p.getMaxIndex()+"\n";
		}
		
		Utility.saveTextToSDCard(s, "3_InitPixelGroups.txt");
		
		Peak[] finalPixelGroups = imgPackage.getFinalPixelGroups();
		s = "<Min> <Peak> <Max>\n";
		for(int i = 0; i < finalPixelGroups.length; i++)
		{
			Peak p = finalPixelGroups[i];
			s = s + p.getMinIndex()+" "+p.getPeakIndex()+" "+p.getMaxIndex()+"\n";
		}
		
		Utility.saveTextToSDCard(s, "4_FinalPixelGroups.txt");
		
		Peak usedPixelGroup = imgPackage.getUsedPixelGroup();
		s = "<Min> <Peak> <Max>\n";
		s = s + usedPixelGroup.getMinIndex() + " " + usedPixelGroup.getPeakIndex() + " " + usedPixelGroup.getMaxIndex();
		
		Utility.saveTextToSDCard(s, "5_UsedPixelGroup.txt");
		
		temp = Utility.renderBitmap(imgPackage.getRegionGroupPixels(),
				imgPackage.getImgWidth(),
				imgPackage.getImgHeight(),
				true);
		Utility.saveImageToSDCard(temp, "6_Segment.png");
		
		RegionGroup r = imgPackage.getRegionGroup();
		s = "("+r.getTopLeftX()+","+r.getTopLeftY()+") ("+r.getBottomRightX()+","+r.getBottomRightY()+")";
		Utility.saveTextToSDCard(s, "7_FinalRegion");
		
		r = imgPackage.getExtractionArea();
		s = "("+r.getTopLeftX()+","+r.getTopLeftY()+") ("+r.getBottomRightX()+","+r.getBottomRightY()+")";
		Utility.saveTextToSDCard(s, "8_ExtractionArea");
		
		temp = Utility.renderBitmap(imgPackage.getAreaExtractionPixels(),
				imgPackage.getImgWidth(), imgPackage.getImgHeight(), true);
		Utility.saveImageToSDCard(temp, "9_Area.png");
	}
}
