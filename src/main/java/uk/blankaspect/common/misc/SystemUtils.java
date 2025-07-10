/*====================================================================*\

SystemUtils.java

Class: system utility methods.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.misc;

//----------------------------------------------------------------------


// IMPORTS


import java.nio.file.Path;

//----------------------------------------------------------------------


// CLASS: SYSTEM UTILITY METHODS


public class SystemUtils
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	private static final	String	DEFAULT_LINE_SEPARATOR	= "\n";

	/** Keys of system properties. */
	private interface SystemPropertyKey
	{
		String	LINE_SEPARATOR	= "line.separator";
		String	TEMP_DIR		= "java.io.tmpdir";
		String	USER_HOME_DIR	= "user.home";
		String	WORKING_DIR		= "user.dir";
	}

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

	public static String lineSeparator()
	{
		return System.getProperty(SystemPropertyKey.LINE_SEPARATOR, DEFAULT_LINE_SEPARATOR);
	}

	//------------------------------------------------------------------

	public static String userHomeDirectoryPathname()
	{
		return System.getProperty(SystemPropertyKey.USER_HOME_DIR, ".");
	}

	//------------------------------------------------------------------

	public static Path userHomeDirectory()
	{
		return Path.of(userHomeDirectoryPathname());
	}

	//------------------------------------------------------------------

	public static String workingDirectoryPathname()
	{
		return System.getProperty(SystemPropertyKey.WORKING_DIR, ".");
	}

	//------------------------------------------------------------------

	public static Path workingDirectory()
	{
		return Path.of(workingDirectoryPathname());
	}

	//------------------------------------------------------------------

	public static String tempDirectoryPathname()
	{
		return System.getProperty(SystemPropertyKey.TEMP_DIR, ".");
	}

	//------------------------------------------------------------------

	public static Path tempDirectory()
	{
		return Path.of(tempDirectoryPathname());
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
