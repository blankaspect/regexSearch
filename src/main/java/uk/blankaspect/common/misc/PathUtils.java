/*====================================================================*\

PathUtils.java

Class: path-related utility methods.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.misc;

//----------------------------------------------------------------------


// IMPORTS


import java.io.File;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;

import java.nio.file.attribute.BasicFileAttributes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import java.util.function.Predicate;

import uk.blankaspect.common.exception2.FileException;
import uk.blankaspect.common.exception2.OuterIOException;

//----------------------------------------------------------------------


// CLASS: PATH-RELATED UTILITY METHODS


/**
 * This class contains utility methods that relate to {@linkplain Path file-system locations}.
 */

public class PathUtils
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** Error messages. */
	private interface ErrorMsg
	{
		String	FAILED_TO_VISIT_FILE		= "The file or directory could not be processed.";
		String	ERROR_TRAVERSING_DIRECTORY	= "An error occurred when traversing the directory structure.";
	}

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
	 * Traverses the directory structure whose root is the specified directory, creates a list of the files and
	 * directories that are accepted by the specified filter and returns the resulting list.  The traversal of the
	 * directory structure is <i>depth-first</i>.
	 *
	 * @param  directory
	 *           the root of the directory structure that will be traversed.
	 * @param  filter
	 *           the filter that will be applied to the path descriptor of each file or directory that is encountered in
	 *           the traversal of the directory structure.
	 * @return a list of the path descriptors of the files and directories that are encountered in the traversal of the
	 *         directory structure and that are accepted by {@code filter}.
	 * @throws FileException
	 *           if an exception occurred when traversing the directory structure.
	 */

	public static List<Path> getLocations(
		Path			directory,
		Predicate<Path>	filter)
		throws FileException
	{
		// Initialise list of paths
		List<Path> paths = new ArrayList<>();

		// Traverse directory structure
		try
		{
			Files.walkFileTree(directory, new SimpleFileVisitor<>()
			{
				@Override
				public FileVisitResult visitFile(
					Path				file,
					BasicFileAttributes	attrs)
					throws IOException
				{
					// If file is accepted by filter, add it to list
					if (filter.test(file))
						paths.add(file);

					// Continue with traversal
					return FileVisitResult.CONTINUE;
				}

				//------------------------------------------------------

				@Override
				public FileVisitResult visitFileFailed(
					Path		file,
					IOException	exception)
					throws IOException
				{
					throw new OuterIOException(new FileException(ErrorMsg.FAILED_TO_VISIT_FILE, exception, file));
				}

				//------------------------------------------------------

				@Override
				public FileVisitResult postVisitDirectory(
					Path		directory,
					IOException	exception)
					throws IOException
				{
					// If an exception occurred, wrap the exception and throw the wrapper
					if (exception != null)
						throw new OuterIOException(new FileException(ErrorMsg.ERROR_TRAVERSING_DIRECTORY, exception,
																	 directory));

					// Continue with traversal
					return FileVisitResult.CONTINUE;
				}
			});
		}
		catch (IOException e)
		{
			// If cause was a file exception, throw it
			FileException.throwCause(e);

			// Throw exception
			throw new FileException(ErrorMsg.ERROR_TRAVERSING_DIRECTORY, e, directory);
		}

		// Return list of paths
		return paths;
	}

	//------------------------------------------------------------------

	/**
	 * Concatenates the string representations of the specified paths, with adjacent paths separated by the
	 * system-dependent path-separator character, and returns the resulting string.
	 *
	 * @param  paths
	 *           the paths that will be concatenated.
	 * @return a concatenation of the string representations of {@code paths}, with adjacent paths separated by the
	 *         system-dependent path-separator character.
	 */

	public static String joinPaths(
		Path...	paths)
	{
		return joinPaths(Arrays.asList(paths));
	}

	//------------------------------------------------------------------

	/**
	 * Concatenates the string representations of the specified paths, with adjacent paths separated by the
	 * system-dependent path-separator character, and returns the resulting string.
	 *
	 * @param  paths
	 *           the paths that will be concatenated.
	 * @return a concatenation of the string representations of {@code paths}, with adjacent paths separated by the
	 *         system-dependent path-separator character.
	 */

	public static String joinPaths(
		Iterable<? extends Path>	paths)
	{
		StringBuilder buffer = new StringBuilder(256);
		Iterator<? extends Path> it = paths.iterator();
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
