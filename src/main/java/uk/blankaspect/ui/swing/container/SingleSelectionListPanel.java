/*====================================================================*\

SingleSelectionListPanel.java

Single-selection list panel.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.swing.container;

//----------------------------------------------------------------------


// IMPORTS


import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import uk.blankaspect.ui.swing.button.FButton;

import uk.blankaspect.ui.swing.font.FontKey;
import uk.blankaspect.ui.swing.font.FontUtils;

import uk.blankaspect.ui.swing.list.SingleSelectionList;

import uk.blankaspect.ui.swing.misc.GuiConstants;

//----------------------------------------------------------------------


// SINGLE-SELECTION LIST PANEL CLASS


public class SingleSelectionListPanel<E>
	extends JPanel
	implements ActionListener, ChangeListener, ListSelectionListener, SingleSelectionList.IModelListener
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	public enum CommandId
	{
		ADD,
		EDIT,
		DELETE
	}

	protected static final	String	ADD_STR		= "Add";
	protected static final	String	EDIT_STR	= "Edit";
	protected static final	String	DELETE_STR	= "Delete";

	private static final	int	MODIFIERS_MASK	= ActionEvent.ALT_MASK | ActionEvent.META_MASK | ActionEvent.CTRL_MASK
														| ActionEvent.SHIFT_MASK;

	// Commands
	private interface Command
	{
		String	ADD		= "add";
		String	EDIT	= SingleSelectionList.Command.EDIT_ELEMENT;
		String	DELETE	= "delete";
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public SingleSelectionListPanel(int       columns,
									int       viewableRows,
									List<E>   elements,
									int       maxNumElements,
									JButton[] closeButtons)
	{
		this(columns, viewableRows, elements, maxNumElements, closeButtons, EnumSet.noneOf(CommandId.class));
	}

	//------------------------------------------------------------------

	public SingleSelectionListPanel(int            columns,
									int            viewableRows,
									List<E>        elements,
									int            maxNumElements,
									JButton[]      closeButtons,
									Set<CommandId> immediateCommands)
	{
		// Initialise instance variables
		this.maxNumElements = maxNumElements;


		//----  List scroll pane

		// Selection list
		list = new SingleSelectionList<>(columns, viewableRows, FontUtils.getAppFont(FontKey.MAIN),
										 elements);
		list.addActionListener(this);
		list.addListSelectionListener(this);
		list.addModelListener(this);

		// Scroll pane: selection list
		listScrollPane = new JScrollPane(list, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
										 JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		listScrollPane.getVerticalScrollBar().setFocusable(false);
		listScrollPane.getVerticalScrollBar().getModel().addChangeListener(this);

		list.setViewport(listScrollPane.getViewport());


		//----  List button panel

		JPanel listButtonPanel = new JPanel(new GridLayout(0, 1, 0, 8));

		// Button: add
		addButton = new FButton(immediateCommands.contains(CommandId.ADD) ? ADD_STR
																		  : ADD_STR + GuiConstants.ELLIPSIS_STR);
		addButton.setMnemonic(KeyEvent.VK_A);
		addButton.setActionCommand(Command.ADD);
		addButton.addActionListener(this);
		listButtonPanel.add(addButton);

		// Button: edit
		editButton = new FButton(immediateCommands.contains(CommandId.EDIT) ? EDIT_STR
																			: EDIT_STR + GuiConstants.ELLIPSIS_STR);
		editButton.setMnemonic(KeyEvent.VK_E);
		editButton.setActionCommand(Command.EDIT);
		editButton.addActionListener(this);
		listButtonPanel.add(editButton);

		// Button: delete
		deleteButton = new FButton(immediateCommands.contains(CommandId.DELETE)
																			? DELETE_STR
																			: DELETE_STR + GuiConstants.ELLIPSIS_STR);
		deleteButton.setMnemonic(KeyEvent.VK_D);
		deleteButton.setActionCommand(Command.DELETE);
		deleteButton.addActionListener(this);
		listButtonPanel.add(deleteButton);


		//----  Close button panel

		JPanel closeButtonPanel = new JPanel(new GridLayout(0, 1, 0, 8));
		if (closeButtons != null)
		{
			for (JButton button : closeButtons)
				closeButtonPanel.add(button);
		}

		// Update buttons
		updateButtons();


		//----  Outer panel

		GridBagLayout gridBag = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();

		setLayout(gridBag);

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 2;
		gbc.weightx = 1.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(0, 0, 0, 8);
		gridBag.setConstraints(listScrollPane, gbc);
		add(listScrollPane);

		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(0, 0, 0, 0);
		gridBag.setConstraints(listButtonPanel, gbc);
		add(listButtonPanel);

		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.SOUTH;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(12, 0, 0, 0);
		gridBag.setConstraints(closeButtonPanel, gbc);
		add(closeButtonPanel);
	}

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
			updateButtons();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : SingleSelectionList.IModelListener interface
////////////////////////////////////////////////////////////////////////

	@Override
	public void modelChanged(SingleSelectionList.ModelEvent event)
	{
		modelChanged();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public List<E> getElements()
	{
		return list.getElements();
	}

	//------------------------------------------------------------------

	public E getElement(int index)
	{
		return list.getElement(index);
	}

	//------------------------------------------------------------------

	public void setElements(List<E> elements)
	{
		list.setElements(elements);
	}

	//------------------------------------------------------------------

	public void setElement(int index,
						   E   element)
	{
		list.setElement(index, element);
	}

	//------------------------------------------------------------------

	protected SingleSelectionList<E> getList()
	{
		return list;
	}

	//------------------------------------------------------------------

	protected E getAddElement()
	{
		return null;
	}

	//------------------------------------------------------------------

	protected E getEditElement(int index)
	{
		return null;
	}

	//------------------------------------------------------------------

	protected boolean confirmDelete()
	{
		return false;
	}

	//------------------------------------------------------------------

	protected void modelChanged()
	{
		// do nothing
	}

	//------------------------------------------------------------------

	protected void updateButtons()
	{
		addButton.setEnabled(list.getNumElements() < maxNumElements);
		editButton.setEnabled(list.isSelection());
		deleteButton.setEnabled(list.isSelection());
	}

	//------------------------------------------------------------------

	private void onAdd()
	{
		E element = getAddElement();
		if (element != null)
		{
			list.addElement(element);
			updateButtons();
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
		updateButtons();
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

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	int						maxNumElements;
	private	SingleSelectionList<E>	list;
	private	JScrollPane				listScrollPane;
	private	JButton					addButton;
	private	JButton					editButton;
	private	JButton					deleteButton;

}

//----------------------------------------------------------------------
