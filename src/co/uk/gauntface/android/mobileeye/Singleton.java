package co.uk.gauntface.android.mobileeye;

import co.uk.gauntface.android.mobileeye.bluetooth.BluetoothConnectionThread;
import android.content.BroadcastReceiver;
import android.graphics.Bitmap;

public class Singleton
{
	public static final String TAG = new String("mobileeye");
	public static Bitmap updateImageView;
	
	/**
	 * Bluetooth connection
	 */
	private static BluetoothConnectionThread mBluetoothConnection = null;
	
	public static BluetoothConnectionThread getBluetoothConnection()
	{
		return mBluetoothConnection;
	}
	
	public static void setBluetoothConnection(BluetoothConnectionThread c)
	{
		mBluetoothConnection = c;
	}
}
