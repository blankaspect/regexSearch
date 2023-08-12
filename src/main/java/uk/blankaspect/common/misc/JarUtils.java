/*====================================================================*\

JarUtils.java

Class: JAR-related utility methods.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.misc;

//----------------------------------------------------------------------


// IMPORTS


import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;

import java.util.List;

import uk.blankaspect.common.exception2.FileException;
import uk.blankaspect.common.exception2.LocationException;
import uk.blankaspect.common.exception2.UrlException;

import uk.blankaspect.common.filesystem.PathnameUtils;

//----------------------------------------------------------------------


// CLASS: JAR-RELATED UTILITY METHODS


/**
 * This class contains utility methods that relate to Java archives (JARs).
 */

public class JarUtils
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The filename extension of a JAR file. */
	private static final	String	JAR_FILENAME_EXTENSION	= ".jar";

	/** The prefix of a JAR-scheme URL. */
	private static final	String	JAR_URL_PREFIX	= "jar:";

	/** The separator between the pathname of the JAR file and the name of an entry in the URL of the JAR file. */
	private static final	String	JAR_URL_SEPARATOR	= "!/";

	/** The prefix of a file-scheme URI. */
	private static final	String	FILE_SCHEME_PREFIX	= "file:";

	/** Error messages. */
	private interface ErrorMsg
	{
		String	MALFORMED_PATHNAME	= "The pathname is malformed.";
		String	INVALID_LOCATION	= "The location is invalid.";
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private JarUtils()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a list of the JAR files that are found during a traversal of the directory structure whose root is the
	 * specified directory.
	 *
	 * @param  directory
	 *           the root of the directory structure that will be traversed.
	 * @return a list of the path descriptors of the JAR files that are found in a traversal of the directory structure.
	 * @throws FileException
	 *           if an exception occurred when traversing the directory structure.
	 */

	public static List<Path> getJars(
		Path	directory)
		throws FileException
	{
		return PathUtils.getLocations(directory, path ->
				Files.isRegularFile(path) && PathnameUtils.suffixMatches(path, JAR_FILENAME_EXTENSION));
	}

	//------------------------------------------------------------------

	/**
	 * Converts the specified file-system location to a JAR-scheme URL and returns the result.
	 *
	 * @param  location
	 *           the file-system location that will be converted to a JAR-scheme URL.
	 * @return the result of converting {@code location} to a JAR-scheme URL.
	 * @throws FileException
	 *           if the location, when converted to a JAR-scheme URL, is not well-formed.
	 */

	public static URL pathToUrl(
		Path	location)
		throws FileException
	{
		try
		{
			return new URI(JAR_URL_PREFIX + location.toUri() + JAR_URL_SEPARATOR).toURL();
		}
		catch (URISyntaxException | MalformedURLException e)
		{
			throw new FileException(ErrorMsg.MALFORMED_PATHNAME, location);
		}
	}

	//------------------------------------------------------------------

	/**
	 * Converts the specified URL, which is expected to have a JAR-scheme prefix and a JAR-entry suffix, to a
	 * file-system location and returns the result.
	 *
	 * @param  url
	 *           the JAR-scheme URL that will be converted to a file-system location.
	 * @return the result of converting {@code url} to a file-system location.
	 * @throws UrlException
	 *           if the URL does not yield a valid file-system location.
	 */

	public static Path urlToPath(
		URL	url)
		throws LocationException
	{
		// If the URL denotes an entry in a JAR file, extract the location of the JAR file from the URL
		String urlStr = url.toString();
		if (urlStr.startsWith(JAR_URL_PREFIX))
		{
			int index = urlStr.indexOf(JAR_URL_SEPARATOR);
			urlStr = (index < 0) ? null : urlStr.substring(JAR_URL_PREFIX.length(), index);
		}

		// Initialise file-system location
		Path location = null;

		// If the URL (or the 'inner' URL of a JAR entry) starts with a file-scheme prefix, convert the URL to a URI and
		// thence to a file-system location
		if ((urlStr != null) && urlStr.startsWith(FILE_SCHEME_PREFIX))
		{
			try
			{
				location = Path.of(new URI(urlStr));
			}
			catch (URISyntaxException | InvalidPathException e)
			{
				throw new LocationException(ErrorMsg.INVALID_LOCATION, urlStr);
			}
		}

		// Return file-system location
		return location;
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
