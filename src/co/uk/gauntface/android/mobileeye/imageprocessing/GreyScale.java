package co.uk.gauntface.android.mobileeye.imageprocessing;

public class GreyScale
{
	public static YCbCr420Pixel[][] applyGreyScale(YCbCr420Pixel[][] pixels, int width, int height)
	{
		YCbCr420Pixel[][] newPixels = new YCbCr420Pixel[height][width];
		
		for(int i = 0; i < height; i++)
		{
			for(int j = 0; j < width; j++)
			{
				newPixels[i][j] = getGreyScalePixel(pixels[i][j]);
			}
		}
		
		return newPixels;
	}

	private static YCbCr420Pixel getGreyScalePixel(YCbCr420Pixel p)
	{
		return new YCbCr420Pixel((int)(0.3 * p.getRedValue()), (int)(0.59 * p.getGreenValue()), (int)(0.11 * p.getBlueValue()), YCbCr420Pixel.RGB_FORMAT);
	}
}
