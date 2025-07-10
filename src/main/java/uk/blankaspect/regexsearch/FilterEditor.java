/*====================================================================*\

FilterEditor.java

Filter editor class.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.regexsearch;

//----------------------------------------------------------------------


// IMPORTS


import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import javax.swing.text.Document;

import uk.blankaspect.common.exception.AppException;

import uk.blankaspect.ui.swing.action.KeyAction;

import uk.blankaspect.ui.swing.button.FButton;

import uk.blankaspect.ui.swing.list.SingleSelectionListEditor;

import uk.blankaspect.ui.swing.menu.FMenu;
import uk.blankaspect.ui.swing.menu.FMenuItem;

import uk.blankaspect.ui.swing.misc.GuiUtils;

import uk.blankaspect.ui.swing.modifiers.InputModifiers;

import uk.blankaspect.ui.swing.text.TextUtils;

//----------------------------------------------------------------------


// FILTER EDITOR CLASS


class FilterEditor
	extends JPanel
	implements ListEditor.ITextModel
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	private static final	int	MAX_MENU_ITEM_WIDTH	= 320;

	private static final	Insets	BUTTON_MARGINS	= new Insets(1, 3, 1, 3);

	private static final	String	KIND_PLACEHOLDER_STR	= "%";

	private static final	String	SELECT_ITEM_STR		= "Select item";
	private static final	String	EDIT_STR			= "Edit";
	private static final	String	COPY_STR			= "Copy";
	private static final	String	EDIT_TOOLTIP_STR	= "Edit the " + KIND_PLACEHOLDER_STR + " filter";

	private interface Command
	{
		String	EDIT				= "edit";
		String	COPY				= "copy";
		String	SHOW_CONTEXT_MENU	= "showContextMenu";
	}

	private static final	KeyAction.KeyCommandPair[]	KEY_COMMANDS	=
	{
		new KeyAction.KeyCommandPair
		(
			KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.ALT_DOWN_MASK),
			Command.EDIT
		),
		new KeyAction.KeyCommandPair
		(
			KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK),
			Command.COPY
		),
		new KeyAction.KeyCommandPair
		(
			KeyStroke.getKeyStroke(KeyEvent.VK_CONTEXT_MENU, 0),
			Command.SHOW_CONTEXT_MENU
		)
	};

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// LIST DIALOG CLASS


	private static class ListDialog
		extends SingleSelectionListEditor<String>
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		private static final	String	FILTER_STR			= "filter";
		private static final	String	DELETE_MESSAGE_STR	= "Do you want to delete the selected filter?";

		private static final	String[]	TOOLTIP_STRS	=
		{
			"Add a new filter",
			"Edit the selected filter",
			"Delete the selected filter"
		};

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private ListDialog(Component          parent,
						   FileSet.FilterKind filterKind,
						   List<String>       elements)
		{
			super(parent, new EditorSelectionList(elements, false), FileSet.MAX_NUM_FILTERS, TOOLTIP_STRS);
			this.filterKind = filterKind;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Class methods
	////////////////////////////////////////////////////////////////////

		public static List<String> showDialog(Component          parent,
											  FileSet.FilterKind filterKind,
											  List<String>       elements)
		{
			ListDialog dialog = new ListDialog(parent, filterKind, elements);
			dialog.setVisible(true);
			return dialog.getElements();
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		protected String getAddElement()
		{
			return PathnameFilterDialog.showDialog(this, getTitleString(ADD_STR), filterKind, null);
		}

		//--------------------------------------------------------------

		@Override
		protected String getEditElement(int index)
		{
			return PathnameFilterDialog.showDialog(this, getTitleString(EDIT_STR), filterKind,
												   getElement(index));
		}

		//--------------------------------------------------------------

		@Override
		protected boolean confirmDelete()
		{
			String[] optionStrs = Utils.getOptionStrings(DELETE_STR);
			return (JOptionPane.showOptionDialog(this, DELETE_MESSAGE_STR, getTitleString(DELETE_STR),
												 JOptionPane.OK_CANCEL_OPTION,
												 JOptionPane.QUESTION_MESSAGE, null, optionStrs,
												 optionStrs[1]) == JOptionPane.OK_OPTION);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		private String getTitleString(String actionStr)
		{
			return (actionStr + " " + filterKind.toString().toLowerCase() + " " + FILTER_STR);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	FileSet.FilterKind	filterKind;

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Member classes : inner classes
////////////////////////////////////////////////////////////////////////


	// PATTERN FIELD CLASS


	private class PatternField
		extends JTextField
		implements MouseListener
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		private static final	int	VERTICAL_MARGIN		= 1;
		private static final	int	HORIZONTAL_MARGIN	= 4;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private PatternField()
		{
			// Set properties
			AppFont.TEXT_FIELD.apply(this);
			GuiUtils.setPaddedLineBorder(this, VERTICAL_MARGIN, HORIZONTAL_MARGIN);
			setForeground(AppConstants.TEXT_COLOUR);
			setDisabledTextColor(Utils.getDisabledTextColour());

			// Add listeners
			addMouseListener(this);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : MouseListener interface
	////////////////////////////////////////////////////////////////////

		public void mouseClicked(MouseEvent event)
		{
			// do nothing
		}

		//--------------------------------------------------------------

		public void mouseEntered(MouseEvent event)
		{
			// do nothing
		}

		//--------------------------------------------------------------

		public void mouseExited(MouseEvent event)
		{
			// do nothing
		}

		//--------------------------------------------------------------

		public void mousePressed(MouseEvent event)
		{
			if (SwingUtilities.isLeftMouseButton(event))
			{
				if (isEnabled() && (InputModifiers.forEvent(event) == InputModifiers.CTRL))
					showListDialog();
			}

			else if (SwingUtilities.isRightMouseButton(event))
				requestFocusInWindow();

			showContextMenu(event);
		}

		//--------------------------------------------------------------

		public void mouseReleased(MouseEvent event)
		{
			showContextMenu(event);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		public void setText(String text)
		{
			super.setText(text);
			setCaretPosition(getText().length());
		}

		//--------------------------------------------------------------

		@Override
		public Color getBackground()
		{
			Color colour = AppConstants.BACKGROUND_COLOUR;
			if (!isEnabled())
			{
				colour = getParent().getBackground();
				if (colour == null)
					colour = AppConstants.DISABLED_BACKGROUND_COLOUR;
			}
			return colour;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		public boolean isEmpty()
		{
			Document document = getDocument();
			return ((document == null) ? true : (document.getLength() == 0));
		}

		//--------------------------------------------------------------

	}

	//==================================================================


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

		public void actionPerformed(ActionEvent event)
		{
			String command = event.getActionCommand();

			if (command.equals(Command.EDIT))
				onEdit();

			else if (command.equals(Command.COPY))
				onCopy();

			else if (command.equals(Command.SHOW_CONTEXT_MENU))
				onShowContextMenu();
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		private void onEdit()
		{
			showListDialog();
		}

		//--------------------------------------------------------------

		private void onCopy()
		{
			try
			{
				Utils.putClipboardText(patternField.getText());
			}
			catch (AppException e)
			{
				RegexSearchApp.INSTANCE.showErrorMessage(RegexSearchApp.SHORT_NAME, e);
			}
		}

		//--------------------------------------------------------------

		private void onShowContextMenu()
		{
			showContextMenu(null);
		}

		//--------------------------------------------------------------

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public FilterEditor(FileSet.FilterKind filterKind,
						int                maxNumItems,
						boolean            suppressEmptyItem)
	{
		// Initialise instance variables
		this.filterKind = filterKind;
		this.suppressEmptyItem = suppressEmptyItem;
		editor = new ListEditor(maxNumItems, this);

		// Initialise actions
		actionMap = new ActionMap();
		addAction(Command.EDIT, EDIT_STR + AppConstants.ELLIPSIS_STR);
		addAction(Command.COPY, COPY_STR);
		addAction(Command.SHOW_CONTEXT_MENU, null);

		// Set layout
		GridBagLayout gridBag = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		setLayout(gridBag);

		// Field: patterns
		patternField = new PatternField();

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(0, 0, 0, 0);
		gridBag.setConstraints(patternField, gbc);
		add(patternField);

		// Button: edit
		JButton editButton = new FButton(getAction(Command.EDIT));
		editButton.setMargin(BUTTON_MARGINS);
		editButton.setToolTipText(EDIT_TOOLTIP_STR.replace(KIND_PLACEHOLDER_STR,
														   filterKind.toString().toLowerCase()));

		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.VERTICAL;
		gbc.insets = new Insets(0, 6, 0, 0);
		gridBag.setConstraints(editButton, gbc);
		add(editButton);

		// Add editor commands to action map
		KeyAction.create(this, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, editor,
						 AppConstants.LIST_EDITOR_KEY_COMMANDS);

		// Add commands to action map
		for (KeyAction.KeyCommandPair command : KEY_COMMANDS)
			KeyAction.create(this, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, command.keyStroke(),
							 getAction(command.command()));
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : ListEditor.ITextModel interface
////////////////////////////////////////////////////////////////////////

	public String getText()
	{
		String str = FileSet.patternsToString(getPatterns());
		return (str.isEmpty() && suppressEmptyItem ? null : str);
	}

	//------------------------------------------------------------------

	public void setText(String text)
	{
		patternField.setText(text);
	}

	//------------------------------------------------------------------

	public String toActionText(String str)
	{
		return ((itemsSubmenu == null)
					? str
					: TextUtils.getLimitedWidthString(str, itemsSubmenu.getFontMetrics(itemsSubmenu.getFont()),
													  MAX_MENU_ITEM_WIDTH, TextUtils.RemovalMode.END));
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	@Override
	public void setEnabled(boolean enabled)
	{
		super.setEnabled(enabled);
		for (Component component : getComponents())
			component.setEnabled(enabled);
	}

	//------------------------------------------------------------------

	@Override
	public boolean requestFocusInWindow()
	{
		return patternField.requestFocusInWindow();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public boolean isEmpty()
	{
		return getPatterns().isEmpty();
	}

	//------------------------------------------------------------------

	public List<String> getPatterns()
	{
		return FileSet.stringToPatterns(patternField.getText());
	}

	//------------------------------------------------------------------

	public List<List<String>> getItems()
	{
		List<List<String>> items = new ArrayList<>();
		for (String str : editor.getItems())
			items.add(FileSet.stringToPatterns(str));
		return items;
	}

	//------------------------------------------------------------------

	public int getIndex()
	{
		return editor.getItemIndex();
	}

	//------------------------------------------------------------------

	public void setItems(List<List<String>> items)
	{
		List<String> editorItems = new ArrayList<>();
		if (items != null)
		{
			for (List<String> item : items)
				editorItems.add(FileSet.patternsToString(item));
		}
		editor.setItems(editorItems);
	}

	//------------------------------------------------------------------

	public void setIndex(int index)
	{
		editor.setItemIndex(index);
	}

	//------------------------------------------------------------------

	public void updateList()
	{
		editor.updateList();
	}

	//------------------------------------------------------------------

	private CommandAction getAction(String key)
	{
		return (CommandAction)actionMap.get(key);
	}

	//------------------------------------------------------------------

	private void addAction(String key,
						   String text)
	{
		actionMap.put(key, new CommandAction(key, text));
	}

	//------------------------------------------------------------------

	private void updateActions()
	{
		editor.updateActions();
		getAction(Command.COPY).setEnabled(!patternField.isEmpty());
	}

	//------------------------------------------------------------------

	private void showListDialog()
	{
		List<String> items = ListDialog.showDialog(this, filterKind, editor.getItems());
		if (items != null)
			editor.setItems(items);
	}

	//------------------------------------------------------------------

	private void showContextMenu(MouseEvent event)
	{
		if (isEnabled() && ((event == null) || event.isPopupTrigger()))
		{
			// Create context menu
			if (contextMenu == null)
			{
				contextMenu = new JPopupMenu();
				itemsSubmenu = new FMenu(SELECT_ITEM_STR);
				contextMenu.add(itemsSubmenu);

				contextMenu.add(new FMenuItem(editor.getAction(ListEditor.Command.SELECT_PREVIOUS)));
				contextMenu.add(new FMenuItem(editor.getAction(ListEditor.Command.SELECT_NEXT)));

				contextMenu.addSeparator();

				contextMenu.add(new FMenuItem(getAction(Command.EDIT)));

				contextMenu.addSeparator();

				contextMenu.add(new FMenuItem(getAction(Command.COPY)));

				contextMenu.addSeparator();

				contextMenu.add(new FMenuItem(editor.getAction(ListEditor.Command.DELETE)));
			}

			// Update actions for menu items
			updateActions();

			// Update list and add current items to submenu
			editor.updateList();
			itemsSubmenu.removeAll();
			itemsSubmenu.setEnabled(!editor.isEmpty());
			if (itemsSubmenu.isEnabled())
			{
				for (Action action : editor.getItemActions())
					itemsSubmenu.add(new FMenuItem(action));
			}

			// Display menu
			Utils.showContextMenu(contextMenu, this, event);
		}
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	FileSet.FilterKind	filterKind;
	private	boolean				suppressEmptyItem;
	private	ListEditor			editor;
	private	ActionMap			actionMap;
	private	PatternField		patternField;
	private	JPopupMenu			contextMenu;
	private	JMenu				itemsSubmenu;

}

//----------------------------------------------------------------------
