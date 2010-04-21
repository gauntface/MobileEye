package co.uk.gauntface.android.mobileeye.imageprocessing;

public class RegionGroup
{
	private int mTopLeftX;
	private int mTopLeftY;
	private int mBottomRightX;
	private int mBottomRightY;
	
	private int mRegionSize;
	
	private double mWeightedCenterX;
	private double mWeightedCenterY;
	
	public RegionGroup()
	{
		mTopLeftX = -1;
		mTopLeftY = -1;
		mBottomRightX = -1;
		mBottomRightY = -1;
		
		mRegionSize = 0;
	}
	
	public RegionGroup(int topLeftX, int topLeftY, int bottomRightX, int bottomRightY)
	{
		mTopLeftX = topLeftX;
		mTopLeftY = topLeftY;
		mBottomRightX = bottomRightX;
		mBottomRightY = bottomRightY;
		
		int regionWidth = bottomRightX - topLeftX;
		int regionHeight = bottomRightY - topLeftY;
		
		mRegionSize = regionWidth * regionHeight;
	}
	
	public void extendRegion(int x, int y)
	{
		if(mTopLeftX == -1 || x < mTopLeftX)
		{
			mTopLeftX = x;
		}
		if(mBottomRightX == -1 || x > mBottomRightX)
		{
			mBottomRightX = x;
		}
		
		if(mTopLeftY == -1 || y < mTopLeftY)
		{
			mTopLeftY = y;
		}
		if(mBottomRightY == -1 || y > mBottomRightY)
		{
			mBottomRightY = y;
		}
		
		mRegionSize = mRegionSize + 1;
		
		mWeightedCenterX = mWeightedCenterX + x;
		mWeightedCenterY = mWeightedCenterY + y;
	}
	
	public int getTopLeftX()
	{
		return mTopLeftX;
	}
	
	public int getTopLeftY()
	{
		return mTopLeftY;
	}
	
	public int getBottomRightX()
	{
		return mBottomRightX;
	}
	
	public int getBottomRightY()
	{
		return mBottomRightY;
	}
	
	public int getRegionSize()
	{
		return mRegionSize;
	}
	
	public Pair getWeightedCenter()
	{
		return new Pair((int)(mWeightedCenterX / mRegionSize), (int)(mWeightedCenterY / mRegionSize));
	}
}
