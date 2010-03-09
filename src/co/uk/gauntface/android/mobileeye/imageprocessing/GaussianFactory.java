package co.uk.gauntface.android.mobileeye.imageprocessing;

import android.util.Log;
import co.uk.gauntface.android.mobileeye.Singleton;

/**
 * This class is required to speed up implementation by only creating
 * the GaussianBlur Kernel once rather than every processed image
 * @author matt
 *
 */
public class GaussianFactory
{
	private static double GAUSSIAN_BLUR_SIGMA = 1.0;
	private static GaussianBlur mGaussianBlur = null;
	
	public static GaussianBlur getGaussianBlur()
	{
		if(mGaussianBlur == null)
		{
			Log.v(Singleton.TAG, "Creating new Gaussian Blur");
			mGaussianBlur = new GaussianBlur(GAUSSIAN_BLUR_SIGMA);
		}
		
		return mGaussianBlur;
	}
}
