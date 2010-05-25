package co.uk.gauntface.android.mobileeye.imageprocessing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;

import co.uk.gauntface.android.mobileeye.Singleton;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;
import android.util.Log;

public class Utility
{
	private static String mPrependFileName;
	
	public static Bitmap renderBitmap(int[] pixels, int width, int height, boolean isGreyScale, int opacity)
	{
		if(pixels != null)
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
						if(pixels[inputOffset + x] >= 0)
						{
							image[outputOffset + x] = (opacity * 0x01000000) | (pixels[inputOffset + x] * 0x00010101);
						}
						else
						{
							image[outputOffset + x] = 0x00000000;
						}
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
		
		return null;
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
	
	public static boolean saveImageToSDCard(Bitmap b, String appendFileName)
	{
		if(b != null)
		{
			try
			{
				File sdCardFile = Environment.getExternalStorageDirectory();
				if(sdCardFile.canWrite() == true)
				{
					File mobileEyeFile = new File(sdCardFile, "MobileEye");
					mobileEyeFile.mkdirs();
					
					File imageFile = new File(mobileEyeFile, mPrependFileName+appendFileName);
					
					FileOutputStream fileStream = new FileOutputStream(imageFile);
					b.compress(CompressFormat.PNG, 100, fileStream);
					
					fileStream.close();
				}
				else
				{
					Log.e(Singleton.TAG, "Cannot write to SD Card");
				}
				
				return true;
			}
			catch(Exception e)
			{
				Log.e(Singleton.TAG, "Utility Error - " + e);
				e.printStackTrace();
			}
		}
		
		return false;
	}

	public static void setFilePrePend(String fileName)
	{
		Log.v(Singleton.TAG, "Settings the prepended filename to: " + fileName);
		mPrependFileName = fileName;
	}

	public static boolean saveTextToSDCard(String content, String appendFileName)
	{
		try
		{
			File sdCardFile = Environment.getExternalStorageDirectory();
			if(sdCardFile.canWrite() == true)
			{
				File mobileEyeFile = new File(sdCardFile, "MobileEye");
				mobileEyeFile.mkdirs();
				
				File textFile = new File(mobileEyeFile, mPrependFileName+appendFileName);
				
				FileWriter fileWriter = new FileWriter(textFile);
				BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
				
				try
				{
					bufferedWriter.write(content);
				}
				finally
				{
					bufferedWriter.close();
				}
			}
			else
			{
				Log.e(Singleton.TAG, "Cannot write to SD Card");
			}
			
			return true;
		}
		catch(Exception e)
		{
			Log.e(Singleton.TAG, "Utility Error [String empty string, depends on state] - " + e);
			e.printStackTrace();
		}
		
		return false;
	}
}
