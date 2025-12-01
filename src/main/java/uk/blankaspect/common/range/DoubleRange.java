/*====================================================================*\

DoubleRange.java

Class: double range.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.range;

//----------------------------------------------------------------------


// IMPORTS


import java.text.NumberFormat;

import uk.blankaspect.common.exception2.UnexpectedRuntimeException;

//----------------------------------------------------------------------


// CLASS: DOUBLE RANGE


public class DoubleRange
	implements Cloneable
{

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	public	double	lowerBound;
	public	double	upperBound;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public DoubleRange()
	{
	}

	//------------------------------------------------------------------

	public DoubleRange(double lowerBound,
					   double upperBound)
	{
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}

	//------------------------------------------------------------------

	public DoubleRange(DoubleRange range)
	{
		lowerBound = range.lowerBound;
		upperBound = range.upperBound;
	}

	//------------------------------------------------------------------

	/**
	 * @throws NumberFormatException
	 * @throws IllegalArgumentException
	 */

	public DoubleRange(String str)
	{
		String[] strs = str.split(" *, *", -1);
		if (strs.length != 2)
			throw new IllegalArgumentException();
		lowerBound = Double.parseDouble(strs[0]);
		upperBound = Double.parseDouble(strs[1]);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	@Override
	public DoubleRange clone()
	{
		try
		{
			return (DoubleRange)super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			throw new UnexpectedRuntimeException(e);
		}
	}

	//------------------------------------------------------------------

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;

		return (obj instanceof DoubleRange other) && (lowerBound == other.lowerBound)
				&& (upperBound == other.upperBound);
	}

	//------------------------------------------------------------------

	@Override
	public int hashCode()
	{
		long bits = Double.doubleToLongBits(lowerBound);
		bits ^= 31 * Double.doubleToLongBits(upperBound);
		return (int)bits ^ (int)(bits >> 32);
	}

	//------------------------------------------------------------------

	@Override
	public String toString()
	{
		return new String(lowerBound + ", " + upperBound);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public String toString(NumberFormat format)
	{
		return new String(format.format(lowerBound) + ", " + format.format(upperBound));
	}

	//------------------------------------------------------------------

	public double getInterval()
	{
		return upperBound - lowerBound;
	}

	//------------------------------------------------------------------

	public double getValue(double fraction)
	{
		return lowerBound + (upperBound - lowerBound) * fraction;
	}

	//------------------------------------------------------------------

	public boolean contains(double value)
	{
		return (value >= lowerBound) && (value <= upperBound);
	}

	//------------------------------------------------------------------

	public double nearestValueWithin(double value)
	{
		return Math.min(Math.max(lowerBound, value), upperBound);
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
