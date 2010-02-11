package co.uk.gauntface.android.mobileeye.imageprocessing;

public class RGB888Pixel
{
	private int mRed;
	private int mGreen;
	private int mBlue;
	
	public RGB888Pixel(byte red, byte green, byte blue)
	{
		mRed = red;
		mGreen = green;
		mBlue = blue;
	}
	
	public int getGrayscaleValue()
	{
		return (int) ((0.3 * mRed) + (0.59 * mGreen) + (0.11 * mBlue));
	}
}
