package co.uk.gauntface.android.mobileeye.imageprocessing;

public class EdgeDetection
{
	private double[] mKernel;
	private int mKernelSize;
	private double mKernelTotal;
	
	private ComplexNumber[] mFourierKernel;
	
	/**
	 * 
	 * @param sigma		Standard Deviation
	 */
	public EdgeDetection()
	{
		createEdgeKernel();
		
		mFourierKernel = IPUtility.computeFreqDomain(mKernel);
	}
	
	private void createEdgeKernel()
	{
		mKernelSize = 3;
		mKernel = new double[]{-1, -1, -1, -1, 8, -1, -1, -1, -1};
		mKernelTotal = 16;
	}
	
	public int[] classifyEdges(int[] pixels, int width, int height)
	{
		return IPUtility.convolve(pixels, width, height, mKernel, mKernelSize, mKernelTotal);
	}
	
}
