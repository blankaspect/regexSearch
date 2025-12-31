/*====================================================================*\

FPathnameField.java

Class: pathname field.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.regexsearch;

//----------------------------------------------------------------------


// IMPORTS


import java.io.File;

import uk.blankaspect.ui.swing.misc.GuiUtils;

import uk.blankaspect.ui.swing.textfield.PathnameField;

//----------------------------------------------------------------------


// CLASS: PATHNAME FIELD


class FPathnameField
	extends PathnameField
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	private static final	int	NUM_COLUMNS	= 40;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public FPathnameField()
	{
		super(NUM_COLUMNS);
		_init();
	}

	//------------------------------------------------------------------

	public FPathnameField(
		String	pathname)
	{
		super(pathname, NUM_COLUMNS);
		_init();
	}

	//------------------------------------------------------------------

	public FPathnameField(
		File	file)
	{
		super(file, NUM_COLUMNS);
		_init();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	private void _init()
	{
		AppFont.TEXT_FIELD.apply(this);
		GuiUtils.setTextComponentMargins(this);
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
