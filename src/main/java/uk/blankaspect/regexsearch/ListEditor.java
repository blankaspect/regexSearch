/*====================================================================*\

ListEditor.java

List editor class.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.regexsearch;

//----------------------------------------------------------------------


// IMPORTS


import java.awt.FontMetrics;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import uk.blankaspect.common.string.StringUtils;

//----------------------------------------------------------------------


// LIST EDITOR CLASS


public class ListEditor
	implements ActionListener
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	public interface Command
	{
		String	COMMIT			= "commit";
		String	DELETE			= "delete";
		String	SELECT_PREVIOUS	= "selectPrevious";
		String	SELECT_NEXT		= "selectNext";
		String	SELECT_ITEM		= "selectItem";
	}

	private static final	String	DELETE_ITEM_STR				= "Delete item";
	private static final	String	SELECT_PREVIOUS_ITEM_STR	= "Select previous item";
	private static final	String	SELECT_NEXT_ITEM_STR		= "Select next item";

////////////////////////////////////////////////////////////////////////
//  Member interfaces
////////////////////////////////////////////////////////////////////////


	// TEXT MODEL INTERFACE


	public interface ITextModel
	{

	////////////////////////////////////////////////////////////////////
	//  Methods
	////////////////////////////////////////////////////////////////////

		String getText();

		//--------------------------------------------------------------

		String toActionText(String      str,
							FontMetrics fontMetrics);

		//--------------------------------------------------------------

		void setText(String text);

		//--------------------------------------------------------------

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Member classes : inner classes
////////////////////////////////////////////////////////////////////////


	// COMMAND ACTION CLASS


	private class CommandAction
		extends AbstractAction
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private CommandAction(String command,
							  String text)
		{
			super(text);
			putValue(Action.ACTION_COMMAND_KEY, command);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : ActionListener interface
	////////////////////////////////////////////////////////////////////

		@Override
		public void actionPerformed(ActionEvent event)
		{
			ListEditor.this.actionPerformed(event);
		}

		//--------------------------------------------------------------

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public ListEditor(int        maxNumItems,
					  ITextModel textModel)
	{
		// Initialise instance variables
		items = new ArrayList<>();
		itemIndex = -1;
		this.maxNumItems = maxNumItems;
		this.textModel = textModel;
		changeListeners = new ArrayList<>();

		// Initialise actions
		actionMap = new ActionMap();
		addAction(Command.DELETE,          DELETE_ITEM_STR);
		addAction(Command.SELECT_PREVIOUS, SELECT_PREVIOUS_ITEM_STR);
		addAction(Command.SELECT_NEXT,     SELECT_NEXT_ITEM_STR);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : ActionListener interface
////////////////////////////////////////////////////////////////////////

	@Override
	public void actionPerformed(ActionEvent event)
	{
		String command = event.getActionCommand();

		if (command.startsWith(Command.SELECT_ITEM))
			onSelectItem(Integer.parseInt(StringUtils.removePrefix(command, Command.SELECT_ITEM)));
		else
		{
			switch (command)
			{
				case Command.COMMIT          -> onCommit();
				case Command.DELETE          -> onDelete();
				case Command.SELECT_PREVIOUS -> onSelectPrevious();
				case Command.SELECT_NEXT     -> onSelectNext();
			}
		}
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public int getMaxNumItems()
	{
		return maxNumItems;
	}

	//------------------------------------------------------------------

	public boolean isEmpty()
	{
		return items.isEmpty();
	}

	//------------------------------------------------------------------

	public List<String> getItems()
	{
		updateList();
		return new ArrayList<>(items);
	}

	//------------------------------------------------------------------

	public int getItemIndex()
	{
		return itemIndex;
	}

	//------------------------------------------------------------------

	public void setItems(List<String> items)
	{
		this.items = (items == null) ? new ArrayList<>() : new ArrayList<>(items);
		itemIndex = -2;
		setItemIndex(0);
	}

	//------------------------------------------------------------------

	public void setItemIndex(int index)
	{
		int oldIndex = itemIndex;
		itemIndex = Math.min(Math.max(0, index), items.size() - 1);
		if (itemIndex != oldIndex)
		{
			textModel.setText(getItem());
			updateActions();
		}
	}

	//------------------------------------------------------------------

	public boolean isTextChanged()
	{
		String text = textModel.getText();
		String item = getItem();
		return (text == null) ? (item != null) : !text.equals(item);
	}

	//------------------------------------------------------------------

	public boolean updateList()
	{
		boolean modified = false;
		String text = textModel.getText();
		if ((text != null) && isTextChanged())
		{
			// Insert current text at beginning of list
			items.add(0, text);
			itemIndex = 0;
			modified = true;

			// If the current text appears elsewhere in the list, remove it
			for (int i = 1; i < items.size(); i++)
			{
				if (items.get(i).equals(text))
				{
					items.remove(i);
					break;
				}
			}

			// Remove items from the end of the list until its length is within bounds
			while (items.size() > maxNumItems)
				items.remove(items.size() - 1);

			// Update actions
			updateActions();
		}
		return modified;
	}

	//------------------------------------------------------------------

	public Action getAction(String key)
	{
		return (Action)actionMap.get(key);
	}

	//------------------------------------------------------------------

	public List<Action> getItemActions(FontMetrics fontMetrics)
	{
		List<Action> actions = new ArrayList<>();
		for (int i = 0; i < items.size(); i++)
		{
			String str = textModel.toActionText(items.get(i), fontMetrics);
			actions.add(new CommandAction(Command.SELECT_ITEM + i, str.isEmpty() ? " " : str));
		}
		return actions;
	}

	//------------------------------------------------------------------

	public void updateActions()
	{
		getAction(Command.DELETE).setEnabled(!items.isEmpty());
		getAction(Command.SELECT_PREVIOUS).setEnabled(itemIndex > 0);
		getAction(Command.SELECT_NEXT).setEnabled(itemIndex < items.size() - 1);
		fireStateChanged();
	}

	//------------------------------------------------------------------

	public void addChangeListener(ChangeListener listener)
	{
		changeListeners.add(listener);
	}

	//------------------------------------------------------------------

	public void removeChangeListener(ChangeListener listener)
	{
		changeListeners.remove(listener);
	}

	//------------------------------------------------------------------

	protected void fireStateChanged()
	{
		for (int i = changeListeners.size() - 1; i >= 0; i--)
		{
			if (changeEvent == null)
				changeEvent = new ChangeEvent(this);
			changeListeners.get(i).stateChanged(changeEvent);
		}
	}

	//------------------------------------------------------------------

	private void addAction(String key,
						   String text)
	{
		actionMap.put(key, new CommandAction(key, text));
	}

	//------------------------------------------------------------------

	private String getItem()
	{
		return (itemIndex < 0) ? null : items.get(itemIndex);
	}

	//------------------------------------------------------------------

	private void onCommit()
	{
		itemIndex = -1;
		updateList();
	}

	//------------------------------------------------------------------

	private void onDelete()
	{
		if (itemIndex >= 0)
		{
			String item = items.remove(itemIndex);
			if (itemIndex >= items.size())
				itemIndex = items.size() - 1;
			if (item.equals(textModel.getText()))
				textModel.setText(getItem());
			updateActions();
		}
	}

	//------------------------------------------------------------------

	private void onSelectPrevious()
	{
		if (itemIndex > 0)
			onSelectItem(itemIndex - 1);
	}

	//------------------------------------------------------------------

	private void onSelectNext()
	{
		if (itemIndex < items.size() - 1)
			onSelectItem(itemIndex + 1);
	}

	//------------------------------------------------------------------

	private void onSelectItem(int index)
	{
		String item = items.get(index);
		updateList();
		index = items.indexOf(item);
		if (index >= 0)
		{
			itemIndex = index;
			textModel.setText(items.get(itemIndex));
			updateActions();
		}
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	List<String>			items;
	private	int						itemIndex;
	private	int						maxNumItems;
	private	ITextModel				textModel;
	private	ActionMap				actionMap;
	private	List<ChangeListener>	changeListeners;
	private	ChangeEvent				changeEvent;

}

//----------------------------------------------------------------------
