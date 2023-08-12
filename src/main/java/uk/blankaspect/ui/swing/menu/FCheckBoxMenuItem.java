/*====================================================================*\

FCheckBoxMenuItem.java

Check box menu item class.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.swing.menu;

//----------------------------------------------------------------------


// IMPORTS


import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;

import uk.blankaspect.ui.swing.font.FontKey;
import uk.blankaspect.ui.swing.font.FontUtils;

//----------------------------------------------------------------------


// CHECK BOX MENU ITEM CLASS


public class FCheckBoxMenuItem
	extends JCheckBoxMenuItem
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public FCheckBoxMenuItem(Action action)
	{
		super(action);
		FontUtils.setAppFont(FontKey.MAIN, this);
	}

	//------------------------------------------------------------------

	public FCheckBoxMenuItem(Action  action,
							 boolean selected)
	{
		this(action);
		setSelected(selected);
	}

	//------------------------------------------------------------------

	public FCheckBoxMenuItem(Action action,
							 int    mnemonic)
	{
		this(action);
		setMnemonic(mnemonic);
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
