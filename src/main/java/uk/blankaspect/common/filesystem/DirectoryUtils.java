/*====================================================================*\

DirectoryUtils.java

Class: directory-related utility methods.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.filesystem;

//----------------------------------------------------------------------


// IMPORTS


import java.io.IOException;

import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.FileVisitResult;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;

import java.nio.file.attribute.BasicFileAttributes;

import java.util.ArrayList;
import java.util.List;

import java.util.function.Predicate;

import uk.blankaspect.common.exception2.FileException;
import uk.blankaspect.common.exception2.OuterIOException;

//----------------------------------------------------------------------


// CLASS: DIRECTORY-RELATED UTILITY METHODS


/**
 * This class contains utility methods that relate to directories.
 */

public class DirectoryUtils
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** A predicate that is {@code true} if a specified location is a regular file. */
	public static final		Predicate<Path>	IS_REGULAR_FILE	= location ->
			Files.isRegularFile(location, LinkOption.NOFOLLOW_LINKS);

	/** A predicate that is {@code true} if a specified location is a directory. */
	public static final		Predicate<Path>	IS_DIRECTORY	= location ->
			Files.isDirectory(location, LinkOption.NOFOLLOW_LINKS);

	/** The number of attempts that will be made to delete a file or directory. */
	private static final	int		NUM_DELETION_ATTEMPTS	= 3;

	/** Miscellaneous strings. */
	private static final	String	NULL_LOCATION_STR	= "Null location";

	/** Error messages. */
	private interface ErrorMsg
	{
		String	NOT_A_DIRECTORY				= "The location does not denote a directory.";
		String	FAILED_TO_CREATE_DIRECTORY	= "Failed to create the directory.";
		String	FAILED_TO_DELETE_FILE		= "Failed to delete the file.";
		String	FAILED_TO_DELETE_DIRECTORY	= "Failed to delete the directory.";
		String	FAILED_TO_COPY_FILE			= "Failed to copy the file.";
		String	ERROR_LISTING_DIRECTORY		= "An error occurred when listing the entries of the directory.";
		String	ERROR_TRAVERSING_DIRECTORY	= "An error occurred when traversing the directory structure.";
		String	FAILED_TO_VISIT_FILE		= "The file or directory could not be processed.";
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private DirectoryUtils()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a list of the entries of the directory at the specified location.
	 *
	 * @param  directory
	 *           the location of the directory for which a list of entries is required.
	 * @return a list of the entries of {@code directory}.
	 * @throws FileException
	 *           if an error occurs when listing the directory entries.
	 */

	public static List<Path> listEntries(
		Path	directory)
		throws FileException
	{
		// Validate arguments
		if (directory == null)
			throw new IllegalArgumentException(NULL_LOCATION_STR);

		// Initialise list of entries
		List<Path> entries = new ArrayList<>();

		// Create list of entries
		if (Files.exists(directory, LinkOption.NOFOLLOW_LINKS))
		{
			// Test for directory
			if (!Files.isDirectory(directory, LinkOption.NOFOLLOW_LINKS))
				throw new FileException(ErrorMsg.NOT_A_DIRECTORY, directory);

			// Add entries to list
			try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory))
			{
				for (Path location : stream)
					entries.add(location);
			}
			catch (DirectoryIteratorException e)
			{
				throw new FileException(ErrorMsg.ERROR_LISTING_DIRECTORY, e.getCause(), directory);
			}
			catch (Exception e)
			{
				throw new FileException(ErrorMsg.ERROR_LISTING_DIRECTORY, e, directory);
			}
		}

		// Return list of entries
		return entries;
	}

	//------------------------------------------------------------------

	/**
	 * Returns a list of the regular files in the directory at the specified location.
	 *
	 * @param  directory
	 *           the location of the directory for which a list of files is required.
	 * @return a list of the regular files in {@code directory}.
	 * @throws FileException
	 *           if an error occurs when listing the directory entries.
	 */

	public static List<Path> listFiles(
		Path	directory)
		throws FileException
	{
		return listFiles(directory, null);
	}

	//------------------------------------------------------------------

	/**
	 * Returns a list of the regular files in the directory at the specified location after applying the specified
	 * filter to the location of each file.
	 *
	 * @param  directory
	 *           the location of the directory for which a list of files is required.
	 * @param  filter
	 *           the filter that will be applied to the location of each file.  If it is {@code null}, all files will be
	 *           accepted.
	 * @return a list of the regular files in {@code directory} after applying {@code filter} to the location of each
	 *         file.
	 * @throws FileException
	 *           if an error occurs when listing the directory entries.
	 */

	public static List<Path> listFiles(
		Path			directory,
		Predicate<Path>	filter)
		throws FileException
	{
		// Validate arguments
		if (directory == null)
			throw new IllegalArgumentException(NULL_LOCATION_STR);

		// Initialise list of files
		List<Path> files = new ArrayList<>();

		// Create list of files
		if (Files.exists(directory, LinkOption.NOFOLLOW_LINKS))
		{
			// Test for directory
			if (!Files.isDirectory(directory, LinkOption.NOFOLLOW_LINKS))
				throw new FileException(ErrorMsg.NOT_A_DIRECTORY, directory);

			// Add files to list
			try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory, location ->
					Files.isRegularFile(location, LinkOption.NOFOLLOW_LINKS)
							&& ((filter == null) || filter.test(location))))
			{
				for (Path file : stream)
					files.add(file);
			}
			catch (DirectoryIteratorException e)
			{
				throw new FileException(ErrorMsg.ERROR_LISTING_DIRECTORY, e.getCause(), directory);
			}
			catch (Exception e)
			{
				throw new FileException(ErrorMsg.ERROR_LISTING_DIRECTORY, e, directory);
			}
		}

		// Return list of files
		return files;
	}

	//------------------------------------------------------------------

	/**
	 * Returns a list of the subdirectories of the directory at the specified location.
	 *
	 * @param  directory
	 *           the location of the directory for which a list of subdirectories is required.
	 * @return a list of the subdirectories of {@code directory}.
	 * @throws FileException
	 *           if an error occurs when listing the directory entries.
	 */

	public static List<Path> listDirectories(
		Path	directory)
		throws FileException
	{
		return listDirectories(directory, null);
	}

	//------------------------------------------------------------------

	/**
	 * Returns a list of the subdirectories of the directory at the specified location after applying the specified
	 * filter to the location of each subdirectory.
	 *
	 * @param  directory
	 *           the location of the directory for which a list of subdirectories is required.
	 * @param  filter
	 *           the filter that will be applied to the location of each subdirectory.  If it is {@code null}, all
	 *           subdirectories will be accepted.
	 * @return a list of the subdirectories of {@code directory} after applying {@code filter} to the location of each
	 *         subdirectory.
	 * @throws FileException
	 *           if an error occurs when listing the directory entries.
	 */

	public static List<Path> listDirectories(
		Path			directory,
		Predicate<Path>	filter)
		throws FileException
	{
		// Validate arguments
		if (directory == null)
			throw new IllegalArgumentException(NULL_LOCATION_STR);

		// Initialise list of subdirectories
		List<Path> directories = new ArrayList<>();

		// Create list of subdirectories
		if (Files.exists(directory, LinkOption.NOFOLLOW_LINKS))
		{
			// Test for directory
			if (!Files.isDirectory(directory, LinkOption.NOFOLLOW_LINKS))
				throw new FileException(ErrorMsg.NOT_A_DIRECTORY, directory);

			// Add subdirectories to list
			try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory, location ->
					Files.isDirectory(location, LinkOption.NOFOLLOW_LINKS)
							&& ((filter == null) || filter.test(location))))
			{
				for (Path dir : stream)
					directories.add(dir);
			}
			catch (DirectoryIteratorException e)
			{
				throw new FileException(ErrorMsg.ERROR_LISTING_DIRECTORY, e.getCause(), directory);
			}
			catch (Exception e)
			{
				throw new FileException(ErrorMsg.ERROR_LISTING_DIRECTORY, e, directory);
			}
		}

		// Return list of subdirectories
		return directories;
	}

	//------------------------------------------------------------------

	/**
	 * Traverses the directory structure whose root is the specified directory, creates a list of the files that are
	 * accepted by the specified filter and returns the resulting list.  The traversal of the directory structure is
	 * <i>depth-first</i>.
	 *
	 * @param  directory
	 *           the root of the directory structure that will be traversed.
	 * @param  filter
	 *           the filter that will be applied to the location of each file that is encountered in the traversal of
	 *           the directory structure.
	 * @return a list of the locations of the files that are encountered in the traversal of the directory structure and
	 *         that are accepted by {@code filter}.
	 * @throws FileException
	 *           if an exception occurred when traversing the directory structure.
	 */

	public static List<Path> findFiles(
		Path			directory,
		Predicate<Path>	filter)
		throws FileException
	{
		return findLocations(directory, IS_REGULAR_FILE.and(filter));
	}

	//------------------------------------------------------------------

	/**
	 * Traverses the directory structure whose root is the specified directory, creates a list of the directories that
	 * are accepted by the specified filter and returns the resulting list.  The traversal of the directory structure is
	 * <i>depth-first</i>.
	 *
	 * @param  directory
	 *           the root of the directory structure that will be traversed.
	 * @param  filter
	 *           the filter that will be applied to the location of each directory that is encountered in the traversal
	 *           of the directory structure.
	 * @return a list of the locations of the directories that are encountered in the traversal of the directory
	 *         structure and that are accepted by {@code filter}.
	 * @throws FileException
	 *           if an exception occurred when traversing the directory structure.
	 */

	public static List<Path> findDirectories(
		Path			directory,
		Predicate<Path>	filter)
		throws FileException
	{
		return findLocations(directory, IS_DIRECTORY.and(filter));
	}

	//------------------------------------------------------------------

	/**
	 * Traverses the directory structure whose root is the specified directory, creates a list of the files and
	 * directories that are accepted by the specified filter and returns the resulting list.  The traversal of the
	 * directory structure is <i>depth-first</i>.
	 *
	 * @param  directory
	 *           the root of the directory structure that will be traversed.
	 * @param  filter
	 *           the filter that will be applied to the location of each file or directory that is encountered in the
	 *           traversal of the directory structure.
	 * @return a list of the locations of the files and directories that are encountered in the traversal of the
	 *         directory structure and that are accepted by {@code filter}.
	 * @throws FileException
	 *           if an exception occurred when traversing the directory structure.
	 */

	public static List<Path> findLocations(
		Path			directory,
		Predicate<Path>	filter)
		throws FileException
	{
		// Initialise list of locations
		List<Path> locations = new ArrayList<>();

		// Traverse directory structure
		try
		{
			Files.walkFileTree(directory, new SimpleFileVisitor<>()
			{
				@Override
				public FileVisitResult preVisitDirectory(
					Path				directory,
					BasicFileAttributes	attrs)
					throws IOException
				{
					// If location of directory is accepted by filter, add it to list
					if (filter.test(directory))
						locations.add(directory);

					// Continue with traversal
					return FileVisitResult.CONTINUE;
				}

				//------------------------------------------------------

				@Override
				public FileVisitResult visitFile(
					Path				file,
					BasicFileAttributes	attrs)
					throws IOException
				{
					// If location of file is accepted by filter, add it to list
					if (filter.test(file))
						locations.add(file);

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
					// If an exception was thrown when traversing the directory, rethrow it
					if (exception != null)
						throw exception;

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

		// Return list of locations
		return locations;
	}

	//------------------------------------------------------------------

	/**
	 * Recursively deletes the directory at the specified location.
	 *
	 * @param  location
	 *           the location of the directory that will be deleted.
	 * @throws FileException
	 *           if an error occurs when traversing the directory structure or when deleting a file or directory.
	 */

	public static void deleteDirectory(
		Path	location)
		throws FileException
	{
		deleteDirectory(location, false);
	}

	//------------------------------------------------------------------

	/**
	 * Recursively deletes the contents of the directory at the specified location and, optionally, deletes the
	 * directory itself.
	 *
	 * @param  directory
	 *           the location of the directory that will be deleted.
	 * @param  contentsOnly
	 *           if {@code true}, only the contents of the directory will be deleted; the directory itself will not be
	 *           deleted.
	 * @throws FileException
	 *           if an error occurs when traversing the directory structure or when deleting a file or directory.
	 */

	public static void deleteDirectory(
		Path	directory,
		boolean	contentsOnly)
		throws FileException
	{
		// Validate arguments
		if (directory == null)
			throw new IllegalArgumentException(NULL_LOCATION_STR);

		// Test for directory
		if (!Files.exists(directory, LinkOption.NOFOLLOW_LINKS))
			return;
		if (!Files.isDirectory(directory, LinkOption.NOFOLLOW_LINKS))
			throw new FileException(ErrorMsg.NOT_A_DIRECTORY, directory);

		// Delete directory
		try
		{
			Files.walkFileTree(directory, new SimpleFileVisitor<>()
			{
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
				{
					// Delete file
					try
					{
						FileSystemUtils.deleteWithRetries(file, NUM_DELETION_ATTEMPTS);
					}
					catch (IOException e)
					{
						throw new OuterIOException(new FileException(ErrorMsg.FAILED_TO_DELETE_FILE, e, file));
					}

					// Continue walking the tree
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException exception) throws IOException
				{
					// If an exception was thrown when traversing the directory, rethrow it
					if (exception != null)
						throw exception;

					// Delete directory
					if (!contentsOnly || !Files.isSameFile(dir, directory))
					{
						try
						{
							FileSystemUtils.deleteWithRetries(dir, NUM_DELETION_ATTEMPTS);
						}
						catch (IOException e)
						{
							throw new OuterIOException(new FileException(ErrorMsg.FAILED_TO_DELETE_DIRECTORY, e, dir));
						}
					}

					// Continue walking the tree
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
	}

	//------------------------------------------------------------------

	/**
	 * Copies the directory from the specified source location to the specified destination location.
	 *
	 * @param  srcDirectory
	 *           the location of the source directory.
	 * @param  destDirectory
	 *           the location of the destination directory.
	 * @throws FileException
	 *           if an error occurs when copying the directory.
	 */

	public static void copyDirectory(
		Path	srcDirectory,
		Path	destDirectory)
		throws FileException
	{
		// Test for source directory
		if (!Files.isDirectory(srcDirectory, LinkOption.NOFOLLOW_LINKS))
			throw new FileException(ErrorMsg.NOT_A_DIRECTORY, srcDirectory);

		// If destination exists, test that it's a directory ...
		if (Files.exists(destDirectory, LinkOption.NOFOLLOW_LINKS))
		{
			if (!Files.isDirectory(destDirectory, LinkOption.NOFOLLOW_LINKS))
				throw new FileException(ErrorMsg.NOT_A_DIRECTORY, destDirectory);
		}

		// ... otherwise, create destination directory and any missing ancestors
		else
		{
			try
			{
				Files.createDirectories(destDirectory);
			}
			catch (IOException e)
			{
				throw new FileException(ErrorMsg.FAILED_TO_CREATE_DIRECTORY, destDirectory);
			}
		}

		// Copy files
		for (Path file : listFiles(srcDirectory))
		{
			try
			{
				Files.copy(file, destDirectory.resolve(file.getFileName()), StandardCopyOption.REPLACE_EXISTING,
						   StandardCopyOption.COPY_ATTRIBUTES);
			}
			catch (IOException e)
			{
				throw new FileException(ErrorMsg.FAILED_TO_COPY_FILE, e, file);
			}
		}

		// Copy subdirectories
		for (Path directory : listDirectories(srcDirectory))
			copyDirectory(directory, destDirectory.resolve(directory).getFileName());
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
