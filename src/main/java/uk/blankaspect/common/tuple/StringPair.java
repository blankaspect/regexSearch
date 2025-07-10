/*====================================================================*\

StringPair.java

Record: pair of strings.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.tuple;

//----------------------------------------------------------------------


// IMPORTS


import java.util.Objects;

//----------------------------------------------------------------------


// RECORD: PAIR OF STRINGS


/**
 * This record implements an immutable ordered pair of strings.
 */

public record StringPair(
	String	first,
	String	second)
	implements Cloneable
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** A pair of empty strings. */
	public static final	StringPair	EMPTY	= new StringPair("", "");

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates and returns a new instance of a pair of strings with the specified elements.
	 *
	 * @param  first
	 *           the first element of the pair.
	 * @param  second
	 *           the second element of the pair.
	 * @return a new instance of a pair of strings whose elements are {@code first} and {@code second}.
	 */

	public static StringPair of(
		String	first,
		String	second)
	{
		return new StringPair(first, second);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : Cloneable interface
////////////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 */

	@Override
	public StringPair clone()
	{
		return new StringPair(first, second);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 */

	@Override
	public boolean equals(
		Object	obj)
	{
		if (this == obj)
			return true;

		return (obj instanceof StringPair other) && Objects.equals(first, other.first)
				&& Objects.equals(second, other.second);
	}

	//------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */

	@Override
	public int hashCode()
	{
		return Objects.hash(first, second);
	}

	//------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */

	@Override
	public String toString()
	{
		return first + ", " + second;
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
