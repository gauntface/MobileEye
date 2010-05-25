package co.uk.gauntface.android.mobileeye;

import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;

public class EyePreviewCallback implements PreviewCallback
{
	private Size mPreviewSize;
	private ImageProcessingThread mProcessingThread;
	private boolean mLogHistogram = false;
	private boolean mFindObject = false;
	
	public EyePreviewCallback(Size previewSize)
	{
		mPreviewSize = previewSize;
	}
	
	public void onPreviewFrame(byte[] data, Camera camera)
	{
		if(mProcessingThread == null || mProcessingThread.isAlive() == false)
		{
			mProcessingThread = new ImageProcessingThread(mPreviewSize, data, mLogHistogram, mFindObject);
			mProcessingThread.start();
			mLogHistogram = false;
		}
	}

	public void logNextHistogram()
	{
		mLogHistogram = true;
	}

	public void findObject()
	{
		mFindObject = true;
	}

}
