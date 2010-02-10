package co.uk.gauntface.android.mobileeye;

import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.os.Message;

public class EyeAutoFocusCallback implements AutoFocusCallback
{

	public void onAutoFocus(boolean success, Camera camera)
	{
		Message msg = CameraWrapper.mHandler.obtainMessage();
		msg.arg1 = MobileEye.START_AUTO_FOCUS;
		if(success == true)
		{
			// Force a new update on preview
			msg.arg2 = MobileEye.AUTO_FOCUS_SUCCESSFUL;
		}
		else
		{
			// Restart autofocus
			msg.arg2 = MobileEye.AUTO_FOCUS_UNSUCCESSFUL;
		}
		
		CameraWrapper.mHandler.sendMessage(msg);
	}

}
