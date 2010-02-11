package co.uk.gauntface.android.mobileeye;

import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;

public class EyePreviewCallback implements PreviewCallback
{
	private ImageProcessingThread mProcessingThread;

	public void onPreviewFrame(byte[] data, Camera camera)
	{
		if(mProcessingThread == null || mProcessingThread.isAlive() == false)
		{
			mProcessingThread = new ImageProcessingThread(data.clone());
			mProcessingThread.start();
		}
	}

}
