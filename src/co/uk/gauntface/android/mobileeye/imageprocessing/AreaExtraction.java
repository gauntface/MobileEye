package co.uk.gauntface.android.mobileeye.imageprocessing;

import java.util.ArrayList;

import android.util.Log;

import co.uk.gauntface.android.mobileeye.Singleton;

public class AreaExtraction
{
	private static int[] mPixels;
	private static int mCenterPixelIndex;
	private static int[] mTopLeft;
	private static int[] mBottomRight;
	private static int mImgWidth;
	private static int mImgHeight;
	
	public static ImagePackage getExtraction(ImagePackage imgPackage)
	{
		mPixels = imgPackage.getImgPixels();
		
		mImgWidth = imgPackage.getImgWidth();
		mImgHeight = imgPackage.getImgHeight();
		
		int[] regionPixels = imgPackage.getRegionGroupPixels();
		RegionGroup regionGroup = imgPackage.getRegionGroup();
		
		int centerPointX = (int) (regionGroup.getTopLeftX() - regionGroup.getBottomRightX()) / 2;
		int centerPointY = (int) (regionGroup.getTopLeftY() - regionGroup.getBottomRightY()) / 2;
		mCenterPixelIndex = (centerPointY * mImgWidth) + centerPointX;
		
		mTopLeft = new int[]{centerPointX, centerPointY};
		mBottomRight = new int[]{centerPointX, centerPointY};
		
		boolean areaFullyExpanded = false;
		
		boolean expandUp = true;
		boolean expandLeft = true;
		boolean expandRight = true;
		boolean expandDown = true;
		
		while(areaFullyExpanded == false)
		{
			int xSpread = mBottomRight[0] - mTopLeft[0];
			int ySpread = mBottomRight[1] - mTopLeft[1];
			//Log.v(Singleton.TAG, "ySpread = " + ySpread);
			
			if(expandUp == true)
			{
				int tempTopY = mTopLeft[1];
				if(tempTopY > 0)
				{
					// Can move up
					tempTopY = tempTopY - 1;
					
					int newPixelOffset = (tempTopY * mImgWidth) + mTopLeft[0];
					
					for(int i = 0; i <= xSpread; i++)
					{
						if(mPixels[newPixelOffset + i] != mPixels[mCenterPixelIndex])
						{
							expandUp = false;
							tempTopY = mTopLeft[1];
							break;
						}
					}
					
					if(expandUp == true)
					{
						mTopLeft[1] = tempTopY;
					}
				}
				else
				{
					expandUp = false;
				}
			}
			
			if(expandDown == true)
			{
				int tempBottomY = mBottomRight[1];
				if(mBottomRight[1] < (mImgHeight - 1))
				{
					// Can move down
					tempBottomY = tempBottomY + 1;
					
					int newPixelOffset = (tempBottomY * mImgWidth) + mTopLeft[0];
					
					for(int i = 0; i <= xSpread; i++)
					{
						Log.d("mobileeye", "tempBottomY = " + tempBottomY);
						Log.d("mobileeye", "ImgWidth = " + mImgWidth);
						Log.d("mobileeye", "mTopLeftX = " + mTopLeft[0]);
						Log.d("mobileeye", "xSpread = " + xSpread);
						Log.d("mobileeye", "i = " + i);
						
						if(mPixels[newPixelOffset + i] != mPixels[mCenterPixelIndex])
						{
							expandDown = false;
							tempBottomY = mBottomRight[1];
							break;
						}
					}
					
					if(expandDown == true)
					{
						mBottomRight[1] = tempBottomY;
					}
				}
				else
				{
					expandDown = false;
				}
			}
			
			if(expandLeft == true)
			{
				int tempTopX = mTopLeft[0];
				if(mTopLeft[0] > 0)
				{
					// Can move up
					tempTopX = tempTopX - 1;
					
					int yOffset = (mTopLeft[1] * mImgWidth) + tempTopX;
					
					for(int i = 0; i <= ySpread; i++)
					{
						if(mPixels[yOffset] != mPixels[mCenterPixelIndex])
						{
							expandLeft = false;
							tempTopX = mTopLeft[0];
							break;
						}
						yOffset = yOffset + mImgWidth;
					}
					
					if(expandLeft == true)
					{
						mTopLeft[0] = tempTopX;
					}
				}
				else
				{
					expandLeft = false;
				}
			}
			
			if(expandRight == true)
			{
				int tempBottomX = mBottomRight[0];
				if(mBottomRight[0] < (mImgWidth - 1))
				{
					// Can move up
					tempBottomX = tempBottomX + 1;
					
					int yOffset = (mTopLeft[1] * mImgWidth) + tempBottomX;
					
					for(int i = 0; i <= ySpread; i++)
					{
						if(mPixels[yOffset] != mPixels[mCenterPixelIndex])
						{
							tempBottomX = mBottomRight[0];
							expandRight = false;
							break;
						}
						yOffset = yOffset + mImgWidth;
					}
					
					if(expandRight == true)
					{
						mBottomRight[0] = tempBottomX;
					}
				}
				else
				{
					expandRight = false;
				}
			}
			
			//Log.v(Singleton.TAG, "topX = "+mTopLeft[0]+" topY = " + mTopLeft[1]+" bottomX = "+mBottomRight[0]+" bottomY = " + mBottomRight[1]);
			
			if(expandUp == false && expandDown == false &&
					expandLeft == false && expandRight == false)
			{
				areaFullyExpanded = false;
			}
		}
		
		//Pair maxGroup = imgPackage.getPixelGroups().get(0);
		
		
		
		/**Pair groupCenter = imgPackage.getGroupCenters().get(0);
		
		//Log.v(Singleton.TAG, "Center Max Group = ("+mCenter[0]+","+mCenter[1]+")");
		
		boolean run = true;
		while(run)
		{
			run = expandArea();
		}
		
		int[][] newPixels = new int[mImgHeight][mImgWidth];
		
		for(int i = 0; i < mImgHeight; i++)
		{
			for(int j = 0; j < mImgWidth; j++)
			{
				if(i >= mTopLeft[1] && i < mBottomRight[1] && j >= mTopLeft[0] && j < mBottomRight[0])
				{
					newPixels[i][j] = 255;
				}
				else
				{
					newPixels[i][j] = -1;
				}
			}
		}
		
		imgPackage.setImgPixels(IPUtility.convert2DArrayTo1DArray(newPixels, mImgWidth, mImgHeight));
		**/
		
		return imgPackage;
	}
	
	/**
	 * Change orientation priority if camera is portrait of landscape
	 * @return
	 */
	/**private static boolean expandArea()
	{
		int xSpread = mBottomRight[0] - mTopLeft[0];
		int ySpread = mBottomRight[1] - mTopLeft[1];
		//Log.v(Singleton.TAG, "ySpread = " + ySpread);
		
		int tempTopY = mTopLeft[1];
		if(tempTopY > 0)
		{
			// Can move up
			tempTopY = tempTopY - 1;
			
			boolean increase = true;
			int newPixelOffset = (tempTopY * mImgWidth) + mTopLeft[0];
			
			for(int i = 0; i <= xSpread; i++)
			{
				if(mPixels[newPixelOffset + i] != mPixels[mCenterPixelIndex])
				{
					increase = false;
					break;
				}
			}
			
			if(increase == false)
			{
				tempTopY = mTopLeft[1];
			}
		}
		
		int tempBottomY = mBottomRight[1];
		if(mBottomRight[1] < (mImgHeight - 1))
		{
			// Can move down
			tempBottomY = tempBottomY + 1;
			
			boolean increase = true;
			for(int i = 0; i <= xSpread; i++)
			{
				if(mPixels[tempBottomY][mTopLeft[0] + i] != mPixels[mCenter[0]][mCenter[1]])
				{
					increase = false;
					break;
				}
			}
			
			if(increase == false)
			{
				tempBottomY = mBottomRight[1];
			}
		}
		
		int tempTopX = mTopLeft[0];
		if(mTopLeft[0] > 0)
		{
			// Can move up
			tempTopX = tempTopX - 1;
			
			boolean increase = true;
			for(int i = 0; i <= ySpread; i++)
			{
				if(mPixels[mTopLeft[1] + i][tempTopX] != mPixels[mCenter[0]][mCenter[1]])
				{
					increase = false;
					break;
				}
			}
			
			if(increase == false)
			{
				tempTopX = mTopLeft[0];
			}
		}
		
		int tempBottomX = mBottomRight[0];
		if(mBottomRight[0] < (mImgWidth - 1))
		{
			// Can move up
			tempBottomX = tempBottomX + 1;
			
			boolean increase = true;
			for(int i = 0; i <= ySpread; i++)
			{
				if(mPixels[mTopLeft[1] + i][tempBottomX] != mPixels[mCenter[0]][mCenter[1]])
				{
					increase = false;
					break;
				}
			}
			
			if(increase == false)
			{
				tempBottomX = mBottomRight[0];
			}
		}
		
		//Log.v(Singleton.TAG, "topX = "+mTopLeft[0]+" topY = " + mTopLeft[1]+" bottomX = "+mBottomRight[0]+" bottomY = " + mBottomRight[1]);
		
		if(tempTopX == mTopLeft[0] && tempTopY == mTopLeft[1] &&
				tempBottomX == mBottomRight[0] && tempBottomY == mBottomRight[1])
		{
			return false;
		}
		else
		{
			mTopLeft[0] = tempTopX;
			mTopLeft[1] = tempTopY;
			mBottomRight[0] = tempBottomX;
			mBottomRight[1] = tempBottomY;
		}
		
		return true;
	}**/
}
