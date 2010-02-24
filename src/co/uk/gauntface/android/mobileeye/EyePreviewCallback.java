package co.uk.gauntface.android.mobileeye;

import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.util.Log;

public class EyePreviewCallback implements PreviewCallback
{
	private Size mPreviewSize;
	private ImageProcessingThread mProcessingThread;
	
	public EyePreviewCallback(Size previewSize)
	{
		mPreviewSize = previewSize;
	}
	
	public void onPreviewFrame(byte[] data, Camera camera)
	{
		if(mProcessingThread == null || mProcessingThread.isAlive() == false)
		{
			mProcessingThread = new ImageProcessingThread(mPreviewSize, data);
			mProcessingThread.start();
		}
	}

}
