package co.uk.gauntface.android.mobileeye;

import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;

public class CameraWrapper
{
	private Camera mCamera;
	private boolean mPreviewing;
	private boolean mFocusing;
	
	private EyeCameraCallback mErrorCallback;
	private EyePreviewCallback mPreviewCallback;
	private EyeAutoFocusCallback mAutoFocusCallback;
	
	public static Handler mHandler;
	
	public CameraWrapper(Handler h)
	{
		mPreviewing = false;
		
		mHandler = h;
	}
	
	public void startPreview(SurfaceHolder holder) throws CameraHardwareException
	{
		Log.d(Singleton.TAG, "Camera - startPreview()");
		
		ensureCameraDevice();
		
		if(mPreviewing == true)
		{
			mCamera.stopPreview();
		}
		
		setPreviewDisplay(holder);
		
		Size previewSize = setCameraParameters();
		
		mErrorCallback = new EyeCameraCallback();
		mCamera.setErrorCallback(mErrorCallback);
		
		mPreviewCallback = new EyePreviewCallback(previewSize);
		mCamera.setPreviewCallback(mPreviewCallback);
		
		try
		{
			mCamera.startPreview();
		}
		catch(Throwable e)
		{
			throw new RuntimeException("Camera startPreview failed", e);
		}
		
		mPreviewing = true;
	}
	
	private Size setCameraParameters()
	{
		// Set up camera settings here
		Camera.Parameters params = mCamera.getParameters();
		params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
		params.setPreviewFormat(PixelFormat.YCbCr_420_SP);
		Size previewSize = params.getPreviewSize();
		
		mCamera.setParameters(params);
		
		return previewSize;
	}
	
	public void stopPreview()
	{
		Log.d(Singleton.TAG, "Camera - stopPreview");
		
		if(mPreviewing == true)
		{
			mCamera.stopPreview();
		}
		
		mPreviewing = false;
	}
	
	public void setPreviewDisplay(SurfaceHolder holder)
	{
		Log.d(Singleton.TAG, "Camera - setPreviewDisplay");
		
		try
		{
			mCamera.setPreviewDisplay(holder);
		}
		catch (Throwable e)
		{
			closeCamera();
			throw new RuntimeException("Camera setPreviewDisplay failed", e);
		}
	}
	
	public void closeCamera()
	{
		Log.d(Singleton.TAG, "Camera - closeCamera()");
		
		if(mCamera != null)
		{   
			mCamera.cancelAutoFocus();
			mCamera.setPreviewCallback(null);
			
            stopPreview();
            mCamera.release();
            
            mCamera = null;
            mPreviewing = false;
        }
	}
	
	private void ensureCameraDevice() throws CameraHardwareException
	{
        if (mCamera == null)
        {
        	mCamera = Camera.open();
        }
    }
	
	public boolean isNull()
	{
		if(mCamera == null)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public boolean isPreviewing()
	{
		return mPreviewing;
	}

	public void startAutoFocus()
	{
		//Log.v(Singleton.TAG, "Starting Autofocus");
		mAutoFocusCallback = new EyeAutoFocusCallback();
		mCamera.autoFocus(mAutoFocusCallback);
	}
}
