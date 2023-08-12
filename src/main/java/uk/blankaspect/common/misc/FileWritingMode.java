/*====================================================================*\

FileWritingMode.java

File-writing mode enumeration.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.misc;

//----------------------------------------------------------------------


// FILE-WRITING MODE ENUMERATION


public enum FileWritingMode
	implements IStringKeyed
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	DIRECT
	(
		"direct",
		"Direct"
	),

	USE_TEMP_FILE
	(
		"useTempFile",
		"Use a temporary file"
	),

	USE_TEMP_FILE_PRESERVE_ATTRS
	(
		"useTempFilePreserveAttributes",
		"Use a temporary file, preserve attributes"
	);

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	private FileWritingMode(String key,
							String text)
	{
		this.key = key;
		this.text = text;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static FileWritingMode forKey(String key)
	{
		for (FileWritingMode value : values())
		{
			if (value.key.equals(key))
				return value;
		}
		return null;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : IStringKeyed interface
////////////////////////////////////////////////////////////////////////

	public String getKey()
	{
		return key;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	@Override
	public String toString()
	{
		return text;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	String	key;
	private	String	text;

}

//----------------------------------------------------------------------
