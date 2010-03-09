package co.uk.gauntface.android.mobileeye.imageprocessing;

/**
 * Code taken from here - http://en.literateprograms.org/Complex_numbers_(Java)
 * @author matt
 *
 */
public class ComplexNumber
{
	double mReal;
	double mImginary;
	
	public ComplexNumber()
	{
		mReal = 0;
		mImginary = 0;
	}
		  
	public ComplexNumber(double real, double imaginary)
	{
		mReal= real;
		mImginary = imaginary;
	}
		  
	public ComplexNumber(ComplexNumber input)
	{
		mReal = input.getReal();
		mImginary = input.getImaginary();
	}
	
	public void setReal(double real)
	{
		mReal = real;
	}
	  
	public void setImaginary(double imaginary)
	{
		mImginary = imaginary;
	}
	
	public double getReal()
	{
		return mReal;
	}
	
	public double getImaginary()
	{
		return mImginary;
	}
	
	public ComplexNumber getConjugate()
	{
		return new ComplexNumber(mReal, mImginary * (-1));
	}
	
	public ComplexNumber add(ComplexNumber op)
	{
		ComplexNumber result = new ComplexNumber();
	    result.setReal(mReal + op.getReal());
	    result.setImaginary(mImginary + op.getImaginary());
	    return result;
	}
	  
	public ComplexNumber sub(ComplexNumber op)
	{
		ComplexNumber result = new ComplexNumber();
	    result.setReal(mReal - op.getReal());
	    result.setImaginary(mImginary - op.getImaginary());
	    return result;
	}
	  
	public ComplexNumber mul(ComplexNumber op)
	{
		ComplexNumber result = new ComplexNumber();
	    result.setReal(mReal * op.getReal() - mImginary * op.getImaginary());
	    result.setImaginary(mReal * op.getImaginary() + mImginary * op.getReal());
	    return result;
	}

	public ComplexNumber div(ComplexNumber op)
	{
		ComplexNumber result = new ComplexNumber(this);
	    result = result.mul(op.getConjugate());
	    double opNormSq = op.getReal()*op.getReal()+op.getImaginary()*op.getImaginary();
	    result.setReal(result.getReal() / opNormSq);
	    result.setImaginary(result.getImaginary() / opNormSq);
	    return result;
	}
}
