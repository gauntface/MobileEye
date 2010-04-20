package co.uk.gauntface.android.mobileeye.imageprocessing;

public class Peak
{
	private int mMinIndex;
	private int mMaxIndex;
	private int mPeakIndex;
	private int mPeakSize;
	
	public Peak()
	{
		mMinIndex = -1;
		mMaxIndex = -1;
		mPeakIndex = -1;
		mPeakSize = 0;
	}
	
	public Peak(int minIndex, int maxIndex, int peakIndex, int peakSize)
	{
		mMinIndex = minIndex;
		mMaxIndex = maxIndex;
		mPeakIndex = peakIndex;
		mPeakSize = peakSize;
	}
	
	public int getMinIndex()
	{
		return mMinIndex;
	}
	
	public void setMinIndex(int minIndex)
	{
		mMinIndex = minIndex;
	}
	
	public int getMaxIndex()
	{
		return mMaxIndex;
	}
	
	public void setMaxIndex(int maxIndex)
	{
		mMaxIndex = maxIndex;
	}
	
	public int getPeakIndex()
	{
		return mPeakIndex;
	}
	
	public void setPeakIndex(int peakIndex)
	{
		mPeakIndex = peakIndex;
	}
	
	public int getPeakSize()
	{
		return mPeakSize;
	}
	
	public void setPeakSize(int size)
	{
		mPeakSize = size;
	}
}
