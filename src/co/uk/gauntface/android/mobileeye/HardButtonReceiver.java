package co.uk.gauntface.android.mobileeye;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;

public class HardButtonReceiver extends BroadcastReceiver
{
	private Handler mHandler;
	
	public HardButtonReceiver(Handler h)
	{
		mHandler = h;
	}
	
	@Override
	public void onReceive(Context context, Intent intent)
	{
		Log.v("mobileeye", "Button press received");
		abortBroadcast();
		
		KeyEvent key = (KeyEvent) intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
		
		if(key.getAction() == KeyEvent.ACTION_UP)
		{
			Singleton.setApplicationState(Singleton.STATE_PROJECTING_MARKERS);
			
			int keycode = key.getKeyCode();
			
			if(keycode == KeyEvent.KEYCODE_MEDIA_NEXT)
			{
				Log.d("mobileeye", "Next Pressed");
			}
			else if(keycode == KeyEvent.KEYCODE_MEDIA_PREVIOUS)
			{
				Log.d("mobileeye", "Previous pressed");
			}
			else if(keycode == KeyEvent.KEYCODE_HEADSETHOOK)
			{
				Log.d("mobileeye", "Head Set Hook pressed");
			}
			else
			{
				Log.d("mobileeye", "Equals "+keycode+" :-(");
			}
		}
	}
	
}
