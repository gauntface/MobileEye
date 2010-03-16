package co.uk.gauntface.android.mobileeye.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import co.uk.gauntface.android.mobileeye.MobileEye;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class BluetoothConnectionThread extends Thread
{
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
			msg.arg1 = BluetoothEstablishConnection.BLUETOOTH_CONNECT_FAILED;
			msg.arg2 = 0;
			mHandler.sendMessage(msg);
			
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
			msg.arg1 = BluetoothEstablishConnection.BLUETOOTH_CONNECT_FAILED;
			msg.arg2 = 1;
			mHandler.sendMessage(msg);
			
            // Unable to connect; close the socket and get out
            try
            {
            	mBluetoothSocket.close();
            }
            catch (IOException closeException)
            {
            	Message msg2 = Message.obtain();
    			msg.arg1 = BluetoothEstablishConnection.BLUETOOTH_CONNECT_FAILED;
    			msg.arg2 = 2;
    			mHandler.sendMessage(msg2);
    			
            }
            
            return;
        }
        
        Message msg = Message.obtain();
        msg.arg1 = BluetoothEstablishConnection.BLUETOOTH_CONNECT_SUCCESSFUL;
        mHandler.sendMessage(msg);
        
        // Do work to manage the connection (in a separate thread)
        manageConnectedSocket();
    }

    private void manageConnectedSocket()
    {	
		InputStream tmpIn = null;
        OutputStream tmpOut = null;

        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = mBluetoothSocket.getInputStream();
            tmpOut = mBluetoothSocket.getOutputStream();
        } catch (IOException e) { }

        mInputStream = tmpIn;
        mOutputStream = tmpOut;
        
        Message msg = Message.obtain();
        msg.arg1 = MobileEye.BLUETOOTH_STREAMS_INIT;
        mHandler.sendMessage(msg);
        
        byte[] buffer = new byte[1024];  // buffer store for the stream
        int bytes; // bytes returned from read()

        // Keep listening to the InputStream until an exception occurs
        while (true)
        {
            try
            {
                // Read from the InputStream
                bytes = mInputStream.read(buffer);
                // Send the obtained bytes to the UI Activity
                Log.v("mobileeye", "Received data");
                //mHandler.obtainMessage(BluetoothTest.BLUETOOTH_BYTES_RECEIVED, bytes, -1, buffer).sendToTarget();
            }
            catch (IOException e)
            {
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
			msg.arg1 = BluetoothEstablishConnection.BLUETOOTH_CONNECT_FAILED;
			msg.arg2 = 3;
			mHandler.sendMessage(msg);
        }
    }
}
