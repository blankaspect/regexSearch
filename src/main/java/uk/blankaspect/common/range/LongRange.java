/*====================================================================*\

LongRange.java

Class: long range.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.range;

//----------------------------------------------------------------------


// IMPORTS


import uk.blankaspect.common.exception.UnexpectedRuntimeException;

//----------------------------------------------------------------------


// CLASS: LONG RANGE


public class LongRange
	implements Cloneable
{

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	public	long	lowerBound;
	public	long	upperBound;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public LongRange()
	{
	}

	//------------------------------------------------------------------

	public LongRange(long lowerBound,
					 long upperBound)
	{
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}

	//------------------------------------------------------------------

	public LongRange(LongRange range)
	{
		lowerBound = range.lowerBound;
		upperBound = range.upperBound;
	}

	//------------------------------------------------------------------

	/**
	 * @throws NumberFormatException
	 * @throws IllegalArgumentException
	 */

	public LongRange(String str)
	{
		String[] strs = str.split(" *, *", -1);
		if (strs.length != 2)
			throw new IllegalArgumentException();
		lowerBound = Long.parseLong(strs[0]);
		upperBound = Long.parseLong(strs[1]);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	@Override
	public LongRange clone()
	{
		try
		{
			return (LongRange)super.clone();
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
		if (obj instanceof LongRange)
		{
			LongRange range = (LongRange)obj;
			return ((lowerBound == range.lowerBound) && (upperBound == range.upperBound));
		}
		return false;
	}

	//------------------------------------------------------------------

	@Override
	public int hashCode()
	{
		long sum = lowerBound + upperBound;
		return (int)(sum * (sum + 1) / 2 + lowerBound);
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

	public long getInterval()
	{
		return (upperBound - lowerBound + 1);
	}

	//------------------------------------------------------------------

	public long getValue(double fraction)
	{
		return (lowerBound + Math.round((double)(upperBound - lowerBound) * fraction));
	}

	//------------------------------------------------------------------

	public boolean contains(long value)
	{
		return ((value >= lowerBound) && (value <= upperBound));
	}

	//------------------------------------------------------------------

	public long nearestValueWithin(long value)
	{
		return (Math.min(Math.max(lowerBound, value), upperBound));
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
