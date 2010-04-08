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
		
		if (mLeftOffset + mCroppedImgWidth > mImgWidth 
				|| mTopOffset + mCroppedImgHeight > mImgHeight) {
			throw new IllegalArgumentException("Crop rectangle does not fit within image data.");
		}
		
		createPixels();
		
		//convertToPixels();
	}
	
	/**int origImgWidth = yuvPixel.getImgWidth();
	int origImgHeight = yuvPixel.getImgHeight();
	
	int targetWidth = origImgWidth / scaleDownFactor;
	int targetHeight = origImgHeight / scaleDownFactor;
	
	int xRatio = origImgWidth / targetWidth;
	int yRatio = origImgHeight / targetHeight;
	
	int[] pixels = yuvPixel.getPixels();
	int[] newPixels = new int[targetWidth * targetHeight];
	
	int yNewOffset = 0;
	int yOrigImgIndex = 0;

	for(int y = 0; y < targetHeight; y++)
	{
		int xOrigImgIndex = 0;
		for(int x = 0; x < targetWidth; x++)
		{
			newPixels[yNewOffset + x] = pixels[(yOrigImgIndex * origImgWidth) + xOrigImgIndex];
			
			xOrigImgIndex = xOrigImgIndex + xRatio;
		}
		
		yNewOffset = yNewOffset + targetWidth;
		yOrigImgIndex = yOrigImgIndex + yRatio;
	}
	
	yuvPixel.setPixels(newPixels);
	yuvPixel.setImgWidth(targetWidth);
	yuvPixel.setImgHeight(targetHeight);**/
	
	private void createPixels()
	{
		int xRatio = mScaleDownFactor;
		int yRatio = mScaleDownFactor;
		
		int targetWidth = mCroppedImgWidth / mScaleDownFactor;
		int targetHeight = mCroppedImgHeight / mScaleDownFactor;
		
		mPixels = new int[targetWidth * targetHeight];
		
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
			}
			
			yNewOffset = yNewOffset + targetWidth;
			yOrigImgIndex = yOrigImgIndex + yOrigImgSkip;
		}
		
		mCroppedImgWidth = targetWidth;
		mCroppedImgHeight = targetHeight;
	}
	
	public void setPixels(int[] pixels)
	{
		mPixels = pixels;
	}
	
	/**
	 * int[] pixels - a single array of pixel values (0 - 255?)
	 * byte[] yuv - just a copy of the data
	 * int inputOffset - the position in the 1D array of pixels
	 * 
	 * int grey - By ANDing yuv value with 0xff == 0b11111111 you remove negative values of yuv and make it 0 - 255
	 * pixels[Assignment] :
	 * 			0xXX000000 Alpha value of pixels - allows ghosting effect
	 * 			0x00010101 NOTE: This is hex NOT decimal
	 * 			grey * 0x00010101 - this is simply setting the grey value to Red, Green, Blue Color components
	 * @return
	 */
	private void convertToPixels()
	{
		/**mPixels = new long[mCroppedImgWidth * mCroppedImgHeight];
	    int inputOffset = mTopOffset * mImgWidth + mLeftOffset;

	    for (int y = 0; y < mCroppedImgHeight; y++)
	    {
	    	int outputOffset = y * mCroppedImgWidth;
	    	for (int x = 0; x < mCroppedImgWidth; x++)
	    	{
	    		int grey = mData[inputOffset + x] & 0xff;
	    		mPixels[outputOffset + x] = 0xff000000 | (grey * 0x00010101);
	    	}
	    	inputOffset = inputOffset + mImgWidth;
	    }**/
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
	
	public Bitmap renderCroppedGreyscaleBitmap()
	{
	    //Bitmap bitmap = Bitmap.createBitmap(mCroppedImgWidth, mCroppedImgHeight, Bitmap.Config.ARGB_8888);
	    //bitmap.setPixels(, 0, mCroppedImgWidth, 0, 0, mCroppedImgWidth, mCroppedImgHeight);
	    //return bitmap;
		return null;
	}
}
