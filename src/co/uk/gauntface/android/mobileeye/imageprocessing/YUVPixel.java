package co.uk.gauntface.android.mobileeye.imageprocessing;

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
			int scaleDownFactor, boolean targetSizePrescaled)
	{
		mData = data;
		mImgWidth = imgWidth;
		mImgHeight = imgHeight;
		
		// The offset will only be non zero when scaled
		mLeftOffset = leftOffset;
		mTopOffset = topOffset;
		
		// The targetsize may be the full image size & hence not scaled down
		if(targetSizePrescaled == false)
		{
			mCroppedImgWidth = croppedWidth / scaleDownFactor;
			mCroppedImgHeight = croppedHeight / scaleDownFactor;
		}
		else
		{
			mCroppedImgWidth = croppedWidth;
			mCroppedImgHeight = croppedHeight;
		}
		
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
		int noOfPixels = mCroppedImgWidth * mCroppedImgHeight;
		mPixels = new int[noOfPixels];
		
		// Need to mulitply by the scaledown factor to take into account that
		// the offset will be scaled down
		int yNewOffset = 0;
		int yOrigImgIndex = mScaleDownFactor *((mTopOffset * mImgWidth) + mLeftOffset);
		int yOrigImgSkip = mScaleDownFactor * mImgWidth;
		
		for(int y = 0; y < mCroppedImgHeight; y++)
		{
			int xOrigImgIndex = 0;
			for(int x = 0; x < mCroppedImgWidth; x++)
			{
				int greyPixel = mData[yOrigImgIndex + xOrigImgIndex] & 0xff;
				mPixels[yNewOffset + x] = greyPixel;
				
				xOrigImgIndex = xOrigImgIndex + mScaleDownFactor;
				
				mAveragePixelValue = mAveragePixelValue + greyPixel;
			}
			
			yNewOffset = yNewOffset + mCroppedImgWidth;
			yOrigImgIndex = yOrigImgIndex + yOrigImgSkip;
		}
		
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
