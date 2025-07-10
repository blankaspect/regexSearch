/*====================================================================*\

LinkUnlinkButton.java

Class: link/unlink button.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.swing.button;

//----------------------------------------------------------------------


// IMPORTS


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import java.awt.geom.RoundRectangle2D;

import javax.swing.JToggleButton;

import uk.blankaspect.ui.swing.misc.GuiConstants;
import uk.blankaspect.ui.swing.misc.GuiUtils;

//----------------------------------------------------------------------


// CLASS: LINK/UNLINK BUTTON


public class LinkUnlinkButton
	extends JToggleButton
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	public enum Orientation
	{
		HORIZONTAL,
		VERTICAL
	}

	private static final	int		MARGIN_D1	= 4;
	private static final	int		MARGIN_D2	= 6;

	private static final	double	LINK_STROKE_WIDTH	= 2.0;
	private static final	double	LINK_RADIUS			= 3.5;
	private static final	double	LINK_LINE_LENGTH	= 3.0;
	private static final	double	LINK_ARC_SIZE		= 2.0 * LINK_RADIUS;
	private static final	double	LINK_SIZE_D1		= 2.0 * LINK_RADIUS + LINK_LINE_LENGTH;
	private static final	double	LINK_SIZE_D2		= 2.0 * LINK_RADIUS;
	private static final	double	LINK_GAP			= 3.0;
	private static final	double	LINK_OVERLAP		= 3.0;

	private static final	int		SIZE_D1	=
			(int)Math.ceil(2.0 * (double)MARGIN_D1 + 2.0 * LINK_SIZE_D1 + LINK_STROKE_WIDTH + LINK_GAP);
	private static final	int		SIZE_D2	=
			(int)Math.ceil(2.0 * (double)MARGIN_D2 + LINK_SIZE_D2 + LINK_STROKE_WIDTH);

	private static final	Color	BACKGROUND_COLOUR	= new Color(224, 232, 240);
	private static final	Color	LINK_COLOUR			= new Color(80, 80, 80);

	private static final	Color	BORDER_COLOUR			= new Color(160, 176, 192);
	private static final	Color	DISABLED_COLOUR			= new Color(176, 176, 176);
	private static final	Color	FOCUSED_BORDER_COLOUR1	= Color.WHITE;
	private static final	Color	FOCUSED_BORDER_COLOUR2	= Color.BLACK;

	private static final	String	LINK_STR	= "Link ";
	private static final	String	UNLINK_STR	= "Unlink ";

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	Orientation	orientation;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public LinkUnlinkButton(
		Orientation	orientation)
	{
		// Initialise instance variables
		this.orientation = orientation;

		// Set properties
		setBorder(null);
		Dimension size = switch (orientation)
		{
			case HORIZONTAL -> new Dimension(SIZE_D1, SIZE_D2);
			case VERTICAL   -> new Dimension(SIZE_D2, SIZE_D1);
		};
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static LinkUnlinkButton horizontal()
	{
		return new LinkUnlinkButton(Orientation.HORIZONTAL);
	}

	//------------------------------------------------------------------

	public static LinkUnlinkButton vertical()
	{
		return new LinkUnlinkButton(Orientation.VERTICAL);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	@Override
	public String getToolTipText()
	{
		String text = super.getToolTipText();
		return (text == null) ? null : (isSelected() ? UNLINK_STR : LINK_STR) + text;
	}

	//------------------------------------------------------------------

	@Override
	protected void paintComponent(
		Graphics	gr)
	{
		// Create copy of graphics context
		Graphics2D gr2d = GuiUtils.copyGraphicsContext(gr);

		// Get dimensions
		int width = getWidth();
		int height = getHeight();

		// Fill interior
		gr2d.setColor(isEnabled() ? BACKGROUND_COLOUR : getBackground());
		gr2d.fillRect(0, 0, width, height);

		// Set rendering hints
		gr2d.setRenderingHint(RenderingHints.KEY_RENDERING,    RenderingHints.VALUE_RENDER_QUALITY);
		gr2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// Calculate coordinates and dimensions of links
		double size_d1 = 0.0;
		double size_d2 = 0.0;
		switch (orientation)
		{
			case HORIZONTAL:
				size_d1 = (double)width;
				size_d2 = (double)height;
				break;

			case VERTICAL:
				size_d1 = (double)height;
				size_d2 = (double)width;
				break;
		};

		double a1 = 0.5 * (size_d1 - 2.0 * LINK_SIZE_D1 - LINK_GAP - 1.0);
		double a2 = a1 + LINK_SIZE_D1 + LINK_GAP;
		if (isSelected())
		{
			double d = 0.5 * (LINK_GAP + LINK_OVERLAP);
			a1 += d;
			a2 -= d;
		}
		double b = 0.5 * (size_d2 - LINK_SIZE_D2 - 1.0);

		double x1 = 0.0;
		double x2 = 0.0;
		double y1 = 0.0;
		double y2 = 0.0;
		double w = 0.0;
		double h = 0.0;
		switch (orientation)
		{
			case HORIZONTAL:
			{
				x1 = a1;
				x2 = a2;
				y1 = y2 = b;
				w = LINK_SIZE_D1;
				h = LINK_SIZE_D2;
				break;
			}

			case VERTICAL:
			{
				x1 = x2 = b;
				y1 = a1;
				y2 = a2;
				w = LINK_SIZE_D2;
				h = LINK_SIZE_D1;
				break;
			}
		}

		// Draw links
		gr2d.setStroke(new BasicStroke((float)LINK_STROKE_WIDTH));
		gr2d.setColor(isEnabled() ? LINK_COLOUR : DISABLED_COLOUR);
		gr2d.draw(new RoundRectangle2D.Double(x1, y1, w, h, LINK_ARC_SIZE, LINK_ARC_SIZE));
		gr2d.draw(new RoundRectangle2D.Double(x2, y2, w, h, LINK_ARC_SIZE, LINK_ARC_SIZE));

		// Draw border
		gr2d.setStroke(new BasicStroke(1.0f));
		gr2d.setColor(isEnabled() ? BORDER_COLOUR : DISABLED_COLOUR);
		gr2d.drawRect(0, 0, width - 1, height - 1);
		if (isFocusOwner())
		{
			gr2d.setColor(FOCUSED_BORDER_COLOUR1);
			gr2d.drawRect(1, 1, width - 3, height - 3);

			gr2d.setStroke(GuiConstants.BASIC_DASH);
			gr2d.setColor(FOCUSED_BORDER_COLOUR2);
			gr2d.drawRect(1, 1, width - 3, height - 3);
		}
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
