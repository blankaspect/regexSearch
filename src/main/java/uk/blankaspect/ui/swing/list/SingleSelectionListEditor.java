/*====================================================================*\

SingleSelectionListEditor.java

Single-selection list editor class.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.swing.list;

//----------------------------------------------------------------------


// IMPORTS


import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import uk.blankaspect.ui.swing.action.KeyAction;

import uk.blankaspect.ui.swing.button.FButton;

import uk.blankaspect.ui.swing.icon.DialogIcon;

import uk.blankaspect.ui.swing.menu.FMenuItem;

import uk.blankaspect.ui.swing.misc.GuiConstants;
import uk.blankaspect.ui.swing.misc.GuiUtils;

//----------------------------------------------------------------------


// SINGLE-SELECTION LIST EDITOR CLASS


public abstract class SingleSelectionListEditor<E>
	extends JDialog
	implements ActionListener, ChangeListener, ListSelectionListener, MouseListener
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	public static final		Color	BORDER_COLOUR		= new Color(224, 144, 88);
	public static final		Color	BACKGROUND_COLOUR	= new Color(240, 224, 176);

	protected static final	String	ADD_STR		= "Add";
	protected static final	String	EDIT_STR	= "Edit";
	protected static final	String	DELETE_STR	= "Delete";

	private static final	int		MODIFIERS_MASK	=
			ActionEvent.ALT_MASK | ActionEvent.META_MASK | ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK;

	private static final	Insets	BUTTON_MARGINS		= new Insets(1, 4, 1, 4);
	private static final	Insets	ICON_BUTTON_MARGINS	= new Insets(1, 1, 1, 1);

	private static final	String	ACCEPT_TOOLTIP_STR	= "Close the editor and accept all changes";
	private static final	String	CLOSE_TOOLTIP_STR	= "Close the editor and discard any changes";

	// Commands
	private interface Command
	{
		String	ADD		= "add";
		String	EDIT	= SingleSelectionList.Command.EDIT_ELEMENT;
		String	DELETE	= "delete";
		String	ACCEPT	= "accept";
		String	CLOSE	= "close";
	}

	private static final	KeyAction.KeyCommandPair[]	KEY_COMMANDS	=
	{
		KeyAction.command(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
						  Command.ACCEPT),
		KeyAction.command(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.CTRL_DOWN_MASK),
						  Command.ACCEPT),
		KeyAction.command(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
						  Command.CLOSE)
	};

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	boolean					accepted;
	private	int						maxNumElements;
	private	ActionMap				actionMap;
	private	SingleSelectionList<E>	list;
	private	JScrollPane				listScrollPane;
	private	JButton					addButton;
	private	JButton					editButton;
	private	JButton					deleteButton;
	private	JPopupMenu				contextMenu;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	protected SingleSelectionListEditor(Component              parent,
										SingleSelectionList<E> list,
										int                    maxNumElements,
										String[]               tooltipStrs)
	{

		// Call superclass constructor
		super(GuiUtils.getWindow(parent), ModalityType.APPLICATION_MODAL);

		// Initialise instance variables
		this.list = list;
		this.maxNumElements = maxNumElements;

		// Initialise actions
		actionMap = new ActionMap();
		addAction(Command.ADD, ADD_STR + GuiConstants.ELLIPSIS_STR);
		addAction(Command.EDIT, EDIT_STR + GuiConstants.ELLIPSIS_STR);
		addAction(Command.DELETE, DELETE_STR);


		//----  List scroll pane

		// Selection list
		list.addActionListener(this);
		list.addListSelectionListener(this);
		list.addMouseListener(this);

		// Scroll pane: selection list
		listScrollPane = new JScrollPane(list, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
										 JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		listScrollPane.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER_COLOUR));
		listScrollPane.getVerticalScrollBar().setFocusable(false);
		listScrollPane.getVerticalScrollBar().getModel().addChangeListener(this);

		list.setViewport(listScrollPane.getViewport());


		//----  List button panel

		JPanel listButtonPanel = new JPanel(new GridLayout(0, 1, 0, 6));
		listButtonPanel.setBackground(BACKGROUND_COLOUR);

		// Button: add
		addButton = new FButton(getAction(Command.ADD));
		addButton.setMargin(BUTTON_MARGINS);
		addButton.setMnemonic(KeyEvent.VK_A);
		addButton.setToolTipText(tooltipStrs[0]);
		listButtonPanel.add(addButton);

		// Button: edit
		editButton = new FButton(getAction(Command.EDIT));
		editButton.setMargin(BUTTON_MARGINS);
		editButton.setMnemonic(KeyEvent.VK_E);
		editButton.setToolTipText(tooltipStrs[1]);
		listButtonPanel.add(editButton);

		// Button: delete
		deleteButton = new FButton(getAction(Command.DELETE));
		deleteButton.setMargin(BUTTON_MARGINS);
		deleteButton.setMnemonic(KeyEvent.VK_D);
		deleteButton.setToolTipText(tooltipStrs[2]);
		listButtonPanel.add(deleteButton);


		//----  Close button panel

		JPanel closeButtonPanel = new JPanel(new GridLayout(1, 0, 4, 0));
		closeButtonPanel.setBackground(BACKGROUND_COLOUR);

		// Button: OK
		JButton okButton = new JButton(DialogIcon.TICK);
		okButton.setMargin(ICON_BUTTON_MARGINS);
		okButton.setToolTipText(ACCEPT_TOOLTIP_STR);
		okButton.setActionCommand(Command.ACCEPT);
		okButton.addActionListener(this);
		closeButtonPanel.add(okButton);

		// Button: cancel
		JButton cancelButton = new JButton(DialogIcon.CROSS);
		cancelButton.setMargin(ICON_BUTTON_MARGINS);
		cancelButton.setToolTipText(CLOSE_TOOLTIP_STR);
		cancelButton.setActionCommand(Command.CLOSE);
		cancelButton.addActionListener(this);
		closeButtonPanel.add(cancelButton);


		//----  Outer panel

		GridBagLayout gridBag = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();

		JPanel outerPanel = new JPanel(gridBag);
		outerPanel.setBorder(BorderFactory.createLineBorder(BORDER_COLOUR));
		outerPanel.setBackground(BACKGROUND_COLOUR);

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 2;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(0, 0, 0, 0);
		gridBag.setConstraints(listScrollPane, gbc);
		outerPanel.add(listScrollPane);

		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(4, 4, 4, 4);
		gridBag.setConstraints(listButtonPanel, gbc);
		outerPanel.add(listButtonPanel);

		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.SOUTH;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(4, 4, 4, 4);
		gridBag.setConstraints(closeButtonPanel, gbc);
		outerPanel.add(closeButtonPanel);

		// Add commands to action map
		KeyAction.create(outerPanel, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, this, KEY_COMMANDS);

		// Set content pane
		setContentPane(outerPanel);

		// Update actions
		updateActions();

		// Omit frame from window
		setUndecorated(true);

		// Dispose of window explicitly
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		// Handle window events
		addWindowListener(new WindowEventHandler());

		// Prevent window from being resized
		setResizable(false);

		// Resize window to its preferred size
		pack();

		// Set location of window
		Point location = parent.getLocationOnScreen();
		int x = location.x;
		Rectangle screenRect = GuiUtils.getVirtualScreenBounds(parent);
		int y = Math.max(0, Math.min(location.y, screenRect.y + screenRect.height - getHeight()));
		setLocation(x, y);

		// Set focus
		if (list.isEmpty())
			addButton.requestFocusInWindow();

	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Abstract methods
////////////////////////////////////////////////////////////////////////

	protected abstract E getAddElement();

	//------------------------------------------------------------------

	protected abstract E getEditElement(int index);

	//------------------------------------------------------------------

	protected abstract boolean confirmDelete();

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : ActionListener interface
////////////////////////////////////////////////////////////////////////

	@Override
	public void actionPerformed(ActionEvent event)
	{
		switch (event.getActionCommand())
		{
			case Command.ADD                                   -> onAdd();
			case Command.EDIT                                  -> onEdit();
			case Command.DELETE                                ->
			{
				if ((event.getModifiers() & MODIFIERS_MASK) == ActionEvent.SHIFT_MASK)
					onDelete();
				else
					onConfirmDelete();
			}
			case SingleSelectionList.Command.DELETE_ELEMENT    -> onConfirmDelete();
			case SingleSelectionList.Command.DELETE_EX_ELEMENT -> onDelete();
			case SingleSelectionList.Command.MOVE_ELEMENT_UP   -> onMoveUp();
			case SingleSelectionList.Command.MOVE_ELEMENT_DOWN -> onMoveDown();
			case SingleSelectionList.Command.DRAG_ELEMENT      -> onMove();
			case Command.ACCEPT                                -> onAccept();
			case Command.CLOSE                                 -> onClose();
		}
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : ChangeListener interface
////////////////////////////////////////////////////////////////////////

	@Override
	public void stateChanged(ChangeEvent event)
	{
		if (!listScrollPane.getVerticalScrollBar().getValueIsAdjusting() && !list.isDragging())
			list.snapViewPosition();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : ListSelectionListener interface
////////////////////////////////////////////////////////////////////////

	@Override
	public void valueChanged(ListSelectionEvent event)
	{
		if (!event.getValueIsAdjusting())
			updateActions();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : MouseListener interface
////////////////////////////////////////////////////////////////////////

	@Override
	public void mouseClicked(MouseEvent event)
	{
		// do nothing
	}

	//------------------------------------------------------------------

	@Override
	public void mouseEntered(MouseEvent event)
	{
		// do nothing
	}

	//------------------------------------------------------------------

	@Override
	public void mouseExited(MouseEvent event)
	{
		// do nothing
	}

	//------------------------------------------------------------------

	@Override
	public void mousePressed(MouseEvent event)
	{
		showContextMenu(event);
	}

	//------------------------------------------------------------------

	@Override
	public void mouseReleased(MouseEvent event)
	{
		showContextMenu(event);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public List<E> getElements()
	{
		return accepted ? list.getElements() : null;
	}

	//------------------------------------------------------------------

	public E getElement(int index)
	{
		return list.getElement(index);
	}

	//------------------------------------------------------------------

	public void showContextMenu(Point location)
	{
		// Create context menu
		if (contextMenu == null)
		{
			contextMenu = new JPopupMenu();
			contextMenu.add(new FMenuItem(getAction(Command.ADD)));
			contextMenu.add(new FMenuItem(getAction(Command.EDIT)));
			contextMenu.add(new FMenuItem(getAction(Command.DELETE)));
		}

		// Update actions for menu items
		updateActions();

		// Display menu
		contextMenu.show(this, location.x, location.y);
	}

	//------------------------------------------------------------------

	protected void windowOpened()
	{
		// do nothing
	}

	//------------------------------------------------------------------

	private void showContextMenu(MouseEvent event)
	{
		if (event.isPopupTrigger())
			showContextMenu(SwingUtilities.convertPoint(event.getComponent(), event.getPoint(),
														this));
	}

	//------------------------------------------------------------------

	@SuppressWarnings("unchecked")
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
		getAction(Command.ADD).setEnabled(list.getNumElements() < maxNumElements);
		getAction(Command.EDIT).setEnabled(list.isSelection());
		getAction(Command.DELETE).setEnabled(list.isSelection());
	}

	//------------------------------------------------------------------

	private void onAdd()
	{
		E element = getAddElement();
		if (element != null)
		{
			list.addElement(element);
			updateActions();
		}
	}

	//------------------------------------------------------------------

	private void onEdit()
	{
		int index = list.getSelectedIndex();
		E element = getEditElement(index);
		if (element != null)
			list.setElement(index, element);
	}

	//------------------------------------------------------------------

	private void onConfirmDelete()
	{
		if (confirmDelete())
			onDelete();
	}

	//------------------------------------------------------------------

	private void onDelete()
	{
		list.removeElement(list.getSelectedIndex());
		updateActions();
	}

	//------------------------------------------------------------------

	private void onMoveUp()
	{
		int index = list.getSelectedIndex();
		list.moveElement(index, index - 1);
	}

	//------------------------------------------------------------------

	private void onMoveDown()
	{
		int index = list.getSelectedIndex();
		list.moveElement(index, index + 1);
	}

	//------------------------------------------------------------------

	private void onMove()
	{
		int fromIndex = list.getSelectedIndex();
		int toIndex = list.getDragEndIndex();
		if (toIndex > fromIndex)
			--toIndex;
		list.moveElement(fromIndex, toIndex);
	}

	//------------------------------------------------------------------

	private void onAccept()
	{
		accepted = true;
		onClose();
	}

	//------------------------------------------------------------------

	private void onClose()
	{
		setVisible(false);
		dispose();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Member classes : inner classes
////////////////////////////////////////////////////////////////////////


	// WINDOW EVENT HANDLER CLASS


	private class WindowEventHandler
		extends WindowAdapter
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private WindowEventHandler()
		{
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		public void windowOpened(WindowEvent event)
		{
			SingleSelectionListEditor.this.windowOpened();
		}

		//--------------------------------------------------------------

		@Override
		public void windowClosing(WindowEvent event)
		{
			onClose();
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

		@Override
		public void actionPerformed(ActionEvent event)
		{
			SingleSelectionListEditor.this.actionPerformed(event);
		}

		//--------------------------------------------------------------

	}

	//==================================================================

}

//----------------------------------------------------------------------
