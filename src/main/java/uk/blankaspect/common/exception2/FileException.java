/*====================================================================*\

FileException.java

Class: file exception.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.exception2;

//----------------------------------------------------------------------


// IMPORTS


import java.nio.file.Path;

//----------------------------------------------------------------------


// CLASS: FILE EXCEPTION


/**
 * This class implements a checked exception that is associated with the location of a file or directory.
 */

public class FileException
	extends LocationException
{

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** The location with which this exception is associated. */
	private	Path	location;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of an exception with the specified detail message and associated location.
	 *
	 * @param message
	 *          the detail message of the exception.
	 * @param location
	 *          the location with which the exception will be associated, which may be {@code null}.
	 * @param replacements
	 *          the items whose string representations will replace placeholders in {@code message}.
	 */

	public FileException(
		String		message,
		Path		location,
		Object...	replacements)
	{
		// Call alternative constructor
		this(message, null, location, replacements);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of an exception with the specified detail message, cause and associated location.
	 *
	 * @param message
	 *          the detail message of the exception.
	 * @param cause
	 *          the underlying cause of the exception.
	 * @param location
	 *          the location with which the exception will be associated, which may be {@code null}.
	 * @param replacements
	 *          the items whose string representations will replace placeholders in {@code message}.
	 */

	public FileException(
		String		message,
		Throwable	cause,
		Path		location,
		Object...	replacements)
	{
		// Call superclass constructor
		super(message, cause, locationToString(location), replacements);

		// Initialise instance variables
		this.location = location;
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of an exception from the detail message and cause of the specified exception and the
	 * specified location.
	 *
	 * @param exception
	 *          the exception that will provide the detail message and underlying cause.
	 * @param location
	 *          the location with which the exception will be associated, which may be {@code null}.
	 * @param replacements
	 *          the items whose string representations will replace placeholders in {@code message}.
	 */

	public FileException(
		BaseException	exception,
		Path			location,
		Object...		replacements)
	{
		// Call alternative constructor
		this(exception.getMessage(), exception.getCause(), location, replacements);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a string representation of the specified location.
	 *
	 * @param  location
	 *           the location whose string representation is required, which may be {@code null}.
	 * @return a string representation of {@code location}.
	 */

	public static String locationToString(
		Path	location)
	{
		return (location == null) ? null : location.toAbsolutePath().normalize().toString();
	}

	//------------------------------------------------------------------

	/**
	 * Creates a composite message from the specified components, and returns the result.
	 *
	 * @param  message
	 *           the base message.
	 * @param  location
	 *           the location that will be prefixed to {@code message}, which may be {@code null}.
	 * @param  replacements
	 *          the items whose string representations will replace placeholders in {@code message}.
	 * @return a composite detail message created from {@code message}, {@code location} and {@code replacements}.
	 */

	public static String createMessage(
		String		message,
		Path		location,
		Object...	replacements)
	{
		return createMessage(message, locationToString(location), replacements);
	}

	//------------------------------------------------------------------

	/**
	 * Tests the cause of the specified exception, and throws the causal exception if it is a {@link FileException}.
	 *
	 * @param  exception
	 *           the exception of interest.
	 * @throws FileException
	 *           if the cause of {@code exception} is a {@code FileException}.
	 */

	public static void throwCause(
		Throwable	exception)
		throws FileException
	{
		throwCause(exception, false);
	}

	//------------------------------------------------------------------

	/**
	 * Tests the cause of the specified exception, or, optionally, searches the causal chain of the specified exception,
	 * and, if a {@link FileException} is found, throws it.
	 *
	 * @param  exception
	 *           the exception of interest.
	 * @param  traverse
	 *           if {@code true}, the causal chain of {@code exception} will be searched for a {@code FileException};
	 *           otherwise, only the immediate cause will be tested.
	 * @throws FileException
	 *           <ul>
	 *             <li>if {@code traverse} is {@code false} and the cause of {@code exception} is a {@code
	 *                 FileException}, or</li>
	 *             <li>if {@code traverse} is {@code true} and a {@code FileException} is found in the causal chain of
	 *                 {@code exception}.</li>
	 *           </ul>
	 */

	public static void throwCause(
		Throwable	exception,
		boolean		traverse)
		throws FileException
	{
		Throwable cause = exception.getCause();
		while (cause != null)
		{
			if (cause instanceof FileException)
				throw (FileException)cause;
			if (!traverse)
				break;
			cause = cause.getCause();
		}
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the location with which this exception is associated.
	 *
	 * @return the location with which this exception is associated.
	 */

	public Path getLocation()
	{
		return location;
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
