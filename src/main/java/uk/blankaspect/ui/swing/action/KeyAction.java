/*====================================================================*\

KeyAction.java

Key action class.

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


// KEY ACTION CLASS


public class KeyAction
	extends AbstractAction
{

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// KEY-COMMAND PAIR CLASS


	public static class KeyCommandPair
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		public KeyCommandPair(KeyStroke keyStroke,
							  String    command)
		{
			this.keyStroke = keyStroke;
			this.command = command;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		public	KeyStroke	keyStroke;
		public	String		command;

	}

	//==================================================================


	// KEY-ACTION PAIR CLASS


	public static class KeyActionPair
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		public KeyActionPair(KeyStroke keyStroke,
							 Action    action)
		{
			this.keyStroke = keyStroke;
			this.action = action;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		public	KeyStroke	keyStroke;
		public	Action		action;

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	private KeyAction(String         command,
					  ActionListener listener)
	{
		this.listener = listener;
		putValue(ACTION_COMMAND_KEY, command);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static void create(JComponent component,
							  int        condition,
							  Action     action)
	{
		create(component, condition, (KeyStroke)action.getValue(ACCELERATOR_KEY), action);
	}

	//------------------------------------------------------------------

	public static void create(JComponent component,
							  int        condition,
							  KeyStroke  keyStroke,
							  Action     action)
	{
		String command = action.getValue(ACTION_COMMAND_KEY).toString();
		component.getInputMap(condition).put(keyStroke, command);
		component.getActionMap().put(command, action);
	}

	//------------------------------------------------------------------

	public static void create(JComponent       component,
							  int              condition,
							  KeyActionPair... keyActionPairs)
	{
		for (KeyActionPair keyActionPair : keyActionPairs)
			create(component, condition, keyActionPair.keyStroke, keyActionPair.action);
	}

	//------------------------------------------------------------------

	public static void create(JComponent          component,
							  int                 condition,
							  List<KeyActionPair> keyActionPairs)
	{
		for (KeyActionPair keyActionPair : keyActionPairs)
			create(component, condition, keyActionPair.keyStroke, keyActionPair.action);
	}

	//------------------------------------------------------------------

	public static Action create(JComponent     component,
								int            condition,
								KeyStroke      keyStroke,
								String         command,
								ActionListener listener)
	{
		Action action = new KeyAction(command, listener);
		create(component, condition, keyStroke, action);
		return action;
	}

	//------------------------------------------------------------------

	public static void create(JComponent        component,
							  int               condition,
							  ActionListener    listener,
							  KeyCommandPair... keyCommandPairs)
	{
		for (KeyCommandPair keyCommandPair : keyCommandPairs)
			create(component, condition, keyCommandPair.keyStroke, keyCommandPair.command, listener);
	}

	//------------------------------------------------------------------

	public static void create(JComponent           component,
							  int                  condition,
							  ActionListener       listener,
							  List<KeyCommandPair> keyCommandPairs)
	{
		for (KeyCommandPair keyCommandPair : keyCommandPairs)
			create(component, condition, keyCommandPair.keyStroke, keyCommandPair.command, listener);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : ActionListener interface
////////////////////////////////////////////////////////////////////////

	public void actionPerformed(ActionEvent event)
	{
		event.setSource(null);
		listener.actionPerformed(event);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	ActionListener	listener;

}

//----------------------------------------------------------------------
