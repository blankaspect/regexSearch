/*====================================================================*\

Command.java

Command class.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.swing.action;

//----------------------------------------------------------------------


// IMPORTS


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.beans.PropertyChangeListener;

import java.util.Hashtable;
import java.util.Map;

import javax.swing.Action;

import javax.swing.event.SwingPropertyChangeSupport;

//----------------------------------------------------------------------


// COMMAND CLASS


public class Command
	implements Action
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	public static final		String	ENABLED_KEY	= "enabled";

	private static final	int	PROPERTIES_INITIAL_CAPACITY	= 8;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public Command(ActionListener listener)
	{
		this.listener = listener;
		enabled = true;
		properties = new Hashtable<>(PROPERTIES_INITIAL_CAPACITY);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : Action interface
////////////////////////////////////////////////////////////////////////

	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		if (changeSupport == null)
			changeSupport = new SwingPropertyChangeSupport(this);
		changeSupport.addPropertyChangeListener(listener);
	}

	//------------------------------------------------------------------

	public Object getValue(String key)
	{
		return key.equals(ENABLED_KEY) ? enabled : properties.get(key);
	}

	//------------------------------------------------------------------

	public boolean isEnabled()
	{
		return enabled;
	}

	//------------------------------------------------------------------

	public void putValue(String key,
						 Object value)
	{
		Object oldValue = getValue(key);
		if (key.equals(ENABLED_KEY))
		{
			if (!(value instanceof Boolean))
				value = Boolean.FALSE;
			enabled = (Boolean)value;
		}
		else
		{
			if (value == null)
				properties.remove(key);
			else
				properties.put(key, value);
		}

		if ((changeSupport != null) &&
			 ((value == null) ? (oldValue != null) : !value.equals(oldValue)))
			changeSupport.firePropertyChange(key, oldValue, value);
	}

	//------------------------------------------------------------------

	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		if (changeSupport != null)
			changeSupport.removePropertyChangeListener(listener);
	}

	//------------------------------------------------------------------

	public void setEnabled(boolean enabled)
	{
		boolean oldValue = this.enabled;
		if (this.enabled != enabled)
		{
			this.enabled = enabled;
			if (changeSupport != null)
				changeSupport.firePropertyChange(ENABLED_KEY, oldValue, enabled);
		}
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : ActionListener interface
////////////////////////////////////////////////////////////////////////

	public void actionPerformed(ActionEvent event)
	{
		listener.actionPerformed(event);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	ActionListener				listener;
	private	boolean						enabled;
	private	Map<String, Object>			properties;
	private	SwingPropertyChangeSupport	changeSupport;

}

//----------------------------------------------------------------------
