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
import android.graphics.Bitmap;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Message;
import android.text.format.Time;

public class ImageProcessingThread extends Thread
{
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
		
		YUVPixel yuvPixel = new YUVPixel(mData, mImageSize.width, mImageSize.height, 0, 0, mImageSize.width, mImageSize.height, 4);
		
		QuickSegment quickSegment = QuickSegmentFactory.getQuickSegment();
		ImagePackage imgPackage = quickSegment.segmentImage(yuvPixel.getPixels(), mLogData, yuvPixel.getImgWidth(), yuvPixel.getImgHeight());
		
		if(imgPackage != null)
		{
			imgPackage = AreaExtraction.getExtraction(imgPackage);
			
			b = Utility.renderBitmap(imgPackage.getImgPixels(), imgPackage.getImgWidth(), imgPackage.getImgHeight(), true);
		}
		
		if(mLogData == true)
		{
			logData(yuvPixel, imgPackage);
		}
		
		Singleton.updateImageView = b;
		
		Message msg = CameraWrapper.mHandler.obtainMessage();
		msg.arg1 = CameraActivity.DRAW_IMAGE_PROCESSING;
		
		Bundle data = new Bundle();
		
		msg.setData(data);
		
		CameraWrapper.mHandler.dispatchMessage(msg);
	}
	
	private void logData(YUVPixel yuvPixel, ImagePackage imgPackage)
	{
		Time time = new Time();
		time.setToNow();
		String currentTime = time.format("%Y%m%d%H%M%S");
		
		Utility.setFilePrePend(currentTime);
		
		Bitmap temp = Utility.renderBitmap(yuvPixel.getPixels(),
				yuvPixel.getImgWidth(),
				yuvPixel.getImgHeight(),
				true);
		Utility.saveImageToSDCard(temp, "1B&W.png");
		
		String s = new String();
		int[] hist = imgPackage.getHistogram();
		for(int i = 0; i < hist.length; i++)
		{
			s = s + hist[i]+"\n";
		}
		
		Utility.saveTextToSDCard(s, "2HistogramData.txt");
		
		ArrayList<Peak> pixelGroups = imgPackage.getPixelGroups();
		s = "<Min> <Max>\n";
		for(int i = 0; i < pixelGroups.size(); i++)
		{
			Peak p = pixelGroups.get(i);
			s = s + p.getMinIndex()+" "+" "+p.getMaxIndex()+"\n";
		}
		
		Utility.saveTextToSDCard(s, "3PixelGroups.txt");
		
		temp = Utility.renderBitmap(imgPackage.getRegionGroupPixels(),
				imgPackage.getImgWidth(),
				imgPackage.getImgHeight(),
				true);
		Utility.saveImageToSDCard(temp, "4Segment.png");
		
		RegionGroup r = imgPackage.getRegionGroup();
		s = "("+r.getTopLeftX()+","+r.getTopLeftY()+") ("+r.getBottomRightX()+","+r.getBottomRightY()+")";
		Utility.saveTextToSDCard(s, "5OrigRegion");
		
		r = imgPackage.getExtractionArea();
		s = "("+r.getTopLeftX()+","+r.getTopLeftY()+") ("+r.getBottomRightX()+","+r.getBottomRightY()+")";
		Utility.saveTextToSDCard(s, "6ExtractionArea");
		
		temp = Utility.renderBitmap(imgPackage.getImgPixels(),
				imgPackage.getImgWidth(), imgPackage.getImgHeight(), true);
		Utility.saveImageToSDCard(temp, "7Area.png");
	}
}
