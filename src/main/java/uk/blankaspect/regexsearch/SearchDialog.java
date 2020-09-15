/*====================================================================*\

SearchDialog.java

Search dialog box class.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.regexsearch;

//----------------------------------------------------------------------


// IMPORTS


import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Window;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

import uk.blankaspect.common.swing.action.KeyAction;

import uk.blankaspect.common.swing.button.FButton;

import uk.blankaspect.common.swing.font.FontUtils;

import uk.blankaspect.common.swing.label.FLabel;

import uk.blankaspect.common.swing.misc.GuiUtils;

//----------------------------------------------------------------------


// SEARCH DIALOG BOX CLASS


class SearchDialog
	extends JDialog
	implements ActionListener
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	private static final	int	TARGET_FIELD_NUM_COLUMNS	= 80;
	private static final	int	FILE_FIELD_NUM_COLUMNS		= 80;
	private static final	int	LINE_FIELD_NUM_COLUMNS		= 10;
	private static final	int	OFFSET_FIELD_NUM_COLUMNS	= 10;

	private static final	Insets	BUTTON_MARGINS	= new Insets(2, 8, 2, 8);

	private static final	String	TARGET_STR	= "Target";
	private static final	String	FILE_STR	= "File";
	private static final	String	LINE_STR	= "Line";
	private static final	String	OFFSET_STR	= "Offset";
	private static final	String	TO_STR		= "to";

	private static final	OptionEx[]	FIND_OPTIONS	=
	{
		new OptionEx(Option.YES,
					 "Search for the next occurrence"),
		new OptionEx(Option.GLOBAL,
					 "Search for all remaining occurrences in the current file and all subsequent files " +
						"without further prompting"),
		new OptionEx(Option.NEXT_FILE,
					 "Search the next file"),
		new OptionEx(Option.CANCEL,
					 "Cancel the search")
	};

	private static final	OptionEx[]	REPLACE_OPTIONS	=
	{
		new OptionEx(Option.YES,
					 "Replace this occurrence and resume the search"),
		new OptionEx(Option.NO,
					 "Don't replace this occurrence; resume the search"),
		new OptionEx(Option.PREVIEW,
					 "Replace this occurrence and display the replacement"),
		new OptionEx(Option.THIS_FILE,
					 "Replace all remaining occurrences in the current file, then search the next file"),
		new OptionEx(Option.GLOBAL,
					 "Replace all remaining occurrences in the current file and all subsequent files " +
						"without further prompting"),
		new OptionEx(Option.NEXT_FILE,
					 "Don't replace this occurrence; save any changes to the current file, then search " +
						"the next file"),
		new OptionEx(Option.CANCEL,
					 "Cancel the search, discarding any changes to the current file")
	};

	private static final	OptionEx[]	PREVIEW_OPTIONS	=
	{
		new OptionEx(Option.KEEP,
					 "Keep the highlighted replacement"),
		new OptionEx(Option.RESTORE,
					 "Restore the original text"),
		new OptionEx(Option.CANCEL,
					 "Cancel the search, discarding any changes to the current file")
	};

	// Commands
	interface Command
	{
		String	CLOSE	= "close";
	}

////////////////////////////////////////////////////////////////////////
//  Enumerated types
////////////////////////////////////////////////////////////////////////


	// DIALOG KIND


	enum Kind
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		FIND
		(
			"Search options | Find",
			"Do you want to search for the next occurrence?",
			FIND_OPTIONS,
			0
		),

		REPLACE
		(
			"Search options | Find-and-replace",
			"Do you want to replace this occurrence?",
			REPLACE_OPTIONS,
			1
		),

		PREVIEW
		(
			"Text replacement preview",
			"Do you want to keep the replacement or to restore the original text?",
			PREVIEW_OPTIONS,
			1
		);

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private Kind(String     titleStr,
					 String     questionStr,
					 OptionEx[] options,
					 int        defaultFocusIndex)
		{
			this.titleStr = titleStr;
			this.questionStr = questionStr;
			this.options = options;
			this.defaultFocusIndex = defaultFocusIndex;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		public String getTitleString()
		{
			return titleStr;
		}

		//--------------------------------------------------------------

		public String getQuestionString()
		{
			return questionStr;
		}

		//--------------------------------------------------------------

		public int getNumOptions()
		{
			return options.length;
		}

		//--------------------------------------------------------------

		public OptionEx getOption(int index)
		{
			return options[index];
		}

		//--------------------------------------------------------------

		public int getDefaultFocusIndex()
		{
			return defaultFocusIndex;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	String		titleStr;
		private	String		questionStr;
		private	OptionEx[]	options;
		private	int			defaultFocusIndex;

	}

	//==================================================================


	// OPTION


	private enum Option
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		YES
		(
			TextSearcher.Option.REPLACE,
			"Yes",
			KeyEvent.VK_Y
		),

		NO
		(
			TextSearcher.Option.SKIP,
			"No",
			KeyEvent.VK_N
		),

		PREVIEW
		(
			TextSearcher.Option.PREVIEW,
			"Preview",
			KeyEvent.VK_P
		),

		THIS_FILE
		(
			TextSearcher.Option.REPLACE_FILE,
			"This file",
			KeyEvent.VK_T
		),

		GLOBAL
		(
			TextSearcher.Option.REPLACE_GLOBAL,
			"Global",
			KeyEvent.VK_G
		),

		NEXT_FILE
		(
			TextSearcher.Option.NEXT_FILE,
			"Next file",
			KeyEvent.VK_X
		),

		KEEP
		(
			TextSearcher.Option.KEEP,
			"Keep",
			KeyEvent.VK_K
		),

		RESTORE
		(
			TextSearcher.Option.RESTORE,
			"Restore",
			KeyEvent.VK_R
		),

		CANCEL
		(
			TextSearcher.Option.CANCEL,
			"Cancel",
			KeyEvent.VK_ESCAPE
		);

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private Option(TextSearcher.Option searchOption,
					   String              text,
					   int                 key)
		{
			this.searchOption = searchOption;
			this.text = text;
			this.key = key;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		public TextSearcher.Option getSearchOption()
		{
			return searchOption;
		}

		//--------------------------------------------------------------

		public String getText()
		{
			return text;
		}

		//--------------------------------------------------------------

		public int getKey()
		{
			return key;
		}

		//--------------------------------------------------------------

		public KeyStroke getKeyStroke()
		{
			return KeyStroke.getKeyStroke(key, 0);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	TextSearcher.Option	searchOption;
		private	String				text;
		private	int					key;

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// EXTENDED OPTION CLASS


	private static class OptionEx
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private OptionEx(Option option,
						 String tooltipStr)
		{
			this.option = option;
			this.tooltipStr = tooltipStr;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		Option	option;
		String	tooltipStr;

	}

	//==================================================================


	// INFORMATION FIELD CLASS


	private static class InfoField
		extends JTextField
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private InfoField(String text,
						  int    numColumns)
		{
			super(text, numColumns);
			AppFont.MAIN.apply(this);
			GuiUtils.setPaddedLineBorder(this, 2, 4);
			AppConfig config = AppConfig.INSTANCE;
			setForeground(config.getTextAreaTextColour());
			setBackground(config.getTextAreaBackgroundColour());
			setSelectedTextColor(config.getTextAreaHighlightTextColour());
			setSelectionColor(config.getTextAreaHighlightBackgroundColour());
			setEditable(false);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		public Dimension getPreferredSize()
		{
			Dimension size = super.getPreferredSize();
			return new Dimension(size.width + 1, size.height);
		}

		//--------------------------------------------------------------

		@Override
		protected int getColumnWidth()
		{
			return FontUtils.getCharWidth('0', getFontMetrics(getFont()));
		}

		//--------------------------------------------------------------

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	private SearchDialog(Window owner,
						 Kind   dialogKind,
						 String targetStr,
						 String pathname,
						 int    lineIndex,
						 int    startOffset,
						 int    endOffset)
	{

		// Call superclass constructor
		super(owner, dialogKind.getTitleString());

		// Set icons
		setIconImages(owner.getIconImages());

		// Initialise instance variables
		this.dialogKind = dialogKind;


		//----  Top panel

		GridBagLayout gridBag = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();

		JPanel topPanel = new JPanel(gridBag);
		topPanel.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));

		int gridY = 0;

		if (dialogKind != Kind.PREVIEW)
		{
			// Label: target
			JLabel targetLabel = new FLabel(TARGET_STR);

			gbc.gridx = 0;
			gbc.gridy = gridY;
			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			gbc.weightx = 0.0;
			gbc.weighty = 0.0;
			gbc.anchor = GridBagConstraints.LINE_END;
			gbc.fill = GridBagConstraints.NONE;
			gbc.insets = AppConstants.COMPONENT_INSETS;
			gridBag.setConstraints(targetLabel, gbc);
			topPanel.add(targetLabel);

			// Field: target
			InfoField targetField = new InfoField(targetStr, TARGET_FIELD_NUM_COLUMNS);

			gbc.gridx = 1;
			gbc.gridy = gridY++;
			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			gbc.weightx = 0.0;
			gbc.weighty = 0.0;
			gbc.anchor = GridBagConstraints.LINE_START;
			gbc.fill = GridBagConstraints.NONE;
			gbc.insets = AppConstants.COMPONENT_INSETS;
			gridBag.setConstraints(targetField, gbc);
			topPanel.add(targetField);

			// Label: file
			JLabel fileLabel = new FLabel(FILE_STR);

			gbc.gridx = 0;
			gbc.gridy = gridY;
			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			gbc.weightx = 0.0;
			gbc.weighty = 0.0;
			gbc.anchor = GridBagConstraints.LINE_END;
			gbc.fill = GridBagConstraints.NONE;
			gbc.insets = AppConstants.COMPONENT_INSETS;
			gridBag.setConstraints(fileLabel, gbc);
			topPanel.add(fileLabel);

			// Field: file
			InfoField fileField = new InfoField(pathname, FILE_FIELD_NUM_COLUMNS);

			gbc.gridx = 1;
			gbc.gridy = gridY++;
			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			gbc.weightx = 0.0;
			gbc.weighty = 0.0;
			gbc.anchor = GridBagConstraints.LINE_START;
			gbc.fill = GridBagConstraints.NONE;
			gbc.insets = AppConstants.COMPONENT_INSETS;
			gridBag.setConstraints(fileField, gbc);
			topPanel.add(fileField);

			// Label: line
			JLabel lineLabel = new FLabel(LINE_STR);

			gbc.gridx = 0;
			gbc.gridy = gridY;
			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			gbc.weightx = 0.0;
			gbc.weighty = 0.0;
			gbc.anchor = GridBagConstraints.LINE_END;
			gbc.fill = GridBagConstraints.NONE;
			gbc.insets = AppConstants.COMPONENT_INSETS;
			gridBag.setConstraints(lineLabel, gbc);
			topPanel.add(lineLabel);

			// Field: line
			InfoField lineField = new InfoField(Integer.toString(lineIndex + 1),
												LINE_FIELD_NUM_COLUMNS);

			gbc.gridx = 1;
			gbc.gridy = gridY++;
			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			gbc.weightx = 0.0;
			gbc.weighty = 0.0;
			gbc.anchor = GridBagConstraints.LINE_START;
			gbc.fill = GridBagConstraints.NONE;
			gbc.insets = AppConstants.COMPONENT_INSETS;
			gridBag.setConstraints(lineField, gbc);
			topPanel.add(lineField);

			// Label: offset
			JLabel offsetLabel = new FLabel(OFFSET_STR);

			gbc.gridx = 0;
			gbc.gridy = gridY;
			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			gbc.weightx = 0.0;
			gbc.weighty = 0.0;
			gbc.anchor = GridBagConstraints.LINE_END;
			gbc.fill = GridBagConstraints.NONE;
			gbc.insets = AppConstants.COMPONENT_INSETS;
			gridBag.setConstraints(offsetLabel, gbc);
			topPanel.add(offsetLabel);

			// Field: start offset
			JPanel offsetPanel = new JPanel(gridBag);

			InfoField startOffsetField = new InfoField(Integer.toString(startOffset),
													   OFFSET_FIELD_NUM_COLUMNS);

			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			gbc.weightx = 0.0;
			gbc.weighty = 0.0;
			gbc.anchor = GridBagConstraints.LINE_START;
			gbc.fill = GridBagConstraints.NONE;
			gbc.insets = new Insets(0, 0, 0, 0);
			gridBag.setConstraints(startOffsetField, gbc);
			offsetPanel.add(startOffsetField);

			// Label: to
			JLabel toLabel = new FLabel(TO_STR);

			gbc.gridx = 1;
			gbc.gridy = 0;
			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			gbc.weightx = 0.0;
			gbc.weighty = 0.0;
			gbc.anchor = GridBagConstraints.LINE_START;
			gbc.fill = GridBagConstraints.NONE;
			gbc.insets = new Insets(0, 4, 0, 0);
			gridBag.setConstraints(toLabel, gbc);
			offsetPanel.add(toLabel);

			// Field: end offset
			InfoField endOffsetField = new InfoField(Integer.toString(endOffset),
													 OFFSET_FIELD_NUM_COLUMNS);

			gbc.gridx = 2;
			gbc.gridy = 0;
			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			gbc.weightx = 0.0;
			gbc.weighty = 0.0;
			gbc.anchor = GridBagConstraints.LINE_START;
			gbc.fill = GridBagConstraints.NONE;
			gbc.insets = new Insets(0, 4, 0, 0);
			gridBag.setConstraints(endOffsetField, gbc);
			offsetPanel.add(endOffsetField);

			gbc.gridx = 1;
			gbc.gridy = gridY++;
			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			gbc.weightx = 0.0;
			gbc.weighty = 0.0;
			gbc.anchor = GridBagConstraints.LINE_START;
			gbc.fill = GridBagConstraints.NONE;
			gbc.insets = AppConstants.COMPONENT_INSETS;
			gridBag.setConstraints(offsetPanel, gbc);
			topPanel.add(offsetPanel);
		}

		// Icon: question
		JLabel questionIcon = new JLabel(UIManager.getIcon("OptionPane.questionIcon"));

		gbc.gridx = 0;
		gbc.gridy = gridY;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(6, 4, 4, 4);
		gridBag.setConstraints(questionIcon, gbc);
		topPanel.add(questionIcon);

		// Label: question
		JLabel questionLabel = new FLabel(dialogKind.getQuestionString());

		gbc.gridx = 1;
		gbc.gridy = gridY++;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(12, 3, 12, 4);
		gridBag.setConstraints(questionLabel, gbc);
		topPanel.add(questionLabel);


		//----  Button panel

		JPanel buttonPanel = new JPanel(new GridLayout(1, 0, 8, 0));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));

		// Initialise buttons and key command map elements
		List<JButton> buttons = new ArrayList<>();
		List<KeyAction.KeyCommandPair> keyCommands = new ArrayList<>();
		for (int i = 0; i < dialogKind.getNumOptions(); i++)
		{
			Option option = dialogKind.getOption(i).option;
			JButton button = new FButton(option.getText());
			if (option != Option.CANCEL)
				button.setMnemonic(option.getKey());
			button.setToolTipText(dialogKind.getOption(i).tooltipStr);
			String command = Command.CLOSE + i;
			button.setActionCommand(command);
			button.setMargin(BUTTON_MARGINS);
			button.addActionListener(this);
			buttonPanel.add(button);

			buttons.add(button);
			keyCommands.add(new KeyAction.KeyCommandPair(option.getKeyStroke(), command));
		}


		//----  Main panel

		JPanel mainPanel = new JPanel(gridBag);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

		gridY = 0;

		gbc.gridx = 0;
		gbc.gridy = gridY++;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(0, 0, 0, 0);
		gridBag.setConstraints(topPanel, gbc);
		mainPanel.add(topPanel);

		gbc.gridx = 0;
		gbc.gridy = gridY++;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(-1, 0, 0, 0);
		gridBag.setConstraints(buttonPanel, gbc);
		mainPanel.add(buttonPanel);

		// Add commands to action map
		KeyAction.create(mainPanel, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, this, keyCommands);


		//----  Window

		// Set content pane
		setContentPane(mainPanel);

		// Dispose of window explicitly
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		// Handle window closing
		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent event)
			{
				close(TextSearcher.Option.CANCEL);
			}
		});

		// Prevent dialog from being resized
		setResizable(false);

		// Resize dialog to its preferred size
		pack();

		// Set location of dialog box
		if (locations == null)
			locations = new Point[Kind.values().length];
		Point location = locations[dialogKind.ordinal()];
		if (location == null)
			location = GuiUtils.getComponentLocation(this, owner);
		setLocation(GuiUtils.getLocationWithinScreen(this, location));

		// Set default button
		getRootPane().setDefaultButton(buttons.get(0));

		// Set focus
		if (focusIndices == null)
		{
			focusIndices = new int[Kind.values().length];
			for (int i = 0; i < focusIndices.length; i++)
				focusIndices[i] = -1;
		}
		int index = (focusIndices[dialogKind.ordinal()] < 0) ? dialogKind.getDefaultFocusIndex()
															 : focusIndices[dialogKind.ordinal()];
		buttons.get(index).requestFocusInWindow();

		// Show dialog
		setVisible(true);

	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static SearchDialog showDialog(Component parent,
										  Kind      dialogKind,
										  String    targetStr,
										  String    pathname,
										  int       lineIndex,
										  int       startOffset,
										  int       endOffset)
	{
		return new SearchDialog(GuiUtils.getWindow(parent), dialogKind, targetStr, pathname,
								lineIndex, startOffset, endOffset);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : ActionListener interface
////////////////////////////////////////////////////////////////////////

	public void actionPerformed(ActionEvent event)
	{
		int index = Integer.parseInt(event.getActionCommand().substring(Command.CLOSE.length()));
		Option option = dialogKind.getOption(index).option;
		if (option != Option.CANCEL)
			focusIndices[dialogKind.ordinal()] = index;
		close(option.getSearchOption());
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	private void close(TextSearcher.Option option)
	{
		locations[dialogKind.ordinal()] = getLocation();
		setVisible(false);
		dispose();
		App.INSTANCE.getMainWindow().searchDialogClosed(option);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class variables
////////////////////////////////////////////////////////////////////////

	private static	Point[]	locations;
	private static	int[]	focusIndices;

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	Kind	dialogKind;

}

//----------------------------------------------------------------------
