/*====================================================================*\

SingleSelectionList.java

Class: single-selection list.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.swing.list;

//----------------------------------------------------------------------


// IMPORTS


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import uk.blankaspect.common.list.IListModel;

import uk.blankaspect.ui.swing.action.KeyAction;

import uk.blankaspect.ui.swing.colour.Colours;

import uk.blankaspect.ui.swing.font.FontKey;
import uk.blankaspect.ui.swing.font.FontUtils;

import uk.blankaspect.ui.swing.misc.GuiConstants;
import uk.blankaspect.ui.swing.misc.GuiUtils;

import uk.blankaspect.ui.swing.modifiers.InputModifiers;

import uk.blankaspect.ui.swing.text.TextRendering;

//----------------------------------------------------------------------


// CLASS: SINGLE-SELECTION LIST


public class SingleSelectionList<E>
	extends JComponent
	implements ActionListener, FocusListener, MouseListener, MouseMotionListener, MouseWheelListener, Scrollable
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	public static final		int		DEFAULT_HORIZONTAL_MARGIN	= 4;
	public static final		int		DEFAULT_VERTICAL_MARGIN		= 1;

	public static final		Color	DEFAULT_DISABLED_TEXT_COLOUR	= new Color(144, 144, 144);

	private static final	int		UNIT_INCREMENT_ROWS		= 1;
	private static final	int		BLOCK_INCREMENT_ROWS	= 8;

	private static final	int		DRAG_BAR_HEIGHT	= 6;

	// Commands
	public interface Command
	{
		String	EDIT_ELEMENT		= "editElement";
		String	DELETE_ELEMENT		= "deleteElement";
		String	DELETE_EX_ELEMENT	= "deleteExElement";
		String	MOVE_ELEMENT_UP		= "moveElementUp";
		String	MOVE_ELEMENT_DOWN	= "moveElementDown";
		String	DRAG_ELEMENT		= "dragElement";
	}

	private interface ListCommand
	{
		String	SELECT_UP_UNIT		= "selectUpUnit";
		String	SELECT_DOWN_UNIT	= "selectDownUnit";
		String	SELECT_UP_BLOCK		= "selectUpBlock";
		String	SELECT_DOWN_BLOCK	= "selectDownBlock";
		String	SELECT_UP_MAX		= "selectUpMax";
		String	SELECT_DOWN_MAX		= "selectDownMax";
		String	SCROLL_UP_UNIT		= "scrollUpUnit";
		String	SCROLL_DOWN_UNIT	= "scrollDownUnit";
	}

	private static final	KeyAction.KeyCommandPair[]	KEY_COMMANDS	=
	{
		new KeyAction.KeyCommandPair
		(
			KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
			Command.EDIT_ELEMENT
		),
		new KeyAction.KeyCommandPair
		(
			KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0),
			Command.DELETE_ELEMENT
		),
		new KeyAction.KeyCommandPair
		(
			KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, KeyEvent.SHIFT_DOWN_MASK),
			Command.DELETE_EX_ELEMENT
		),
		new KeyAction.KeyCommandPair
		(
			KeyStroke.getKeyStroke(KeyEvent.VK_UP, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK),
			Command.MOVE_ELEMENT_UP
		),
		new KeyAction.KeyCommandPair
		(
			KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK),
			Command.MOVE_ELEMENT_DOWN
		),
		new KeyAction.KeyCommandPair
		(
			KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0),
			ListCommand.SELECT_UP_UNIT
		),
		new KeyAction.KeyCommandPair
		(
			KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0),
			ListCommand.SELECT_DOWN_UNIT
		),
		new KeyAction.KeyCommandPair
		(
			KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, 0),
			ListCommand.SELECT_UP_BLOCK
		),
		new KeyAction.KeyCommandPair
		(
			KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, 0),
			ListCommand.SELECT_DOWN_BLOCK
		),
		new KeyAction.KeyCommandPair
		(
			KeyStroke.getKeyStroke(KeyEvent.VK_HOME, 0),
			ListCommand.SELECT_UP_MAX
		),
		new KeyAction.KeyCommandPair
		(
			KeyStroke.getKeyStroke(KeyEvent.VK_END, 0),
			ListCommand.SELECT_DOWN_MAX
		)
	};

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	int							columns;
	private	int							viewableRows;
	private	int							columnWidth;
	private	int							rowHeight;
	private	int							horizontalMargin;
	private	int							extraWidth;
	private	Color						disabledTextColour;
	private	int							selectedIndex;
	private	boolean						dragEnabled;
	private	int							dragIndex;
	private	int							dragStartIndex;
	private	int							dragEndIndex;
	private	Integer						dragY;
	private	JViewport					viewport;
	private	Popup						popUp;
	private	IListModel<E>				model;
	private	List<ActionListener>		actionListeners;
	private	List<ListSelectionListener>	selectionListeners;
	private	List<IModelListener>		modelListeners;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public SingleSelectionList(
		int		columns,
		int		viewableRows,
		Font	font)
	{
		this(columns, viewableRows, 0, 0, font);
	}

	//------------------------------------------------------------------

	public SingleSelectionList(
		int		columns,
		int		viewableRows,
		Font	font,
		E[]		elements)
	{
		this(columns, viewableRows, 0, 0, font);
		setElements(elements);
	}

	//------------------------------------------------------------------

	public SingleSelectionList(
		int		columns,
		int		viewableRows,
		Font	font,
		List<E>	elements)
	{
		this(columns, viewableRows, 0, 0, font);
		setElements(elements);
	}

	//------------------------------------------------------------------

	public SingleSelectionList(
		int				columns,
		int				viewableRows,
		Font			font,
		IListModel<E>	model)
	{
		this(columns, viewableRows, 0, 0, font);
		setModel(model);
	}

	//------------------------------------------------------------------

	public SingleSelectionList(
		int		columns,
		int		viewableRows,
		int		columnWidth,
		int		rowHeight,
		Font	font)
	{
		// Initialise instance variables
		this.columns = columns;
		this.viewableRows = viewableRows;
		FontMetrics fontMetrics = getFontMetrics(font);
		this.columnWidth = (columnWidth == 0) ? FontUtils.getCharWidth('0', fontMetrics) : columnWidth;
		this.rowHeight = (rowHeight == 0)
									? 2 * DEFAULT_VERTICAL_MARGIN + fontMetrics.getAscent() + fontMetrics.getDescent()
									: rowHeight;
		horizontalMargin = DEFAULT_HORIZONTAL_MARGIN;
		disabledTextColour = DEFAULT_DISABLED_TEXT_COLOUR;
		selectedIndex = -1;
		dragEnabled = true;
		dragIndex = -1;
		dragStartIndex = -1;
		dragEndIndex = -1;
		model = new DefaultModel<>();

		// Set properties
		setFont(font);
		setForeground(Colours.List.FOREGROUND.getColour());
		setBackground(Colours.List.BACKGROUND.getColour());
		setOpaque(true);
		setFocusable(true);
		setAutoscrolls(true);

		// Add commands to action map
		KeyAction.create(this, JComponent.WHEN_FOCUSED, this, KEY_COMMANDS);

		// Add listeners
		addFocusListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
	}

	//------------------------------------------------------------------

	public SingleSelectionList(
		int		columns,
		int		viewableRows,
		int		columnWidth,
		int		rowHeight,
		Font	font,
		E[]		elements)
	{
		this(columns, viewableRows, columnWidth, rowHeight, font);
		setElements(elements);
	}

	//------------------------------------------------------------------

	public SingleSelectionList(
		int		columns,
		int		viewableRows,
		int		columnWidth,
		int		rowHeight,
		Font	font,
		List<E>	elements)
	{
		this(columns, viewableRows, columnWidth, rowHeight, font);
		setElements(elements);
	}

	//------------------------------------------------------------------

	public SingleSelectionList(
		int				columns,
		int				viewableRows,
		int				columnWidth,
		int				rowHeight,
		Font			font,
		IListModel<E>	model)
	{
		this(columns, viewableRows, columnWidth, rowHeight, font);
		setModel(model);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	protected static String truncateText(
		String		text,
		FontMetrics	fontMetrics,
		int			maxTextWidth)
	{
		int textWidth = fontMetrics.stringWidth(text);
		if (textWidth > maxTextWidth)
		{
			maxTextWidth -= fontMetrics.stringWidth(GuiConstants.ELLIPSIS_STR);
			char[] chars = text.toCharArray();
			int length = chars.length;
			while ((length > 0) && (textWidth > maxTextWidth))
				textWidth -= fontMetrics.charWidth(chars[--length]);
			text = new String(chars, 0, length) + GuiConstants.ELLIPSIS_STR;
		}
		return text;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : ActionListener interface
////////////////////////////////////////////////////////////////////////

	@Override
	public void actionPerformed(
		ActionEvent	event)
	{
		if (isEnabled())
		{
			String command = event.getActionCommand();

			if (command.equals(Command.EDIT_ELEMENT))
				onEditElement();

			else if (command.equals(Command.DELETE_ELEMENT))
				onDeleteElement();

			else if (command.equals(Command.DELETE_EX_ELEMENT))
				onDeleteExElement();

			else if (command.equals(Command.MOVE_ELEMENT_UP))
				onMoveElementUp();

			else if (command.equals(Command.MOVE_ELEMENT_DOWN))
				onMoveElementDown();

			else if (command.equals(ListCommand.SELECT_UP_UNIT))
				onSelectUpUnit();

			else if (command.equals(ListCommand.SELECT_DOWN_UNIT))
				onSelectDownUnit();

			else if (command.equals(ListCommand.SELECT_UP_BLOCK))
				onSelectUpBlock();

			else if (command.equals(ListCommand.SELECT_DOWN_BLOCK))
				onSelectDownBlock();

			else if (command.equals(ListCommand.SELECT_UP_MAX))
				onSelectUpMax();

			else if (command.equals(ListCommand.SELECT_DOWN_MAX))
				onSelectDownMax();

			else if (command.equals(ListCommand.SCROLL_UP_UNIT))
				onScrollUpUnit();

			else if (command.equals(ListCommand.SCROLL_DOWN_UNIT))
				onScrollDownUnit();
		}
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : FocusListener interface
////////////////////////////////////////////////////////////////////////

	@Override
	public void focusGained(
		FocusEvent	event)
	{
		repaint();
	}

	//------------------------------------------------------------------

	@Override
	public void focusLost(
		FocusEvent	event)
	{
		repaint();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : MouseListener interface
////////////////////////////////////////////////////////////////////////

	@Override
	public void mouseClicked(
		MouseEvent	event)
	{
		if (isEnabled() && SwingUtilities.isLeftMouseButton(event) && (event.getClickCount() > 1))
		{
			int index = event.getY() / rowHeight;
			if ((index >= 0) && (index < getNumElements()))
				onEditElement();
		}
	}

	//------------------------------------------------------------------

	@Override
	public void mouseEntered(
		MouseEvent	event)
	{
		// do nothing
	}

	//------------------------------------------------------------------

	@Override
	public void mouseExited(
		MouseEvent	event)
	{
		// do nothing
	}

	//------------------------------------------------------------------

	@Override
	public void mousePressed(
		MouseEvent	event)
	{
		if (isEnabled())
		{
			requestFocusInWindow();

			if (SwingUtilities.isLeftMouseButton(event))
			{
				int index = event.getY() / rowHeight;
				if ((index >= 0) && (index < getNumElements()))
				{
					if (InputModifiers.forEvent(event).isControl())
						showPopUp(index);
					else
					{
						dragStartIndex = index;
						setSelectedIndex(index);
					}
				}
				else
					dragStartIndex = -1;
			}
		}
	}

	//------------------------------------------------------------------

	@Override
	public void mouseReleased(
		MouseEvent	event)
	{
		if (SwingUtilities.isLeftMouseButton(event))
		{
			hidePopUp();

			if (isDragging())
			{
				dragEndIndex = dragIndex;
				setDragIndex(-1);
				if (dragEndIndex != selectedIndex)
					fireActionPerformed(Command.DRAG_ELEMENT);
			}
		}
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : MouseMotionListener interface
////////////////////////////////////////////////////////////////////////

	@Override
	public void mouseDragged(
		MouseEvent	event)
	{
		if (SwingUtilities.isLeftMouseButton(event))
		{
			boolean dragging = isDragging();
			if (dragging)
				hidePopUp();

			if (isEnabled() && dragEnabled && !dragging)
			{
				int index = event.getY() / rowHeight;
				if ((dragStartIndex >= 0) && (dragStartIndex != index))
					dragging = true;
			}
			if (dragging)
			{
				int index = Math.min(Math.max(0, (event.getY() + rowHeight / 2) / rowHeight),
									 getNumElements());
				scrollRectToVisible(new Rectangle(event.getX(), index * rowHeight, 1, rowHeight));
				if (dragIndex != index)
					setDragIndex(index);
			}
		}
	}

	//------------------------------------------------------------------

	@Override
	public void mouseMoved(
		MouseEvent	event)
	{
		// do nothing
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : MouseWheelListener interface
////////////////////////////////////////////////////////////////////////

	@Override
	public void mouseWheelMoved(
		MouseWheelEvent	event)
	{
		if (viewport != null)
		{
			String command = null;
			int numUnits = event.getWheelRotation();
			if (numUnits < 0)
			{
				numUnits = -numUnits;
				command = ListCommand.SCROLL_UP_UNIT;
			}
			else
				command = ListCommand.SCROLL_DOWN_UNIT;

			while (--numUnits >= 0)
				actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, command));
		}
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : Scrollable interface
////////////////////////////////////////////////////////////////////////

	@Override
	public Dimension getPreferredScrollableViewportSize()
	{
		return new Dimension(2 * horizontalMargin + columns * columnWidth + extraWidth,
							 viewableRows * rowHeight);
	}

	//------------------------------------------------------------------

	@Override
	public boolean getScrollableTracksViewportWidth()
	{
		return true;
	}

	//------------------------------------------------------------------

	@Override
	public boolean getScrollableTracksViewportHeight()
	{
		return false;
	}

	//------------------------------------------------------------------

	@Override
	public int getScrollableUnitIncrement(
		Rectangle	visibleRect,
		int			orientation,
		int			direction)
	{
		int delta = 0;
		if (orientation == SwingConstants.VERTICAL)
		{
			int y = visibleRect.y;
			if (direction < 0)
			{
				int row = (Math.max(0, y) + rowHeight - 1) / rowHeight;
				delta = y - (Math.max(0, row - UNIT_INCREMENT_ROWS) * rowHeight);
			}
			else
			{
				int row = Math.max(0, y) / rowHeight;
				int maxRow = Math.max(0, getHeight() - visibleRect.height) / rowHeight;
				delta = Math.min(row + UNIT_INCREMENT_ROWS, maxRow) * rowHeight - y;
			}
		}
		return delta;
	}

	//------------------------------------------------------------------

	@Override
	public int getScrollableBlockIncrement(
		Rectangle	visibleRect,
		int			orientation,
		int			direction)
	{
		int delta = 0;
		if (orientation == SwingConstants.VERTICAL)
		{
			int y = visibleRect.y;
			if (direction < 0)
			{
				int row = (Math.max(0, y) + rowHeight - 1) / rowHeight;
				delta = y - (Math.max(0, row - BLOCK_INCREMENT_ROWS) * rowHeight);
			}
			else
			{
				int row = Math.max(0, y) / rowHeight;
				int maxRow = Math.max(0, getHeight() - visibleRect.height) / rowHeight;
				delta = Math.min(row + BLOCK_INCREMENT_ROWS, maxRow) * rowHeight - y;
			}
		}
		return delta;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	@Override
	public Dimension getMinimumSize()
	{
		return getPreferredSize();
	}

	//------------------------------------------------------------------

	@Override
	public Dimension getPreferredSize()
	{
		// Calculate width
		int width = 2 * horizontalMargin + columns * columnWidth + extraWidth;
		if (viewport != null)
			width = Math.max(width, viewport.getWidth());

		// Calculate height
		int height = Math.max(viewableRows, getNumElements()) * rowHeight;
		if (viewport != null)
			height = Math.max(height, viewport.getHeight());

		// Return dimensions
		return new Dimension(width, height);
	}

	//------------------------------------------------------------------

	@Override
	protected void paintComponent(
		Graphics	gr)
	{
		// Fill background
		Rectangle rect = gr.getClipBounds();
		gr.setColor(getBackground());
		gr.fillRect(rect.x, rect.y, rect.width, rect.height);

		// Get start and end indices of rows
		int startIndex = Math.max(0, rect.y / rowHeight);
		int endIndex = Math.max(0, rect.y + rect.height + rowHeight - 1) / rowHeight;

		// Draw cells
		for (int i = startIndex; i < endIndex; i++)
		{
			if (isEnabled() && (i == selectedIndex))
			{
				gr.setColor(getBackgroundColour(i));
				gr.fillRect(rect.x, i * rowHeight, rect.width, rowHeight);
			}

			if (i < getNumElements())
				drawElement(gr, i);
		}

		// Draw drag bar
		if (isDragging())
		{
			int x1 = 0;
			int x2 = getWidth() - 1;
			int y = dragIndex * rowHeight - DRAG_BAR_HEIGHT / 2;
			gr.setColor(Colours.List.DRAG_BAR.getColour());
			gr.drawLine(x1, y, x1, y);
			gr.drawLine(x2, y, x2, y);
			++y;
			gr.drawLine(x1, y, x1 + 1, y);
			gr.drawLine(x2 - 1, y, x2, y);
			++y;
			gr.drawLine(rect.x, y, rect.x + rect.width - 1, y);
			++y;
			gr.drawLine(rect.x, y, rect.x + rect.width - 1, y);
			++y;
			gr.drawLine(x1, y, x1 + 1, y);
			gr.drawLine(x2 - 1, y, x2, y);
			++y;
			gr.drawLine(x1, y, x1, y);
			gr.drawLine(x2, y, x2, y);
		}
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public int getColumns()
	{
		return columns;
	}

	//------------------------------------------------------------------

	public int getViewableRows()
	{
		return viewableRows;
	}

	//------------------------------------------------------------------

	public int getColumnWidth()
	{
		return columnWidth;
	}

	//------------------------------------------------------------------

	public int getRowHeight()
	{
		return rowHeight;
	}

	//------------------------------------------------------------------

	public int getHorizontalMargin()
	{
		return horizontalMargin;
	}

	//------------------------------------------------------------------

	public int getExtraWidth()
	{
		return extraWidth;
	}

	//------------------------------------------------------------------

	public Color getDisabledTextColour()
	{
		return disabledTextColour;
	}

	//------------------------------------------------------------------

	public int getSelectedIndex()
	{
		return selectedIndex;
	}

	//------------------------------------------------------------------

	public boolean isDragging()
	{
		return (dragIndex >= 0);
	}

	//------------------------------------------------------------------

	public int getDragEndIndex()
	{
		return dragEndIndex;
	}

	//------------------------------------------------------------------

	public JViewport getViewport()
	{
		return viewport;
	}

	//------------------------------------------------------------------

	public IListModel<E> getModel()
	{
		return model;
	}

	//------------------------------------------------------------------

	public boolean isEmpty()
	{
		return (getNumElements() == 0);
	}

	//------------------------------------------------------------------

	public int getNumElements()
	{
		return model.getNumElements();
	}

	//------------------------------------------------------------------

	public E getElement(
		int	index)
	{
		return model.getElement(index);
	}

	//------------------------------------------------------------------

	public String getElementText(
		int	index)
	{
		return model.getElementText(index);
	}

	//------------------------------------------------------------------

	public List<E> getElements()
	{
		List<E> elements = new ArrayList<>();
		for (int i = 0; i < getNumElements(); i++)
			elements.add(getElement(i));
		return elements;
	}

	//------------------------------------------------------------------

	public E getSelectedElement()
	{
		return ((selectedIndex < 0) ? null : getElement(selectedIndex));
	}

	//------------------------------------------------------------------

	public boolean isSelection()
	{
		return (selectedIndex >= 0);
	}

	//------------------------------------------------------------------

	public boolean isDragEnabled()
	{
		return dragEnabled;
	}

	//------------------------------------------------------------------

	public void setColumnWidth(
		int	columnWidth)
	{
		if (this.columnWidth != columnWidth)
		{
			this.columnWidth = columnWidth;
			resize();
			repaint();
		}
	}

	//------------------------------------------------------------------

	public void setRowHeight(
		int	rowHeight)
	{
		if (this.rowHeight != rowHeight)
		{
			this.rowHeight = rowHeight;
			resize();
			repaint();
		}
	}

	//------------------------------------------------------------------

	public void setHorizontalMargin(
		int	horizontalMargin)
	{
		if (this.horizontalMargin != horizontalMargin)
		{
			this.horizontalMargin = horizontalMargin;
			resize();
			repaint();
		}
	}

	//------------------------------------------------------------------

	public void setExtraWidth(
		int	extraWidth)
	{
		if (this.extraWidth != extraWidth)
		{
			this.extraWidth = extraWidth;
			resize();
			repaint();
		}
	}

	//------------------------------------------------------------------

	public void setDisabledTextColour(
		Color	colour)
	{
		if (!disabledTextColour.equals(colour))
		{
			disabledTextColour = colour;
			repaint();
		}
	}

	//------------------------------------------------------------------

	public void setSelectedIndex(
		int	index)
	{
		if (index != selectedIndex)
			forceSelectedIndex(index);
	}

	//------------------------------------------------------------------

	public void setDragEnabled(
		boolean	enabled)
	{
		dragEnabled = enabled;
	}

	//------------------------------------------------------------------

	public void setViewport(
		JViewport	viewport)
	{
		this.viewport = viewport;
	}

	//------------------------------------------------------------------

	public void setModel(
		IListModel<E>	model)
	{
		this.model = model;
		resizeAndRedraw();
	}

	//------------------------------------------------------------------

	public void setElement(
		int	index,
		E	element)
	{
		model.setElement(index, element);
		redrawElements(index, 1);
		fireModelChanged();
	}

	//------------------------------------------------------------------

	public void setElements(
		E[]	elements)
	{
		setElements((elements == null) ? null : List.of(elements));
	}

	//------------------------------------------------------------------

	public void setElements(
		List<E>	elements)
	{
		if (model instanceof DefaultModel<?>)
			((DefaultModel<E>)model).setElements(elements);
		else
		{
			while (getNumElements() > 0)
				model.removeElement(getNumElements() - 1);
			if (elements != null)
			{
				for (E element : elements)
					model.addElement(getNumElements(), element);
			}
		}
		resizeAndRedraw();
		fireModelChanged();
	}

	//------------------------------------------------------------------

	public void addElement(
		E	element)
	{
		addElement(getNumElements(), element);
	}

	//------------------------------------------------------------------

	public void addElement(
		int	index,
		E	element)
	{
		model.addElement(index, element);
		resize();
		redrawElements(index, getNumElements() - index);
		forceSelectedIndex(index);
		fireModelChanged();
	}

	//------------------------------------------------------------------

	public void removeElement(
		int	index)
	{
		int length = getNumElements() - index;
		model.removeElement(index);
		resize();
		redrawElements(index, length);
		if (selectedIndex >= getNumElements())
			forceSelectedIndex(getNumElements() - 1);
		fireModelChanged();
	}

	//------------------------------------------------------------------

	public void moveElement(
		int	fromIndex,
		int	toIndex)
	{
		if (toIndex != fromIndex)
		{
			model.addElement(toIndex, model.removeElement(fromIndex));
			redrawElements(Math.min(fromIndex, toIndex), Math.abs(fromIndex - toIndex) + 1);
			forceSelectedIndex(toIndex);
			fireModelChanged();
		}
		else
			forceSelectedIndex(toIndex);
	}

	//------------------------------------------------------------------

	public int findIndex(
		E	element)
	{
		for (int i = 0; i < getNumElements(); i++)
		{
			if (getElement(i).equals(element))
				return i;
		}
		return -1;
	}

	//------------------------------------------------------------------

	public void snapViewPosition()
	{
		if (viewport != null)
		{
			Point viewPosition = viewport.getViewPosition();
			int y = Math.max(0, viewPosition.y) / rowHeight * rowHeight;
			if (viewPosition.y != y)
			{
				viewPosition.y = y;
				viewport.setViewPosition(viewPosition);
			}
		}
	}

	//------------------------------------------------------------------

	public void addActionListener(
		ActionListener	listener)
	{
		if (actionListeners == null)
			actionListeners = new ArrayList<>();
		actionListeners.add(listener);
	}

	//------------------------------------------------------------------

	public void removeActionListener(
		ActionListener	listener)
	{
		if (actionListeners != null)
			actionListeners.remove(listener);
	}

	//------------------------------------------------------------------

	public void addListSelectionListener(
		ListSelectionListener	listener)
	{
		if (selectionListeners == null)
			selectionListeners = new ArrayList<>();
		selectionListeners.add(listener);
	}

	//------------------------------------------------------------------

	public void removeListSelectionListener(
		ListSelectionListener	listener)
	{
		if (selectionListeners != null)
			selectionListeners.remove(listener);
	}

	//------------------------------------------------------------------

	public void addModelListener(
		IModelListener	listener)
	{
		if (modelListeners == null)
			modelListeners = new ArrayList<>();
		modelListeners.add(listener);
	}

	//------------------------------------------------------------------

	public void removeModelListener(
		IModelListener	listener)
	{
		if (modelListeners != null)
			modelListeners.remove(listener);
	}

	//------------------------------------------------------------------

	protected void fireActionPerformed(
		String	command)
	{
		if (actionListeners != null)
		{
			ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, command);
			for (int i = actionListeners.size() - 1; i >= 0; i--)
				actionListeners.get(i).actionPerformed(event);
		}
	}

	//------------------------------------------------------------------

	protected void fireSelectionChanged()
	{
		if (selectionListeners != null)
		{
			ListSelectionEvent event = new ListSelectionEvent(this, selectedIndex, selectedIndex, false);
			for (int i = selectionListeners.size() - 1; i >= 0; i--)
				selectionListeners.get(i).valueChanged(event);
		}
	}

	//------------------------------------------------------------------

	protected void fireModelChanged()
	{
		if (modelListeners != null)
		{
			ModelEvent event = new ModelEvent(this);
			for (int i = modelListeners.size() - 1; i >= 0; i--)
				modelListeners.get(i).modelChanged(event);
		}
	}

	//------------------------------------------------------------------

	protected Color getBackgroundColour(
		int	index)
	{
		return (index == selectedIndex)
							? isFocusOwner()
									? Colours.List.FOCUSED_SELECTION_BACKGROUND.getColour()
									: Colours.List.SELECTION_BACKGROUND.getColour()
							: getBackground();
	}

	//------------------------------------------------------------------

	protected Color getForegroundColour(
		int	index)
	{
		return (isEnabled() ? (index == selectedIndex)
											? isFocusOwner()
													? Colours.List.FOCUSED_SELECTION_FOREGROUND.getColour()
													: Colours.List.SELECTION_FOREGROUND.getColour()
											: getForeground()
							: disabledTextColour);
	}

	//------------------------------------------------------------------

	protected int getMaxTextWidth()
	{
		return (getWidth() - 2 * horizontalMargin - extraWidth);
	}

	//------------------------------------------------------------------

	protected void drawElement(
		Graphics	gr,
		int			index)
	{
		// Create copy of graphics context
		Graphics2D gr2d = GuiUtils.copyGraphicsContext(gr);

		// Set rendering hints for text antialiasing and fractional metrics
		TextRendering.setHints(gr2d);

		// Get text and truncate it if it is too wide
		FontMetrics fontMetrics = gr2d.getFontMetrics();
		String text = truncateText(getElementText(index), fontMetrics, getMaxTextWidth());

		// Draw text
		gr2d.setColor(getForegroundColour(index));
		gr2d.drawString(text, horizontalMargin,
						index * rowHeight + FontUtils.getBaselineOffset(rowHeight, fontMetrics));
	}

	//------------------------------------------------------------------

	protected String getPopUpText(
		int	index)
	{
		return getElementText(index);
	}

	//------------------------------------------------------------------

	protected int getPopUpXOffset()
	{
		return 0;
	}

	//------------------------------------------------------------------

	protected boolean isShowPopUp(
		int	index)
	{
		return (getFontMetrics(getFont()).stringWidth(getElementText(index)) > getMaxTextWidth());
	}

	//------------------------------------------------------------------

	protected void showPopUp(
		int	index)
	{
		if (isShowPopUp(index))
		{
			PopUpComponent popUpComponent = new PopUpComponent(getPopUpText(index));
			Point location = getLocationOnScreen();
			Rectangle screen = GuiUtils.getVirtualScreenBounds(this);
			int x = Math.min(location.x + getPopUpXOffset(),
							 screen.x + screen.width - popUpComponent.getPreferredSize().width);
			int y = location.y + index * rowHeight - 1;
			popUp = PopupFactory.getSharedInstance().getPopup(this, popUpComponent, x, y);
			popUp.show();
		}
	}

	//------------------------------------------------------------------

	protected void hidePopUp()
	{
		if (popUp != null)
		{
			popUp.hide();
			popUp = null;
		}
	}

	//------------------------------------------------------------------

	protected void resize()
	{
		if (viewport != null)
			viewport.setViewSize(getPreferredSize());
		revalidate();
	}

	//------------------------------------------------------------------

	protected void resizeAndRedraw()
	{
		resize();
		selectedIndex = -1;
		repaint();
		forceSelectedIndex((getNumElements() > 0) ? 0 : -1);
	}

	//------------------------------------------------------------------

	protected void forceSelectedIndex(
		int	index)
	{
		// Remove current selection
		if (selectedIndex >= 0)
		{
			int oldSelectedIndex = selectedIndex;
			selectedIndex = -1;
			redrawElements(oldSelectedIndex, 1);
		}

		// Set selected index
		if ((index >= -1) && (index < getNumElements()))
		{
			selectedIndex = index;
			makeSelectionViewable();
			if (selectedIndex >= 0)
				redrawElements(selectedIndex, 1);
			fireSelectionChanged();
		}
	}

	//------------------------------------------------------------------

	private void redrawElements(
		int	index,
		int	length)
	{
		repaint(0, index * rowHeight, getWidth(), length * rowHeight);
	}

	//------------------------------------------------------------------

	private void setDragIndex(
		int	index)
	{
		// Erase old drag bar
		if (dragY != null)
		{
			dragIndex = -1;
			repaint(0, dragY, getWidth(), DRAG_BAR_HEIGHT);
			dragY = null;
		}

		// Draw new drag bar
		if (index >= 0)
		{
			dragIndex = index;
			int y = index * rowHeight - DRAG_BAR_HEIGHT / 2;
			repaint(0, y, getWidth(), DRAG_BAR_HEIGHT);
			dragY = y;
		}
	}

	//------------------------------------------------------------------

	private void incrementSelectedIndex(
		int	increment)
	{
		int index = (selectedIndex < 0) ? 0 : selectedIndex;
		setSelectedIndex(Math.min(Math.max(0, index + increment), getNumElements() - 1));
	}

	//------------------------------------------------------------------

	private void makeSelectionViewable()
	{
		if ((viewport != null) && (selectedIndex >= 0))
		{
			Point viewPosition = viewport.getViewPosition();
			int y = viewPosition.y;
			int startIndex = y / rowHeight;
			if (selectedIndex < startIndex)
				y = selectedIndex * rowHeight;
			else
			{
				if (selectedIndex >= startIndex + viewableRows)
					y = Math.max(0, selectedIndex - viewableRows + 1) * rowHeight;
			}
			if (viewPosition.y != y)
			{
				viewPosition.y = y;
				viewport.setViewPosition(viewPosition);
			}
		}
	}

	//------------------------------------------------------------------

	private void incrementViewY(
		int	deltaY)
	{
		if (deltaY != 0)
			viewport.setViewPosition(new Point(viewport.getViewPosition().x, viewport.getViewPosition().y + deltaY));
	}

	//------------------------------------------------------------------

	private void onEditElement()
	{
		if (selectedIndex >= 0)
			fireActionPerformed(Command.EDIT_ELEMENT);
	}

	//------------------------------------------------------------------

	private void onDeleteElement()
	{
		if (selectedIndex >= 0)
			fireActionPerformed(Command.DELETE_ELEMENT);
	}

	//------------------------------------------------------------------

	private void onDeleteExElement()
	{
		if (selectedIndex >= 0)
			fireActionPerformed(Command.DELETE_EX_ELEMENT);
	}

	//------------------------------------------------------------------

	private void onMoveElementUp()
	{
		if (selectedIndex > 0)
			fireActionPerformed(Command.MOVE_ELEMENT_UP);
	}

	//------------------------------------------------------------------

	private void onMoveElementDown()
	{
		if ((selectedIndex >= 0) && (selectedIndex < getNumElements() - 1))
			fireActionPerformed(Command.MOVE_ELEMENT_DOWN);
	}

	//------------------------------------------------------------------

	private void onSelectUpUnit()
	{
		incrementSelectedIndex(-UNIT_INCREMENT_ROWS);
	}

	//------------------------------------------------------------------

	private void onSelectDownUnit()
	{
		incrementSelectedIndex(UNIT_INCREMENT_ROWS);
	}

	//------------------------------------------------------------------

	private void onSelectUpBlock()
	{
		incrementSelectedIndex(-BLOCK_INCREMENT_ROWS);
	}

	//------------------------------------------------------------------

	private void onSelectDownBlock()
	{
		incrementSelectedIndex(BLOCK_INCREMENT_ROWS);
	}

	//------------------------------------------------------------------

	private void onSelectUpMax()
	{
		setSelectedIndex(0);
	}

	//------------------------------------------------------------------

	private void onSelectDownMax()
	{
		setSelectedIndex(getNumElements() - 1);
	}

	//------------------------------------------------------------------

	private void onScrollUpUnit()
	{
		if (viewport != null)
			incrementViewY(-getScrollableUnitIncrement(viewport.getViewRect(), SwingConstants.VERTICAL, -1));
	}

	//------------------------------------------------------------------

	private void onScrollDownUnit()
	{
		if (viewport != null)
			incrementViewY(getScrollableUnitIncrement(viewport.getViewRect(), SwingConstants.VERTICAL, 1));
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Member interfaces
////////////////////////////////////////////////////////////////////////


	// LIST MODEL LISTENER INTERFACE


	public interface IModelListener
	{

	////////////////////////////////////////////////////////////////////
	//  Methods
	////////////////////////////////////////////////////////////////////

		void modelChanged(
			ModelEvent	event);

		//--------------------------------------------------------------

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// CLASS: LIST MODEL EVENT


	public static class ModelEvent
		extends EventObject
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private ModelEvent(
			SingleSelectionList<?>	source)
		{
			super(source);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		public SingleSelectionList<?> getSource()
		{
			return (SingleSelectionList<?>)source;
		}

		//--------------------------------------------------------------

	}

	//==================================================================


	// CLASS: DEFAULT LIST MODEL


	public static class DefaultModel<E>
		extends ArrayList<E>
		implements IListModel<E>
	{

	////////////////////////////////////////////////////////////////////
	//  Instance methods : Model interface
	////////////////////////////////////////////////////////////////////

		@Override
		public int getNumElements()
		{
			return size();
		}

		//--------------------------------------------------------------

		@Override
		public E getElement(
			int	index)
		{
			return get(index);
		}

		//--------------------------------------------------------------

		@Override
		public String getElementText(
			int	index)
		{
			return get(index).toString();
		}

		//--------------------------------------------------------------

		@Override
		public void setElement(
			int	index,
			E	element)
		{
			set(index, element);
		}

		//--------------------------------------------------------------

		@Override
		public void addElement(
			int	index,
			E	element)
		{
			add(index, element);
		}

		//--------------------------------------------------------------

		@Override
		public E removeElement(
			int	index)
		{
			return remove(index);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		public void setElements(
			List<E>	elements)
		{
			clear();
			if (elements != null)
				addAll(elements);
		}

		//--------------------------------------------------------------

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Member classes : inner classes
////////////////////////////////////////////////////////////////////////


	// CLASS: POP-UP COMPONENT


	protected class PopUpComponent
		extends JComponent
	{

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	String	text;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		public PopUpComponent(
			String	text)
		{
			// Initialise instance variables
			this.text = text;

			// Set properties
			FontUtils.setAppFont(FontKey.MAIN, this);
			int width = 2 * getHorizontalMargin() + getFontMetrics(getFont()).stringWidth(text);
			setPreferredSize(new Dimension(width, rowHeight + 2));
			setOpaque(true);
			setFocusable(false);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		protected void paintComponent(
			Graphics	gr)
		{
			// Create copy of graphics context
			Graphics2D gr2d = GuiUtils.copyGraphicsContext(gr);

			// Get dimensions
			int width = getWidth();
			int height = getHeight();

			// Fill interior
			gr2d.setColor(SingleSelectionList.this.getBackground());
			gr2d.fillRect(0, 0, width, height);

			// Set rendering hints for text antialiasing and fractional metrics
			TextRendering.setHints(gr2d);

			// Draw text
			gr2d.setColor(getForeground());
			gr2d.drawString(text, getHorizontalMargin(), FontUtils.getBaselineOffset(height, gr2d.getFontMetrics()));

			// Draw border
			gr2d.setColor(Colours.LINE_BORDER);
			gr2d.drawRect(0, 0, width - 1, height - 1);
		}

		//--------------------------------------------------------------

	}

	//==================================================================

}

//----------------------------------------------------------------------
