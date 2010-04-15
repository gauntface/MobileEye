package co.uk.gauntface.android.mobileeye;

import co.uk.gauntface.android.mobileeye.bluetooth.BluetoothConnectionThread;
import co.uk.gauntface.android.mobileeye.imageprocessing.RegionGroup;
import android.graphics.Bitmap;
import android.util.Log;

public class Singleton
{
	public static final String TAG = new String("mobileeye");
	public static Bitmap updateImageView;
	
	private static RegionGroup mLastIterationRegionGroup;
	private static double mAveragePixelValue = 0;
	private static BluetoothConnectionThread mBluetoothConnection = null;
	
	public static final int STATE_FINDING_AREA = 0;
	public static final int STATE_SETTING_UP_PROJECTION = 1;
	public static final int STATE_PROJECTING_MARKERS = 2;
	public static final int STATE_PROJECTING_DATA = 3;
	
	private static int mApplicationState = STATE_FINDING_AREA;
	
	public static BluetoothConnectionThread getBluetoothConnection()
	{
		return mBluetoothConnection;
	}
	
	public static void setBluetoothConnection(BluetoothConnectionThread c)
	{
		mBluetoothConnection = c;
	}
	
	public static RegionGroup getLastIterationRegionGroup()
	{
		return mLastIterationRegionGroup;
	}
	
	public static void setLastIterationRegionGroup(RegionGroup r, double average)
	{
		mLastIterationRegionGroup = r;
		mAveragePixelValue = average;
	}
	
	public static RegionGroup useExistingArea(double averagePixelValue)
	{
		if(hasCameraViewChanged(averagePixelValue) == false)
		{
			return mLastIterationRegionGroup;
		}
		else
		{
			return null;
		}
	}
	
	public static boolean hasCameraViewChanged(double averagePixelValue)
	{
		// TODO Make this a variable, not hardcoded
		if(averagePixelValue  > (mAveragePixelValue - 20) && averagePixelValue < (mAveragePixelValue + 20))
		{
			return false;
		}
		else
		{
			return true;
		}
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
}
