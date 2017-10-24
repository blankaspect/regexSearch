/*====================================================================*\

PathnameEditor.java

Pathname editor class.

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
import java.awt.Window;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import uk.blankaspect.common.exception.AppException;

import uk.blankaspect.common.gui.FButton;
import uk.blankaspect.common.gui.FMenu;
import uk.blankaspect.common.gui.FMenuItem;
import uk.blankaspect.common.gui.GuiUtils;
import uk.blankaspect.common.gui.SinglePathnameFieldDialog;
import uk.blankaspect.common.gui.SingleSelectionListEditor;

import uk.blankaspect.common.misc.InputModifiers;
import uk.blankaspect.common.misc.KeyAction;
import uk.blankaspect.common.misc.ListEditor;
import uk.blankaspect.common.misc.Property;
import uk.blankaspect.common.misc.SystemUtils;
import uk.blankaspect.common.misc.TextUtils;

import uk.blankaspect.common.textfield.PathnameField;

//----------------------------------------------------------------------


// PATHNAME EDITOR CLASS


class PathnameEditor
	extends JPanel
	implements ListEditor.ITextModel
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	private static final	int	MAX_MENU_ITEM_WIDTH	= 400;

	private static final	Insets	BROWSE_BUTTON_MARGINS	= new Insets(1, 6, 1, 6);

	private static final	String	SELECT_ITEM_STR	= "Select item";
	private static final	String	EDIT_STR		= "Edit";
	private static final	String	COPY_STR		= "Copy";
	private static final	String	SELECT_STR		= "Select";

	private static final	String	CHOOSE_PATHNAME_TOOLTIP_STR		= "Choose a file or directory";
	private static final	String	SELECT_FILE_OR_DIRECTORY_STR	= "Select file or directory";

	// Commands
	private interface Command
	{
		String	CHOOSE_PATHNAME		= "choosePathname";
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

		private static final	String	PATHNAME_STR		= "Pathname";
		private static final	String	DELETE_MESSAGE_STR	= "Do you want to delete the selected " +
																"pathname?";

		private static final	String[]	TOOLTIP_STRS	=
		{
			"Add a new pathname",
			"Edit the selected pathname",
			"Delete the selected pathname"
		};

	////////////////////////////////////////////////////////////////////
	//  Member classes : non-inner classes
	////////////////////////////////////////////////////////////////////


		// PATHNAME DIALOG CLASS


		private static class PathnameDialog
			extends SinglePathnameFieldDialog
		{

		////////////////////////////////////////////////////////////////
		//  Constants
		////////////////////////////////////////////////////////////////

			private static final	String	KEY	= PathnameDialog.class.getCanonicalName();

		////////////////////////////////////////////////////////////////
		//  Constructors
		////////////////////////////////////////////////////////////////

			private PathnameDialog(Window owner,
								   String titleStr,
								   String pathname)
			{
				super(owner, titleStr, KEY, PATHNAME_STR, pathname, 0,
					  AppConfig.INSTANCE.isShowUnixPathnames());
			}

			//----------------------------------------------------------

		////////////////////////////////////////////////////////////////
		//  Class methods
		////////////////////////////////////////////////////////////////

			private static String showDialog(Component parent,
											 String    titleStr,
											 String    pathname)
			{
				PathnameDialog dialog = new PathnameDialog(GuiUtils.getWindow(parent), titleStr,
														   pathname);
				dialog.setVisible(true);
				return dialog.getPathname();
			}

			//----------------------------------------------------------

		////////////////////////////////////////////////////////////////
		//  Instance methods : overriding methods
		////////////////////////////////////////////////////////////////

			@Override
			protected File chooseFile()
			{
				return PathnameEditor.chooseFile(this, getField().getCanonicalFile());
			}

			//----------------------------------------------------------

		}

		//==============================================================

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private ListDialog(Component    parent,
						   List<String> pathnames)
		{
			super(parent, new EditorSelectionList(pathnames, true), FileSet.MAX_NUM_PATHNAMES,
				  TOOLTIP_STRS);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Class methods
	////////////////////////////////////////////////////////////////////

		public static List<String> showDialog(Component    parent,
											  List<String> pathnames)
		{
			ListDialog dialog = new ListDialog(parent, pathnames);
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
			return PathnameDialog.showDialog(this, getTitleString(ADD_STR), null);
		}

		//--------------------------------------------------------------

		@Override
		protected String getEditElement(int index)
		{
			return PathnameDialog.showDialog(this, getTitleString(EDIT_STR), getElement(index));
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
			return (actionStr + " " + PATHNAME_STR.toLowerCase());
		}

		//--------------------------------------------------------------

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Member classes : inner classes
////////////////////////////////////////////////////////////////////////


	// TEXT FIELD CLASS


	private class TextField
		extends PathnameField
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

		private TextField()
		{
			// Call superclass constructor
			super(0);

			// Set attributes
			AppFont.TEXT_FIELD.apply(this);
			GuiUtils.setPaddedLineBorder(this, VERTICAL_MARGIN, HORIZONTAL_MARGIN);
			setUnixStyle(AppConfig.INSTANCE.isShowUnixPathnames());
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

		@Override
		public void propertyChanged(Property property)
		{
			super.propertyChanged(property);

			List<String> items = new ArrayList<>();
			for (String item : getItems())
				items.add(convertPathname(item));
			setItems(items);
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

			if (command.equals(Command.CHOOSE_PATHNAME))
				onChoosePathname();

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

		private void onChoosePathname()
		{
			File file = chooseFile(PathnameEditor.this, pathnameField.getCanonicalFile());
			if (file != null)
				setFile(file);
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
				Utils.putClipboardText(pathnameField.getText());
			}
			catch (AppException e)
			{
				App.INSTANCE.showErrorMessage(App.SHORT_NAME, e );
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

	public PathnameEditor(int maxNumItems)
	{
		// Initialise instance fields
		editor = new ListEditor(maxNumItems, this);

		// Initialise actions
		actionMap = new ActionMap();
		addAction(Command.CHOOSE_PATHNAME, AppConstants.ELLIPSIS_STR);
		addAction(Command.EDIT, EDIT_STR + AppConstants.ELLIPSIS_STR);
		addAction(Command.COPY, COPY_STR);
		addAction(Command.SHOW_CONTEXT_MENU, null);

		// Set layout
		GridBagLayout gridBag = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		setLayout(gridBag);

		int gridX = 0;

		// Field: pathname
		pathnameField = new TextField();

		gbc.gridx = gridX++;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(0, 0, 0, 0);
		gridBag.setConstraints(pathnameField, gbc);
		add(pathnameField);

		// Button: browse
		JButton browseButton = new FButton(getAction(Command.CHOOSE_PATHNAME));
		browseButton.setMargin(BROWSE_BUTTON_MARGINS);
		browseButton.setToolTipText(CHOOSE_PATHNAME_TOOLTIP_STR);

		gbc.gridx = gridX++;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.VERTICAL;
		gbc.insets = new Insets(0, 6, 0, 0);
		gridBag.setConstraints(browseButton, gbc);
		add(browseButton);

		// Update actions
		updateActions();

		// Add editor commands to action map
		KeyAction.create(this, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, editor,
						 AppConstants.LIST_EDITOR_KEY_COMMANDS);

		// Add commands to action map
		for (KeyAction.KeyCommandPair command : KEY_COMMANDS)
			KeyAction.create(this, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, command.keyStroke,
							 getAction(command.command));
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	private static File chooseFile(Component parent,
								   File      file)
	{
		if (fileChooser == null)
		{
			fileChooser = new JFileChooser(SystemUtils.getUserHomePathname());
			fileChooser.setApproveButtonMnemonic(KeyEvent.VK_S);
		}
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fileChooser.setDialogTitle(SELECT_FILE_OR_DIRECTORY_STR);
		fileChooser.setApproveButtonToolTipText(SELECT_FILE_OR_DIRECTORY_STR);
		if (file != null)
		{
			if (file.isDirectory())
				fileChooser.setSelectedFile(file);
			else
				fileChooser.setCurrentDirectory(file);
		}
		fileChooser.rescanCurrentDirectory();
		return ((fileChooser.showDialog(parent, SELECT_STR) == JFileChooser.APPROVE_OPTION)
																			? fileChooser.getSelectedFile()
																			: null);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : ListEditor.ITextModel interface
////////////////////////////////////////////////////////////////////////

	public String getText()
	{
		String str = pathnameField.getText();
		return (str.isEmpty() ? null : str);
	}

	//------------------------------------------------------------------

	public void setText(String text)
	{
		pathnameField.setText(text);
	}

	//------------------------------------------------------------------

	public String toActionText(String str)
	{
		return ((itemsSubmenu == null)
					? str
					: TextUtils.getLimitedWidthPathname(str, itemsSubmenu.getFontMetrics(
																				itemsSubmenu.getFont()),
														MAX_MENU_ITEM_WIDTH,
														Utils.getFileSeparatorChar()));
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
		return pathnameField.requestFocusInWindow();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public boolean isEmpty()
	{
		return pathnameField.isEmpty();
	}

	//------------------------------------------------------------------

	public File getFile()
	{
		return pathnameField.getFile();
	}

	//------------------------------------------------------------------

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

	public void setFile(File file)
	{
		pathnameField.setFile(file);
		updateList();
	}

	//------------------------------------------------------------------

	public void setItems(List<String> items)
	{
		editor.setItems(items);
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

	public void addImportListener(PathnameField.IImportListener listener)
	{
		pathnameField.addImportListener(listener);
	}

	//------------------------------------------------------------------

	public void addUnixStyleObserver()
	{
		AppConfig.INSTANCE.addShowUnixPathnamesObserver(pathnameField);
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
		getAction(Command.COPY).setEnabled(!pathnameField.isEmpty());
	}

	//------------------------------------------------------------------

	private void showListDialog()
	{
		List<String> items = ListDialog.showDialog(this, getItems());
		if (items != null)
			setItems(items);
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
//  Class fields
////////////////////////////////////////////////////////////////////////

	private static	JFileChooser	fileChooser;

////////////////////////////////////////////////////////////////////////
//  Instance fields
////////////////////////////////////////////////////////////////////////

	private	ListEditor	editor;
	private	ActionMap	actionMap;
	private	TextField	pathnameField;
	private	JPopupMenu	contextMenu;
	private	JMenu		itemsSubmenu;

}

//----------------------------------------------------------------------
