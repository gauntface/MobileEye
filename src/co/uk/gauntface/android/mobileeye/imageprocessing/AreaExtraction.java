package co.uk.gauntface.android.mobileeye.imageprocessing;

public class AreaExtraction
{
	private static final int COLOR_REGION = 255;
	private static final int COLOR_CENTER_REGION = 0;
	private static final int NO_COLOR_REGION = -1;
	
	private static int[] mPixels;
	private static int mCenterPixelIndex;
	private static int[] mTopLeft;
	private static int[] mBottomRight;
	
	public static ImagePackage getExtraction(ImagePackage imgPackage)
	{
		mPixels = imgPackage.getRegionGroupPixels();
		
		int imgWidth = imgPackage.getImgWidth();
		int imgHeight = imgPackage.getImgHeight();
		
		RegionGroup regionGroup = imgPackage.getRegionGroup();
		Pair weightedCenter = regionGroup.getWeightedCenter();
		
		int centerPointX = weightedCenter.getArg1();
		int centerPointY = weightedCenter.getArg2();
		
		mCenterPixelIndex = (centerPointY * imgWidth) + centerPointX;
		
		mTopLeft = new int[]{centerPointX, centerPointY};
		mBottomRight = new int[]{centerPointX, centerPointY};
		
		boolean areaFullyExpanded = false;
		
		boolean expandUp = true;
		boolean expandLeft = true;
		boolean expandRight = true;
		boolean expandDown = true;
		
		int numberOfAcceptableErrorPixels = 1;
		
		while(areaFullyExpanded == false)
		{
			int xSpread = mBottomRight[0] - mTopLeft[0];
			int ySpread = mBottomRight[1] - mTopLeft[1];
			
			if(expandUp == true)
			{
				int tempTopY = mTopLeft[1];
				
				if(tempTopY > 0)
				{
					// Can move up
					tempTopY = tempTopY - 1;
					
					int newPixelOffset = (tempTopY * imgWidth) + mTopLeft[0];
					int incorrectPixelCount = 0;
					
					for(int i = 0; i <= xSpread; i++)
					{
						if(mPixels[newPixelOffset + i] != mPixels[mCenterPixelIndex])
						{
							if(incorrectPixelCount > numberOfAcceptableErrorPixels)
							{
								expandUp = false;
								tempTopY = mTopLeft[1];
								break;
							}
							else
							{
								incorrectPixelCount = incorrectPixelCount + 1;
							}
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
					int incorrectPixelCount = 0;
					
					for(int i = 0; i <= xSpread; i++)
					{
						if(mPixels[newPixelOffset + i] != mPixels[mCenterPixelIndex])
						{
							if(incorrectPixelCount > numberOfAcceptableErrorPixels)
							{
								expandDown = false;
								tempBottomY = mBottomRight[1];
								break;
							}
							else
							{
								incorrectPixelCount = incorrectPixelCount + 1;
							}
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
					int incorrectPixelCount = 0;
					
					for(int i = 0; i <= ySpread; i++)
					{
						if(mPixels[yOffset] != mPixels[mCenterPixelIndex])
						{
							if(incorrectPixelCount > numberOfAcceptableErrorPixels)
							{
								expandLeft = false;
								tempTopX = mTopLeft[0];
								break;
							}
							else
							{
								incorrectPixelCount = incorrectPixelCount + 1;
							}
						}
						yOffset = yOffset + imgWidth;
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
					int incorrectPixelCount = 0;
					
					for(int i = 0; i <= ySpread; i++)
					{
						if(mPixels[yOffset] != mPixels[mCenterPixelIndex])
						{
							if(incorrectPixelCount > numberOfAcceptableErrorPixels)
							{
								tempBottomX = mBottomRight[0];
								expandRight = false;
								break;
							}
							else
							{
								incorrectPixelCount = incorrectPixelCount + 1; 
							}
						}
						yOffset = yOffset + imgWidth;
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
		
		//Pair maxGroup = imgPackage.getPixelGroups().get(0);
		
		RegionGroup extraction = new RegionGroup(mTopLeft[0], mTopLeft[1], mBottomRight[0], mBottomRight[1]);
		
		imgPackage.setExtractionArea(extraction);
		imgPackage = createPixels(imgPackage);
		
		return imgPackage;
	}
	
	public static ImagePackage createPixels(ImagePackage i)
	{
		int[] origImg = i.getOrigImgPixels();
		RegionGroup r = i.getExtractionArea();
		int[] areaPixels = new int[i.getImgWidth() * i.getImgHeight()];
		
		double averageRegionValue = 0;
		int extractionSize = 0;
		
		int xAmount = r.getBottomRightX() - r.getTopLeftX();
		int yAmount = r.getBottomRightY() - r.getTopLeftY();
		
		int centerX = r.getTopLeftX() + (xAmount / 2);
		int centerY = r.getTopLeftY() + (yAmount / 2);
		
		int centerXMin = centerX - 5;
		int centerXMax = centerX + 5;
		
		int centerYMin = centerY - 5;
		int centerYMax = centerY + 5;
		
		int yOffset  = 0;
		for(int y = 0; y < i.getImgHeight(); y++)
		{
			for(int x = 0; x < i.getImgWidth(); x++)
			{
				if(y >= r.getTopLeftY() && y <= r.getBottomRightY() && x >= r.getTopLeftX() && x <= r.getBottomRightX())
				{
					int t = yOffset + x;
					averageRegionValue = averageRegionValue + origImg[t];
					extractionSize = extractionSize + 1;
					
					if((y >= centerYMin && y <= centerYMax) && (x >= centerXMin && x <= centerXMax))
					{
						areaPixels[t] = COLOR_CENTER_REGION;
					}
					else
					{
						areaPixels[t] = COLOR_REGION;
					}
				}
				else
				{
					areaPixels[yOffset+x] = NO_COLOR_REGION;
				}
			}
			yOffset = yOffset + i.getImgWidth();
		}
		
		if(extractionSize > 0)
		{
			averageRegionValue = averageRegionValue / extractionSize;
		}
		else
		{
			averageRegionValue = 0;
		}
		
		i.setAreaExtractionPixels(areaPixels);
		i.setAveragePixelValue(averageRegionValue);
		
		return i;
	}
}
