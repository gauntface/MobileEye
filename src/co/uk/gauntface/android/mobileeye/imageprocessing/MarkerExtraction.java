package co.uk.gauntface.android.mobileeye.imageprocessing;

public class MarkerExtraction
{
	private static final int COLOR_REGION = 255;
	private static final int COLOR_CENTER_REGION = 0;
	private static final int NO_COLOR_REGION = -1;
	
	private int[] mNewPixels;
	private int[] mPixels;
	
	public Pair[] extractCorners(ImagePackage imgPkg)
	{
		mPixels = imgPkg.getOrigImgPixels();
		int imgWidth = imgPkg.getImgWidth();
		int imgHeight = imgPkg.getImgHeight();
		
		int centerX = imgWidth / 2;
		int centerY = imgHeight / 2;
		
		mNewPixels = new int[mPixels.length];
		
		int markerInitValue = mPixels[centerX + (imgWidth * centerY)];
		
		Pair topLeft = regionSearch(-1, -1, centerX, centerY, imgWidth, imgHeight);
		Pair topRight = regionSearch(1, -1, centerX, centerY, imgWidth, imgHeight);
		Pair bottomLeft = regionSearch(-1, 1, centerX, centerY, imgWidth, imgHeight);
		Pair bottomRight = regionSearch(1, 1, centerX, centerY, imgWidth, imgHeight);
		
		if(topLeft != null && topRight != null && bottomLeft != null && bottomRight != null)
		{
			return new Pair[]{topLeft, topRight, bottomRight, bottomLeft};
		}
		
		return null;
	}
	
	private Pair regionSearch(int xChange, int yChange, int startX, int startY, int imgWidth, int imgHeight)
	{
		boolean searchX = true;
		boolean searchY = true;
		
		int xCoord = startX;
		int yCoord = startY;
		
		int yOffset = yCoord * imgWidth;
		
		boolean error = false;
		
		while(searchX == true || searchY == true)
		{
			if(searchX == true)
			{
				int newXCoord = xCoord + xChange;
				if(newXCoord > 0 && newXCoord < (imgWidth-1))
				{
					if(passThreshold(mPixels[yOffset + newXCoord], mPixels[yOffset + xCoord]) == true)
					{
						xCoord = newXCoord;
						searchY = true;
					}
					else
					{
						searchX = false;
					}
				}
				else
				{
					error = true;
					break;
				}
			}
			
			if(searchY == true)
			{
				int newYCoord = yCoord + yChange;
				if(newYCoord > 0 && newYCoord < (imgHeight-1))
				{
					int newOffset = newYCoord * imgWidth;
					if(passThreshold(mPixels[newOffset + xCoord], mPixels[yOffset + xCoord]) == true)
					{
						yCoord = newYCoord;
						yOffset = newOffset;
						searchX = true;
					}
					else
					{
						searchY = false;
					}
				}
				else
				{
					error = true;
					break;
				}
			}
		}
		
		if(error == false)
		{
			Pair result = new Pair(xCoord, yCoord);
			return result;
		}
		else
		{
			return null;
		}
	}
	
	public void regionGrowSearch(int intPixelValue, int startX, int startY, int imgWidth, int imgHeight)
	{
		boolean areaFullyExpanded = false;
		
		int[] mTopLeft = new int[]{startX, startY};
		int[] mBottomRight = new int[]{startX, startY};
		
		boolean expandUp = true;
		boolean expandDown = true;
		boolean expandLeft = true;
		boolean expandRight = true;
		
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
					
					int newPixelOffset = (tempTopY * imgWidth) + mTopLeft[0];
					int prevPixelOffset = (mTopLeft[1] * imgWidth) + mTopLeft[0];
					
					for(int i = 0; i <= xSpread; i++)
					{
						if(passThreshold(mPixels[newPixelOffset + i], mPixels[prevPixelOffset + i]) == false)
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
				if(mBottomRight[1] < (imgHeight - 1))
				{
					// Can move down
					tempBottomY = tempBottomY + 1;
					
					int newPixelOffset = (tempBottomY * imgWidth) + mTopLeft[0];
					int prevPixelOffset = (mBottomRight[1] * imgWidth) + mTopLeft[0];
					
					for(int i = 0; i <= xSpread; i++)
					{
						if(passThreshold(mPixels[newPixelOffset + i], mPixels[prevPixelOffset + 1]) == false)
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
					
					int yOffset = (mTopLeft[1] * imgWidth) + tempTopX;
					int prevYOffset = (mTopLeft[1] * imgWidth) + mTopLeft[0];
					
					for(int i = 0; i <= ySpread; i++)
					{
						if(passThreshold(mPixels[yOffset], mPixels[prevYOffset]) == false)
						{
							expandLeft = false;
							tempTopX = mTopLeft[0];
							break;
						}
						yOffset = yOffset + imgWidth;
						prevYOffset = prevYOffset + imgWidth;
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
				if(mBottomRight[0] < (imgWidth - 1))
				{
					// Can move up
					tempBottomX = tempBottomX + 1;
					
					int yOffset = (mTopLeft[1] * imgWidth) + tempBottomX;
					int prevYOffset = (mTopLeft[1] * imgWidth) + mBottomRight[0];
					
					for(int i = 0; i <= ySpread; i++)
					{
						if(passThreshold(mPixels[yOffset], mPixels[prevYOffset]) == false)
						{
							tempBottomX = mBottomRight[0];
							expandRight = false;
							break;
						}
						yOffset = yOffset + imgWidth;
						prevYOffset = prevYOffset + imgWidth;
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
			
			if(expandUp == false && expandDown == false &&
					expandLeft == false && expandRight == false)
			{
				areaFullyExpanded = true;
			}
		}
		
		for(int y = 0; y < imgHeight; y++)
		{
			for(int x = 0; x < imgWidth; x++)
			{
				if(x >= mTopLeft[0] && x <= mBottomRight[0] && y >= mTopLeft[1] && y <= mBottomRight[1])
				{
					mNewPixels[(x + (y*imgWidth))] = COLOR_REGION;
				}
				else
				{
					mNewPixels[(x + (y*imgWidth))] = NO_COLOR_REGION;
				}
			}
		}
	}
	
	private boolean passThreshold(int pixelValueNew, int pixelValueOld)
	{
		int threshold = 10;
		
		if(pixelValueNew >= (pixelValueOld - threshold) && pixelValueNew <= (pixelValueOld + threshold))
		{
			return true;
		}
		
		return false;
	}
}
