/*====================================================================*\

AbstractSpinner.java

Abstract spinner class.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.swing.spinner;

//----------------------------------------------------------------------


// IMPORTS


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.KeyStroke;
import javax.swing.SpinnerModel;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import uk.blankaspect.common.string.StringUtils;

import uk.blankaspect.ui.swing.action.KeyAction;

import uk.blankaspect.ui.swing.modifiers.InputModifiers;

//----------------------------------------------------------------------


// ABSTRACT SPINNER CLASS


public abstract class AbstractSpinner
	extends JSpinner
	implements ActionListener, ChangeListener, FocusListener, MouseWheelListener
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	protected interface Command
	{
		String	DECREMENT	= "decrement.";
		String	INCREMENT	= "increment.";
		String	COMMIT_EDIT	= "commitEdit";
	}

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	protected	boolean	alwaysUpdate;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	protected AbstractSpinner(SpinnerModel model,
							  boolean      alwaysUpdate)
	{
		// Call superclass constructor
		super(model);

		// Initialise instance variables
		this.alwaysUpdate = alwaysUpdate;

		// Add listeners
		addChangeListener(this);
		addMouseWheelListener(this);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : ActionListener interface
////////////////////////////////////////////////////////////////////////

	@Override
	public void actionPerformed(ActionEvent event)
	{
		String command = event.getActionCommand();

		if (command.startsWith(Command.DECREMENT))
			onDecrement(StringUtils.removePrefix(command, Command.DECREMENT));

		else if (command.startsWith(Command.INCREMENT))
			onIncrement(StringUtils.removePrefix(command, Command.INCREMENT));

		else if (command.equals(Command.COMMIT_EDIT))
			onCommitEdit();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : ChangeListener interface
////////////////////////////////////////////////////////////////////////

	@Override
	public void stateChanged(ChangeEvent event)
	{
		updateEditorValue();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : FocusListener interface
////////////////////////////////////////////////////////////////////////

	@Override
	public void focusGained(FocusEvent event)
	{
		// do nothing
	}

	//------------------------------------------------------------------

	@Override
	public void focusLost(FocusEvent event)
	{
		updateValue();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Abstract methods
////////////////////////////////////////////////////////////////////////

	public abstract void updateValue();

	//------------------------------------------------------------------

	protected abstract void updateEditorValue();

	//------------------------------------------------------------------

	protected abstract void incrementValue(int increment);

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : MouseWheelListener interface
////////////////////////////////////////////////////////////////////////

	@Override
	public void mouseWheelMoved(MouseWheelEvent event)
	{
		if (getEditor().isFocusOwner())
		{
			int factor = getModifierFactor(InputModifiers.forEvent(event));
			if (factor != 0)
				incrementValue(factor * -event.getWheelRotation());
		}
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	protected int getModifierFactor(InputModifiers modifiers)
	{
		int factor = 0;
		switch (modifiers)
		{
			case NONE:
				factor = 1;
				break;

			case CTRL:
				factor = 10;
				break;

			case SHIFT:
				factor = 100;
				break;

			case CTRL_SHIFT:
				factor = 1000;
				break;

			default:
				// do nothing
				break;
		}
		return factor;
	}

	//------------------------------------------------------------------

	protected void addEditorActions()
	{
		addIncDecAction(KeyEvent.CTRL_DOWN_MASK);
		addIncDecAction(KeyEvent.SHIFT_DOWN_MASK);
		addIncDecAction(KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK);
	}

	//------------------------------------------------------------------

	private void addAction(int    keyCode,
						   int    modifiers,
						   String command)
	{
		KeyAction.create(getEditor(), JComponent.WHEN_FOCUSED, KeyStroke.getKeyStroke(keyCode, modifiers), command,
						 this);
	}

	//------------------------------------------------------------------

	private void addIncDecAction(int modifiers)
	{
		addAction(KeyEvent.VK_UP, modifiers, Command.INCREMENT + modifiers);
		addAction(KeyEvent.VK_DOWN, modifiers, Command.DECREMENT + modifiers);
	}

	//------------------------------------------------------------------

	private void onDecrement(String str)
	{
		incrementValue(-getModifierFactor(InputModifiers.forModifiers(Integer.parseInt(str))));
	}

	//------------------------------------------------------------------

	private void onIncrement(String str)
	{
		incrementValue(getModifierFactor(InputModifiers.forModifiers(Integer.parseInt(str))));
	}

	//------------------------------------------------------------------

	private void onCommitEdit()
	{
		updateValue();
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
