/*====================================================================*\

NonEditableTextAreaDialog.java

Non-editable text area dialog class.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.swing.dialog;

//----------------------------------------------------------------------


// IMPORTS


import java.awt.Window;

import java.awt.datatransfer.StringSelection;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import uk.blankaspect.common.exception.AppException;

import uk.blankaspect.ui.swing.action.KeyAction;

import uk.blankaspect.ui.swing.misc.GuiConstants;

//----------------------------------------------------------------------


// NON-EDITABLE TEXT AREA DIALOG CLASS


public class NonEditableTextAreaDialog
	extends AbstractNonEditableTextAreaDialog
	implements ActionListener
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	private static final	String	CLEAR_STR			= "Clear";
	private static final	String	COPY_STR			= "Copy";
	private static final	String	CLEAR_TOOLTIP_STR	= "Clear text (Alt+X)";
	private static final	String	COPY_TOOLTIP_STR	= "Copy text to clipboard (Alt+C)";
	private static final	String	CLIPBOARD_ERROR_STR	= "Clipboard error";

	// Commands
	private interface Command
	{
		String	CLEAR	= "clear";
		String	COPY	= "copy";
		String	CLOSE	= "close";
	}

	private static final	KeyAction.KeyCommandPair[]	KEY_COMMANDS	=
	{
		new KeyAction.KeyCommandPair(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), Command.CLOSE)
	};

	private static final	Map<String, CommandAction>	COMMANDS;

////////////////////////////////////////////////////////////////////////
//  Enumerated types
////////////////////////////////////////////////////////////////////////


	// ERROR IDENTIFIERS


	private enum ErrorId
		implements AppException.IId
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		CLIPBOARD_IS_UNAVAILABLE
		("The clipboard is currently unavailable.");

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private ErrorId(String message)
		{
			this.message = message;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : AppException.IId interface
	////////////////////////////////////////////////////////////////////

		public String getMessage()
		{
			return message;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	String	message;

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// COMMAND ACTION CLASS


	private static class CommandAction
		extends AbstractAction
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private CommandAction(String command,
							  String text,
							  int    mnemonicKey,
							  String tooltipStr)
		{
			// Call superclass constructor
			super(text);

			// Set action properties
			putValue(Action.ACTION_COMMAND_KEY, command);
			if (mnemonicKey != 0)
				putValue(Action.MNEMONIC_KEY, mnemonicKey);
			if (tooltipStr != null)
				putValue(Action.SHORT_DESCRIPTION, tooltipStr);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : ActionListener interface
	////////////////////////////////////////////////////////////////////

		public void actionPerformed(ActionEvent event)
		{
			listener.actionPerformed(event);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	ActionListener	listener;

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	protected NonEditableTextAreaDialog(Window owner,
										String titleStr,
										String key,
										int    numColumns,
										int    numRows,
										String text)
	{
		this(owner, titleStr, key, numColumns, numRows, text, false);
	}

	//------------------------------------------------------------------

	protected NonEditableTextAreaDialog(Window  owner,
										String  titleStr,
										String  key,
										int     numColumns,
										int     numRows,
										String  text,
										boolean canClear)
	{
		// Call superclass constructor
		super(owner, titleStr, key, numColumns, numRows, getCommands(canClear), Command.CLOSE, text);

		// Initialise instance variables
		this.canClear = canClear;

		// Add commands to action map
		KeyAction.create((JComponent)getContentPane(), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
						 this, KEY_COMMANDS);

		// Set action listener in commands
		for (String commandKey : COMMANDS.keySet())
			COMMANDS.get(commandKey).listener = this;

		// Show dialog
		setVisible(true);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	private static List<Action> getCommands(boolean canClear)
	{
		List<Action> commands = new ArrayList<>();
		if (canClear)
			commands.add(COMMANDS.get(Command.CLEAR));
		commands.add(COMMANDS.get(Command.COPY));
		commands.add(COMMANDS.get(Command.CLOSE));
		return commands;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : ActionListener interface
////////////////////////////////////////////////////////////////////////

	public void actionPerformed(ActionEvent event)
	{
		String command = event.getActionCommand();

		if (command.equals(Command.CLEAR))
			onClear();

		else if (command.equals(Command.COPY))
			onCopy();

		else if (command.equals(Command.CLOSE))
			onClose();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	@Override
	protected void updateComponents()
	{
		if (getTextLength() == 0)
		{
			COMMANDS.get(Command.COPY).setEnabled(false);
			getButton(Command.CLOSE).requestFocusInWindow();
		}
		else
			COMMANDS.get(Command.COPY).setEnabled(true);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public boolean isCleared()
	{
		return cleared;
	}

	//------------------------------------------------------------------

	private void onClear()
	{
		if (canClear)
		{
			getTextArea().setText(null);
			COMMANDS.get(Command.CLEAR).setEnabled(false);
			cleared = true;
			updateComponents();
		}
	}

	//------------------------------------------------------------------

	private void onCopy()
	{
		try
		{
			try
			{
				StringSelection selection = new StringSelection(getText());
				getToolkit().getSystemClipboard().setContents(selection, selection);
			}
			catch (IllegalStateException e)
			{
				throw new AppException(ErrorId.CLIPBOARD_IS_UNAVAILABLE, e);
			}
		}
		catch (AppException e)
		{
			JOptionPane.showMessageDialog(this, e, CLIPBOARD_ERROR_STR, JOptionPane.ERROR_MESSAGE);
		}
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Static initialiser
////////////////////////////////////////////////////////////////////////

	static
	{
		COMMANDS = new HashMap<>();
		COMMANDS.put(Command.CLEAR,
					 new CommandAction(Command.CLEAR, CLEAR_STR, KeyEvent.VK_X, CLEAR_TOOLTIP_STR));
		COMMANDS.put(Command.COPY,
					 new CommandAction(Command.COPY, COPY_STR, KeyEvent.VK_C, COPY_TOOLTIP_STR));
		COMMANDS.put(Command.CLOSE,
					 new CommandAction(Command.CLOSE, GuiConstants.CLOSE_STR, 0, null));
	}

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	boolean	canClear;
	private	boolean	cleared;

}

//----------------------------------------------------------------------
