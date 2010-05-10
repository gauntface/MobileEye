package co.uk.gauntface.android.mobileeye;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import android.util.Log;

public class FabMapServerConnection extends Thread
{
	private int PORT_ADDRESS = 1112;
	
	private String mServerAddr;
	
	private Socket mSocketConnection;
	private DataOutputStream mOutputData;
	private BufferedReader mInputData;
	
	public FabMapServerConnection(String a)
	{
		mServerAddr = a;
	}
	
	public void run()
	{
		try
		{
			Log.d("mobileeye", "Connectiong to socket...");
			mSocketConnection = new Socket(mServerAddr, PORT_ADDRESS);
			Log.d("mobileeye", "Connected to socket");
			
			Log.d("mobileeye", "Openning input output streams...");
		    mOutputData = new DataOutputStream(mSocketConnection.getOutputStream());
		    mInputData = new BufferedReader(new InputStreamReader(mSocketConnection.getInputStream()));
		    Log.d("mobileeye", "Data streams opened");
		    
		    mOutputData.writeChars("Hello FabMap Server :-)\n");
		    
		    mInputData.close();
		    mOutputData.close();
		    
		    mSocketConnection.close();
		}
		catch(UnknownHostException e)
		{
			Log.d("mobileeye", "UnknownHostException @ "+mServerAddr+" - "+e);
		}
		catch (IOException e)
		{
		    Log.d("mobileeye", "IOException - " + e);
		}
	}
}
