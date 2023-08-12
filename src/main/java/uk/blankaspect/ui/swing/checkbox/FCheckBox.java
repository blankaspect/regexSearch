/*====================================================================*\

FCheckBox.java

Check box class.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.swing.checkbox;

//----------------------------------------------------------------------


// IMPORTS


import javax.swing.BorderFactory;
import javax.swing.JCheckBox;

import uk.blankaspect.ui.swing.font.FontKey;
import uk.blankaspect.ui.swing.font.FontUtils;

//----------------------------------------------------------------------


// CHECK BOX CLASS


public class FCheckBox
	extends JCheckBox
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	private static final	int	ICON_TEXT_GAP	= 6;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public FCheckBox(String text)
	{
		this(text, false);
	}

	//------------------------------------------------------------------

	public FCheckBox(String  text,
					 boolean noVerticalBorder)
	{
		super(text);
		FontUtils.setAppFont(FontKey.MAIN, this);
		setBorder(noVerticalBorder ? null : BorderFactory.createEmptyBorder(2, 0, 2, 0));
		setIconTextGap(ICON_TEXT_GAP);
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
