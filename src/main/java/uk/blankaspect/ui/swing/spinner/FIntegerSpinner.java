/*====================================================================*\

FIntegerSpinner.java

Integer spinner class.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.swing.spinner;

//----------------------------------------------------------------------


// IMPORTS


import uk.blankaspect.ui.swing.font.FontKey;
import uk.blankaspect.ui.swing.font.FontUtils;

//----------------------------------------------------------------------


// INTEGER SPINNER CLASS


public class FIntegerSpinner
	extends IntegerSpinner
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public FIntegerSpinner(int value,
						   int minValue,
						   int maxValue,
						   int maxLength)
	{
		this(value, minValue, maxValue, maxLength, false);
	}

	//------------------------------------------------------------------

	public FIntegerSpinner(int     value,
						   int     minValue,
						   int     maxValue,
						   int     maxLength,
						   boolean signed)
	{
		super(value, minValue, maxValue, maxLength, signed);
		FontUtils.setAppFont(FontKey.TEXT_FIELD, this);
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
