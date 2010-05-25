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
	private EyePictureCallback mPictureCallback;
	
	public static Handler mHandler;
	
	public CameraWrapper(Handler h)
	{
		mPreviewing = false;
		
		mHandler = h;
	}
	
	public void startPreview(SurfaceHolder holder) throws CameraHardwareException
	{
		Log.v(Singleton.TAG, "Camera - startPreview()");
		
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
		
		mPictureCallback = new EyePictureCallback();
		
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
		
		try
		{
			mCamera.setParameters(params);
		}
		catch(Exception e)
		{
			Log.e(Singleton.TAG, "CameraWrapper: Exception - " + e);
		}
		
		return previewSize;
	}
	
	public void stopPreview()
	{
		Log.v(Singleton.TAG, "Camera - stopPreview");
		
		if(mPreviewing == true)
		{
			mCamera.stopPreview();
		}
		
		mPreviewing = false;
	}
	
	public void setPreviewDisplay(SurfaceHolder holder)
	{
		Log.v(Singleton.TAG, "Camera - setPreviewDisplay");
		
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
		Log.v(Singleton.TAG, "Camera - closeCamera()");
		
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

	public void logHistogram()
	{
		mPreviewCallback.logNextHistogram();
	}

	public void findObject()
	{
		Log.d("mobileeye", "Find Object Called");
		mCamera.takePicture(null, null, mPictureCallback);
	}

	public void freePictureCallback()
	{
		mPictureCallback.makeFree();
	}
}
