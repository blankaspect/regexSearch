/*====================================================================*\

BooleanComboBox.java

Boolean combo box class.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.swing.combobox;

//----------------------------------------------------------------------


// IMPORTS


import javax.swing.JComboBox;

import uk.blankaspect.common.misc.NoYes;

import uk.blankaspect.ui.swing.font.FontKey;
import uk.blankaspect.ui.swing.font.FontUtils;

//----------------------------------------------------------------------


// BOOLEAN COMBO BOX CLASS


public class BooleanComboBox
	extends JComboBox<NoYes>
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public BooleanComboBox()
	{
		super(NoYes.values());
		FontUtils.setAppFont(FontKey.COMBO_BOX, this);
		setRenderer(new ComboBoxRenderer<>(this));
	}

	//------------------------------------------------------------------

	public BooleanComboBox(boolean value)
	{
		this();
		setSelectedValue(value);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public boolean getSelectedValue()
	{
		return ((NoYes)getSelectedItem()).toBoolean();
	}

	//------------------------------------------------------------------

	public void setSelectedValue(boolean value)
	{
		setSelectedItem(NoYes.forBoolean(value));
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
