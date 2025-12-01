/*====================================================================*\

MainWindow.java

Class: main window.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.regexsearch;

//----------------------------------------------------------------------


// IMPORTS


import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.File;

import java.nio.file.Path;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import uk.blankaspect.common.exception.AppException;
import uk.blankaspect.common.exception.ExceptionUtils;

import uk.blankaspect.common.filesystem.PathnameUtils;

import uk.blankaspect.common.misc.PathnameFilter;

import uk.blankaspect.common.string.StringUtils;

import uk.blankaspect.common.thread.DaemonFactory;

import uk.blankaspect.ui.swing.filechooser.FileChooserUtils;

import uk.blankaspect.ui.swing.menu.FMenu;
import uk.blankaspect.ui.swing.menu.FMenuItem;

import uk.blankaspect.ui.swing.misc.GuiUtils;

import uk.blankaspect.ui.swing.text.TextRendering;

import uk.blankaspect.ui.swing.textarea.TextArea;

import uk.blankaspect.ui.swing.workaround.LinuxWorkarounds;

//----------------------------------------------------------------------


// CLASS: MAIN WINDOW


class MainWindow
	extends JFrame
	implements ActionListener, MenuListener, MouseListener
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	public static final		int		HIGHLIGHT_MARGIN_ROWS		= 4;
	public static final		int		HIGHLIGHT_MARGIN_COLUMNS	= 8;

	private static final	String	EDITOR_THREAD_NAME_PREFIX	= "editor-";

	private static final	String	SEARCHING_STR				= "Searching ";
	private static final	String	OPEN_SEARCH_PARAMS_STR		= "Open search parameters";
	private static final	String	SAVE_SEARCH_PARAMS_STR		= "Save search parameters";
	private static final	String	WRITE_SEARCH_PARAMS_STR		= "Write search parameters";
	private static final	String	SAVE_STR					= "Save";
	private static final	String	DISCARD_STR					= "Discard";
	private static final	String	SAVE_MESSAGE_STR			=
			"The search parameters have changed.\nDo you want to save the current search parameters?";
	private static final	String	TARGET_NOT_FOUND_STR		= "Files in which the target was not found";
	private static final	String	UNPROCESSED_STR				= "Unprocessed files or directories";
	private static final	String	NO_CANONICAL_PATHNAME_STR	= "Failed to get canonical pathname";
	private static final	String	PROCESSING_ERROR_STR		= "Error processing file or directory";
	private static final	String	ATTRIBUTES_NOT_SET_STR		= "File attributes not set";

	private static final	char	TAB_GLYPH_CHAR	= '\u2192';

	private interface Command
	{
		String	SHOW_CONTEXT_MENU	= "showContextMenu";
	}

////////////////////////////////////////////////////////////////////////
//  Class variables
////////////////////////////////////////////////////////////////////////

	private static	int	editorThreadIndex;

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	TextModel			textModel;
	private	String				currentPathname;
	private	SearchDialog.Kind	searchKind;
	private	boolean				searching;
	private	boolean				controlDialogHidden;
	private	File				deferredFile;
	private	ControlDialog		controlDialog;
	private	SearchDialog		searchDialog;
	private	JPopupMenu			contextMenu;
	private	TextArea			textView;
	private	TextArea			resultArea;
	private	JFileChooser		openFileChooser;
	private	JFileChooser		saveFileChooser;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public MainWindow()
	{
		// Set icons
		setIconImages(Images.APP_ICON_IMAGES);


		//----  Menu bar

		JMenuBar menuBar = new JMenuBar();
		menuBar.setBorder(null);

		// File menu
		JMenu menu = Menu.FILE.menu;
		menu.addMenuListener(this);

		menu.add(new FMenuItem(AppCommand.OPEN_SEARCH_PARAMETERS, KeyEvent.VK_O));
		menu.add(new FMenuItem(AppCommand.SAVE_SEARCH_PARAMETERS, KeyEvent.VK_S));

		menu.addSeparator();

		menu.add(new FMenuItem(AppCommand.EXIT, KeyEvent.VK_X));

		menuBar.add(menu);

		// Edit menu
		menu = Menu.EDIT.menu;
		menu.addMenuListener(this);

		menu.add(new FMenuItem(AppCommand.EDIT_FILE, KeyEvent.VK_E));
		menu.add(new FMenuItem(AppCommand.EDIT_FILE_DEFERRED, KeyEvent.VK_D));

		menuBar.add(menu);

		// Search menu
		menu = Menu.SEARCH.menu;
		menu.addMenuListener(this);

		menu.add(new FMenuItem(AppCommand.SEARCH, KeyEvent.VK_S));

		menu.addSeparator();

		menu.add(new FMenuItem(AppCommand.COPY_RESULTS, KeyEvent.VK_C));
		menu.add(new FMenuItem(AppCommand.SAVE_RESULTS, KeyEvent.VK_A));
		menu.add(new FMenuItem(AppCommand.VIEW_SAVED_RESULTS, KeyEvent.VK_V));

		menuBar.add(menu);

		// View menu
		menu = Menu.VIEW.menu;
		menu.addMenuListener(this);

		menu.add(new FMenuItem(AppCommand.TOGGLE_CONTROL_DIALOG, KeyEvent.VK_C));

		menuBar.add(menu);

		// Options menu
		menu = Menu.OPTIONS.menu;
		menu.addMenuListener(this);

		menu.add(new FMenuItem(AppCommand.EDIT_PREFERENCES, KeyEvent.VK_P));

		menuBar.add(menu);

		// Set menu bar
		setJMenuBar(menuBar);


		//----  Text view scroll pane

		// Text area: text view
		AppConfig config = AppConfig.INSTANCE;

		textView = new TextArea(config.getTextViewViewableSize().width, config.getTextViewViewableSize().height,
								config.getTextViewMaxNumColumns(), AppFont.TEXT_VIEW.getFont());
		textView.setBlockIncrementRows(textView.getRows() - 2);
		setTextAreaColours(textView);
		textView.setDoubleBuffered(false);
		textView.setAntialiasing(config.getTextViewTextAntialiasing());

		textView.addMouseListener(this);

		// Scroll pane: text view
		ScrollPane textViewScrollPane = new ScrollPane(textView);


		//----  Result area

		// Text area: results
		resultArea = new ResultArea(config.getTextViewViewableSize().width, config.getResultAreaNumRows());
		resultArea.setModel(new ResultList());
		resultArea.setBlockIncrementRows(resultArea.getRows() - 1);
		setTextAreaColours(resultArea);
		resultArea.setDoubleBuffered(false);
		resultArea.setAntialiasing(TextRendering.getAntialiasing());

		resultArea.addMouseListener(this);
		getResultList().addChangeListener(resultArea);

		// Scroll pane: results
		ScrollPane resultAreaScrollPane = new ScrollPane(resultArea);


		//----  Main panel

		GridBagLayout gridBag = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();

		JPanel mainPanel = new JPanel(gridBag);

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
		gridBag.setConstraints(textViewScrollPane, gbc);
		mainPanel.add(textViewScrollPane);

		gbc.gridx = 0;
		gbc.gridy = gridY++;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(0, 0, 0, 0);
		gridBag.setConstraints(resultAreaScrollPane, gbc);
		mainPanel.add(resultAreaScrollPane);

		// Set transfer handler on main panel
		mainPanel.setTransferHandler(FileTransferHandler.INSTANCE);

		// Add listener
		mainPanel.addMouseListener(this);


		//----  Window

		// Set content pane
		setContentPane(mainPanel);

		// Update title
		updateTitle();

		// Dispose of window explicitly
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		// Handle window closing
		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(
				WindowEvent	event)
			{
				AppCommand.EXIT.execute();
			}
		});

		// Prevent window from being resized
		setResizable(false);

		// Resize window to its preferred size
		pack();

		// Set location of window
		Point location = config.getMainWindowLocation();
		location = (location == null)
							? GuiUtils.getComponentLocation(this)
							: GuiUtils.getLocationWithinScreen(this, location);
		setLocation(location);

		// Make window visible
		setVisible(true);

		// WORKAROUND for a bug that has been observed on Linux/GNOME whereby a window is displaced downwards when its
		// location is set.  The error in the y coordinate is the height of the title bar of the window.  The workaround
		// is to set the location of the window again with an adjustment for the error.
		LinuxWorkarounds.fixWindowYCoord(this, location);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	private static SearchParameters getSearchParams()
	{
		return RegexSearchApp.INSTANCE.getSearchParams();
	}

	//------------------------------------------------------------------

	private static void setTextAreaColours(
		TextArea	textArea)
	{
		AppConfig config = AppConfig.INSTANCE;
		textArea.setForeground(config.getTextAreaTextColour());
		textArea.setBackground(config.getTextAreaBackgroundColour());
		textArea.setHighlightTextColour(config.getTextAreaHighlightTextColour());
		textArea.setHighlightBackgroundColour(config.getTextAreaHighlightBackgroundColour());
	}

	//------------------------------------------------------------------

	private static void editFile(
		String	pathname)
		throws AppException
	{
		final	char	ESCAPE_CHAR					= '%';
		final	char	PATHNAME_PLACEHOLDER_CHAR	= 'f';
		final	char	URI_PLACEHOLDER_CHAR		= 'u';

		// Parse editor command to create list of arguments
		String command = AppConfig.INSTANCE.getEditorCommand();
		if (StringUtils.isNullOrEmpty(command))
			throw new AppException(ErrorId.NO_EDITOR_COMMAND);

		List<String> arguments = new ArrayList<>();
		StringBuilder buffer = new StringBuilder();
		int index = 0;
		while (index < command.length())
		{
			char ch = command.charAt(index++);
			switch (ch)
			{
				case ESCAPE_CHAR:
					if (index < command.length())
					{
						ch = command.charAt(index++);
						if (ch == PATHNAME_PLACEHOLDER_CHAR)
							buffer.append(pathname);
						else if (ch == URI_PLACEHOLDER_CHAR)
							buffer.append(Path.of(pathname).toUri());
						else
							buffer.append(ch);
					}
					break;

				case ' ':
					if (!buffer.isEmpty())
					{
						arguments.add(PathnameUtils.parsePathname(buffer.toString()));
						buffer.setLength(0);
					}
					break;

				default:
					buffer.append(ch);
					break;
			}
		}
		if (!buffer.isEmpty())
			arguments.add(PathnameUtils.parsePathname(buffer.toString()));

		// Execute editor command
		DaemonFactory.create(EDITOR_THREAD_NAME_PREFIX + ++editorThreadIndex, () ->
		{
			try
			{
				// Start process
				new ProcessBuilder(arguments).inheritIO().start();
			}
			catch (Exception e)
			{
				SwingUtilities.invokeLater(() ->
						RegexSearchApp.INSTANCE
								.showErrorMessage(RegexSearchApp.SHORT_NAME,
												  new AppException(ErrorId.FAILED_TO_EXECUTE_EDITOR_COMMAND, e)));
			}
		})
		.start();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : ActionListener interface
////////////////////////////////////////////////////////////////////////

	@Override
	public void actionPerformed(
		ActionEvent	event)
	{
		if (event.getActionCommand().equals(Command.SHOW_CONTEXT_MENU))
			onShowContextMenu();

		updateCommands();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : MenuListener interface
////////////////////////////////////////////////////////////////////////

	@Override
	public void menuCanceled(
		MenuEvent	event)
	{
		// do nothing
	}

	//------------------------------------------------------------------

	@Override
	public void menuDeselected(
		MenuEvent	event)
	{
		// do nothing
	}

	//------------------------------------------------------------------

	@Override
	public void menuSelected(
		MenuEvent	event)
	{
		Object eventSource = event.getSource();
		for (Menu menu : Menu.values())
		{
			if (eventSource == menu.menu)
				menu.update();
		}
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : MouseListener interface
////////////////////////////////////////////////////////////////////////

	@Override
	public void mouseClicked(
		MouseEvent	event)
	{
		// do nothing
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
		showContextMenu(event);
	}

	//------------------------------------------------------------------

	@Override
	public void mouseReleased(
		MouseEvent	event)
	{
		showContextMenu(event);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public TextModel getTextModel()
	{
		return textModel;
	}

	//------------------------------------------------------------------

	public ControlDialog getControlDialog()
	{
		return controlDialog;
	}

	//------------------------------------------------------------------

	public void openControlDialog()
	{
		if (controlDialog == null)
			controlDialog = ControlDialog.showDialog(this);
	}

	//------------------------------------------------------------------

	public void initTextModel(
		File			file,
		StringBuffer	text,
		boolean			visible)
	{
		// Perform any deferred edit
		deferredEdit();

		// Initialise text model
		textModel = new TextModel(file, text, TAB_GLYPH_CHAR);

		// Set model in text view and update window title
		if (visible)
		{
			textView.setModel(textModel);
			textModel.addChangeListener(textView);

			currentPathname = (file == null)
								? AppConstants.CLIPBOARD_STR
								: Utils.getPathname(file, AppConfig.INSTANCE.isShowUnixPathnames());
			updateTitle();
		}
		else
		{
			textView.setModel(null);

			if (currentPathname != null)
			{
				currentPathname = null;
				updateTitle();
			}
		}
	}

	//------------------------------------------------------------------

	public void makeTextSelectionViewable()
	{
		if (textView.getModel() != null)
		{
			// Get selection from text model
			TextModel.Selection selection = textModel.getSelection();

			// Get x coordinate of view
			TextArea.Line[] lines = textModel.getLines(selection.startRow, selection.startRow + 1);
			int selectionX = (lines.length == 0)
									? 0
									: textView.getFontMetrics(textView.getFont())
											.stringWidth(lines[0].text.substring(0, lines[0].highlightStartOffset));
			int verticalMargin = HIGHLIGHT_MARGIN_COLUMNS * textView.getColumnWidth();
			int x = Math.min(Math.max(0, selectionX + verticalMargin - textView.getViewport().getWidth()),
							 textView.getMaximumX());

			// Get y coordinate of view
			int numRows = textView.getRows();
			int maxRowIndex = Math.max(0, textModel.getNumLines() - numRows);
			int startRow = textView.getViewport().getViewPosition().y / textView.getRowHeight();
			if ((selection.startRow < startRow + HIGHLIGHT_MARGIN_ROWS)
					|| (selection.endRow >= startRow + numRows - HIGHLIGHT_MARGIN_ROWS))
				startRow = Math.min(Math.max(0, selection.startRow - HIGHLIGHT_MARGIN_ROWS), maxRowIndex);
			int y = startRow * textView.getRowHeight();

			// Set view position
			textView.setViewPosition(x, y);
		}
	}

	//------------------------------------------------------------------

	public List<File> getResultFiles()
	{
		return getResultList().getFiles();
	}

	//------------------------------------------------------------------

	public void appendResult(
		TextSearcher.FileResult	result)
	{
		getResultList().addFile(result);
		updateResultAreaViewPosition();
	}

	//------------------------------------------------------------------

	public void searchDialogClosed(
		TextSearcher.Option	option)
	{
		if (searchDialog != null)
		{
			searchDialog = null;
			search(new Task.ResumeSearch(option));
		}
	}

	//------------------------------------------------------------------

	public void executeCommand(
		AppCommand	command)
	{
		try
		{
			switch (command)
			{
				case IMPORT_FILE:
					onImportFile();
					break;

				case OPEN_SEARCH_PARAMETERS:
					onOpenSearchParams();
					break;

				case SAVE_SEARCH_PARAMETERS:
					onSaveSearchParams();
					break;

				case EXIT:
					onExit();
					break;

				case EDIT_FILE:
					onEditFile();
					break;

				case EDIT_FILE_DEFERRED:
					onEditFileDeferred();
					break;

				case SEARCH:
					onSearch();
					break;

				case COPY_RESULTS:
					onCopyResults();
					break;

				case SAVE_RESULTS:
					onSaveResults();
					break;

				case VIEW_SAVED_RESULTS:
					onViewSavedResults();
					break;

				case TOGGLE_CONTROL_DIALOG:
					onToggleControlDialog();
					break;

				case EDIT_PREFERENCES:
					onEditPreferences();
					break;
			}
		}
		catch (AppException e)
		{
			RegexSearchApp.INSTANCE.showErrorMessage(RegexSearchApp.SHORT_NAME, e);
		}

		updateCommands();
	}

	//------------------------------------------------------------------

	public void updateCommands()
	{
		boolean isFileSet = (controlDialog != null) && !controlDialog.isBeyondLastFileSet();
		boolean canEdit = (textModel != null) && (textModel.getFile() != null) && (currentPathname != null)
							&& (AppConfig.INSTANCE.getEditorCommand() != null);

		AppCommand.IMPORT_FILE.setEnabled(isFileSet);
		AppCommand.OPEN_SEARCH_PARAMETERS.setEnabled(!searching);
		AppCommand.SAVE_SEARCH_PARAMETERS.setEnabled(!searching);
		AppCommand.EXIT.setEnabled(true);
		AppCommand.EDIT_FILE.setEnabled(canEdit);
		AppCommand.EDIT_FILE_DEFERRED.setEnabled(canEdit && (deferredFile == null) && searching
													&& (searchKind == SearchDialog.Kind.REPLACE));
		AppCommand.SEARCH.setEnabled(isFileSet && !searching);
		AppCommand.COPY_RESULTS.setEnabled(!searching && !getResultList().isEmpty());
		AppCommand.SAVE_RESULTS.setEnabled(!searching && getResultList().isSearchedFiles());
		AppCommand.VIEW_SAVED_RESULTS.setEnabled(!searching && !getResultList().getFiles().isEmpty());
		AppCommand.TOGGLE_CONTROL_DIALOG.setEnabled(true);
		AppCommand.TOGGLE_CONTROL_DIALOG.setName(((controlDialog != null) && controlDialog.isVisible())
																	? AppCommand.HIDE_CONTROL_DIALOG_STR
																	: AppCommand.SHOW_CONTROL_DIALOG_STR);
		AppCommand.EDIT_PREFERENCES.setEnabled(!searching);
	}

	//------------------------------------------------------------------

	public void showContextMenu(
		MouseEvent	event,
		Component	component)
	{
		if ((event == null) || event.isPopupTrigger())
		{
			// Create context menu
			if (contextMenu == null)
			{
				contextMenu = new JPopupMenu();

				contextMenu.add(new FMenuItem(AppCommand.SEARCH));

				contextMenu.addSeparator();

				contextMenu.add(new FMenuItem(AppCommand.TOGGLE_CONTROL_DIALOG));

				contextMenu.addSeparator();

				contextMenu.add(new FMenuItem(AppCommand.EDIT_FILE));
				contextMenu.add(new FMenuItem(AppCommand.EDIT_FILE_DEFERRED));

				contextMenu.addSeparator();

				contextMenu.add(new FMenuItem(AppCommand.COPY_RESULTS));
				contextMenu.add(new FMenuItem(AppCommand.SAVE_RESULTS));
				contextMenu.add(new FMenuItem(AppCommand.VIEW_SAVED_RESULTS));

				contextMenu.addSeparator();

				contextMenu.add(new FMenuItem(AppCommand.EDIT_PREFERENCES));
			}

			// Update commands for menu items
			updateCommands();

			// Display menu
			if (event == null)
				contextMenu.show(component, 0, 0);
			else
				contextMenu.show(event.getComponent(), event.getX(), event.getY());
		}
	}

	//------------------------------------------------------------------

	private void showContextMenu(
		MouseEvent	event)
	{
		showContextMenu(event, textView);
	}

	//------------------------------------------------------------------

	private ResultList getResultList()
	{
		return (ResultList)resultArea.getModel();
	}

	//------------------------------------------------------------------

	private void updateTitle()
	{
		setTitle((currentPathname == null) ? RegexSearchApp.LONG_NAME + " " + RegexSearchApp.INSTANCE.getVersionString()
										   : RegexSearchApp.SHORT_NAME + " - " + currentPathname);
	}

	//------------------------------------------------------------------

	private void updateResultAreaViewPosition()
	{
		int y = Math.max(0, getResultList().getNumLines() - resultArea.getRows()) * resultArea.getRowHeight();
		resultArea.setViewPosition(0, y);
	}

	//------------------------------------------------------------------

	private void showSearchDialog(
		SearchDialog.Kind	dialogKind)
	{
		searchDialog = SearchDialog.showDialog(controlDialog, dialogKind, controlDialog.getTargetString(true),
											   currentPathname, textModel.getSelection().startRow,
											   textModel.getSelectionStart(), textModel.getSelectionEnd());
	}

	//------------------------------------------------------------------

	private boolean closeSearchParams()
	{
		// Update search parameters
		controlDialog.updateSearchParams();

		// Prompt to save search parameters
		SearchParameters searchParams = getSearchParams();
		if ((searchParams.getFile() != null) && searchParams.isChanged())
		{
			String[] optionStrs = Utils.getOptionStrings(SAVE_STR, DISCARD_STR);
			int result = JOptionPane.showOptionDialog(this, SAVE_MESSAGE_STR, RegexSearchApp.SHORT_NAME,
													  JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
													  null, optionStrs, optionStrs[0]);
			if (result == JOptionPane.YES_OPTION)
				searchParams.write();
			else if (result != JOptionPane.NO_OPTION)
				return false;
		}
		return true;
	}

	//------------------------------------------------------------------

	private File chooseOpen()
	{
		if (openFileChooser == null)
		{
			openFileChooser = new JFileChooser(getSearchParams().getFile());
			openFileChooser.setDialogTitle(OPEN_SEARCH_PARAMS_STR);
			openFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			FileChooserUtils.setFilter(openFileChooser, AppConstants.XML_FILE_FILTER);
		}
		openFileChooser.rescanCurrentDirectory();
		return (openFileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
				? openFileChooser.getSelectedFile()
				: null;
	}

	//------------------------------------------------------------------

	private File chooseSave(
		File	file)
	{
		if (saveFileChooser == null)
		{
			saveFileChooser = new JFileChooser(getSearchParams().getFile());
			saveFileChooser.setDialogTitle(SAVE_SEARCH_PARAMS_STR);
			saveFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			FileChooserUtils.setFilter(saveFileChooser, AppConstants.XML_FILE_FILTER);
		}
		saveFileChooser.setSelectedFile((file == null) ? new File("") : file);
		saveFileChooser.rescanCurrentDirectory();
		return (saveFileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
				? Utils.appendSuffix(saveFileChooser.getSelectedFile(), AppConstants.XML_FILENAME_EXTENSION)
				: null;
	}

	//------------------------------------------------------------------

	private void search(
		Task	task)
	{
		// Perform search
		TextSearcher textSearcher = RegexSearchApp.INSTANCE.getTextSearcher();
		try
		{
			// Hide control dialog
			if (controlDialog.isVisible() && AppConfig.INSTANCE.isHideControlDialogWhenSearching())
			{
				controlDialog.setVisible(false);
				controlDialogHidden = true;
			}

			// Update commands for search in progress
			searching = true;
			updateCommands();

			// Display search progress dialog
			TaskProgressDialog.showDialog(this, SEARCHING_STR + AppConstants.ELLIPSIS_STR, task);

			// Bring window to front
			toFront();

			// Display dialog of search options
			switch (textSearcher.getStopSubstate())
			{
				case MATCH:
					showSearchDialog(searchKind);
					break;

				case PREVIEW:
					showSearchDialog(SearchDialog.Kind.PREVIEW);
					break;

				case DONE:
					// do nothing
					break;
			}
		}
		catch (AppException e)
		{
			RegexSearchApp.INSTANCE.showErrorMessage(RegexSearchApp.SHORT_NAME, e);
		}

		// Update results at end of search
		if (searchDialog == null)
		{
			// Indicate end of search
			searching = false;

			// Show control dialog
			if (controlDialogHidden && !controlDialog.isVisible())
				controlDialog.setVisible(true);

			// Perform any deferred edit
			deferredEdit();

			// Update results with aggregate result
			ResultList resultList = getResultList();
			resultList.addAggregate(textSearcher.getAggregateResult());

			// Update results with files in which the target was not found
			List<File> files = textSearcher.getTargetNotFoundFiles();
			if (!files.isEmpty())
				resultList.addFiles(TARGET_NOT_FOUND_STR, files, true);

			// Update results with erroneous pathnames
			files = PathnameFilter.getErrors();
			if (!files.isEmpty())
				resultList.addFiles(UNPROCESSED_STR + ": " + NO_CANONICAL_PATHNAME_STR, files, false);

			// Update results with unprocessed files
			files = textSearcher.getUnprocessedFiles();
			if (!files.isEmpty())
				resultList.addFiles(UNPROCESSED_STR + ": " + PROCESSING_ERROR_STR, files, true);

			// Update results with files whose attributes were not set
			files = textSearcher.getAttributesNotSetFiles();
			if (!files.isEmpty())
				resultList.addFiles(UNPROCESSED_STR + ": " + ATTRIBUTES_NOT_SET_STR, files, true);

			// Update result area
			updateResultAreaViewPosition();

			// Update commands
			updateCommands();
		}
	}

	//------------------------------------------------------------------

	private void deferredEdit()
	{
		if (textModel != null)
		{
			File file = textModel.getFile();
			if ((file != null) && (file == deferredFile))
			{
				try
				{
					deferredFile = null;
					onEditFile();
				}
				catch (AppException e)
				{
					RegexSearchApp.INSTANCE.showErrorMessage(RegexSearchApp.SHORT_NAME, e);
				}
			}
		}
	}

	//------------------------------------------------------------------

	private void updateConfiguration()
	{
		// Set configuration properties
		AppConfig config = AppConfig.INSTANCE;
		Dimension size = controlDialog.getParameterEditorSize();
		if (!controlDialog.getInitialParameterEditorSize().equals(size))
		{
			size.width = Math.min(Math.max(ParameterEditor.MIN_NUM_COLUMNS, size.width),
								  ParameterEditor.MAX_NUM_COLUMNS);
			size.height = Math.min(Math.max(ParameterEditor.MIN_NUM_ROWS, size.height), ParameterEditor.MAX_NUM_ROWS);
			config.setParameterEditorSize(size);
		}

		// Save location of main window and control dialog
		if (config.isMainWindowLocation())
		{
			Point location = GuiUtils.getFrameLocation(this);
			if (location != null)
				config.setMainWindowLocation(location);

			controlDialog.setVisible(true);
			location = controlDialog.getLocationOnScreen();
			if (location != null)
				config.setControlDialogLocation(location);
		}

		// Write configuration
		config.write();
	}

	//------------------------------------------------------------------

	private void onShowContextMenu()
	{
		showContextMenu(null);
	}

	//------------------------------------------------------------------

	private void onImportFile()
	{
		controlDialog.importFile();
	}

	//------------------------------------------------------------------

	private void onOpenSearchParams()
		throws AppException
	{
		// Prompt to save search parameters
		if (!closeSearchParams())
			return;

		// Choose file
		File file = chooseOpen();

		// Open file
		if (file != null)
			RegexSearchApp.INSTANCE.openSearchParams(file);

		// Update components of control dialog
		controlDialog.updateComponents();
	}

	//------------------------------------------------------------------

	private void onSaveSearchParams()
		throws AppException
	{
		// Update search parameters
		controlDialog.updateSearchParams();

		// Choose file
		SearchParameters searchParams = getSearchParams();
		File file = chooseSave(searchParams.getFile());

		// Write file
		String[] optionStrs = Utils.getOptionStrings(AppConstants.REPLACE_STR);
		if ((file != null)
				&& (!file.exists()
				|| (JOptionPane.showOptionDialog(this, Utils.getPathname(file) + AppConstants.ALREADY_EXISTS_STR,
												 SAVE_SEARCH_PARAMS_STR, JOptionPane.OK_CANCEL_OPTION,
												 JOptionPane.WARNING_MESSAGE, null, optionStrs, optionStrs[1])
																							== JOptionPane.OK_OPTION)))
		{
			TaskProgressDialog.showDialog(this, WRITE_SEARCH_PARAMS_STR,
										  new Task.WriteSearchParams(searchParams, file));
		}
	}

	//------------------------------------------------------------------

	private void onExit()
	{
		// Prompt to save search parameters
		if (!closeSearchParams())
			return;

		// Update configuration
		updateConfiguration();

		// Close window and exit
		setVisible(false);
		dispose();
		System.exit(0);
	}

	//------------------------------------------------------------------

	private void onEditFile()
		throws AppException
	{
		if (textModel != null)
		{
			File file = textModel.getFile();
			if (file != null)
				editFile(file.getPath());
		}
	}

	//------------------------------------------------------------------

	private void onEditFileDeferred()
	{
		if (textModel != null)
			deferredFile = textModel.getFile();
	}

	//------------------------------------------------------------------

	private void onSearch()
		throws AppException
	{
		// Get search parameters from control dialog
		TextSearcher.Params params = controlDialog.getCurrentSearchParams();

		// Set search kind
		searchKind = (params.replacementStr == null) ? SearchDialog.Kind.FIND : SearchDialog.Kind.REPLACE;

		// Clear result area
		resultArea.setText(null);

		// Reset text model
		initTextModel(null, null, false);

		// Start search
		controlDialogHidden = false;
		search(new Task.StartSearch(params));
	}

	//------------------------------------------------------------------

	private void onCopyResults()
		throws AppException
	{
		String text = AppConfig.INSTANCE.isCopyResultsAsListFile()
											? getResultList().getText(ControlDialog.COMMENT_PREFIX_CHAR)
											: getResultList().getText();
		Utils.putClipboardText(text);
	}

	//------------------------------------------------------------------

	private void onSaveResults()
	{
		getResultList().updateFiles();
	}

	//------------------------------------------------------------------

	private void onViewSavedResults()
	{
		StringBuilder buffer = new StringBuilder();
		List<File> files = getResultList().getFiles();
		for (int i = 0; i < files.size(); i++)
		{
			if (i > 0)
				buffer.append('\n');
			buffer.append(Utils.getPathname(files.get(i)));
		}
		SavedResultsDialog.showDialog(this, buffer.toString());
	}

	//------------------------------------------------------------------

	private void onToggleControlDialog()
	{
		if (controlDialog != null)
			controlDialog.setVisible(!controlDialog.isVisible());
	}

	//------------------------------------------------------------------

	private void onEditPreferences()
	{
		if (PreferencesDialog.showDialog(this))
		{
			ExceptionUtils.setUnixStyle(AppConfig.INSTANCE.isShowUnixPathnames());
			controlDialog.updatePreferences();
		}
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Enumerated types
////////////////////////////////////////////////////////////////////////


	// ENUMERATION: MENUS


	private enum Menu
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		FILE
		(
			"File",
			KeyEvent.VK_F
		)
		{
			@Override
			protected void update()
			{
				getWindow().updateCommands();
			}
		},

		EDIT
		(
			"Edit",
			KeyEvent.VK_E
		)
		{
			@Override
			protected void update()
			{
				getWindow().updateCommands();
			}
		},

		SEARCH
		(
			"Search",
			KeyEvent.VK_S
		)
		{
			@Override
			protected void update()
			{
				getMenu().setEnabled(!getWindow().searching);
				getWindow().updateCommands();
			}
		},

		VIEW
		(
			"View",
			KeyEvent.VK_V
		)
		{
			@Override
			protected void update()
			{
				getWindow().updateCommands();
			}
		},

		OPTIONS
		(
			"Options",
			KeyEvent.VK_O
		)
		{
			@Override
			protected void update()
			{
				getMenu().setEnabled(!getWindow().searching);
				getWindow().updateCommands();
			}
		};

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	JMenu	menu;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private Menu(
			String	text,
			int		keyCode)
		{
			menu = new FMenu(text, keyCode);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Abstract methods
	////////////////////////////////////////////////////////////////////

		protected abstract void update();

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		protected JMenu getMenu()
		{
			return menu;
		}

		//--------------------------------------------------------------

		protected MainWindow getWindow()
		{
			return RegexSearchApp.INSTANCE.getMainWindow();
		}

		//--------------------------------------------------------------

	}

	//==================================================================


	// ENUMERATION: ERROR IDENTIFIERS


	private enum ErrorId
		implements AppException.IId
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		NO_EDITOR_COMMAND
		("No editor command is defined."),

		FAILED_TO_EXECUTE_EDITOR_COMMAND
		("Failed to execute the editor command.");

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

		@Override
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


	// CLASS: SCROLL PANE


	private static class ScrollPane
		extends JScrollPane
		implements ChangeListener
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		private static final	int	VERTICAL_MARGIN		= 2;
		private static final	int	HORIZONTAL_MARGIN	= 4;

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	TextArea	textArea;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private ScrollPane(
			TextArea	textArea)
		{
			// Call superclass constructor
			super(textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

			// Initialise instance variables
			this.textArea = textArea;

			// Set viewport in text area
			textArea.setViewport(getViewport());

			// Set properties
			setBackground(AppConfig.INSTANCE.getTextAreaBackgroundColour());
			setCorner(JScrollPane.LOWER_RIGHT_CORNER, new JPanel());
			GuiUtils.setViewportBorder(this, VERTICAL_MARGIN, HORIZONTAL_MARGIN);
			getViewport().setBackground(AppConfig.INSTANCE.getTextAreaBackgroundColour());
			getViewport().setFocusable(false);
			getVerticalScrollBar().setFocusable(false);
			getHorizontalScrollBar().setFocusable(false);

			// Add listeners
			getVerticalScrollBar().getModel().addChangeListener(this);
			getHorizontalScrollBar().getModel().addChangeListener(this);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : ChangeListener interface
	////////////////////////////////////////////////////////////////////

		@Override
		public void stateChanged(
			ChangeEvent	event)
		{
			// Update viewport position if neither scroll bar knob is being dragged
			if (!getVerticalScrollBar().getValueIsAdjusting() &&
				 !getHorizontalScrollBar().getValueIsAdjusting())
				textArea.snapViewPosition();
		}

		//--------------------------------------------------------------

	}

	//==================================================================


	// RESULT AREA CLASS


	private static class ResultArea
		extends TextArea
		implements MouseMotionListener
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		private static final	int	MAX_NUM_COLUMNS	= 1024;

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	int		selectedIndex;
		private	boolean	armed;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private ResultArea(
			int	columns,
			int	rows)
		{
			// Call superclass constructor
			super(columns, rows, MAX_NUM_COLUMNS, AppFont.RESULT_AREA.getFont());

			// Initialise instance variables
			selectedIndex = -1;

			// Add listeners
			addMouseMotionListener(this);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : MouseMotionListener interface
	////////////////////////////////////////////////////////////////////

		@Override
		public void mouseDragged(
			MouseEvent	event)
		{
			if (SwingUtilities.isLeftMouseButton(event) && (selectedIndex >= 0))
			{
				int index = getIndex(event);
				if ((index == selectedIndex) != armed)
				{
					armed = !armed;
					((ResultList)getModel()).setElementSelected(selectedIndex, armed);
					repaint();
				}
			}
		}

		//--------------------------------------------------------------

		@Override
		public void mouseMoved(
			MouseEvent	event)
		{
			// do nothing
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		public void mousePressed(
			MouseEvent	event)
		{
			super.mousePressed(event);

			if (SwingUtilities.isLeftMouseButton(event) && event.isControlDown())
			{
				int index = getIndex(event);
				if (index >= 0)
				{
					ResultList resultList = (ResultList)getModel();
					String pathname = resultList.getSearchedPathname(index);
					if (pathname != null)
					{
						resultList.setElementSelected(index, true);
						repaint();
						selectedIndex = index;
						armed = true;
					}
				}
			}
		}

		//--------------------------------------------------------------

		@Override
		public void mouseReleased(
			MouseEvent	event)
		{
			if (SwingUtilities.isLeftMouseButton(event) && (selectedIndex >= 0))
			{
				ResultList resultList = (ResultList)getModel();
				int index = getIndex(event);
				if ((index == selectedIndex) && armed)
				{
					try
					{
						editFile(resultList.getSearchedPathname(index));
					}
					catch (AppException e)
					{
						RegexSearchApp.INSTANCE.showErrorMessage(RegexSearchApp.SHORT_NAME, e);
					}
				}

				resultList.setElementSelected(selectedIndex, false);
				repaint();
				selectedIndex = -1;
				armed = false;
			}
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		private int getIndex(
			MouseEvent	event)
		{
			int index = -1;
			int x = event.getX();
			if ((x >= 0) && (x < getWidth()))
			{
				int row = event.getY() / getRowHeight();
				ResultList resultList = (ResultList)getModel();
				if ((row >= 0) && (row < resultList.getNumLines()))
				{
					String pathname = resultList.getSearchedPathname(row);
					if ((pathname != null) && (x < getFontMetrics(getFont()).stringWidth(pathname)))
						 index = row;
				}
			}
			return index;
		}

		//--------------------------------------------------------------

	}

	//==================================================================

}

//----------------------------------------------------------------------
