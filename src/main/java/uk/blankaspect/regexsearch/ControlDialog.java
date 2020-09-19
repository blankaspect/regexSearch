/*====================================================================*\

ControlDialog.java

Control dialog box class.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.regexsearch;

//----------------------------------------------------------------------


// IMPORTS


import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.File;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import uk.blankaspect.common.exception.AppException;
import uk.blankaspect.common.exception.FileException;

import uk.blankaspect.common.filesystem.PathnameUtils;

import uk.blankaspect.common.misc.PathnameFilter;
import uk.blankaspect.common.misc.TextFile;
import uk.blankaspect.common.misc.VHPos;

import uk.blankaspect.common.regex.RegexUtils;

import uk.blankaspect.common.string.StringUtils;

import uk.blankaspect.common.swing.action.KeyAction;

import uk.blankaspect.common.swing.button.ArrowButton;

import uk.blankaspect.common.swing.checkbox.FCheckBox;

import uk.blankaspect.common.swing.colour.Colours;
import uk.blankaspect.common.swing.colour.ColourUtils;

import uk.blankaspect.common.swing.combobox.FComboBox;

import uk.blankaspect.common.swing.font.FontUtils;

import uk.blankaspect.common.swing.label.FLabel;

import uk.blankaspect.common.swing.list.SingleSelectionListEditor;

import uk.blankaspect.common.swing.misc.GuiUtils;

import uk.blankaspect.common.swing.modifiers.InputModifiers;

import uk.blankaspect.common.swing.text.TextRendering;

import uk.blankaspect.common.swing.textfield.PathnameField;

//----------------------------------------------------------------------


// CONTROL DIALOG BOX CLASS


class ControlDialog
	extends JDialog
	implements ActionListener, MouseListener, PathnameField.IImportListener
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	public static final		char	COMMENT_PREFIX_CHAR	= ';';

	private static final	int	SCROLL_INTERVAL	= 400;

	private static final	int	ARROW_BUTTON_WIDTH	= 24;
	private static final	int	ARROW_BUTTON_HEIGHT	= 21;
	private static final	int	ARROW_SIZE			= 7;

	private static final	Insets	ICON_BUTTON_MARGINS		= new Insets(1, 1, 1, 1);

	private static final	String	TITLE_STR				= App.SHORT_NAME + " : Control";
	private static final	String	FILE_SET_KIND_STR		= "File-set kind";
	private static final	String	PATHNAME_STR			= "Pathname";
	private static final	String	INCLUDE_STR				= "Include";
	private static final	String	EXCLUDE_STR				= "Exclude";
	private static final	String	TARGET_STR				= "Target";
	private static final	String	REPLACEMENT_STR			= "Replacement";
	private static final	String	REPLACE_STR				= "Replace";
	private static final	String	REGULAR_EXPRESSION_STR	= "Regular expression";
	private static final	String	IGNORE_CASE_STR			= "Ignore case";
	private static final	String	SHOW_NOT_FOUND_STR		= "Show not found";
	private static final	String	DELETE_STR				= "Delete";
	private static final	String	DELETE_FILE_SET_STR		= "Delete file set";
	private static final	String	DELETE_MESSAGE_STR		= "Do you want to delete the current file set?";

	private static final	String	INSERT_FILE_SET_STR			= "Insert a new file set (F2)";
	private static final	String	DUPLICATE_FILE_SET_STR		= "Duplicate the current file set (F3)";
	private static final	String	DELETE_CURRENT_FILE_SET_STR	= "Delete the current file set (F4)";
	private static final	String	GO_TO_FILE_SET_START_STR	= "Go to the start of the file-set list (F5)";
	private static final	String	GO_TO_FILE_SET_END_STR		= "Go to the end of the file-set list (F8)";
	private static final	String	GO_TO_FILE_SET_PREVIOUS_STR	= "Go to the previous file set (F6), or move the file "
																	+ "set up (Ctrl+F6)";
	private static final	String	GO_TO_FILE_SET_NEXT_STR		= "Go to the next file set (F7), or move the file set "
																	+ "down (Ctrl+F7)";
	private static final	String	ESCAPE_TARGET_STR			= "Escape the target text";
	private static final	String	ESCAPE_REPLACEMENT_STR		= "Escape the replacement text";

	private enum ScrollDirection
	{
		BACKWARD,
		FORWARD
	}

	// Commands
	private interface Command
	{
		String	SELECT_FILE_SET_KIND	= "selectFileSetKind";
		String	INSERT_FILE_SET			= "insertFileSet";
		String	DUPLICATE_FILE_SET		= "duplicateFileSet";
		String	DELETE_FILE_SET			= "deleteFileSet";
		String	SCROLL_FILE_SET			= "scrollFileSet";
		String	GO_TO_FILE_SET_PREVIOUS	= "goToFileSetPrevious";
		String	GO_TO_FILE_SET_NEXT		= "goToFileSetNext";
		String	GO_TO_FILE_SET_START	= "goToFileSetStart";
		String	GO_TO_FILE_SET_END		= "goToFileSetEnd";
		String	MOVE_FILE_SET_UP		= "moveFileSetUp";
		String	MOVE_FILE_SET_DOWN		= "moveFileSetDown";
		String	ESCAPE_TARGET			= "escapeTarget";
		String	ESCAPE_REPLACEMENT		= "escapeReplacement";
		String	TOGGLE_REPLACE			= "toggleReplace";
		String	TOGGLE_REGEX			= "toggleRegex";
		String	SHOW_CONTEXT_MENU		= "showContextMenu";
	}

	private static final	KeyAction.KeyCommandPair[]	KEY_COMMANDS	=
	{
		new KeyAction.KeyCommandPair(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0),
									 Command.INSERT_FILE_SET),
		new KeyAction.KeyCommandPair(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0),
									 Command.DUPLICATE_FILE_SET),
		new KeyAction.KeyCommandPair(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0),
									 Command.DELETE_FILE_SET),
		new KeyAction.KeyCommandPair(KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0),
									 Command.GO_TO_FILE_SET_PREVIOUS),
		new KeyAction.KeyCommandPair(KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0),
									 Command.GO_TO_FILE_SET_NEXT),
		new KeyAction.KeyCommandPair(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0),
									 Command.GO_TO_FILE_SET_START),
		new KeyAction.KeyCommandPair(KeyStroke.getKeyStroke(KeyEvent.VK_F8, 0),
									 Command.GO_TO_FILE_SET_END),
		new KeyAction.KeyCommandPair(KeyStroke.getKeyStroke(KeyEvent.VK_F6, KeyEvent.CTRL_DOWN_MASK),
									 Command.MOVE_FILE_SET_UP),
		new KeyAction.KeyCommandPair(KeyStroke.getKeyStroke(KeyEvent.VK_F7, KeyEvent.CTRL_DOWN_MASK),
									 Command.MOVE_FILE_SET_DOWN),
		new KeyAction.KeyCommandPair(KeyStroke.getKeyStroke(KeyEvent.VK_CONTEXT_MENU, 0),
									 Command.SHOW_CONTEXT_MENU)
	};

////////////////////////////////////////////////////////////////////////
//  Enumerated types
////////////////////////////////////////////////////////////////////////


	// ERROR IDENTIFIERS


	private enum ErrorId
		implements AppException.IId
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		NO_PATHNAME
		("No pathname was specified."),

		FILE_DOES_NOT_EXIST
		("The file does not exist."),

		DIRECTORY_DOES_NOT_EXIST
		("The directory does not exist."),

		FILE_OR_DIRECTORY_DOES_NOT_EXIST
		("The file or directory specified by this pathname in the list file does not exist."),

		FILE_ACCESS_NOT_PERMITTED
		("Access to the file was not permitted."),

		DIRECTORY_ACCESS_NOT_PERMITTED
		("Access to the directory was not permitted."),

		FILE_OR_DIRECTORY_ACCESS_NOT_PERMITTED
		("Access to the file or directory specified in the list file was not permitted."),

		NO_RESULTS
		("No search results have been saved."),

		NO_TARGET
		("No target was specified."),

		MALFORMED_REGULAR_EXPRESSION
		("The target is not a well-formed regular expression.");

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private ErrorId(String message)
		{
			this.message = message;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : AppException.IId interface
	////////////////////////////////////////////////////////////////////

		public String getMessage()
		{
			return message;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	String	message;

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// FILE-SET INDEX FIELD CLASS


	private static class FileSetIndexField
		extends JComponent
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		private static final	int	VERTICAL_MARGIN		= 2;
		private static final	int	HORIZONTAL_MARGIN	= 5;

		private static final	Color	BORDER_COLOUR		= ArrowButton.BORDER_COLOUR;
		private static final	Color	BACKGROUND_COLOUR	= SingleSelectionListEditor.BACKGROUND_COLOUR;
		private static final	Color	TEXT_COLOUR			= Colours.FOREGROUND;

		private static final	String	PROTOTYPE_STR	= "000 / 000";
		private static final	String	END_STR			= "End";

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private FileSetIndexField()
		{
			AppFont.MAIN.apply(this);
			FontMetrics fontMetrics = getFontMetrics(getFont());
			preferredWidth = 2 * HORIZONTAL_MARGIN + fontMetrics.stringWidth(PROTOTYPE_STR);
			preferredHeight = 2 * VERTICAL_MARGIN + fontMetrics.getAscent() + fontMetrics.getDescent();
			index = -1;
			setOpaque(true);
			setFocusable(false);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		public Dimension getPreferredSize()
		{
			return new Dimension(preferredWidth, preferredHeight);
		}

		//--------------------------------------------------------------

		@Override
		protected void paintComponent(Graphics gr)
		{
			// Create copy of graphics context
			gr = gr.create();

			// Fill background
			Rectangle rect = gr.getClipBounds();
			gr.setColor(BACKGROUND_COLOUR);
			gr.fillRect(rect.x, rect.y, rect.width, rect.height);

			// Get dimensions
			int width = getWidth();
			int height = getHeight();

			// Set rendering hints for text antialiasing and fractional metrics
			TextRendering.setHints((Graphics2D)gr);

			// Draw text
			String str = getText();
			FontMetrics fontMetrics = gr.getFontMetrics();
			gr.setColor(TEXT_COLOUR);
			gr.drawString(str, (width - fontMetrics.stringWidth(str)) / 2,
						  FontUtils.getBaselineOffset(height, fontMetrics));

			// Draw border
			gr.setColor(BORDER_COLOUR);
			gr.drawRect(0, 0, width - 1, height - 1);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		public void setValues(int index,
							  int maxIndex)
		{
			if ((this.index != index) || (this.maxIndex != maxIndex))
			{
				this.index = index;
				this.maxIndex = maxIndex;
				repaint();
			}
		}

		//--------------------------------------------------------------

		private String getText()
		{
			return ((index > maxIndex) ? END_STR
									   : Integer.toString(index + 1) + " / " + Integer.toString(maxIndex + 1));
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	int	preferredWidth;
		private	int	preferredHeight;
		private	int	index;
		private	int	maxIndex;

	}

	//==================================================================


	// ESCAPE STATUS INDICATOR CLASS


	private static class EscapeIndicator
		extends JComponent
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		private static final	int	VERTICAL_MARGIN		= 1;
		private static final	int	HORIZONTAL_MARGIN	= 4;

		private static final	Color	BORDER_COLOUR		= SingleSelectionListEditor.BORDER_COLOUR;
		private static final	Color	BACKGROUND_COLOUR	= SingleSelectionListEditor.BACKGROUND_COLOUR;
		private static final	Color	TEXT_COLOUR			= Colours.FOREGROUND;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private EscapeIndicator(String text)
		{
			this.text = text;
			AppFont.MAIN.apply(this);
			FontMetrics fontMetrics = getFontMetrics(getFont());
			preferredWidth = Math.max(preferredWidth, 2 * HORIZONTAL_MARGIN + fontMetrics.stringWidth(text));
			preferredHeight = Math.max(preferredHeight,
									   2 * VERTICAL_MARGIN + fontMetrics.getAscent() + fontMetrics.getDescent());
			setOpaque(true);
			setFocusable(false);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		public Dimension getPreferredSize()
		{
			return new Dimension(preferredWidth, preferredHeight);
		}

		//--------------------------------------------------------------

		@Override
		protected void paintComponent(Graphics gr)
		{
			// Create copy of graphics context
			gr = gr.create();

			// Fill background
			Rectangle rect = gr.getClipBounds();
			gr.setColor(BACKGROUND_COLOUR);
			gr.fillRect(rect.x, rect.y, rect.width, rect.height);

			// Get dimensions
			int width = getWidth();
			int height = getHeight();

			// Set rendering hints for text antialiasing and fractional metrics
			TextRendering.setHints((Graphics2D)gr);

			// Draw text
			FontMetrics fontMetrics = gr.getFontMetrics();
			gr.setColor(TEXT_COLOUR);
			gr.drawString(text, (width - fontMetrics.stringWidth(text)) / 2,
						  FontUtils.getBaselineOffset(height, fontMetrics));

			// Draw border
			gr.setColor(BORDER_COLOUR);
			gr.drawRect(0, 0, width - 1, height - 1);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Class variables
	////////////////////////////////////////////////////////////////////

		private static	int	preferredWidth;
		private static	int	preferredHeight;

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	String	text;

	}

	//==================================================================


	// ESCAPE STATUS PANEL CLASS


	private static class EscapeStatusPanel
		extends JPanel
		implements ParameterEditor.EscapeListener
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private EscapeStatusPanel()
		{
			super(new GridLayout(1, 0, -1, 0));

			tabIndicator = new EscapeIndicator("t");
			add(tabIndicator);

			lineFeedIndicator = new EscapeIndicator("n");
			add(lineFeedIndicator);

			setTabsEscaped(false);
			setLineFeedsEscaped(false);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : ParameterEditor.EscapeListener interface
	////////////////////////////////////////////////////////////////////

		public void setTabsEscaped(boolean escaped)
		{
			tabIndicator.setVisible(escaped);
		}

		//--------------------------------------------------------------

		public void setLineFeedsEscaped(boolean escaped)
		{
			lineFeedIndicator.setVisible(escaped);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	EscapeIndicator	tabIndicator;
		private	EscapeIndicator	lineFeedIndicator;

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Member classes : inner classes
////////////////////////////////////////////////////////////////////////


	// FILE-SET BUTTON CLASS


	private class FileSetButton
		extends ArrowButton
		implements MouseListener, Runnable
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private FileSetButton(ScrollDirection scrollDirection)
		{
			this(scrollDirection, false);
		}

		//--------------------------------------------------------------

		private FileSetButton(ScrollDirection scrollDirection,
							  boolean         bar)

		{
			super(ARROW_BUTTON_WIDTH, ARROW_BUTTON_HEIGHT, ARROW_SIZE, bar);
			this.scrollDirection = scrollDirection;
			setDirection((scrollDirection == ScrollDirection.BACKWARD) ? Direction.LEFT : Direction.RIGHT);
			if (!bar)
				setActive(Active.PRESSED);
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
			if (!isBar() && isEnabled())
			{
				moveFileSet = InputModifiers.forEvent(event).isControl();
				SwingUtilities.invokeLater(this);
			}
		}

		//--------------------------------------------------------------

		public void mouseReleased(MouseEvent event)
		{
			if (!isBar() && (ControlDialog.this.scrollDirection == scrollDirection))
				stopScrolling();
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : Runnable interface
	////////////////////////////////////////////////////////////////////

		public void run()
		{
			ControlDialog.this.scrollDirection = scrollDirection;
			scrollTimer.start();
			onScrollFileSet();
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		protected void fireActionPerformed(ActionEvent event)
		{
			if (!isScrolling() || (event.getID() != ActionEvent.ACTION_PERFORMED))
				super.fireActionPerformed(event);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	ScrollDirection	scrollDirection;

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	private ControlDialog(MainWindow mainWindow)
	{
		// Call superclass constructor
		super(mainWindow, TITLE_STR);

		// Set icons
		setIconImages(mainWindow.getIconImages());

		// Initialise instance variables
		this.mainWindow = mainWindow;
		scrollTimer = new Timer(SCROLL_INTERVAL, this);
		scrollTimer.setActionCommand(Command.SCROLL_FILE_SET);


		//----  Main panel

		AppConfig config = AppConfig.INSTANCE;

		GridBagLayout gridBag = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();

		JPanel mainPanel = new JPanel(gridBag);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

		int gridY = 0;

		// Label: file-set kind
		JLabel fileSetKindLabel = new FLabel(FILE_SET_KIND_STR);

		gbc.gridx = 0;
		gbc.gridy = gridY;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = AppConstants.COMPONENT_INSETS;
		gridBag.setConstraints(fileSetKindLabel, gbc);
		mainPanel.add(fileSetKindLabel);

		// Panel: file-set control
		JPanel fileSetControlPanel = new JPanel(gridBag);

		gbc.gridx = 1;
		gbc.gridy = gridY++;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = AppConstants.COMPONENT_INSETS;
		gridBag.setConstraints(fileSetControlPanel, gbc);
		mainPanel.add(fileSetControlPanel);

		// Combo box: file-set kind
		fileSetKindComboBox = new FComboBox<>(FileSet.Kind.values());
		fileSetKindComboBox.setActionCommand(Command.SELECT_FILE_SET_KIND);
		fileSetKindComboBox.addActionListener(this);

		int gridX = 0;

		gbc.gridx = gridX++;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(0, 0, 0, 0);
		gridBag.setConstraints(fileSetKindComboBox, gbc);
		fileSetControlPanel.add(fileSetKindComboBox);

		// Filler: file-set control panel
		Box.Filler fileSetControlPanelFiller = GuiUtils.createFiller();

		gbc.gridx = gridX++;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(0, 12, 0, 0);
		gridBag.setConstraints(fileSetControlPanelFiller, gbc);
		fileSetControlPanel.add(fileSetControlPanelFiller);

		// Panel: file set
		JPanel fileSetPanel = new JPanel(gridBag);

		gbc.gridx = gridX++;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(0, 0, 0, 0);
		gridBag.setConstraints(fileSetPanel, gbc);
		fileSetControlPanel.add(fileSetPanel);

		// Panel: file set commands
		JPanel fileSetCommandPanel = new JPanel(gridBag);
		GuiUtils.setPaddedLineBorder(fileSetCommandPanel, 3, 5);
		Color backgroundColour = fileSetCommandPanel.getBackground();
		if (backgroundColour != null)
			fileSetCommandPanel.setBackground(ColourUtils.scaleBrightness(backgroundColour, 0.9375f));

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(0, 0, 0, 0);
		gridBag.setConstraints(fileSetCommandPanel, gbc);
		fileSetPanel.add(fileSetCommandPanel);

		gridX = 0;

		// Button: insert file set
		insertFileSetButton = new JButton(Icons.PLUS);
		insertFileSetButton.setMargin(ICON_BUTTON_MARGINS);
		insertFileSetButton.setToolTipText(INSERT_FILE_SET_STR);
		insertFileSetButton.setActionCommand(Command.INSERT_FILE_SET);
		insertFileSetButton.addActionListener(this);

		gbc.gridx = gridX++;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(0, 0, 0, 0);
		gridBag.setConstraints(insertFileSetButton, gbc);
		fileSetCommandPanel.add(insertFileSetButton);

		// Button: duplicate file set
		duplicateFileSetButton = new JButton(Icons.RHOMB_PAIR);
		duplicateFileSetButton.setMargin(ICON_BUTTON_MARGINS);
		duplicateFileSetButton.setToolTipText(DUPLICATE_FILE_SET_STR);
		duplicateFileSetButton.setActionCommand(Command.DUPLICATE_FILE_SET);
		duplicateFileSetButton.addActionListener(this);

		gbc.gridx = gridX++;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(0, 6, 0, 0);
		gridBag.setConstraints(duplicateFileSetButton, gbc);
		fileSetCommandPanel.add(duplicateFileSetButton);

		// Button: delete file set
		deleteFileSetButton = new JButton(Icons.MINUS);
		deleteFileSetButton.setMargin(ICON_BUTTON_MARGINS);
		deleteFileSetButton.setToolTipText(DELETE_CURRENT_FILE_SET_STR);
		deleteFileSetButton.setActionCommand(Command.DELETE_FILE_SET);
		deleteFileSetButton.addActionListener(this);

		gbc.gridx = gridX++;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(0, 6, 0, 0);
		gridBag.setConstraints(deleteFileSetButton, gbc);
		fileSetCommandPanel.add(deleteFileSetButton);

		// Panel: file set navigation
		JPanel fileSetNavigationPanel = new JPanel(gridBag);
		GuiUtils.setPaddedLineBorder(fileSetNavigationPanel, 2);
		fileSetNavigationPanel.setBackground(SingleSelectionListEditor.BACKGROUND_COLOUR);

		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(0, 12, 0, 0);
		gridBag.setConstraints(fileSetNavigationPanel, gbc);
		fileSetPanel.add(fileSetNavigationPanel);

		gridX = 0;

		// Button: start of file-set list
		fileSetStartButton = new FileSetButton(ScrollDirection.BACKWARD, true);
		fileSetStartButton.setToolTipText(GO_TO_FILE_SET_START_STR);
		fileSetStartButton.setActionCommand(Command.GO_TO_FILE_SET_START);
		fileSetStartButton.addActionListener(this);

		gbc.gridx = gridX++;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(0, 0, 0, 0);
		gridBag.setConstraints(fileSetStartButton, gbc);
		fileSetNavigationPanel.add(fileSetStartButton);

		// Button: previous file set
		fileSetPreviousButton = new FileSetButton(ScrollDirection.BACKWARD);
		fileSetPreviousButton.setToolTipText(GO_TO_FILE_SET_PREVIOUS_STR);
		fileSetPreviousButton.setActionCommand(Command.GO_TO_FILE_SET_PREVIOUS);
		fileSetPreviousButton.addActionListener(this);

		gbc.gridx = gridX++;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(0, 2, 0, 0);
		gridBag.setConstraints(fileSetPreviousButton, gbc);
		fileSetNavigationPanel.add(fileSetPreviousButton);

		// Field: file-set index
		fileSetIndexField = new FileSetIndexField();

		gbc.gridx = gridX++;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.VERTICAL;
		gbc.insets = new Insets(0, 2, 0, 0);
		gridBag.setConstraints(fileSetIndexField, gbc);
		fileSetNavigationPanel.add(fileSetIndexField);

		// Button: next file set
		fileSetNextButton = new FileSetButton(ScrollDirection.FORWARD);
		fileSetNextButton.setToolTipText(GO_TO_FILE_SET_NEXT_STR);
		fileSetNextButton.setActionCommand(Command.GO_TO_FILE_SET_NEXT);
		fileSetNextButton.addActionListener(this);

		gbc.gridx = gridX++;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(0, 2, 0, 0);
		gridBag.setConstraints(fileSetNextButton, gbc);
		fileSetNavigationPanel.add(fileSetNextButton);

		// Button: end of file-set list
		fileSetEndButton = new FileSetButton(ScrollDirection.FORWARD, true);
		fileSetEndButton.setToolTipText(GO_TO_FILE_SET_END_STR);
		fileSetEndButton.setActionCommand(Command.GO_TO_FILE_SET_END);
		fileSetEndButton.addActionListener(this);

		gbc.gridx = gridX++;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(0, 2, 0, 0);
		gridBag.setConstraints(fileSetEndButton, gbc);
		fileSetNavigationPanel.add(fileSetEndButton);

		// Label: pathname
		JLabel pathnameLabel = new FLabel(PATHNAME_STR);

		gbc.gridx = 0;
		gbc.gridy = gridY;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = AppConstants.COMPONENT_INSETS;
		gridBag.setConstraints(pathnameLabel, gbc);
		mainPanel.add(pathnameLabel);

		// Editor: pathname
		pathnameEditor = new PathnameEditor(FileSet.MAX_NUM_PATHNAMES);
		pathnameEditor.addImportListener(this);
		pathnameEditor.addUnixStyleObserver();

		gbc.gridx = 1;
		gbc.gridy = gridY++;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = AppConstants.COMPONENT_INSETS;
		gridBag.setConstraints(pathnameEditor, gbc);
		mainPanel.add(pathnameEditor);

		// Label: inclusion filter
		JLabel inclusionFilterLabel = new FLabel(INCLUDE_STR);

		gbc.gridx = 0;
		gbc.gridy = gridY;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = AppConstants.COMPONENT_INSETS;
		gridBag.setConstraints(inclusionFilterLabel, gbc);
		mainPanel.add(inclusionFilterLabel);

		// Editor: inclusion filter
		inclusionFilterEditor = new FilterEditor(FileSet.FilterKind.INCLUSION, FileSet.MAX_NUM_FILTERS, true);

		gbc.gridx = 1;
		gbc.gridy = gridY++;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = AppConstants.COMPONENT_INSETS;
		gridBag.setConstraints(inclusionFilterEditor, gbc);
		mainPanel.add(inclusionFilterEditor);

		// Label: exclusion filter
		JLabel exclusionFilterLabel = new FLabel(EXCLUDE_STR);

		gbc.gridx = 0;
		gbc.gridy = gridY;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = AppConstants.COMPONENT_INSETS;
		gridBag.setConstraints(exclusionFilterLabel, gbc);
		mainPanel.add(exclusionFilterLabel);

		// Editor: exclusion filter
		exclusionFilterEditor = new FilterEditor(FileSet.FilterKind.EXCLUSION, FileSet.MAX_NUM_FILTERS, false);

		gbc.gridx = 1;
		gbc.gridy = gridY++;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = AppConstants.COMPONENT_INSETS;
		gridBag.setConstraints(exclusionFilterEditor, gbc);
		mainPanel.add(exclusionFilterEditor);

		// Panel: target label
		JPanel targetLabelPanel = new JPanel(gridBag);

		gbc.gridx = 0;
		gbc.gridy = gridY;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.FIRST_LINE_END;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = AppConstants.COMPONENT_INSETS;
		gridBag.setConstraints(targetLabelPanel, gbc);
		mainPanel.add(targetLabelPanel);

		// Label: target
		JLabel targetLabel = new FLabel(TARGET_STR);

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 1.0;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(2, 0, 0, 0);
		gridBag.setConstraints(targetLabel, gbc);
		targetLabelPanel.add(targetLabel);

		// Panel: target escape status
		EscapeStatusPanel targetEscapeStatusPanel = new EscapeStatusPanel();

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(8, 2, 2, 2);
		gridBag.setConstraints(targetEscapeStatusPanel, gbc);
		targetLabelPanel.add(targetEscapeStatusPanel);

		// Editor: target
		targetEditor = new ParameterEditor(ParameterEditor.ParamKind.TARGET, SearchParameters.MAX_NUM_TARGETS, '\\',
										   targetEscapeStatusPanel);
		JButton escapeButton = targetEditor.getEscapeButton();
		escapeButton.setToolTipText(ESCAPE_TARGET_STR);
		escapeButton.setActionCommand(Command.ESCAPE_TARGET);
		escapeButton.addActionListener(this);

		gbc.gridx = 1;
		gbc.gridy = gridY++;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1.0;
		gbc.weighty = 0.5;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = AppConstants.COMPONENT_INSETS;
		gridBag.setConstraints(targetEditor, gbc);
		mainPanel.add(targetEditor);

		// Panel: replacement label
		JPanel replacementLabelPanel = new JPanel(gridBag);

		gbc.gridx = 0;
		gbc.gridy = gridY;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.FIRST_LINE_END;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = AppConstants.COMPONENT_INSETS;
		gridBag.setConstraints(replacementLabelPanel, gbc);
		mainPanel.add(replacementLabelPanel);

		// Label: replacement
		JLabel replacementLabel = new FLabel(REPLACEMENT_STR);

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 1.0;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(2, 0, 0, 0);
		gridBag.setConstraints(replacementLabel, gbc);
		replacementLabelPanel.add(replacementLabel);

		// Panel: replacement escape status
		EscapeStatusPanel replacementEscapeStatusPanel = new EscapeStatusPanel();

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(8, 2, 2, 2);
		gridBag.setConstraints(replacementEscapeStatusPanel, gbc);
		replacementLabelPanel.add(replacementEscapeStatusPanel);

		// Editor: replacement
		replacementEditor = new ParameterEditor(ParameterEditor.ParamKind.REPLACEMENT,
												SearchParameters.MAX_NUM_REPLACEMENTS,
												config.getReplacementEscapeChar(), replacementEscapeStatusPanel);

		escapeButton = replacementEditor.getEscapeButton();
		escapeButton.setToolTipText(ESCAPE_REPLACEMENT_STR);
		escapeButton.setActionCommand(Command.ESCAPE_REPLACEMENT);
		escapeButton.addActionListener(this);

		gbc.gridx = 1;
		gbc.gridy = gridY++;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1.0;
		gbc.weighty = 0.5;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = AppConstants.COMPONENT_INSETS;
		gridBag.setConstraints(replacementEditor, gbc);
		mainPanel.add(replacementEditor);

		// Panel: check boxes
		JPanel checkBoxPanel = new JPanel(gridBag);

		gbc.gridx = 1;
		gbc.gridy = gridY++;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(3, 3, 1, 3);
		gridBag.setConstraints(checkBoxPanel, gbc);
		mainPanel.add(checkBoxPanel);

		// Check box: replace
		gridX = 0;

		replaceCheckBox = new FCheckBox(REPLACE_STR);
		replaceCheckBox.setMnemonic(KeyEvent.VK_R);
		replaceCheckBox.setActionCommand(Command.TOGGLE_REPLACE);
		replaceCheckBox.addActionListener(this);

		gbc.gridx = gridX++;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(0, 0, 0, 0);
		gridBag.setConstraints(replaceCheckBox, gbc);
		checkBoxPanel.add(replaceCheckBox);

		// Check box: regular expression
		regexCheckBox = new FCheckBox(REGULAR_EXPRESSION_STR);
		regexCheckBox.setMnemonic(KeyEvent.VK_E);
		regexCheckBox.setActionCommand(Command.TOGGLE_REGEX);
		regexCheckBox.addActionListener(this);

		gbc.gridx = gridX++;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(0, 16, 0, 0);
		gridBag.setConstraints(regexCheckBox, gbc);
		checkBoxPanel.add(regexCheckBox);

		// Check box: ignore case
		ignoreCaseCheckBox = new FCheckBox(IGNORE_CASE_STR);
		ignoreCaseCheckBox.setMnemonic(KeyEvent.VK_I);

		gbc.gridx = gridX++;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(0, 16, 0, 0);
		gridBag.setConstraints(ignoreCaseCheckBox, gbc);
		checkBoxPanel.add(ignoreCaseCheckBox);

		// Check box: show not found
		showNotFoundCheckBox = new FCheckBox(SHOW_NOT_FOUND_STR);
		showNotFoundCheckBox.setMnemonic(KeyEvent.VK_N);

		gbc.gridx = gridX++;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(0, 16, 0, 0);
		gridBag.setConstraints(showNotFoundCheckBox, gbc);
		checkBoxPanel.add(showNotFoundCheckBox);

		// Set transfer handler
		mainPanel.setTransferHandler(FileTransferHandler.INSTANCE);

		// Add listener
		mainPanel.addMouseListener(this);

		// Add commands from main window menu to action map
		for (AppCommand command : AppCommand.values())
			KeyAction.create(mainPanel, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, command);

		// Add commands to action map
		KeyAction.create(mainPanel, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, this, KEY_COMMANDS);


		//----  Window

		// Set content pane
		setContentPane(mainPanel);

		// Update components
		updateComponents();

		// Dispose of window explicitly
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		// Handle window closing
		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent event)
			{
				AppCommand.EXIT.execute();
			}
		});

		// Resize dialog to its preferred size
		pack();

		// Set initial size of parameter editor
		initialParameterEditorSize = targetEditor.getTextAreaViewportSize();

		// Set location of window
		Point location = null;
		if (config.isMainWindowLocation())
			location = config.getControlDialogLocation();
		if (location == null)
			location = GuiUtils.getComponentLocation(this, mainWindow, VHPos.BOTTOM_CENTRE);
		setLocation(GuiUtils.getLocationWithinScreen(this, location));

		// Set focus
		targetEditor.requestFocusInWindow();

		// Make window visible
		setVisible(true);

	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static ControlDialog showDialog(MainWindow mainWindow)
	{
		return new ControlDialog(mainWindow);
	}

	//------------------------------------------------------------------

	private static SearchParameters getSearchParams()
	{
		return App.INSTANCE.getSearchParams();
	}

	//------------------------------------------------------------------

	private static List<File> readListFile(File listFile)
		throws AppException
	{
		// Parse file
		List<File> files = new ArrayList<>();
		for (String str : TextFile.readLines(listFile, TextModel.getCharEncoding()))
		{
			// Set index of end of pathname, ignoring any comment
			int index = str.indexOf(COMMENT_PREFIX_CHAR);
			if (index >= 0)
			{
				while (--index >= 0)
				{
					char ch = str.charAt(index);
					if ((ch != '\t') && (ch != ' '))
						break;
				}
				str = str.substring(0, ++index);
			}

			// Parse pathname and add file to list
			if (!str.isEmpty())
			{
				File file = new File(PathnameUtils.parsePathname(str));
				try
				{
					if (!file.isFile() && !file.isDirectory())
						throw new FileException(ErrorId.FILE_OR_DIRECTORY_DOES_NOT_EXIST, file);
					files.add(file);
				}
				catch (SecurityException e)
				{
					throw new FileException(ErrorId.FILE_OR_DIRECTORY_ACCESS_NOT_PERMITTED, file);
				}
			}
		}

		// Return files
		return files;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : ActionListener interface
////////////////////////////////////////////////////////////////////////

	@Override
	public void actionPerformed(ActionEvent event)
	{
		String command = event.getActionCommand();

		if (command.equals(Command.SELECT_FILE_SET_KIND))
			onSelectFileSetKind();

		else if (command.equals(Command.INSERT_FILE_SET))
			onInsertFileSet();

		else if (command.equals(Command.DUPLICATE_FILE_SET))
			onDuplicateFileSet();

		else if (command.equals(Command.DELETE_FILE_SET))
			onDeleteFileSet();

		else if (command.equals(Command.SCROLL_FILE_SET))
			onScrollFileSet();

		else if (command.equals(Command.GO_TO_FILE_SET_PREVIOUS))
			onGoToFileSetPrevious();

		else if (command.equals(Command.GO_TO_FILE_SET_NEXT))
			onGoToFileSetNext();

		else if (command.equals(Command.GO_TO_FILE_SET_START))
			onGoToFileSetStart();

		else if (command.equals(Command.GO_TO_FILE_SET_END))
			onGoToFileSetEnd();

		else if (command.equals(Command.MOVE_FILE_SET_UP))
			onMoveFileSetUp();

		else if (command.equals(Command.MOVE_FILE_SET_DOWN))
			onMoveFileSetDown();

		else if (command.equals(Command.ESCAPE_TARGET))
			onEscapeTarget();

		else if (command.equals(Command.ESCAPE_REPLACEMENT))
			onEscapeReplacement();

		else if (command.equals(Command.TOGGLE_REPLACE))
			onToggleReplace();

		else if (command.equals(Command.TOGGLE_REGEX))
			onToggleRegex();

		else if (command.equals(Command.SHOW_CONTEXT_MENU))
			onShowContextMenu();

		updateCommands();
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
//  Instance methods : PathnameField.IImportListener interface
////////////////////////////////////////////////////////////////////////

	@Override
	public void dataImported(PathnameField.ImportEvent event)
	{
		toFront();
		setPathname(pathnameEditor.getFile());
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public Dimension getInitialParameterEditorSize()
	{
		return initialParameterEditorSize;
	}

	//------------------------------------------------------------------

	public Dimension getParameterEditorSize()
	{
		return targetEditor.getTextAreaViewportSize();
	}

	//------------------------------------------------------------------

	public String getTargetString(boolean escape)
	{
		return targetEditor.getText(escape);
	}

	//------------------------------------------------------------------

	public boolean canAddFileSet()
	{
		return (getSearchParams().getNumFileSets() < SearchParameters.MAX_NUM_FILE_SETS);
	}

	//------------------------------------------------------------------

	public boolean isBeyondLastFileSet()
	{
		return (fileSetIndex > getMaxFileSetIndex());
	}

	//------------------------------------------------------------------

	public void updateComponents()
	{
		SearchParameters searchParams = getSearchParams();
		fileSetIndex = searchParams.getFileSetIndex();
		updateFileSetComponents();
		targetEditor.setItems(searchParams.getTargets());
		targetEditor.setIndex(searchParams.getTargetIndex());
		replacementEditor.setItems(searchParams.getReplacements());
		replacementEditor.setIndex(searchParams.getReplacementIndex());
		replaceCheckBox.setSelected(searchParams.isReplace());
		regexCheckBox.setSelected(searchParams.isRegex());
		ignoreCaseCheckBox.setSelected(searchParams.isIgnoreCase());
		showNotFoundCheckBox.setSelected(searchParams.isShowNotFound());
		enableComponents();
		updateCommands();
	}

	//------------------------------------------------------------------

	public void updateSearchParams()
	{
		updateFileSet();
		SearchParameters searchParams = getSearchParams();
		searchParams.setFileSetIndex(fileSetIndex);
		searchParams.setTargets(targetEditor.getItems());
		searchParams.setTargetIndex(targetEditor.getIndex());
		searchParams.setReplacements(replacementEditor.getItems());
		searchParams.setReplacementIndex(replacementEditor.getIndex());
		searchParams.setReplace(isReplace());
		searchParams.setRegex(isRegex());
		searchParams.setIgnoreCase(isIgnoreCase());
		searchParams.setShowNotFound(isShowNotFound());
	}

	//------------------------------------------------------------------

	public void updatePreferences()
	{
		AppConfig config = AppConfig.INSTANCE;
		char tabSurrogate = config.getTabSurrogate();
		int tabWidth = config.getTargetAndReplacementTabWidth();
		targetEditor.setTabSurrogate(tabSurrogate);
		targetEditor.setTabWidth(tabWidth);
		replacementEditor.setTabSurrogate(tabSurrogate);
		replacementEditor.setTabWidth(tabWidth);
		replacementEditor.setEscapeChar(config.getReplacementEscapeChar());
	}

	//------------------------------------------------------------------

	public void importFile()
	{
		if (isBeyondLastFileSet())
			onInsertFileSet();
		else
			onDuplicateFileSet();
		setPathname((File)AppCommand.IMPORT_FILE.getValue(AppCommand.Property.FILE));
	}

	//------------------------------------------------------------------

	public TextSearcher.Params getCurrentSearchParams()
		throws AppException
	{
		// Validate search parameters
		validateSearchParams();

		// Update parameter lists
		pathnameEditor.updateList();
		inclusionFilterEditor.updateList();
		exclusionFilterEditor.updateList();
		targetEditor.updateList();
		replacementEditor.updateList();

		// Set list of files
		TextSearcher.Params params = new TextSearcher.Params();
		switch (getFileSetKind())
		{
			case FILE:
			case DIRECTORY:
				params.files = Collections.singletonList(pathnameEditor.getFile());
				break;

			case LIST:
				params.files = readListFile(pathnameEditor.getFile());
				break;

			case RESULTS:
				params.files = mainWindow.getResultFiles();
				break;

			case CLIPBOARD:
				// do nothing
				break;
		}

		// Set filter patterns
		if (inclusionFilterEditor.isEnabled())
		{
			params.inclusionPatterns = inclusionFilterEditor.getPatterns();
			if (params.inclusionPatterns.isEmpty())
				params.inclusionPatterns.add(PathnameFilter.PATH_MULTIPLE_WILDCARD_STR);
		}

		if (exclusionFilterEditor.isEnabled())
			params.exclusionPatterns = exclusionFilterEditor.getPatterns();

		// Set target and replacement strings
		params.targetStr = getTargetString(isRegex());
		params.replacementStr = isReplace() ? getReplacementString() : null;

		// Set flags
		params.regex = isRegex();
		params.ignoreCase = isIgnoreCase();
		params.recordTargetNotFound = isShowNotFound();

		// Return parameters
		return params;
	}

	//------------------------------------------------------------------

	private void setPathname(File file)
	{
		pathnameEditor.setFile(file);
		switch (getFileSetKind())
		{
			case FILE:
			case LIST:
				if (file.isDirectory())
					fileSetKindComboBox.setSelectedValue(FileSet.Kind.DIRECTORY);
				break;

			case DIRECTORY:
				if (file.isFile())
					fileSetKindComboBox.setSelectedValue(FileSet.Kind.FILE);
				break;

			case RESULTS:
			case CLIPBOARD:
				// do nothing
				break;
		}
	}

	//------------------------------------------------------------------

	private boolean isScrolling()
	{
		return (scrollDirection != null);
	}

	//------------------------------------------------------------------

	private int getMaxFileSetIndex()
	{
		return (getSearchParams().getNumFileSets() - 1);
	}

	//------------------------------------------------------------------

	private FileSet getFileSet()
	{
		return getSearchParams().getFileSet(fileSetIndex);
	}

	//------------------------------------------------------------------

	private void updateFileSet()
	{
		if (!isBeyondLastFileSet())
		{
			FileSet fileSet = new FileSet(fileSetKindComboBox.getSelectedValue(),
										  pathnameEditor.getItems(),
										  pathnameEditor.getIndex(),
										  inclusionFilterEditor.getItems(),
										  inclusionFilterEditor.getIndex(),
										  exclusionFilterEditor.getItems(),
										  exclusionFilterEditor.getIndex());
			getSearchParams().setFileSet(fileSetIndex, fileSet);
		}
	}

	//------------------------------------------------------------------

	private void updateFileSetComponents()
	{
		FileSet fileSet = getFileSet();
		boolean isFileSet = (fileSet != null);

		fileSetIndexField.setValues(fileSetIndex, getMaxFileSetIndex());
		fileSetKindComboBox.setSelectedValue(isFileSet ? fileSet.getKind() : null);
		pathnameEditor.setItems(isFileSet ? fileSet.getPathnames() : null);
		pathnameEditor.setIndex(isFileSet ? fileSet.getPathnameIndex() : -1);
		inclusionFilterEditor.setItems(isFileSet ? fileSet.getInclusionFilters() : null);
		inclusionFilterEditor.setIndex(isFileSet ? fileSet.getInclusionFilterIndex() : -1);
		exclusionFilterEditor.setItems(isFileSet ? fileSet.getExclusionFilters() : null);
		exclusionFilterEditor.setIndex(isFileSet ? fileSet.getExclusionFilterIndex() : -1);
	}

	//------------------------------------------------------------------

	/**
	 * Enables or disables components according to the current state.  After the components have been
	 * updated, an attempt is made to move the focus to the next focusable component if the current focus
	 * owner is disabled.
	 */

	private void enableComponents()
	{
		// Enable/disable components according to current state
		FileSet.Kind fileSetKind = getFileSetKind();
		boolean isFileSet = !isBeyondLastFileSet();
		boolean canAddFileSet = canAddFileSet();
		boolean notScrolling = !isScrolling();

		fileSetKindComboBox.setEnabled(isFileSet);
		insertFileSetButton.setEnabled(canAddFileSet);
		duplicateFileSetButton.setEnabled(isFileSet && canAddFileSet);
		deleteFileSetButton.setEnabled(isFileSet);
		fileSetStartButton.setEnabled((fileSetIndex > 0) && notScrolling);
		fileSetPreviousButton.setEnabled(fileSetIndex > 0);
		fileSetNextButton.setEnabled(isFileSet);
		fileSetEndButton.setEnabled(isFileSet && notScrolling);
		pathnameEditor.setEnabled(isFileSet && fileSetKind.hasPathname());
		inclusionFilterEditor.setEnabled(isFileSet && fileSetKind.hasFilters());
		exclusionFilterEditor.setEnabled(isFileSet && fileSetKind.hasFilters());
		targetEditor.getEscapeButton().setEnabled(isRegex());
		replacementEditor.setEnabled(isReplace());
		replacementEditor.getEscapeButton().setEnabled(isReplace());

		// Move focus to next component if focus owner is disabled
		KeyboardFocusManager focusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		Component focusOwner = focusManager.getFocusOwner();
		if ((focusOwner != null) && !focusOwner.isEnabled())
			focusManager.focusNextComponent();
	}

	//------------------------------------------------------------------

	private void updateCommands()
	{
		mainWindow.updateCommands();
	}

	//------------------------------------------------------------------

	private void fileSetChanged()
	{
		updateFileSetComponents();
		enableComponents();
		updateCommands();
	}

	//------------------------------------------------------------------

	private FileSet.Kind getFileSetKind()
	{
		return fileSetKindComboBox.getSelectedValue();
	}

	//------------------------------------------------------------------

	private boolean isReplace()
	{
		return replaceCheckBox.isSelected();
	}

	//------------------------------------------------------------------

	private boolean isRegex()
	{
		return regexCheckBox.isSelected();
	}

	//------------------------------------------------------------------

	private boolean isIgnoreCase()
	{
		return ignoreCaseCheckBox.isSelected();
	}

	//------------------------------------------------------------------

	private boolean isShowNotFound()
	{
		return showNotFoundCheckBox.isSelected();
	}

	//------------------------------------------------------------------

	private String getReplacementString()
	{
		return replacementEditor.getText(true);
	}

	//------------------------------------------------------------------

	private void validateSearchParams()
		throws AppException
	{
		// Results ...
		FileSet.Kind fileSetKind = getFileSetKind();
		if (fileSetKind == FileSet.Kind.RESULTS)
		{
			if (mainWindow.getResultFiles().isEmpty())
				throw new AppException(ErrorId.NO_RESULTS);
		}

		// ... or pathname
		else
		{
			try
			{
				if (pathnameEditor.isEnabled() && pathnameEditor.isEmpty())
					throw new AppException(ErrorId.NO_PATHNAME);
				File file = pathnameEditor.getFile();
				switch (fileSetKind)
				{
					case FILE:
					case LIST:
						try
						{
							if (!file.isFile())
								throw new FileException(ErrorId.FILE_DOES_NOT_EXIST, file);
						}
						catch (SecurityException e)
						{
							throw new FileException(ErrorId.FILE_ACCESS_NOT_PERMITTED, file, e);
						}
						break;

					case DIRECTORY:
						try
						{
							if (!file.isDirectory())
								throw new FileException(ErrorId.DIRECTORY_DOES_NOT_EXIST, file);
						}
						catch (SecurityException e)
						{
							throw new FileException(ErrorId.DIRECTORY_ACCESS_NOT_PERMITTED, file, e);
						}
						break;

					case RESULTS:
					case CLIPBOARD:
						// do nothing
						break;
				}
			}
			catch (AppException e)
			{
				GuiUtils.setFocus(pathnameEditor);
				throw e;
			}
		}

		// Target string
		String targetStr = null;
		try
		{
			targetStr = getTargetString(isRegex());
			if (StringUtils.isNullOrEmpty(targetStr))
				throw new AppException(ErrorId.NO_TARGET);
		}
		catch (AppException e)
		{
			GuiUtils.setFocus(targetEditor);
			throw e;
		}
		if (isRegex())
		{
			try
			{
				int flags = Pattern.MULTILINE | Pattern.UNIX_LINES;
				if (isIgnoreCase())
					flags |= Pattern.CASE_INSENSITIVE;
				Pattern.compile(targetStr, flags);
			}
			catch (PatternSyntaxException e)
			{
				targetEditor.requestFocusInWindow();
				int index = e.getIndex();
				if (index >= 0)
					targetEditor.setCaretPosition(index);
				throw new TextSearcher.SyntaxException(ErrorId.MALFORMED_REGULAR_EXPRESSION,
													   RegexUtils.getExceptionMessage(e));
			}
		}

		// Replacement string
		if (isReplace())
		{
			try
			{
				TextSearcher.createReplacementString(getReplacementString(), null, isRegex());
			}
			catch (TextSearcher.SyntaxException e)
			{
				replacementEditor.requestFocusInWindow();
				int index = e.getIndex();
				if (index >= 0)
					replacementEditor.setCaretPosition(index);
				throw e;
			}
		}
	}

	//------------------------------------------------------------------

	private void showContextMenu(MouseEvent event)
	{
		mainWindow.showContextMenu(event, getContentPane());
	}

	//------------------------------------------------------------------

	private void stopScrolling()
	{
		scrollTimer.stop();
		scrollDirection = null;
		enableComponents();
	}

	//------------------------------------------------------------------

	private void onSelectFileSetKind()
	{
		enableComponents();
	}

	//------------------------------------------------------------------

	private void onInsertFileSet()
	{
		updateFileSet();
		getSearchParams().addFileSet(fileSetIndex, new FileSet());
		fileSetChanged();
	}

	//------------------------------------------------------------------

	private void onDuplicateFileSet()
	{
		updateFileSet();
		getSearchParams().addFileSet(fileSetIndex, getFileSet());
		++fileSetIndex;
		fileSetChanged();
	}

	//------------------------------------------------------------------

	private void onDeleteFileSet()
	{
		String[] optionStrs = Utils.getOptionStrings(DELETE_STR);
		if (JOptionPane.showOptionDialog(this, DELETE_MESSAGE_STR, DELETE_FILE_SET_STR, JOptionPane.OK_CANCEL_OPTION,
										 JOptionPane.QUESTION_MESSAGE, null, optionStrs, optionStrs[1])
																							== JOptionPane.OK_OPTION)
		{
			getSearchParams().removeFileSet(fileSetIndex);
			if (isBeyondLastFileSet() && (fileSetIndex > 0))
				--fileSetIndex;
			fileSetChanged();
		}
	}

	//------------------------------------------------------------------

	private void onScrollFileSet()
	{
		switch (scrollDirection)
		{
			case BACKWARD:
				if (moveFileSet)
					onMoveFileSetUp();
				else
					onGoToFileSetPrevious();
				break;

			case FORWARD:
				if (moveFileSet)
					onMoveFileSetDown();
				else
					onGoToFileSetNext();
				break;
		}
	}

	//------------------------------------------------------------------

	private void onGoToFileSetPrevious()
	{
		if (fileSetIndex > 0)
		{
			updateFileSet();
			--fileSetIndex;
			fileSetChanged();
			if (fileSetIndex <= 0)
				stopScrolling();
		}
	}

	//------------------------------------------------------------------

	private void onGoToFileSetNext()
	{
		int maxIndex = getMaxFileSetIndex();
		if (fileSetIndex <= maxIndex)
		{
			updateFileSet();
			++fileSetIndex;
			fileSetChanged();
			if (fileSetIndex > maxIndex)
				stopScrolling();
		}
	}

	//------------------------------------------------------------------

	private void onGoToFileSetStart()
	{
		if (fileSetIndex > 0)
		{
			updateFileSet();
			fileSetIndex = 0;
			fileSetChanged();
		}
	}

	//------------------------------------------------------------------

	private void onGoToFileSetEnd()
	{
		int maxIndex = getMaxFileSetIndex();
		if (fileSetIndex <= maxIndex)
		{
			updateFileSet();
			fileSetIndex = maxIndex + 1;
			fileSetChanged();
		}
	}

	//------------------------------------------------------------------

	private void onMoveFileSetUp()
	{
		if ((fileSetIndex > 0) && !isBeyondLastFileSet())
		{
			updateFileSet();
			SearchParameters searchParams = getSearchParams();
			FileSet fileSet = searchParams.removeFileSet(fileSetIndex);
			--fileSetIndex;
			searchParams.addFileSet(fileSetIndex, fileSet);
			fileSetChanged();
			if (fileSetIndex <= 0)
				stopScrolling();
		}
	}

	//------------------------------------------------------------------

	private void onMoveFileSetDown()
	{
		int maxIndex = getMaxFileSetIndex();
		if (fileSetIndex <= maxIndex)
		{
			updateFileSet();
			if (fileSetIndex < maxIndex)
			{
				SearchParameters searchParams = getSearchParams();
				FileSet fileSet = searchParams.removeFileSet(fileSetIndex);
				++fileSetIndex;
				searchParams.addFileSet(fileSetIndex, fileSet);
			}
			fileSetChanged();
			if (fileSetIndex > maxIndex)
				stopScrolling();
		}
	}

	//------------------------------------------------------------------

	private void onEscapeTarget()
	{
		if (isRegex())
			targetEditor.setText(StringUtils.escape(targetEditor.getText(),
													AppConfig.INSTANCE.getEscapedMetacharacters()));
	}

	//------------------------------------------------------------------

	private void onEscapeReplacement()
	{
		String str = replacementEditor.getText();
		String escapePrefix = Character.toString(AppConfig.INSTANCE.getReplacementEscapeChar());
		replacementEditor.setText(str.replace(escapePrefix, escapePrefix + escapePrefix));
	}

	//------------------------------------------------------------------

	private void onToggleReplace()
	{
		enableComponents();
	}

	//------------------------------------------------------------------

	private void onToggleRegex()
	{
		enableComponents();
	}

	//------------------------------------------------------------------

	private void onShowContextMenu()
	{
		showContextMenu(null);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	Dimension				initialParameterEditorSize;
	private	ScrollDirection			scrollDirection;
	private	boolean					moveFileSet;
	private	int						fileSetIndex;
	private	Timer					scrollTimer;
	private	MainWindow				mainWindow;
	private	FComboBox<FileSet.Kind>	fileSetKindComboBox;
	private	JButton					insertFileSetButton;
	private	JButton					duplicateFileSetButton;
	private	JButton					deleteFileSetButton;
	private	FileSetButton			fileSetStartButton;
	private	FileSetButton			fileSetEndButton;
	private	FileSetButton			fileSetPreviousButton;
	private	FileSetButton			fileSetNextButton;
	private	FileSetIndexField		fileSetIndexField;
	private	PathnameEditor			pathnameEditor;
	private	FilterEditor			inclusionFilterEditor;
	private	FilterEditor			exclusionFilterEditor;
	private	ParameterEditor			targetEditor;
	private	ParameterEditor			replacementEditor;
	private	JCheckBox				replaceCheckBox;
	private	JCheckBox				regexCheckBox;
	private	JCheckBox				ignoreCaseCheckBox;
	private	JCheckBox				showNotFoundCheckBox;

}

//----------------------------------------------------------------------
