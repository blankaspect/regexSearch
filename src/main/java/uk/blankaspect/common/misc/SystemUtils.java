/*====================================================================*\

SystemUtils.java

System utility methods class.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.misc;

//----------------------------------------------------------------------


// SYSTEM UTILITY METHODS CLASS


public class SystemUtils
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	private static final	String	USER_HOME_PROPERTY_KEY					= "user.home";
	private static final	String	CURRENT_WORKING_DIRECTORY_PROPERTY_KEY	= "user.dir";
	private static final	String	LINE_SEPARATOR_PROPERTY_KEY				= "line.separator";

	private static final	String	DEFAULT_LINE_SEPARATOR	= "\n";

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private SystemUtils()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static String getUserHomePathname()
	{
		return System.getProperty(USER_HOME_PROPERTY_KEY);
	}

	//------------------------------------------------------------------

	public static String getCurrentWorkingDirectoryPathname()
	{
		return System.getProperty(CURRENT_WORKING_DIRECTORY_PROPERTY_KEY, ".");
	}

	//------------------------------------------------------------------

	public static String getLineSeparator()
	{
		return System.getProperty(LINE_SEPARATOR_PROPERTY_KEY, DEFAULT_LINE_SEPARATOR);
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
