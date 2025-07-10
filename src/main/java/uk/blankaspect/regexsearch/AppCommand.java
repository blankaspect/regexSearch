/*====================================================================*\

AppCommand.java

Application command enumeration.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.regexsearch;

//----------------------------------------------------------------------


// IMPORTS


import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.KeyStroke;

import uk.blankaspect.ui.swing.action.Command;

//----------------------------------------------------------------------


// APPLICATION COMMAND ENUMERATION


enum AppCommand
	implements Action
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	// Commands

	IMPORT_FILE
	(
		"importFile"
	),

	OPEN_SEARCH_PARAMETERS
	(
		"openSearchParams",
		"Open search parameters" + AppConstants.ELLIPSIS_STR
	),

	SAVE_SEARCH_PARAMETERS
	(
		"saveSearchParams",
		"Save search parameters" + AppConstants.ELLIPSIS_STR
	),

	EXIT
	(
		"exit",
		"Exit"
	),

	EDIT_FILE
	(
		"editFile",
		"Edit file",
		KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.CTRL_DOWN_MASK)
	),

	EDIT_FILE_DEFERRED
	(
		"editFileDeferred",
		"Edit file, deferred",
		KeyStroke.getKeyStroke(KeyEvent.VK_D, KeyEvent.CTRL_DOWN_MASK)
	),

	SEARCH
	(
		"search",
		"Search",
		KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK)
	),

	COPY_RESULTS
	(
		"copyResults",
		"Copy results",
		KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK)
	),

	SAVE_RESULTS
	(
		"saveResults",
		"Save results",
		KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK)
	),

	VIEW_SAVED_RESULTS
	(
		"viewSavedResults",
		"View saved results"
	),

	TOGGLE_CONTROL_DIALOG
	(
		"toggleControlDialog",
		"",
		KeyStroke.getKeyStroke(KeyEvent.VK_M, KeyEvent.CTRL_DOWN_MASK)
	),

	EDIT_PREFERENCES
	(
		"editPreferences",
		"Preferences" + AppConstants.ELLIPSIS_STR
	);

	//------------------------------------------------------------------

	// Property keys
	interface Property
	{
		String	FILE	= "file";
	}

	// Other constants
	public static final	String	HIDE_CONTROL_DIALOG_STR	= "Hide control dialog";
	public static final	String	SHOW_CONTROL_DIALOG_STR	= "Show control dialog";

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	private AppCommand(String key)
	{
		command = new Command(this);
		putValue(Action.ACTION_COMMAND_KEY, key);
	}

	//------------------------------------------------------------------

	private AppCommand(String key,
					   String name)
	{
		this(key);
		putValue(Action.NAME, name);
	}

	//------------------------------------------------------------------

	private AppCommand(String    key,
					   String    name,
					   KeyStroke acceleratorKey)
	{
		this(key, name);
		putValue(Action.ACCELERATOR_KEY, acceleratorKey);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : Action interface
////////////////////////////////////////////////////////////////////////

	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		command.addPropertyChangeListener(listener);
	}

	//------------------------------------------------------------------

	public Object getValue(String key)
	{
		return command.getValue(key);
	}

	//------------------------------------------------------------------

	public boolean isEnabled()
	{
		return command.isEnabled();
	}

	//------------------------------------------------------------------

	public void putValue(String key,
						 Object value)
	{
		command.putValue(key, value);
	}

	//------------------------------------------------------------------

	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		command.removePropertyChangeListener(listener);
	}

	//------------------------------------------------------------------

	public void setEnabled(boolean enabled)
	{
		command.setEnabled(enabled);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : ActionListener interface
////////////////////////////////////////////////////////////////////////

	public void actionPerformed(ActionEvent event)
	{
		RegexSearchApp.INSTANCE.getMainWindow().executeCommand(this);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public void setSelected(boolean selected)
	{
		putValue(Action.SELECTED_KEY, selected);
	}

	//------------------------------------------------------------------

	public void setName(String name)
	{
		putValue(Action.NAME, name);
	}

	//------------------------------------------------------------------

	public void execute()
	{
		actionPerformed(null);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	Command	command;

}

//----------------------------------------------------------------------
