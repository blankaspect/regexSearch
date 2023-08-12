/*====================================================================*\

TitledBorder.java

Class: titled border.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.swing.border;

//----------------------------------------------------------------------


// IMPORTS


import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;

import javax.swing.border.AbstractBorder;

import uk.blankaspect.ui.swing.colour.Colours;

import uk.blankaspect.ui.swing.font.FontKey;
import uk.blankaspect.ui.swing.font.FontUtils;

import uk.blankaspect.ui.swing.text.TextRendering;

//----------------------------------------------------------------------


// CLASS: TITLED BORDER


public class TitledBorder
	extends AbstractBorder
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	public static final		Color	DEFAULT_BORDER_COLOUR			= Colours.LINE_BORDER;
	public static final		Color	DEFAULT_TITLE_BORDER_COLOUR		= new Color(216, 200, 128);
	public static final		Color	DEFAULT_TITLE_BACKGROUND_COLOUR	= new Color(248, 240, 200);
	public static final		Color	TITLE_TEXT_COLOUR				= Colours.FOREGROUND;

	private static final	int		TITLE_HORIZONTAL_MARGIN	= 5;
	private static final	int		TITLE_VERTICAL_MARGIN	= 2;

	private static final	int		DEFAULT_PADDING	= 6;

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	String	text;
	private	Font	font;
	private	boolean	fullWidth;
	private	Color	borderColour;
	private	Color	titleBorderColour;
	private	Color	titleBackgroundColour;
	private	Color	titleForegroundColour;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public TitledBorder(
		String	text)
	{
		// Call alternative constructor
		this(text, DEFAULT_BORDER_COLOUR, DEFAULT_TITLE_BORDER_COLOUR, DEFAULT_TITLE_BACKGROUND_COLOUR, TITLE_TEXT_COLOUR);
	}

	//------------------------------------------------------------------

	public TitledBorder(
		String	text,
		Color	borderColour)
	{
		// Call alternative constructor
		this(text, borderColour, DEFAULT_TITLE_BORDER_COLOUR, DEFAULT_TITLE_BACKGROUND_COLOUR, TITLE_TEXT_COLOUR);
	}

	//------------------------------------------------------------------

	public TitledBorder(
		String	text,
		Color	borderColour,
		Color	titleBorderColour,
		Color	titleBackgroundColour,
		Color	titleForegroundColour)
	{
		// Initialise instance variables
		this.text = text;
		this.borderColour = borderColour;
		this.titleBorderColour = titleBorderColour;
		this.titleBackgroundColour = titleBackgroundColour;
		this.titleForegroundColour = titleForegroundColour;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static TitledBorder setPaddedBorder(
		JComponent	component,
		String		text)
	{
		return setPaddedBorder(component, text, DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING);
	}

	//------------------------------------------------------------------

	public static TitledBorder setPaddedBorder(
		JComponent	component,
		String		text,
		int			padding)
	{
		return setPaddedBorder(component, text, padding, padding, padding, padding);
	}

	//------------------------------------------------------------------

	public static TitledBorder setPaddedBorder(
		JComponent	component,
		String		text,
		int			vertical,
		int			horizontal)
	{
		return setPaddedBorder(component, text, vertical, horizontal, vertical, horizontal);
	}

	//------------------------------------------------------------------

	public static TitledBorder setPaddedBorder(
		JComponent	component,
		String		text,
		int			top,
		int			left,
		int			bottom,
		int			right)
	{
		TitledBorder titledBorder = new TitledBorder(text);
		component.setBorder(BorderFactory.createCompoundBorder(titledBorder,
															   BorderFactory.createEmptyBorder(top, left, bottom, right)));
		return titledBorder;
	}

	//------------------------------------------------------------------

	public static void setPaddedBorder(
		JComponent		component,
		TitledBorder	titledBorder)
	{
		setPaddedBorder(component, titledBorder, DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING);
	}

	//------------------------------------------------------------------

	public static void setPaddedBorder(
		JComponent		component,
		TitledBorder	titledBorder,
		int				padding)
	{
		setPaddedBorder(component, titledBorder, padding, padding, padding, padding);
	}

	//------------------------------------------------------------------

	public static void setPaddedBorder(
		JComponent		component,
		TitledBorder	titledBorder,
		int				vertical,
		int				horizontal)
	{
		setPaddedBorder(component, titledBorder, vertical, horizontal, vertical, horizontal);
	}

	//------------------------------------------------------------------

	public static void setPaddedBorder(
		JComponent		component,
		TitledBorder	titledBorder,
		int				top,
		int				left,
		int				bottom,
		int				right)
	{
		component.setBorder(BorderFactory.createCompoundBorder(titledBorder,
															   BorderFactory.createEmptyBorder(top, left, bottom, right)));
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	@Override
	public Insets getBorderInsets(
		Component	component)
	{
		return getBorderInsets(component, new Insets(0, 0, 0, 0));
	}

	//------------------------------------------------------------------

	@Override
	public Insets getBorderInsets(
		Component	component,
		Insets		insets)
	{
		insets.top = 2 * TITLE_VERTICAL_MARGIN + component.getFontMetrics(getFont()).getHeight();
		insets.bottom = 1;
		insets.left = 1;
		insets.right = 1;
		return insets;
	}

	//------------------------------------------------------------------

	@Override
	public boolean isBorderOpaque()
	{
		return true;
	}

	//------------------------------------------------------------------

	@Override
	public void paintBorder(
		Component	component,
		Graphics	gr,
		int			x,
		int			y,
		int			width,
		int			height)
	{
		// Create copy of graphics context
		gr = gr.create();

		// Set font
		gr.setFont(getFont());

		// Set rendering hints for text antialiasing and fractional metrics
		if (gr instanceof Graphics2D gr2d)
			TextRendering.setHints(gr2d);

		// Get dimensions of text and title
		FontMetrics fontMetrics = gr.getFontMetrics();
		int textWidth = fontMetrics.stringWidth(text);
		int titleWidth = fullWidth ? width : Math.min(width, 2 * TITLE_HORIZONTAL_MARGIN + textWidth);
		int titleHeight = Math.min(height, 2 * TITLE_VERTICAL_MARGIN + fontMetrics.getAscent() + fontMetrics.getDescent());

		// Fill component background of title
		gr.setColor(component.getBackground());
		gr.fillRect(x, y, width, titleHeight);

		// Fill background of title
		gr.setColor(titleBackgroundColour);
		gr.fillRect(x, y, titleWidth, titleHeight);

		// Draw text
		int textX = fullWidth ? x + (width - textWidth) / 2 : x + TITLE_HORIZONTAL_MARGIN;
		gr.setColor(titleForegroundColour);
		gr.drawString(text, textX, TITLE_VERTICAL_MARGIN + fontMetrics.getAscent());

		// Draw title border
		gr.setColor(titleBorderColour);
		gr.drawRect(x, y, x + titleWidth - 1, y + titleHeight - 1);

		// Draw outer border
		gr.setColor(borderColour);
		gr.drawRect(x, y, x + width - 1, y + height - 1);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public Color getBorderColour()
	{
		return borderColour;
	}

	//------------------------------------------------------------------

	public Color getTitleBorderColour()
	{
		return titleBorderColour;
	}

	//------------------------------------------------------------------

	public Color getTitleBackgroundColour()
	{
		return titleBackgroundColour;
	}

	//------------------------------------------------------------------

	public Color getTitleForegroundColour()
	{
		return titleForegroundColour;
	}

	//------------------------------------------------------------------

	public Dimension getTitleSize(
		Component	component)
	{
		Font font = component.getFont();
		if (font == null)
			font = getFont();
		FontMetrics fontMetrics = component.getFontMetrics(font);
		return new Dimension(2 * TITLE_HORIZONTAL_MARGIN + fontMetrics.stringWidth(text),
							 2 * TITLE_VERTICAL_MARGIN + fontMetrics.getAscent() + fontMetrics.getDescent());
	}

	//------------------------------------------------------------------

	public void setFont(
		Font	font)
	{
		this.font = font;
	}

	//------------------------------------------------------------------

	public void setFullWidth(
		boolean	fullWidth)
	{
		this.fullWidth = fullWidth;
	}

	//------------------------------------------------------------------

	protected Font getFont()
	{
		if (font != null)
			return font;

		Font font0 = FontUtils.getAppFont(FontKey.MAIN);
		return (font0 == null) ? new JLabel().getFont() : font0;
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
