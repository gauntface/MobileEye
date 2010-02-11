package co.uk.gauntface.android.mobileeye;

import co.uk.gauntface.android.mobileeye.imageprocessing.GaussianBlur;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera.Size;
import android.util.Log;

public class ImageProcessingThread extends Thread
{
	private Size mImageSize;
	private byte[] mData;
	
	public ImageProcessingThread(Size imageSize, byte[] data)
	{
		mImageSize = imageSize;
		mData = data;
	}
	
	public void run()
	{
		Log.v(Singleton.TAG, "Inside Processing Thread");
		
		double sigmaValue = 1.0;
		GaussianBlur gaussianBlur = new GaussianBlur(sigmaValue);
		gaussianBlur.blurImage(mData, mImageSize.width, mImageSize.height);
		
	}
}
