package co.uk.gauntface.android.mobileeye;

import co.uk.gauntface.android.mobileeye.imageprocessing.GaussianBlur;
import co.uk.gauntface.android.mobileeye.imageprocessing.GreyScale;
import co.uk.gauntface.android.mobileeye.imageprocessing.RGB888Pixel;
import co.uk.gauntface.android.mobileeye.imageprocessing.YCbCr420Pixel;
import co.uk.gauntface.android.mobileeye.imageprocessing.YUVPixel;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Message;
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
		mData = mData.clone();
		
		YUVPixel yuvPixel = new YUVPixel(mData, mImageSize.width, mImageSize.height, 0, 0, mImageSize.width, mImageSize.height);
		Bitmap b = yuvPixel.renderCroppedGreyscaleBitmap();
		
		Singleton.updateImaveView = b;
		
		Message msg = CameraWrapper.mHandler.obtainMessage();
		msg.arg1 = MobileEye.DRAW_IMAGE_PROCESSING;
		
		Bundle data = new Bundle();
		data.putByteArray(MobileEye.IMAGE_PROCESSED_DATA, mData);
		data.putInt(MobileEye.IMAGE_PROCESSED_WIDTH, mImageSize.width);
		data.putInt(MobileEye.IMAGE_PROCESSED_HEIGHT, mImageSize.height);
		
		msg.setData(data);
		
		CameraWrapper.mHandler.dispatchMessage(msg);
	}
}
