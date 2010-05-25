package co.uk.gauntface.android.mobileeye.imageprocessing;

import java.util.ArrayList;

public class QuickSegment
{
	private double mAveragePixelValue;
	
	private int MAX_PIXEL_VALUE = 255;
	private int BUCKET_RANGE = 4;
	
	private int AVG_WINDOW_SIZE = 1;
	private int AVG_LOOPS = 2;
	
	private boolean SHOULD_MERGE_GROUPS = true;
	// MERGE_MAX_GAP is the number of buckets (256 / 4 = 64) divided by 4 (64 / 4 = 16)
	private int MERGE_MIN_BRIGHTNESS = 4;
	
	private int HISTOGRAM_SPACING = 10;
	private int BUCKET_MIN_SIZE_THRESHOLD = 300;
	
	private int GROUPING_VARIATION = 40;
	
	private int GROUP_NO_COLOR = -1;
	private int GROUP_0_COLOR = 255;
	private int GROUP_1_COLOR = 120;
	private int GROUP_2_COLOR = 60;
	
	private boolean mLog;
	private int mWeightInFavour;
	
	private ImagePackage mImgPkg;
	
	public QuickSegment()
	{
		
	}
	
	public ImagePackage segmentImage(int[] origPixels, boolean log, int imgWidth, int imgHeight, int weightInFavour)
	{
		mLog = log;
		mWeightInFavour = weightInFavour;
		
		mImgPkg = new ImagePackage(origPixels, imgWidth, imgHeight);
		
		int[] pixelBuckets = createHistogram(origPixels);
		
		int[] avgPixelBuckets = averageArray(pixelBuckets);
		mImgPkg.setHistogram(avgPixelBuckets);
		
		Peak[] maxIndices = hillClimb(avgPixelBuckets);
		mImgPkg.setFinalPixelGroups(maxIndices);
		
		ArrayList<Peak> groupValues = groupAndMerge(avgPixelBuckets, maxIndices);
		
		ImagePackage imgPkg = generateImagePkg(origPixels, avgPixelBuckets, groupValues, imgWidth, imgHeight, mAveragePixelValue);
		
		return imgPkg;
	}
	
	private int[] createHistogram(int[] pixels)
	{
		/**
		 * MAX_PIXEL_VALUE since 0 is a pixel value
		 * i.e. 0 - 255 is 256 values
		 */
		int[] pixelBuckets = new int[(int) Math.floor((MAX_PIXEL_VALUE + 1) / BUCKET_RANGE)];

		for(int i = 0; i < pixels.length; i++)
		{
			int pixelValue = pixels[i];
			int remainder = pixelValue % BUCKET_RANGE;
			int bucketIndex = (pixelValue - remainder) / BUCKET_RANGE;
			
			pixelBuckets[bucketIndex] = pixelBuckets[bucketIndex] + 1;
		}
		
		return pixelBuckets;
	}
	
	private int[] averageArray(int[] array)
	{
		int[] newArray = new int[array.length];
		
		for(int i = 0; i < AVG_LOOPS; i++)
		{
			int[] tempArray = new int[array.length];
			for(int j = 0; j < array.length; j++)
			{
				double averagedValue = 0;
				int startWindowIndex = j - AVG_WINDOW_SIZE;
				int endWindowIndex = j + AVG_WINDOW_SIZE;
				
				for(int k = startWindowIndex; k <= endWindowIndex; k++)
				{
					if(k >= 0 && k < array.length)
					{
						averagedValue = averagedValue + array[k];
					}
				}
				
				averagedValue = averagedValue / ((2*AVG_WINDOW_SIZE) + 1);
				tempArray[j] = (int) averagedValue;
			}
			newArray = tempArray;
		}
		
		return newArray;
	}
	
	private Peak[] hillClimb(int[] data)
	{
		ArrayList<Peak> maxPoints = findHillPeaks(data);
		mImgPkg.setInitPixelGroups(maxPoints);
		
		Peak[] maxIndices = extractTopPeaks(data, maxPoints);
		
		return maxIndices;
	}
	
	private ArrayList<Peak> findHillPeaks(int[] data)
	{
		int totalNoPixels = mImgPkg.getImgWidth() * mImgPkg.getImgHeight();
		int minHistSize = (totalNoPixels / ((MAX_PIXEL_VALUE + 1)/BUCKET_RANGE)) / 3;
		
		ArrayList<Peak> maxPoints = new ArrayList<Peak>();
		
		int minGroupIndex = 0;
		int maxGroupIndex = 0;
		int currentPeakIndex = 0;
		int peakSize = data[0];
		boolean foundPeak = false;
		
		for(int i = 1; i < data.length; i++)
		{
			if(foundPeak == false)
			{
				// Change the 200 to a percentage of the pixels in the image
				if(data[minGroupIndex] < minHistSize)
				{
					minGroupIndex = i;
					currentPeakIndex = i;
					peakSize = data[i];
				}
				else
				{
					if(data[i] > data[currentPeakIndex])
					{
						currentPeakIndex = i;
						peakSize = peakSize + data[i];
					}
					else
					{	
						foundPeak = true;
						maxGroupIndex = i;
						peakSize = peakSize + data[i];
					}
				}
				
				if((i + 1) == data.length)
				{
					maxGroupIndex = i;
					maxPoints.add(new Peak(minGroupIndex, maxGroupIndex, currentPeakIndex, peakSize));
				}
			}
			else
			{
				// Change the 200 to a percentage of the pixels in the image
				if(data[i] < data[maxGroupIndex] && data[i] > minHistSize)
				{
					maxGroupIndex = i;
					peakSize = peakSize + data[i];
				}
				else
				{
					maxPoints.add(new Peak(minGroupIndex, maxGroupIndex, currentPeakIndex, peakSize));
					
					foundPeak = false;
					
					minGroupIndex = i;
					currentPeakIndex = i;
					peakSize = data[minGroupIndex];
				}
				
				if((i + 1) == data.length && data[i] > minHistSize)
				{
					maxPoints.add(new Peak(minGroupIndex, maxGroupIndex, currentPeakIndex, peakSize));
				}
			}
		}
		
		return maxPoints;
	}
	
	private Peak[] extractTopPeaks(int[] data, ArrayList<Peak> maxPoints)
	{
		Peak group1 = new Peak();
		Peak group2 = new Peak();
		Peak group3 = new Peak();
		
		for(int i = 0; i < maxPoints.size(); i++)
		{
			int peakIndex = maxPoints.get(i).getPeakIndex();
			
			if(peakIndex != group1.getPeakIndex()
					&& peakIndex != group2.getPeakIndex()
					&& peakIndex != group3.getPeakIndex())
			{
				if(group1.getPeakIndex() == -1 || data[peakIndex] > data[group1.getPeakIndex()])
				{
					group3 = group2;
					group2 = group1;
					group1 = maxPoints.get(i);
				}
				else if(group2.getPeakIndex() == -1 || data[peakIndex] > data[group2.getPeakIndex()])
				{
					group3 = group2;
					group2 = maxPoints.get(i);
				}
				else if(group3.getPeakIndex() == -1 || data[peakIndex] > data[group3.getPeakIndex()])
				{
					group3 = maxPoints.get(i);
				}
			}
		}
		
		if(group1.getPeakIndex() == -1)
		{
			return new Peak[]{};
		}
		else if(group2.getPeakIndex() == -1)
		{
			return new Peak[]{group1};
		}
		else if(group3.getPeakIndex() == -1)
		{
			if(group1.getPeakIndex() > group2.getPeakIndex())
			{
				return new Peak[]{group1, group2};
			}
			else
			{
				return new Peak[]{group2, group1};
			}
		}
		else
		{
			if(group1.getPeakIndex() > group2.getPeakIndex())
			{
				if(group2.getPeakIndex() > group3.getPeakIndex())
				{
					return new Peak[]{group1, group2, group3};
				}
				else if(group1.getPeakIndex() > group3.getPeakIndex())
				{
					return new Peak[]{group1, group3, group2};
				}
				else
				{
					return new Peak[]{group3, group1, group2}; 
				}
			}
			else
			{
				if(group1.getPeakIndex() > group3.getPeakIndex())
				{
					return new Peak[]{group2, group1, group3};
				}
				else if(group2.getPeakIndex() > group3.getPeakIndex())
				{
					return new Peak[]{group2, group3, group1};
				}
				else
				{
					return new Peak[]{group3, group2, group1};
				}
			}
		}
	}
	
	public ArrayList<Peak> groupAndMerge(int[] data, Peak[] maxIndices)
	{
		ArrayList<Peak> groupValues = new ArrayList<Peak>();
		int prevPeak = 0;
		for(int i = 0; i < maxIndices.length; i++)
		{
			int minGroupValue = maxIndices[i].getMinIndex() * BUCKET_RANGE;
			int maxGroupValue = (maxIndices[i].getMaxIndex() * BUCKET_RANGE) + BUCKET_RANGE;
			int peakGroupValue = maxIndices[i].getPeakIndex();
			int peakSize = maxIndices[i].getPeakSize();
			
			if(SHOULD_MERGE_GROUPS == true && groupValues.size() > 0)
			{
				Peak p = groupValues.get(groupValues.size() - 1);
				
				double currentPeakWeight = getPeakWeight(peakGroupValue, peakSize);
				double higherPeakWeight =  getPeakWeight(p.getPeakIndex(), p.getPeakSize());
				
				if((currentPeakWeight / higherPeakWeight) > 0.2)
				{
					if(maxGroupValue >= p.getMinIndex() && minGroupValue < p.getMinIndex())
					{
						if(maxGroupValue < p.getMaxIndex())
						{
							maxGroupValue = p.getMaxIndex();
							
							prevPeak = peakGroupValue;
							
							peakSize = peakSize + p.getPeakSize();
							
							groupValues.remove(groupValues.size() - 1);
						}
					}
				}
			}
			else
			{
				prevPeak = peakGroupValue;
			}
			
			Peak p = new Peak(minGroupValue, maxGroupValue, peakGroupValue, peakSize);
			groupValues.add(p);
		}
		
		return groupValues;
	}
	
	private ImagePackage generateImagePkg(int[] origPixels, int[] avgPixelBuckets, ArrayList<Peak> groupValues, int imgWidth, int imgHeight, double averagePixel)
	{	
		int groupIndex = getMainGroupToExtract(avgPixelBuckets, groupValues);
		mImgPkg.setUsedPixelGroup(groupValues.get(groupIndex));
		
		int[] newPixels = new int[origPixels.length];
		
		int pixelRangeMin = groupValues.get(groupIndex).getMinIndex();
		int pixelRangeMax = groupValues.get(groupIndex).getMaxIndex();
		
		int nextAvailGroupIndex = 1;
		
		int currentYOffset = 0;
		int neighbourAboveOffset = currentYOffset-(imgWidth);
		
		ArrayList<RegionGroup> regions = new ArrayList<RegionGroup>();
		int[] regionGrouping = new int[origPixels.length];
		
		int maxRegionGroup = -1;
		RegionGroup rG = new RegionGroup();
		for(int y = 0; y < imgHeight; y++)
		{
			for(int x = 0; x < imgWidth; x++)
			{
				int offsetTotal = currentYOffset + x;
				int pixelValue = origPixels[offsetTotal];
				if(pixelValue >= pixelRangeMin && pixelValue <= pixelRangeMax)
				{
					rG.extendRegion(x, y);
					regionGrouping[currentYOffset + x] = nextAvailGroupIndex;
					newPixels[currentYOffset + x] = GROUP_0_COLOR;
				}
				else
				{
					regionGrouping[currentYOffset + x] = -2;
					newPixels[currentYOffset + x] = GROUP_NO_COLOR;
				}
			}
			
			currentYOffset = currentYOffset + imgWidth;
			neighbourAboveOffset = neighbourAboveOffset + imgWidth;
		}
		
		if(rG.getRegionSize() > 0)
		{
			mImgPkg.setRegionGroup(rG);
			mImgPkg.setRegionGroupPixels(regionGrouping);
			
			return mImgPkg;
		}
		
		return null;
	}

	private int getMainGroupToExtract(int[] avgPixelBuckets, ArrayList<Peak> groupValues)
	{
		double maxWeight = -1;
		int maxIndex = -1;
		
		for(int i = 0; i < groupValues.size(); i++)
		{
			int peakIndex = groupValues.get(i).getPeakIndex();
			double weight = getPeakWeight(peakIndex, groupValues.get(i).getPeakSize());
			
			if(weight > maxWeight)
			{
				maxWeight = weight;
				maxIndex = i;
			}
		}
		
		return maxIndex;
	}
	
	private double getPeakWeight(int peakIndex, int peakSize)
	{
		double prefValue = (peakIndex * BUCKET_RANGE) - (MAX_PIXEL_VALUE - mWeightInFavour);
		if(prefValue < 0)
		{
			prefValue = -prefValue;
		}
		
		double factor = prefValue / (double) MAX_PIXEL_VALUE;
		double weight = factor * peakSize;
		
		return weight;
	}
	
	private int checkForNeighbouringGroup(int[] regionGrouping,
			int currentYOffset,
			int neighbourAboveOffset,
			int x)
	{
		int aboveGroup = -1;
		int leftGroup = -1;
		
		int neighbourAboveIndex = neighbourAboveOffset + x; 
		if(neighbourAboveIndex >= 0)
		{
			int temp = regionGrouping[neighbourAboveIndex];
			if(temp >= 0)
			{
				aboveGroup = temp;
			}
		}
		
		int xPos = x - 1;
		if(xPos >= 0)
		{
			int neighbourLeftIndex = currentYOffset + xPos;
			int temp = regionGrouping[neighbourLeftIndex];
			if(temp >= 0)
			{
				leftGroup = temp;
			}
		}
		
		return -1;
	}
}
