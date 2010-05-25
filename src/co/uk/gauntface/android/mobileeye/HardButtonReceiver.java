package co.uk.gauntface.android.mobileeye;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
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
			int keycode = key.getKeyCode();
			
			if(keycode == KeyEvent.KEYCODE_MEDIA_NEXT)
			{
				Log.v("mobileeye", "Next Pressed");
			}
			else if(keycode == KeyEvent.KEYCODE_MEDIA_PREVIOUS)
			{
				Log.v("mobileeye", "Previous pressed");
			}
			else if(keycode == KeyEvent.KEYCODE_HEADSETHOOK)
			{
				Log.v("mobileeye", "Head Set Hook pressed");
				Message msg = Message.obtain();
				msg.arg1 = CameraActivity.HARDWARE_BUTTON_PRESS;
				msg.arg2 = KeyEvent.KEYCODE_HEADSETHOOK;
				
				mHandler.dispatchMessage(msg);
			}
			else
			{
				Log.d("mobileeye", "Equals "+keycode+" :-(");
			}
		}
	}
	
}
