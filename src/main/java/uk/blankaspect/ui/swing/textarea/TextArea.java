/*====================================================================*\

TextArea.java

Text area class.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.swing.textarea;

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
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.awt.image.BufferedImage;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import uk.blankaspect.ui.swing.action.KeyAction;

import uk.blankaspect.ui.swing.font.FontUtils;

import uk.blankaspect.ui.swing.text.TextRendering;

//----------------------------------------------------------------------


// TEXT AREA CLASS


public class TextArea
	extends JComponent
	implements ActionListener, ChangeListener, MouseListener, Scrollable
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	private static final	int		EOL_IMAGE_WIDTH		= 8;
	private static final	int		EOL_IMAGE_HEIGHT1	= 3;
	private static final	int		EOL_IMAGE_HEIGHT2	= 2;
	private static final	int[]	EOL_BITMAPS			= { 0, 0x38, 0x3F, 0x38, 0 };

	private static final	int	DEFAULT_UNIT_INCREMENT_COLUMNS	= 1;
	private static final	int	DEFAULT_UNIT_INCREMENT_ROWS		= 1;
	private static final	int	DEFAULT_BLOCK_INCREMENT_COLUMNS	= 8;
	private static final	int	DEFAULT_BLOCK_INCREMENT_ROWS	= 8;

	private static final	Color	DEFAULT_HIGHLIGHT_TEXT_COLOUR		= Color.BLACK;
	private static final	Color	DEFAULT_HIGHLIGHT_BACKGROUND_COLOUR	= Color.LIGHT_GRAY;

	// Commands
	private interface Command
	{
		String	SCROLL_LEFT_UNIT	= "scrollLeftUnit";
		String	SCROLL_RIGHT_UNIT	= "scrollRightUnit";
		String	SCROLL_LEFT_BLOCK	= "scrollLeftBlock";
		String	SCROLL_RIGHT_BLOCK	= "scrollRightBlock";
		String	SCROLL_LEFT_MAX		= "scrollLeftMax";
		String	SCROLL_RIGHT_MAX	= "scrollRightMax";
		String	SCROLL_UP_UNIT		= "scrollUpUnit";
		String	SCROLL_DOWN_UNIT	= "scrollDownUnit";
		String	SCROLL_UP_BLOCK		= "scrollUpBlock";
		String	SCROLL_DOWN_BLOCK	= "scrollDownBlock";
		String	SCROLL_UP_MAX		= "scrollUpMax";
		String	SCROLL_DOWN_MAX		= "scrollDownMax";
	}

	private static final	KeyAction.KeyCommandPair[]	KEY_COMMANDS	=
	{
		new KeyAction.KeyCommandPair(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0),
									 Command.SCROLL_LEFT_UNIT),
		new KeyAction.KeyCommandPair(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0),
									 Command.SCROLL_RIGHT_UNIT),
		new KeyAction.KeyCommandPair(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, KeyEvent.CTRL_DOWN_MASK),
									 Command.SCROLL_LEFT_BLOCK),
		new KeyAction.KeyCommandPair(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, KeyEvent.CTRL_DOWN_MASK),
									 Command.SCROLL_RIGHT_BLOCK),
		new KeyAction.KeyCommandPair(KeyStroke.getKeyStroke(KeyEvent.VK_HOME, 0),
									 Command.SCROLL_LEFT_MAX),
		new KeyAction.KeyCommandPair(KeyStroke.getKeyStroke(KeyEvent.VK_END, 0),
									 Command.SCROLL_RIGHT_MAX),
		new KeyAction.KeyCommandPair(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0),
									 Command.SCROLL_UP_UNIT),
		new KeyAction.KeyCommandPair(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0),
									 Command.SCROLL_DOWN_UNIT),
		new KeyAction.KeyCommandPair(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, 0),
									 Command.SCROLL_UP_BLOCK),
		new KeyAction.KeyCommandPair(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, 0),
									 Command.SCROLL_DOWN_BLOCK),
		new KeyAction.KeyCommandPair(KeyStroke.getKeyStroke(KeyEvent.VK_HOME, KeyEvent.CTRL_DOWN_MASK),
									 Command.SCROLL_UP_MAX),
		new KeyAction.KeyCommandPair(KeyStroke.getKeyStroke(KeyEvent.VK_END, KeyEvent.CTRL_DOWN_MASK),
									 Command.SCROLL_DOWN_MAX)
	};

////////////////////////////////////////////////////////////////////////
//  Member interfaces
////////////////////////////////////////////////////////////////////////


	// TEXT MODEL INTERFACE


	public interface IModel
	{

	////////////////////////////////////////////////////////////////////
	//  Methods
	////////////////////////////////////////////////////////////////////

		int getNumLines();

		//--------------------------------------------------------------

		Line[] getLines(int startIndex,
						int endIndex);

		//--------------------------------------------------------------

		String getText();

		//--------------------------------------------------------------

		void setText(String text);

		//--------------------------------------------------------------

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// TEXT LINE CLASS


	public static class Line
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		public Line()
		{
			text = "";
		}

		//--------------------------------------------------------------

		public Line(String text)
		{
			this.text = text;
		}

		//--------------------------------------------------------------

		public Line(String  text,
					int     highlightStartOffset,
					int     highlightEndOffset,
					boolean highlightEol)
		{
			this.text = text;
			this.highlightStartOffset = highlightStartOffset;
			this.highlightEndOffset = highlightEndOffset;
			this.highlightEol = highlightEol;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		public	String	text;
		public	int		highlightStartOffset;
		public	int		highlightEndOffset;
		public	boolean	highlightEol;

	}

	//==================================================================


	// DEFAULT TEXT MODEL CLASS


	public static class DefaultModel
		implements IModel
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		public DefaultModel()
		{
			textLines = new ArrayList<>();
			changeListeners = new ArrayList<>();
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : IModel interface
	////////////////////////////////////////////////////////////////////

		public int getNumLines()
		{
			return textLines.size();
		}

		//--------------------------------------------------------------

		public Line[] getLines(int startIndex,
							   int endIndex)
		{
			if ((startIndex < 0) || (startIndex > textLines.size()))
				startIndex = textLines.size();
			if ((endIndex < 0) || (endIndex > textLines.size()))
				endIndex = textLines.size();

			Line[] lines = new Line[endIndex - startIndex];
			int index = 0;
			for (int i = startIndex; i < endIndex; i++)
				lines[index++] = new Line(textLines.get(i));
			return lines;
		}

		//--------------------------------------------------------------

		public String getText()
		{
			StringBuilder buffer = new StringBuilder();
			for (int i = 0; i < textLines.size(); i++)
			{
				if (i > 0)
					buffer.append('\n');
				buffer.append(textLines.get(i));
			}
			return buffer.toString();
		}

		//--------------------------------------------------------------

		public void setText(String text)
		{
			textLines.clear();

			if (text != null)
			{
				int index = 0;
				int endIndex = text.length();
				while (index < endIndex)
				{
					int startIndex = index;
					index = text.indexOf('\n', startIndex);
					if (index < 0)
						index = endIndex;
					textLines.add(text.substring(startIndex, index));
					++index;
				}
			}

			fireStateChanged();
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		public List<String> getTextLines()
		{
			return textLines;
		}

		//--------------------------------------------------------------

		public String getTextLine(int index)
		{
			return textLines.get(index);
		}

		//--------------------------------------------------------------

		public void addChangeListener(ChangeListener listener)
		{
			changeListeners.add(listener);
		}

		//--------------------------------------------------------------

		protected void fireStateChanged()
		{
			for (int i = changeListeners.size() - 1; i >= 0; i--)
			{
				if (changeEvent == null)
					changeEvent = new ChangeEvent(this);
				changeListeners.get(i).stateChanged(changeEvent);
			}
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	List<String>			textLines;
		private	List<ChangeListener>	changeListeners;
		private	ChangeEvent				changeEvent;

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public TextArea(int  columns,
					int  rows,
					int  maxNumColumns,
					Font font)
	{
		// Initialise instance variables
		this.columns = columns;
		this.rows = rows;
		this.maxNumColumns = maxNumColumns;
		FontMetrics fontMetrics = getFontMetrics(font);
		columnWidth = FontUtils.getCharWidth('0', fontMetrics);
		rowHeight = fontMetrics.getHeight();
		unitIncrementColumns = DEFAULT_UNIT_INCREMENT_COLUMNS;
		unitIncrementRows = DEFAULT_UNIT_INCREMENT_ROWS;
		blockIncrementColumns = DEFAULT_BLOCK_INCREMENT_COLUMNS;
		blockIncrementRows = DEFAULT_BLOCK_INCREMENT_ROWS;
		highlightTextColour = DEFAULT_HIGHLIGHT_TEXT_COLOUR;
		highlightBackgroundColour = DEFAULT_HIGHLIGHT_BACKGROUND_COLOUR;
		antialiasing = TextRendering.getAntialiasing();
		fractionalMetrics = TextRendering.getFractionalMetrics();
		model = new DefaultModel();

		// Set properties
		setFont(font);
		setOpaque(true);
		setFocusable(true);

		// Add commands to action map
		KeyAction.create(this, JComponent.WHEN_FOCUSED, this, KEY_COMMANDS);

		// Add listeners
		addMouseListener(this);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : ActionListener interface
////////////////////////////////////////////////////////////////////////

	public void actionPerformed(ActionEvent event)
	{
		if (viewport != null)
		{
			String command = event.getActionCommand();

			if (command.equals(Command.SCROLL_LEFT_UNIT))
				onScrollLeftUnit();

			else if (command.equals(Command.SCROLL_RIGHT_UNIT))
				onScrollRightUnit();

			else if (command.equals(Command.SCROLL_LEFT_BLOCK))
				onScrollLeftBlock();

			else if (command.equals(Command.SCROLL_RIGHT_BLOCK))
				onScrollRightBlock();

			else if (command.equals(Command.SCROLL_LEFT_MAX))
				onScrollLeftMax();

			else if (command.equals(Command.SCROLL_RIGHT_MAX))
				onScrollRightMax();

			else if (command.equals(Command.SCROLL_UP_UNIT))
				onScrollUpUnit();

			else if (command.equals(Command.SCROLL_DOWN_UNIT))
				onScrollDownUnit();

			else if (command.equals(Command.SCROLL_UP_BLOCK))
				onScrollUpBlock();

			else if (command.equals(Command.SCROLL_DOWN_BLOCK))
				onScrollDownBlock();

			else if (command.equals(Command.SCROLL_UP_MAX))
				onScrollUpMax();

			else if (command.equals(Command.SCROLL_DOWN_MAX))
				onScrollDownMax();
		}
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : ChangeListener interface
////////////////////////////////////////////////////////////////////////

	public void stateChanged(ChangeEvent event)
	{
		if (event.getSource() == model)
		{
			resize();
			repaint();
		}
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : MouseListener interface
////////////////////////////////////////////////////////////////////////

	public void mouseClicked(MouseEvent event)
	{
		// do nothing
	}

	//------------------------------------------------------------------

	public void mouseEntered(MouseEvent event)
	{
		// do nothing
	}

	//------------------------------------------------------------------

	public void mouseExited(MouseEvent event)
	{
		// do nothing
	}

	//------------------------------------------------------------------

	public void mousePressed(MouseEvent event)
	{
		requestFocusInWindow();
	}

	//------------------------------------------------------------------

	public void mouseReleased(MouseEvent event)
	{
		// do nothing
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : Scrollable interface
////////////////////////////////////////////////////////////////////////

	public Dimension getPreferredScrollableViewportSize()
	{
		return new Dimension(columns * columnWidth, rows * rowHeight);
	}

	//------------------------------------------------------------------

	public boolean getScrollableTracksViewportWidth()
	{
		return false;
	}

	//------------------------------------------------------------------

	public boolean getScrollableTracksViewportHeight()
	{
		return false;
	}

	//------------------------------------------------------------------

	public int getScrollableUnitIncrement(Rectangle visibleRect,
										  int       orientation,
										  int       direction)
	{
		int delta = 0;
		if (orientation == SwingConstants.HORIZONTAL)
		{
			int x = visibleRect.x;
			if (direction < 0)
			{
				int column = (Math.max(0, x) + columnWidth - 1) / columnWidth;
				delta = x - (Math.max(0, column - unitIncrementColumns) * columnWidth);
			}
			else
			{
				int column = Math.max(0, x) / columnWidth;
				int maxColumn = Math.max(0, getWidth() - visibleRect.width) / columnWidth;
				delta = Math.min(column + unitIncrementColumns, maxColumn) * columnWidth - x;
			}
		}
		else if (orientation == SwingConstants.VERTICAL)
		{
			int y = visibleRect.y;
			if (direction < 0)
			{
				int row = (Math.max(0, y) + rowHeight - 1) / rowHeight;
				delta = y - (Math.max(0, row - unitIncrementRows) * rowHeight);
			}
			else
			{
				int row = Math.max(0, y) / rowHeight;
				int maxRow = Math.max(0, getHeight() - visibleRect.height) / rowHeight;
				delta = Math.min(row + unitIncrementRows, maxRow) * rowHeight - y;
			}
		}
		return delta;
	}

	//------------------------------------------------------------------

	public int getScrollableBlockIncrement(Rectangle visibleRect,
										   int       orientation,
										   int       direction)
	{
		int delta = 0;
		if (orientation == SwingConstants.HORIZONTAL)
		{
			int x = visibleRect.x;
			if (direction < 0)
			{
				int column = (Math.max(0, x) + columnWidth - 1) / columnWidth;
				delta = x - (Math.max(0, column - blockIncrementColumns) * columnWidth);
			}
			else
			{
				int column = Math.max(0, x) / columnWidth;
				int maxColumn = Math.max(0, getWidth() - visibleRect.width) / columnWidth;
				delta = Math.min(column + blockIncrementColumns, maxColumn) * columnWidth - x;
			}
		}
		else if (orientation == SwingConstants.VERTICAL)
		{
			int y = visibleRect.y;
			if (direction < 0)
			{
				int row = (Math.max(0, y) + rowHeight - 1) / rowHeight;
				delta = y - (Math.max(0, row - blockIncrementRows) * rowHeight);
			}
			else
			{
				int row = Math.max(0, y) / rowHeight;
				int maxRow = Math.max(0, getHeight() - visibleRect.height) / rowHeight;
				delta = Math.min(row + blockIncrementRows, maxRow) * rowHeight - y;
			}
		}
		return delta;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	@Override
	public Dimension getPreferredSize()
	{
		return new Dimension(Math.max(columns, maxNumColumns) * columnWidth,
							 Math.max(rows, (model == null) ? 0 : model.getNumLines()) * rowHeight);
	}

	//------------------------------------------------------------------

	@Override
	protected void paintComponent(Graphics gr)
	{
		// Create copy of graphics context
		gr = gr.create();

		// Fill background
		Rectangle rect = gr.getClipBounds();
		gr.setColor(getBackground());
		gr.fillRect(rect.x, rect.y, rect.width, rect.height);

		// Get start and end indices of text lines
		int startIndex = Math.max(0, rect.y / rowHeight);
		int endIndex = Math.max(0, rect.y + rect.height + rowHeight - 1) / rowHeight;

		// Get text lines from model
		if (model == null)
			return;
		Line[] lines = model.getLines(startIndex, endIndex);
		if (lines.length == 0)
			return;

		// Set rendering hints for text antialiasing and fractional metrics
		setRenderingHints((Graphics2D)gr);

		// Draw text lines
		Color textColour = getForeground();
		FontMetrics fontMetrics = gr.getFontMetrics();
		int ascent = fontMetrics.getAscent();
		int y = startIndex * rowHeight;
		for (Line line : lines)
		{
			int textWidth = fontMetrics.stringWidth(line.text);

			if (line.highlightStartOffset < line.highlightEndOffset)
			{
				int x = 0;
				if (line.highlightStartOffset > 0)
				{
					String str = line.text.substring(0, line.highlightStartOffset);
					gr.setColor(textColour);
					gr.drawString(str, x, y + ascent);
					x += fontMetrics.stringWidth(str);
				}

				String str = line.text.substring(line.highlightStartOffset, line.highlightEndOffset);
				int highlightWidth = fontMetrics.stringWidth(str);
				gr.setColor(highlightBackgroundColour);
				gr.fillRect(x, y, highlightWidth, rowHeight);
				gr.setColor(highlightTextColour);
				gr.drawString(str, x, y + ascent);
				x += highlightWidth;

				if (line.highlightEndOffset < line.text.length())
				{
					str = line.text.substring(line.highlightEndOffset);
					gr.setColor(textColour);
					gr.drawString(str, x, y + ascent);
				}
			}
			else
			{
				int x = 0;
				gr.setColor(textColour);
				gr.drawString(line.text, x, y + ascent);
			}

			if (line.highlightEol)
			{
				BufferedImage image = getEolImage(highlightBackgroundColour.getRGB());
				int x = textWidth;
				gr.drawImage(image, x, y, null);
			}

			y += rowHeight;
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

	public int getRows()
	{
		return rows;
	}

	//------------------------------------------------------------------

	public int getMaxNumColumns()
	{
		return maxNumColumns;
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

	public int getUnitIncrementColumns()
	{
		return unitIncrementColumns;
	}

	//------------------------------------------------------------------

	public int getUnitIncrementRows()
	{
		return unitIncrementRows;
	}

	//------------------------------------------------------------------

	public int getBlockIncrementColumns()
	{
		return blockIncrementColumns;
	}

	//------------------------------------------------------------------

	public int getBlockIncrementRows()
	{
		return blockIncrementRows;
	}

	//------------------------------------------------------------------

	public Color getHighlightTextColour()
	{
		return highlightTextColour;
	}

	//------------------------------------------------------------------

	public Color getHighlightBackgroundColour()
	{
		return highlightBackgroundColour;
	}

	//------------------------------------------------------------------

	public TextRendering.Antialiasing getAntialiasing()
	{
		return antialiasing;
	}

	//------------------------------------------------------------------

	public TextRendering.FractionalMetrics getFractionalMetrics()
	{
		return fractionalMetrics;
	}

	//------------------------------------------------------------------

	public IModel getModel()
	{
		return model;
	}

	//------------------------------------------------------------------

	public JViewport getViewport()
	{
		return viewport;
	}

	//------------------------------------------------------------------

	public String getText()
	{
		return ((model == null) ? null : model.getText());
	}

	//------------------------------------------------------------------

	public void setColumns(int columns)
	{
		if (this.columns != columns)
		{
			this.columns = columns;
			resize();
			repaint();
		}
	}

	//------------------------------------------------------------------

	public void setRows(int rows)
	{
		if (this.rows != rows)
		{
			this.rows = rows;
			resize();
			repaint();
		}
	}

	//------------------------------------------------------------------

	public void setMaxNumColumns(int numColumns)
	{
		if (maxNumColumns != numColumns)
		{
			maxNumColumns = numColumns;
			resize();
			repaint();
		}
	}

	//------------------------------------------------------------------

	public void setUnitIncrementColumns(int unitIncrementColumns)
	{
		this.unitIncrementColumns = unitIncrementColumns;
	}

	//------------------------------------------------------------------

	public void setUnitIncrementRows(int unitIncrementRows)
	{
		this.unitIncrementRows = unitIncrementRows;
	}

	//------------------------------------------------------------------

	public void setBlockIncrementColumns(int blockIncrementColumns)
	{
		this.blockIncrementColumns = blockIncrementColumns;
	}

	//------------------------------------------------------------------

	public void setBlockIncrementRows(int blockIncrementRows)
	{
		this.blockIncrementRows = blockIncrementRows;
	}

	//------------------------------------------------------------------

	public void setHighlightTextColour(Color colour)
	{
		highlightTextColour = colour;
		repaint();
	}

	//------------------------------------------------------------------

	public void setHighlightBackgroundColour(Color colour)
	{
		highlightBackgroundColour = colour;
		repaint();
	}

	//------------------------------------------------------------------

	public void setAntialiasing(TextRendering.Antialiasing antialiasing)
	{
		this.antialiasing = antialiasing;
		repaint();
	}

	//------------------------------------------------------------------

	public void setFractionalMetrics(TextRendering.FractionalMetrics fractionalMetrics)
	{
		this.fractionalMetrics = fractionalMetrics;
		repaint();
	}

	//------------------------------------------------------------------

	public void setModel(IModel model)
	{
		if (this.model != model)
		{
			Dimension oldSize = getPreferredSize();
			this.model = model;
			if (!getPreferredSize().equals(oldSize))
				resize();
			if (viewport != null)
				viewport.setViewPosition(new Point(0, 0));
			repaint();
		}
	}

	//------------------------------------------------------------------

	public void setText(String text)
	{
		if (model != null)
		{
			Dimension oldSize = getPreferredSize();
			model.setText(text);
			if (!getPreferredSize().equals(oldSize))
				resize();
			repaint();
		}
	}

	//------------------------------------------------------------------

	public void setViewport(JViewport viewport)
	{
		this.viewport = viewport;
	}

	//------------------------------------------------------------------

	public int getMaximumX()
	{
		return Math.max(0, getWidth() - viewport.getWidth());
	}

	//------------------------------------------------------------------

	public int getMaximumY()
	{
		return Math.max(0, getHeight() - viewport.getHeight());
	}

	//------------------------------------------------------------------

	public Point snapPoint(Point point)
	{
		return new Point(Math.max(0, point.x) / columnWidth * columnWidth,
						 Math.max(0, point.y) / rowHeight * rowHeight);
	}

	//------------------------------------------------------------------

	public void setViewPosition(int x,
								int y)
	{
		if (viewport != null)
		{
			Point viewPosition = new Point(x, y);
			if (!viewPosition.equals(viewport.getViewPosition()))
				viewport.setViewPosition(viewPosition);
		}
	}

	//------------------------------------------------------------------

	public void snapViewPosition()
	{
		if (viewport != null)
		{
			Point viewPosition = snapPoint(viewport.getViewPosition());
			if (!viewPosition.equals(viewport.getViewPosition()))
				viewport.setViewPosition(viewPosition);
		}
	}

	//------------------------------------------------------------------

	private BufferedImage getEolImage(int rgb)
	{
		if ((eolRgb != rgb) || (eolImage == null) || (eolImage.getHeight() != rowHeight))
		{
			// Generate pixel data
			int height3 = Math.max(1, rowHeight - 2 * (EOL_IMAGE_HEIGHT1 + EOL_IMAGE_HEIGHT2));
			if ((rowHeight - height3) % 2 != 0)
				++height3;
			int height2 = Math.min((rowHeight - height3) / 2, EOL_IMAGE_HEIGHT2);
			int height1 = Math.min((rowHeight - height3 - 2 * height2) / 2, EOL_IMAGE_HEIGHT1);
			int[] endYs = { 0, height1, height1 + height2, height1 + height2 + height3,
							height1 + 2 * height2 + height3, rowHeight };
			int[] rgbValues = new int[EOL_IMAGE_WIDTH * rowHeight];
			int bitmapIndex = 0;
			int offset = 0;
			int bitmap = 0;
			for (int y = 0; y < rowHeight; y++)
			{
				while (y >= endYs[bitmapIndex])
					bitmap = EOL_BITMAPS[bitmapIndex++];
				if (bitmap == 0)
					offset += EOL_IMAGE_WIDTH;
				else
				{
					int mask = 1 << (EOL_IMAGE_WIDTH - 1);
					while (mask != 0)
					{
						if ((bitmap & mask) != 0)
							rgbValues[offset] = rgb;
						++offset;
						mask >>= 1;
					}
				}
			}

			// Create buffered image from pixel data
			if ((eolImage == null) || (eolImage.getHeight() != rowHeight))
				eolImage = new BufferedImage(EOL_IMAGE_WIDTH, rowHeight, BufferedImage.TYPE_INT_ARGB);
			eolImage.setRGB(0, 0, EOL_IMAGE_WIDTH, rowHeight, rgbValues, 0, EOL_IMAGE_WIDTH);

			// Set RGB
			eolRgb = rgb;
		}

		return eolImage;
	}

	//------------------------------------------------------------------

	private void setRenderingHints(Graphics2D gr)
	{
		gr.setRenderingHint(TextRendering.Antialiasing.getHintKey(), antialiasing.getHintValue());
		gr.setRenderingHint(TextRendering.FractionalMetrics.getHintKey(),
							fractionalMetrics.getHintValue());
	}

	//------------------------------------------------------------------

	private void resize()
	{
		if (viewport != null)
			viewport.setViewSize(getPreferredSize());
		revalidate();
	}

	//------------------------------------------------------------------

	private void setViewX(int x)
	{
		viewport.setViewPosition(new Point(x, viewport.getViewPosition().y));
	}

	//------------------------------------------------------------------

	private void incrementViewX(int deltaX)
	{
		if (deltaX != 0)
			setViewX(viewport.getViewPosition().x + deltaX);
	}

	//------------------------------------------------------------------

	private void setViewY(int y)
	{
		viewport.setViewPosition(new Point(viewport.getViewPosition().x, y));
	}

	//------------------------------------------------------------------

	private void incrementViewY(int deltaY)
	{
		if (deltaY != 0)
			setViewY(viewport.getViewPosition().y + deltaY);
	}

	//------------------------------------------------------------------

	private void onScrollLeftUnit()
	{
		incrementViewX(-getScrollableUnitIncrement(viewport.getViewRect(), SwingConstants.HORIZONTAL,
												   -1));
	}

	//------------------------------------------------------------------

	private void onScrollRightUnit()
	{
		incrementViewX(getScrollableUnitIncrement(viewport.getViewRect(), SwingConstants.HORIZONTAL,
												  1));
	}

	//------------------------------------------------------------------

	private void onScrollLeftBlock()
	{
		incrementViewX(-getScrollableBlockIncrement(viewport.getViewRect(), SwingConstants.HORIZONTAL,
													-1));
	}

	//------------------------------------------------------------------

	private void onScrollRightBlock()
	{
		incrementViewX(getScrollableBlockIncrement(viewport.getViewRect(), SwingConstants.HORIZONTAL,
												   1));
	}

	//------------------------------------------------------------------

	private void onScrollLeftMax()
	{
		setViewX(0);
	}

	//------------------------------------------------------------------

	private void onScrollRightMax()
	{
		setViewX(getMaximumX());
	}

	//------------------------------------------------------------------

	private void onScrollUpUnit()
	{
		incrementViewY(-getScrollableUnitIncrement(viewport.getViewRect(), SwingConstants.VERTICAL,
												   -1));
	}

	//------------------------------------------------------------------

	private void onScrollDownUnit()
	{
		incrementViewY(getScrollableUnitIncrement(viewport.getViewRect(), SwingConstants.VERTICAL, 1));
	}

	//------------------------------------------------------------------

	private void onScrollUpBlock()
	{
		incrementViewY(-getScrollableBlockIncrement(viewport.getViewRect(), SwingConstants.VERTICAL,
													-1));
	}

	//------------------------------------------------------------------

	private void onScrollDownBlock()
	{
		incrementViewY(getScrollableBlockIncrement(viewport.getViewRect(), SwingConstants.VERTICAL,
												   1));
	}

	//------------------------------------------------------------------

	private void onScrollUpMax()
	{
		setViewY(0);
	}

	//------------------------------------------------------------------

	private void onScrollDownMax()
	{
		setViewY(getMaximumY());
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	int								columns;
	private	int								rows;
	private	int								maxNumColumns;
	private	int								columnWidth;
	private	int								rowHeight;
	private	int								unitIncrementColumns;
	private	int								unitIncrementRows;
	private	int								blockIncrementColumns;
	private	int								blockIncrementRows;
	private	Color							highlightTextColour;
	private	Color							highlightBackgroundColour;
	private	TextRendering.Antialiasing		antialiasing;
	private	TextRendering.FractionalMetrics	fractionalMetrics;
	private	IModel							model;
	private	JViewport						viewport;
	private	int								eolRgb;
	private	BufferedImage					eolImage;

}

//----------------------------------------------------------------------
