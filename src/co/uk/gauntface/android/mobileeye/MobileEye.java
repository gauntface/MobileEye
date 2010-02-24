package co.uk.gauntface.android.mobileeye;

import co.uk.gauntface.android.mobileeye.imageprocessing.RGB565;
import co.uk.gauntface.android.mobileeye.imageprocessing.RGB888Pixel;
import co.uk.gauntface.android.mobileeye.imageprocessing.YCbCr420Pixel;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.view.SurfaceHolder.Callback;
import android.widget.ImageView;

public class MobileEye extends Activity implements Callback
{
	/**
	 * Handler arg1 Values
	 */
	public static final int START_AUTO_FOCUS = 0;
	public static final int DRAW_IMAGE_PROCESSING = 1;
	
	public static final int AUTO_FOCUS_SUCCESSFUL = 0;
	public static final int AUTO_FOCUS_UNSUCCESSFUL = 1;
	
	public static final String IMAGE_PROCESSED_DATA = "ImageProcessData";
	public static final String IMAGE_PROCESSED_WIDTH = "ImageProcessWidth";
	public static final String IMAGE_PROCESSED_HEIGHT = "ImageProcessHeight";
	
	private SurfaceView mSurfaceView;
	private ImageView mImageProcessedSurfaceView;
	private boolean mStartPreviewFail;
	private CameraWrapper mCamera;
	private SurfaceHolder mSurfaceHolder;
	private Handler mHandler;
	
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
    	mHandler = new Handler(){
    		
    		public void handleMessage(Message msg)
    		{
    			if(msg.arg1 == START_AUTO_FOCUS)
    			{
    				// Was prev auto_focus successful
    				if(msg.arg2 == AUTO_FOCUS_SUCCESSFUL)
    				{
    					// Previous auto focus successful
    				}
    				else
    				{
    					// Previous auto focus unsuccessful
    				}
    				
    				// Start Auto Focus
    				if(mCamera.isNull() == false && mCamera.isPreviewing() == true)
    				{
    					mCamera.startAutoFocus();
    				}
    			}
    			else if(msg.arg1 == DRAW_IMAGE_PROCESSING)
    			{
    				final byte[] data = msg.getData().getByteArray(IMAGE_PROCESSED_DATA);
    				final int width = msg.getData().getInt(IMAGE_PROCESSED_WIDTH);
    				final int height = msg.getData().getInt(IMAGE_PROCESSED_HEIGHT);
    				
    				Runnable showImage = new Runnable(){

						public void run()
						{
							// Used in Gaussian Blur so needs to be changed
							YCbCr420Pixel[] pixels = YCbCr420Pixel.convert(data, width, height);
							
							Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
							
							for(int row = 0; row < height; row++)
							{
								for(int col = 0; col < width; col++)
								{
									//Log.v(Singleton.TAG, "col = " + col + " row = " + row + " RGB = ("+pixels[row][col].getRedValue()+","+pixels[row][col].getGreenValue()+","+pixels[row][col].getBlueValue()+")");
									//b.setPixel(col, row, Color.argb(255, pixels[row][col].getRedValue(), pixels[row][col].getGreenValue(), pixels[row][col].getBlueValue()));
									//b.setPixel(col, row, Color.argb(255, pixels[(row * width) + col].getRedValue(), pixels[(row * width) + col].getGreenValue(), pixels[(row * width) + col].getBlueValue()));
									b.setPixel(col, row, Color.argb(0, 0, 0, 0));
								}
							}
							
							double wP = mImageProcessedSurfaceView.getWidth();
							double hP = mImageProcessedSurfaceView.getHeight();
							double wB = b.getWidth();
							double hB = b.getHeight();
							double menos=1;
							double i = 1;
							double j = 1;

							Log.v(Singleton.TAG, "Surface="+wP+" x "+hP+"   Bitmap="+wB+" x "+hB);
							
							if (wP < wB)
							{
								i = wP/wB;
							}
							
							if (hP < hB)
							{
								j= hP/hB;
							}

							menos = Math.min(i,j);

							int wTotal = (int)(wB*menos);
							int hTotal = (int)(hB*menos);
							int left = (int)((wP-wTotal)/2);
							int top = (int)((hP-hTotal)/2);

							//mImageProcessedSurfaceView.setImageBitmap(b);
							
							//Canvas canvas = mImageProcessedSurfaceView.getHolder().lockCanvas();
							
							//if(canvas != null)
							//{
								//canvas.drawColor(Color.BLACK);
								//Log.v(Singleton.TAG, "left="+left+" top="+top+" width="+(wTotal+left)+" height="+(hTotal+top));
								//canvas.drawBitmap(b, new Rect(0,0,b.getWidth(),b.getHeight()), new Rect(left,top,wTotal+left,hTotal+top), null);
								//canvas.save();
								//mImageProcessedSurfaceView.getHolder().unlockCanvasAndPost(canvas);
							
								/**for(int i = 0; i < data.length; i = (i + 2)*width)
								{
									for(int j = 0; (j < data.length) && ((j / 2) < width); j = j + 2)
									{
										int col = i % width;
										int row = (i - col) / width;
										
										b.setPixel(row, col, Color.argb(100, pixels[row][col].getRedValue(), pixels[row][col].getGreenValue(), pixels[row][col].getBlueValue()));
									}
								}**/
							
								//Log.v(Singleton.TAG, "Here ready to set Bitmap to SurfaceView");
							//}
							//else
							//{
								//Log.v(Singleton.TAG, "ImageView == null");
							//}
							
						}
    					
    				};
    				Thread showImageThread = new Thread(showImage);
    				showImageThread.start();
    				Log.v(Singleton.TAG, "Started Thread to show image");
    				
    				mHandler.post(new Runnable(){

						public void run()
						{
							mImageProcessedSurfaceView.setImageBitmap(Singleton.updateImaveView);
						}
    					
    				});
    			}
    		}
    		
    	};
    	mCamera = new CameraWrapper(mHandler);
    	mSurfaceView = (SurfaceView) findViewById(R.id.CameraSurfaceView);
    	
    	mImageProcessedSurfaceView = (ImageView) findViewById(R.id.ImageProcessedSurfaceView);
    	
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
            	Log.e(Singleton.TAG, "ERROR: Start Preview of the camera failed");
            	finish();
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
		
		mCamera.startAutoFocus();
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