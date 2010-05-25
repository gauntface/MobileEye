package co.uk.gauntface.android.mobileeye.imageprocessing;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;

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
	}
}
