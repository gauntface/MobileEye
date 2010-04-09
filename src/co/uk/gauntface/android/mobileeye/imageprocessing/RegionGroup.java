package co.uk.gauntface.android.mobileeye.imageprocessing;

public class RegionGroup
{
	private int mTopLeftX;
	private int mTopLeftY;
	private int mBottomRightX;
	private int mBottomRightY;
	
	private int mRegionSize;
	
	public RegionGroup(int topLeftX, int topLeftY, int bottomRightX, int bottomRightY)
	{
		mTopLeftX = topLeftX;
		mTopLeftY = topLeftY;
		mBottomRightX = bottomRightX;
		mBottomRightY = bottomRightY;
		
		mRegionSize = 1;
	}
	
	public void extendRegion(int x, int y)
	{
		if(x < mTopLeftX)
		{
			mTopLeftX = x;
		}
		if(x > mBottomRightX)
		{
			mBottomRightX = x;
		}
		
		if(y < mTopLeftY)
		{
			mTopLeftY = y;
		}
		if(y > mBottomRightY)
		{
			mBottomRightY = y;
		}
		
		mRegionSize = mRegionSize + 1;
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
}
