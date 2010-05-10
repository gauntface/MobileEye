package co.uk.gauntface.android.mobileeye;

import java.util.HashMap;
import java.util.Locale;

import co.uk.gauntface.android.mobileeye.bluetooth.BluetoothConnectionThread;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.SurfaceHolder.Callback;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CameraActivity extends Activity implements Callback
{
	/**
	 * Handler arg1 Values
	 */
	public static final int START_AUTO_FOCUS = 0;
	public static final int DRAW_IMAGE_PROCESSING = 1;
	public static final int BLUETOOTH_BYTES_RECEIVED = 2;
	public static final int BLUETOOTH_STREAMS_INIT = 3;
	public static final int ROTATE_PROJECTOR_VIEW = 4;
	public static final int APPLICATION_STATE_CHANGED = 5;
	public static final int SHOW_TOAST = 6;
	public static final int HARDWARE_BUTTON_PRESS = 7;
	
	public static final int AUTO_FOCUS_SUCCESSFUL = 0;
	public static final int AUTO_FOCUS_UNSUCCESSFUL = 1;
	
	public static final String TOAST_STRING_KEY = "ShowToastStringKey";
	
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
	private TextView mStatusTextView;
	
	private BluetoothConnectionThread mBluetoothConnection;
	private FabMapServerConnection mFabMapServerConnection;
	
	private boolean mHasTextToSpeech;
	private TextToSpeech mTextToSpeech;
	private final static String UTTERANCE_ID = "UtteranceID";
	private boolean mTextToSpeechIsFree;
	
    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera);
        
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
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
    							Singleton.setApplicationState(Singleton.STATE_FINDING_AREA);
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
    	else
    	{
    		Log.d("mobileeye", "Received some unknown activity result");
    	}
    }
    
    @Override
    public void onStart()
    {
    	super.onStart();
    	
    	getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    	
    	Log.v(Singleton.TAG, "MobileEye - onStart");
    }
    
    @Override
    public void onResume() {
        super.onResume();

        Log.v(Singleton.TAG, "MobileEye - onResume");
        
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
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
    	
    	Log.d(Singleton.TAG, "MobileEye - onPause");
    	
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
    	
    	Log.d(Singleton.TAG, "MobileEye - onStop");
    	
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
    	
    	Log.d(Singleton.TAG, "MobileEye - onDestroy");
    	
    	if(mBluetoothConnection != null)
    	{
    		mBluetoothConnection.kill();
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
    				Log.v("mobileeye", "Bluetooth Connection Lost Message Received");
    				Intent intent = new Intent(getApplicationContext(), MobileEye.class);
    				startActivity(intent);
    				
    				finish();
    			}
    			else if(msg.arg1 == ROTATE_PROJECTOR_VIEW)
    			{
    				Log.v("mobileeye", "Rotate Projector View Message Received");
    				mHandler.post(new Runnable(){

						public void run()
						{
							mStatusTextView.setText("Set up markers");
						}
    					
    				});
    				
    				if(mHasTextToSpeech == true && (mTextToSpeechIsFree == true || mTextToSpeechIsFree == false))
    	    		{
    	    			mTextToSpeechIsFree = false;
    	    			Log.v("mobileeye", "Rotate porjector view");
    	    			
    	    			//Singleton.setApplicationState(Singleton.STATE_TEST_SUITABLE_PROJECTION);
    	    			
    	    			double rLeftRight = msg.getData().getDouble(ROTATE_LEFT_RIGHT_KEY);
    	    			double rUpDown = msg.getData().getDouble(ROTATE_UP_DOWN_KEY);
    	    			
    	    			//Log.d("mobileeye", "rLR - " + rLeftRight);
    	    			
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
    	    			
    	    			if(mBluetoothConnection != null)
    	    			{
    	    				String btMsg = new String("<ShowMarkers>"+Math.abs(rLeftRight)+"</ShowMarkers>");
    	    				mBluetoothConnection.write(btMsg.getBytes());
    	    			}
    	    			
    	    			mTextToSpeech.setOnUtteranceCompletedListener(new OnUtteranceCompletedListener() {
							
							public void onUtteranceCompleted(String utteranceId)
							{
								//Log.d("mobileeye", "Utterance Complete ID - " + utteranceId);
							    if(utteranceId.equals(UTTERANCE_ID))
							    {
							    	//Log.d("mobileeye", "TextToSpeechIsFree = true");
							    	mTextToSpeechIsFree = true;
							    }
							}
						});
    	    			HashMap<String, String> hashParams = new HashMap();
    	    			hashParams.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, UTTERANCE_ID);
    	    			mTextToSpeech.speak(s, TextToSpeech.QUEUE_FLUSH, hashParams);
    	    		}
    			}
    			else if(msg.arg1 == APPLICATION_STATE_CHANGED)
    			{
    				Log.v("mobileeye", "Application State Changed");
    				if(mBluetoothConnection != null)
    				{
    					if(Singleton.getApplicationState() == Singleton.STATE_FINDING_AREA)
    					{
    						String hideMarkers = new String("<HideMarkers></HideMarkers>");
    						mBluetoothConnection.write(hideMarkers.getBytes());
    						
    						mHandler.post(new Runnable(){

    							public void run()
    							{
    								mStatusTextView.setText("Searching for projectable area");
    							}
    	    					
    	    				});
    					}
    				}
    			}
    			else if(msg.arg1 == SHOW_TOAST)
    			{
    				final String s = msg.getData().getString(TOAST_STRING_KEY); 
    				mHandler.post(new Runnable(){

						public void run()
						{
		    				Toast t = Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG);
		    				t.show();
						}
    					
    				});
    			}
    			else if(msg.arg1 == HARDWARE_BUTTON_PRESS)
    			{
    				if(msg.arg2 == KeyEvent.KEYCODE_HEADSETHOOK)
    				{
    					if(Singleton.getApplicationState() == Singleton.STATE_SETTING_UP_MARKERS)
    					{
    						mHandler.post(new Runnable(){

    							public void run()
    							{
    			    				Toast t = Toast.makeText(getApplicationContext(), "Beginning set up projection", Toast.LENGTH_LONG);
    			    				t.show();
    			    				mStatusTextView.setText("Looking for markers");
    							}
    	    					
    	    				});
    						
    						Singleton.setApplicationState(Singleton.STATE_PROJECTING_MARKERS);
    					}
    				}
    			}
    		}
    		
    	};
    	
    	mHasTextToSpeech = false;
    	mTextToSpeechIsFree = true;
    	
    	Intent checkIntent = new Intent();
		checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
		startActivityForResult(checkIntent, TEXT_TO_SPEECH_REQ_CODE);
    	
    	HardButtonReceiver buttonReceiver = new HardButtonReceiver(mHandler);
    	IntentFilter iF = new IntentFilter(Intent.ACTION_MEDIA_BUTTON);
    	iF.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
    	registerReceiver(buttonReceiver, iF);
    	
    	mBluetoothConnection = Singleton.getBluetoothConnection();
    	if(mBluetoothConnection != null)
    	{
    		mBluetoothConnection.setHandler(mHandler);
    	}
    	
    	mFabMapServerConnection = new FabMapServerConnection();
    	mFabMapServerConnection.start();
    	
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
        
        mStatusTextView = (TextView) findViewById(R.id.StatusTextView);
        mStatusTextView.setText("Initialising...");
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
