package co.uk.gauntface.android.mobileeye.bluetooth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.UUID;

import co.uk.gauntface.android.mobileeye.CameraActivity;
import co.uk.gauntface.android.mobileeye.MobileEye;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class BluetoothConnectionThread extends Thread
{
	public static final int BLUETOOTH_CONNECTION_LOST = 50;
	
	private final BluetoothDevice mBluetoothDevice;
	private final BluetoothSocket mBluetoothSocket;
	
	private InputStream mInputStream;
	private OutputStream mOutputStream;
	
	private Handler mHandler;
	
	public BluetoothConnectionThread(BluetoothDevice d, Handler h)
	{
		mBluetoothDevice = d;
		mHandler = h;
		
		BluetoothSocket tmp = null;
		try
		{
			tmp = mBluetoothDevice.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
			
		}
		catch (IOException e)
		{
			Message msg = Message.obtain();
			msg.arg1 = MobileEye.BLUETOOTH_CONNECT_FAILED;
			msg.arg2 = 0;
			synchronized(mHandler)
	    	{
				mHandler.sendMessage(msg);
	    	}
			
			Log.e("mobileeye", "Connection to bluetooth server failed");
			e.printStackTrace();
		}
		
		mBluetoothSocket = tmp;
	}
	
	public void run()
	{
		// Cancel discovery because it will slow down the connection
		// Done in UI Thread atm
        //mAdapter.cancelDiscovery();

        try
        {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            mBluetoothSocket.connect();
        }
        catch (IOException connectException)
        {
        	Message msg = Message.obtain();
			msg.arg1 = MobileEye.BLUETOOTH_CONNECT_FAILED;
			msg.arg2 = 1;
			synchronized(mHandler)
	    	{
				mHandler.sendMessage(msg);
	    	}
			
            // Unable to connect; close the socket and get out
            try
            {
            	mBluetoothSocket.close();
            }
            catch (IOException closeException)
            {
            	Message msg2 = Message.obtain();
    			msg.arg1 = MobileEye.BLUETOOTH_CONNECT_FAILED;
    			msg.arg2 = 2;
    			synchronized(mHandler)
    	    	{
    				mHandler.sendMessage(msg);
    	    	}
            }
            
            return;
        }
        
        Message msg = Message.obtain();
        msg.arg1 = MobileEye.BLUETOOTH_CONNECT_SUCCESSFUL;
        synchronized(mHandler)
    	{
			mHandler.sendMessage(msg);
    	}
        
        // Do work to manage the connection (in a separate thread)
        manageConnectedSocket();
    }

    private void manageConnectedSocket()
    {	
		InputStream tmpIn = null;
        OutputStream tmpOut = null;

        // Get the input and output streams, using temp objects because
        // member streams are final
        try
        {
            tmpIn = mBluetoothSocket.getInputStream();
            tmpOut = mBluetoothSocket.getOutputStream();
        }
        catch(IOException e)
        {
        	
        }

        mInputStream = tmpIn;
        mOutputStream = tmpOut;
        
        Message msg = Message.obtain();
        msg.arg1 = MobileEye.BLUETOOTH_STREAMS_INIT;
        synchronized(mHandler)
    	{
			mHandler.sendMessage(msg);
    	}
        
        byte[] buffer = new byte[1024];  // buffer store for the stream
        int noBytes;
        
        while (true)
        {
        	try
    		{
    			noBytes = mInputStream.read(buffer);
    			byte[] recvInfo = new byte[noBytes];
    			
    			// Keep listening to the InputStream until an exception occurs
    	        for(int i = 0; i < noBytes; i++)
    	        {
    	        	recvInfo[i] = buffer[i];
    	        }
    	        
    	        String data = new String(recvInfo);
    	        
    	        Log.v("mobileeye", "Data Received - " + data);
    	        
    	        if(data.equals("<ConnectionConfirm></ConnectionConfirm>"))
    	        {
    	        	Message successMsg = Message.obtain();
    	        	successMsg.arg1 = MobileEye.BLUETOOTH_CONNECT_CONFIRMED;
                    synchronized(mHandler)
                	{
            			mHandler.sendMessage(successMsg);
                	}
    	        }
    		}
    		catch (IOException e1)
    		{
    			Log.v("mobileeye", "IOException occured");
            	Message errorMsg = Message.obtain();
            	errorMsg.arg1 = BLUETOOTH_CONNECTION_LOST;
                synchronized(mHandler)
            	{
        			mHandler.sendMessage(errorMsg);
            	}
                break;
    		}
        }
	}

    /* Call this from the main Activity to send data to the remote device */
    public void write(byte[] bytes)
    {
        try
        {
        	mOutputStream.write(bytes);
        }
        catch (IOException e)
        {
        	Log.e("mobileeye", "Error when writing bytes");
        }
    }
    
	/** Will cancel an in-progress connection, and close the socket */
    public void cancel()
    {
        try
        {
        	mBluetoothSocket.close();
        }
        catch (IOException e)
        {
        	Message msg = Message.obtain();
			msg.arg1 = MobileEye.BLUETOOTH_CONNECT_FAILED;
			msg.arg2 = 3;
			synchronized(mHandler)
	    	{
				mHandler.sendMessage(msg);
	    	}
        }
    }
    
    public void setHandler(Handler h)
    {
    	synchronized(mHandler)
    	{
    		mHandler = h;
        }
    }
}
