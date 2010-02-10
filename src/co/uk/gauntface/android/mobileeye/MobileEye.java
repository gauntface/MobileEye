package co.uk.gauntface.android.mobileeye;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.view.SurfaceHolder.Callback;

public class MobileEye extends Activity implements Callback
{
	private SurfaceView mSurfaceView;
	private boolean mStartPreviewFail;
	private CameraWrapper mCamera;
	private SurfaceHolder mSurfaceHolder;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera);
        
        initActivity();
    }
    
    @Override
    public void onStart()
    {
    	super.onStart();
    	
    	Log.v(Singleton.TAG, "MobileEye - onStart");
    }
    
    @Override
    public void onResume() {
        super.onResume();

        Log.v(Singleton.TAG, "MobileEye - onResume");
        
        // Start the preview if it is not started.
        if ((mCamera.isPreviewing() == false) && (mStartPreviewFail == false)) {
            try
            {
                mCamera.startPreview(mSurfaceHolder);
            }
            catch(CameraHardwareException e)
            {
                // Show Error and finish
                return;
            }
        }

        //if (mSurfaceHolder != null) {
            // If first time initialization is not finished, put it in the
            // message queue.
            //if (!mFirstTimeInitialized) {
            //    mHandler.sendEmptyMessage(FIRST_TIME_INIT);
            //} else {
            //    initializeSecondTime();
            //}
        //}
    }
    
    @Override
    protected void onPause()
    {
    	Log.v(Singleton.TAG, "MobileEye - onPause");
    	
        mCamera.stopPreview();
        
        // Close the camera now because other activities may need to use it.
        mCamera.closeCamera();

        super.onPause();
    }
    
    private void initActivity()
    {
    	mCamera = new CameraWrapper();
    	mSurfaceView = (SurfaceView) findViewById(R.id.CameraSurfaceView);
    	
    	/*
         * To reduce startup time, we start the preview in another thread.
         * We make sure the preview is started at the end of onCreate.
         */
        Thread startPreviewThread = new Thread(new Runnable() {
            public void run()
            {
                try
                {
                    mStartPreviewFail = false;
                    mCamera.startPreview(mSurfaceHolder);
                }
                catch (CameraHardwareException e)
                {
                    mStartPreviewFail = true;
                }
            }
        });
        startPreviewThread.start();
        
        SurfaceHolder surfaceHolder = mSurfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    	
    	// Make sure preview is started.
        try
        {
            startPreviewThread.join();
            
            if (mStartPreviewFail == true)
            {
                //showCameraErrorAndFinish();
                return;
            }
        }
        catch (InterruptedException ex)
        {
            // ignore
        }
    }
    
    
    /**
     * The SurfaceView Callback methods
     */
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h)
	{
		Log.v(Singleton.TAG, "MobileEye - surfaceChanged");
		
		if(mCamera.isNull() == true)
		{
			// TODO: Return Error
			return;
		}
		
		// Make sure we have a surface in the holder before proceeding.
        if (holder.getSurface() == null)
        {
            Log.d(Singleton.TAG, "Camera surfaceChanged holder.getSurface() == null");
            return;
        }
        
		mSurfaceHolder = holder;
		
		if(holder.isCreating() == true)
		{
			mCamera.setPreviewDisplay(mSurfaceHolder);
		}
	}

	public void surfaceCreated(SurfaceHolder holder)
	{
		
	}

	public void surfaceDestroyed(SurfaceHolder holder)
	{
		Log.v(Singleton.TAG, "MobileEye - surfaceDestroyed");
		
		mCamera.stopPreview();
        mSurfaceHolder = null;
	}
}