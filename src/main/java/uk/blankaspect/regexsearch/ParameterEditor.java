/*====================================================================*\

ParameterEditor.java

Class: parameter editor.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.regexsearch;

//----------------------------------------------------------------------


// IMPORTS


import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Window;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.util.Collections;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

import uk.blankaspect.common.exception.AppException;

import uk.blankaspect.common.misc.IStringKeyed;

import uk.blankaspect.common.string.StringUtils;

import uk.blankaspect.ui.swing.action.KeyAction;

import uk.blankaspect.ui.swing.button.FButton;

import uk.blankaspect.ui.swing.colour.Colours;

import uk.blankaspect.ui.swing.dialog.SingleTextFieldDialog;

import uk.blankaspect.ui.swing.font.FontUtils;

import uk.blankaspect.ui.swing.list.SingleSelectionListEditor;

import uk.blankaspect.ui.swing.menu.FCheckBoxMenuItem;
import uk.blankaspect.ui.swing.menu.FMenu;
import uk.blankaspect.ui.swing.menu.FMenuItem;

import uk.blankaspect.ui.swing.misc.GuiUtils;

import uk.blankaspect.ui.swing.modifiers.InputModifiers;

import uk.blankaspect.ui.swing.text.TextUtils;

//----------------------------------------------------------------------


// CLASS: PARAMETER EDITOR


class ParameterEditor
	extends JPanel
	implements ChangeListener, ListEditor.ITextModel
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	public static final		int	MIN_NUM_COLUMNS		= 16;
	public static final		int	MAX_NUM_COLUMNS		= 256;
	public static final		int	DEFAULT_NUM_COLUMNS	= 80;

	public static final		int	MIN_NUM_ROWS		= 1;
	public static final		int	MAX_NUM_ROWS		= 32;
	public static final		int	DEFAULT_NUM_ROWS	= 4;

	private static final	int	VERTICAL_MARGIN		= 1;
	private static final	int	HORIZONTAL_MARGIN	= 4;

	private static final	int	MAX_MENU_ITEM_WIDTH	= 320;

	private static final	Insets	ICON_BUTTON_MARGINS		= new Insets(1, 1, 1, 1);
	private static final	Insets	ESCAPE_BUTTON_MARGINS	= new Insets(1, 3, 1, 3);

	private static final	String	ESCAPE_STR					= "Escape";
	private static final	String	SELECT_ITEM_STR				= "Select item";
	private static final	String	TABS_ESCAPED_STR			= "Tabs escaped";
	private static final	String	LINE_FEEDS_ESCAPED_STR		= "Line feeds escaped";
	private static final	String	EDIT_STR					= "Edit";
	private static final	String	COPY_STR					= "Copy";
	private static final	String	SELECT_PREVIOUS_TOOLTIP_STR	= "Select the previous item (Ctrl+PageUp)";
	private static final	String	SELECT_NEXT_TOOLTIP_STR		= "Select the next item (Ctrl+PageDown)";

	private interface Command
	{
		String	TOGGLE_TABS_ESCAPED			= "toggleTabsEscaped";
		String	TOGGLE_LINE_FEEDS_ESCAPED	= "toggleLineFeedsEscaped";
		String	EDIT						= "edit";
		String	COPY						= "copy";
		String	SHOW_CONTEXT_MENU			= "showContextMenu";
	}

	private static final	KeyAction.KeyCommandPair[]	KEY_COMMANDS	=
	{
		new KeyAction.KeyCommandPair
		(
			KeyStroke.getKeyStroke(KeyEvent.VK_T, KeyEvent.CTRL_DOWN_MASK),
			Command.TOGGLE_TABS_ESCAPED
		),
		new KeyAction.KeyCommandPair
		(
			KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK),
			Command.TOGGLE_LINE_FEEDS_ESCAPED
		),
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
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	ParamKind		paramKind;
	private	EscapeListener	escapeListener;
	private	ListEditor		editor;
	private	boolean			tabsEscaped;
	private	boolean			lineFeedsEscaped;
	private	char			escapeChar;
	private	char			tabSurrogate;
	private	ActionMap		actionMap;
	private	TextArea		textArea;
	private	JScrollPane		textAreaScrollPane;
	private	JButton			escapeButton;
	private	JPopupMenu		contextMenu;
	private	JMenu			itemsSubmenu;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public ParameterEditor(
		ParamKind		paramKind,
		int				maxNumItems,
		char			escapeChar,
		EscapeListener	escapeListener)
	{
		// Initialise instance variables
		this.paramKind = paramKind;
		this.escapeListener = escapeListener;
		editor = new ListEditor(maxNumItems, this);
		tabSurrogate = AppConfig.INSTANCE.getTabSurrogate();
		this.escapeChar = escapeChar;

		// Initialise actions
		actionMap = new ActionMap();
		addAction(Command.TOGGLE_TABS_ESCAPED, TABS_ESCAPED_STR);
		addAction(Command.TOGGLE_LINE_FEEDS_ESCAPED, LINE_FEEDS_ESCAPED_STR);
		addAction(Command.EDIT, EDIT_STR + AppConstants.ELLIPSIS_STR);
		addAction(Command.COPY, COPY_STR);
		addAction(Command.SHOW_CONTEXT_MENU, null);


		//----  Text area scroll pane

		// Text area
		textArea = new TextArea(AppConfig.INSTANCE.getTargetAndReplacementTabWidth());

		// Scroll pane
		textAreaScrollPane = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
											 JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		textAreaScrollPane.getVerticalScrollBar().getModel().addChangeListener(this);

		Dimension editorSize = AppConfig.INSTANCE.getParameterEditorSize();
		int width = editorSize.width * textArea.getColumnWidth();
		int height = editorSize.height * textArea.getRowHeight();
		textAreaScrollPane.getViewport().setPreferredSize(new Dimension(width, height));
		textAreaScrollPane.setBorder(BorderFactory.createLineBorder(Colours.LINE_BORDER));
		GuiUtils.setViewportBorder(textAreaScrollPane, VERTICAL_MARGIN, HORIZONTAL_MARGIN);

		textAreaScrollPane.getViewport().setFocusable(false);
		textAreaScrollPane.getVerticalScrollBar().setFocusable(false);
		textAreaScrollPane.getVerticalScrollBar().getModel().addChangeListener(this);

		// Remove keys from input map
		InputMap inputMap = textAreaScrollPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		while (inputMap != null)
		{
			inputMap.remove(AppConstants.LE_KEY_SELECT_PREVIOUS);
			inputMap.remove(AppConstants.LE_KEY_SELECT_NEXT);
			inputMap = inputMap.getParent();
		}

		//----  Button panel

		GridBagLayout gridBag = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();

		JPanel buttonPanel = new JPanel(gridBag);

		// Upper button panel
		JPanel upperButtonPanel = new JPanel(new GridLayout(1, 0, 4, 0));

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(0, 0, 0, 0);
		gridBag.setConstraints(upperButtonPanel, gbc);
		buttonPanel.add(upperButtonPanel);

		// Button: previous
		JButton previousButton = new JButton(editor.getAction(ListEditor.Command.SELECT_PREVIOUS));
		previousButton.setIcon(Icons.ARROW_LEFT);
		previousButton.setText(null);
		previousButton.setMargin(ICON_BUTTON_MARGINS);
		previousButton.setToolTipText(SELECT_PREVIOUS_TOOLTIP_STR);
		upperButtonPanel.add(previousButton);

		// Button: next
		JButton nextButton = new JButton(editor.getAction(ListEditor.Command.SELECT_NEXT));
		nextButton.setIcon(Icons.ARROW_RIGHT);
		nextButton.setText(null);
		nextButton.setMargin(ICON_BUTTON_MARGINS);
		nextButton.setToolTipText(SELECT_NEXT_TOOLTIP_STR);
		upperButtonPanel.add(nextButton);

		// Button: escape
		escapeButton = new FButton(ESCAPE_STR);
		escapeButton.setMargin(ESCAPE_BUTTON_MARGINS);

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(4, 0, 0, 0);
		gridBag.setConstraints(escapeButton, gbc);
		buttonPanel.add(escapeButton);


		//----  Outer panel

		setLayout(gridBag);

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(0, 0, 0, 0);
		gridBag.setConstraints(textAreaScrollPane, gbc);
		add(textAreaScrollPane);

		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(0, 6, 0, 0);
		gridBag.setConstraints(buttonPanel, gbc);
		add(buttonPanel);

		// Update actions
		updateActions();

		// Add editor commands to action map
		KeyAction.create(this, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, editor,
						 AppConstants.LIST_EDITOR_KEY_COMMANDS);

		// Add commands to action map
		for (KeyAction.KeyCommandPair command : KEY_COMMANDS)
		{
			KeyAction.create(this, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, command.keyStroke(),
							 getAction(command.command()));
		}
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	private static String getEscapeSequence(
		char	escapeChar,
		char	ch)
	{
		return new String(new char[] { escapeChar, ch });
	}

	//------------------------------------------------------------------

	private static String escape(
		String	str,
		char	escapeChar)
	{
		return str.replace("\t", getEscapeSequence(escapeChar, 't')).replace("\n", getEscapeSequence(escapeChar, 'n'));
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : ChangeListener interface
////////////////////////////////////////////////////////////////////////

	@Override
	public void stateChanged(
		ChangeEvent	event)
	{
		if (!textAreaScrollPane.getVerticalScrollBar().getValueIsAdjusting())
			textArea.snapViewPosition(textAreaScrollPane.getViewport());
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : ListEditor.ITextModel interface
////////////////////////////////////////////////////////////////////////

	@Override
	public String getText()
	{
		String str = textArea.getText();
		if (str.isEmpty() && (paramKind == ParamKind.TARGET))
			str = null;
		else
		{
			str = tabsEscaped ? str.replace(getEscapeSequence('t'), "\t")
							  : str.replace(tabSurrogate, '\t');
			if (lineFeedsEscaped)
				str = str.replace(getEscapeSequence('n'), "\n");
		}
		return str;
	}

	//------------------------------------------------------------------

	@Override
	public void setText(
		String	text)
	{
		textArea.setText(text);
	}

	//------------------------------------------------------------------

	@Override
	public String toActionText(
		String	str)
	{
		return (itemsSubmenu == null)
					? str
					: TextUtils.getLimitedWidthString(escape(str), itemsSubmenu.getFontMetrics(itemsSubmenu.getFont()),
													  MAX_MENU_ITEM_WIDTH, TextUtils.RemovalMode.END);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	@Override
	public void setEnabled(
		boolean	enabled)
	{
		super.setEnabled(enabled);
		for (Component component : getComponents())
			GuiUtils.setAllEnabled(component, enabled);
		if (enabled)
			updateActions();
	}

	//------------------------------------------------------------------

	@Override
	public boolean requestFocusInWindow()
	{
		return textArea.requestFocusInWindow();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public List<String> getItems()
	{
		return editor.getItems();
	}

	//------------------------------------------------------------------

	public int getIndex()
	{
		return editor.getItemIndex();
	}

	//------------------------------------------------------------------

	public JButton getEscapeButton()
	{
		return escapeButton;
	}

	//------------------------------------------------------------------

	public String getText(
		boolean	escape)
	{
		return (escape ? escape(getText()) : getText());
	}

	//------------------------------------------------------------------

	public void setItems(
		List<String>	items)
	{
		editor.setItems(items);
	}

	//------------------------------------------------------------------

	public void setIndex(
		int	index)
	{
		editor.setItemIndex(index);
	}

	//------------------------------------------------------------------

	public void setCaretPosition(
		int	index)
	{
		textArea.setCaretPosition(getTextIndex(index));
	}

	//------------------------------------------------------------------

	public void setTabWidth(
		int	value)
	{
		textArea.setTabSize(value);
	}

	//------------------------------------------------------------------

	public void setTabSurrogate(
		char	tabSurrogate)
	{
		if (this.tabSurrogate != tabSurrogate)
		{
			if (tabsEscaped)
				this.tabSurrogate = tabSurrogate;
			else
			{
				String str = getText();
				this.tabSurrogate = tabSurrogate;
				setText(str);
			}
		}
	}

	//------------------------------------------------------------------

	public void setEscapeChar(
		char	escapeChar)
	{
		if (this.escapeChar != escapeChar)
		{
			String str = getText();
			this.escapeChar = escapeChar;
			setText(str);
		}
	}

	//------------------------------------------------------------------

	public void updateList()
	{
		editor.updateList();
	}

	//------------------------------------------------------------------

	public Dimension getTextAreaViewportSize()
	{
		Dimension viewportSize = textAreaScrollPane.getViewport().getExtentSize();
		return new Dimension(viewportSize.width / textArea.getColumnWidth(),
							 viewportSize.height / textArea.getRowHeight());
	}

	//------------------------------------------------------------------

	private String getEscapeSequence(
		char	ch)
	{
		return getEscapeSequence(escapeChar, ch);
	}

	//------------------------------------------------------------------

	private String escape(
		String	str)
	{
		return escape(str, escapeChar);
	}

	//------------------------------------------------------------------

	private int getTextIndex(
		int	index)
	{
		String str = getText();
		index = Math.min(index, str.length());
		int increment = 0;
		if (tabsEscaped)
		{
			for (int i = 0; i < index; i++)
			{
				if (str.charAt(i) == '\t')
					++increment;
			}
		}
		if (lineFeedsEscaped)
		{
			for (int i = 0; i < index; i++)
			{
				if (str.charAt(i) == '\n')
					++increment;
			}
		}
		return index + increment;
	}

	//------------------------------------------------------------------

	private CommandAction getAction(
		String	key)
	{
		return (CommandAction)actionMap.get(key);
	}

	//------------------------------------------------------------------

	private void addAction(
		String	key,
		String	text)
	{
		actionMap.put(key, new CommandAction(key, text));
	}

	//------------------------------------------------------------------

	private void updateActions()
	{
		editor.updateActions();
		getAction(Command.COPY).setEnabled(!textArea.getText().isEmpty());
		getAction(Command.TOGGLE_TABS_ESCAPED).setSelected(tabsEscaped);
		getAction(Command.TOGGLE_LINE_FEEDS_ESCAPED).setSelected(lineFeedsEscaped);
	}

	//------------------------------------------------------------------

	private void showListDialog()
	{
		List<String> items = getItems();
		for (int i = 0; i < items.size(); i++)
			items.set(i, escape(items.get(i)));
		items = ListDialog.showDialog(this, paramKind, items, editor.getMaxNumItems());
		if (items != null)
		{
			for (int i = 0; i < items.size(); i++)
			{
				String str = items.get(i).replace(getEscapeSequence('t'), "\t").replace(getEscapeSequence('n'), "\n");
				items.set(i, str);
			}
			setItems(items);
		}
	}

	//------------------------------------------------------------------

	private void showContextMenu(
		MouseEvent	event)
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

				contextMenu.add(new FCheckBoxMenuItem(getAction(Command.TOGGLE_TABS_ESCAPED)));
				contextMenu.add(new FCheckBoxMenuItem(getAction(Command.TOGGLE_LINE_FEEDS_ESCAPED)));

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
//  Enumerated types
////////////////////////////////////////////////////////////////////////


	// ENUMERATION: PARAMETER KIND


	enum ParamKind
		implements IStringKeyed
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		TARGET
		(
			"target"
		),

		REPLACEMENT
		(
			"replacement"
		);

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	String	key;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private ParamKind(
			String	key)
		{
			this.key = key;
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
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		public String toString()
		{
			return StringUtils.firstCharToUpperCase(key);
		}

		//--------------------------------------------------------------

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Member interfaces
////////////////////////////////////////////////////////////////////////


	// INTERFACE: ESCAPE LISTENER


	interface EscapeListener
	{

	////////////////////////////////////////////////////////////////////
	//  Methods
	////////////////////////////////////////////////////////////////////

		void setTabsEscaped(
			boolean	escaped);

		//--------------------------------------------------------------

		void setLineFeedsEscaped(
			boolean	escaped);

		//--------------------------------------------------------------

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// CLASS: LIST DIALOG


	private static class ListDialog
		extends SingleSelectionListEditor<String>
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		private static final	String	DELETE_MESSAGE_STR	= "Do you want to delete the selected ";

		private static final	String[]	TOOLTIP_STRS	=
		{
			"Add a new ",
			"Edit the selected ",
			"Delete the selected "
		};

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	ParamKind	paramKind;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private ListDialog(
			Component		parent,
			ParamKind		paramKind,
			List<String>	elements,
			int				maxNumElements)
		{
			super(parent, new EditorSelectionList(elements, false), maxNumElements, getTooltipStrings(paramKind));
			this.paramKind = paramKind;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Class methods
	////////////////////////////////////////////////////////////////////

		public static List<String> showDialog(
			Component		parent,
			ParamKind		paramKind,
			List<String>	elements,
			int				maxNumElements)
		{
			ListDialog dialog = new ListDialog(parent, paramKind, elements, maxNumElements);
			dialog.setVisible(true);
			return dialog.getElements();
		}

		//--------------------------------------------------------------

		private static String[] getTooltipStrings(
			ParamKind	paramKind)
		{
			String[] strs = new String[TOOLTIP_STRS.length];
			for (int i = 0; i < strs.length; i++)
				strs[i] = TOOLTIP_STRS[i] + paramKind.getKey();
			return strs;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		protected String getAddElement()
		{
			return ItemDialog.showDialog(this, getTitleString(ADD_STR), paramKind, null);
		}

		//--------------------------------------------------------------

		@Override
		protected String getEditElement(
			int	index)
		{
			return ItemDialog.showDialog(this, getTitleString(EDIT_STR), paramKind, getElement(index));
		}

		//--------------------------------------------------------------

		@Override
		protected boolean confirmDelete()
		{
			String[] optionStrs = Utils.getOptionStrings(DELETE_STR);
			return (JOptionPane.showOptionDialog(this, DELETE_MESSAGE_STR + paramKind.getKey() + "?",
												 getTitleString(DELETE_STR), JOptionPane.OK_CANCEL_OPTION,
												 JOptionPane.QUESTION_MESSAGE, null, optionStrs,
												 optionStrs[1]) == JOptionPane.OK_OPTION);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		private String getTitleString(
			String	actionStr)
		{
			return actionStr + " " + paramKind.getKey();
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Member classes : non-inner classes
	////////////////////////////////////////////////////////////////////


		// CLASS: ITEM DIALOG


		private static class ItemDialog
			extends SingleTextFieldDialog
		{

		////////////////////////////////////////////////////////////////
		//  Constants
		////////////////////////////////////////////////////////////////

			private static final	String	KEY	= ItemDialog.class.getCanonicalName();

		////////////////////////////////////////////////////////////////
		//  Constructors
		////////////////////////////////////////////////////////////////

			private ItemDialog(
				Window		owner,
				String		title,
				ParamKind	paramKind,
				String		pattern)
			{
				super(owner, title, KEY + "." + paramKind.getKey(), paramKind.toString(), pattern);
			}

			//----------------------------------------------------------

		////////////////////////////////////////////////////////////////
		//  Class methods
		////////////////////////////////////////////////////////////////

			private static String showDialog(
				Component	parent,
				String		title,
				ParamKind	paramKind,
				String		pattern)
			{
				ItemDialog dialog = new ItemDialog(GuiUtils.getWindow(parent), title, paramKind, pattern);
				dialog.setVisible(true);
				return dialog.getText();
			}

			//----------------------------------------------------------

		}

		//==============================================================

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Member classes : inner classes
////////////////////////////////////////////////////////////////////////


	// CLASS: PARAMETER EDITOR TEXT AREA


	private class TextArea
		extends JTextArea
		implements MouseListener
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		private static final	int	MARGIN	= 4;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private TextArea(
			int	tabWidth)
		{
			// Set properties
			AppFont.PARAMETER_EDITOR.apply(this);
			setBorder(null);
			setForeground(AppConstants.TEXT_COLOUR);
			setDisabledTextColor(Utils.getDisabledTextColour());
			setTabSize(tabWidth);
			setCaretPosition(getText().length());

			// Set Tab and Shift+Tab as focus traversal keys
			setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
								  Collections.singleton(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0)));
			setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,
								  Collections.singleton(KeyStroke.getKeyStroke(KeyEvent.VK_TAB,
																			   KeyEvent.SHIFT_DOWN_MASK)));

			// Add listeners
			addMouseListener(this);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : MouseListener interface
	////////////////////////////////////////////////////////////////////

		@Override
		public void mouseClicked(
			MouseEvent	event)
		{
			// do nothing
		}

		//--------------------------------------------------------------

		@Override
		public void mouseEntered(
			MouseEvent	event)
		{
			// do nothing
		}

		//--------------------------------------------------------------

		@Override
		public void mouseExited(
			MouseEvent	event)
		{
			// do nothing
		}

		//--------------------------------------------------------------

		@Override
		public void mousePressed(
			MouseEvent	event)
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

		@Override
		public void mouseReleased(
			MouseEvent	event)
		{
			showContextMenu(event);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		/**
		 * Increases the preferred width to allow the caret to be viewable at the trailing end of the
		 * longest line, if the width exceeds the viewport width.
		 */

		@Override
		public Dimension getPreferredSize()
		{
			Dimension size = super.getPreferredSize();
			return new Dimension(size.width + MARGIN, size.height);
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

		@Override
		public void setEnabled(
			boolean	enabled)
		{
			super.setEnabled(enabled);
			GuiUtils.setViewportBorder(textAreaScrollPane, VERTICAL_MARGIN, HORIZONTAL_MARGIN);
		}

		//--------------------------------------------------------------

		@Override
		protected int getColumnWidth()
		{
			return FontUtils.getCharWidth('0', getFontMetrics(getFont()));
		}

		//--------------------------------------------------------------

		@Override
		protected int getRowHeight()
		{
			return getFontMetrics(getFont()).getHeight();
		}

		//--------------------------------------------------------------

		@Override
		protected Document createDefaultModel()
		{
			return new TextAreaDocument();
		}

		//--------------------------------------------------------------

		@Override
		protected boolean processKeyBinding(
			KeyStroke	keyStroke,
			KeyEvent	event,
			int			condition,
			boolean		pressed)
		{
			// Replace Ctrl+Tab with Tab
			if ((event.getKeyCode() == KeyEvent.VK_TAB) && (InputModifiers.forEvent(event) == InputModifiers.CTRL))
			{
				event = new KeyEvent(event.getComponent(), event.getID(), event.getWhen(), 0, event.getKeyCode(),
									 event.getKeyChar(), event.getKeyLocation());
				keyStroke = KeyStroke.getKeyStrokeForEvent(event);
			}

			// Process key as normal
			return super.processKeyBinding(keyStroke, event, condition, pressed);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		public void snapViewPosition(
			JViewport	viewport)
		{
			int rowHeight = getRowHeight();
			Point viewPosition = viewport.getViewPosition();
			int y = Math.max(0, viewPosition.y + rowHeight / 2) / rowHeight * rowHeight;
			if (viewPosition.y != y)
			{
				viewPosition.y = y;
				viewport.setViewPosition(viewPosition);
			}
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Member classes : inner classes
	////////////////////////////////////////////////////////////////////


		// CLASS: TEXT AREA DOCUMENT


		private class TextAreaDocument
			extends PlainDocument
		{

		////////////////////////////////////////////////////////////////
		//  Constructors
		////////////////////////////////////////////////////////////////

			private TextAreaDocument()
			{
			}

			//----------------------------------------------------------

		////////////////////////////////////////////////////////////////
		//  Instance methods : overriding methods
		////////////////////////////////////////////////////////////////

			@Override
			public void insertString(
				int				offset,
				String			str,
				AttributeSet	attrSet)
				throws BadLocationException
			{
				str = tabsEscaped ? str.replace("\t", getEscapeSequence('t'))
								  : str.replace('\t', tabSurrogate);
				if (lineFeedsEscaped)
					str = str.replace("\n", getEscapeSequence('n'));
				super.insertString(offset, str, attrSet);
				setCaretPosition(offset + str.length());
			}

			//----------------------------------------------------------

		}

		//==============================================================

	}

	//==================================================================


	// CLASS: COMMAND ACTION


	private class CommandAction
		extends AbstractAction
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private CommandAction(
			String	command,
			String	text)
		{
			super(text);
			putValue(Action.ACTION_COMMAND_KEY, command);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : ActionListener interface
	////////////////////////////////////////////////////////////////////

		public void actionPerformed(
			ActionEvent	event)
		{
			String command = event.getActionCommand();

			if (command.equals(Command.TOGGLE_TABS_ESCAPED))
				onToggleTabsEscaped();

			else if (command.equals(Command.TOGGLE_LINE_FEEDS_ESCAPED))
				onToggleLineFeedsEscaped();

			else if (command.equals(Command.EDIT))
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

		public void setSelected(
			boolean	selected)
		{
			putValue(Action.SELECTED_KEY, selected);
		}

		//--------------------------------------------------------------

		private void onToggleTabsEscaped()
		{
			String str = getText();
			tabsEscaped = !tabsEscaped;
			setText(str);
			escapeListener.setTabsEscaped(tabsEscaped);
		}

		//--------------------------------------------------------------

		private void onToggleLineFeedsEscaped()
		{
			String str = getText();
			lineFeedsEscaped = !lineFeedsEscaped;
			setText(str);
			escapeListener.setLineFeedsEscaped(lineFeedsEscaped);
		}

		//--------------------------------------------------------------

		private void onEdit()
		{
			showListDialog();
		}

		//--------------------------------------------------------------

		private void onCopy()
		{
			try
			{
				Utils.putClipboardText(textArea.getText());
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

}

//----------------------------------------------------------------------
