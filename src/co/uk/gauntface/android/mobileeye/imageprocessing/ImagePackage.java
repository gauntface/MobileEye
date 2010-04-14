package co.uk.gauntface.android.mobileeye.imageprocessing;

import java.util.ArrayList;

public class ImagePackage
{
	private int[] mImgPixels;
	private int[] mHistogram;
	private int mImgWidth;
	private int mImgHeight;
	private ArrayList<Peak> mPixelGroups;
	private RegionGroup mRegionGroups;
	private int[] mRegionGroupPixels;
	private RegionGroup mExtractionArea;
	private double mAveragePixelValue;
	
	public ImagePackage()
	{
		
	}
	
	public ImagePackage(int[] p, int[] a, int w, int h, ArrayList<Peak> g, RegionGroup r, int[] rG, double aPV)
	{
		mImgPixels = p;
		mHistogram = a;
		mImgWidth = w;
		mImgHeight = h;
		mPixelGroups = g;
		mRegionGroups = r;
		mRegionGroupPixels = rG;
		mExtractionArea = null;
		mAveragePixelValue = aPV;
	}
	
	public int[] getImgPixels()
	{
		return mImgPixels;
	}
	
	public void setImgPixels(int[] p)
	{
		mImgPixels = p;
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
	
	public ArrayList<Peak> getPixelGroups()
	{
		return mPixelGroups;
	}
	
	public void setPixelGroups(ArrayList<Peak> g)
	{
		mPixelGroups = g;
	}
	
	public RegionGroup getRegionGroup()
	{
		return mRegionGroups;
	}
	
	public void setRegionGroup(RegionGroup rg)
	{
		mRegionGroups = rg;
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
	
	public double getAveragePixelValue()
	{
		return mAveragePixelValue;
	}
	
	public void setAveragePixelValue(double a)
	{
		mAveragePixelValue = a;
	}
	
	public boolean stableArea()
	{
		return false;
	}
}
