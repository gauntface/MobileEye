package co.uk.gauntface.android.mobileeye.imageprocessing;

import java.util.ArrayList;

public class ImagePackage
{
	private int[] mOrigImgPixels;
	private int mImgWidth;
	private int mImgHeight;
	
	private int[] mHistogram;
	
	private ArrayList<Peak> mInitialPixelGroups;
	private Peak[] mFinalPixelGroups;
	private Peak mUsedPixelGroup;
	
	private RegionGroup mRegionGroup;
	private int[] mRegionGroupPixels;
	
	private RegionGroup mExtractionArea;
	private int[] mExtractionAreaPixels;
	private double mAveragePixelValue;
	
	public ImagePackage(int[] origPixels, int imgWidth, int imgHeight)
	{
		mOrigImgPixels = origPixels;
		mImgWidth = imgWidth;
		mImgHeight = imgHeight;
	}
	
	public int[] getOrigImgPixels()
	{
		return mOrigImgPixels;
	}
	
	public void setOrigImgPixels(int[] p)
	{
		mOrigImgPixels = p;
	}
	
	public int[] getHistogram()
	{
		return mHistogram;
	}
	
	public void setHistogram(int[] histogramBuckets)
	{
		mHistogram = histogramBuckets;
	}
	
	public int getImgWidth()
	{
		return mImgWidth;
	}
	
	public void setImgWidth(int w)
	{
		mImgWidth = w;
	}
	
	public int getImgHeight()
	{
		return mImgHeight;
	}
	
	public void setImgHeight(int h)
	{
		mImgHeight = h;
	}
	
	public ArrayList<Peak> getInitPixelGroups()
	{
		return mInitialPixelGroups;
	}
	
	public void setInitPixelGroups(ArrayList<Peak> g)
	{
		mInitialPixelGroups = g;
	}
	
	public Peak[] getFinalPixelGroups()
	{
		return mFinalPixelGroups;
	}
	
	public void setFinalPixelGroups(Peak[] f)
	{
		mFinalPixelGroups = f;
	}
	
	public Peak getUsedPixelGroup()
	{
		return mUsedPixelGroup;
	}
	
	public void setUsedPixelGroup(Peak u)
	{
		mUsedPixelGroup = u;
	}
	
	public RegionGroup getRegionGroup()
	{
		return mRegionGroup;
	}
	
	public void setRegionGroup(RegionGroup rg)
	{
		mRegionGroup = rg;
	}
	
	public int[] getRegionGroupPixels()
	{
		return mRegionGroupPixels;
	}
	
	public void setRegionGroupPixels(int[] regionGroupPixels)
	{
		mRegionGroupPixels = regionGroupPixels;
	}
	
	public RegionGroup getExtractionArea()
	{
		return mExtractionArea;
	}
	
	public void setExtractionArea(RegionGroup ea)
	{
		mExtractionArea = ea;
	}
	
	public int[] getAreaExtractionPixels()
	{
		return mExtractionAreaPixels;
	}
	
	public void setAreaExtractionPixels(int[] e)
	{
		mExtractionAreaPixels = e;
	}
	
	public double getAveragePixelValue()
	{
		return mAveragePixelValue;
	}
	
	public void setAveragePixelValue(double a)
	{
		mAveragePixelValue = a;
	}
}
