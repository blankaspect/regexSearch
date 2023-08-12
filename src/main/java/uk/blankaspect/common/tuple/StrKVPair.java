/*====================================================================*\

StrKVPair.java

Record: key-value pair whose key and value are strings.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.tuple;

//----------------------------------------------------------------------


// RECORD: KEY-VALUE PAIR WHOSE KEY AND VALUE ARE STRINGS


/**
 * This record implements a key&ndash;value pair whose key and value are strings.
 */

public record StrKVPair(
	String	key,
	String	value)
	implements IStrKVPair
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public StrKVPair
	{
		// Validate arguments
		if (key == null)
			throw new IllegalArgumentException("Null key");
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates and returns a new instance of a string key&ndash;value pair with the specified key and with the empty
	 * string as its value.
	 *
	 * @param  key
	 *           the key of the pair.
	 * @return a key&ndash;value pair whose key is {@code key} and whose value is the empty string.
	 */

	public static StrKVPair of(
		String	key)
	{
		return new StrKVPair(key, "");
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a new instance of a string key&ndash;value pair with the specified key and value.
	 *
	 * @param  key
	 *           the key of the pair.
	 * @param  value
	 *           the value of the pair.
	 * @return a key&ndash;value pair whose key is {@code key} and whose value is {@code value}.
	 */

	public static StrKVPair of(
		String	key,
		String	value)
	{
		return new StrKVPair(key, value);
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a new instance of a string key&ndash;value pair that is a copy of the specified pair.
	 *
	 * @param  source
	 *           the key&ndash;value pair that will be copied.
	 * @return a key&ndash;value pair that is a copy of {@code source}.
	 */

	public static StrKVPair copyOf(
		IStrKVPair	source)
	{
		return new StrKVPair(source.key(), source.value());
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
