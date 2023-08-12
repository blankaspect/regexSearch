/*====================================================================*\

ClassUtils.java

Class: class-related utility methods.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.cls;

//----------------------------------------------------------------------


// IMPORTS


import java.io.File;

import java.net.URL;

import java.nio.file.Path;

import java.security.CodeSource;
import java.security.ProtectionDomain;

import uk.blankaspect.common.exception2.LocationException;

import uk.blankaspect.common.misc.JarUtils;

//----------------------------------------------------------------------


// CLASS: CLASS-RELATED UTILITY METHODS


/**
 * This class contains utility methods that relate to Java classes.
 */

public class ClassUtils
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The filename extension of a Java class file. */
	private static final	String	CLASS_FILENAME_EXTENSION	= ".class";

	/** The character that separates adjacent elements of the fully qualified name of a class. */
	private static final	char	CLASS_NAME_SEPARATOR_CHAR	= '.';

	/** The prefix of a JAR-scheme URL. */
	private static final	String	JAR_URL_PREFIX	= "jar:";

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private ClassUtils()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns {@code true} if the specified class was loaded from a JAR.
	 *
	 * @param  cls
	 *           the class of interest.
	 * @return {@code true} if the specified class was loaded from a JAR, {@code false} otherwise.
	 */

	public static boolean isFromJar(
		Class<?>	cls)
	{
		URL url = cls.getResource(cls.getSimpleName() + CLASS_FILENAME_EXTENSION);
		return (url != null) && url.toString().startsWith(JAR_URL_PREFIX);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the location of the {@link CodeSource} of the {@link ProtectionDomain} of the specified class.
	 *
	 * @param  cls
	 *           the class of interest.
	 * @return the location of the {@link CodeSource} of the {@link ProtectionDomain} of the specified class, or {@code
	 *         null} if there is no location or the location could not be obtained.
	 */

	public static URL getSourceLocation(
		Class<?>	cls)
	{
		try
		{
			CodeSource codeSource = cls.getProtectionDomain().getCodeSource();
			return (codeSource == null) ? null : codeSource.getLocation();
		}
		catch (Exception e)
		{
			return null;
		}
	}

	//------------------------------------------------------------------

	/**
	 * Returns the file-system location of the container of the class file of the specified class.  The container may be
	 * a JAR file or a directory.
	 *
	 * @param  cls
	 *           the class of interest.
	 * @return the file-system location of the container of the class file of the specified class, or {@code null} if
	 *         there is no location or the location could not be obtained.
	 */

	public static Path getClassFileContainer(
		Class<?>	cls)
	{
		// Initialise location of JAR file or directory
		Path location = null;

		// Try to get the location of the container through the code source of the protection domain of the class
		try
		{
			CodeSource codeSource = cls.getProtectionDomain().getCodeSource();
			if (codeSource != null)
			{
				URL url = codeSource.getLocation();
				if (url != null)
					location = Path.of(url.toURI());
			}
		}
		catch (Exception e)
		{
			// ignore
		}

		// Find the location of the class file, then extract the location of the JAR file or directory from it
		if (location == null)
		{
			URL url = cls.getResource(cls.getSimpleName() + CLASS_FILENAME_EXTENSION);
			if (url != null)
			{
				// Get file-system location of class file
				Path file = null;
				try
				{
					file = JarUtils.urlToPath(url);
				}
				catch (LocationException e)
				{
					// ignore
				}

				// If location of class file was obtained, get location of JAR file or base directory of class file
				if (file != null)
				{
					// Case: container is JAR
					if (url.toString().startsWith(JAR_URL_PREFIX))
						location = file;

					// Case: container is directory
					else
					{
						String relativePathname =
								(cls.getName() + CLASS_FILENAME_EXTENSION).replace(CLASS_NAME_SEPARATOR_CHAR,
																				   File.separatorChar);
						if (file.endsWith(relativePathname))
						{
							String pathname = file.toString();
							location = Path.of(pathname.substring(0, pathname.length() - relativePathname.length()));
						}
					}
				}
			}
		}

		// Return location of JAR file or directory
		return location;
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
