/*====================================================================*\

PathUtils.java

Class: path-related utility methods.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.filesystem;

//----------------------------------------------------------------------


// IMPORTS


import java.io.File;

import java.nio.file.Path;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//----------------------------------------------------------------------


// CLASS: PATH-RELATED UTILITY METHODS


/**
 * This class contains utility methods that relate to {@linkplain Path file-system locations}.
 */

public class PathUtils
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private PathUtils()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Converts the specified file-system location to a normalised absolute location and returns the result.  The
	 * following methods are called sequentially on the input location:
	 * <ul>
	 *   <li>{@link Path#toAbsolutePath()}</li>
	 *   <li>{@link Path#normalize()}</li>
	 * </ul>
	 *
	 * @param  location
	 *           the location of interest.
	 * @return {@code location} converted to an absolute location and normalised.
	 */

	public static Path abs(
		Path	location)
	{
		return location.toAbsolutePath().normalize();
	}

	//------------------------------------------------------------------

	/**
	 * Converts the specified file-system location to a normalised absolute location and returns the parent of the
	 * resulting location.  The following methods are called sequentially on the input location:
	 * <ul>
	 *   <li>{@link Path#toAbsolutePath()}</li>
	 *   <li>{@link Path#normalize()}</li>
	 *   <li>{@link Path#getParent()}</li>
	 * </ul>
	 *
	 * @param  location
	 *           the location of interest.
	 * @return the parent of {@code location} after it is converted to an absolute location and normalised.
	 */

	public static Path absParent(
		Path	location)
	{
		return location.toAbsolutePath().normalize().getParent();
	}

	//------------------------------------------------------------------

	/**
	 * Converts the specified file-system location to a normalised absolute location and returns a string representation
	 * of the resulting location.  The following methods are called sequentially on the input location:
	 * <ul>
	 *   <li>{@link Path#toAbsolutePath()}</li>
	 *   <li>{@link Path#normalize()}</li>
	 *   <li>{@link Path#toString()}</li>
	 * </ul>
	 *
	 * @param  location
	 *           the location of interest.
	 * @return a string representation of {@code location} after it is converted to an absolute location and normalised.
	 */

	public static String absString(
		Path	location)
	{
		return location.toAbsolutePath().normalize().toString();
	}

	//------------------------------------------------------------------

	/**
	 * Converts the specified file-system location to a normalised absolute location and returns a string representation
	 * of the resulting location in which the system-dependent {@linkplain File#separatorChar separators} between
	 * adjacent components are replaced by Unix separators, {@code '/'}.  The following methods are called sequentially
	 * on the input location:
	 * <ul>
	 *   <li>{@link Path#toAbsolutePath()}</li>
	 *   <li>{@link Path#normalize()}</li>
	 *   <li>{@link Path#toString()}</li>
	 *   <li>{@link String#replace(char, char)}</li>
	 * </ul>
	 *
	 * @param  location
	 *           the location of interest.
	 * @return a string representation of {@code location} after it is converted to an absolute location and normalised.
	 *         In the string representation, the system-dependent separators between adjacent components are replaced by
	 *         Unix separators, {@code '/'}.
	 */

	public static String absStringStd(
		Path	location)
	{
		return location.toAbsolutePath().normalize().toString().replace(File.separatorChar, '/');
	}

	//------------------------------------------------------------------

	/**
	 * Concatenates the string representations of the specified locations, with adjacent locations separated by the
	 * system-dependent separator character, and returns the resulting string.
	 *
	 * @param  locations
	 *           the locations that will be concatenated.
	 * @return a concatenation of the string representations of {@code locations}, with adjacent locations separated by
	 *         the system-dependent separator character.
	 */

	public static String join(
		Path...	locations)
	{
		return join(List.of(locations));
	}

	//------------------------------------------------------------------

	/**
	 * Concatenates the string representations of the specified locations, with adjacent locations separated by the
	 * system-dependent separator character, and returns the resulting string.
	 *
	 * @param  locations
	 *           the locations that will be concatenated.
	 * @return a concatenation of the string representations of {@code locations}, with adjacent locations separated by
	 *         the system-dependent separator character.
	 */

	public static String join(
		Iterable<? extends Path>	locations)
	{
		StringBuilder buffer = new StringBuilder(256);
		Iterator<? extends Path> it = locations.iterator();
		while (it.hasNext())
		{
			buffer.append(it.next().toString());
			if (it.hasNext())
				buffer.append(File.pathSeparatorChar);
		}
		return buffer.toString();
	}

	//------------------------------------------------------------------

	/**
	 * Returns a list of {@link Path}s in which each element corresponds to an element of the specified input sequence
	 * of {@link File}s.
	 *
	 * @param  files
	 *           the locations that will be converted.
	 * @return a list of {@code Path}s in which each element corresponds to an element of the input sequence.
	 */

	public static List<Path> convertLocations(
		Iterable<? extends File>	files)
	{
		List<Path> paths = new ArrayList<>();
		for (File file : files)
			paths.add(file.toPath());
		return paths;
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
