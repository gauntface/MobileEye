package co.uk.gauntface.android.mobileeye.imageprocessing;

import java.util.ArrayList;

public class ImagePackage
{
	private int[] mImgPixels;
	private int mImgWidth;
	private int mImgHeight;
	private ArrayList<Pair> mPixelGroups;
	private ArrayList<Pair> mGroupCenters;
	
	public ImagePackage()
	{
		
	}
	
	public ImagePackage(int[] p, int w, int h, ArrayList<Pair> g, ArrayList<Pair> gC)
	{
		mImgPixels = p;
		mImgWidth = w;
		mImgHeight = h;
		mPixelGroups = g;
		mGroupCenters = gC;
	}
	
	public int[] getImgPixels()
	{
		return mImgPixels;
	}
	
	public void setImgPixels(int[] p)
	{
		mImgPixels = p;
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
	
	public ArrayList<Pair> getPixelGroups()
	{
		return mPixelGroups;
	}
	
	public void setPixelGroups(ArrayList<Pair> g)
	{
		mPixelGroups = g;
	}
	
	public ArrayList<Pair> getGroupCenters()
	{
		return mGroupCenters;
	}
	
	public void setGroupCenters(ArrayList<Pair> gC)
	{
		mGroupCenters = gC;
	}
}
