package co.uk.gauntface.android.mobileeye.imageprocessing;

import co.uk.gauntface.android.mobileeye.Singleton;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
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
			//RGB565 rgbPixelValue = new RGB565(new byte[]{data[0], data[1]});
			
			// TODO: Optimise this to require one loop and and generate byte[] rather than two loops
			RGB565[][] pixels = new RGB565[height][width];
			
			for(int i = 0; i < data.length; i = i + 2)
			{
				RGB565 rgbPixelValue = new RGB565(new byte[]{data[i], data[i+1]});
				
				int col = i % width;
				int row = (i - col) / width;
				
				//Log.v(Singleton.TAG, "Here 0 <-- row = "+row+", col = "+col);
				
				pixels[row][col] = rgbPixelValue;
			}
			
			/**for(int i = 0; i < data.length; i = (i + 2)*width)
			{
				for(int j = 0; (j < data.length) && ((j / 2) < width); j = j + 2)
				{
					RGB565 rgbPixelValue = new RGB565(new byte[]{data[i+j], data[i+j+1]});
					int row = (i%2) / width;
					int col = (j%2);
					
					Log.v(Singleton.TAG, "Here 0 <-- row = "+row+", col = "+col);
					
					pixels[row][col] = rgbPixelValue;
				}
			}**/
			
			byte[] outputData = new byte[(width * height) * 2];
			
			for(int i = 0; i < width; i++)
			{
				for(int j = 0; j < height; j++)
				{
					Log.v(Singleton.TAG, "Here 1 <-- i = "+i+", j = "+j);
					byte[] pixelByteValues = pixels[i][j].getBytes(PixelFormat.RGB_565);
					Log.v(Singleton.TAG, "Here 2 <-- i = "+i+", j = "+j);
					
					if(pixelByteValues.length == 2)
					{
						int col = (i * width) + j;
						outputData[col] = pixelByteValues[0];
						outputData[col] = pixelByteValues[1];
					}
				}
			}
			
			return outputData;
		}
		else
		{
			Log.e(Singleton.TAG, "Data isn't a modulo of 2 which is VERY unexpected");
			throw new RuntimeException("Data in GaussianBlur isn't modulo of 2");
		}
	}
}
