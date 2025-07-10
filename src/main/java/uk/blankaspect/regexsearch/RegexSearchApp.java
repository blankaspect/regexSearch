/*====================================================================*\

RegexSearchApp.java

Class: RegexSearch application.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.regexsearch;

//----------------------------------------------------------------------


// IMPORTS


import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import uk.blankaspect.common.build.BuildUtils;

import uk.blankaspect.common.cls.ClassUtils;

import uk.blankaspect.common.exception.AppException;
import uk.blankaspect.common.exception.ExceptionUtils;
import uk.blankaspect.common.exception.FileException;

import uk.blankaspect.common.exception2.LocationException;

import uk.blankaspect.common.logging.ErrorLogger;

import uk.blankaspect.common.resource.ResourceProperties;
import uk.blankaspect.common.resource.ResourceUtils;

import uk.blankaspect.common.string.StringUtils;

import uk.blankaspect.ui.swing.text.TextRendering;

import uk.blankaspect.ui.swing.textfield.TextFieldUtils;

//----------------------------------------------------------------------


// CLASS: APPLICATION


public class RegexSearchApp
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	public static final		RegexSearchApp	INSTANCE	= new RegexSearchApp();

	public static final		String	SHORT_NAME	= "RegexSearch";
	public static final		String	LONG_NAME	= "Regular-expression search tool";
	public static final		String	NAME_KEY	= StringUtils.firstCharToLowerCase(SHORT_NAME);

	private static final	String	BUILD_PROPERTIES_FILENAME	= "build.properties";

	private static final	String	CONFIG_ERROR_STR		= "Configuration error";
	private static final	String	LAF_ERROR1_STR			= "Look-and-feel: ";
	private static final	String	LAF_ERROR2_STR			= "\nThe look-and-feel is not installed.";
	private static final	String	SEARCH_PARAMS_STR		= "Search parameters";
	private static final	String	READ_SEARCH_PARAMS_STR	= "Read search parameters";

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	ResourceProperties	buildProperties;
	private	String				versionStr;
	private	SearchParameters	searchParams;
	private	TextSearcher		textSearcher;
	private	MainWindow			mainWindow;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	private RegexSearchApp()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static void main(
		String[]	args)
	{
		INSTANCE.start();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public SearchParameters getSearchParams()
	{
		return searchParams;
	}

	//------------------------------------------------------------------

	public TextSearcher getTextSearcher()
	{
		return textSearcher;
	}

	//------------------------------------------------------------------

	public String getVersionString()
	{
		return versionStr;
	}

	//------------------------------------------------------------------

	public MainWindow getMainWindow()
	{
		return mainWindow;
	}

	//------------------------------------------------------------------

	public void showWarningMessage(
		String	title,
		Object	message)
	{
		showMessageDialog(title, message, JOptionPane.WARNING_MESSAGE);
	}

	//------------------------------------------------------------------

	public void showErrorMessage(
		String	title,
		Object	message)
	{
		showMessageDialog(title, message, JOptionPane.ERROR_MESSAGE);
	}

	//------------------------------------------------------------------

	public void showMessageDialog(
		String	title,
		Object	message,
		int		messageKind)
	{
		JOptionPane.showMessageDialog(mainWindow, message, title, messageKind);
	}

	//------------------------------------------------------------------

	public void openSearchParams(
		File	file)
		throws AppException
	{
		SearchParameters newSearchParams = new SearchParameters();
		TaskProgressDialog.showDialog(mainWindow, READ_SEARCH_PARAMS_STR,
									  new Task.ReadSearchParams(newSearchParams, file));
		searchParams = newSearchParams;
	}

	//------------------------------------------------------------------

	private void start()
	{
		// Log stack trace of uncaught exception
		if (ClassUtils.isFromJar(getClass()))
		{
			Thread.setDefaultUncaughtExceptionHandler((thread, exception) ->
			{
				try
				{
					ErrorLogger.INSTANCE.write(exception);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			});
		}

		// Read build properties and initialise version string
		try
		{
			buildProperties =
					new ResourceProperties(ResourceUtils.normalisedPathname(getClass(), BUILD_PROPERTIES_FILENAME));
			versionStr = BuildUtils.versionString(getClass(), buildProperties);
		}
		catch (LocationException e)
		{
			e.printStackTrace();
		}

		// Initialise instance variables
		searchParams = new SearchParameters();
		textSearcher = new TextSearcher();

		// Read configuration
		AppConfig config = AppConfig.INSTANCE;
		config.read();

		// Set UNIX style for pathnames in file exceptions
		ExceptionUtils.setUnixStyle(config.isShowUnixPathnames());

		// Set text antialiasing
		TextRendering.setAntialiasing(config.getTextAntialiasing());

		// Set look-and-feel
		String lookAndFeelName = config.getLookAndFeel();
		for (UIManager.LookAndFeelInfo lookAndFeelInfo : UIManager.getInstalledLookAndFeels())
		{
			if (lookAndFeelInfo.getName().equals(lookAndFeelName))
			{
				try
				{
					UIManager.setLookAndFeel(lookAndFeelInfo.getClassName());
				}
				catch (Exception e)
				{
					// ignore
				}
				lookAndFeelName = null;
				break;
			}
		}
		if (lookAndFeelName != null)
		{
			showWarningMessage(SHORT_NAME + " : " + CONFIG_ERROR_STR,
							   LAF_ERROR1_STR + lookAndFeelName + LAF_ERROR2_STR);
		}

		// Select all text when a text field gains focus
		if (config.isSelectTextOnFocusGained())
			TextFieldUtils.selectAllOnFocusGained();

		// Perform remaining initialisation on event-dispatching thread
		SwingUtilities.invokeLater(() ->
		{
			// Create main window
			mainWindow = new MainWindow();

			// Create control dialog
			mainWindow.openControlDialog();

			// Read search parameters
			try
			{
				File file = AppConfig.INSTANCE.getDefaultSearchParamsFile();
				if (file != null)
				{
					if (!file.isFile())
						throw new FileException(ErrorId.FILE_DOES_NOT_EXIST, file);
					searchParams = new SearchParameters(file);
					mainWindow.getControlDialog().updateComponents();
				}
			}
			catch (AppException e)
			{
				showErrorMessage(SHORT_NAME + " : " + SEARCH_PARAMS_STR, e);
			}
		});
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

		FILE_DOES_NOT_EXIST
		("The file does not exist.");

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

}

//----------------------------------------------------------------------
