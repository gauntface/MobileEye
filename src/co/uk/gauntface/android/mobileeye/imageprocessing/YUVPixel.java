package co.uk.gauntface.android.mobileeye.imageprocessing;

import android.graphics.Bitmap;

public class YUVPixel
{
	private byte[] mData;
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
	}
	
	public Bitmap renderCroppedGreyscaleBitmap()
	{
	    int[] pixels = new int[mCroppedImgWidth * mCroppedImgHeight];
	    byte[] yuv = mData;
	    int inputOffset = mTopOffset * mImgWidth + mLeftOffset;

	    for (int y = 0; y < mCroppedImgHeight; y++)
	    {
	      int outputOffset = y * mCroppedImgWidth;
	      for (int x = 0; x < mCroppedImgWidth; x++)
	      {
	    	  int grey = yuv[inputOffset + x] & 0xff;
	    	  pixels[outputOffset + x] = 0xFF000000 | (grey * 0x00010101);
	      }
	      inputOffset += mImgWidth;
	    }

	    Bitmap bitmap = Bitmap.createBitmap(mCroppedImgWidth, mCroppedImgHeight, Bitmap.Config.ARGB_8888);
	    bitmap.setPixels(pixels, 0, mCroppedImgWidth, 0, 0, mCroppedImgWidth, mCroppedImgHeight);
	    return bitmap;
	  }
}
