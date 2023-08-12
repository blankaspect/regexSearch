/*====================================================================*\

ColourSampleIcon.java

Colour sample icon class.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.swing.icon;

//----------------------------------------------------------------------


// IMPORTS


import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;

//----------------------------------------------------------------------


// COLOUR SAMPLE ICON CLASS


public class ColourSampleIcon
	implements Icon
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	private static final	Color	BORDER_COLOUR			= Color.GRAY;
	private static final	Color	DISABLED_BORDER_COLOUR	= Color.LIGHT_GRAY;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public ColourSampleIcon(int width,
							int height)
	{
		this.width = width;
		this.height = height;
	}

	//------------------------------------------------------------------

	public ColourSampleIcon(int   width,
							int   height,
							Color colour)
	{
		this(width, height);
		this.colour = colour;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : Icon interface
////////////////////////////////////////////////////////////////////////

	public int getIconWidth()
	{
		return width;
	}

	//------------------------------------------------------------------

	public int getIconHeight()
	{
		return height;
	}

	//------------------------------------------------------------------

	public void paintIcon(Component component,
						  Graphics  gr,
						  int       x,
						  int       y)
	{
		gr.setColor(component.isEnabled() ? BORDER_COLOUR : DISABLED_BORDER_COLOUR);
		gr.drawRect(x, y, width - 1, height - 1);
		gr.setColor(component.isEnabled() ? (colour == null) ? component.getForeground() : colour
										  : component.getBackground());
		gr.fillRect(x + 1, y + 1, width - 2, height - 2);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	int		width;
	private	int		height;
	private	Color	colour;

}

//----------------------------------------------------------------------
