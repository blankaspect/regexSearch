/*====================================================================*\

FontUtils.java

Class: font-related utility methods.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.swing.font;

//----------------------------------------------------------------------


// IMPORTS


import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;

import uk.blankaspect.common.misc.IStringKeyed;

//----------------------------------------------------------------------


// CLASS: FONT-RELATED UTILITY METHODS


public class FontUtils
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	private static final	String	CLASS_NAME_APP_FONT		= "AppFont";
	private static final	String	METHOD_NAME_GET_FONT	= "getFont";
	private static final	String	METHOD_NAME_APPLY		= "apply";

////////////////////////////////////////////////////////////////////////
//  Class variables
////////////////////////////////////////////////////////////////////////

	private static	Class<?>	appFontClass;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	private FontUtils()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static int getCharWidth(
		int			codePoint,
		FontMetrics	fontMetrics)
	{
		return fontMetrics.charWidth(fontMetrics.getFont().canDisplay(codePoint)
														? codePoint
														: fontMetrics.getFont().getMissingGlyphCode());
	}

	//------------------------------------------------------------------

	public static int getBaselineOffset(
		int			height,
		FontMetrics	fontMetrics)
	{
		return getBaselineOffset(height, fontMetrics, false);
	}

	//------------------------------------------------------------------

	public static int getBaselineOffset(
		int			height,
		FontMetrics	fontMetrics,
		boolean		roundUp)
	{
		return ((height - fontMetrics.getAscent() - fontMetrics.getDescent() + (roundUp ? 1 : 0)) / 2
																						+ fontMetrics.getAscent());
	}

	//------------------------------------------------------------------

	public static void setAppFontClass(
		Class<?>	cls)
	{
		appFontClass = cls;
	}

	//------------------------------------------------------------------

	public static boolean isAppFont(
		String	key)
	{
		try
		{
			Class<?> cls = getAppFontClass();
			if (cls.isEnum())
			{
				for (Object obj : cls.getEnumConstants())
				{
					if (((IStringKeyed)obj).getKey().equals(key))
						return true;
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}

	//------------------------------------------------------------------

	public static Font getAppFont(
		String	key)
	{
		Font font = null;
		try
		{
			Class<?> cls = getAppFontClass();
			if (cls.isEnum())
			{
				for (Object obj : cls.getEnumConstants())
				{
					if (((IStringKeyed)obj).getKey().equals(key))
						font = (Font)cls.getMethod(METHOD_NAME_GET_FONT).invoke(obj);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return font;
	}

	//------------------------------------------------------------------

	public static void setAppFont(
		String		key,
		Component	component)
	{
		try
		{
			Class<?> cls = getAppFontClass();
			if (cls.isEnum())
			{
				for (Object obj : cls.getEnumConstants())
				{
					if (((IStringKeyed)obj).getKey().equals(key))
					{
						cls.getMethod(METHOD_NAME_APPLY, Component.class).invoke(obj, component);
						break;
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	//------------------------------------------------------------------

	private static Class<?> getAppFontClass()
		throws Exception
	{
		return (appFontClass == null) ? Class.forName(CLASS_NAME_APP_FONT) : appFontClass;
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
