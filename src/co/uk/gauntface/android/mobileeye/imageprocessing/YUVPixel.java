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
	
	public YUVPixel(byte[] data, int imgWidth, int imgHeight,
			int leftOffset, int topOffset,
			int croppedWidth, int croppedHeight)
	{
		mData = data;
		mImgWidth = imgWidth;
		mImgHeight = imgHeight;
		mLeftOffset = leftOffset;
		mTopOffset = topOffset;
		mCroppedImgWidth = croppedWidth;
		mCroppedImgHeight = croppedHeight;
		
		if (mLeftOffset + mCroppedImgWidth > mImgWidth 
				|| mTopOffset + mCroppedImgHeight > mImgHeight) {
			throw new IllegalArgumentException("Crop rectangle does not fit within image data.");
		}
		
		createPixels();
		
		//convertToPixels();
	}
	
	private void createPixels()
	{
		mPixels = new int[mCroppedImgWidth * mCroppedImgHeight];
		int inputOffset = mTopOffset * mImgWidth + mLeftOffset;
		
		for (int y = 0; y < mCroppedImgHeight; y++)
	    {
	    	int outputOffset = y * mCroppedImgWidth;
	    	for (int x = 0; x < mCroppedImgWidth; x++)
	    	{
	    		int grey = mData[inputOffset + x] & 0xff;
	    		mPixels[outputOffset + x] = grey;
	    	}
	    	inputOffset = inputOffset + mImgWidth;
	    }
		
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
	
	public int getImgHeight()
	{
		return mCroppedImgHeight;
	}
	
	public Bitmap renderCroppedGreyscaleBitmap()
	{
	    //Bitmap bitmap = Bitmap.createBitmap(mCroppedImgWidth, mCroppedImgHeight, Bitmap.Config.ARGB_8888);
	    //bitmap.setPixels(, 0, mCroppedImgWidth, 0, 0, mCroppedImgWidth, mCroppedImgHeight);
	    //return bitmap;
		return null;
	}
}
