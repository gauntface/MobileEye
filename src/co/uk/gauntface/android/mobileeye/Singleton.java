package co.uk.gauntface.android.mobileeye;

import co.uk.gauntface.android.mobileeye.bluetooth.BluetoothConnectionThread;
import co.uk.gauntface.android.mobileeye.imageprocessing.RegionGroup;
import android.graphics.Bitmap;
import android.text.format.Time;
import android.util.Log;

public class Singleton
{
	public static final String TAG = new String("mobileeye");
	public static Bitmap updateImageView;
	
	private static BluetoothConnectionThread mBluetoothConnection = null;
	
	public static final int STATE_INIT_APP = 0;
	public static final int STATE_FINDING_AREA = 1;
	public static final int STATE_TEST_SUITABLE_PROJECTION = 2;
	public static final int STATE_SETTING_UP_MARKERS = 3;
	public static final int STATE_PROJECTING_MARKERS = 4;
	public static final int STATE_PROJECTING_DATA = 5;
	
	private static int mApplicationState = STATE_INIT_APP;
	private static boolean mControlMsgSent = false;
	private static long mTimeElapse = 0;
	
	private static long mStateTimerStart = 0;
	
	private static RegionGroup mLastProjectedArea = null;
	private static int mLastProjectedAreaImgW = -1;
	private static int mLastProjectedAreaImgH = -1;
	private static double mLastProjectedPixelAverage = -1;
	
	public static BluetoothConnectionThread getBluetoothConnection()
	{
		BluetoothConnectionThread temp = mBluetoothConnection;
		mBluetoothConnection = null;
		return temp;
	}
	
	public static void setBluetoothConnection(BluetoothConnectionThread c)
	{
		mBluetoothConnection = c;
	}
	
	public static synchronized void setApplicationState(int s)
	{
		if(s == STATE_SETTING_UP_MARKERS)
		{
			if(mApplicationState == STATE_TEST_SUITABLE_PROJECTION)
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
		resetStateTimer();
		mControlMsgSent = false;
	}
	
	public static long getStatePeriodSecs(long t)
	{
		return  (t - mStateTimerStart) / 1000000000;
	}
	
	public static void resetStateTimer()
	{
		mStateTimerStart = System.nanoTime();
	}
	
	public static boolean hasVoiceCommandBeenSent()
	{
		return mControlMsgSent;
	}
	
	public static void voiceCommandSent()
	{
		mControlMsgSent = true;
		mTimeElapse = System.nanoTime();
	}
	
	public static long timeElapsed(long t)
	{
		return t - mTimeElapse;
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
	
	public static int getLastProjectedImgWidth()
	{
		return mLastProjectedAreaImgW;
	}
	
	public static void setLastProjectedImgWidth(int w)
	{
		mLastProjectedAreaImgW = w;
	}
	
	public static int getLastProjectedImgHeight()
	{
		return mLastProjectedAreaImgH;
	}
	
	public static void setLastProjectedImgHeight(int h)
	{
		mLastProjectedAreaImgH = h;
	}
}
