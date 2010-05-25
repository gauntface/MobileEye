package co.uk.gauntface.android.mobileeye;

import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.os.Message;

public class EyeAutoFocusCallback implements AutoFocusCallback
{
	public void onAutoFocus(boolean success, Camera camera)
	{
		Message msg = CameraWrapper.mHandler.obtainMessage();
		
		CameraWrapper.mHandler.sendMessage(msg);
	}

}
