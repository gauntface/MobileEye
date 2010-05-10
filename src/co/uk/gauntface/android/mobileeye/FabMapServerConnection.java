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
	private String IP_ADDRESS = "172.21.97.21";
	private int PORT_ADDRESS = 1112;
	
	private Socket mSocketConnection;
	private DataOutputStream mOutputData;
	private BufferedReader mInputData;
	
	public FabMapServerConnection()
	{
		
	}
	
	public void run()
	{
		try
		{
			Log.d("mobileeye", "Connectiong to socket...");
			mSocketConnection = new Socket(IP_ADDRESS, PORT_ADDRESS);
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
			Log.d("mobileeye", "UnknownHostException @ "+IP_ADDRESS+" - "+e);
		}
		catch (IOException e)
		{
		    Log.d("mobileeye", "IOException - " + e);
		}
	}
}
