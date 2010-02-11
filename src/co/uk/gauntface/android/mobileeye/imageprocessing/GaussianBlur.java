package co.uk.gauntface.android.mobileeye.imageprocessing;

import co.uk.gauntface.android.mobileeye.Singleton;
import android.graphics.Bitmap;
import android.util.Log;

public class GaussianBlur
{	
	private double mSigma;
	
	private long[][] mKernel;
	
	public GaussianBlur(double sigma)
	{
		mSigma = sigma;
		
		createGaussianKernel();
	}
	
	private void createGaussianKernel()
	{
		// Size is less than 6 x sigma
		int sizeOfKernel = (int) Math.floor(6 * mSigma);
		int halfSize = (int) Math.floor((sizeOfKernel/2));
		
		mKernel = new long[sizeOfKernel][sizeOfKernel];
		
		for(int i = 0; i < sizeOfKernel; i++)
		{
			for(int j = 0; j < sizeOfKernel; j++)
			{
				int xDist = i % halfSize;
				int yDist = j % halfSize;
				
				mKernel[i][j] = getDistrbutionValue(xDist, yDist);
			}
		}
	}
	
	private long getDistrbutionValue(int xValue, int yValue)
	{
		return (long) ((1 / (2 * Math.PI * (mSigma * mSigma))) * Math.exp(-(((xValue * xValue) + (yValue * yValue)) / (2 * (mSigma * mSigma)))));
	}
	
	public byte[] blurImage(byte[] data, int width, int height)
	{
		Log.v(Singleton.TAG, "Data size = " + data.length + " width = " + width + " height = " + height);
		
		if((data.length % 2) == 0)
		{
			RGB565 rgbPixelValue = new RGB565(new byte[]{data[0], data[1]});
			
			for(int i = 0; i < data.length; i = i + 2)
			{
				//RGB565 rgbPixelValue = new RGB565(new byte[]{data[i], data[i + 1]});
			}
		}
		else
		{
			Log.e(Singleton.TAG, "Data isn't a modulo of 2 which is VERY unexpected");
			throw new RuntimeException("Data in GaussianBlur isn't modulo of 2");
		}
		
		try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
}
