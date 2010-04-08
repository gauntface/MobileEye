package co.uk.gauntface.android.mobileeye.imageprocessing;

import co.uk.gauntface.android.mobileeye.Singleton;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.Log;

public class IPUtility
{
	// Taken from com.android.contacts.AttachImage
	public static Bitmap transformPhoto(Matrix scaler, Bitmap src, int targetWidth, int targetHeight, boolean scaleUp)
	{
		int deltaX = src.getWidth() - targetWidth;
		int deltaY = src.getHeight() - targetHeight;
		
		if(!scaleUp && (deltaX < 0 || deltaY < 0))
		{
			/*
			* In this case the bitmap is smaller, at least in one dimension,
			* than the target.  Transform it by placing as much of the image
			* as possible into the target and leaving the top/bottom or
			* left/right (or both) black.
			*/
			Bitmap b2 = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888);
			Canvas c = new Canvas(b2);

			int deltaXHalf = Math.max(0, deltaX / 2);
			int deltaYHalf = Math.max(0, deltaY / 2);
			Rect srcRect = new Rect(deltaXHalf, deltaYHalf, deltaXHalf + Math.min(targetWidth, src.getWidth()), deltaYHalf + Math.min(targetHeight, src.getHeight()));
			int dstX = (targetWidth  - srcRect.width())  / 2;
			int dstY = (targetHeight - srcRect.height()) / 2;
			
			Rect dst = new Rect(dstX, dstY, targetWidth - dstX, targetHeight - dstY);
			c.drawBitmap(src, srcRect, dst, null);
			
			return b2;
		}
		
		float bitmapWidthF = src.getWidth();
		float bitmapHeightF = src.getHeight();

		float bitmapAspect = bitmapWidthF / bitmapHeightF;
		float viewAspect   = (float) targetWidth / targetHeight;

		if (bitmapAspect > viewAspect)
		{
			float scale = targetHeight / bitmapHeightF;
			if (scale < .9F || scale > 1F)
			{
				scaler.setScale(scale, scale);
			}
			else
			{
				scaler = null;
			}
		}
		else
		{
			float scale = targetWidth / bitmapWidthF;
			if (scale < .9F || scale > 1F)
			{
				scaler.setScale(scale, scale);
			}
			else
			{
				scaler = null;
			}
		}
		
		Bitmap b1;
		if (scaler != null)
		{
			// this is used for minithumb and crop, so we want to filter here.
			b1 = Bitmap.createBitmap(src, 0, 0,
			src.getWidth(), src.getHeight(), scaler, true);
		}
		else
		{
			b1 = src;
		}
		
		int dx1 = Math.max(0, b1.getWidth() - targetWidth);
		int dy1 = Math.max(0, b1.getHeight() - targetHeight);

		Bitmap b2 = Bitmap.createBitmap(b1, dx1 / 2, dy1 / 2, targetWidth, targetHeight);
		
		if (b1 != src)
		{
			b1.recycle();
		}

		return b2;
	}
	
	public static int[] convolve(int[] pixels, int width, int height, double[] kernel, int kernelSize, double kernelTotal)
	{
		if((pixels.length & 0x1) == 0x0)
		{
			//Log.v(Singleton.TAG, "FFT Start <!--------------- width = " + width + " height = " + height);
			//ComplexNumber[] pixelFreqDomain = computeFreqDomain(pixels);
			//Log.v(Singleton.TAG, "FFT Complete");
			
			int avgPixelValue = 0;
			
			int halfKernel = (int) Math.floor(kernelSize / 2);
			int[] convolvedPixels = new int[pixels.length];
			
			for(int y = 0; y < height; y++)
			{
				int pixelOffset = y * width;
				for(int x = 0; x < width; x++)
				{
					double accumulator = 0;
					int accumulatorCount = 0;
					for(int i = 0; i < kernelSize; i++)
					{
						int kernelOffset = i * kernelSize;
						for(int j = 0; j < kernelSize; j++)
						{
							int relXPosition = x + (i - halfKernel);
							int relYPosition = y + (j - halfKernel);
							
							if(relXPosition >= 0 && relXPosition < width && 
									relYPosition >= 0 && relYPosition < height)
							{
								int offset = (relYPosition * width) + relXPosition;
								
								accumulator = accumulator + (pixels[offset] * kernel[kernelOffset + j]);
								accumulatorCount = accumulatorCount + 1;
							}
						}
					}
					
					convolvedPixels[pixelOffset + x] = (int) (accumulator / kernelTotal);
					avgPixelValue = avgPixelValue + (int) (accumulator / kernelTotal);
				}
			}
			
			avgPixelValue = avgPixelValue / (width * height);
			
			return convolvedPixels;
		}
		else
		{
			throw new RuntimeException("Data in GaussianBlur isn't modulo of 2");
		}
	}
	
	public static ComplexNumber[] computeFreqDomain(int[] function)
	{
		int n = Utility.multipleTwoPreComp(function.length);
		ComplexNumber[] complexFunction = new ComplexNumber[n];
		
		for(int i = 0; i < function.length; i++)
		{
			complexFunction[i] = new ComplexNumber(function[i], 0);
		}
		
		// Make the function a power of 2 by adding padding
		for(int i = function.length; i < n; i++)
		{
			complexFunction[i] = new ComplexNumber(0, 0);
		}
		
		return computeFreqDomain(complexFunction);
	}
	
	public static  ComplexNumber[] computeFreqDomain(double[] function)
	{
		int n = Utility.multipleTwoPreComp(function.length);
		
		ComplexNumber[] complexFunction = new ComplexNumber[n];
		
		for(int i = 0; i < function.length; i++)
		{
			complexFunction[i] = new ComplexNumber(function[i], 0);
		}
		
		// Make the function a power of 2 by adding padding
		for(int i = function.length; i < n; i++)
		{
			complexFunction[i] = new ComplexNumber(0, 0);
		}
		
		return computeFreqDomain(complexFunction);
	}
	
	/**
	 * Needs changing to FFT to get time complexity of nlogn instead of n^2
	 * @return
	 */
	public static ComplexNumber[] computeFreqDomain(ComplexNumber[] function)
	{
		int n = function.length;
		
		if(n == 1)
		{
			return function;
		}
		
		ComplexNumber w = new ComplexNumber(1, 0);
		
		ComplexNumber[] a0 = new ComplexNumber[(int) Math.floor(n / 2)];
		ComplexNumber[] a1 = new ComplexNumber[(int) Math.floor(n / 2)];
		
		int counter = 0;
		for(int i = 0; i < n; i = i + 2)
		{
			a0[counter] = function[i];
			a1[counter] = function[i+1];
			
			counter++;
		}
		
		ComplexNumber[] y0 = computeFreqDomain(a0);
		ComplexNumber[] y1 = computeFreqDomain(a1);
		
		ComplexNumber[] yi = new ComplexNumber[n];
		for(int i = 0; i < (n/2); i++)
		{
			double u = (-2 * i * Math.PI) / n;
			
			double real = Math.cos(u);
			double img = Math.sin(u);
			
			ComplexNumber wn = new ComplexNumber(real, img);
			
			yi[i] = y0[i].add(w).mul(y1[i]);
			yi[i+(n/2)] = y0[i].sub(w).mul(y1[i]);
			
			w = w.mul(wn);
		}
		
		return yi;
	}
	
	public static int[][] convert1DArrayTo2DArray(int[] array, int width, int height)
	{
		int[][] pixelMatrix = new int[height][width];
		
		for(int i = 0; i < height; i++)
		{
			int heightOffset = i * width;
			for(int j = 0; j < width; j++)
			{
				pixelMatrix[i][j] = array[heightOffset+j];
			}
		}
		
		return pixelMatrix;
	}

	public static int[] convert2DArrayTo1DArray(int[][] array, int width, int height)
	{
		int[] pixels = new int[width * height];
		
		for(int i = 0; i < height; i++)
		{
			int heightOffset = i * width;
			for(int j = 0; j < width; j++)
			{
				pixels[heightOffset + j] = array[i][j];
			}
		}
		
		return pixels;
	}

	/**int deltaX = src.getWidth() - targetWidth;
	int deltaY = src.getHeight() - targetHeight;
	
	float bitmapWidthF = src.getWidth();
	float bitmapHeightF = src.getHeight();

	float bitmapAspect = bitmapWidthF / bitmapHeightF;
	float viewAspect   = (float) targetWidth / targetHeight;

	if (bitmapAspect > viewAspect)
	{
		float scale = targetHeight / bitmapHeightF;
		if (scale < .9F || scale > 1F)
		{
			scaler.setScale(scale, scale);
		}
		else
		{
			scaler = null;
		}
	}
	else
	{
		float scale = targetWidth / bitmapWidthF;
		if (scale < .9F || scale > 1F)
		{
			scaler.setScale(scale, scale);
		}
		else
		{
			scaler = null;
		}
	}
	
	Bitmap b1;
	if (scaler != null)
	{
		// this is used for minithumb and crop, so we want to filter here.
		b1 = Bitmap.createBitmap(src, 0, 0,
		src.getWidth(), src.getHeight(), scaler, true);
	}
	else
	{
		b1 = src;
	}
	
	int dx1 = Math.max(0, b1.getWidth() - targetWidth);
	int dy1 = Math.max(0, b1.getHeight() - targetHeight);

	Bitmap b2 = Bitmap.createBitmap(b1, dx1 / 2, dy1 / 2, targetWidth, targetHeight);
	
	if (b1 != src)
	{
		b1.recycle();
	}

	return b2;
	**/
	/**
	 * NOTE: The scale down must yield an integer ratio
	 */
	public static YUVPixel shrinkImage(YUVPixel yuvPixel, int scaleDownFactor)
	{
		int origImgWidth = yuvPixel.getImgWidth();
		int origImgHeight = yuvPixel.getImgHeight();
		
		int targetWidth = origImgWidth / scaleDownFactor;
		int targetHeight = origImgHeight / scaleDownFactor;
		
		int xRatio = origImgWidth / targetWidth;
		int yRatio = origImgHeight / targetHeight;
		
		int[] pixels = yuvPixel.getPixels();
		int[] newPixels = new int[targetWidth * targetHeight];
		
		int yNewOffset = 0;
		int yOrigImgIndex = 0;

		for(int y = 0; y < targetHeight; y++)
		{
			int xOrigImgIndex = 0;
			for(int x = 0; x < targetWidth; x++)
			{
				newPixels[yNewOffset + x] = pixels[(yOrigImgIndex * origImgWidth) + xOrigImgIndex];
				
				xOrigImgIndex = xOrigImgIndex + xRatio;
			}
			
			yNewOffset = yNewOffset + targetWidth;
			yOrigImgIndex = yOrigImgIndex + yRatio;
		}
		
		yuvPixel.setPixels(newPixels);
		yuvPixel.setImgWidth(targetWidth);
		yuvPixel.setImgHeight(targetHeight);
		
		return yuvPixel;
		
		/**
		int[] origPixels = yuvPixel.getPixels();
		int[] newPixels = new int[origPixels.length];
		
		int srcWidth = yuvPixel.getImgWidth();
		int srcHeight = yuvPixel.getImgHeight();
		
		int targetWidth =  srcWidth / 2;
		int targetHeight = srcHeight / 2;
		
		int rowOffset = 0;
		int nextRowOffset = rowOffset + srcWidth;
		
		for(int y = 0; y < targetHeight; y++)
		{
			int yOffset = 2 * y;
			int newYOffset = y * targetWidth;
			
			for(int x = 0; x < targetWidth; x++)
			{
				int xOffset = 2 * x;
				int xOffsetPlus1 = xOffset + 1;
				
				int newPixelValue = (int) (
						(origPixels[rowOffset+xOffset]
					+ origPixels[rowOffset + xOffsetPlus1]
					+ origPixels[nextRowOffset + xOffset]
					+ origPixels[nextRowOffset + xOffsetPlus1]) / 4);
				
				newPixels[newYOffset + x] = newPixelValue;
			}
			
			rowOffset = nextRowOffset;
			nextRowOffset = rowOffset + srcWidth;
		}
		
		yuvPixel.setPixels(newPixels);
		yuvPixel.setImgWidth(targetWidth);
		yuvPixel.setImgHeight(targetHeight);
		
		return yuvPixel;**/
	}
}
