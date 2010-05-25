package co.uk.gauntface.android.mobileeye;

import java.util.ArrayList;

import co.uk.gauntface.android.mobileeye.bluetooth.BluetoothListAdapter;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;

public class MobileEye extends Activity
{
	private BluetoothAdapter mBluetoothAdapter;
	private ListView mListView;
	private BluetoothListAdapter mListAdapter;
	private ArrayList<BluetoothDevice> mBluetoothDevices;
	private Handler mHandler;
	
	private BluetoothDevice mChoosenBluetoothDevice;
	
	private static int REQUEST_ENABLE_BT = 0;
	
	// Initialised here as we need to maintain this receiver through
	// the Activity lifecycle
	private final BroadcastReceiver mReceiver = new BroadcastReceiver()
	{
	    public void onReceive(Context context, Intent intent)
	    {
	        String action = intent.getAction();
	        
	        if (BluetoothDevice.ACTION_FOUND.equals(action))
	        {
	            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
	            
	            Log.v("mobileeye", "Name: "+device.getName() + " Address: " + device.getAddress());
	            
	            mListAdapter.add(device.getName() + "\n" + device.getAddress());
	            mBluetoothDevices.add(device);
	            mListView.invalidateViews();
	        }
	    }
	};
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.establish_bluetooth_connection);
        
        initActivity();
        executeActivity();
    }
    
    @Override
    public void onStart()
    {
    	super.onStart();
    	
    	IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
    	registerReceiver(mReceiver, filter);
    }
    
    @Override
    public void onResume()
    {
        super.onResume();
        
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
    	registerReceiver(mReceiver, filter);
    }
    
    @Override
    protected void onPause()
    {
        super.onPause();
    }
    
    protected void onStop()
    {
    	super.onStop();
    	
    	try
    	{
    		unregisterReceiver(mReceiver);
    	}
    	catch(Exception e)
    	{
    		
    	}
    }

    protected void onDestroy()
    {
    	super.onDestroy();
    	
    	try
    	{
    		unregisterReceiver(mReceiver);
    	}
    	catch(Exception e)
    	{
    		
    	}
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
    	if(requestCode == REQUEST_ENABLE_BT)
    	{
    		// Handle the result from requesting to have Bluetooth switched on
    		if(resultCode == Activity.RESULT_OK)
    		{
    			beginScanning();
    		}
    		else
    		{
    			Toast t = Toast.makeText(getApplicationContext(),
						"Failed to turn on Bluetooth",
						Toast.LENGTH_LONG);
				
				t.show();
    		}
    	}
    }
    
    private void initActivity()
    {
    	mHandler = new Handler(){
    		
    		public void handleMessage(Message msg)
    		{
    			
    		}
    		
    	};
    	
    	IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
    	registerReceiver(mReceiver, filter);
    	
    	Button bluetoothConnect = (Button) findViewById(R.id.BluetoothConnectBtn);
    	bluetoothConnect.setOnClickListener(new OnClickListener(){

			public void onClick(View v)
			{
				if(mBluetoothAdapter != null)
				{
					mListAdapter.clear();
					mBluetoothDevices.clear();
					
					if(mBluetoothAdapter.isEnabled() == true)
					{
						beginScanning();
					}
					else
					{
						Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    		    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
					}
				}
			}
    		
    	});
    	
    	Button bluetoothSkip = (Button) findViewById(R.id.BluetoothSkipBtn);
    	bluetoothSkip.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v)
			{
				startNextActivity(null);
			}
		});
    	
    	mListAdapter = new BluetoothListAdapter(getApplicationContext());
    	mBluetoothDevices = new ArrayList<BluetoothDevice>();
    	
    	mListView = (ListView) findViewById(R.id.BluetoothDeviceListView);
    	mListView.setAdapter(mListAdapter);
    	
    	mListView.setOnItemLongClickListener(new OnItemLongClickListener(){

			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				mChoosenBluetoothDevice = mBluetoothDevices.get(position);
				
				mBluetoothAdapter.cancelDiscovery();
				
				startNextActivity(mChoosenBluetoothDevice);
				
				return true;
			}
    		
    	});
    }
    
    public void executeActivity()
    {
    	mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    	
    	if (mBluetoothAdapter != null)
    	{
    		if (mBluetoothAdapter.isEnabled() == true)
    		{
    			beginScanning();
    		}
    		else
    		{
    			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
    		    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    		}
    	}
    	else
    	{
    		Toast t = Toast.makeText(getApplicationContext(),
					"This device doesn't support Bluetooth",
					Toast.LENGTH_LONG);
			
			t.show();
    	}
    }
    
    private void beginScanning()
    {
    	boolean discoverySuccess = mBluetoothAdapter.startDiscovery();
    	
    	if(discoverySuccess == false)
    	{
    		Log.e("mobileeye", "Error occured when attempting to discover bluetooth devices");
    	}
    }
    
    private void startNextActivity(BluetoothDevice btDevice)
    {
    	Singleton.setBluetoothDevice(btDevice);
		
		Toast t = Toast.makeText(getApplicationContext(),
				"Starting FabMap Activity",
				Toast.LENGTH_LONG);
		
		t.show();
		
		Intent intent = new Intent(getApplicationContext(), FabMapInit.class);
		startActivity(intent);
		
		finish();
    }
}