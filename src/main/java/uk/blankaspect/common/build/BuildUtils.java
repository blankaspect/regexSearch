/*====================================================================*\

BuildUtils.java

Class: build-related utility methods.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.build;

//----------------------------------------------------------------------


// IMPORTS


import java.time.LocalDateTime;

import java.time.format.DateTimeFormatter;

import uk.blankaspect.common.cls.ClassUtils;

import uk.blankaspect.common.resource.ResourceProperties;

//----------------------------------------------------------------------


// CLASS: BUILD-RELATED UTILITY METHODS


/**
 * This class contains utility methods that relate to builds.
 */

public class BuildUtils
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The pattern for the date and time in the version string. */
	private static final	String	VERSION_DATE_TIME_PATTERN	= "uuuuMMdd-HHmmss";

	/** Keys of build properties. */
	private interface BuildPropertyKey
	{
		String	BUILD	= "build";
		String	RELEASE	= "release";
		String	VERSION	= "version";
	}

	/** Keys of system properties. */
	private interface SystemPropertyKey
	{
		String	NO_VERSION	= "blankaspect.build.noVersion";
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private BuildUtils()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a string representation of the version of the specified class.  If the class was loaded from a JAR, the
	 * string is created from the specified build properties; otherwise, the string is created from the current date and
	 * time.
	 * @param  cls
	 *           the class for which a string representation of a version is desired.
	 * @param  properties
	 *           the build properties from which a string representation will be created if {@code cls} was loaded from
	 *           a JAR.
	 * @return a string representation of the version of {@code cls}.
	 */

	public static String versionString(
		Class<?>			cls,
		ResourceProperties	properties)
	{
		// Test whether version has been disabled
		if (Boolean.getBoolean(SystemPropertyKey.NO_VERSION))
			return "";

		// Allocate buffer for string
		StringBuilder buffer = new StringBuilder(32);

		// Case: class is from JAR
		if (ClassUtils.isFromJar(cls))
		{
			// Append version number
			String str = properties.get(BuildPropertyKey.VERSION);
			if (str != null)
				buffer.append(str);

			// If this is not a release, append build
			boolean release = Boolean.parseBoolean(properties.get(BuildPropertyKey.RELEASE));
			if (!release)
			{
				str = properties.get(BuildPropertyKey.BUILD);
				if (str != null)
				{
					if (!buffer.isEmpty())
						buffer.append(' ');
					buffer.append(str);
				}
			}
		}

		// Case: class is not from JAR
		else
		{
			buffer.append('b');
			buffer.append(DateTimeFormatter.ofPattern(VERSION_DATE_TIME_PATTERN).format(LocalDateTime.now()));
		}

		// Return string representation of version
		return buffer.toString();
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
