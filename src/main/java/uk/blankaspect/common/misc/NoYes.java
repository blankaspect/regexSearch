/*====================================================================*\

NoYes.java

Enumeration: No-Yes option.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.misc;

//----------------------------------------------------------------------


// IMPORTS


import java.util.stream.Stream;

import uk.blankaspect.common.string.StringUtils;

//----------------------------------------------------------------------


// ENUMERATION: NO-YES OPTION


public enum NoYes
	implements IStringKeyed
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	NO
	(
		"no", "false"
	),

	YES
	(
		"yes", "true"
	);

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	String[]	keys;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	private NoYes(String... keys)
	{
		this.keys = keys;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static NoYes forKey(String key)
	{
		return Stream.of(values())
						.filter(value -> Stream.of(value.keys).anyMatch(key0 -> key0.equals(key)))
						.findFirst()
						.orElse(null);
	}

	//------------------------------------------------------------------

	public static NoYes forBoolean(boolean value)
	{
		return (value ? YES : NO);
	}

	//------------------------------------------------------------------

	public static String getKey(boolean value)
	{
		return forBoolean(value).getKey();
	}

	//------------------------------------------------------------------

	public static String toString(boolean value)
	{
		return forBoolean(value).toString();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : IStringKeyed interface
////////////////////////////////////////////////////////////////////////

	@Override
	public String getKey()
	{
		return keys[0];
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	@Override
	public String toString()
	{
		return StringUtils.firstCharToUpperCase(getKey());
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public boolean toBoolean()
	{
		return ((this == NO) ? false : true);
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
