/*====================================================================*\

Colours.java

Colours class.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.swing.colour;

//----------------------------------------------------------------------


// IMPORTS


import java.awt.Color;

import uk.blankaspect.common.misc.IStringKeyed;

import uk.blankaspect.common.string.StringUtils;

//----------------------------------------------------------------------


// COLOURS CLASS


public class Colours
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	// Colours
	public static final		Color	BACKGROUND						= new Color(248, 248, 240);
	public static final		Color	FOREGROUND						= Color.BLACK;
	public static final		Color	SELECTION_BACKGROUND			= new Color(224, 224, 220);
	public static final		Color	SELECTION_FOREGROUND			= FOREGROUND;
	public static final		Color	FOCUSED_SELECTION_BACKGROUND	= new Color(252, 216, 104);
	public static final		Color	FOCUSED_SELECTION_FOREGROUND	= FOREGROUND;
	public static final		Color	LINE_BORDER						= new Color(200, 200, 200);
	public static final		Color	FOCUSED_CELL_BORDER				= new Color(224, 96, 32);

	// Keys
	public static final		String	BACKGROUND_KEY						= "background";
	public static final		String	FOREGROUND_KEY						= "foreground";
	public static final		String	SELECTION_BACKGROUND_KEY			= "selectionBackground";
	public static final		String	SELECTION_FOREGROUND_KEY			= "selectionForeground";
	public static final		String	FOCUSED_SELECTION_BACKGROUND_KEY	= "focusedSelectionBackground";
	public static final		String	FOCUSED_SELECTION_FOREGROUND_KEY	= "focusedSelectionForeground";
	public static final		String	FOCUSED_CELL_BORDER_KEY				= "focusedCellBorder";

	public static final		String	DRAG_BAR_KEY						= "dragBar";
	public static final		String	GRID_KEY							= "grid";
	public static final		String	HEADER_BACKGROUND_KEY				= "headerBackground";
	public static final		String	FOCUSED_HEADER_BACKGROUND_KEY		= "focusedHeaderBackground";
	public static final		String	HEADER_BORDER_KEY					= "headerBorder";
	public static final		String	FOCUSED_HEADER_BORDER_KEY			= "focusedHeaderBorder";
	public static final		String	EDITABLE_CELL_BACKGROUND_KEY		= "editableCellBackground";

	private static final	char	KEY_SEPARATOR_CHAR	= '.';
	private static final	String	KEY_SEPARATOR		= Character.toString(KEY_SEPARATOR_CHAR);

////////////////////////////////////////////////////////////////////////
//  Enumerated types
////////////////////////////////////////////////////////////////////////


	// ENUMERATION: LIST COLOURS


	public enum List
		implements IStringKeyed
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		BACKGROUND                   (BACKGROUND_KEY,                   Colours.BACKGROUND),
		FOREGROUND                   (FOREGROUND_KEY,                   Colours.FOREGROUND),
		SELECTION_BACKGROUND         (SELECTION_BACKGROUND_KEY,         Colours.SELECTION_BACKGROUND),
		SELECTION_FOREGROUND         (SELECTION_FOREGROUND_KEY,         Colours.SELECTION_FOREGROUND),
		FOCUSED_SELECTION_BACKGROUND (FOCUSED_SELECTION_BACKGROUND_KEY, Colours.FOCUSED_SELECTION_BACKGROUND),
		FOCUSED_SELECTION_FOREGROUND (FOCUSED_SELECTION_FOREGROUND_KEY, Colours.FOCUSED_SELECTION_FOREGROUND),
		FOCUSED_CELL_BORDER          (FOCUSED_CELL_BORDER_KEY,          Colours.FOCUSED_CELL_BORDER),
		DRAG_BAR                     (DRAG_BAR_KEY,                     Colours.FOCUSED_CELL_BORDER);

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	String	key;
		private	Color	colour;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private List(String key,
					 Color  colour)
		{
			this.key = key;
			this.colour = colour;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Class methods
	////////////////////////////////////////////////////////////////////

		public static Color getColour(String key)
		{
			for (List value : values())
			{
				if (value.key.equals(key))
					return value.colour;
			}
			return null;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : IStringKeyed interface
	////////////////////////////////////////////////////////////////////

		@Override
		public String getKey()
		{
			return key;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		public String getPrefixedKey()
		{
			return (classToPrefix(List.class) + KEY_SEPARATOR + key);
		}

		//--------------------------------------------------------------

		public Color getColour()
		{
			return colour;
		}

		//--------------------------------------------------------------

	}

	//==================================================================


	// ENUMERATION: TABLE COLOURS


	public enum Table
		implements IStringKeyed
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		BACKGROUND                   (BACKGROUND_KEY,                   Colours.BACKGROUND),
		FOREGROUND                   (FOREGROUND_KEY,                   Colours.FOREGROUND),
		SELECTION_BACKGROUND         (SELECTION_BACKGROUND_KEY,         Colours.SELECTION_BACKGROUND),
		SELECTION_FOREGROUND         (SELECTION_FOREGROUND_KEY,         Colours.SELECTION_FOREGROUND),
		FOCUSED_SELECTION_BACKGROUND (FOCUSED_SELECTION_BACKGROUND_KEY, Colours.FOCUSED_SELECTION_BACKGROUND),
		FOCUSED_SELECTION_FOREGROUND (FOCUSED_SELECTION_FOREGROUND_KEY, Colours.FOCUSED_SELECTION_FOREGROUND),
		GRID                         (GRID_KEY,                         new Color(224, 224, 224)),
		HEADER_BACKGROUND1           (HEADER_BACKGROUND_KEY,            new Color(216, 224, 216)),
		HEADER_BACKGROUND2           (HEADER_BACKGROUND_KEY,            new Color(216, 216, 216)),
		FOCUSED_HEADER_BACKGROUND1   (FOCUSED_HEADER_BACKGROUND_KEY,    new Color(186, 216, 186)),
		FOCUSED_HEADER_BACKGROUND2   (FOCUSED_HEADER_BACKGROUND_KEY,    new Color(240, 224, 176)),
		HEADER_BORDER1               (HEADER_BORDER_KEY,                new Color(168, 192, 168)),
		HEADER_BORDER2               (HEADER_BORDER_KEY,                new Color(184, 184, 184)),
		FOCUSED_HEADER_BORDER1       (FOCUSED_HEADER_BORDER_KEY,        new Color(152, 176, 152)),
		FOCUSED_HEADER_BORDER2       (FOCUSED_HEADER_BORDER_KEY,        new Color(224, 176, 128)),
		FOCUSED_CELL_BORDER          (FOCUSED_CELL_BORDER_KEY,          Colours.FOCUSED_CELL_BORDER),
		EDITABLE_CELL_BACKGROUND     (EDITABLE_CELL_BACKGROUND_KEY,     new Color(255, 240, 160));

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	String	key;
		private	Color	colour;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private Table(String key,
					  Color  colour)
		{
			this.key = key;
			this.colour = colour;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Class methods
	////////////////////////////////////////////////////////////////////

		public static Color getColour(String key)
		{
			for (Table value : values())
			{
				if (value.key.equals(key))
					return value.colour;
			}
			return null;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : IStringKeyed interface
	////////////////////////////////////////////////////////////////////

		@Override
		public String getKey()
		{
			return key;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		public String getPrefixedKey()
		{
			return (classToPrefix(Table.class) + KEY_SEPARATOR + key);
		}

		//--------------------------------------------------------------

		public Color getColour()
		{
			return colour;
		}

		//--------------------------------------------------------------

	}

	//==================================================================


	// ENUMERATION: TEXT AREA COLOURS


	public enum TextArea
		implements IStringKeyed
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		BACKGROUND                   (BACKGROUND_KEY,                   Colours.BACKGROUND),
		FOREGROUND                   (FOREGROUND_KEY,                   Colours.FOREGROUND),
		SELECTION_BACKGROUND         (SELECTION_BACKGROUND_KEY,         Colours.SELECTION_BACKGROUND),
		SELECTION_FOREGROUND         (SELECTION_FOREGROUND_KEY,         Colours.SELECTION_FOREGROUND),
		FOCUSED_SELECTION_BACKGROUND (FOCUSED_SELECTION_BACKGROUND_KEY, Colours.FOCUSED_SELECTION_BACKGROUND),
		FOCUSED_SELECTION_FOREGROUND (FOCUSED_SELECTION_FOREGROUND_KEY, Colours.FOCUSED_SELECTION_FOREGROUND);

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	String	key;
		private	Color	colour;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private TextArea(String key,
						 Color  colour)
		{
			this.key = key;
			this.colour = colour;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Class methods
	////////////////////////////////////////////////////////////////////

		public static Color getColour(String key)
		{
			for (TextArea value : values())
			{
				if (value.key.equals(key))
					return value.colour;
			}
			return null;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : IStringKeyed interface
	////////////////////////////////////////////////////////////////////

		@Override
		public String getKey()
		{
			return key;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		public String getPrefixedKey()
		{
			return (classToPrefix(TextArea.class) + KEY_SEPARATOR + key);
		}

		//--------------------------------------------------------------

		public Color getColour()
		{
			return colour;
		}

		//--------------------------------------------------------------

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static Color getColour(String key)
	{
		Color colour = null;
		String[] keyParts = StringUtils.splitAtFirst(key, KEY_SEPARATOR_CHAR);
		if (keyParts[1] != null)
		{
			if (keyParts[0].equals(classToPrefix(List.class)))
				colour = List.getColour(keyParts[1]);

			else if (keyParts[0].equals(classToPrefix(Table.class)))
				colour = Table.getColour(keyParts[1]);

			else if (keyParts[0].equals(classToPrefix(TextArea.class)))
				colour = TextArea.getColour(keyParts[1]);
		}
		return colour;
	}

	//------------------------------------------------------------------

	private static String classToPrefix(Class<?> cls)
	{
		String name = cls.getSimpleName();
		return StringUtils.firstCharToLowerCase(name);
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
