/*====================================================================*\

KeyAction.java

Class: an action that is associated with a key stroke.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.swing.action;

//----------------------------------------------------------------------


// IMPORTS


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

//----------------------------------------------------------------------


// CLASS: AN ACTION THAT IS ASSOCIATED WITH A KEY STROKE


public class KeyAction
	extends AbstractAction
{

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	ActionListener	listener;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	private KeyAction(
		String			command,
		ActionListener	listener)
	{
		this.listener = listener;
		putValue(ACTION_COMMAND_KEY, command);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static void create(
		JComponent	component,
		int			condition,
		Action		action)
	{
		create(component, condition, (KeyStroke)action.getValue(ACCELERATOR_KEY), action);
	}

	//------------------------------------------------------------------

	public static void create(
		JComponent	component,
		int			condition,
		KeyStroke	keyStroke,
		Action		action)
	{
		String command = action.getValue(ACTION_COMMAND_KEY).toString();
		component.getInputMap(condition).put(keyStroke, command);
		component.getActionMap().put(command, action);
	}

	//------------------------------------------------------------------

	public static void create(
		JComponent			component,
		int					condition,
		KeyActionPair...	keyActions)
	{
		for (KeyActionPair keyAction : keyActions)
			create(component, condition, keyAction.keyStroke, keyAction.action);
	}

	//------------------------------------------------------------------

	public static void create(
		JComponent						component,
		int								condition,
		List<? extends KeyActionPair>	keyActions)
	{
		for (KeyActionPair keyAction : keyActions)
			create(component, condition, keyAction.keyStroke, keyAction.action);
	}

	//------------------------------------------------------------------

	public static Action create(
		JComponent		component,
		int				condition,
		KeyStroke		keyStroke,
		String			command,
		ActionListener	listener)
	{
		Action action = new KeyAction(command, listener);
		create(component, condition, keyStroke, action);
		return action;
	}

	//------------------------------------------------------------------

	public static void create(
		JComponent			component,
		int					condition,
		ActionListener		listener,
		KeyCommandPair...	keyCommands)
	{
		for (KeyCommandPair keyCommand : keyCommands)
			create(component, condition, keyCommand.keyStroke, keyCommand.command, listener);
	}

	//------------------------------------------------------------------

	public static void create(
		JComponent						component,
		int								condition,
		ActionListener					listener,
		List<? extends KeyCommandPair>	keyCommands)
	{
		for (KeyCommandPair keyCommand : keyCommands)
			create(component, condition, keyCommand.keyStroke, keyCommand.command, listener);
	}

	//------------------------------------------------------------------

	public static KeyCommandPair command(
		KeyStroke	keyStroke,
		String		command)
	{
		return new KeyCommandPair(keyStroke, command);
	}

	//------------------------------------------------------------------

	public static KeyActionPair action(
		KeyStroke	keyStroke,
		Action		action)
	{
		return new KeyActionPair(keyStroke, action);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : ActionListener interface
////////////////////////////////////////////////////////////////////////

	@Override
	public void actionPerformed(
		ActionEvent	event)
	{
		event.setSource(null);
		listener.actionPerformed(event);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Member records
////////////////////////////////////////////////////////////////////////


	// RECORD: PAIRING OF A KEY STROKE AND A COMMAND


	public record KeyCommandPair(
		KeyStroke	keyStroke,
		String		command)
	{ }

	//==================================================================


	// RECORD: PAIRING OF A KEY STROKE AND AN ACTION


	public record KeyActionPair(
		KeyStroke	keyStroke,
		Action		action)
	{ }

	//==================================================================

}

//----------------------------------------------------------------------
