/*====================================================================*\

App.java

Application class.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.regexsearch;

//----------------------------------------------------------------------


// IMPORTS


import java.io.File;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import uk.blankaspect.common.exception.AppException;
import uk.blankaspect.common.exception.ExceptionUtils;
import uk.blankaspect.common.exception.FileException;

import uk.blankaspect.common.gui.TextRendering;

import uk.blankaspect.common.misc.CalendarTime;
import uk.blankaspect.common.misc.NoYes;
import uk.blankaspect.common.misc.ResourceProperties;

import uk.blankaspect.common.textfield.TextFieldUtils;

//----------------------------------------------------------------------


// APPLICATION CLASS


public class App
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	public static final		App		INSTANCE	= new App();

	public static final		String	SHORT_NAME	= "RegexSearch";
	public static final		String	LONG_NAME	= "Regular-expression search tool";
	public static final		String	NAME_KEY	= "regexSearch";

	private static final	String	VERSION_PROPERTY_KEY	= "version";
	private static final	String	BUILD_PROPERTY_KEY		= "build";
	private static final	String	RELEASE_PROPERTY_KEY	= "release";

	private static final	String	BUILD_PROPERTIES_FILENAME	= "build.properties";

	private static final	String	CONFIG_ERROR_STR		= "Configuration error";
	private static final	String	LAF_ERROR1_STR			= "Look-and-feel: ";
	private static final	String	LAF_ERROR2_STR			= "\nThe look-and-feel is not installed.";
	private static final	String	SEARCH_PARAMS_STR		= "Search parameters";
	private static final	String	READ_SEARCH_PARAMS_STR	= "Read search parameters";

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

		FILE_DOES_NOT_EXIST
		("The file does not exist.");

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
//  Member classes : inner classes
////////////////////////////////////////////////////////////////////////


	// INITIALISATION CLASS


	/**
	 * The run() method of this class creates the main window and performs the remaining initialisation of
	 * the application from the event-dispatching thread.
	 */

	private class DoInitialisation
		implements Runnable
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private DoInitialisation()
		{
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : Runnable interface
	////////////////////////////////////////////////////////////////////

		public void run()
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
		}

		//--------------------------------------------------------------

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	private App()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static void main(String[] args)
	{
		INSTANCE.init();
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

	public MainWindow getMainWindow()
	{
		return mainWindow;
	}

	//------------------------------------------------------------------

	public String getVersionString()
	{
		StringBuilder buffer = new StringBuilder(32);
		String str = buildProperties.get(VERSION_PROPERTY_KEY);
		if (str != null)
			buffer.append(str);

		str = buildProperties.get(RELEASE_PROPERTY_KEY);
		if (str == null)
		{
			long time = System.currentTimeMillis();
			if (buffer.length() > 0)
				buffer.append(' ');
			buffer.append('b');
			buffer.append(CalendarTime.dateToString(time));
			buffer.append('-');
			buffer.append(CalendarTime.hoursMinsToString(time));
		}
		else
		{
			NoYes release = NoYes.forKey(str);
			if ((release == null) || !release.toBoolean())
			{
				str = buildProperties.get(BUILD_PROPERTY_KEY);
				if (str != null)
				{
					if (buffer.length() > 0)
						buffer.append(' ');
					buffer.append(str);
				}
			}
		}

		return buffer.toString();
	}

	//------------------------------------------------------------------

	public void showWarningMessage(String titleStr,
								   Object message)
	{
		showMessageDialog(titleStr, message, JOptionPane.WARNING_MESSAGE);
	}

	//------------------------------------------------------------------

	public void showErrorMessage(String titleStr,
								 Object message)
	{
		showMessageDialog(titleStr, message, JOptionPane.ERROR_MESSAGE);
	}

	//------------------------------------------------------------------

	public void showMessageDialog(String titleStr,
								  Object message,
								  int    messageKind)
	{
		JOptionPane.showMessageDialog(mainWindow, message, titleStr, messageKind);
	}

	//------------------------------------------------------------------

	public void openSearchParams(File file)
		throws AppException
	{
		SearchParameters newSearchParams = new SearchParameters();
		TaskProgressDialog.showDialog(mainWindow, READ_SEARCH_PARAMS_STR,
									  new Task.ReadSearchParams(newSearchParams, file));
		searchParams = newSearchParams;
	}

	//------------------------------------------------------------------

	private void init()
	{
		// Read build properties
		buildProperties = new ResourceProperties(BUILD_PROPERTIES_FILENAME, getClass());

		// Initialise instance fields
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
			showWarningMessage(SHORT_NAME + " : " + CONFIG_ERROR_STR,
							   LAF_ERROR1_STR + lookAndFeelName + LAF_ERROR2_STR);

		// Select all text when a text field gains focus
		if (config.isSelectTextOnFocusGained())
			TextFieldUtils.selectAllOnFocusGained();

		// Perform remaining initialisation from event-dispatching thread
		SwingUtilities.invokeLater(new DoInitialisation());
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance fields
////////////////////////////////////////////////////////////////////////

	private	ResourceProperties	buildProperties;
	private	SearchParameters	searchParams;
	private	TextSearcher		textSearcher;
	private	MainWindow			mainWindow;

}

//----------------------------------------------------------------------
