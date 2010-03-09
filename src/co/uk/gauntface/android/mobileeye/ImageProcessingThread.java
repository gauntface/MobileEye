package co.uk.gauntface.android.mobileeye;

import co.uk.gauntface.android.mobileeye.imageprocessing.EdgeDetection;
import co.uk.gauntface.android.mobileeye.imageprocessing.EdgeFactory;
import co.uk.gauntface.android.mobileeye.imageprocessing.GaussianBlur;
import co.uk.gauntface.android.mobileeye.imageprocessing.GaussianFactory;
import co.uk.gauntface.android.mobileeye.imageprocessing.IPUtility;
import co.uk.gauntface.android.mobileeye.imageprocessing.Utility;
import co.uk.gauntface.android.mobileeye.imageprocessing.YUVPixel;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
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
		
		Bitmap b = Utility.renderBitmap(yuvPixel.getPixels(), mImageSize.width, mImageSize.height, true);
		b = IPUtility.transformPhoto(new Matrix(), b, (int) Math.floor(b.getWidth() / 3), (int) Math.floor(b.getHeight() / 3), false);
		
		int[] pixels = new int[b.getWidth() * b.getHeight()];
		b.getPixels(pixels, 0, b.getWidth(), 0, 0, b.getWidth(), b.getHeight());
		
		for(int i = 0; i < pixels.length; i++)
		{
			int red = Color.red(pixels[i]);
			int green = Color.green(pixels[i]);
			int blue = Color.blue(pixels[i]);
			
			pixels[i] = (int) ((0.3 * red) + (0.59 * green) + (0.11 * blue));
			
			if(pixels[i] > 255)
			{
				pixels[i] = 255;
			}
			else if(pixels[i] < 0)
			{
				pixels[i] = 0;
			}
		}
		
		GaussianBlur gaussianBlur = GaussianFactory.getGaussianBlur();
		pixels = gaussianBlur.blurImage(pixels, b.getWidth(), b.getHeight());
		
		EdgeDetection edgeDetection = EdgeFactory.getEdgeDetector();
		pixels = edgeDetection.classifyEdges(pixels, b.getWidth(), b.getHeight());
		
		b = Utility.renderBitmap(pixels, b.getWidth(), b.getHeight(), true);
		//Bitmap b = Utility.renderBitmap(yuvPixel.getPixels(), mImageSize.width, mImageSize.height);
		
		Singleton.updateImageView = b;
		
		Message msg = CameraWrapper.mHandler.obtainMessage();
		msg.arg1 = MobileEye.DRAW_IMAGE_PROCESSING;
		
		Bundle data = new Bundle();
		
		msg.setData(data);
		
		CameraWrapper.mHandler.dispatchMessage(msg);
	}
}
