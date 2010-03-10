package co.uk.gauntface.android.mobileeye.imageprocessing;

import android.util.Log;
import co.uk.gauntface.android.mobileeye.Singleton;

public class QuickSegmentFactory
{
	private static QuickSegment mQuickSegment = null;
	
	public static QuickSegment getQuickSegment()
	{
		if(mQuickSegment == null)
		{
			Log.v(Singleton.TAG, "Creating new Quick Segment");
			mQuickSegment = new QuickSegment();
		}
		
		return mQuickSegment;
	}
}
