package co.uk.gauntface.android.mobileeye.imageprocessing;

import java.util.ArrayList;

import android.util.Log;

import co.uk.gauntface.android.mobileeye.Singleton;

public class QuickSegment
{
	private int MAX_PIXEL_VALUE = 255;
	private int REDUCE_AVG_WINDOW_HALF = 5;
	
	private int HISTOGRAM_SPACING = 10;
	private int BUCKET_MIN_SIZE_THRESHOLD = 50;
	
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
		int[] pixelBucket = new int[(MAX_PIXEL_VALUE+1)];
		
		// Bucket Sort the Pixels [Histogram Essentially]
		for(int i = 0; i < pixels.length; i++)
		{
			pixelBucket[pixels[i]] = pixelBucket[pixels[i]] + 1;
		}
		
		/**
		 * Output the histogram to SD Card if neccessary
		 */
		if(logHistogram == true)
		{
			String histogramOuput = new String();;
			
			for(int i = 0; i < pixelBucket.length; i++)
			{
				histogramOuput = histogramOuput + i +","+pixelBucket[i]+"\n";
			}
			
			Utility.saveTextToSDCard(histogramOuput, "hist.txt");
		}
		
		/**
		 * Average and Reduce the data
		 */
		int[] reducedPixelBucket = new int[(int)Math.floor(pixelBucket.length / REDUCE_AVG_WINDOW_HALF)];
		int counter = 0;
		int maxIndex = 0;
		for(int i = REDUCE_AVG_WINDOW_HALF; i < MAX_PIXEL_VALUE; i = i + REDUCE_AVG_WINDOW_HALF)
		{
			double cumulativeValue = 0;
			for(int j = i-REDUCE_AVG_WINDOW_HALF; j < i+REDUCE_AVG_WINDOW_HALF; j++)
			{
				if(j >= 0 && j < pixelBucket.length)
				{
					cumulativeValue = cumulativeValue + pixelBucket[j];
				}
				else
				{
					throw new RuntimeException("QuickSegment shouldn't be reaching out of bounds of pixel bucket");
				}
			}
			cumulativeValue = cumulativeValue / (REDUCE_AVG_WINDOW_HALF * 2);
			reducedPixelBucket[counter] = (int) cumulativeValue;
			
			if(cumulativeValue > maxIndex)
			{
				maxIndex = counter;
			}
			
			counter = counter + 1;
		}
		
		/**
		 * Output the reduced histogram to SD Card if neccessary
		 */
		if(logHistogram == true)
		{
			String histogramOuput = new String();;
			
			for(int i = 0; i < reducedPixelBucket.length; i++)
			{
				histogramOuput = histogramOuput + i +","+reducedPixelBucket[i]+"\n";
			}
			
			Utility.saveTextToSDCard(histogramOuput, "redhist.txt");
		}
		
		int[] groups = hillClimb(reducedPixelBucket, maxIndex);
		
		/**
		 * Output the peaks / groups to SD Card if neccessary
		 */
		if(logHistogram == true)
		{
			String histogramOuput = new String();;
			
			for(int i = 0; i < groups.length; i++)
			{
				histogramOuput = histogramOuput + i +","+groups[i]+"\n";
			}
			
			Utility.saveTextToSDCard(histogramOuput, "groups.txt");
		}
		
		int[] newPixels = new int[pixels.length];
		
		Pair[] groupValues = new Pair[groups.length];
		for(int i = (groups.length - 1); i > 0; i--)
		{
			// TODO Make this span more of the histogram lowest 1, next one up 2, third one third
			int minGroupValue = (groups[i] - 1) * REDUCE_AVG_WINDOW_HALF;
			int maxGroupValue = ((groups[i] + 1) * REDUCE_AVG_WINDOW_HALF) + (2 * REDUCE_AVG_WINDOW_HALF);
			
			groupValues[i] = new Pair(minGroupValue, maxGroupValue);
		}
		
		for(int i = 0; i < pixels.length; i++)
		{
			for(int j = 0; j < groupValues.length; j++)
			{
				Pair groupValue = groupValues[j];
				//if(pixels[i] >= (groups[j] - GROUPING_VARIATION) && pixels[i] <= (groups[j] + GROUPING_VARIATION))
				//{
				if(pixels[i] >= ((Number)groupValue.getArg1()).intValue() && pixels[i] < ((Number)groupValue.getArg2()).intValue())
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
	
	private int[] hillClimb(int[] data, int maxIndex)
	{
		ArrayList<Integer> maxPoints = new ArrayList<Integer>();
		
		// Make this dynamic
		int[] hillStartPoints = new int[]{0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, maxIndex};
		
		for(int i = 0; i < hillStartPoints.length; i++)
		{
			int currentPoint = hillStartPoints[i];
			boolean maximumFound = false;
			while(maximumFound == false)
			{
				if((currentPoint + 1) < data.length && data[currentPoint + 1] > data[currentPoint])
				{
					currentPoint = currentPoint + 1;
				}
				/**else if((currentPoint + 2) < data.length && data[currentPoint + 2] > data[currentPoint])
				{
					currentPoint = currentPoint + 2;
				}**/
				else if((currentPoint - 1) >= 0 && data[currentPoint - 1] > data[currentPoint])
				{
					currentPoint = currentPoint - 1;
				}
				/**else if((currentPoint - 2) >= 0 && data[currentPoint - 2] > data[currentPoint])
				{
					currentPoint = currentPoint - 2;
				}**/
				else
				{
					// Make sure small values don't get considered as a group
					if(data[currentPoint] > BUCKET_MIN_SIZE_THRESHOLD)
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
			
			if(maxPoint != group1 && maxPoint != group2 && maxPoint != group3)
			{
				if(group1 == -1 || data[maxPoint] > data[group1])
				{
					group3 = group2;
					group2 = group1;
					group1 = maxPoint;
				}
				else if(group2 == -1 || data[maxPoint] > data[group2])
				{
					group3 = group2;
					group2 = maxPoint;
				}
				else if(group3 == -1 || data[maxPoint] > data[group3])
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
