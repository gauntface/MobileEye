package co.uk.gauntface.android.mobileeye.imageprocessing;

import android.graphics.PixelFormat;
import android.util.Log;
import co.uk.gauntface.android.mobileeye.Singleton;

public class RGB565
{
	// The first five bit mask - 11111000 - 248
	private static final int FIRST_FIVE_BIT_MASK = 248;
	// The last three bit mask - 00000111 - 7
	private static final int LAST_THREE_BIT_MASK = 7;
	// The first three bit mask - 11100000 - 224
	private static final int FIRST_THREE_BIT_MASK = 224;
	// The last five bit mask - 00011111 - 31
	private static final int LAST_FIVE_BIT_MASK = 31;
	
	// The first six bit mask - 00111111 - 63
	private static final int FIRST_SIX_BIT_MASK = 63;
	// The last eight bit mask - 1111111100000000 - 65280
	private static final int LAST_EIGHT_BIT_MASK = 65280;
	// The first eight bit mask - 11111111 - 255
	private static final int FIRST_EIGHT_BIT_MASK = 255;
	
	private int mRed;
	private int mGreen;
	private int mBlue;
	
	public RGB565()
	{
		mRed = -1;
		mBlue = -1;
		
		mGreen = -1;
	}
	
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

	public byte[] getBytes(int pixelFormat)
	{
		if(pixelFormat == PixelFormat.RGB_565)
		{
			int red = (int) ((mRed / 255) * 31);
			int blue = (int) ((mBlue / 255) * 31);
			
			int green = (int) ((mGreen / 255) * 31);
			
			// force the values to have 5 or 6 bits respectifully
			red = red & FIRST_FIVE_BIT_MASK;
			blue = blue & FIRST_FIVE_BIT_MASK;
			
			green = green & FIRST_SIX_BIT_MASK;
			
			int bytes = 0;
			
			bytes = bytes & blue;
			bytes = bytes & (green << 5);
			bytes = bytes & (red << 11);
			
			byte byte1 = 0;
			byte byte2 = 0;
			
			byte1 = (byte) ((bytes & LAST_EIGHT_BIT_MASK) >> 8);
			byte2 = (byte) (bytes & FIRST_EIGHT_BIT_MASK);
			
			Log.v(Singleton.TAG, "mRed = "+Integer.toBinaryString(mRed) + " mGreen = "+Integer.toBinaryString(mGreen)+" mBlue = "+Integer.toBinaryString(mBlue));
			Log.v(Singleton.TAG, "byte1 = "+Integer.toBinaryString(byte1)+" byte2 = "+Integer.toBinaryString(byte2));
			
			return new byte[]{byte1, byte2};
		}
		
		return new byte[]{};
	}
}
