/*====================================================================*\

PreferencesDialog.java

Preferences dialog box class.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.regexsearch;

//----------------------------------------------------------------------


// IMPORTS


import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
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

import java.io.File;

import java.nio.charset.Charset;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import uk.blankaspect.common.exception.AppException;
import uk.blankaspect.common.exception.FileException;

import uk.blankaspect.common.gui.BooleanComboBox;
import uk.blankaspect.common.gui.ColourSampleIcon;
import uk.blankaspect.common.gui.DimensionsSpinnerPanel;
import uk.blankaspect.common.gui.FButton;
import uk.blankaspect.common.gui.FComboBox;
import uk.blankaspect.common.gui.FIntegerSpinner;
import uk.blankaspect.common.gui.FixedWidthLabel;
import uk.blankaspect.common.gui.FLabel;
import uk.blankaspect.common.gui.FontEx;
import uk.blankaspect.common.gui.FontStyle;
import uk.blankaspect.common.gui.FTabbedPane;
import uk.blankaspect.common.gui.FTextField;
import uk.blankaspect.common.gui.GuiUtils;
import uk.blankaspect.common.gui.IntegerSpinner;
import uk.blankaspect.common.gui.PathnamePanel;
import uk.blankaspect.common.gui.SingleSelectionList;
import uk.blankaspect.common.gui.TextRendering;
import uk.blankaspect.common.gui.TitledBorder;

import uk.blankaspect.common.misc.FilenameSuffixFilter;
import uk.blankaspect.common.misc.FileWritingMode;
import uk.blankaspect.common.misc.KeyAction;
import uk.blankaspect.common.misc.MaxValueMap;
import uk.blankaspect.common.misc.NumberUtils;

import uk.blankaspect.common.textfield.ConstrainedTextField;
import uk.blankaspect.common.textfield.IntegerValueField;

//----------------------------------------------------------------------


// PREFERENCES DIALOG BOX CLASS


class PreferencesDialog
	extends JDialog
	implements ActionListener, ChangeListener, ListSelectionListener
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	private static final	String	KEY	= PreferencesDialog.class.getCanonicalName();

	// Main panel
	private static final	int		MODIFIERS_MASK	= ActionEvent.ALT_MASK | ActionEvent.META_MASK |
															ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK;

	private static final	String	TITLE_STR				= "Preferences";
	private static final	String	SAVE_CONFIGURATION_STR	= "Save configuration";
	private static final	String	SAVE_CONFIG_FILE_STR	= "Save configuration file";
	private static final	String	WRITE_CONFIG_FILE_STR	= "Write configuration file";

	// General panel
	private static final	String	CHARACTER_ENCODING_STR			= "Character encoding";
	private static final	String	ESCAPED_METACHARS_STR			= "Escaped metacharacters";
	private static final	String	REPLACEMENT_ESCAPE_CHAR_STR		= "Replacement escape character";
	private static final	String	IGNORE_FILENAME_CASE_STR		= "Ignore case of filenames";
	private static final	String	FILE_WRITING_MODE_STR			= "File-writing mode";
	private static final	String	PRESERVE_LINE_SEPARATOR_STR		= "Preserve line-separator kind";
	private static final	String	SHOW_UNIX_PATHNAMES_STR			= "Display UNIX-style pathnames";
	private static final	String	SELECT_TEXT_ON_FOCUS_GAINED_STR	= "Select text when focus is gained";
	private static final	String	SAVE_MAIN_WINDOW_LOCATION_STR	= "Save location of main window";
	private static final	String	HIDE_CONTROL_DIALOG_STR			= "Hide control dialog when searching";
	private static final	String	COPY_RESULTS_AS_LIST_FILE_STR	= "Copy search results as list file";
	private static final	String	DEFAULT_ENCODING_STR			= "<default encoding>";

	// Appearance panel
	private static final	int		PARAMETER_EDITOR_WIDTH_FIELD_LENGTH		= 3;
	private static final	int		PARAMETER_EDITOR_HEIGHT_FIELD_LENGTH	= 2;
	private static final	int		TEXT_VIEW_WIDTH_FIELD_LENGTH			= 3;
	private static final	int		TEXT_VIEW_HEIGHT_FIELD_LENGTH			= 3;
	private static final	int		TEXT_VIEW_MAX_NUM_COLUMNS_FIELD_LENGTH	= 4;
	private static final	int		RESULT_AREA_NUM_ROWS_FIELD_LENGTH		= 2;

	private static final	String	LOOK_AND_FEEL_STR			= "Look-and-feel";
	private static final	String	TEXT_ANTIALIASING_STR		= "Text antialiasing";
	private static final	String	PARAMETER_EDITOR_SIZE_STR	= "Size of target and replacement editors";
	private static final	String	COLUMNS_STR					= "columns";
	private static final	String	ROWS_STR					= "rows";
	private static final	String	RESULT_AREA_NUM_ROWS_STR	= "Number of rows in result area";
	private static final	String	TAB_SURROGATE_STR			= "Tab surrogate";
	private static final	String	TEXT_VIEW_STR				= "Text view";
	private static final	String	VIEWABLE_SIZE_STR			= "Viewable size";
	private static final	String	MAX_NUM_COLUMNS_STR			= "Maximum number of columns";
	private static final	String	TEXT_AREA_COLOURS_STR		= "Text area colours";
	private static final	String	TEXT_STR					= "Text";
	private static final	String	BACKGROUND_STR				= "Background";
	private static final	String	HIGHLIGHT_TEXT_STR			= "Highlighted text";
	private static final	String	HIGHLIGHT_BACKGROUND_STR	= "Highlighted background";
	private static final	String	NO_LOOK_AND_FEELS_STR		= "<no look-and-feels>";

	private static final	String	TAC_STR							= "Text area colour | ";
	private static final	String	TEXT_TITLE_STR					= "Text";
	private static final	String	BACKGROUND_TITLE_STR			= "Background";
	private static final	String	HIGHLIGHT_TEXT_TITLE_STR		= "Highlighted text";
	private static final	String	HIGHLIGHT_BACKGROUND_TITLE_STR	= "Highlighted background";

	// Tab width panel
	private static final	int		DEFAULT_TAB_WIDTH_FIELD_LENGTH					= 3;
	private static final	int		TARGET_AND_REPLACEMENT_TAB_WIDTH_FIELD_LENGTH	= 3;

	private static final	String	DEFAULT_TAB_WIDTH_STR		= "Default tab width";
	private static final	String	TARGET_AND_REPLACEMENT_STR	= "Target and replacement editors";
	private static final	String	TAB_WIDTH1_STR				= "Tab width";
	private static final	String	ADD_STR						= "Add";
	private static final	String	EDIT_STR					= "Edit";
	private static final	String	DELETE_STR					= "Delete";
	private static final	String	ADD_FILTER_STR				= "Add tab-width filter";
	private static final	String	EDIT_FILTER_STR				= "Edit tab-width filter";
	private static final	String	DELETE_FILTER_STR			= "Delete tab-width filter";
	private static final	String	DELETE_FILTER_MESSAGE_STR	= "Do you want to delete the selected " +
																	"filter?";

	// Editor panel
	private static final	int		EDITOR_COMMAND_FIELD_NUM_COLUMNS	= 40;

	private static final	String	COMMAND_STR	= "Command";

	// File locations panel
	private static final	String	DEFAULT_SEARCH_PARAMS_STR		= "Default search parameters";
	private static final	String	DEFAULT_SEARCH_PARAMS_TITLE_STR	= "Default search parameters file";
	private static final	String	SELECT_STR						= "Select";
	private static final	String	SELECT_FILE_STR					= "Select file";

	// Fonts panel
	private static final	String	PT_STR	= "pt";

	// Commands
	private interface Command
	{
		String	CHOOSE_TEXT_COLOUR					= "chooseTextColour";
		String	CHOOSE_BACKGROUND_COLOUR			= "chooseBackgroundColour";
		String	CHOOSE_HIGHLIGHT_TEXT_COLOUR		= "chooseHighlightTextColour";
		String	CHOOSE_HIGHLIGHT_BACKGROUND_COLOUR	= "chooseHighlightBackgroundColour";
		String	CHOOSE_DEFAULT_SEARCH_PARAMS_FILE	= "choosedDefaultSearchParamsFile";
		String	ADD_TAB_WIDTH_FILTER				= "addTabWidthFilter";
		String	EDIT_TAB_WIDTH_FILTER				= "editTabWidthFilter";
		String	DELETE_TAB_WIDTH_FILTER				= "deleteTabWidthFilter";
		String	CONFIRM_DELETE_TAB_WIDTH_FILTER		= "confirmDeleteTabWidthFilter";
		String	MOVE_TAB_WIDTH_FILTER_UP			= "moveTabWidthFilterUp";
		String	MOVE_TAB_WIDTH_FILTER_DOWN			= "moveTabWidthFilterDown";
		String	MOVE_TAB_WIDTH_FILTER				= "moveTabWidthFilter";
		String	SAVE_CONFIGURATION					= "saveConfiguration";
		String	ACCEPT								= "accept";
		String	CLOSE								= "close";
	}

	private static final	Map<String, String>	COMMAND_MAP;

////////////////////////////////////////////////////////////////////////
//  Enumerated types
////////////////////////////////////////////////////////////////////////


	// TABS


	private enum Tab
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		GENERAL
		(
			"General"
		)
		{
			@Override
			protected JPanel createPanel(PreferencesDialog dialog)
			{
				return dialog.createPanelGeneral();
			}

			//----------------------------------------------------------

			@Override
			protected void validatePreferences(PreferencesDialog dialog)
				throws AppException
			{
				dialog.validatePreferencesGeneral();
			}

			//----------------------------------------------------------

			@Override
			protected void setPreferences(PreferencesDialog dialog)
			{
				dialog.setPreferencesGeneral();
			}

			//----------------------------------------------------------
		},

		APPEARANCE
		(
			"Appearance"
		)
		{
			@Override
			protected JPanel createPanel(PreferencesDialog dialog)
			{
				return dialog.createPanelAppearance();
			}

			//----------------------------------------------------------

			@Override
			protected void validatePreferences(PreferencesDialog dialog)
				throws AppException
			{
				dialog.validatePreferencesAppearance();
			}

			//----------------------------------------------------------

			@Override
			protected void setPreferences(PreferencesDialog dialog)
			{
				dialog.setPreferencesAppearance();
			}

			//----------------------------------------------------------
		},

		TAB_WIDTH
		(
			"Tab width"
		)
		{
			@Override
			protected JPanel createPanel(PreferencesDialog dialog)
			{
				return dialog.createPanelTabWidth();
			}

			//----------------------------------------------------------

			@Override
			protected void validatePreferences(PreferencesDialog dialog)
				throws AppException
			{
				dialog.validatePreferencesTabWidth();
			}

			//----------------------------------------------------------

			@Override
			protected void setPreferences(PreferencesDialog dialog)
			{
				dialog.setPreferencesTabWidth();
			}

			//----------------------------------------------------------
		},

		EDITOR
		(
			"Editor"
		)
		{
			@Override
			protected JPanel createPanel(PreferencesDialog dialog)
			{
				return dialog.createPanelEditor();
			}

			//----------------------------------------------------------

			@Override
			protected void validatePreferences(PreferencesDialog dialog)
				throws AppException
			{
				dialog.validatePreferencesEditor();
			}

			//----------------------------------------------------------

			@Override
			protected void setPreferences(PreferencesDialog dialog)
			{
				dialog.setPreferencesEditor();
			}

			//----------------------------------------------------------
		},

		FILE_LOCATIONS
		(
			"File locations"
		)
		{
			@Override
			protected JPanel createPanel(PreferencesDialog dialog)
			{
				return dialog.createPanelFileLocations();
			}

			//----------------------------------------------------------

			@Override
			protected void validatePreferences(PreferencesDialog dialog)
				throws AppException
			{
				dialog.validatePreferencesFileLocations();
			}

			//----------------------------------------------------------

			@Override
			protected void setPreferences(PreferencesDialog dialog)
			{
				dialog.setPreferencesFileLocations();
			}

			//----------------------------------------------------------
		},

		FONTS
		(
			"Fonts"
		)
		{
			@Override
			protected JPanel createPanel(PreferencesDialog dialog)
			{
				return dialog.createPanelFonts();
			}

			//----------------------------------------------------------

			@Override
			protected void validatePreferences(PreferencesDialog dialog)
				throws AppException
			{
				dialog.validatePreferencesFonts();
			}

			//----------------------------------------------------------

			@Override
			protected void setPreferences(PreferencesDialog dialog)
			{
				dialog.setPreferencesFonts();
			}

			//----------------------------------------------------------
		};

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private Tab(String text)
		{
			this.text = text;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Abstract methods
	////////////////////////////////////////////////////////////////////

		protected abstract JPanel createPanel(PreferencesDialog dialog);

		//--------------------------------------------------------------

		protected abstract void validatePreferences(PreferencesDialog dialog)
			throws AppException;

		//--------------------------------------------------------------

		protected abstract void setPreferences(PreferencesDialog dialog);

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance fields
	////////////////////////////////////////////////////////////////////

		private	String	text;

	}

	//==================================================================


	// ERROR IDENTIFIERS


	private enum ErrorId
		implements AppException.IId
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		FILE_DOES_NOT_EXIST
		("The file does not exist."),

		NOT_A_FILE
		("The pathname does not denote a normal file."),

		FILE_ACCESS_NOT_PERMITTED
		("Access to the file was not permitted."),

		INVALID_TAB_SURROGATE
		("The tab surrogate is invalid.");

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
	//  Instance fields
	////////////////////////////////////////////////////////////////////

		private	String	message;

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// ESCAPED METACHARACTERS FIELD CLASS


	private static class EscapedMetacharsField
		extends ConstrainedTextField
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		private static final	int	NUM_COLUMNS	= 16;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private EscapedMetacharsField(String text)
		{
			super(AppConfig.PUNCTUATION_CHARS.length(), NUM_COLUMNS, text);
			AppFont.TEXT_FIELD.apply(this);
			GuiUtils.setTextComponentMargins(this);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		protected boolean acceptCharacter(char ch,
										  int  index)
		{
			return (AppConfig.PUNCTUATION_CHARS.indexOf(ch) >= 0);
		}

		//--------------------------------------------------------------

	}

	//==================================================================


	// TAB SURROGATE FIELD CLASS


	private static class TabSurrogateField
		extends ConstrainedTextField
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		private static final	int	LENGTH	= 4;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private TabSurrogateField(char ch)
		{
			super(LENGTH);
			AppFont.TEXT_FIELD.apply(this);
			GuiUtils.setTextComponentMargins(this);
			setText((Character.isISOControl(ch) || !getFont().canDisplay(ch))
															? NumberUtils.uIntToHexString(ch, 4, '0')
															: Character.toString(ch));
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		public char getChar()
		{
			char ch = '\0';
			String str = getText();
			switch (str.length())
			{
				case 1:
					ch = str.charAt(0);
					break;

				case LENGTH:
					try
					{
						ch = (char)Integer.parseInt(str, 16);
					}
					catch (NumberFormatException e)
					{
						// ignore
					}
					break;
			}
			return ch;
		}

		//--------------------------------------------------------------

	}

	//==================================================================


	// TAB-WIDTH SELECTION LIST CLASS


	private static class TabWidthList
		extends SingleSelectionList<TabWidthFilter>
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		private static final	int	FILTER_FIELD_NUM_COLUMNS	= 40;
		private static final	int	WIDTH_FIELD_NUM_COLUMNS		= 3;
		private static final	int	NUM_ROWS					= 16;

		private static final	int	SEPARATOR_WIDTH	= 1;

		private static final	Color	SEPARATOR_COLOUR	= new Color(192, 200, 192);

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private TabWidthList(List<TabWidthFilter> filters)
		{
			super(FILTER_FIELD_NUM_COLUMNS + WIDTH_FIELD_NUM_COLUMNS, NUM_ROWS, AppFont.MAIN.getFont(),
				  filters);
			setExtraWidth(2 * getHorizontalMargin() + SEPARATOR_WIDTH);
			setRowHeight(getRowHeight() + 1);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		public String getElementText(int index)
		{
			return getElement(index).getFilterString();
		}

		//--------------------------------------------------------------

		@Override
		protected void drawElement(Graphics gr,
								   int      index)
		{
			// Create copy of graphics context
			gr = gr.create();

			// Set rendering hints for text antialiasing and fractional metrics
			TextRendering.setHints((Graphics2D)gr);

			// Get filter text and truncate it if it is too wide
			FontMetrics fontMetrics = gr.getFontMetrics();
			int filterFieldWidth = getMaxTextWidth() - WIDTH_FIELD_NUM_COLUMNS * getColumnWidth();
			String text = truncateText(getElementText(index), fontMetrics, filterFieldWidth);

			// Draw filter text
			int rowHeight = getRowHeight();
			int x = getHorizontalMargin();
			int y = index * rowHeight;
			int textY = y + GuiUtils.getBaselineOffset(rowHeight, fontMetrics);
			gr.setColor(getForegroundColour(index));
			gr.drawString(text, x, textY);

			// Draw tab-width text
			text = Integer.toString(getElement(index).getTabWidth());
			x = getWidth() - getHorizontalMargin() - fontMetrics.stringWidth(text);
			gr.drawString(text, x, textY);

			// Draw separator
			x = getHorizontalMargin() + filterFieldWidth + getExtraWidth() / 2;
			gr.setColor(SEPARATOR_COLOUR);
			gr.drawLine(x, y, x, y + rowHeight - 1);

			// Draw bottom border
			y += rowHeight - 1;
			gr.drawLine(0, y, getWidth() - 1, y);
		}

		//--------------------------------------------------------------

	}

	//==================================================================


	// APPEARANCE PANEL LABEL CLASS


	private static class AppearancePanelLabel
		extends FixedWidthLabel
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		private static final	String	KEY	= AppearancePanelLabel.class.getCanonicalName();

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private AppearancePanelLabel(String text)
		{
			super(text);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Class methods
	////////////////////////////////////////////////////////////////////

		private static void reset()
		{
			MaxValueMap.removeAll(KEY);
		}

		//--------------------------------------------------------------

		private static void update()
		{
			MaxValueMap.update(KEY);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		protected String getKey()
		{
			return KEY;
		}

		//--------------------------------------------------------------

	}

	//==================================================================


	// TAB-WIDTH PANEL LABEL CLASS


	private static class TabWidthPanelLabel
		extends FixedWidthLabel
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		private static final	String	KEY	= TabWidthPanelLabel.class.getCanonicalName();

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private TabWidthPanelLabel(String text)
		{
			super(text);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Class methods
	////////////////////////////////////////////////////////////////////

		private static void reset()
		{
			MaxValueMap.removeAll(KEY);
		}

		//--------------------------------------------------------------

		private static void update()
		{
			MaxValueMap.update(KEY);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		protected String getKey()
		{
			return KEY;
		}

		//--------------------------------------------------------------

	}

	//==================================================================


	// COLOUR BUTTON CLASS


	private static class ColourButton
		extends JButton
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		private static final	int		ICON_WIDTH	= 40;
		private static final	int		ICON_HEIGHT	= 16;
		private static final	Insets	MARGINS		= new Insets(2, 2, 2, 2);

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private ColourButton(Color colour)
		{
			super(new ColourSampleIcon(ICON_WIDTH, ICON_HEIGHT));
			setMargin(MARGINS);
			setForeground(colour);
		}

		//--------------------------------------------------------------

	}

	//==================================================================


	// FONT PANEL CLASS


	private static class FontPanel
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		private static final	int	MIN_SIZE	= 0;
		private static final	int	MAX_SIZE	= 99;

		private static final	int	SIZE_FIELD_LENGTH	= 2;

		private static final	String	DEFAULT_FONT_STR	= "<default font>";

	////////////////////////////////////////////////////////////////////
	//  Member classes : non-inner classes
	////////////////////////////////////////////////////////////////////


		// SIZE SPINNER CLASS


		private static class SizeSpinner
			extends IntegerSpinner
		{

		////////////////////////////////////////////////////////////////
		//  Constructors
		////////////////////////////////////////////////////////////////

			private SizeSpinner(int value)
			{
				super(value, MIN_SIZE, MAX_SIZE, SIZE_FIELD_LENGTH);
				AppFont.TEXT_FIELD.apply(this);
			}

			//----------------------------------------------------------

		////////////////////////////////////////////////////////////////
		//  Instance methods : overriding methods
		////////////////////////////////////////////////////////////////

			/**
			 * @throws NumberFormatException
			 */

			@Override
			protected int getEditorValue()
			{
				IntegerValueField field = (IntegerValueField)getEditor();
				return (field.isEmpty() ? 0 : field.getValue());
			}

			//----------------------------------------------------------

			@Override
			protected void setEditorValue(int value)
			{
				IntegerValueField field = (IntegerValueField)getEditor();
				if (value == 0)
					field.setText(null);
				else
					field.setValue(value);
			}

			//----------------------------------------------------------

		}

		//==============================================================

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private FontPanel(FontEx   font,
						  String[] fontNames)
		{
			nameComboBox = new FComboBox<>();
			nameComboBox.addItem(DEFAULT_FONT_STR);
			for (String fontName : fontNames)
				nameComboBox.addItem(fontName);
			nameComboBox.setSelectedIndex(Utils.indexOf(font.getName(), fontNames) + 1);

			styleComboBox = new FComboBox<>(FontStyle.values());
			styleComboBox.setSelectedValue(font.getStyle());

			sizeSpinner = new SizeSpinner(font.getSize());
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		public FontEx getFont()
		{
			String name = (nameComboBox.getSelectedIndex() <= 0) ? null : nameComboBox.getSelectedValue();
			return new FontEx(name, styleComboBox.getSelectedValue(), sizeSpinner.getIntValue());
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance fields
	////////////////////////////////////////////////////////////////////

		private	FComboBox<String>		nameComboBox;
		private	FComboBox<FontStyle>	styleComboBox;
		private	SizeSpinner				sizeSpinner;

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	private PreferencesDialog(Window owner)
	{

		// Call superclass constructor
		super(owner, TITLE_STR, Dialog.ModalityType.APPLICATION_MODAL);

		// Set icons
		setIconImages(owner.getIconImages());


		//----  Tabbed panel

		tabbedPanel = new FTabbedPane();
		for (Tab tab : Tab.values())
			tabbedPanel.addTab(tab.text, tab.createPanel(this));
		tabbedPanel.setSelectedIndex(tabIndex);


		//----  Button panel: save configuration

		JPanel saveButtonPanel = new JPanel(new GridLayout(1, 0, 8, 0));

		// Button: save configuration
		JButton saveButton = new FButton(SAVE_CONFIGURATION_STR + AppConstants.ELLIPSIS_STR);
		saveButton.setActionCommand(Command.SAVE_CONFIGURATION);
		saveButton.addActionListener(this);
		saveButtonPanel.add(saveButton);


		//----  Button panel: OK, cancel

		JPanel okCancelButtonPanel = new JPanel(new GridLayout(1, 0, 8, 0));

		// Button: OK
		JButton okButton = new FButton(AppConstants.OK_STR);
		okButton.setActionCommand(Command.ACCEPT);
		okButton.addActionListener(this);
		okCancelButtonPanel.add(okButton);

		// Button: cancel
		JButton cancelButton = new FButton(AppConstants.CANCEL_STR);
		cancelButton.setActionCommand(Command.CLOSE);
		cancelButton.addActionListener(this);
		okCancelButtonPanel.add(cancelButton);


		//----  Button panel

		GridBagLayout gridBag = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();

		JPanel buttonPanel = new JPanel(gridBag);
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(3, 24, 3, 24));

		int gridX = 0;

		gbc.gridx = gridX++;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.5;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(0, 0, 0, 12);
		gridBag.setConstraints(saveButtonPanel, gbc);
		buttonPanel.add(saveButtonPanel);

		gbc.gridx = gridX++;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.5;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(0, 12, 0, 0);
		gridBag.setConstraints(okCancelButtonPanel, gbc);
		buttonPanel.add(okCancelButtonPanel);


		//----  Main panel

		JPanel mainPanel = new JPanel(gridBag);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

		int gridY = 0;

		gbc.gridx = 0;
		gbc.gridy = gridY++;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(0, 0, 0, 0);
		gridBag.setConstraints(tabbedPanel, gbc);
		mainPanel.add(tabbedPanel);

		gbc.gridx = 0;
		gbc.gridy = gridY++;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.fill = GridBagConstraints.HORIZONTAL;
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
			public void windowClosing(WindowEvent event)
			{
				onClose();
			}
		});

		// Prevent dialog from being resized
		setResizable(false);

		// Resize dialog to its preferred size
		pack();

		// Set location of dialog box
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

	public static boolean showDialog(Component parent)
	{
		return new PreferencesDialog(GuiUtils.getWindow(parent)).accepted;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : ActionListener interface
////////////////////////////////////////////////////////////////////////

	public void actionPerformed(ActionEvent event)
	{
		String command = event.getActionCommand();
		if (command.equals(Command.CONFIRM_DELETE_TAB_WIDTH_FILTER) &&
			 ((event.getModifiers() & MODIFIERS_MASK) == ActionEvent.SHIFT_MASK))
			command = Command.DELETE_TAB_WIDTH_FILTER;
		else if (COMMAND_MAP.containsKey(command))
			command = COMMAND_MAP.get(command);

		if (command.equals(Command.CHOOSE_TEXT_COLOUR))
			onChooseTextColour();

		else if (command.equals(Command.CHOOSE_BACKGROUND_COLOUR))
			onChooseBackgroundColour();

		else if (command.equals(Command.CHOOSE_HIGHLIGHT_TEXT_COLOUR))
			onChooseHighlightTextColour();

		else if (command.equals(Command.CHOOSE_HIGHLIGHT_BACKGROUND_COLOUR))
			onChooseHighlightBackgroundColour();

		else if (command.equals(Command.CHOOSE_DEFAULT_SEARCH_PARAMS_FILE))
			onChooseDefaultSearchParamsFile();

		else if (command.equals(Command.ADD_TAB_WIDTH_FILTER))
			onAddTabWidthFilter();

		else if (command.equals(Command.EDIT_TAB_WIDTH_FILTER))
			onEditTabWidthFilter();

		else if (command.equals(Command.DELETE_TAB_WIDTH_FILTER))
			onDeleteTabWidthFilter();

		else if (command.equals(Command.CONFIRM_DELETE_TAB_WIDTH_FILTER))
			onConfirmDeleteTabWidthFilter();

		else if (command.equals(Command.MOVE_TAB_WIDTH_FILTER_UP))
			onMoveTabWidthFilterUp();

		else if (command.equals(Command.MOVE_TAB_WIDTH_FILTER_DOWN))
			onMoveTabWidthFilterDown();

		else if (command.equals(Command.MOVE_TAB_WIDTH_FILTER))
			onMoveTabWidthFilter();

		else if (command.equals(Command.SAVE_CONFIGURATION))
			onSaveConfiguration();

		else if (command.equals(Command.ACCEPT))
			onAccept();

		else if (command.equals(Command.CLOSE))
			onClose();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : ChangeListener interface
////////////////////////////////////////////////////////////////////////

	public void stateChanged(ChangeEvent event)
	{
		if (!tabWidthFilterListScrollPane.getVerticalScrollBar().getValueIsAdjusting() &&
			 !tabWidthFilterList.isDragging())
			tabWidthFilterList.snapViewPosition();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : ListSelectionListener interface
////////////////////////////////////////////////////////////////////////

	public void valueChanged(ListSelectionEvent event)
	{
		if (!event.getValueIsAdjusting())
		{
			Object eventSource = event.getSource();

			if (eventSource == tabWidthFilterList)
				updateTabWidthFilterButtons();
		}
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	private void validatePreferences()
		throws AppException
	{
		for (Tab tab : Tab.values())
			tab.validatePreferences(this);
	}

	//------------------------------------------------------------------

	private void setPreferences()
	{
		for (Tab tab : Tab.values())
			tab.setPreferences(this);
	}

	//------------------------------------------------------------------

	private void updateTabWidthFilterButtons()
	{
		tabWidthAddButton.
				setEnabled(tabWidthFilterList.getNumElements() < AppConfig.MAX_NUM_TAB_WIDTH_FILTERS);
		tabWidthEditButton.setEnabled(tabWidthFilterList.isSelection());
		tabWidthDeleteButton.setEnabled(tabWidthFilterList.isSelection());
	}

	//------------------------------------------------------------------

	private void onChooseTextColour()
	{
		Color colour = JColorChooser.showDialog(this, TAC_STR + TEXT_TITLE_STR,
												textColourButton.getForeground());
		if (colour != null)
			textColourButton.setForeground(colour);
	}

	//------------------------------------------------------------------

	private void onChooseBackgroundColour()
	{
		Color colour = JColorChooser.showDialog(this, TAC_STR + BACKGROUND_TITLE_STR,
												backgroundColourButton.getForeground());
		if (colour != null)
			backgroundColourButton.setForeground(colour);
	}

	//------------------------------------------------------------------

	private void onChooseHighlightTextColour()
	{
		Color colour = JColorChooser.showDialog(this, TAC_STR + HIGHLIGHT_TEXT_TITLE_STR,
												highlightTextColourButton.getForeground());
		if (colour != null)
			highlightTextColourButton.setForeground(colour);
	}

	//------------------------------------------------------------------

	private void onChooseHighlightBackgroundColour()
	{
		Color colour = JColorChooser.showDialog(this, TAC_STR + HIGHLIGHT_BACKGROUND_TITLE_STR,
												highlightBackgroundColourButton.getForeground());
		if (colour != null)
			highlightBackgroundColourButton.setForeground(colour);
	}

	//------------------------------------------------------------------

	private void onChooseDefaultSearchParamsFile()
	{
		if (defaultSearchParamsFileChooser == null)
		{
			defaultSearchParamsFileChooser = new JFileChooser();
			defaultSearchParamsFileChooser.setDialogTitle(DEFAULT_SEARCH_PARAMS_TITLE_STR);
			defaultSearchParamsFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			defaultSearchParamsFileChooser.setApproveButtonMnemonic(KeyEvent.VK_S);
			defaultSearchParamsFileChooser.setApproveButtonToolTipText(SELECT_FILE_STR);
			defaultSearchParamsFileChooser.
								setFileFilter(new FilenameSuffixFilter(AppConstants.XML_FILES_STR,
																	   AppConstants.XML_FILE_SUFFIX));
		}
		defaultSearchParamsFileChooser.setSelectedFile(defaultSearchParamsField.getCanonicalFile());
		defaultSearchParamsFileChooser.rescanCurrentDirectory();
		if (defaultSearchParamsFileChooser.showDialog(this, SELECT_STR) == JFileChooser.APPROVE_OPTION)
			defaultSearchParamsField.setFile(defaultSearchParamsFileChooser.getSelectedFile());
	}

	//------------------------------------------------------------------

	private void onAddTabWidthFilter()
	{
		TabWidthFilter filter = TabWidthFilterDialog.showDialog(this, ADD_FILTER_STR, null);
		if (filter != null)
		{
			tabWidthFilterList.addElement(filter);
			updateTabWidthFilterButtons();
		}
	}

	//------------------------------------------------------------------

	private void onEditTabWidthFilter()
	{
		int index = tabWidthFilterList.getSelectedIndex();
		TabWidthFilter filter = TabWidthFilterDialog.showDialog(this, EDIT_FILTER_STR,
																tabWidthFilterList.getElement(index));
		if (filter != null)
			tabWidthFilterList.setElement(index, filter);
	}

	//------------------------------------------------------------------

	private void onConfirmDeleteTabWidthFilter()
	{
		String[] optionStrs = Utils.getOptionStrings(DELETE_STR);
		if (JOptionPane.showOptionDialog(this, DELETE_FILTER_MESSAGE_STR, DELETE_FILTER_STR,
										 JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,
										 optionStrs, optionStrs[1]) == JOptionPane.OK_OPTION)
			onDeleteTabWidthFilter();
	}

	//------------------------------------------------------------------

	private void onDeleteTabWidthFilter()
	{
		tabWidthFilterList.removeElement(tabWidthFilterList.getSelectedIndex());
		updateTabWidthFilterButtons();
	}

	//------------------------------------------------------------------

	private void onMoveTabWidthFilterUp()
	{
		int index = tabWidthFilterList.getSelectedIndex();
		tabWidthFilterList.moveElement(index, index - 1);
	}

	//------------------------------------------------------------------

	private void onMoveTabWidthFilterDown()
	{
		int index = tabWidthFilterList.getSelectedIndex();
		tabWidthFilterList.moveElement(index, index + 1);
	}

	//------------------------------------------------------------------

	private void onMoveTabWidthFilter()
	{
		int fromIndex = tabWidthFilterList.getSelectedIndex();
		int toIndex = tabWidthFilterList.getDragEndIndex();
		if (toIndex > fromIndex)
			--toIndex;
		tabWidthFilterList.moveElement(fromIndex, toIndex);
	}

	//------------------------------------------------------------------

	private void onSaveConfiguration()
	{
		try
		{
			validatePreferences();

			File file = AppConfig.INSTANCE.chooseFile(this);
			if (file != null)
			{
				String[] optionStrs = Utils.getOptionStrings(AppConstants.REPLACE_STR);
				if (!file.exists() ||
					 (JOptionPane.showOptionDialog(this, Utils.getPathname(file) +
																			AppConstants.ALREADY_EXISTS_STR,
												   SAVE_CONFIG_FILE_STR, JOptionPane.OK_CANCEL_OPTION,
												   JOptionPane.WARNING_MESSAGE, null, optionStrs,
												   optionStrs[1]) == JOptionPane.OK_OPTION))
				{
					setPreferences();
					accepted = true;
					TaskProgressDialog.showDialog(this, WRITE_CONFIG_FILE_STR,
												  new Task.WriteConfig(file));
				}
			}
		}
		catch (AppException e)
		{
			JOptionPane.showMessageDialog(this, e, App.SHORT_NAME, JOptionPane.ERROR_MESSAGE);
		}
		if (accepted)
			onClose();
	}

	//------------------------------------------------------------------

	private void onAccept()
	{
		try
		{
			validatePreferences();
			setPreferences();
			accepted = true;
			onClose();
		}
		catch (AppException e)
		{
			JOptionPane.showMessageDialog(this, e, App.SHORT_NAME, JOptionPane.ERROR_MESSAGE);
		}
	}

	//------------------------------------------------------------------

	private void onClose()
	{
		FPathnameField.removeObservers(KEY);

		location = getLocation();
		tabIndex = tabbedPanel.getSelectedIndex();
		setVisible(false);
		dispose();
	}

	//------------------------------------------------------------------

	private JPanel createPanelGeneral()
	{

		//----  Control panel

		GridBagLayout gridBag = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();

		JPanel controlPanel = new JPanel(gridBag);
		GuiUtils.setPaddedLineBorder(controlPanel);

		int gridY = 0;

		AppConfig config = AppConfig.INSTANCE;

		// Label: character encoding
		JLabel characterEncodingLabel = new FLabel(CHARACTER_ENCODING_STR);

		gbc.gridx = 0;
		gbc.gridy = gridY;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = AppConstants.COMPONENT_INSETS;
		gridBag.setConstraints(characterEncodingLabel, gbc);
		controlPanel.add(characterEncodingLabel);

		// Combo box: character encoding
		characterEncodingComboBox = new FComboBox<>();
		characterEncodingComboBox.addItem(DEFAULT_ENCODING_STR);
		for (String key : Charset.availableCharsets().keySet())
			characterEncodingComboBox.addItem(key);
		String encodingName = config.getCharacterEncoding();
		if (encodingName == null)
			characterEncodingComboBox.setSelectedIndex(0);
		else
			characterEncodingComboBox.setSelectedValue(encodingName);

		gbc.gridx = 1;
		gbc.gridy = gridY++;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = AppConstants.COMPONENT_INSETS;
		gridBag.setConstraints(characterEncodingComboBox, gbc);
		controlPanel.add(characterEncodingComboBox);

		// Label: escaped metacharacters
		JLabel escapedMetacharsLabel = new FLabel(ESCAPED_METACHARS_STR);

		gbc.gridx = 0;
		gbc.gridy = gridY;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = AppConstants.COMPONENT_INSETS;
		gridBag.setConstraints(escapedMetacharsLabel, gbc);
		controlPanel.add(escapedMetacharsLabel);

		// Field: escaped metacharacters
		escapedMetacharsField = new EscapedMetacharsField(config.getEscapedMetacharacters());

		gbc.gridx = 1;
		gbc.gridy = gridY++;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = AppConstants.COMPONENT_INSETS;
		gridBag.setConstraints(escapedMetacharsField, gbc);
		controlPanel.add(escapedMetacharsField);

		// Label: replacement escape character
		JLabel replacementEscapeCharLabel = new FLabel(REPLACEMENT_ESCAPE_CHAR_STR);

		gbc.gridx = 0;
		gbc.gridy = gridY;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = AppConstants.COMPONENT_INSETS;
		gridBag.setConstraints(replacementEscapeCharLabel, gbc);
		controlPanel.add(replacementEscapeCharLabel);

		// Combo box: replacement escape character
		replacementEscapeCharComboBox = new FComboBox<>();
		for (int i = 0; i < AppConfig.PUNCTUATION_CHARS.length(); i++)
			replacementEscapeCharComboBox.
									addItem(Character.valueOf(AppConfig.PUNCTUATION_CHARS.charAt(i)));
		replacementEscapeCharComboBox.
								setSelectedValue(Character.valueOf(config.getReplacementEscapeChar()));

		gbc.gridx = 1;
		gbc.gridy = gridY++;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = AppConstants.COMPONENT_INSETS;
		gridBag.setConstraints(replacementEscapeCharComboBox, gbc);
		controlPanel.add(replacementEscapeCharComboBox);

		// Label: ignore filename case
		JLabel ignoreFilenameCaseLabel = new FLabel(IGNORE_FILENAME_CASE_STR);

		gbc.gridx = 0;
		gbc.gridy = gridY;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = AppConstants.COMPONENT_INSETS;
		gridBag.setConstraints(ignoreFilenameCaseLabel, gbc);
		controlPanel.add(ignoreFilenameCaseLabel);

		// Combo box: ignore filename case
		ignoreFilenameCaseComboBox = new BooleanComboBox(config.isIgnoreFilenameCase());

		gbc.gridx = 1;
		gbc.gridy = gridY++;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = AppConstants.COMPONENT_INSETS;
		gridBag.setConstraints(ignoreFilenameCaseComboBox, gbc);
		controlPanel.add(ignoreFilenameCaseComboBox);

		// Label: file-writing mode
		JLabel fileWritingModeLabel = new FLabel(FILE_WRITING_MODE_STR);

		gbc.gridx = 0;
		gbc.gridy = gridY;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = AppConstants.COMPONENT_INSETS;
		gridBag.setConstraints(fileWritingModeLabel, gbc);
		controlPanel.add(fileWritingModeLabel);

		// Combo box: file-writing mode
		fileWritingModeComboBox = new FComboBox<>(FileWritingMode.values());
		fileWritingModeComboBox.setSelectedValue(config.getFileWritingMode());

		gbc.gridx = 1;
		gbc.gridy = gridY++;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = AppConstants.COMPONENT_INSETS;
		gridBag.setConstraints(fileWritingModeComboBox, gbc);
		controlPanel.add(fileWritingModeComboBox);

		// Label: preserve line separator
		JLabel preserveLineSeparatorLabel = new FLabel(PRESERVE_LINE_SEPARATOR_STR);

		gbc.gridx = 0;
		gbc.gridy = gridY;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = AppConstants.COMPONENT_INSETS;
		gridBag.setConstraints(preserveLineSeparatorLabel, gbc);
		controlPanel.add(preserveLineSeparatorLabel);

		// Combo box: preserve line separator
		preserveLineSeparatorComboBox = new BooleanComboBox(config.isPreserveLineSeparator());

		gbc.gridx = 1;
		gbc.gridy = gridY++;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = AppConstants.COMPONENT_INSETS;
		gridBag.setConstraints(preserveLineSeparatorComboBox, gbc);
		controlPanel.add(preserveLineSeparatorComboBox);

		// Label: show UNIX pathnames
		JLabel showUnixPathnamesLabel = new FLabel(SHOW_UNIX_PATHNAMES_STR);

		gbc.gridx = 0;
		gbc.gridy = gridY;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = AppConstants.COMPONENT_INSETS;
		gridBag.setConstraints(showUnixPathnamesLabel, gbc);
		controlPanel.add(showUnixPathnamesLabel);

		// Combo box: show UNIX pathnames
		showUnixPathnamesComboBox = new BooleanComboBox(config.isShowUnixPathnames());

		gbc.gridx = 1;
		gbc.gridy = gridY++;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = AppConstants.COMPONENT_INSETS;
		gridBag.setConstraints(showUnixPathnamesComboBox, gbc);
		controlPanel.add(showUnixPathnamesComboBox);

		// Label: select text on focus gained
		JLabel selectTextOnFocusGainedLabel = new FLabel(SELECT_TEXT_ON_FOCUS_GAINED_STR);

		gbc.gridx = 0;
		gbc.gridy = gridY;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = AppConstants.COMPONENT_INSETS;
		gridBag.setConstraints(selectTextOnFocusGainedLabel, gbc);
		controlPanel.add(selectTextOnFocusGainedLabel);

		// Combo box: select text on focus gained
		selectTextOnFocusGainedComboBox = new BooleanComboBox(config.isSelectTextOnFocusGained());

		gbc.gridx = 1;
		gbc.gridy = gridY++;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = AppConstants.COMPONENT_INSETS;
		gridBag.setConstraints(selectTextOnFocusGainedComboBox, gbc);
		controlPanel.add(selectTextOnFocusGainedComboBox);

		// Label: save main window location
		JLabel saveMainWindowLocationLabel = new FLabel(SAVE_MAIN_WINDOW_LOCATION_STR);

		gbc.gridx = 0;
		gbc.gridy = gridY;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = AppConstants.COMPONENT_INSETS;
		gridBag.setConstraints(saveMainWindowLocationLabel, gbc);
		controlPanel.add(saveMainWindowLocationLabel);

		// Combo box: save main window location
		saveMainWindowLocationComboBox = new BooleanComboBox(config.isMainWindowLocation());

		gbc.gridx = 1;
		gbc.gridy = gridY++;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = AppConstants.COMPONENT_INSETS;
		gridBag.setConstraints(saveMainWindowLocationComboBox, gbc);
		controlPanel.add(saveMainWindowLocationComboBox);

		// Label: hide control dialog
		JLabel hideControlDialogLabel = new FLabel(HIDE_CONTROL_DIALOG_STR);

		gbc.gridx = 0;
		gbc.gridy = gridY;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = AppConstants.COMPONENT_INSETS;
		gridBag.setConstraints(hideControlDialogLabel, gbc);
		controlPanel.add(hideControlDialogLabel);

		// Combo box: hide control dialog
		hideControlDialogComboBox = new BooleanComboBox(config.isHideControlDialogWhenSearching());

		gbc.gridx = 1;
		gbc.gridy = gridY++;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = AppConstants.COMPONENT_INSETS;
		gridBag.setConstraints(hideControlDialogComboBox, gbc);
		controlPanel.add(hideControlDialogComboBox);

		// Label: copy results as list file
		JLabel copyResultsAsListFileLabel = new FLabel(COPY_RESULTS_AS_LIST_FILE_STR);

		gbc.gridx = 0;
		gbc.gridy = gridY;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = AppConstants.COMPONENT_INSETS;
		gridBag.setConstraints(copyResultsAsListFileLabel, gbc);
		controlPanel.add(copyResultsAsListFileLabel);

		// Combo box: copy results as list file
		copyResultsAsListFileComboBox = new BooleanComboBox(config.isCopyResultsAsListFile());

		gbc.gridx = 1;
		gbc.gridy = gridY++;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = AppConstants.COMPONENT_INSETS;
		gridBag.setConstraints(copyResultsAsListFileComboBox, gbc);
		controlPanel.add(copyResultsAsListFileComboBox);


		//----  Outer panel

		JPanel outerPanel = new JPanel(gridBag);
		outerPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(0, 0, 0, 0);
		gridBag.setConstraints(controlPanel, gbc);
		outerPanel.add(controlPanel);

		return outerPanel;

	}

	//------------------------------------------------------------------

	private JPanel createPanelAppearance()
	{

		// Reset fixed-width labels
		AppearancePanelLabel.reset();


		//----  Upper panel

		GridBagLayout gridBag = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();

		JPanel upperPanel = new JPanel(gridBag);
		GuiUtils.setPaddedLineBorder(upperPanel);

		int gridY = 0;

		AppConfig config = AppConfig.INSTANCE;

		// Label: look-and-feel
		JLabel lookAndFeelLabel = new AppearancePanelLabel(LOOK_AND_FEEL_STR);

		gbc.gridx = 0;
		gbc.gridy = gridY;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = AppConstants.COMPONENT_INSETS;
		gridBag.setConstraints(lookAndFeelLabel, gbc);
		upperPanel.add(lookAndFeelLabel);

		// Combo box: look-and-feel
		lookAndFeelComboBox = new FComboBox<>();

		UIManager.LookAndFeelInfo[] lookAndFeelInfos = UIManager.getInstalledLookAndFeels();
		if (lookAndFeelInfos.length == 0)
		{
			lookAndFeelComboBox.addItem(NO_LOOK_AND_FEELS_STR);
			lookAndFeelComboBox.setSelectedIndex(0);
			lookAndFeelComboBox.setEnabled(false);
		}
		else
		{
			String[] lookAndFeelNames = new String[lookAndFeelInfos.length];
			for (int i = 0; i < lookAndFeelInfos.length; i++)
			{
				lookAndFeelNames[i] = lookAndFeelInfos[i].getName();
				lookAndFeelComboBox.addItem(lookAndFeelNames[i]);
			}
			lookAndFeelComboBox.setSelectedValue(config.getLookAndFeel());
		}

		gbc.gridx = 1;
		gbc.gridy = gridY++;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = AppConstants.COMPONENT_INSETS;
		gridBag.setConstraints(lookAndFeelComboBox, gbc);
		upperPanel.add(lookAndFeelComboBox);

		// Label: text antialiasing
		JLabel textAntialiasingLabel = new AppearancePanelLabel(TEXT_ANTIALIASING_STR);

		gbc.gridx = 0;
		gbc.gridy = gridY;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = AppConstants.COMPONENT_INSETS;
		gridBag.setConstraints(textAntialiasingLabel, gbc);
		upperPanel.add(textAntialiasingLabel);

		// Combo box: text antialiasing
		textAntialiasingComboBox = new FComboBox<>(TextRendering.Antialiasing.values());
		textAntialiasingComboBox.setSelectedValue(config.getTextAntialiasing());

		gbc.gridx = 1;
		gbc.gridy = gridY++;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = AppConstants.COMPONENT_INSETS;
		gridBag.setConstraints(textAntialiasingComboBox, gbc);
		upperPanel.add(textAntialiasingComboBox);

		// Label: parameter editor size
		JLabel paramEditorSizeLabel = new AppearancePanelLabel(PARAMETER_EDITOR_SIZE_STR);

		gbc.gridx = 0;
		gbc.gridy = gridY;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = AppConstants.COMPONENT_INSETS;
		gridBag.setConstraints(paramEditorSizeLabel, gbc);
		upperPanel.add(paramEditorSizeLabel);

		// Panel: parameter editor size
		paramEditorSizePanel = new DimensionsSpinnerPanel(config.getParameterEditorSize().width,
														  ParameterEditor.MIN_NUM_COLUMNS,
														  ParameterEditor.MAX_NUM_COLUMNS,
														  PARAMETER_EDITOR_WIDTH_FIELD_LENGTH,
														  config.getParameterEditorSize().height,
														  ParameterEditor.MIN_NUM_ROWS,
														  ParameterEditor.MAX_NUM_ROWS,
														  PARAMETER_EDITOR_HEIGHT_FIELD_LENGTH,
														  new String[]{ COLUMNS_STR, ROWS_STR });

		gbc.gridx = 1;
		gbc.gridy = gridY++;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = AppConstants.COMPONENT_INSETS;
		gridBag.setConstraints(paramEditorSizePanel, gbc);
		upperPanel.add(paramEditorSizePanel);

		// Label: number of rows in result area
		JLabel resultAreaNumRowsLabel = new AppearancePanelLabel(RESULT_AREA_NUM_ROWS_STR);

		gbc.gridx = 0;
		gbc.gridy = gridY;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = AppConstants.COMPONENT_INSETS;
		gridBag.setConstraints(resultAreaNumRowsLabel, gbc);
		upperPanel.add(resultAreaNumRowsLabel);

		// Spinner: number of rows in result area
		resultAreaNumRowsSpinner = new FIntegerSpinner(config.getResultAreaNumRows(),
													   AppConfig.MIN_RESULT_AREA_NUM_ROWS,
													   AppConfig.MAX_RESULT_AREA_NUM_ROWS,
													   RESULT_AREA_NUM_ROWS_FIELD_LENGTH);

		gbc.gridx = 1;
		gbc.gridy = gridY++;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = AppConstants.COMPONENT_INSETS;
		gridBag.setConstraints(resultAreaNumRowsSpinner, gbc);
		upperPanel.add(resultAreaNumRowsSpinner);

		// Label: tab surrogate
		JLabel tabSurrogateLabel = new AppearancePanelLabel(TAB_SURROGATE_STR);

		gbc.gridx = 0;
		gbc.gridy = gridY;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = AppConstants.COMPONENT_INSETS;
		gridBag.setConstraints(tabSurrogateLabel, gbc);
		upperPanel.add(tabSurrogateLabel);

		// Field: tab surrogate
		tabSurrogateField = new TabSurrogateField(config.getTabSurrogate());

		gbc.gridx = 1;
		gbc.gridy = gridY++;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = AppConstants.COMPONENT_INSETS;
		gridBag.setConstraints(tabSurrogateField, gbc);
		upperPanel.add(tabSurrogateField);


		//----  Text view panel

		JPanel textViewPanel = new JPanel(gridBag);
		TitledBorder.setPaddedBorder(textViewPanel, TEXT_VIEW_STR);

		gridY = 0;

		// Label: viewable size
		JLabel viewableSizeLabel = new AppearancePanelLabel(VIEWABLE_SIZE_STR);

		gbc.gridx = 0;
		gbc.gridy = gridY;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = AppConstants.COMPONENT_INSETS;
		gridBag.setConstraints(viewableSizeLabel, gbc);
		textViewPanel.add(viewableSizeLabel);

		// Panel: text-view size
		textViewSizePanel = new DimensionsSpinnerPanel(config.getTextViewViewableSize().width,
													   AppConfig.MIN_TEXT_VIEW_NUM_COLUMNS,
													   AppConfig.MAX_TEXT_VIEW_NUM_COLUMNS,
													   TEXT_VIEW_WIDTH_FIELD_LENGTH,
													   config.getTextViewViewableSize().height,
													   AppConfig.MIN_TEXT_VIEW_NUM_ROWS,
													   AppConfig.MAX_TEXT_VIEW_NUM_ROWS,
													   TEXT_VIEW_HEIGHT_FIELD_LENGTH,
													   new String[]{ COLUMNS_STR, ROWS_STR });

		gbc.gridx = 1;
		gbc.gridy = gridY++;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = AppConstants.COMPONENT_INSETS;
		gridBag.setConstraints(textViewSizePanel, gbc);
		textViewPanel.add(textViewSizePanel);

		// Label: maximum number of columns
		JLabel maxNumColumnsLabel = new AppearancePanelLabel(MAX_NUM_COLUMNS_STR);

		gbc.gridx = 0;
		gbc.gridy = gridY;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = AppConstants.COMPONENT_INSETS;
		gridBag.setConstraints(maxNumColumnsLabel, gbc);
		textViewPanel.add(maxNumColumnsLabel);

		// Spinner: maximum number of columns
		textViewMaxNumColumnsSpinner = new FIntegerSpinner(config.getTextViewMaxNumColumns(),
														   AppConfig.MIN_TEXT_VIEW_MAX_NUM_COLUMNS,
														   AppConfig.MAX_TEXT_VIEW_MAX_NUM_COLUMNS,
														   TEXT_VIEW_MAX_NUM_COLUMNS_FIELD_LENGTH);

		gbc.gridx = 1;
		gbc.gridy = gridY++;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = AppConstants.COMPONENT_INSETS;
		gridBag.setConstraints(textViewMaxNumColumnsSpinner, gbc);
		textViewPanel.add(textViewMaxNumColumnsSpinner);

		// Label: text antialiasing
		JLabel textViewTextAntialiasingLabel = new AppearancePanelLabel(TEXT_ANTIALIASING_STR);

		gbc.gridx = 0;
		gbc.gridy = gridY;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = AppConstants.COMPONENT_INSETS;
		gridBag.setConstraints(textViewTextAntialiasingLabel, gbc);
		textViewPanel.add(textViewTextAntialiasingLabel);

		// Combo box: text antialiasing
		textViewTextAntialiasingComboBox = new FComboBox<>(TextRendering.Antialiasing.values());
		textViewTextAntialiasingComboBox.setSelectedValue(config.getTextViewTextAntialiasing());

		gbc.gridx = 1;
		gbc.gridy = gridY++;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = AppConstants.COMPONENT_INSETS;
		gridBag.setConstraints(textViewTextAntialiasingComboBox, gbc);
		textViewPanel.add(textViewTextAntialiasingComboBox);

		// Update widths of labels
		AppearancePanelLabel.update();


		//----  Text area colours panel

		JPanel textAreaColoursPanel = new JPanel(gridBag);
		TitledBorder.setPaddedBorder(textAreaColoursPanel, TEXT_AREA_COLOURS_STR);

		// Text area colours panel A
		JPanel textAreaColoursPanelA = new JPanel(gridBag);

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(0, 0, 0, 0);
		gridBag.setConstraints(textAreaColoursPanelA, gbc);
		textAreaColoursPanel.add(textAreaColoursPanelA);

		// Label: text colour
		JLabel textColourLabel = new FLabel(TEXT_STR);

		gbc.gridx = 0;
		gbc.gridy = gridY;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = AppConstants.COMPONENT_INSETS;
		gridBag.setConstraints(textColourLabel, gbc);
		textAreaColoursPanelA.add(textColourLabel);

		// Button: text colour
		textColourButton = new ColourButton(config.getTextAreaTextColour());
		textColourButton.setActionCommand(Command.CHOOSE_TEXT_COLOUR);
		textColourButton.addActionListener(this);

		gbc.gridx = 1;
		gbc.gridy = gridY++;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = AppConstants.COMPONENT_INSETS;
		gridBag.setConstraints(textColourButton, gbc);
		textAreaColoursPanelA.add(textColourButton);

		// Label: background colour
		JLabel backgroundColourLabel = new FLabel(BACKGROUND_STR);

		gbc.gridx = 0;
		gbc.gridy = gridY;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = AppConstants.COMPONENT_INSETS;
		gridBag.setConstraints(backgroundColourLabel, gbc);
		textAreaColoursPanelA.add(backgroundColourLabel);

		// Button: background colour
		backgroundColourButton = new ColourButton(config.getTextAreaBackgroundColour());
		backgroundColourButton.setActionCommand(Command.CHOOSE_BACKGROUND_COLOUR);
		backgroundColourButton.addActionListener(this);

		gbc.gridx = 1;
		gbc.gridy = gridY++;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = AppConstants.COMPONENT_INSETS;
		gridBag.setConstraints(backgroundColourButton, gbc);
		textAreaColoursPanelA.add(backgroundColourButton);

		// Text area colours panel B
		JPanel textAreaColoursPanelB = new JPanel(gridBag);

		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(0, 8, 0, 0);
		gridBag.setConstraints(textAreaColoursPanelB, gbc);
		textAreaColoursPanel.add(textAreaColoursPanelB);

		// Label: highlighted text colour
		JLabel highlightTextColourLabel = new FLabel(HIGHLIGHT_TEXT_STR);

		gbc.gridx = 0;
		gbc.gridy = gridY;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = AppConstants.COMPONENT_INSETS;
		gridBag.setConstraints(highlightTextColourLabel, gbc);
		textAreaColoursPanelB.add(highlightTextColourLabel);

		// Button: highlighted text colour
		highlightTextColourButton = new ColourButton(config.getTextAreaHighlightTextColour());
		highlightTextColourButton.setActionCommand(Command.CHOOSE_HIGHLIGHT_TEXT_COLOUR);
		highlightTextColourButton.addActionListener(this);

		gbc.gridx = 1;
		gbc.gridy = gridY++;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = AppConstants.COMPONENT_INSETS;
		gridBag.setConstraints(highlightTextColourButton, gbc);
		textAreaColoursPanelB.add(highlightTextColourButton);

		// Label: highlighted background colour
		JLabel highlightBackgroundColourLabel = new FLabel(HIGHLIGHT_BACKGROUND_STR);

		gbc.gridx = 0;
		gbc.gridy = gridY;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = AppConstants.COMPONENT_INSETS;
		gridBag.setConstraints(highlightBackgroundColourLabel, gbc);
		textAreaColoursPanelB.add(highlightBackgroundColourLabel);

		// Button: highlighted background colour
		highlightBackgroundColourButton =
										new ColourButton(config.getTextAreaHighlightBackgroundColour());
		highlightBackgroundColourButton.setActionCommand(Command.CHOOSE_HIGHLIGHT_BACKGROUND_COLOUR);
		highlightBackgroundColourButton.addActionListener(this);

		gbc.gridx = 1;
		gbc.gridy = gridY++;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = AppConstants.COMPONENT_INSETS;
		gridBag.setConstraints(highlightBackgroundColourButton, gbc);
		textAreaColoursPanelB.add(highlightBackgroundColourButton);


		//----  Outer panel

		JPanel outerPanel = new JPanel(gridBag);
		outerPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

		gridY = 0;

		gbc.gridx = 0;
		gbc.gridy = gridY++;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(0, 0, 0, 0);
		gridBag.setConstraints(upperPanel, gbc);
		outerPanel.add(upperPanel);

		gbc.gridx = 0;
		gbc.gridy = gridY++;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(3, 0, 0, 0);
		gridBag.setConstraints(textViewPanel, gbc);
		outerPanel.add(textViewPanel);

		gbc.gridx = 0;
		gbc.gridy = gridY++;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(3, 0, 0, 0);
		gridBag.setConstraints(textAreaColoursPanel, gbc);
		outerPanel.add(textAreaColoursPanel);

		return outerPanel;

	}

	//------------------------------------------------------------------

	private JPanel createPanelTabWidth()
	{

		// Reset fixed-width labels
		TabWidthPanelLabel.reset();


		//----  Tab-width filter list

		// Selection list
		AppConfig config = AppConfig.INSTANCE;

		tabWidthFilterList = new TabWidthList(config.getTabWidthFilters());
		tabWidthFilterList.addActionListener(this);
		tabWidthFilterList.addListSelectionListener(this);

		// Scroll pane: selection list
		tabWidthFilterListScrollPane = new JScrollPane(tabWidthFilterList,
													   JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
													   JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		tabWidthFilterListScrollPane.getVerticalScrollBar().setFocusable(false);
		tabWidthFilterListScrollPane.getVerticalScrollBar().getModel().addChangeListener(this);

		tabWidthFilterList.setViewport(tabWidthFilterListScrollPane.getViewport());


		//----  Button panel

		JPanel buttonPanel = new JPanel(new GridLayout(0, 1, 0, 8));

		// Button: add
		tabWidthAddButton = new FButton(ADD_STR + AppConstants.ELLIPSIS_STR);
		tabWidthAddButton.setMnemonic(KeyEvent.VK_A);
		tabWidthAddButton.setActionCommand(Command.ADD_TAB_WIDTH_FILTER);
		tabWidthAddButton.addActionListener(this);
		buttonPanel.add(tabWidthAddButton);

		// Button: edit
		tabWidthEditButton = new FButton(EDIT_STR + AppConstants.ELLIPSIS_STR);
		tabWidthEditButton.setMnemonic(KeyEvent.VK_E);
		tabWidthEditButton.setActionCommand(Command.EDIT_TAB_WIDTH_FILTER);
		tabWidthEditButton.addActionListener(this);
		buttonPanel.add(tabWidthEditButton);

		// Button: delete
		tabWidthDeleteButton = new FButton(DELETE_STR + AppConstants.ELLIPSIS_STR);
		tabWidthDeleteButton.setMnemonic(KeyEvent.VK_D);
		tabWidthDeleteButton.setActionCommand(Command.CONFIRM_DELETE_TAB_WIDTH_FILTER);
		tabWidthDeleteButton.addActionListener(this);
		buttonPanel.add(tabWidthDeleteButton);

		// Update buttons
		updateTabWidthFilterButtons();


		//----  Tab-width filter panel

		GridBagLayout gridBag = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();

		JPanel tabWidthFilterPanel = new JPanel(gridBag);

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(0, 0, 0, 0);
		gridBag.setConstraints(tabWidthFilterListScrollPane, gbc);
		tabWidthFilterPanel.add(tabWidthFilterListScrollPane);

		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(0, 8, 0, 0);
		gridBag.setConstraints(buttonPanel, gbc);
		tabWidthFilterPanel.add(buttonPanel);


		//----  Default tab width panel

		JPanel defaultTabWidthPanel = new JPanel(gridBag);

		// Label: default tab width
		JLabel defaultTabWidthLabel = new TabWidthPanelLabel(DEFAULT_TAB_WIDTH_STR);

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = AppConstants.COMPONENT_INSETS;
		gridBag.setConstraints(defaultTabWidthLabel, gbc);
		defaultTabWidthPanel.add(defaultTabWidthLabel);

		// Spinner: default tab width
		defaultTabWidthSpinner = new FIntegerSpinner(config.getDefaultTabWidth(), TextModel.MIN_TAB_WIDTH,
													 TextModel.MAX_TAB_WIDTH,
													 DEFAULT_TAB_WIDTH_FIELD_LENGTH);

		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = AppConstants.COMPONENT_INSETS;
		gridBag.setConstraints(defaultTabWidthSpinner, gbc);
		defaultTabWidthPanel.add(defaultTabWidthSpinner);


		//----  Text view panel

		JPanel textViewPanel = new JPanel(gridBag);
		TitledBorder.setPaddedBorder(textViewPanel, TEXT_VIEW_STR);

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(0, 0, 0, 0);
		gridBag.setConstraints(tabWidthFilterPanel, gbc);
		textViewPanel.add(tabWidthFilterPanel);

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(4, 0, 0, 0);
		gridBag.setConstraints(defaultTabWidthPanel, gbc);
		textViewPanel.add(defaultTabWidthPanel);


		//----  Target and replacement areas panel

		JPanel targetAndReplacementPanel = new JPanel(gridBag);
		TitledBorder.setPaddedBorder(targetAndReplacementPanel, TARGET_AND_REPLACEMENT_STR);

		// Label: target and replacement tab width
		JLabel tabWidthLabel = new TabWidthPanelLabel(TAB_WIDTH1_STR);

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = AppConstants.COMPONENT_INSETS;
		gridBag.setConstraints(tabWidthLabel, gbc);
		targetAndReplacementPanel.add(tabWidthLabel);

		// Spinner: default tab width
		targetAndReplacementTabWidthSpinner =
									new FIntegerSpinner(config.getTargetAndReplacementTabWidth(),
														TextModel.MIN_TAB_WIDTH, TextModel.MAX_TAB_WIDTH,
														TARGET_AND_REPLACEMENT_TAB_WIDTH_FIELD_LENGTH);

		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = AppConstants.COMPONENT_INSETS;
		gridBag.setConstraints(targetAndReplacementTabWidthSpinner, gbc);
		targetAndReplacementPanel.add(targetAndReplacementTabWidthSpinner);

		// Update widths of labels
		TabWidthPanelLabel.update();


		//----  Outer panel

		JPanel outerPanel = new JPanel(gridBag);
		outerPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

		int gridY = 0;

		gbc.gridx = 0;
		gbc.gridy = gridY++;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(0, 0, 0, 0);
		gridBag.setConstraints(textViewPanel, gbc);
		outerPanel.add(textViewPanel);

		gbc.gridx = 0;
		gbc.gridy = gridY++;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(3, 0, 0, 0);
		gridBag.setConstraints(targetAndReplacementPanel, gbc);
		outerPanel.add(targetAndReplacementPanel);

		return outerPanel;

	}

	//------------------------------------------------------------------

	private JPanel createPanelEditor()
	{

		//----  Control panel

		GridBagLayout gridBag = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();

		JPanel controlPanel = new JPanel(gridBag);
		GuiUtils.setPaddedLineBorder(controlPanel);

		int gridY = 0;

		AppConfig config = AppConfig.INSTANCE;

		// Label: command
		JLabel commandLabel = new FLabel(COMMAND_STR);

		gbc.gridx = 0;
		gbc.gridy = gridY;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = AppConstants.COMPONENT_INSETS;
		gridBag.setConstraints(commandLabel, gbc);
		controlPanel.add(commandLabel);

		// Field: command
		editorCommandField = new FTextField(config.getEditorCommand(), EDITOR_COMMAND_FIELD_NUM_COLUMNS);

		gbc.gridx = 1;
		gbc.gridy = gridY++;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = AppConstants.COMPONENT_INSETS;
		gridBag.setConstraints(editorCommandField, gbc);
		controlPanel.add(editorCommandField);


		//----  Outer panel

		JPanel outerPanel = new JPanel(gridBag);
		outerPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(0, 0, 0, 0);
		gridBag.setConstraints(controlPanel, gbc);
		outerPanel.add(controlPanel);

		return outerPanel;

	}

	//------------------------------------------------------------------

	private JPanel createPanelFileLocations()
	{

		//----  Control panel

		GridBagLayout gridBag = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();

		JPanel controlPanel = new JPanel(gridBag);
		GuiUtils.setPaddedLineBorder(controlPanel);

		int gridY = 0;

		AppConfig config = AppConfig.INSTANCE;

		// Label: default search parameters
		JLabel defaultSearchParamsLabel = new FLabel(DEFAULT_SEARCH_PARAMS_STR);

		gbc.gridx = 0;
		gbc.gridy = gridY;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = AppConstants.COMPONENT_INSETS;
		gridBag.setConstraints(defaultSearchParamsLabel, gbc);
		controlPanel.add(defaultSearchParamsLabel);

		// Panel: default search parameters
		defaultSearchParamsField = new FPathnameField(config.getDefaultSearchParamsFile());
		FPathnameField.addObserver(KEY, defaultSearchParamsField);
		JPanel defaultSearchParamsPanel = new PathnamePanel(defaultSearchParamsField,
															Command.CHOOSE_DEFAULT_SEARCH_PARAMS_FILE,
															this);

		gbc.gridx = 1;
		gbc.gridy = gridY++;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = AppConstants.COMPONENT_INSETS;
		gridBag.setConstraints(defaultSearchParamsPanel, gbc);
		controlPanel.add(defaultSearchParamsPanel);


		//----  Outer panel

		JPanel outerPanel = new JPanel(gridBag);
		outerPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(0, 0, 0, 0);
		gridBag.setConstraints(controlPanel, gbc);
		outerPanel.add(controlPanel);

		return outerPanel;

	}

	//------------------------------------------------------------------

	private JPanel createPanelFonts()
	{

		//----  Control panel

		GridBagLayout gridBag = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();

		JPanel controlPanel = new JPanel(gridBag);
		GuiUtils.setPaddedLineBorder(controlPanel);

		String[] fontNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		fontPanels = new FontPanel[AppFont.getNumFonts()];
		for (int i = 0; i < fontPanels.length; i++)
		{
			FontEx fontEx = AppConfig.INSTANCE.getFont(i);
			fontPanels[i] = new FontPanel(fontEx, fontNames);

			int gridX = 0;

			// Label: font
			JLabel fontLabel = new FLabel(AppFont.values()[i].toString());

			gbc.gridx = gridX++;
			gbc.gridy = i;
			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			gbc.weightx = 0.0;
			gbc.weighty = 0.0;
			gbc.anchor = GridBagConstraints.LINE_END;
			gbc.fill = GridBagConstraints.NONE;
			gbc.insets = AppConstants.COMPONENT_INSETS;
			gridBag.setConstraints(fontLabel, gbc);
			controlPanel.add(fontLabel);

			// Combo box: font name
			gbc.gridx = gridX++;
			gbc.gridy = i;
			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			gbc.weightx = 0.0;
			gbc.weighty = 0.0;
			gbc.anchor = GridBagConstraints.LINE_START;
			gbc.fill = GridBagConstraints.NONE;
			gbc.insets = AppConstants.COMPONENT_INSETS;
			gridBag.setConstraints(fontPanels[i].nameComboBox, gbc);
			controlPanel.add(fontPanels[i].nameComboBox);

			// Combo box: font style
			gbc.gridx = gridX++;
			gbc.gridy = i;
			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			gbc.weightx = 0.0;
			gbc.weighty = 0.0;
			gbc.anchor = GridBagConstraints.LINE_START;
			gbc.fill = GridBagConstraints.NONE;
			gbc.insets = AppConstants.COMPONENT_INSETS;
			gridBag.setConstraints(fontPanels[i].styleComboBox, gbc);
			controlPanel.add(fontPanels[i].styleComboBox);

			// Panel: font size
			JPanel sizePanel = new JPanel(gridBag);

			gbc.gridx = gridX++;
			gbc.gridy = i;
			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			gbc.weightx = 0.0;
			gbc.weighty = 0.0;
			gbc.anchor = GridBagConstraints.LINE_START;
			gbc.fill = GridBagConstraints.NONE;
			gbc.insets = AppConstants.COMPONENT_INSETS;
			gridBag.setConstraints(sizePanel, gbc);
			controlPanel.add(sizePanel);

			// Spinner: font size
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			gbc.weightx = 0.0;
			gbc.weighty = 0.0;
			gbc.anchor = GridBagConstraints.LINE_START;
			gbc.fill = GridBagConstraints.NONE;
			gbc.insets = new Insets(0, 0, 0, 0);
			gridBag.setConstraints(fontPanels[i].sizeSpinner, gbc);
			sizePanel.add(fontPanels[i].sizeSpinner);

			// Label: "pt"
			JLabel ptLabel = new FLabel(PT_STR);

			gbc.gridx = 1;
			gbc.gridy = 0;
			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			gbc.weightx = 0.0;
			gbc.weighty = 0.0;
			gbc.anchor = GridBagConstraints.LINE_START;
			gbc.fill = GridBagConstraints.NONE;
			gbc.insets = new Insets(0, 4, 0, 0);
			gridBag.setConstraints(ptLabel, gbc);
			sizePanel.add(ptLabel);
		}


		//----  Outer panel

		JPanel outerPanel = new JPanel(gridBag);
		outerPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(0, 0, 0, 0);
		gridBag.setConstraints(controlPanel, gbc);
		outerPanel.add(controlPanel);

		return outerPanel;

	}

	//------------------------------------------------------------------

	private void setFocus(Tab        tab,
						  JComponent component)
	{
		tabbedPanel.setSelectedIndex(tab.ordinal());
		GuiUtils.setFocus(component);
	}

	//------------------------------------------------------------------

	private void validatePreferencesGeneral()
	{
		// do nothing
	}

	//------------------------------------------------------------------

	private void validatePreferencesAppearance()
		throws AppException
	{
		// Tab surrogate
		try
		{
			if (tabSurrogateField.getChar() == '\0')
				throw new AppException(ErrorId.INVALID_TAB_SURROGATE);
		}
		catch (AppException e)
		{
			setFocus(Tab.APPEARANCE, tabSurrogateField);
			throw e;
		}
	}

	//------------------------------------------------------------------

	private void validatePreferencesTabWidth()
	{
		// do nothing
	}

	//------------------------------------------------------------------

	private void validatePreferencesEditor()
	{
		// do nothing
	}

	//------------------------------------------------------------------

	private void validatePreferencesFileLocations()
		throws AppException
	{
		// Default search parameters file
		try
		{
			if (!defaultSearchParamsField.isEmpty())
			{
				File file = defaultSearchParamsField.getFile();
				try
				{
					if (!file.exists())
						throw new FileException(ErrorId.FILE_DOES_NOT_EXIST, file);
					if (!file.isFile())
						throw new FileException(ErrorId.NOT_A_FILE, file);
				}
				catch (SecurityException e)
				{
					throw new FileException(ErrorId.FILE_ACCESS_NOT_PERMITTED, file, e);
				}
			}
		}
		catch (AppException e)
		{
			setFocus(Tab.FILE_LOCATIONS, defaultSearchParamsField);
			throw e;
		}
	}

	//------------------------------------------------------------------

	private void validatePreferencesFonts()
	{
		// do nothing
	}

	//------------------------------------------------------------------

	private void setPreferencesGeneral()
	{
		AppConfig config = AppConfig.INSTANCE;
		config.setCharacterEncoding((characterEncodingComboBox.getSelectedIndex() <= 0)
														? null
														: characterEncodingComboBox.getSelectedValue());
		config.setEscapedMetacharacters(escapedMetacharsField.getText());
		config.setReplacementEscapeChar(replacementEscapeCharComboBox.getSelectedValue());
		config.setIgnoreFilenameCase(ignoreFilenameCaseComboBox.getSelectedValue());
		config.setFileWritingMode(fileWritingModeComboBox.getSelectedValue());
		config.setPreserveLineSeparator(preserveLineSeparatorComboBox.getSelectedValue());
		config.setShowUnixPathnames(showUnixPathnamesComboBox.getSelectedValue());
		config.setSelectTextOnFocusGained(selectTextOnFocusGainedComboBox.getSelectedValue());
		if (saveMainWindowLocationComboBox.getSelectedValue() != config.isMainWindowLocation())
			config.setMainWindowLocation(saveMainWindowLocationComboBox.getSelectedValue() ? new Point()
																						   : null);
		config.setHideControlDialogWhenSearching(hideControlDialogComboBox.getSelectedValue());
		config.setCopyResultsAsListFile(copyResultsAsListFileComboBox.getSelectedValue());
	}

	//------------------------------------------------------------------

	private void setPreferencesAppearance()
	{
		AppConfig config = AppConfig.INSTANCE;
		if (lookAndFeelComboBox.isEnabled() && (lookAndFeelComboBox.getSelectedIndex() >= 0))
			config.setLookAndFeel(lookAndFeelComboBox.getSelectedValue());
		config.setTextAntialiasing(textAntialiasingComboBox.getSelectedValue());
		config.setParameterEditorSize(paramEditorSizePanel.getDimensions());
		config.setResultAreaNumRows(resultAreaNumRowsSpinner.getIntValue());
		config.setTabSurrogate(tabSurrogateField.getChar());
		config.setTextViewViewableSize(textViewSizePanel.getDimensions());
		config.setTextViewMaxNumColumns(textViewMaxNumColumnsSpinner.getIntValue());
		config.setTextViewTextAntialiasing(textViewTextAntialiasingComboBox.getSelectedValue());
		config.setTextAreaTextColour(textColourButton.getForeground());
		config.setTextAreaBackgroundColour(backgroundColourButton.getForeground());
		config.setTextAreaHighlightTextColour(highlightTextColourButton.getForeground());
		config.setTextAreaHighlightBackgroundColour(highlightBackgroundColourButton.getForeground());
	}

	//------------------------------------------------------------------

	private void setPreferencesTabWidth()
	{
		AppConfig config = AppConfig.INSTANCE;
		config.setDefaultTabWidth(defaultTabWidthSpinner.getIntValue());
		config.setTabWidthFilters(tabWidthFilterList.getElements());
		config.setTargetAndReplacementTabWidth(targetAndReplacementTabWidthSpinner.getIntValue());
	}

	//------------------------------------------------------------------

	private void setPreferencesEditor()
	{
		AppConfig config = AppConfig.INSTANCE;
		config.setEditorCommand(editorCommandField.isEmpty() ? null : editorCommandField.getText());
	}

	//------------------------------------------------------------------

	private void setPreferencesFileLocations()
	{
		AppConfig config = AppConfig.INSTANCE;
		config.setDefaultSearchParamsPathname(defaultSearchParamsField.isEmpty()
																	? null
																	: defaultSearchParamsField.getText());
	}

	//------------------------------------------------------------------

	private void setPreferencesFonts()
	{
		for (int i = 0; i < fontPanels.length; i++)
		{
			if (fontPanels[i].nameComboBox.getSelectedIndex() >= 0)
				AppConfig.INSTANCE.setFont(i, fontPanels[i].getFont());
		}
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class fields
////////////////////////////////////////////////////////////////////////

	private static	Point	location;
	private static	int		tabIndex;

////////////////////////////////////////////////////////////////////////
//  Static initialiser
////////////////////////////////////////////////////////////////////////

	static
	{
		COMMAND_MAP = new HashMap<>();
		COMMAND_MAP.put(SingleSelectionList.Command.EDIT_ELEMENT,
						Command.EDIT_TAB_WIDTH_FILTER);
		COMMAND_MAP.put(SingleSelectionList.Command.DELETE_ELEMENT,
						Command.CONFIRM_DELETE_TAB_WIDTH_FILTER);
		COMMAND_MAP.put(SingleSelectionList.Command.DELETE_EX_ELEMENT,
						Command.DELETE_TAB_WIDTH_FILTER);
		COMMAND_MAP.put(SingleSelectionList.Command.MOVE_ELEMENT_UP,
						Command.MOVE_TAB_WIDTH_FILTER_UP);
		COMMAND_MAP.put(SingleSelectionList.Command.MOVE_ELEMENT_DOWN,
						Command.MOVE_TAB_WIDTH_FILTER_DOWN);
		COMMAND_MAP.put(SingleSelectionList.Command.DRAG_ELEMENT,
						Command.MOVE_TAB_WIDTH_FILTER);
	}

////////////////////////////////////////////////////////////////////////
//  Instance fields
////////////////////////////////////////////////////////////////////////

	// Main panel
	private	boolean									accepted;
	private	JTabbedPane								tabbedPanel;

	// General panel
	private	FComboBox<String>						characterEncodingComboBox;
	private	EscapedMetacharsField					escapedMetacharsField;
	private	FComboBox<Character>					replacementEscapeCharComboBox;
	private	BooleanComboBox							ignoreFilenameCaseComboBox;
	private	FComboBox<FileWritingMode>				fileWritingModeComboBox;
	private	BooleanComboBox							preserveLineSeparatorComboBox;
	private	BooleanComboBox							showUnixPathnamesComboBox;
	private	BooleanComboBox							selectTextOnFocusGainedComboBox;
	private	BooleanComboBox							saveMainWindowLocationComboBox;
	private	BooleanComboBox							hideControlDialogComboBox;
	private	BooleanComboBox							copyResultsAsListFileComboBox;

	// Appearance panel
	private	FComboBox<String>						lookAndFeelComboBox;
	private	FComboBox<TextRendering.Antialiasing>	textAntialiasingComboBox;
	private	DimensionsSpinnerPanel					paramEditorSizePanel;
	private	FIntegerSpinner							resultAreaNumRowsSpinner;
	private	TabSurrogateField						tabSurrogateField;
	private	DimensionsSpinnerPanel					textViewSizePanel;
	private	FIntegerSpinner							textViewMaxNumColumnsSpinner;
	private	FComboBox<TextRendering.Antialiasing>	textViewTextAntialiasingComboBox;
	private	JButton									textColourButton;
	private	JButton									backgroundColourButton;
	private	JButton									highlightTextColourButton;
	private	JButton									highlightBackgroundColourButton;

	// Tab width panel
	private	TabWidthList							tabWidthFilterList;
	private	JScrollPane								tabWidthFilterListScrollPane;
	private	FIntegerSpinner							defaultTabWidthSpinner;
	private	FIntegerSpinner							targetAndReplacementTabWidthSpinner;
	private	JButton									tabWidthAddButton;
	private	JButton									tabWidthEditButton;
	private	JButton									tabWidthDeleteButton;

	// Editor panel
	private	FTextField								editorCommandField;

	// File locations panel
	private	FPathnameField							defaultSearchParamsField;
	private	JFileChooser							defaultSearchParamsFileChooser;

	// Fonts panel
	private	FontPanel[]								fontPanels;

}

//----------------------------------------------------------------------
