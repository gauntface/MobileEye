package co.uk.gauntface.android.mobileeye;

import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.util.Log;

public class EyePreviewCallback implements PreviewCallback
{
	private Size mPreviewSize;
	private ImageProcessingThread mProcessingThread;
	private boolean mFirstPreview;
	private boolean mLogHistogram = false;
	
	public EyePreviewCallback(Size previewSize)
	{
		mPreviewSize = previewSize;
		mFirstPreview = true;
	}
	
	public void onPreviewFrame(byte[] data, Camera camera)
	{
		if(mFirstPreview == false)
		{
			if(mProcessingThread == null || mProcessingThread.isAlive() == false)
			{
				mProcessingThread = new ImageProcessingThread(mPreviewSize, data, mLogHistogram);
				mProcessingThread.start();
				
				mLogHistogram = false;
			}
		}
		else
		{
			mFirstPreview = false;
		}
	}

	public void logNextHistogram()
	{
		mLogHistogram = true;
	}

}
