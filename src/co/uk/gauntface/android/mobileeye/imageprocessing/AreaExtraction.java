package co.uk.gauntface.android.mobileeye.imageprocessing;

import java.util.ArrayList;

import android.util.Log;

import co.uk.gauntface.android.mobileeye.Singleton;

public class AreaExtraction
{
	private static int[][] mPixels;
	private static int[] mCenter;
	private static int[] mTopLeft;
	private static int[] mBottomRight;
	private static int mImgWidth;
	private static int mImgHeight;
	
	public static ImagePackage getExtraction(ImagePackage imgPackage)
	{
		Pair maxGroup = imgPackage.getPixelGroups().get(0);
		Pair groupCenter = imgPackage.getGroupCenters().get(0);
		
		int centerX = ((Number)groupCenter.getArg1()).intValue();
		int centerY = ((Number)groupCenter.getArg2()).intValue();
		
		mCenter = new int[]{centerX, centerY};
		
		mImgWidth = imgPackage.getImgWidth();
		mImgHeight = imgPackage.getImgHeight();
		
		mPixels = IPUtility.convert1DArrayTo2DArray(imgPackage.getImgPixels(), mImgWidth, imgPackage.getImgHeight());
		
		mTopLeft = new int[]{centerX, centerY};
		mBottomRight = new int[]{centerX, centerY};
		
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
		
		return imgPackage;
	}
	
	/**
	 * Change orientation priority if camera is portrait of landscape
	 * @return
	 */
	private static boolean expandArea()
	{
		int xSpread = mBottomRight[0] - mTopLeft[0];
		int ySpread = mBottomRight[1] - mTopLeft[1];
		//Log.v(Singleton.TAG, "ySpread = " + ySpread);
		
		int tempTopY = mTopLeft[1];
		if(mTopLeft[1] > 0)
		{
			// Can move up
			tempTopY = tempTopY - 1;
			
			boolean increase = true;
			for(int i = 0; i <= xSpread; i++)
			{
				if(mPixels[tempTopY][mTopLeft[0] + i] != mPixels[mCenter[0]][mCenter[1]])
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
	}
}
