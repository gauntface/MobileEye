package co.uk.gauntface.android.mobileeye.imageprocessing;

import java.util.ArrayList;

import android.util.Log;

import co.uk.gauntface.android.mobileeye.Singleton;

public class QuickSegment
{
	private int MAX_PIXEL_VALUE = 255;
	private int HISTOGRAM_SPACING = 10;
	
	private int GROUP_0_COLOR = 0;
	private int GROUP_1_COLOR = 150;
	private int GROUP_2_COLOR = 255;
	
	public QuickSegment()
	{
		
	}
	
	public int[] segmentImage(int[] pixels)
	{
		int[] pixelBucket = new int[MAX_PIXEL_VALUE];
		
		// Bucket Sort the Pixels [Histogram Essentially]
		for(int i = 0; i < pixels.length; i++)
		{
			pixelBucket[pixels[i]]++;
		}
		
		int[] groups = hillClimb(pixelBucket);
		
		return null;
	}
	
	public int[] hillClimb(int[] pixelBucket)
	{
		ArrayList<Integer> maxPoints = new ArrayList<Integer>();
		
		int[] hillStartPoints = new int[]{0, 50, 100, 150, 200, 250};
		
		for(int i = 0; i < hillStartPoints.length; i++)
		{
			int currentPoint = hillStartPoints[i];
			boolean maximumFound = false;
			while(maximumFound == false)
			{
				if(pixelBucket[currentPoint + 1] > pixelBucket[currentPoint])
				{
					currentPoint = currentPoint + 1;
				}
				else if(pixelBucket[currentPoint - 1] > pixelBucket[currentPoint])
				{
					currentPoint = currentPoint - 1;
				}
				else
				{
					maxPoints.add(currentPoint);
					maximumFound = true;
				}
			}
		}
		
		int group1 = -1;
		int group2 = -1;
		int group3 = -1;
		
		for(int i = 0; i < maxPoints.size(); i++)
		{
			int maxPoint = maxPoints.get(i);
			
			if(!((maxPoint < (group1 + HISTOGRAM_SPACING)
					&& maxPoint > (group1 - HISTOGRAM_SPACING))
				|| (maxPoint < (group2 + HISTOGRAM_SPACING)
						&& maxPoint > (group2 - HISTOGRAM_SPACING))
				|| (maxPoint < (group3 + HISTOGRAM_SPACING)
						&& maxPoint > (group3 - HISTOGRAM_SPACING))))
			{
				if(maxPoint > group1)
				{
					group3 = group2;
					group2 = group1;
					group1 = maxPoint;
				}
				else if(maxPoint > group2)
				{
					group3 = group2;
					group2 = maxPoint;
				}
				else if(maxPoint > group3)
				{
					group3 = maxPoint;
				}
			}
			else
			{
				Log.v(Singleton.TAG, "Skipping Peak - " + maxPoint);
				Log.v(Singleton.TAG, "Group1 = " + group1);
				Log.v(Singleton.TAG, "Group2 = " + group2);
				Log.v(Singleton.TAG, "Group3 = " + group3);
			}
		}
		
		Log.v(Singleton.TAG, "Final Group1 = " + group1);
		Log.v(Singleton.TAG, "Final Group2 = " + group2);
		Log.v(Singleton.TAG, "Final Group3 = " + group3);
		
		if(group1 == -1)
		{
			return new int[]{};
		}
		else if(group2 == -1)
		{
			return new int[]{group1};
		}
		else if(group3 == -1)
		{
			return new int[]{group1, group2};
		}
		else
		{
			return new int[]{group1, group2, group3};
		}
	}
}
