/*====================================================================*\

PathnameFilterDialog.java

Class: pathname-filter dialog.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.regexsearch;

//----------------------------------------------------------------------


// IMPORTS


import java.awt.Component;
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

import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import uk.blankaspect.common.exception.AppException;

import uk.blankaspect.ui.swing.action.KeyAction;

import uk.blankaspect.ui.swing.button.FButton;

import uk.blankaspect.ui.swing.container.SingleSelectionListPanel;

import uk.blankaspect.ui.swing.dialog.SingleTextFieldDialog;

import uk.blankaspect.ui.swing.label.FLabel;

import uk.blankaspect.ui.swing.misc.GuiUtils;

import uk.blankaspect.ui.swing.textfield.FTextField;

//----------------------------------------------------------------------


// CLASS: PATHNAME-FILTER DIALOG


class PathnameFilterDialog
	extends JDialog
	implements ActionListener, DocumentListener
{

////////////////////////////////////////////////////////////////////////
//  Class variables
////////////////////////////////////////////////////////////////////////

	private static	Point	location;

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	FileSet.FilterKind	filterKind;
	private	boolean				accepted;
	private	boolean				adjusting;
	private	JTextField			filterField;
	private	PatternListPanel	patternListPanel;

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	private static final	int		FILTER_FIELD_NUM_COLUMNS	= 40;

	private static final	String	FILTER_STR		= "filter";
	private static final	String	PATTERN_STR		= "Pattern";
	private static final	String	PATTERNS_STR	= "Patterns";

	// Commands
	private interface Command
	{
		String	ACCEPT	= "accept";
		String	CLOSE	= "close";
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	private PathnameFilterDialog(
		Window				owner,
		String				title,
		FileSet.FilterKind	filterKind,
		String				patterns)
	{
		// Call superclass constructor
		super(owner, title, ModalityType.APPLICATION_MODAL);

		// Set icons
		setIconImages(owner.getIconImages());

		// Initialise instance variables
		this.filterKind = filterKind;


		//----  Control panel

		GridBagLayout gridBag = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();

		JPanel controlPanel = new JPanel(gridBag);
		GuiUtils.setPaddedLineBorder(controlPanel);

		int gridY = 0;

		// Label: patterns
		JLabel patternsLabel = new FLabel(PATTERNS_STR);

		gbc.gridx = 0;
		gbc.gridy = gridY;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = AppConstants.COMPONENT_INSETS;
		gridBag.setConstraints(patternsLabel, gbc);
		controlPanel.add(patternsLabel);

		// Field: filter
		filterField = new FTextField(patterns, FILTER_FIELD_NUM_COLUMNS);
		filterField.getDocument().addDocumentListener(this);

		gbc.gridx = 1;
		gbc.gridy = gridY++;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = AppConstants.COMPONENT_INSETS;
		gridBag.setConstraints(filterField, gbc);
		controlPanel.add(filterField);

		// Panel: pattern selection list
		patternListPanel = new PatternListPanel((patterns == null) ? null : FileSet.stringToPatterns(patterns));

		gbc.gridx = 1;
		gbc.gridy = gridY++;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = AppConstants.COMPONENT_INSETS;
		gridBag.setConstraints(patternListPanel, gbc);
		controlPanel.add(patternListPanel);


		//----  Button panel

		JPanel buttonPanel = new JPanel(new GridLayout(1, 0, 8, 0));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));

		// Button: OK
		JButton okButton = new FButton(AppConstants.OK_STR);
		okButton.setActionCommand(Command.ACCEPT);
		okButton.addActionListener(this);
		buttonPanel.add(okButton);

		// Button: cancel
		JButton cancelButton = new FButton(AppConstants.CANCEL_STR);
		cancelButton.setActionCommand(Command.CLOSE);
		cancelButton.addActionListener(this);
		buttonPanel.add(cancelButton);


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
		gridBag.setConstraints(controlPanel, gbc);
		mainPanel.add(controlPanel);

		gbc.gridx = 0;
		gbc.gridy = gridY++;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(3, 0, 0, 0);
		gridBag.setConstraints(buttonPanel, gbc);
		mainPanel.add(buttonPanel);

		// Add commands to action map
		KeyAction.create(mainPanel, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
						 KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), Command.CLOSE, this);


		//----  Window

		// Set content pane
		setContentPane(mainPanel);

		// Dispose of window explicitly
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		// Handle window closing
		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(
				WindowEvent	event)
			{
				onClose();
			}
		});

		// Prevent dialog from being resized
		setResizable(false);

		// Resize dialog to its preferred size
		pack();

		// Set location of dialog
		if (location == null)
			location = GuiUtils.getComponentLocation(this, owner);
		setLocation(location);

		// Set default button
		getRootPane().setDefaultButton(okButton);

		// Show dialog
		setVisible(true);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static String showDialog(
		Component			parent,
		String				title,
		FileSet.FilterKind	filterKind,
		String				patterns)
	{
		return new PathnameFilterDialog(GuiUtils.getWindow(parent), title, filterKind, patterns).getPatterns();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : ActionListener interface
////////////////////////////////////////////////////////////////////////

	public void actionPerformed(
		ActionEvent	event)
	{
		String command = event.getActionCommand();

		if (command.equals(Command.ACCEPT))
			onAccept();

		else if (command.equals(Command.CLOSE))
			onClose();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : DocumentListener interface
////////////////////////////////////////////////////////////////////////

	public void changedUpdate(
		DocumentEvent	event)
	{
		// do nothing
	}

	//------------------------------------------------------------------

	public void insertUpdate(
		DocumentEvent	event)
	{
		updateList();
	}

	//------------------------------------------------------------------

	public void removeUpdate(
		DocumentEvent	event)
	{
		updateList();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	private String getPatterns()
	{
		return accepted ? FileSet.patternsToString(patternListPanel.getElements()) : null;
	}

	//------------------------------------------------------------------

	private void updateList()
	{
		if (!adjusting)
		{
			adjusting = true;
			patternListPanel.setElements(FileSet.stringToPatterns(filterField.getText()));
			adjusting = false;
		}
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
		location = getLocation();
		setVisible(false);
		dispose();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Enumerated types
////////////////////////////////////////////////////////////////////////


	// ENUMERATION: ERROR IDENTIFIERS


	private enum ErrorId
		implements AppException.IId
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		NO_FILTER
		("No filter was specified.");

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	String	message;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private ErrorId(
			String	message)
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

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// CLASS: PATTERN DIALOG


	private static class PatternDialog
		extends SingleTextFieldDialog
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		private static final	String	KEY	= PatternDialog.class.getCanonicalName();

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private PatternDialog(
			Window				owner,
			String				title,
			FileSet.FilterKind	filterKind,
			String				pattern)
		{
			super(owner, title, KEY + "." + filterKind, PATTERN_STR, pattern);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Class methods
	////////////////////////////////////////////////////////////////////

		private static String showDialog(
			Component			parent,
			String				title,
			FileSet.FilterKind	filterKind,
			String				pattern)
		{
			PatternDialog dialog = new PatternDialog(GuiUtils.getWindow(parent), title, filterKind, pattern);
			dialog.setVisible(true);
			return dialog.getText();
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		protected boolean isTextValid()
		{
			try
			{
				if (getField().getText().isEmpty())
					throw new AppException(ErrorId.NO_FILTER);
				return true;
			}
			catch (AppException e)
			{
				GuiUtils.setFocus(getField());
				JOptionPane.showMessageDialog(this, e, RegexSearchApp.SHORT_NAME, JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}

		//--------------------------------------------------------------

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Member classes : inner classes
////////////////////////////////////////////////////////////////////////


	// CLASS: PATTERN LIST PANEL


	private class PatternListPanel
		extends SingleSelectionListPanel<String>
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		private static final	int		NUM_COLUMNS	= 32;
		private static final	int		NUM_ROWS	= 8;

		private static final	String	DELETE_MESSAGE_STR	= "Do you want to delete the selected pattern?";

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private PatternListPanel(
			List<String>	patterns)
		{
			super(NUM_COLUMNS, NUM_ROWS, patterns, FileSet.MAX_NUM_FILTER_PATTERNS, null);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		protected String getAddElement()
		{
			return PatternDialog.showDialog(this, getTitleString(ADD_STR), filterKind, null);
		}

		//--------------------------------------------------------------

		@Override
		protected String getEditElement(int index)
		{
			return PatternDialog.showDialog(this, getTitleString(EDIT_STR), filterKind, getElement(index));
		}

		//--------------------------------------------------------------

		@Override
		protected boolean confirmDelete()
		{
			String[] optionStrs = Utils.getOptionStrings(DELETE_STR);
			return (JOptionPane.showOptionDialog(this, DELETE_MESSAGE_STR, getTitleString(DELETE_STR),
												 JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,
												 optionStrs, optionStrs[1]) == JOptionPane.OK_OPTION);
		}

		//--------------------------------------------------------------

		@Override
		protected void modelChanged()
		{
			if (!adjusting)
			{
				adjusting = true;
				filterField.setText(FileSet.patternsToString(getElements()));
				adjusting = false;
			}
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		private String getTitleString(
			String	actionStr)
		{
			return actionStr + " " + PATTERN_STR.toLowerCase() + " : " + filterKind + " " + FILTER_STR;
		}

		//--------------------------------------------------------------

	}

	//==================================================================

}

//----------------------------------------------------------------------
