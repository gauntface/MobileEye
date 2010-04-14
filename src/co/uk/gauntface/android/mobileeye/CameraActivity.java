package co.uk.gauntface.android.mobileeye;

import java.util.HashMap;
import java.util.Locale;

import co.uk.gauntface.android.mobileeye.bluetooth.BluetoothConnectionThread;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.SurfaceHolder.Callback;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class CameraActivity extends Activity implements Callback
{
	/**
	 * Handler arg1 Values
	 */
	public static final int START_AUTO_FOCUS = 0;
	public static final int DRAW_IMAGE_PROCESSING = 1;
	
	public static final int AUTO_FOCUS_SUCCESSFUL = 0;
	public static final int AUTO_FOCUS_UNSUCCESSFUL = 1;
	
	public static final int BLUETOOTH_BYTES_RECEIVED = 2;
	public static final int BLUETOOTH_STREAMS_INIT = 3;
	
	public static final int ROTATE_PROJECTOR_VIEW = 4;
	
	public static final String ROTATE_LEFT_RIGHT_KEY = "RotateLeftRightKey";
	public static final String ROTATE_UP_DOWN_KEY = "RotateUpDownKey";
	
	private static final int TEXT_TO_SPEECH_REQ_CODE = 0;
	
	private SurfaceView mSurfaceView;
	private ImageView mImageProcessedSurfaceView;
	private boolean mStartPreviewFail;
	private CameraWrapper mCamera;
	private SurfaceHolder mSurfaceHolder;
	private Handler mHandler;
	private Button mOutputHistogramBtn;
	
	private BluetoothConnectionThread mBluetoothConnection;
	
	private boolean mHasTextToSpeech;
	private TextToSpeech mTextToSpeech;
	private final static String UTTERANCE_ID = "UtteranceID";
	private boolean mTextToSpeechIsFree;
	
    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera);
        
        initActivity();
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
    	if(requestCode == TEXT_TO_SPEECH_REQ_CODE)
    	{
    		if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS)
    		{
    			if(mTextToSpeech == null)
    			{
    				mTextToSpeech = new TextToSpeech(getApplicationContext(), new OnInitListener() {
    					
    					public void onInit(int status)
    					{
    						// On Completion (or Failure) of initialisation
    						if(status == TextToSpeech.SUCCESS)
    						{
    							mHasTextToSpeech = true;
    						}
    						else
    						{
    							
    						}
    					}
    				});
    				
    				mTextToSpeech.setLanguage(Locale.UK);
    			}
            }
    		else
    		{
                Intent installIntent = new Intent();
                installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);
            }
    	}
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
    	super.onPause();
    	
    	Log.v(Singleton.TAG, "MobileEye - onPause");
    	
        mCamera.stopPreview();
        
        // Close the camera now because other activities may need to use it.
        mCamera.closeCamera();
        
        if(mHasTextToSpeech == true)
    	{
    		mTextToSpeech.shutdown();
    		mHasTextToSpeech = false;
    	}
    }
    
    protected void onStop()
    {
    	super.onStop();
    	
    	if(mBluetoothConnection != null)
    	{
    		mBluetoothConnection.cancel();
    	}
    	
    	if(mHasTextToSpeech == true)
    	{
    		mTextToSpeech.shutdown();
    		mHasTextToSpeech = false;
    	}
    }
    
    protected void onDestroy()
    {
    	super.onDestroy();
    	
    	if(mBluetoothConnection != null)
    	{
    		mBluetoothConnection.cancel();
    	}
    	
    	if(mHasTextToSpeech == true)
    	{
    		mTextToSpeech.shutdown();
    		mHasTextToSpeech = false;
    	}
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
    					//mCamera.startAutoFocus();
    				}
    			}
    			else if(msg.arg1 == DRAW_IMAGE_PROCESSING)
    			{
    				mHandler.post(new Runnable(){

						public void run()
						{
							mImageProcessedSurfaceView.setImageBitmap(Singleton.updateImageView);
						}
    					
    				});
    			}
    			else if(msg.arg1 == BluetoothConnectionThread.BLUETOOTH_CONNECTION_LOST)
    			{
    				Intent intent = new Intent(getApplicationContext(), MobileEye.class);
    				startActivity(intent);
    				
    				finish();
    			}
    			else if(msg.arg1 == ROTATE_PROJECTOR_VIEW)
    			{		
    				if(mHasTextToSpeech == false)
    				{
    					Intent checkIntent = new Intent();
    					checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
    					startActivityForResult(checkIntent, TEXT_TO_SPEECH_REQ_CODE);
    				}
    				else
    				{
    					if(mTextToSpeechIsFree == true)
    	    			{
    	    				mTextToSpeechIsFree = false;
    	    				Log.v("mobileeye", "Rotate porjector view");
    	    				double rLeftRight = msg.getData().getDouble(ROTATE_LEFT_RIGHT_KEY);
    	    				double rUpDown = msg.getData().getDouble(ROTATE_UP_DOWN_KEY);
    	    				
    	    				Log.d("mobileeye", "rLR - " + rLeftRight);
    	    				
    	    				String s;
    					
    	    				if(rLeftRight < 0)
    	    				{
    	    					s = "Rotate left by "+Math.abs(rLeftRight)+" degrees";
    	    				}
    	    				else if(rLeftRight > 0)
    	    				{
    	    					s = "Rotate right by "+rLeftRight+" degrees";
    	    				}
    	    				else
    	    				{
    	    					s = "Rotate to 0 degrees";
    	    				}
    	    				
    	    				mTextToSpeech.setOnUtteranceCompletedListener(new OnUtteranceCompletedListener() {
								
								public void onUtteranceCompleted(String utteranceId)
								{
									Log.d("mobileeye", "Utterance Complete ID - " + utteranceId);
								    if(utteranceId.equals(UTTERANCE_ID))
								    {
								    	Log.d("mobileeye", "TextToSpeechIsFree = true");
								    	mTextToSpeechIsFree = true;
								    }
								}
							});
    	    				HashMap<String, String> hashParams = new HashMap();
    	    				hashParams.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, UTTERANCE_ID);
    	    				mTextToSpeech.speak(s, TextToSpeech.QUEUE_FLUSH, hashParams);
    	    			}
    				}
    			}
    		}
    		
    	};
    	
    	mHasTextToSpeech = false;
    	mTextToSpeechIsFree = true;
    	
    	mBluetoothConnection = Singleton.getBluetoothConnection();
    	if(mBluetoothConnection != null)
    	{
    		mBluetoothConnection.setHandler(mHandler);
    	}
    	
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
        
        mOutputHistogramBtn = (Button) findViewById(R.id.OutputHistogramBtn);
        mOutputHistogramBtn.setOnClickListener(new OnClickListener(){

			public void onClick(View view)
			{
				mCamera.logHistogram();
			}
        	
        });
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
            Log.v(Singleton.TAG, "Camera surfaceChanged holder.getSurface() == null");
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
