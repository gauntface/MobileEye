package co.uk.gauntface.android.mobileeye;

import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

public class EyePictureCallback implements PictureCallback
{
	private static final float NEW_SCALED_WIDTH = 640;
	private boolean mBusy = false;
	
	public void onPictureTaken(final byte[] data, Camera camera)
	{
		if(mBusy == false)
		{
			mBusy = true;
			Log.d("mobileeye", "PictureTaken Sending");
			Thread t = new Thread(new Runnable(){
				
				public void run()
				{
					Bitmap image = BitmapFactory.decodeByteArray(data, 0, data.length);
					
					float scale = NEW_SCALED_WIDTH / image.getWidth();
					
					Matrix m = new Matrix();
					m.postScale(scale, scale);
					m.postRotate(90);
					
					image = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), m, true);
					
					ByteArrayOutputStream outStream = new ByteArrayOutputStream();
					image.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
					
					Message msg =CameraWrapper.mHandler.obtainMessage();
					msg.arg1 = CameraActivity.FIND_OBJECT_REQUEST;
					
					Bundle b = msg.getData();
					b.putByteArray(CameraActivity.FIND_OBJECT_DATA_KEY, outStream.toByteArray());
					
					msg.setData(b);
					
					CameraWrapper.mHandler.dispatchMessage(msg);
				}
				
			});
			t.start();
		}
		
		camera.startPreview();
	}

	public void makeFree()
	{
		mBusy = false;
	}

}
