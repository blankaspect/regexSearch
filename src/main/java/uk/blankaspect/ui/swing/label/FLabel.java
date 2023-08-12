/*====================================================================*\

FLabel.java

Label class.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.swing.label;

//----------------------------------------------------------------------


// IMPORTS


import javax.swing.JLabel;

import uk.blankaspect.ui.swing.font.FontKey;
import uk.blankaspect.ui.swing.font.FontUtils;

//----------------------------------------------------------------------


// LABEL CLASS


public class FLabel
	extends JLabel
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public FLabel(String text)
	{
		super(text);
		FontUtils.setAppFont(FontKey.MAIN, this);
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
