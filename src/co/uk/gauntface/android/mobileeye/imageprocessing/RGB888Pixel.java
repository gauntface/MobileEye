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
	
	public RGB888Pixel(int red, int green, int blue)
	{
		mRed = red;
		mGreen = green;
		mBlue = blue;
	}

	public int getRed()
	{
		return mRed;
	}
	
	public int getGreen()
	{
		return mGreen;
	}
	
	public int getBlue()
	{
		return mBlue;
	}
}
