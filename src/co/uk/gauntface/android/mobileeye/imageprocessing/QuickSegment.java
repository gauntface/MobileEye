package co.uk.gauntface.android.mobileeye.imageprocessing;

import java.util.ArrayList;

import android.util.Log;

import co.uk.gauntface.android.mobileeye.Singleton;

public class QuickSegment
{
	private int MAX_PIXEL_VALUE = 256;
	private int HISTOGRAM_SPACING = 10;
	private int BUCKET_THRESHOLD = 10;
	
	private int GROUPING_VARIATION = 40;
	
	private int GROUP_NO_COLOR = -1;
	private int GROUP_0_COLOR = 255;
	private int GROUP_1_COLOR = 120;
	private int GROUP_2_COLOR = 60;
	
	public QuickSegment()
	{
		
	}
	
	public int[] segmentImage(int[] pixels, boolean logHistogram)
	{
		int[] pixelBucket = new int[MAX_PIXEL_VALUE];
		
		// Bucket Sort the Pixels [Histogram Essentially]
		int maxIndex = 0;
		
		for(int i = 0; i < pixels.length; i++)
		{
			pixelBucket[pixels[i]] = pixelBucket[pixels[i]] + 1;
			
			if(pixelBucket[pixels[i]] > pixelBucket[maxIndex])
			{
				maxIndex = pixels[i];
			}
		}
		
		if(logHistogram == true)
		{
			String histogramOuput = new String();;
			
			for(int i = 0; i < pixelBucket.length; i++)
			{
				histogramOuput = histogramOuput + i +","+pixelBucket[i]+"\n";
				//Log.v(Singleton.TAG, "HistogramLog: " + i + " - " + pixelBucket[i]);
			}
			
			Utility.saveTextToSDCard(histogramOuput, "hist.txt");
		}
		
		int[] groups = hillClimb(pixelBucket, maxIndex);
		
		int[] newPixels = new int[pixels.length];
		
		for(int i = 0; i < pixels.length; i++)
		{
			for(int j = 0; j < groups.length; j++)
			{
				if(pixels[i] >= (groups[j] - GROUPING_VARIATION) && pixels[i] <= (groups[j] + GROUPING_VARIATION))
				{
					if(j == 0)
					{
						newPixels[i] = GROUP_0_COLOR;
						break;
					}
					else if(j == 1)
					{
						newPixels[i] = GROUP_1_COLOR;
						break;
					}
					else if(j == 2)
					{
						newPixels[i] = GROUP_2_COLOR;
						break;
					}
				}
				else if((j+1) == groups.length)
				{
					newPixels[i] = GROUP_NO_COLOR;
					break;
				}
			}
		}
		
		return newPixels;
	}
	
	private int[] hillClimb(int[] pixelBucket, int maxIndex)
	{
		ArrayList<Integer> maxPoints = new ArrayList<Integer>();
		
		int[] hillStartPoints = new int[]{0, 50, 100, 150, 200, 250, maxIndex};
		
		for(int i = 0; i < hillStartPoints.length; i++)
		{
			int currentPoint = hillStartPoints[i];
			boolean maximumFound = false;
			while(maximumFound == false)
			{
				if((currentPoint + 1) < MAX_PIXEL_VALUE && pixelBucket[currentPoint + 1] > pixelBucket[currentPoint])
				{
					currentPoint = currentPoint + 1;
				}
				else if((currentPoint - 1) >= 0 && pixelBucket[currentPoint - 1] > pixelBucket[currentPoint])
				{
					currentPoint = currentPoint - 1;
				}
				else
				{
					if(pixelBucket[currentPoint] > BUCKET_THRESHOLD)
					{
						maxPoints.add(currentPoint);
					}
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
		}
		
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
