/*====================================================================*\

ResourceUtils.java

Class: resource-related utility methods.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.resource;

//----------------------------------------------------------------------


// IMPORTS


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;

import java.net.URL;
import java.net.URLConnection;

import java.nio.channels.FileChannel;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermissions;

import java.util.ArrayList;
import java.util.List;

import uk.blankaspect.common.bytechannel.ChannelUtils;

import uk.blankaspect.common.dataio.ByteDataList;

import uk.blankaspect.common.exception2.FileException;
import uk.blankaspect.common.exception2.LocationException;

import uk.blankaspect.common.filesystem.FilenameUtils;
import uk.blankaspect.common.filesystem.PathUtils;

//----------------------------------------------------------------------


// CLASS: RESOURCE-RELATED UTILITY METHODS


/**
 * This class contains utility methods that relate to resources.
 */

public class ResourceUtils
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The character that separates adjacent elements of the pathname of a resource. */
	public static final		char	PATHNAME_SEPARATOR_CHAR	= '/';

	/** The string that separates adjacent elements of the pathname of a resource. */
	public static final		String	PATHNAME_SEPARATOR		= Character.toString(PATHNAME_SEPARATOR_CHAR);

	/** The character that separates adjacent elements of the fully qualified name of a class. */
	private static final	char	CLASS_NAME_SEPARATOR_CHAR	= '.';

	/** The length of the buffer that is used by {@link #readBytes(Class, String)}. */
	private static final	int		READ_BUFFER_LENGTH	= 1 << 12;	// 4096

	/** The length of the buffer that is used by {@link #copyResource(Class, String, Path)}. */
	private static final	int		COPY_BUFFER_LENGTH	= 1 << 14;	// 16384

	/** Miscellaneous strings. */
	private static final	String	FILE_NOT_FOUND_STR	= "File was not found";
	private static final	String	FILE_TOO_LONG_STR	= "File is too long";

	/** Error messages. */
	private interface ErrorMsg
	{
		String	RESOURCE_NOT_FOUND_STR =
				"The resource was not found.";

		String	FAILED_TO_OPEN_FILE =
				"Failed to open the file.";

		String	FAILED_TO_CLOSE_FILE =
				"Failed to close the file.";

		String	FAILED_TO_LOCK_FILE =
				"Failed to lock the file.";

		String	FAILED_TO_READ_FILE_ATTRIBUTES =
				"Failed to read the attributes of the file.";

		String	FAILED_TO_CREATE_DIRECTORY =
				"Failed to create the directory.";

		String	FAILED_TO_CREATE_TEMPORARY_FILE =
				"Failed to create a temporary file.";

		String	FAILED_TO_DELETE_FILE =
				"Failed to delete the existing file.";

		String	FAILED_TO_RENAME_FILE =
				"Temporary file: %s\nFailed to rename the temporary file to the specified filename.";

		String	ERROR_READING_FILE =
				"An error occurred when reading the file.";

		String	ERROR_WRITING_FILE =
				"An error occurred when writing the file.";
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private ResourceUtils()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the pathname of the specified package, which is formed from the name of the package by replacing each
	 * occurrence of '.' with '/'.
	 *
	 * @param  pkg
	 *           the package whose pathname is desired.
	 * @return the pathname of {@code pkg}.
	 */

	public static String pathname(
		Package	pkg)
	{
		return pkg.getName().replace(CLASS_NAME_SEPARATOR_CHAR, PATHNAME_SEPARATOR_CHAR);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the pathname of the package that contains the specified class.  The pathname is formed from the name of
	 * the package by replacing each occurrence of '.' with '/'.
	 *
	 * @param  cls
	 *           the class for whose package the pathname is desired.
	 * @return the pathname of the package that contains {@code cls}.
	 */

	public static String packagePathname(
		Class<?>	cls)
	{
		return pathname(cls.getPackage());
	}

	//------------------------------------------------------------------

	/**
	 * Returns the normalised pathname of the resource of the specified name.  If the specified name is not absolute, it
	 * is resolved against the name of the specified package.
	 *
	 * @param  pkg
	 *           the package against whose name {@code name} will be resolved; ignored if {@code name} starts with '/'.
	 * @param  name
	 *           the name of the resource for which the absolute name is desired.  If {@code name} starts with '/', it
	 *           is deemed to be absolute and {@code pkg} is ignored.
	 * @return the normalised absolute pathname of the resource whose name is {@code name}, resolved against the name of
	 *         {@code pkg}, if {@code name} is not absolute.
	 */

	public static String normalisedPathname(
		Package		pkg,
		String		name)
	{
		// Create absolute pathname
		String path = name.startsWith(PATHNAME_SEPARATOR)
											? name
											: PATHNAME_SEPARATOR + pathname(pkg) + PATHNAME_SEPARATOR + name;

		// Split pathname into its elements
		String[] inElements = path.split(PATHNAME_SEPARATOR, -1);

		// Initialise list of elements of normalised pathname
		List<String> outElements = new ArrayList<>();

		// Normalise pathname
		for (String element : inElements)
		{
			if (element.equals("."))
				continue;
			if (element.equals(".."))
			{
				if (!outElements.isEmpty())
					outElements.remove(outElements.size() - 1);
			}
			else
				outElements.add(element);
		}

		// Concatenate elements of normalised pathname and return result.
		return String.join(PATHNAME_SEPARATOR, outElements);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the normalised pathname of the resource of the specified name.  If the specified name is not absolute, it
	 * is resolved against the name of the package that contains the specified class.
	 *
	 * @param  cls
	 *           the class against whose package {@code name} will be resolved; ignored if {@code name} starts with '/'.
	 * @param  name
	 *           the name of the resource for which the absolute name is desired.  If {@code name} starts with '/', it
	 *           is deemed to be absolute and {@code cls} is ignored.
	 * @return the normalised absolute pathname of the resource whose name is {@code name}, resolved against the package
	 *         that contains {@code cls}, if {@code name} is not absolute.
	 */

	public static String normalisedPathname(
		Class<?>	cls,
		String		name)
	{
		return normalisedPathname(cls.getPackage(), name);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the location of the resource with the specified name.
	 *
	 * @param  cls
	 *           the class with which the resource is associated.  If it is {@code null}, the system class loader will
	 *           be used to find the resource.
	 * @param  name
	 *           the name of the desired resource.
	 * @return the location of the resource whose name is {@code name}, or {@code null} if no such resource was found.
	 */

	public static URL findResource(
		Class<?>	cls,
		String		name)
	{
		return (cls == null) ? ClassLoader.getSystemResource(name) : cls.getResource(name);
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if there is a resource with the specified name.
	 *
	 * @param  cls
	 *           the class with which the resource is associated.  If it is {@code null}, the system class loader will
	 *           be used to find the resource.
	 * @param  name
	 *           the name of the resource of interest.
	 * @return {@code true} if there is a resource whose name is {@code name}.
	 */

	public static boolean hasResource(
		Class<?>	cls,
		String		name)
	{
		return (findResource(cls, name) != null);
	}

	//------------------------------------------------------------------

	/**
	 * Reads the resource with the specified name and returns it as a byte array.
	 *
	 * @param  cls
	 *           the class with which the resource is associated.  If it is {@code null}, the system class loader will
	 *           be used to open the resource.
	 * @param  name
	 *           the name of the resource.
	 * @return a byte array of the contents of the resource.
	 * @throws IOException
	 *           if an error occurs when reading the resource.
	 */

	public static byte[] readBytes(
		Class<?>	cls,
		String		name)
		throws IOException
	{
		// Validate arguments
		if (name == null)
			throw new IllegalArgumentException("Null name");

		// Open input stream on resource
		InputStream inStream =
				(cls == null) ? ClassLoader.getSystemResourceAsStream(name) : cls.getResourceAsStream(name);
		if (inStream == null)
			throw new IOException(FILE_NOT_FOUND_STR + ": " + name);

		// Initialise list of data blocks
		ByteDataList dataBlocks = new ByteDataList();

		// Read data from stream
		try
		{
			while (true)
			{
				// Allocate buffer for block
				byte[] buffer = new byte[READ_BUFFER_LENGTH];

				// Read block
				int blockLength = inStream.read(buffer);

				// Test for end of input stream
				if (blockLength < 0)
					break;

				// Add block to list
				if (blockLength > 0)
					dataBlocks.add(buffer, 0, blockLength);

				// Test cumulative length of data
				if (dataBlocks.length() > Integer.MAX_VALUE)
					throw new IOException(FILE_TOO_LONG_STR + ": " + name);
			}
		}
		finally
		{
			// Close input stream
			inStream.close();
		}

		// Return concatenated data blocks
		return dataBlocks.getData();
	}

	//------------------------------------------------------------------

	/**
	 * Reads the resource with the specified name and returns it as a string that is decoded from the contents of the
	 * resource with the UTF-8 character encoding.
	 *
	 * @param  cls
	 *           the class with which the resource is associated.  If it is {@code null}, the system class loader will
	 *           be used to open the resource.
	 * @param  name
	 *           the name of the desired resource.
	 * @throws IOException
	 *           if an error occurs when reading the resource.
	 */

	public static String readText(
		Class<?>	cls,
		String		name)
		throws IOException
	{
		return readText(cls, name, StandardCharsets.UTF_8);
	}

	//------------------------------------------------------------------

	/**
	 * Reads the resource with the specified name and returns it as a string that is decoded from the contents of the
	 * resource with the specified character encoding.
	 *
	 * @param  cls
	 *           the class with which the resource is associated.  If it is {@code null}, the system class loader will
	 *           be used to open the resource.
	 * @param  name
	 *           the name of the desired resource.
	 * @return encoding
	 *           the character encoding that will be used to decode the contents of the resource.
	 * @throws IOException
	 *           if an error occurs when reading the resource.
	 */

	public static String readText(
		Class<?>	cls,
		String		name,
		Charset		encoding)
		throws IOException
	{
		return new String(readBytes(cls, name), encoding);
	}

	//------------------------------------------------------------------

	/**
	 * Reads the resource with the specified name, which is expected to contain text, and returns its contents as a list
	 * of lines of text.  The UTF-8 character encoding will be used to decode the contents of the resource.
	 *
	 * @param  cls
	 *           the class with which the resource is associated.  If it is {@code null}, the system class loader will
	 *           be used to open the resource.
	 * @param  name
	 *           the name of the resource.
	 * @return a list of the lines of text that are the contents of the resource.
	 * @throws IOException
	 *           if an error occurs when reading the resource.
	 */

	public static List<String> readLines(
		Class<?>	cls,
		String		name)
		throws IOException
	{
		return readLines(cls, name, StandardCharsets.UTF_8);
	}

	//------------------------------------------------------------------

	/**
	 * Reads the resource with the specified name, which is expected to contain text, and returns its contents as a list
	 * of lines of text.  The specified character encoding will be used to decode the contents of the resource.
	 *
	 * @param  cls
	 *           the class with which the resource is associated.  If it is {@code null}, the system class loader will
	 *           be used to open the resource.
	 * @param  name
	 *           the name of the resource.
	 * @return encoding
	 *           the character encoding that will be used to decode the contents of the resource.
	 * @return a list of the lines of text that are the contents of the resource.
	 * @throws IOException
	 *           if an error occurs when reading the resource.
	 */

	public static List<String> readLines(
		Class<?>	cls,
		String		name,
		Charset		encoding)
		throws IOException
	{
		// Validate arguments
		if (name == null)
			throw new IllegalArgumentException("Null name");
		if (encoding == null)
			throw new IllegalArgumentException("Null encoding");

		// Open input stream on resource
		InputStream inStream =
				(cls == null) ? ClassLoader.getSystemResourceAsStream(name) : cls.getResourceAsStream(name);
		if (inStream == null)
			throw new IOException(FILE_NOT_FOUND_STR + ": " + name);

		// Wrap input stream in buffered reader
		BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, encoding));

		// Initialise list of lines of text
		List<String> lines = new ArrayList<>();

		// Read lines from stream
		try
		{
			while (true)
			{
				// Read line
				String line = reader.readLine();

				// Test for end of stream
				if (line == null)
					break;

				// Add line to list
				lines.add(line);
			}
		}
		finally
		{
			// Close input stream
			reader.close();
		}

		// Return lines of text
		return lines;
	}

	//------------------------------------------------------------------

	/**
	 * Reads the resource with the specified name and writes it to a file at the specified file-system location.
	 *
	 * @param  cls
	 *           the class with which the resource is associated.  If it is {@code null}, the system class loader will
	 *           be used to open the resource.
	 * @param  name
	 *           the name of the resource.
	 * @param  outFile
	 *           the file-system location to which the resource will be written.
	 * @throws LocationException
	 *           if an error occurs when reading the resource or writing the output file.
	 */

	public static void copyResource(
		Class<?>	cls,
		String		name,
		Path		outFile)
		throws LocationException
	{
		// Validate arguments
		if (name == null)
			throw new IllegalArgumentException("Null name");
		if (outFile == null)
			throw new IllegalArgumentException("Null output file");

		// Initialise variables
		InputStream inStream = null;
		FileChannel outChannel = null;
		Path tempFile = null;
		boolean oldFileDeleted = false;

		// Write file
		try
		{
			// Open input stream on resource
			inStream = (cls == null) ? ClassLoader.getSystemResourceAsStream(name) : cls.getResourceAsStream(name);
			if (inStream == null)
				throw new LocationException(ErrorMsg.RESOURCE_NOT_FOUND_STR, name);

			// Read file permissions of an existing file
			FileAttribute<?>[] attrs = {};
			if (Files.exists(outFile, LinkOption.NOFOLLOW_LINKS))
			{
				try
				{
					PosixFileAttributes posixAttrs = Files.readAttributes(outFile, PosixFileAttributes.class,
																		  LinkOption.NOFOLLOW_LINKS);
					attrs = new FileAttribute<?>[] { PosixFilePermissions.asFileAttribute(posixAttrs.permissions()) };
				}
				catch (UnsupportedOperationException e)
				{
					// ignore
				}
				catch (Exception e)
				{
					throw new FileException(ErrorMsg.FAILED_TO_READ_FILE_ATTRIBUTES, e, outFile);
				}
			}

			// Create parent directory
			Path directory = PathUtils.absParent(outFile);
			try
			{
				Files.createDirectories(directory);
			}
			catch (Exception e)
			{
				throw new FileException(ErrorMsg.FAILED_TO_CREATE_DIRECTORY, e, directory);
			}

			// Create temporary file
			try
			{
				tempFile = FilenameUtils.tempLocation(outFile);
				Files.createFile(tempFile, attrs);
			}
			catch (Exception e)
			{
				throw new FileException(ErrorMsg.FAILED_TO_CREATE_TEMPORARY_FILE, e, tempFile);
			}

			// Open channel for writing
			try
			{
				outChannel = FileChannel.open(tempFile, StandardOpenOption.WRITE);
			}
			catch (Exception e)
			{
				throw new FileException(ErrorMsg.FAILED_TO_OPEN_FILE, e, tempFile);
			}

			// Lock channel
			try
			{
				if (outChannel.tryLock() == null)
					throw new FileException(ErrorMsg.FAILED_TO_LOCK_FILE, tempFile);
			}
			catch (IOException e)
			{
				throw new FileException(ErrorMsg.FAILED_TO_LOCK_FILE, e, tempFile);
			}

			// Allocate buffer
			byte[] buffer = new byte[COPY_BUFFER_LENGTH];

			// Read resource and write output file
			int blockLength = 0;
			while (true)
			{
				// Read block from input stream
				try
				{
					// Read block
					blockLength = inStream.read(buffer);

					// Test for end of input stream
					if (blockLength < 0)
						break;
				}
				catch (Exception e)
				{
					throw new LocationException(ErrorMsg.ERROR_READING_FILE, e, name);
				}

				// Write block to output channel
				if (blockLength > 0)
				{
					try
					{
						ChannelUtils.write(outChannel, buffer, 0, blockLength);
					}
					catch (Exception e)
					{
						throw new FileException(ErrorMsg.ERROR_WRITING_FILE, e, tempFile);
					}
				}
			}

			// Close input stream
			try
			{
				inStream.close();
			}
			catch (Exception e)
			{
				throw new LocationException(ErrorMsg.FAILED_TO_CLOSE_FILE, e, name);
			}
			finally
			{
				inStream = null;
			}

			// Close output channel
			try
			{
				outChannel.close();
			}
			catch (Exception e)
			{
				throw new FileException(ErrorMsg.FAILED_TO_CLOSE_FILE, e, tempFile);
			}
			finally
			{
				outChannel = null;
			}

			// Delete any existing file
			try
			{
				Files.deleteIfExists(outFile);
				oldFileDeleted = true;
			}
			catch (Exception e)
			{
				throw new FileException(ErrorMsg.FAILED_TO_DELETE_FILE, e, outFile);
			}

			// Rename temporary file
			try
			{
				Files.move(tempFile, outFile, StandardCopyOption.ATOMIC_MOVE);
			}
			catch (Exception e)
			{
				throw new FileException(ErrorMsg.FAILED_TO_RENAME_FILE, e, outFile, PathUtils.abs(tempFile));
			}

			// Copy timestamp of resource to output file
			URL url = (cls == null) ? ClassLoader.getSystemResource(name) : cls.getResource(name);
			if (url != null)
			{
				URLConnection connection = null;
				try
				{
					connection = url.openConnection();
					long timestamp = connection.getLastModified();
					if (timestamp > 0)
						Files.setLastModifiedTime(outFile, FileTime.fromMillis(timestamp));
				}
				catch (Exception e)
				{
					// ignore
				}
				finally
				{
					if (connection != null)
					{
						try
						{
							connection.getInputStream().close();
						}
						catch (Exception e)
						{
							// ignore
						}
					}
				}
			}
		}
		catch (LocationException e)
		{
			// Close input stream
			if (inStream != null)
			{
				try
				{
					inStream.close();
				}
				catch (Exception e0)
				{
					// ignore
				}
			}

			// Close channel
			if (outChannel != null)
			{
				try
				{
					outChannel.close();
				}
				catch (Exception e0)
				{
					// ignore
				}
			}

			// Delete temporary file
			if (!oldFileDeleted && (tempFile != null))
			{
				try
				{
					Files.deleteIfExists(tempFile);
				}
				catch (Exception e0)
				{
					// ignore
				}
			}

			// Rethrow exception
			throw e;
		}
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
