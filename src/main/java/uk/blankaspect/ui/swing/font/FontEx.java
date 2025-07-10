/*====================================================================*\

FontEx.java

Class: extended font.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.swing.font;

//----------------------------------------------------------------------


// IMPORTS


import java.awt.Component;
import java.awt.Font;

import java.util.List;
import java.util.Objects;

import uk.blankaspect.common.exception2.UnexpectedRuntimeException;
import uk.blankaspect.common.exception2.ValueOutOfBoundsException;

import uk.blankaspect.common.misc.EscapedTextUtils;

//----------------------------------------------------------------------


// CLASS: EXTENDED FONT


public class FontEx
	implements Cloneable
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	public static final		int		MIN_FONT_SIZE	= 4;
	public static final		int		MAX_FONT_SIZE	= 128;

	public static final		char	SEPARATOR_CHAR	= ',';

	private static final	String	DEFAULT_FONT_NAME	= "Dialog";
	private static final	int		DEFAULT_FONT_STYLE	= Font.PLAIN;
	private static final	int		DEFAULT_FONT_SIZE	= 12;

	private static final	String	MALFORMED_STR			= "The font specifier is malformed.";
	private static final	String	INVALID_STYLE_STR		= "The font style is invalid.";
	private static final	String	INVALID_SIZE_STR		= "The font size is invalid.";
	private static final	String	SIZE_OUT_OF_BOUNDS_STR	=
			"The font size must be between " + MIN_FONT_SIZE + " and " + MAX_FONT_SIZE + ".";

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	String		name;
	private	FontStyle	style;
	private	int			size;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public FontEx()
	{
	}

	//------------------------------------------------------------------

	public FontEx(
		String		name,
		FontStyle	style,
		int			size)
	{
		this.name = name;
		this.style = style;
		this.size = size;
	}

	//------------------------------------------------------------------

	public FontEx(
		Font	font)
	{
		name = font.getFontName();
		style = FontStyle.forAwtStyle(font.getStyle());
		size = font.getSize();
	}

	//------------------------------------------------------------------

	/**
	 * @throws IllegalArgumentException
	 * @throws ValueOutOfBoundsException
	 */

	public FontEx(
		String	str)
	{
		List<String> strs = EscapedTextUtils.split(str, SEPARATOR_CHAR);
		if (strs.size() != 3)
			throw new IllegalArgumentException(MALFORMED_STR);

		int index = 0;
		String value = strs.get(index++).strip();
		if (!value.isEmpty())
			name = value;

		value = strs.get(index++).strip();
		if (!value.isEmpty())
		{
			style = FontStyle.forKey(value);
			if (style == null)
				throw new IllegalArgumentException(INVALID_STYLE_STR);
		}

		value = strs.get(index++).strip();
		if (!value.isEmpty())
		{
			try
			{
				size = Integer.parseInt(value);
			}
			catch (NumberFormatException e)
			{
				throw new IllegalArgumentException(INVALID_SIZE_STR);
			}
			if ((size < MIN_FONT_SIZE) || (size > MAX_FONT_SIZE))
				throw new ValueOutOfBoundsException(SIZE_OUT_OF_BOUNDS_STR);
		}
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : Cloneable interface
////////////////////////////////////////////////////////////////////////

	@Override
	public FontEx clone()
	{
		try
		{
			return (FontEx)super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			throw new UnexpectedRuntimeException(e);
		}
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	@Override
	public boolean equals(
		Object	obj)
	{
		if (this == obj)
			return true;

		return (obj instanceof FontEx other) && Objects.equals(name, other.name) && (style == other.style)
				&& (size == other.size);
	}

	//------------------------------------------------------------------

	@Override
	public int hashCode()
	{
		int code = Objects.hashCode(name);
		code = 31 * code + style.ordinal();
		code = 31 * code + size;
		return code;
	}

	//------------------------------------------------------------------

	@Override
	public String toString()
	{
		StringBuilder buffer = new StringBuilder();

		if (name != null)
			buffer.append(EscapedTextUtils.escapeSeparator(name, SEPARATOR_CHAR));
		buffer.append(SEPARATOR_CHAR);
		buffer.append(' ');

		if (style != null)
			buffer.append(style.getKey());
		buffer.append(SEPARATOR_CHAR);
		buffer.append(' ');

		if (size > 0)
			buffer.append(size);
		return buffer.toString();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	public String getName()
	{
		return name;
	}

	//------------------------------------------------------------------

	public FontStyle getStyle()
	{
		return style;
	}

	//------------------------------------------------------------------

	public int getSize()
	{
		return size;
	}

	//------------------------------------------------------------------

	public void setName(
		String	name)
	{
		this.name = name;
	}

	//------------------------------------------------------------------

	public void setStyle(
		FontStyle	style)
	{
		this.style = style;
	}

	//------------------------------------------------------------------

	public void setSize(
		int	size)
	{
		this.size = size;
	}

	//------------------------------------------------------------------

	public Font toFont()
	{
		return new Font((name == null) ? DEFAULT_FONT_NAME : name,
						(style == null) ? DEFAULT_FONT_STYLE : style.getAwtStyle(),
						(size == 0) ? DEFAULT_FONT_SIZE : size);
	}

	//------------------------------------------------------------------

	public void applyFont(
		Component	component)
	{
		if (component != null)
		{
			Font currentFont = component.getFont();
			if (currentFont == null)
				currentFont = new Font(DEFAULT_FONT_NAME, DEFAULT_FONT_STYLE, DEFAULT_FONT_SIZE);

			String fontName = name;
			if (fontName == null)
			{
				fontName = currentFont.getFontName();
				if (fontName == null)
					fontName = DEFAULT_FONT_NAME;
			}

			int fontStyle = (style == null) ? 0 : style.getAwtStyle();
			if (style == null)
			{
				fontStyle = currentFont.getStyle();
				if (fontStyle < 0)
					fontStyle = DEFAULT_FONT_STYLE;
			}

			int fontSize = size;
			if (fontSize == 0)
			{
				fontSize = currentFont.getSize();
				if (fontSize <= 0)
					fontSize = DEFAULT_FONT_SIZE;
			}

			component.setFont(new Font(fontName, fontStyle, fontSize));
		}
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
