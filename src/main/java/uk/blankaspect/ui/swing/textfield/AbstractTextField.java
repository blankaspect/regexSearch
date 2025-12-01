/*====================================================================*\

AbstractTextField.java

Abstract text field class.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.swing.textfield;

//----------------------------------------------------------------------


// IMPORTS


import java.awt.Color;

import javax.swing.JTextField;

import javax.swing.text.Document;

//----------------------------------------------------------------------


// ABSTRACT TEXT FIELD CLASS


public abstract class AbstractTextField
	extends JTextField
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	private static final	Color	DEFAULT_INVALID_BACKGROUND_COLOUR	= new Color(240, 192, 192);

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	protected AbstractTextField()
	{
		invalidBackgroundColour = DEFAULT_INVALID_BACKGROUND_COLOUR;
	}

	//------------------------------------------------------------------

	protected AbstractTextField(int columns)
	{
		super(columns);
		invalidBackgroundColour = DEFAULT_INVALID_BACKGROUND_COLOUR;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	@Override
	public Color getBackground()
	{
		return invalid
				? invalidBackgroundColour
				: (isEnabled() || (disabledBackgroundColour == null))
						? super.getBackground()
						: disabledBackgroundColour;
	}

	//------------------------------------------------------------------

	@Override
	public void setText(String text)
	{
		invalid = false;
		super.setText(text);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public boolean isEmpty()
	{
		Document document = getDocument();
		return (document == null) ? true : (document.getLength() == 0);
	}

	//------------------------------------------------------------------

	public boolean isInvalid()
	{
		return invalid;
	}

	//------------------------------------------------------------------

	public Color setDisabledBackgroundColour()
	{
		return disabledBackgroundColour;
	}

	//------------------------------------------------------------------

	public Color getInvalidBackgroundColour()
	{
		return invalidBackgroundColour;
	}

	//------------------------------------------------------------------

	public void setInvalid(boolean invalid)
	{
		if (this.invalid != invalid)
		{
			this.invalid = invalid;
			repaint();
		}
	}

	//------------------------------------------------------------------

	public void setDisabledBackgroundColour(Color colour)
	{
		if ((colour == null) ? (disabledBackgroundColour != null)
							 : !colour.equals(disabledBackgroundColour))
		{
			disabledBackgroundColour = colour;
			repaint();
		}
	}

	//------------------------------------------------------------------

	public void setInvalidBackgroundColour(Color colour)
	{
		if ((colour == null) ? (invalidBackgroundColour != null)
							 : !colour.equals(invalidBackgroundColour))
		{
			invalidBackgroundColour = colour;
			repaint();
		}
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	boolean	invalid;
	private	Color	disabledBackgroundColour;
	private	Color	invalidBackgroundColour;

}

//----------------------------------------------------------------------
