/*====================================================================*\

FileSystemUtils.java

Class: utility methods that relate to file systems.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.filesystem;

//----------------------------------------------------------------------


// IMPORTS


import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

import java.nio.file.attribute.DosFileAttributeView;

import java.text.DecimalFormat;

import java.time.LocalDateTime;

import java.time.format.DateTimeFormatter;

import java.util.Random;

import uk.blankaspect.common.exception2.UnexpectedRuntimeException;

//----------------------------------------------------------------------


// CLASS: UTILITY METHODS THAT RELATE TO FILE SYSTEMS


/**
 * This class contains utility methods that relate to file systems.
 */

public class FileSystemUtils
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The default interval in milliseconds between successive attempts to delete a file or directory. */
	private static final	int		DEFAULT_FILE_DELETION_INTERVAL	= 100;

	/** The size multiple of the <i>kibi-</i> prefix (2<sup>10</sup>). */
	private static final	long	KIBI_SIZE	= 1L << 10;

	/** The size multiple of the <i>mebi-</i> prefix (2<sup>20</sup>). */
	private static final	long	MEBI_SIZE	= 1L << 20;

	/** The size multiple of the <i>gibi-</i> prefix (2<sup>30</sup>). */
	private static final	long	GIBI_SIZE	= 1L << 30;

	/** The short number format of the size of a file. */
	private static final	DecimalFormat	SHORT_SIZE_FORMAT	= new DecimalFormat("0.0#");

	/** The long number format of the size of a file. */
	private static final	DecimalFormat	LONG_SIZE_FORMAT;

	/** The date/time pattern of the suffix of the name of a temporary time-based location. */
	private static final	String	TEMP_LOCATION_SUFFIX_PATTERN	= "yyyyMMdd-HHmmss";

	/** Miscellaneous strings. */
	private static final	String	BYTE_STR	= "byte";
	private static final	String	KIB_STR		= "KiB";
	private static final	String	MIB_STR		= "MiB";
	private static final	String	GIB_STR		= "GiB";
	private static final	String	UNKNOWN_STR	= "Unknown";

////////////////////////////////////////////////////////////////////////
//  Static initialiser
////////////////////////////////////////////////////////////////////////

	static
	{
		LONG_SIZE_FORMAT = new DecimalFormat();
		LONG_SIZE_FORMAT.setGroupingSize(3);
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private FileSystemUtils()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Repeatedly attempts to delete a file or directory at the specified location by calling {@link
	 * Files#deleteIfExists(Path)} until either the method succeeds (that is, it does not throw an exception) or the
	 * specified number of attempts have been made.
	 *
	 * @param  location
	 *           the location of the file or directory that will be deleted.
	 * @param  maxNumAttempts
	 *           the maximum number of attempts that will be made to delete the file or directory at {@code location}.
	 * @return {@code true} if the file or directory was deleted; {@code false} if it did not exist.
	 * @throws IOException
	 *           on failure to delete the file or directory, the exception that was thrown by the last call to {@link
	 *           Files#deleteIfExists(Path)}.
	 */

	public static boolean deleteWithRetries(
		Path	location,
		int		maxNumAttempts)
		throws IOException
	{
		return deleteWithRetries(location, maxNumAttempts, DEFAULT_FILE_DELETION_INTERVAL);
	}

	//------------------------------------------------------------------

	/**
	 * Repeatedly attempts to delete a file or directory at the specified location by calling {@link
	 * Files#deleteIfExists(Path)} until either the method succeeds (that is, it does not throw an exception) or the
	 * specified number of attempts have been made.
	 *
	 * @param  location
	 *           the location of the file or directory that will be deleted.
	 * @param  maxNumAttempts
	 *           the maximum number of attempts that will be made to delete the file or directory at {@code location}.
	 * @param  interval
	 *           the interval in milliseconds between successive attempts.
	 * @return {@code true} if the file or directory was deleted; {@code false} if it did not exist.
	 * @throws IOException
	 *           on failure to delete the file or directory, the exception that was thrown by the last call to {@link
	 *           Files#deleteIfExists(Path)}.
	 */

	public static boolean deleteWithRetries(
		Path	location,
		int		maxNumAttempts,
		int		interval)
		throws IOException
	{
		// Validate arguments
		if (location == null)
			throw new IllegalArgumentException("Null location");
		if (maxNumAttempts <= 0)
			throw new IllegalArgumentException("Maximum number of attempts out of bounds: " + maxNumAttempts);

		// Attempt to delete file or directory
		IOException exception = null;
		for (int i = 0; i < maxNumAttempts; i++)
		{
			// Case: first attempt
			//   Clear the DOS read-only attribute, which, if set, can impede the deletion of a file or directory on a
			//   FAT or exFAT file system
			if (i == 0)
			{
				try
				{
					DosFileAttributeView fileAttrs =
							Files.getFileAttributeView(location, DosFileAttributeView.class, LinkOption.NOFOLLOW_LINKS);
					if (fileAttrs != null)
						fileAttrs.setReadOnly(false);
				}
				catch (IOException e)
				{
					// ignore
				}
			}

			// Case: not the first attempt
			//   Delay before attempting to delete file or directory
			else
			{
				try
				{
					Thread.sleep(interval);
				}
				catch (InterruptedException e)
				{
					// ignore
				}
			}

			// Delete file or directory
			try
			{
				return Files.deleteIfExists(location);
			}
			catch (IOException e)
			{
				exception = e;
			}
		}

		// Throw any exception
		if (exception != null)
			throw exception;

		// Shouldn't happen
		throw new UnexpectedRuntimeException();
	}

	//------------------------------------------------------------------

	/**
	 * Returns a string representation of the specified file size.
	 *
	 * @param  size
	 *           the file size for which a string representation is desired.
	 * @return a string representation of {@code size}.
	 */

	public static String getSizeString(
		long	size)
	{
		// Create short-format string
		String shortStr = null;
		if (size >= KIBI_SIZE)
		{
			if (size < MEBI_SIZE)
				shortStr = SHORT_SIZE_FORMAT.format((double)size / (double)KIBI_SIZE) + " " + KIB_STR;
			else if (size < GIBI_SIZE)
				shortStr = SHORT_SIZE_FORMAT.format((double)size / (double)MEBI_SIZE) + " " + MIB_STR;
			else
				shortStr = SHORT_SIZE_FORMAT.format((double)size / (double)GIBI_SIZE) + " " + GIB_STR;
		}

		// Create long-format string
		String longStr = LONG_SIZE_FORMAT.format(size) + " " + BYTE_STR + ((size == 1) ? "" : "s");

		// Create compound text and return it
		return (shortStr == null) ? longStr : shortStr + " (" + longStr + ")";
	}

	//------------------------------------------------------------------

	/**
	 * Returns a string representation of the size of the specified file.
	 *
	 * @param  file
	 *           the file for whose size a string representation is desired.
	 * @return a string representation of the size of {@code file}.
	 */

	public static String getSizeString(
		Path	file)
	{
		if (file != null)
		{
			try
			{
				return getSizeString(Files.size(file));
			}
			catch (IOException e)
			{
				// ignore
			}
		}
		return UNKNOWN_STR;
	}

	//------------------------------------------------------------------

	/**
	 * Returns a file-system location in the specified directory that does not denote an existing file or directory.
	 * The name of a location consists of the specified prefix, a 15-character suffix that represents the current local
	 * date and time, and the specified suffix.  The method returns when either a location that does not denote an
	 * existing file or directory has been found or the specified number of attempts to find such a location have been
	 * made.  Since the interval between attempts is one second, this method should be used only if there is an
	 * expectation of early success.
	 *
	 * @param  directory
	 *           the parent directory of the location.
	 * @param  namePrefix
	 *           the prefix of the name of the location.
	 * @param  nameSuffix
	 *           the suffix of the name of the location.
	 * @param  maxNumAttempts
	 *           the maximum number of names that will be generated; ignored if zero or negative.
	 * @return a location in {@code directory} that does not denote an existing file or directory, or {@code null} if no
	 *         such location was found after {@code maxNumAttempts} names were generated.
	 */

	public static Path findAvailableLocationDateTime(
		Path	directory,
		String	namePrefix,
		String	nameSuffix,
		int		maxNumAttempts)
	{
		Path location = null;

		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(TEMP_LOCATION_SUFFIX_PATTERN);
		String prevSuffix = "";
		int numAttempts = 0;
		while (true)
		{
			// Create suffix from current date/time
			String suffix = dateTimeFormatter.format(LocalDateTime.now());

			// If suffix has not been tried before, test location
			if (!suffix.equals(prevSuffix))
			{
				// Get location in specified directory
				location = directory.resolve(namePrefix + suffix + nameSuffix);

				// Test whether location denotes existing file or directory
				if (Files.notExists(location, LinkOption.NOFOLLOW_LINKS))
					break;

				// Test for maximum number of attempts
				if ((maxNumAttempts > 0) && (++numAttempts >= maxNumAttempts))
				{
					location = null;
					break;
				}

				// Update previous suffix
				prevSuffix = suffix;
			}

			// Wait before next attempt
			try
			{
				Thread.sleep(500);
			}
			catch (InterruptedException e)
			{
				// ignore
			}
		}

		return location;
	}

	//------------------------------------------------------------------

	/**
	 * Returns a file-system location in the specified directory that does not denote an existing file or directory.
	 * The name of the location consists of the specified prefix, a suffix of the specified length, where each character
	 * is randomly selected from the 62-character set { 0..9, A..Z, a..z }, and the specified suffix.
	 *
	 * @param  directory
	 *           the parent directory of the location.
	 * @param  namePrefix
	 *           the prefix of the name of the location.
	 * @param  nameSuffix
	 *           the suffix of the name of the location.
	 * @param  suffixLength
	 *           the length of the randomly generated suffix.
	 * @param  maxNumAttempts
	 *           the maximum number of names that will be generated; ignored if zero or negative.
	 * @return a location in {@code directory} that does not denote an existing file or directory, or {@code null} if no
	 *         such location was found after {@code maxNumAttempts} names were generated.
	 */

	public static Path findAvailableLocationRandom(
		Path	directory,
		String	namePrefix,
		String	nameSuffix,
		int		suffixLength,
		int		maxNumAttempts)
	{
		Path location = null;

		Random prng = new Random();
		char[] chars = new char[suffixLength];
		int numAttempts = 0;
		while (true)
		{
			// Generate random suffix
			for (int i = 0; i < suffixLength; i++)
			{
				int value = prng.nextInt(62);
				chars[i] = (char)((value >= 36)
										? 'a' + value - 36
										: (value >= 10)
												? 'A' + value - 10
												: '0' + value);
			}

			// Get location in specified directory
			location = directory.resolve(namePrefix + new String(chars) + nameSuffix);

			// Test whether location denotes existing file or directory
			if (Files.notExists(location, LinkOption.NOFOLLOW_LINKS))
				break;

			// Test for maximum number of attempts
			if ((maxNumAttempts > 0) && (++numAttempts >= maxNumAttempts))
			{
				location = null;
				break;
			}
		}

		return location;
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
