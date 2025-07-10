/*====================================================================*\

IntegerRange.java

Class: integer range.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.range;

import uk.blankaspect.common.exception2.UnexpectedRuntimeException;

//----------------------------------------------------------------------


// CLASS: INTEGER RANGE


public class IntegerRange
	implements Cloneable
{

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	public	int	lowerBound;
	public	int	upperBound;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public IntegerRange()
	{
	}

	//------------------------------------------------------------------

	public IntegerRange(int lowerBound,
						int upperBound)
	{
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}

	//------------------------------------------------------------------

	public IntegerRange(IntegerRange range)
	{
		lowerBound = range.lowerBound;
		upperBound = range.upperBound;
	}

	//------------------------------------------------------------------

	/**
	 * @throws NumberFormatException
	 * @throws IllegalArgumentException
	 */

	public IntegerRange(String str)
	{
		String[] strs = str.split(" *, *", -1);
		if (strs.length != 2)
			throw new IllegalArgumentException();
		lowerBound = Integer.parseInt(strs[0]);
		upperBound = Integer.parseInt(strs[1]);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	@Override
	public IntegerRange clone()
	{
		try
		{
			return (IntegerRange)super.clone();
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
		if (obj instanceof IntegerRange)
		{
			IntegerRange range = (IntegerRange)obj;
			return ((lowerBound == range.lowerBound) && (upperBound == range.upperBound));
		}
		return false;
	}

	//------------------------------------------------------------------

	@Override
	public int hashCode()
	{
		int sum = lowerBound + upperBound;
		return (sum * (sum + 1) / 2 + lowerBound);
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

	public int getInterval()
	{
		return upperBound - lowerBound + 1;
	}

	//------------------------------------------------------------------

	public int getValue(double fraction)
	{
		return lowerBound + (int)Math.round((double)(upperBound - lowerBound) * fraction);
	}

	//------------------------------------------------------------------

	public boolean contains(int value)
	{
		return (value >= lowerBound) && (value <= upperBound);
	}

	//------------------------------------------------------------------

	public int nearestValueWithin(int value)
	{
		return Math.min(Math.max(lowerBound, value), upperBound);
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
