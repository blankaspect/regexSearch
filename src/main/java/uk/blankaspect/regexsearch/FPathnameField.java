/*====================================================================*\

FPathnameField.java

Pathname field class.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.regexsearch;

//----------------------------------------------------------------------


// IMPORTS


import java.io.File;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.blankaspect.common.swing.misc.GuiUtils;

import uk.blankaspect.common.swing.textfield.PathnameField;

//----------------------------------------------------------------------


// PATHNAME FIELD CLASS


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

	public FPathnameField(String pathname)
	{
		super(pathname, NUM_COLUMNS);
		_init();
	}

	//------------------------------------------------------------------

	public FPathnameField(File file)
	{
		super(file, NUM_COLUMNS);
		_init();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static void addObserver(String         key,
								   FPathnameField field)
	{
		List<FPathnameField> fields = observers.get(key);
		if (fields == null)
		{
			fields = new ArrayList<>();
			observers.put(key, fields);
		}
		fields.add(field);
		AppConfig.INSTANCE.addShowUnixPathnamesObserver(field);
	}

	//------------------------------------------------------------------

	public static void removeObservers(String key)
	{
		List<FPathnameField> fields = observers.get(key);
		if (fields != null)
		{
			for (FPathnameField field : fields)
				AppConfig.INSTANCE.removeShowUnixPathnamesObserver(field);
			observers.remove(key);
		}
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	private void _init()
	{
		AppFont.TEXT_FIELD.apply(this);
		GuiUtils.setTextComponentMargins(this);
		setUnixStyle(AppConfig.INSTANCE.isShowUnixPathnames());
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class variables
////////////////////////////////////////////////////////////////////////

	private static	Map<String, List<FPathnameField>>	observers	= new HashMap<>();

}

//----------------------------------------------------------------------
