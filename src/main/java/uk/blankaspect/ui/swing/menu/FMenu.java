/*====================================================================*\

FMenu.java

Menu class.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.swing.menu;

//----------------------------------------------------------------------


// IMPORTS


import javax.swing.Action;
import javax.swing.JMenu;

import uk.blankaspect.ui.swing.font.FontKey;
import uk.blankaspect.ui.swing.font.FontUtils;

//----------------------------------------------------------------------


// MENU CLASS


public class FMenu
	extends JMenu
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public FMenu(String text)
	{
		super(text);
		FontUtils.setAppFont(FontKey.MAIN, this);
	}

	//------------------------------------------------------------------

	public FMenu(Action action)
	{
		super(action);
		FontUtils.setAppFont(FontKey.MAIN, this);
	}

	//------------------------------------------------------------------

	public FMenu(String text,
				 int    mnemonic)
	{
		this(text);
		setMnemonic(mnemonic);
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
