/*====================================================================*\

AppConstants.java

Application constants interface.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.regexsearch;

//----------------------------------------------------------------------


// IMPORTS


import java.awt.Color;
import java.awt.Insets;

import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import uk.blankaspect.ui.swing.action.KeyAction;

import uk.blankaspect.ui.swing.colour.Colours;

//----------------------------------------------------------------------


// APPLICATION CONSTANTS INTERFACE


interface AppConstants
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	// Component constants
	Insets	COMPONENT_INSETS	= new Insets(2, 3, 2, 3);

	// Strings
	String	ELLIPSIS_STR		= "...";
	String	OK_STR				= "OK";
	String	CANCEL_STR			= "Cancel";
	String	CONTINUE_STR		= "Continue";
	String	REPLACE_STR			= "Replace";
	String	ALREADY_EXISTS_STR	= "\nThe file already exists.\nDo you want to replace it?";
	String	XML_VERSION_STR		= "1.0";
	String	CLIPBOARD_STR		= "// Clipboard //";

	// Filename extensions
	String	XML_FILENAME_EXTENSION	= ".xml";

	// File-filter descriptions
	String	XML_FILES_STR	= "XML files";

	// Colours
	Color	TEXT_COLOUR					= Colours.FOREGROUND;
	Color	DISABLED_TEXT_COLOUR		= new Color(144, 144, 144);
	Color	BACKGROUND_COLOUR			= Colours.BACKGROUND;
	Color	DISABLED_BACKGROUND_COLOUR	= new Color(220, 220, 212);

	// List editor command map
	KeyStroke	LE_KEY_COMMIT			=
							KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.CTRL_DOWN_MASK);
	KeyStroke	LE_KEY_DELETE			=
							KeyStroke.getKeyStroke(KeyEvent.VK_DELETE,
												   KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK);
	KeyStroke	LE_KEY_SELECT_PREVIOUS	=
							KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, KeyEvent.CTRL_DOWN_MASK);
	KeyStroke	LE_KEY_SELECT_NEXT		=
							KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, KeyEvent.CTRL_DOWN_MASK);

	KeyAction.KeyCommandPair[]	LIST_EDITOR_KEY_COMMANDS	=
	{
		new KeyAction.KeyCommandPair(LE_KEY_COMMIT,            ListEditor.Command.COMMIT),
		new KeyAction.KeyCommandPair(LE_KEY_DELETE,            ListEditor.Command.DELETE),
		new KeyAction.KeyCommandPair(LE_KEY_SELECT_PREVIOUS,   ListEditor.Command.SELECT_PREVIOUS),
		new KeyAction.KeyCommandPair(LE_KEY_SELECT_NEXT,       ListEditor.Command.SELECT_NEXT)
	};

}

//----------------------------------------------------------------------
