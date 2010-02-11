package co.uk.gauntface.android.mobileeye;

import android.util.Log;

public class ImageProcessingThread extends Thread
{
	private byte[] mData;
	
	public ImageProcessingThread(byte[] data)
	{
		mData = data;
	}
	
	public void run()
	{
		Log.v(Singleton.TAG, "Inside Processing Thread");
		
	}
}
