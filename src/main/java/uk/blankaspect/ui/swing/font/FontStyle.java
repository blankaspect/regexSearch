/*====================================================================*\

FontStyle.java

Font style enumeration.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.swing.font;

//----------------------------------------------------------------------


// IMPORTS


import java.awt.Font;

import uk.blankaspect.common.misc.IStringKeyed;

//----------------------------------------------------------------------


// FONT STYLE ENUMERATION


public enum FontStyle
	implements IStringKeyed
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	PLAIN
	(
		"plain",
		"Plain",
		Font.PLAIN
	),

	BOLD
	(
		"bold",
		"Bold",
		Font.BOLD
	),

	ITALIC
	(
		"italic",
		"Italic",
		Font.ITALIC
	),

	BOLD_ITALIC
	(
		"boldItalic",
		"Bold italic",
		Font.BOLD | Font.ITALIC
	);

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	private FontStyle(String key,
					  String text,
					  int    awtStyle)
	{
		this.key = key;
		this.text = text;
		this.awtStyle = awtStyle;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static FontStyle forKey(String key)
	{
		for (FontStyle value : values())
		{
			if (value.key.equals(key))
				return value;
		}
		return null;
	}

	//------------------------------------------------------------------

	public static FontStyle forAwtStyle(int awtStyle)
	{
		for (FontStyle value : values())
		{
			if (value.awtStyle == awtStyle)
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
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public int getAwtStyle()
	{
		return awtStyle;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	String	key;
	private	String	text;
	private	int		awtStyle;

}

//----------------------------------------------------------------------
