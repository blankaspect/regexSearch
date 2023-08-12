/*====================================================================*\

EscapedTextUtils.java

Class: utility methods related to escaped text.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.misc;

//----------------------------------------------------------------------


// IMPORTS


import java.util.ArrayList;
import java.util.List;

//----------------------------------------------------------------------


// CLASS: UTILITY METHODS RELATED TO ESCAPED TEXT


public class EscapedTextUtils
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	private static final	char	ESCAPE_PREFIX_CHAR	= '\\';
	private static final	String	ESCAPE_PREFIX		= Character.toString(ESCAPE_PREFIX_CHAR);

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private EscapedTextUtils()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static String escapeSeparator(String str,
										 char   separator)
	{
		String sep = Character.toString(separator);
		return str.replace(ESCAPE_PREFIX, ESCAPE_PREFIX + ESCAPE_PREFIX).replace(sep, ESCAPE_PREFIX + sep);
	}

	//------------------------------------------------------------------

	public static List<String> split(CharSequence seq,
									 char         separator)
	{
		List<String> strs = new ArrayList<>();
		StringBuilder buffer = new StringBuilder();
		int index = 0;
		while (index < seq.length())
		{
			char ch = seq.charAt(index++);
			if (ch == ESCAPE_PREFIX_CHAR)
			{
				if (index < seq.length())
					buffer.append(seq.charAt(index++));
			}
			else if (ch == separator)
			{
				strs.add(buffer.toString());
				buffer.setLength(0);
			}
			else
				buffer.append(ch);
		}
		strs.add(buffer.toString());

		return strs;
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
