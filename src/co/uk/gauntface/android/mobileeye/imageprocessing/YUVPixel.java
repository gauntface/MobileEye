package co.uk.gauntface.android.mobileeye.imageprocessing;

import android.graphics.Bitmap;

public class YUVPixel
{
	private byte[] mData;
	private int[] mPixels;
	private int mImgWidth;
	private int mImgHeight;
	private int mLeftOffset;
	private int mTopOffset;
	private int mCroppedImgWidth;
	private int mCroppedImgHeight;
	private int mScaleDownFactor;
	private double mAveragePixelValue;
	
	public YUVPixel(byte[] data, int imgWidth, int imgHeight,
			int leftOffset, int topOffset,
			int croppedWidth, int croppedHeight,
			int scaleDownFactor)
	{
		mData = data;
		mImgWidth = imgWidth;
		mImgHeight = imgHeight;
		mLeftOffset = leftOffset;
		mTopOffset = topOffset;
		mCroppedImgWidth = croppedWidth;
		mCroppedImgHeight = croppedHeight;
		mScaleDownFactor = scaleDownFactor;
		mAveragePixelValue = 0;
		
		if (mLeftOffset + mCroppedImgWidth > mImgWidth 
				|| mTopOffset + mCroppedImgHeight > mImgHeight) {
			throw new IllegalArgumentException("Crop rectangle does not fit within image data.");
		}
		
		createPixels();
	}
	
	private void createPixels()
	{
		int xRatio = mScaleDownFactor;
		int yRatio = mScaleDownFactor;
		
		int targetWidth = mCroppedImgWidth / mScaleDownFactor;
		int targetHeight = mCroppedImgHeight / mScaleDownFactor;
		
		int noOfPixels = targetWidth * targetHeight;
		
		mPixels = new int[noOfPixels];
		
		int yNewOffset = 0;
		int yOrigImgIndex = mTopOffset * mImgWidth + mLeftOffset;
		
		int yOrigImgSkip = yRatio * mCroppedImgWidth;
		
		for(int y = 0; y < targetHeight; y++)
		{
			int xOrigImgIndex = 0;
			for(int x = 0; x < targetWidth; x++)
			{
				int greyPixel = mData[yOrigImgIndex + xOrigImgIndex] & 0xff;
				mPixels[yNewOffset + x] = greyPixel;
				
				xOrigImgIndex = xOrigImgIndex + xRatio;
				
				mAveragePixelValue = mAveragePixelValue + greyPixel;
			}
			
			yNewOffset = yNewOffset + targetWidth;
			yOrigImgIndex = yOrigImgIndex + yOrigImgSkip;
		}
		
		mCroppedImgWidth = targetWidth;
		mCroppedImgHeight = targetHeight;
		
		mAveragePixelValue = mAveragePixelValue / noOfPixels;
	}
	
	public void setPixels(int[] pixels)
	{
		mPixels = pixels;
	}
	
	public int[] getPixels()
	{
		return mPixels;
	}
	
	public int getImgWidth()
	{
		return mCroppedImgWidth;
	}
	
	public void setImgWidth(int imgWidth)
	{
		mCroppedImgWidth = imgWidth;
	}
	
	public int getImgHeight()
	{
		return mCroppedImgHeight;
	}
	
	public void setImgHeight(int imgHeight)
	{
		mCroppedImgHeight = imgHeight;
	}
	
	public double getAveragePixelValue()
	{
		return mAveragePixelValue;
	}
}
