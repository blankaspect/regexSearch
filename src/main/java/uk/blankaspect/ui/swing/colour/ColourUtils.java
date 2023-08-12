/*====================================================================*\

ColourUtils.java

Class: colour-related utility methods.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.swing.colour;

//----------------------------------------------------------------------


// IMPORTS


import java.awt.Color;

import java.text.DecimalFormat;

import uk.blankaspect.common.exception.ValueOutOfBoundsException;

import uk.blankaspect.common.number.NumberUtils;

//----------------------------------------------------------------------


// CLASS: COLOUR-RELATED UTILITY METHODS


public class ColourUtils
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	public static final		int		MIN_ARGB_COMPONENT_VALUE	= 0;
	public static final		int		MAX_ARGB_COMPONENT_VALUE	= 255;

	public static final		int		RGB_MASK	= 0xFFFFFF;

	public static final		DecimalFormat	OPACITY_FORMAT	= new DecimalFormat("0.0###");

	public static final		String	HEX_FORMAT_PREFIX	= "#";

	public static final		String	OUT_VALUE_SEPARATOR		= ", ";
	public static final		String	VALUE_SEPARATOR_REGEX	= " *, *";

	private static final	double	MIN_OPACITY	= 0.0;
	private static final	double	MAX_OPACITY	= 1.0;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private ColourUtils()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static boolean isTransparent(Color colour)
	{
		return (colour.getAlpha() < MAX_ARGB_COMPONENT_VALUE);
	}

	//------------------------------------------------------------------

	public static double getOpacity(Color colour)
	{
		int alpha = colour.getAlpha();
		return (alpha < MAX_ARGB_COMPONENT_VALUE) ? (double)alpha / (double)MAX_ARGB_COMPONENT_VALUE : MAX_OPACITY;
	}

	//------------------------------------------------------------------

	public static double getBrightness(Color colour)
	{
		return getBrightness(colour.getRed(), colour.getGreen(), colour.getBlue());
	}

	//------------------------------------------------------------------

	public static double getBrightness(int rgb)
	{
		return getBrightness((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF);
	}

	//------------------------------------------------------------------

	public static double getBrightness(int red,
									   int green,
									   int blue)
	{
		double cMax = (red > green) ? red : green;
		if (cMax < blue)
			cMax = blue;
		return (cMax / (double)MAX_ARGB_COMPONENT_VALUE);
	}

	//------------------------------------------------------------------

	public static Color scaleBrightness(Color  colour,
										double factor)
	{
		float[] hsb = new float[3];
		Color.RGBtoHSB(colour.getRed(), colour.getGreen(), colour.getBlue(), hsb);
		return Color.getHSBColor(hsb[0], hsb[1], Math.min(Math.max(0.0f, hsb[2] * (float)factor), 1.0f));
	}

	//------------------------------------------------------------------

	public static Color copy(Color colour)
	{
		return new Color(colour.getRGB(), true);
	}

	//------------------------------------------------------------------

	public static Color opaque(Color colour)
	{
		return new Color(colour.getRGB());
	}

	//------------------------------------------------------------------

	public static Color invert(Color colour)
	{
		return new Color(MAX_ARGB_COMPONENT_VALUE - colour.getRed(), MAX_ARGB_COMPONENT_VALUE - colour.getGreen(),
						 MAX_ARGB_COMPONENT_VALUE - colour.getBlue(), colour.getAlpha());
	}

	//------------------------------------------------------------------

	public static Color blend(Color foreground,
							  Color background)
	{
		Color result = foreground;
		int alpha = foreground.getAlpha();
		if (alpha < MAX_ARGB_COMPONENT_VALUE)
		{
			double a1 = (double)alpha / (double)MAX_ARGB_COMPONENT_VALUE;
			double a2 = 1.0 - a1;
			int red   = (int)(a1 * (double)foreground.getRed())   + (int)(a2 * (double)background.getRed());
			int green = (int)(a1 * (double)foreground.getGreen()) + (int)(a2 * (double)background.getGreen());
			int blue  = (int)(a1 * (double)foreground.getBlue())  + (int)(a2 * (double)background.getBlue());
			result = new Color(red, green, blue);
		}
		return result;
	}

	//------------------------------------------------------------------

	public static Color interpolateHsb(Color  colour1,
									   Color  colour2,
									   double colour2Fraction)
	{
		float[] hsb1 = new float[3];
		Color.RGBtoHSB(colour1.getRed(), colour1.getGreen(), colour1.getBlue(), hsb1);

		float[] hsb2 = new float[3];
		Color.RGBtoHSB(colour2.getRed(), colour2.getGreen(), colour2.getBlue(), hsb2);

		float h = hsb1[0] + (float)colour2Fraction * (hsb2[0] - hsb1[0]);
		float s = hsb1[1] + (float)colour2Fraction * (hsb2[1] - hsb1[1]);
		float b = hsb1[2] + (float)colour2Fraction * (hsb2[2] - hsb1[2]);
		return Color.getHSBColor(h, s, b);
	}

	//------------------------------------------------------------------

	public static String colourToRgbString(int rgb)
	{
		StringBuilder buffer = new StringBuilder(32);
		buffer.append(rgb >> 16 & 0xFF);
		buffer.append(OUT_VALUE_SEPARATOR);
		buffer.append(rgb >> 8 & 0xFF);
		buffer.append(OUT_VALUE_SEPARATOR);
		buffer.append(rgb & 0xFF);
		int alpha = rgb >> 24 & 0xFF;
		if (alpha < MAX_ARGB_COMPONENT_VALUE)
		{
			buffer.append(OUT_VALUE_SEPARATOR);
			buffer.append(OPACITY_FORMAT.format((double)alpha / (double)MAX_ARGB_COMPONENT_VALUE));
		}
		return buffer.toString();
	}

	//------------------------------------------------------------------

	public static String colourToRgbString(Color colour)
	{
		return colourToRgbString(colour.getRGB());
	}

	//------------------------------------------------------------------

	public static String colourToHexString(Color colour)
	{
		boolean lowerCase = NumberUtils.setUpper();
		String str = new String(HEX_FORMAT_PREFIX + NumberUtils.byteToHexString(colour.getRed())
														+ NumberUtils.byteToHexString(colour.getGreen())
														+ NumberUtils.byteToHexString(colour.getBlue()));
		if (lowerCase)
			NumberUtils.setLower();
		return str;
	}

	//------------------------------------------------------------------

	/**
	 * @throws IllegalArgumentException   if {@code str} is malformed.
	 * @throws ValueOutOfBoundsException  if an RGB value in {@code str} is outside the range [0..255].
	 */

	public static Color parseColour(String str)
	{
		Color colour = null;
		if (str.startsWith(HEX_FORMAT_PREFIX))
		{
			// Strip the hex-format prefix
			str = str.substring(HEX_FORMAT_PREFIX.length());

			// Validate length of hex string
			if (str.length() != 6)
				throw new IllegalArgumentException("Malformed string");

			// Parse colour components
			int red = Integer.parseInt(str.substring(0, 2), 16);
			int green = Integer.parseInt(str.substring(2, 4), 16);
			int blue = Integer.parseInt(str.substring(4, 6), 16);

			// Create colour
			colour = new Color(red, green, blue);
		}
		else
		{
			// Split string into colour components
			String[] strs = str.split(VALUE_SEPARATOR_REGEX, -1);

			// Assume no opacity
			double opacity = MAX_OPACITY;

			// If string has opacity component, parse it
			if (strs.length == 4)
			{
				opacity = Double.parseDouble(strs[3]);
				if ((opacity < MIN_OPACITY) || (opacity > MAX_OPACITY))
					throw new ValueOutOfBoundsException("opacity");
			}
			else if (strs.length != 3)
				throw new IllegalArgumentException("Malformed string");

			// Parse red component
			int red = Integer.parseInt(strs[0]);
			if ((red < MIN_ARGB_COMPONENT_VALUE) || (red > MAX_ARGB_COMPONENT_VALUE))
				throw new ValueOutOfBoundsException("red");

			// Parse green component
			int green = Integer.parseInt(strs[1]);
			if ((green < MIN_ARGB_COMPONENT_VALUE) || (green > MAX_ARGB_COMPONENT_VALUE))
				throw new ValueOutOfBoundsException("green");

			// Parse blue component
			int blue = Integer.parseInt(strs[2]);
			if ((blue < MIN_ARGB_COMPONENT_VALUE) || (blue > MAX_ARGB_COMPONENT_VALUE))
				throw new ValueOutOfBoundsException("blue");

			// Create colour
			colour = new Color(red, green, blue, (int)(opacity * (double)MAX_ARGB_COMPONENT_VALUE));
		}

		return colour;
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
