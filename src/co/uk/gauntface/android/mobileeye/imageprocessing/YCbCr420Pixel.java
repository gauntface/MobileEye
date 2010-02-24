package co.uk.gauntface.android.mobileeye.imageprocessing;

import java.util.ArrayList;

import co.uk.gauntface.android.mobileeye.Singleton;

import android.util.Log;

public class YCbCr420Pixel
{
	private static final double W_R = 0.299;
	private static final double W_B = 0.114;
	// 1 - W_R - W_B
	private static final double W_G = 0.587;
	
	private static final double U_MAX = 0.436;
	private static final double V_MAX = 0.615;
	
	// (SEE WIKI PAGE) (1 - W_R) / V_MAX
	private static final double RED_VALUE_CONSTANT = 1.139837398;
	// (SEE WIKI PAGE) (W_B * (1 - W_B)) / (U_MAX * W_G)
	private static final double GREEN_VALUE_CONSTANT_1 = 0.3946517044; 
	// (SEE WIKI PAGE) (W_R * (1 - W_R)) / (V_MAX * W_G)
	private static final double GREEN_VALUE_CONSTANT_2 = 0.5805986067;
	// (SEE WIKI PAGE) (1 - W_B) / U_MAX
	private static final double BLUE_VALUE_CONSTANT = 2.032110092;
	
	// (SEE WIKI PAGE) (1 -W_B)
	private static final double U_VALUE_CONSTANT = 0.886;
	// (SEE WIKI PAGE) (1 - W_R)
	private static final double V_VALUE_CONSTANT = 0.701;
	
	public static final int RGB_FORMAT = 0;
	public static final int YUV_FORMAT = 1;
	
	private int mY;
	private int mU;
	private int mV;
	
	private int mRed;
	private int mGreen;
	private int mBlue;
	
	public YCbCr420Pixel(int y, int u, int v)
	{
		mY = y;
		mU = u;
		mV = v;
		
		mRed = (int) calcRedValue();
		mGreen = (int) calcGreenValue();
		mBlue = (int) calcBlueValue();
	}
	
	public YCbCr420Pixel(int y, int u, int v, int red, int green, int blue)
	{
		mY = y;
		mU = u;
		mV = v;
		
		mRed = red;
		mGreen = green;
		mBlue = blue;
	}
	
	public YCbCr420Pixel(int x, int y, int z, int format)
	{
		if(format == YUV_FORMAT)
		{
			mY = x;
			mU = y;
			mV = z;
			
			mRed = (int) calcRedValue();
			mGreen = (int) calcGreenValue();
			mBlue = (int) calcBlueValue();
		}
		else
		{
			mRed = x;
			mGreen = y;
			mBlue = z;
			
			mY = (int) calcYValue();
			mU = (int) calcUValue();
			mV = (int) calcVValue();
		}
	}
	
	public YCbCr420Pixel()
	{
		mY = mU = mV = 0;
		
		mRed = mGreen = mBlue = 0;
	}
	
	private double calcRedValue()
	{
		double red = mY + (mV * RED_VALUE_CONSTANT);
		
		if(red < 0)
		{
			red = 0;
		}
		else if(red > 255)
		{
			red = 255;
		}
		
		return red;
	}
	
	private double calcGreenValue()
	{
		double green = mY - (mU * GREEN_VALUE_CONSTANT_1) - (mV * GREEN_VALUE_CONSTANT_2); 
		
		if(green < 0)
		{
			green = 0;
		}
		else if(green > 255)
		{
			green = 255;
		}
		
		return green;
	}
	
	private double calcBlueValue()
	{
		double blue = mY + (mU * BLUE_VALUE_CONSTANT);
		
		if(blue < 0)
		{
			blue = 0;
		}
		else if(blue  > 255)
		{
			blue = 255;
		}
		
		return blue;
	}
	
	private double calcYValue()
	{
		return (W_R*mRed) + (W_G*mGreen) + (W_B*mBlue);
	}
	
	private double calcUValue()
	{
		return U_MAX * ((mBlue - mY) / U_VALUE_CONSTANT);
	}
	
	private double calcVValue()
	{
		return V_MAX * ((mRed - mY) / V_VALUE_CONSTANT);
	}
	
	public int getYValue()
	{
		return mY;
	}
	
	public void setYValue(int y)
	{
		mY = y;
	}
	
	public int getUValue()
	{
		return mU;
	}
	
	public void setUValue(int u)
	{
		mU = u;
	}
	
	public int getVValue()
	{
		return mV;
	}
	
	public void setVValue(int v)
	{
		mV = v;
	}
	
	public int getRedValue()
	{
		return mRed;
	}
	
	public void setRedValue(int red)
	{
		mRed = red;
	}
	
	public int getGreenValue()
	{
		return mGreen;
	}
	
	public void setGreenValue(int green)
	{
		mGreen = green;
	}
	
	public int getBlueValue()
	{
		return mBlue;
	}
	
	public void setBlueValue(int blue)
	{
		mBlue = blue;
	}
	
	/**
	 * Helper function to convert the data to an easier image format
	 * when applying image processing
	 * @param data
	 * @param width
	 * @param height
	 * @return
	 */
	public static YCbCr420Pixel[] convert(byte[] data, int width, int height)
	{
		int size = width * height;
		YCbCr420Pixel[] pixels = new YCbCr420Pixel[size];
		
		int i, j;
        int Y, Cr = 0, Cb = 0;
        
        for(j = 0; j < height; j++)
        {
        	int pixPtr = j * width;
            final int jDiv2 = j >> 1;
            for(i = 0; i < width; i++)
            {
            	Y = data[pixPtr];
            	if(Y < 0)
            	{
            		Y += 255;
            	}
            	
                if((i & 0x1) != 1)
                {
                	final int cOff = size + jDiv2 * width + (i >> 1) * 2;
                    Cb = data[cOff];
                    if(Cb < 0) Cb += 127; else Cb -= 128;
                    Cr = data[cOff + 1];
                    if(Cr < 0) Cr += 127; else Cr -= 128;
                }
                
                int R = Y + Cr + (Cr >> 2) + (Cr >> 3) + (Cr >> 5);
                if(R < 0) R = 0; else if(R > 255) R = 255;
                int G = Y - (Cb >> 2) + (Cb >> 4) + (Cb >> 5) - (Cr >> 1) + (Cr >> 3) + (Cr >> 4) + (Cr >> 5);
                if(G < 0) G = 0; else if(G > 255) G = 255;
                int B = Y + Cb + (Cb >> 1) + (Cb >> 2) + (Cb >> 6);
                if(B < 0) B = 0; else if(B > 255) B = 255;
                //out[pixPtr++] = 0xff000000 + (B << 16) + (G << 8) + R;
                pixels[pixPtr++] = new YCbCr420Pixel(Y, Cb, Cr, R, G, B);
            }
        }
		
		
		/**int noOfYValues = width * height;
		
		for(int row = 0; row < height; row++)
		{
			for(int col = 0; col < width; col++)
			{
				int yValue = data[(row * width) + col];
				
				if(yValue < 0)
				{
					yValue  = yValue + 255;
				}
				
				// >> 2 is equivalent to / 4
				int uValue = data[noOfYValues + ((row >> 1) * (width >> 1)) + (col >> 1)];
				
				if(uValue < 0)
				{
					uValue = uValue + 127;
				}
				else
				{
					uValue = uValue - 128;
				}
				
				int vValue = data[noOfYValues + ((row >> 1) * (width >> 1)) + (col >> 1) + 1];
				
				if(vValue < 0)
				{
					vValue = vValue + 127;
				}
				else
				{
					vValue = vValue - 128;
				}
				
				pixels[row][col] = new YCbCr420Pixel(yValue, uValue, vValue);
			}
		}
		
		String dataString = new String();
		dataString = "(" + (int)(50 * 50) + ","+(int)(noOfYValues + ((50 >> 1) * (width >> 1)) + (50 >> 1)) + "," + (int)(noOfYValues + ((50 >> 1) * (width >> 1)) + (50 >> 1) + 1)+")";
		
		Log.v(Singleton.TAG, "YUV = ("+pixels[50][50].getYValue()+","+pixels[50][50].getUValue()+","+pixels[50][50].getVValue()+")");
		Log.v(Singleton.TAG, "RGB = ("+pixels[50][50].getRedValue()+","+pixels[50][50].getGreenValue()+","+pixels[50][50].getBlueValue()+")");
		**/
		/**for(int i = 0; i < noOfYValues; i++)
		{
			int col = i % width;
			int row = (i - col) / width;
			
			int yValue = data[(row * width) + col];
			// >> 2 is equivalent to / 4
			int uValue = data[((row * width) >> 2) + noOfYValues];
			int vValue = data[((row * width) >> 2) + noOfYValues + noOfUValues];
			
			pixels[row][col] = new YCbCr420Pixel(yValue, uValue, vValue);
		}**/
		
		return pixels;
	}
	
	/**
	 * TODO Optimise so that these computations are pre-computed
	 * (i.e. 1-W_R only needs to be calculated once)
	 * @param y
	 * @param u
	 * @param v
	 * @return
	 */
	private static RGB888Pixel YUVToRGB888(int y, int u, int v)
	{
		int red = 0;
		int green = 0;
		int blue = 0;
		
		red = (int) (y + (v*((1-W_R)) / V_MAX));
		green = (int) (y - (u * ((W_B*(1 - W_B))/(U_MAX*W_G))) - ((W_R*(1 - W_R))/(V_MAX*W_G)));
		blue = (int) (y + (u * ((1 - W_B) / U_MAX)));
		
		RGB888Pixel pixel = new RGB888Pixel(red, green, blue);
		
		return pixel;
	}
	
	public static byte[] convertToByteArray(YCbCr420Pixel[][] pixels, int width, int height)
	{
		int noOfYValues = width * height;
		int noOfUValues = noOfYValues / 4;
		
		byte[] data = new byte[noOfYValues + (2 * noOfUValues)];
		
		int i = 0;
		for(int row = 0; row < height; row++)
		{
			for(int col = 0; col < width; col++)
			{
				data[i] = (byte) pixels[row][col].getYValue();
				data[((row * width) >> 2) + noOfYValues] = (byte) pixels[row][col].getUValue();
				data[((row * width) >> 2) + noOfYValues + noOfUValues] = (byte) pixels[row][col].getVValue();
				
				i = i + 1;
			}
		}
		
		/**for(int i = 0; i < noOfYValues; i++)
		{
			int col = i % width;
			int row = (i - col) / width;
			
			data[i] = (byte) pixels[row][col].getYValue();
			data[((row * width) >> 2) + noOfYValues] = (byte) pixels[row][col].getUValue();
			data[((row * width) >> 2) + noOfYValues + noOfUValues] = (byte) pixels[row][col].getVValue();
		}**/
		
		return data;
	}
	
	private static YCbCr420Pixel RGB888ToYUV(int r, int g, int b)
	{
		int y = 0;
		int u = 0;
		int v = 0;
		
		y = (int) ((W_R * r) + (W_G * g) + (W_B * b));
		u = (int) (U_MAX * ((b - y)/(1 - W_B)));
		v = (int) (V_MAX * ((r - y)/(1 - W_R)));
		
		YCbCr420Pixel data = new YCbCr420Pixel(y, u, v);
		
		return data;
	}
}
