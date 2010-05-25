package co.uk.gauntface.android.mobileeye.fabmap;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import co.uk.gauntface.android.mobileeye.CameraActivity;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class FabMapServerConnection extends Thread
{
	private int PORT_ADDRESS = 1112;
	
	private String mServerAddr;
	
	private Socket mSocketConnection;
	private DataOutputStream mOutputData;
	private DataInputStream mInputData;
	private Handler mHandler;
	
	private boolean mClosingConnection = false;
	private boolean mActivityPaused = false;
	
	public FabMapServerConnection(String a, Handler h)
	{
		mServerAddr = a;
		mHandler = h;
	}
	
	public void run()
	{
		try
		{
			Log.d("mobileeye", "Connecting to socket...");
			mSocketConnection = new Socket(mServerAddr, PORT_ADDRESS);
			Log.d("mobileeye", "Connected to socket");
			
			manageConnectedSocket();
		}
		catch(UnknownHostException e)
		{
			Log.d("mobileeye", "UnknownHostException @ "+mServerAddr+" - "+e);
			Message msg = mHandler.obtainMessage();
			msg.arg1 = CameraActivity.FABMAP_CONNECT_FAILED;
			mHandler.dispatchMessage(msg);
		}
		catch (IOException e)
		{
		    Log.d("mobileeye", "IOException - " + e);
		    Message msg = mHandler.obtainMessage();
			msg.arg1 = CameraActivity.FABMAP_CONNECT_FAILED;
			mHandler.dispatchMessage(msg);
		}
	}
	
	private void manageConnectedSocket()
	{
		DataOutputStream tmpOut = null;
		DataInputStream tmpIn = null;
		
	    try
	    {
	    	Log.d("mobileeye", "Openning input output streams...");
	    	tmpOut = new DataOutputStream(mSocketConnection.getOutputStream());
	    	tmpIn = new DataInputStream(mSocketConnection.getInputStream());
		    Log.d("mobileeye", "Data streams opened");
		    
		    mSocketConnection.setSoTimeout(3000);
		}
	    catch (IOException e)
	    {
			Log.d("mobileeye", "Streams Failed to Open - "+e);
			
		    Message msg = mHandler.obtainMessage();
			msg.arg1 = CameraActivity.FABMAP_CONNECT_FAILED;
			mHandler.dispatchMessage(msg);
		}
	    
	    if(tmpOut != null && tmpIn != null)
	    {
	    	mOutputData = tmpOut;
	    	mInputData = tmpIn;
	    	
	    	Message msg = Message.obtain();
	    	msg.arg1 = CameraActivity.FABMAP_STREAMS_INIT;
	    	synchronized(mHandler)
	    	{
	    		mHandler.dispatchMessage(msg);
	    	}
	    	
	    	byte[] buffer = new byte[1024];  // buffer store for the stream
	    	int noBytes;
	    	
	    	while (true)
	    	{
	    		try
	    		{
	    			noBytes = mInputData.read(buffer);
	    			byte[] recvInfo = new byte[noBytes];
    			
	    			// Keep listening to the InputStream until an exception occurs
	    			for(int i = 0; i < noBytes; i++)
	    			{
	    				recvInfo[i] = buffer[i];
	    			}
    	        
	    			String data = new String(recvInfo);
	    			data = data.trim();
	    			
	    			Log.v("mobileeye", "Data Received - " + data);
    	        
	    			if(data.equals("<ConnectionConfirm></ConnectionConfirm>"))
	    			{
	    				Message successMsg = Message.obtain();
	    				successMsg.arg1 = CameraActivity.FABMAP_CONNECT_CONFIRMED;
	    				synchronized(mHandler)
	    				{
	    					mHandler.sendMessage(successMsg);
	    				}
	    			}
	    			else if(data.startsWith("<FabMapResult>"))
	    			{
	    				String productID = data.substring(14, data.length() - 15);
	    				
	    				int id = Integer.parseInt(productID);
	    				
	    				Message photoSentmsg = mHandler.obtainMessage();
	    				photoSentmsg.arg1 = CameraActivity.FABMAP_PHOTO_SENT;
	    				photoSentmsg.arg2 = id;
	    				
	    				mHandler.dispatchMessage(photoSentmsg);
	    			}
	    		}
	    		catch(SocketTimeoutException eT)
	    		{
	    			if(mClosingConnection == true)
	    			{
	    				break;
	    			}
	    		}
	    		catch (IOException e1)
	    		{
	    			if(mClosingConnection == false && mActivityPaused == false)
	    			{
	    				Log.v("mobileeye", "IOException occured - " + e1);
	    				Message errorMsg = Message.obtain();
	    				errorMsg.arg1 = CameraActivity.FABMAP_CONNECT_FAILED;
	    				synchronized(mHandler)
	    				{
	    					mHandler.sendMessage(errorMsg);
	    				}
	    			}
	    			break;
	    		}
	    		catch(Exception e)
	    		{
	    			if(mClosingConnection == false && mActivityPaused == false)
	    			{
	    				Log.v("mobileeye", "IOException occured - " + e);
	    				Message errorMsg = Message.obtain();
	    				errorMsg.arg1 = CameraActivity.FABMAP_CONNECT_FAILED;
	    				synchronized(mHandler)
	    				{
	    					mHandler.sendMessage(errorMsg);
	    				}
	    			}
	    			break;
	    		}
	    	}
	    }
	}
	
	
	public void write(byte[] bytes)
	{
		Log.d("mobileeye", "Writing data to FabMap - " + new String(bytes));
        try
        {
        	mOutputData.write(bytes);
        }
        catch (IOException e)
        {
        	Log.e("mobileeye", "Error when writing bytes");
        }
	}
	
	/** Will cancel any in-progress connection, and close the socket */
    public void cancel()
    {
        try
        {
        	Log.d("mobileeye", "FabmapConnection.cancel() Called");
        	mOutputData.close();
        	mInputData.close();
        	
        	mSocketConnection.close();
        }
        catch (IOException e)
        {
        	/**
        	 * We want the connection to die quietly
        	 */
        	Log.d("mobileeye", "FabMapSocketConnection.cancel() Exception Caused - " + e);
        }
        catch(Exception e)
        {
        	Log.d("mobileeye", "FabMapSocketConnection.cancel() Exception Caused - " + e);
        }
    }
    
    public void kill()
    {
    	mClosingConnection = true;
    	cancel();
    }
    
    public void pauseCalled()
    {
    	Log.d("mobileeye", "Activity Paused Called");
    	mActivityPaused = true;
    	Runnable r = new Runnable(){

			public void run()
			{
				try
				{
					Thread.sleep(3000);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				Log.d("mobileeye", "Paused Timeout");
				pausedTimeOut();
			}
		};
		Thread t = new Thread(r);
		t.start();
    }

	private void pausedTimeOut()
	{
		Log.d("mobileeye", "Paused Timeout Function Start");
		if(mActivityPaused == true)
		{
			kill();
			Log.d("mobileeye", "Paused Timeout - Kill Called");
		}
	}

	public void activityContinue()
	{
		mActivityPaused = false;
	}

	public void transmitPhoto(final byte[] data)
	{
		Log.d("mobileeye", "In Transmit data");
		Thread t = new Thread(new Runnable(){

			public void run()
			{
				write(new String("<Image>"+data.length+"</Image>").getBytes());
				write(data);
			}
			
		});
		t.start();
	}
}
