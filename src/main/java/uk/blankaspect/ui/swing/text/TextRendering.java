/*====================================================================*\

TextRendering.java

Class: text rendering.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.swing.text;

//----------------------------------------------------------------------


// IMPORTS


import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;

import java.util.Map;

import java.util.stream.Stream;

import uk.blankaspect.common.misc.IStringKeyed;

//----------------------------------------------------------------------


// CLASS: TEXT RENDERING


public class TextRendering
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	private static final	String	HINTS_DESKTOP_PROPERTY_KEY	= "awt.font.desktophints";

////////////////////////////////////////////////////////////////////////
//  Class variables
////////////////////////////////////////////////////////////////////////

	private static	Antialiasing		antialiasing		= Antialiasing.DEFAULT;
	private static	FractionalMetrics	fractionalMetrics	= FractionalMetrics.DEFAULT;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	private TextRendering()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static Antialiasing getAntialiasing()
	{
		return antialiasing;
	}

	//------------------------------------------------------------------

	public static FractionalMetrics getFractionalMetrics()
	{
		return fractionalMetrics;
	}

	//------------------------------------------------------------------

	public static void setAntialiasing(
		Antialiasing	value)
	{
		if (value != null)
			antialiasing = value;
	}

	//------------------------------------------------------------------

	public static void setFractionalMetrics(
		FractionalMetrics	value)
	{
		if (value != null)
			fractionalMetrics = value;
	}

	//------------------------------------------------------------------

	public static void setHints(
		Graphics2D	gr)
	{
		gr.setRenderingHint(Antialiasing.getHintKey(), antialiasing.getHintValue());
		gr.setRenderingHint(FractionalMetrics.getHintKey(), fractionalMetrics.getHintValue());
	}

	//------------------------------------------------------------------

	public static Object getDesktopHint(
		Object	key)
	{
		Object hints = Toolkit.getDefaultToolkit().getDesktopProperty(HINTS_DESKTOP_PROPERTY_KEY);
		return (hints instanceof Map) ? ((Map<?, ?>)hints).get(key) : null;
	}

	//------------------------------------------------------------------

	/**
	 * Gets the text-antialiasing hint from the map of AWT desktop hints and, if the map contains such a hint, sets its
	 * value on the specified graphics context.
	 *
	 * @param gr
	 *          the graphics context on which the value of the text-antialiasing hint will be set.
	 */

	public static void setTextAntialiasingHint(
		Graphics2D	gr)
	{
		Object hints = Toolkit.getDefaultToolkit().getDesktopProperty(HINTS_DESKTOP_PROPERTY_KEY);
		if (hints instanceof Map<?, ?> map)
		{
			Object hint = map.get(RenderingHints.KEY_TEXT_ANTIALIASING);
			if (hint != null)
				gr.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, hint);
		}
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Enumerated types
////////////////////////////////////////////////////////////////////////


	// ENUMERATION: ANTIALIASING


	public enum Antialiasing
		implements IStringKeyed
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		DEFAULT
		(
			"default",
			"Default",
			RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT
		),

		NONE
		(
			"none",
			"None",
			RenderingHints.VALUE_TEXT_ANTIALIAS_OFF
		),

		STANDARD
		(
			"standard",
			"Standard",
			RenderingHints.VALUE_TEXT_ANTIALIAS_ON
		),

		SUBPIXEL_H_RGB
		(
			"subpixelHRgb",
			"Subpixel, horizontal RGB",
			RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB
		),

		SUBPIXEL_H_BGR
		(
			"subpixelHBgr",
			"Subpixel, horizontal BGR",
			RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HBGR
		),

		SUBPIXEL_V_RGB
		(
			"subpixelVRgb",
			"Subpixel, vertical RGB",
			RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_VRGB
		),

		SUBPIXEL_V_BGR
		(
			"subpixelVBgr",
			"Subpixel, vertical BGR",
			RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_VBGR
		);

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	String	key;
		private	String	text;
		private	Object	hintValue;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private Antialiasing(
			String	key,
			String	text,
			Object	hintValue)
		{
			this.key = key;
			this.text = text;
			this.hintValue = hintValue;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Class methods
	////////////////////////////////////////////////////////////////////

		public static RenderingHints.Key getHintKey()
		{
			return RenderingHints.KEY_TEXT_ANTIALIASING;
		}

		//--------------------------------------------------------------

		public static Antialiasing forKey(
			String	key)
		{
			return Stream.of(values())
							.filter(value -> value.key.equals(key))
							.findFirst()
							.orElse(null);
		}

		//--------------------------------------------------------------

		public static Antialiasing forHintValue(
			Object	hintValue)
		{
			return Stream.of(values())
							.filter(value -> value.hintValue.equals(hintValue))
							.findFirst()
							.orElse(null);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : IStringKeyed interface
	////////////////////////////////////////////////////////////////////

		@Override
		public String getKey()
		{
			return key;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		public String toString()
		{
			return text;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		public Object getHintValue()
		{
			if (this == DEFAULT)
			{
				Object value = getDesktopHint(RenderingHints.KEY_TEXT_ANTIALIASING);
				if (value != null)
					return value;
			}
			return hintValue;
		}

		//--------------------------------------------------------------

	}

	//==================================================================


	// ENUMERATION: FONT FRACTIONAL METRICS


	public enum FractionalMetrics
		implements IStringKeyed
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		DEFAULT
		(
			"default",
			"Default",
			RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT
		),

		OFF
		(
			"off",
			"Off",
			RenderingHints.VALUE_FRACTIONALMETRICS_OFF
		),

		ON
		(
			"on",
			"On",
			RenderingHints.VALUE_FRACTIONALMETRICS_ON
		);

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	String	key;
		private	String	text;
		private	Object	hintValue;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private FractionalMetrics(
			String	key,
			String	text,
			Object	hintValue)
		{
			this.key = key;
			this.text = text;
			this.hintValue = hintValue;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Class methods
	////////////////////////////////////////////////////////////////////

		public static RenderingHints.Key getHintKey()
		{
			return RenderingHints.KEY_FRACTIONALMETRICS;
		}

		//--------------------------------------------------------------

		public static FractionalMetrics forKey(
			String	key)
		{
			return Stream.of(values())
							.filter(value -> value.key.equals(key))
							.findFirst()
							.orElse(null);
		}

		//--------------------------------------------------------------

		public static FractionalMetrics forHintValue(
			Object	hintValue)
		{
			return Stream.of(values())
							.filter(value -> value.hintValue.equals(hintValue))
							.findFirst()
							.orElse(null);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : IStringKeyed interface
	////////////////////////////////////////////////////////////////////

		@Override
		public String getKey()
		{
			return key;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		public String toString()
		{
			return text;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		public Object getHintValue()
		{
			if (this == DEFAULT)
			{
				Object value = getDesktopHint(RenderingHints.KEY_FRACTIONALMETRICS);
				if (value != null)
					return value;
			}
			return hintValue;
		}

		//--------------------------------------------------------------

	}

	//==================================================================

}

//----------------------------------------------------------------------
