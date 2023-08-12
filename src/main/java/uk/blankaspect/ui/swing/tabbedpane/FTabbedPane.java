/*====================================================================*\

FTabbedPane.java

Tabbed pane class.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.swing.tabbedpane;

//----------------------------------------------------------------------


// IMPORTS


import javax.swing.JTabbedPane;

import uk.blankaspect.ui.swing.font.FontKey;
import uk.blankaspect.ui.swing.font.FontUtils;

//----------------------------------------------------------------------


// TABBED PANE CLASS


public class FTabbedPane
	extends JTabbedPane
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public FTabbedPane()
	{
		FontUtils.setAppFont(FontKey.MAIN, this);
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
