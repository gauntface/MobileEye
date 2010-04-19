package co.uk.gauntface.android.mobileeye;

import co.uk.gauntface.android.mobileeye.bluetooth.BluetoothConnectionThread;
import co.uk.gauntface.android.mobileeye.imageprocessing.RegionGroup;
import android.graphics.Bitmap;
import android.util.Log;

public class Singleton
{
	public static final String TAG = new String("mobileeye");
	public static Bitmap updateImageView;
	
	private static BluetoothConnectionThread mBluetoothConnection = null;
	
	public static final int STATE_FINDING_AREA = 0;
	public static final int STATE_SETTING_UP_PROJECTION = 1;
	public static final int STATE_PROJECTING_MARKERS = 2;
	public static final int STATE_PROJECTING_DATA = 3;
	
	private static int mApplicationState = STATE_FINDING_AREA;
	
	private static RegionGroup mLastProjectedArea = null;
	private static double mLastProjectedPixelAverage = -1;
	
	public static BluetoothConnectionThread getBluetoothConnection()
	{
		return mBluetoothConnection;
	}
	
	public static void setBluetoothConnection(BluetoothConnectionThread c)
	{
		mBluetoothConnection = c;
	}
	
	public static synchronized void setApplicationState(int s)
	{
		if(s == STATE_PROJECTING_MARKERS)
		{
			if(mApplicationState == STATE_SETTING_UP_PROJECTION)
			{
				mApplicationState = s;
			}
			else
			{
				Log.v("mobileeye", "Stuck in a bad place? - new state = " + s + " old state = " + mApplicationState);
			}
		}
		else
		{
			mApplicationState = s;
		}
	}
	
	public static int getApplicationState()
	{
		return mApplicationState;
	}
	
	public static RegionGroup getLastProjectedArea()
	{
		return mLastProjectedArea;
	}
	
	public static void setLastProjectedArea(RegionGroup l)
	{
		mLastProjectedArea = l;
	}

	public static double getLastProjectedAreaAverage()
	{
		return mLastProjectedPixelAverage;
	}
	
	public static void setLastProjectedAreaAverage(double averagePixelValue)
	{
		mLastProjectedPixelAverage = averagePixelValue;
	}
}
