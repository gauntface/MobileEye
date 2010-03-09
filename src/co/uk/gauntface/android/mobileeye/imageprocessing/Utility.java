package co.uk.gauntface.android.mobileeye.imageprocessing;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;

public class Utility
{
	public static Bitmap renderBitmap(int[] pixels, int width, int height, boolean isGreyScale)
	{
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		if(isGreyScale == true)
		{
			int[] image = new int[pixels.length];
			
			int inputOffset = 0;
			for (int y = 0; y < height; y++)
			{
				int outputOffset = y * width;
				
				for (int x = 0; x < width; x++)
				{
					image[outputOffset + x] = 0xff000000 | (pixels[inputOffset + x] * 0x00010101);
				}
				
				inputOffset = inputOffset + width;
			}
			
			bitmap.setPixels(image, 0, width, 0, 0, width, height);
		}
		else
		{
			bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		}
		
	    return bitmap;
	}
	
	public static int multipleTwoPreComp(int n)
	{
		// i < 32 since s^32 is int max size
		int requiredN = 1;
		
		for(int i = 0; i < 32; i++)
		{
			if(n <= requiredN)
			{
				break;
			}
			
			requiredN = requiredN * 2;
		}
		
		if(n > requiredN)
		{
			throw new RuntimeException("Precomputation can't handle input of size - " + n);
		}
		
		return requiredN;
	}
}
