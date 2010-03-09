package co.uk.gauntface.android.mobileeye.imageprocessing;

import android.util.Log;
import co.uk.gauntface.android.mobileeye.Singleton;

public class EdgeFactory
{
	private static EdgeDetection mEdgeDetector = null;
	
	public static EdgeDetection getEdgeDetector()
	{
		if(mEdgeDetector == null)
		{
			Log.v(Singleton.TAG, "Creating new Edge Detection");
			mEdgeDetector = new EdgeDetection();
		}
		
		return mEdgeDetector;
	}
}
