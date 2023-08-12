/*====================================================================*\

FButton.java

Button class.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.swing.button;

//----------------------------------------------------------------------


// IMPORTS


import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

import uk.blankaspect.ui.swing.font.FontKey;
import uk.blankaspect.ui.swing.font.FontUtils;

//----------------------------------------------------------------------


// BUTTON CLASS


public class FButton
	extends JButton
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public FButton(Action action)
	{
		super(action);
		FontUtils.setAppFont(FontKey.MAIN, this);
	}

	//------------------------------------------------------------------

	public FButton(String text)
	{
		super(text);
		FontUtils.setAppFont(FontKey.MAIN, this);
	}

	//------------------------------------------------------------------

	public FButton(String text,
				   Icon   icon)
	{
		super(text, icon);
		FontUtils.setAppFont(FontKey.MAIN, this);
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
