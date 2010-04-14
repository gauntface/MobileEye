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
	
	public static RegionGroup useExistingArea(double average)
	{
		// TODO Make this a variable, not hardcoded
		if(average  > (mAveragePixelValue - 20) && average < (mAveragePixelValue + 20))
		{
			return mLastIterationRegionGroup;
		}
		else
		{
			return null;
		}
	}
}
