/*====================================================================*\

PropertiesPathname.java

Properties pathname class.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.config;

//----------------------------------------------------------------------


// IMPORTS


import java.io.File;

import uk.blankaspect.common.filesystem.PathnameUtils;

import uk.blankaspect.common.misc.SystemUtils;

import uk.blankaspect.common.os.OsUtils;

//----------------------------------------------------------------------


// PROPERTIES PATHNAME CLASS


public class PropertiesPathname
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	private static final	String	APP_PREFIX					= "app.";
	private static final	String	UNIX_PROPERTIES_DIR_PREFIX	= ".";

	private static final	String	PROPERTIES_DIR_PROPERTY_KEY	= "propertiesDir";

	private static final	String	WINDOWS_APP_CONFIG_DIR_KEY	= "APPDATA";

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	private PropertiesPathname()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static String getPathname()
	{
		String pathname = null;
		try
		{
			pathname = System.getProperty(APP_PREFIX + PROPERTIES_DIR_PROPERTY_KEY);
			if (pathname == null)
			{
				if (OsUtils.isWindows())
				{
					pathname = System.getenv(WINDOWS_APP_CONFIG_DIR_KEY);
					if (pathname != null)
						pathname += File.separator;
				}
				else
				{
					pathname = SystemUtils.userHomeDirectoryPathname();
					if (pathname != null)
						pathname += File.separator + UNIX_PROPERTIES_DIR_PREFIX;
				}
				if (pathname != null)
					pathname += ConfigName.DIRECTORY_NAME;
			}
			else
				pathname = PathnameUtils.parsePathname(pathname);

			if ((pathname != null) && !pathname.endsWith(File.separator) && !pathname.endsWith("/"))
				pathname += File.separator;
		}
		catch (SecurityException e)
		{
			// ignore
		}
		return pathname;
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
