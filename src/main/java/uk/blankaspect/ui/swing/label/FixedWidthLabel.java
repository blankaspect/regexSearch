/*====================================================================*\

FixedWidthLabel.java

Class: fixed-width label.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.swing.label;

//----------------------------------------------------------------------


// IMPORTS


import java.awt.Dimension;

import javax.swing.SwingConstants;

import uk.blankaspect.common.misc.MaxValueMap;

//----------------------------------------------------------------------


// CLASS: FIXED-WIDTH LABEL


public abstract class FixedWidthLabel
	extends FLabel
	implements MaxValueMap.IEntry
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	protected FixedWidthLabel(
		String	text)
	{
		super(text);
		setHorizontalAlignment(SwingConstants.TRAILING);
		MaxValueMap.add(getKey(), this);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Abstract methods
////////////////////////////////////////////////////////////////////////

	protected abstract String getKey();

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : MaxValueMap.IEntry interface
////////////////////////////////////////////////////////////////////////

	@Override
	public int getValue()
	{
		return getPreferredSize().width;
	}

	//------------------------------------------------------------------

	@Override
	public void setValue(
		int	value)
	{
		setPreferredSize(new Dimension(value, getPreferredSize().height));
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
