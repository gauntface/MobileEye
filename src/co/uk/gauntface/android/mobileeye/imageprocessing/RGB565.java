package co.uk.gauntface.android.mobileeye.imageprocessing;

import android.util.Log;
import co.uk.gauntface.android.mobileeye.Singleton;

public class RGB565
{
	// The first five bit mask - 11111000 - 248
	private static final int FIRST_FIVE_BIT_MASK = 248;
	// The last three bit mask - 00000111 - 7
	private static final int LAST_THREE_BIT_MASK = 7;
	// the first three bit mask - 11100000 - 224
	private static final int FIRST_THREE_BIT_MASK = 224;
	// the last five bit mask - 00011111 - 31
	private static final int LAST_FIVE_BIT_MASK = 31;
	
	private int mRed;
	private int mGreen;
	private int mBlue;
	
	/**
	 * Takes in 2 bytes of data on the RGB_565 PixelFormat and converts them to RGB_888 value pixel.
	 * @param data
	 */
	public RGB565(byte[] data)
	{
		if(data.length == 2)
		{	
			/**
			 * Preform the bit operations on the values
			 */
			mRed = (data[0] & FIRST_FIVE_BIT_MASK) >> 3;
			mBlue = data[1] & LAST_FIVE_BIT_MASK;
			
			mGreen = ((data[0] & LAST_THREE_BIT_MASK) << 3) | ((data[1] & FIRST_THREE_BIT_MASK) >> 5);
			
			/**
			 * Convert them to 8 bit values (i.e. make them have 0 - 255 values)
			 */
			mRed = (int) ((mRed / 31.0) * 255);
			mBlue = (int) ((mBlue / 31.0) * 255);
			
			mGreen = (int) ((mGreen / 63.0) * 255);
		}
		else
		{
			throw new RuntimeException("Camera setPreviewDisplay failed");
		}
	}
}
