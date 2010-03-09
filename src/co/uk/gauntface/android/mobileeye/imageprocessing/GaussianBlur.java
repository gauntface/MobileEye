package co.uk.gauntface.android.mobileeye.imageprocessing;

import co.uk.gauntface.android.mobileeye.Singleton;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.util.Log;

public class GaussianBlur
{	
	private double mSigma;
	
	private double[] mKernel;
	private int mKernelSize;
	private double mKernelTotal;
	
	private ComplexNumber[] mFourierKernel;
	
	/**
	 * 
	 * @param sigma		Standard Deviation
	 */
	public GaussianBlur(double sigma)
	{
		mSigma = sigma;
		
		createGaussianKernel();
		
		mFourierKernel = IPUtility.computeFreqDomain(mKernel);
	}

	private ComplexNumber calcFourierComp(int k, int n, int totalN)
	{
		double u = (-2 * Math.PI * k * n) / totalN; 
		
		double real = Math.cos(u);
		double img = Math.sin(u);
		
		return new ComplexNumber(real, img);
	}

	/**
	 * The size of the kernel is 3 * the standard deviation
	 * TODO: Speed up using symmetry
	 */
	private void createGaussianKernel()
	{
		mKernelTotal = 0;
		
		mKernelSize = (int) Math.floor(6 * mSigma);
		
		if((mKernelSize & 0x1) == 0x0)
		{
			mKernelSize = mKernelSize - 1;
		}
		
		mKernel = new double[mKernelSize * mKernelSize];
		
		int halfSize = (int) Math.floor((mKernelSize/2));
		
		for(int y = 0; y < mKernelSize; y++)
		{
			int outputOffset = y * mKernelSize;
			
			int yDist = y - halfSize;
			yDist = Math.abs(yDist);
			
			for(int x = 0; x < mKernelSize; x++)
			{
				int xDist = x - halfSize;
				xDist = Math.abs(xDist);
				
				double kernelValue = getDistrbutionValue(xDist, yDist);
				
				mKernel[outputOffset + x] = kernelValue;
				mKernelTotal = mKernelTotal + mKernel[outputOffset + x];
			}
		}
		
		Log.v(Singleton.TAG, "Gaussian Blur kernel size = " + mKernelSize+"");
	}
	
	private double getDistrbutionValue(int xValue, int yValue)
	{
		// TODO speed up by making this a global variable of this function
		double constVar = (1 / (2 * Math.PI * (mSigma * mSigma)));
		
		double topVar = -((xValue * xValue) + (yValue * yValue));
		double bottomConstVar = (2 * (mSigma * mSigma));
		
		return (constVar * Math.exp(topVar / bottomConstVar));
	}
	
	public int[] blurImage(int[] pixels, int width, int height)
	{
		return IPUtility.convolve(pixels, width, height, mKernel, mKernelSize, mKernelTotal);
		
		/**if((pixels.length & 0x1) == 0x0)
		{
			//Log.v(Singleton.TAG, "FFT Start <!--------------- width = " + width + " height = " + height);
			//ComplexNumber[] pixelFreqDomain = computeFreqDomain(pixels);
			//Log.v(Singleton.TAG, "FFT Complete");
			
			int avgPixelValue = 0;
			
			int halfKernel = (int) Math.floor(mKernelSize / 2);
			int[] convolvedPixels = new int[pixels.length];
			
			for(int y = 0; y < height; y++)
			{
				int pixelOffset = y * width;
				for(int x = 0; x < width; x++)
				{
					double accumulator = 0;
					int accumulatorCount = 0;
					for(int i = 0; i < mKernelSize; i++)
					{
						int kernelOffset = i * mKernelSize;
						for(int j = 0; j < mKernelSize; j++)
						{
							int relXPosition = x + (i - halfKernel);
							int relYPosition = y + (j - halfKernel);
							
							if(relXPosition >= 0 && relXPosition < width && 
									relYPosition >= 0 && relYPosition < height)
							{
								int offset = (relYPosition * width) + relXPosition;
								//Log.v(Singleton.TAG, "pixel value =  "+pixels[offset] +" Kernerl Value = " + mKernel[kernelOffset + j]);
								accumulator = accumulator + (pixels[offset] * mKernel[kernelOffset + j]);
								accumulatorCount = accumulatorCount + 1;
							}
						}
					}
					
					convolvedPixels[pixelOffset + x] = (int) (accumulator / mKernelTotal);
					avgPixelValue = avgPixelValue + (int) (accumulator / mKernelTotal);
				}
			}
			
			avgPixelValue = avgPixelValue / (width * height);
			
			Log.v(Singleton.TAG, "Avg. Pixel Value = " + avgPixelValue);
			
			return convolvedPixels;**/
			
			//RGB565 rgbPixelValue = new RGB565(new byte[]{data[0], data[1]});
			
			// TODO: Optimise this to require one loop and and generate byte[] rather than two loops
			//RGB565[][] pixels = new RGB565[height][width];
			
			//for(int i = 0; i < data.length; i = i + 2)
			//{
			//	RGB565 rgbPixelValue = new RGB565(new byte[]{data[i], data[i+1]});
			//	
			//	int col = i % width;
			//	int row = (i - col) / width;
			//	
			//	//Log.v(Singleton.TAG, "Here 0 <-- row = "+row+", col = "+col);
			//	
			//	pixels[row][col] = rgbPixelValue;
			//}
			
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
			
			//byte[] outputData = new byte[(width * height) * 2];
			//
			//for(int i = 0; i < width; i++)
			//{
			//	for(int j = 0; j < height; j++)
			//	{
			//		Log.v(Singleton.TAG, "Here 1 <-- i = "+i+", j = "+j);
			//		byte[] pixelByteValues = pixels[i][j].getBytes(PixelFormat.RGB_565);
			//		Log.v(Singleton.TAG, "Here 2 <-- i = "+i+", j = "+j);
			//		
			//		if(pixelByteValues.length == 2)
			//		{
			//			int col = (i * width) + j;
			//			outputData[col] = pixelByteValues[0];
			//			outputData[col] = pixelByteValues[1];
			//		}
			//	}
			//}
			
			//return outputData;
		/**}
		else
		{
			throw new RuntimeException("Data in GaussianBlur isn't modulo of 2");
		}**/
	}
	
	public int getKernelSize()
	{
		return mKernelSize;
	}
}
