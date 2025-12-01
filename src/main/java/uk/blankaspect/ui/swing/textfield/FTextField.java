/*====================================================================*\

FTextField.java

Text field class.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.swing.textfield;

//----------------------------------------------------------------------


// IMPORTS


import javax.swing.JTextField;

import javax.swing.text.Document;

import uk.blankaspect.ui.swing.font.FontKey;
import uk.blankaspect.ui.swing.font.FontUtils;

import uk.blankaspect.ui.swing.misc.GuiUtils;

//----------------------------------------------------------------------


// TEXT FIELD CLASS


public class FTextField
	extends JTextField
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public FTextField(int numColumns)
	{
		this(null, numColumns);
	}

	//------------------------------------------------------------------

	public FTextField(String text,
					  int    numColumns)
	{
		super(text, numColumns);
		FontUtils.setAppFont(FontKey.TEXT_FIELD, this);
		GuiUtils.setTextComponentMargins(this);
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

}

//----------------------------------------------------------------------
