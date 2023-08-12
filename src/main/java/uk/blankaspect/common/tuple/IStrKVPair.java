/*====================================================================*\

IStrKVPair.java

Interface: key-value pair whose key and value are strings.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.tuple;

//----------------------------------------------------------------------


// IMPORTS


import java.util.Comparator;

//----------------------------------------------------------------------


// INTERFACE: KEY-VALUE PAIR WHOSE KEY AND VALUE ARE STRINGS


/**
 * This interface defines the methods that must be implemented by a key&ndash;value pair whose key and value are
 * strings.
 */

public interface IStrKVPair
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** A comparator that compares the keys of string key&ndash;value pairs. */
	public static final	Comparator<IStrKVPair>	KEY_COMPARATOR	= Comparator.comparing(IStrKVPair::key);

////////////////////////////////////////////////////////////////////////
//  Methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the key of this pair.
	 *
	 * @return the key of this pair.
	 */

	String key();

	//------------------------------------------------------------------

	/**
	 * Returns the value of this pair.
	 *
	 * @return the value of this pair.
	 */

	String value();

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
