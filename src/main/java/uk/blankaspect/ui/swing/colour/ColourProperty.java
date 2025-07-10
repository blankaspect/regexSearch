/*====================================================================*\

ColourProperty.java

Class: colour property.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.swing.colour;

//----------------------------------------------------------------------


// IMPORTS


import java.awt.Color;

import uk.blankaspect.common.exception.AppException;

import uk.blankaspect.common.property.Property;

//----------------------------------------------------------------------


// CLASS: COLOUR PROPERTY


public abstract class ColourProperty
	extends Property.SimpleProperty<Color>
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public ColourProperty(String key)
	{
		// Call superclass constructor
		super(key);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static Color parseColour(Property.Input input)
		throws IllegalValueException, ValueOutOfBoundsException
	{
		try
		{
			return ColourUtils.parseColour(input.getValue());
		}
		catch (IllegalArgumentException e)
		{
			throw new IllegalValueException(input);
		}
		catch (uk.blankaspect.common.exception2.ValueOutOfBoundsException e)
		{
			throw new ValueOutOfBoundsException(input);
		}
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	@Override
	public void parse(Input input)
		throws AppException
	{
		value = parseColour(input);
	}

	//------------------------------------------------------------------

	@Override
	public String toString()
	{
		return ColourUtils.colourToRgbString(value);
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
