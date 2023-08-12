/*====================================================================*\

FilenameUtils.java

Class: filename-related utility methods.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.filesystem;

//----------------------------------------------------------------------


// IMPORTS


import java.io.File;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

//----------------------------------------------------------------------


// CLASS: FILENAME-RELATED UTILITY METHODS


/**
 * This class contains utility methods that relate to filenames.
 */

public class FilenameUtils
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The filename extension of a temporary file. */
	public static final	String	TEMPORARY_FILENAME_EXTENSION	= ".$tmp";

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private FilenameUtils()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Appends the specified extension to the filename of the specified file-system location if the filename does not
	 * already have an extension, and returns the resulting location.  The input location is returned unchanged if its
	 * filename already has an extension.
	 *
	 * @param  location
	 *           the file-system location to whose filename {@code extension} will be appended if the filename does not
	 *           already have an extension.
	 * @param  extension
	 *           the extension that will be appended to the filename of {@code location}.
	 * @return {@code location} with {@code extension} appended to it, if the filename of {@code location} does not
	 *         already have an extension; otherwise, {@code location}.
	 */

	public static Path appendFilenameExtension(
		Path	location,
		String	extension)
	{
		// Validate arguments
		if (location == null)
			throw new IllegalArgumentException("Null location");
		if (extension == null)
			throw new IllegalArgumentException("Null extension");
		if (extension.isBlank())
			throw new IllegalArgumentException("Invalid extension");

		// Ensure that extension starts with dot
		if (extension.charAt(0) != '0')
			extension = "." + extension;

		// Append extension to filename of target location if it does not already have one
		String filename = location.getFileName().toString();
		if (filename.indexOf('.') < 0)
		{
			filename += extension;
			location = location.resolveSibling(filename);
		}

		// Return location
		return location;
	}

	//------------------------------------------------------------------

	/**
	 * Returns a temporary file-system location that has the same parent directory as the specified location.
	 *
	 * @param  location
	 *           the file-system location for which a temporary location is sought.
	 * @return a temporary file-system location that has the same parent directory as {@code location}.
	 */

	public static File tempLocation(
		File	location)
	{
		// Get input name
		String inName = location.getName();

		// Get parent of input location
		File parent = location.getParentFile();

		// Find an output name that does not conflict with an existing name
		File outLocation = null;
		int index = 0;
		while (true)
		{
			String outName = inName + indexToTempSuffix(index);
			outLocation = (parent == null) ? new File(outName) : new File(parent, outName);
			if (!outLocation.exists())
				break;
			++index;
		}

		// Return output location
		return outLocation;
	}

	//------------------------------------------------------------------

	/**
	 * Returns a temporary file-system location that has the same parent directory as the specified location.
	 *
	 * @param  location
	 *           the file-system location for which a temporary location is sought.
	 * @return a temporary file-system location that has the same parent directory as {@code location}.
	 */

	public static Path tempLocation(
		Path	location)
	{
		// Get input name
		String inName = location.getFileName().toString();

		// Find an output name that does not conflict with an existing name
		Path outLocation = null;
		int index = 0;
		while (true)
		{
			String outName = inName + indexToTempSuffix(index);
			outLocation = location.resolveSibling(outName);
			if (!Files.exists(outLocation, LinkOption.NOFOLLOW_LINKS))
				break;
			++index;
		}

		// Return output location
		return outLocation;
	}

	//------------------------------------------------------------------

	/**
	 * Returns a temporary-filename suffix for the specified index.
	 *
	 * @param  index
	 *           the index for which a temporary-filename suffix is desired.
	 * @return a temporary-filename suffix for {@code index}.
	 */

	private static String indexToTempSuffix(
		int	index)
	{
		String prefix = "";
		String str = Integer.toString(index);
		switch (str.length())
		{
			case 1:
				prefix = "00";
				break;

			case 2:
				prefix = "0";
				break;
		}
		return "." + prefix + str + TEMPORARY_FILENAME_EXTENSION;
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
