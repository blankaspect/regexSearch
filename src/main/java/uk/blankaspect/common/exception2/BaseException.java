/*====================================================================*\

BaseException.java

Class: base exception.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.exception2;

//----------------------------------------------------------------------


// CLASS: BASE EXCEPTION


/**
 * This class implements a base checked exception.
 */

public class BaseException
	extends Exception
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** An empty exception that may be tested for identity. */
	public static final	BaseException	EMPTY	= new BaseException();

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of an exception with no detail message.
	 */

	protected BaseException()
	{
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of an exception with the specified detail message and cause.
	 *
	 * @param message
	 *          the detail message of the exception.
	 * @param cause
	 *          the underlying cause of the exception, which may be {@code null}.
	 */

	public BaseException(
		String		message,
		Throwable	cause)
	{
		// Call superclass constructor
		super(message, cause);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of an exception with the specified detail message.
	 *
	 * @param message
	 *          the detail message of the exception.
	 * @param replacements
	 *          the items whose string representations will replace placeholders in {@code message}.
	 */

	public BaseException(
		String		message,
		Object...	replacements)
	{
		// Call superclass constructor
		super(createMessage(message, replacements));
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of an exception with the specified detail message and cause.
	 *
	 * @param message
	 *          the detail message of the exception.
	 * @param cause
	 *          the underlying cause of the exception, which may be {@code null}.
	 * @param replacements
	 *          the items whose string representations will replace placeholders in {@code message}.
	 */

	public BaseException(
		String		message,
		Throwable	cause,
		Object...	replacements)
	{
		// Call superclass constructor
		super(createMessage(message, replacements), cause);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a composite message from the specified message and replacement sequences, and returns the result.
	 *
	 * @param  message
	 *           the base message.
	 * @param  replacements
	 *           the items whose string representations will replace placeholders in {@code message}.
	 * @return the composite message that was created from {@code message} and {@code replacements}.
	 */

	public static String createMessage(
		String		message,
		Object...	replacements)
	{
		return (replacements.length > 0) ? String.format(message, replacements) : message;
	}

	//------------------------------------------------------------------

	/**
	 * Tests the cause of the specified exception, and throws the causal exception if it is a {@link BaseException}.
	 *
	 * @param  exception
	 *           the exception of interest.
	 * @throws BaseException
	 *           if the cause of {@code exception} is a {@code BaseException}.
	 */

	public static void throwCause(
		Throwable	exception)
		throws BaseException
	{
		throwCause(exception, false);
	}

	//------------------------------------------------------------------

	/**
	 * Tests the cause of the specified exception, or, optionally, searches the causal chain of the specified exception,
	 * and, if a {@link BaseException} is found, throws it.
	 *
	 * @param  exception
	 *           the exception of interest.
	 * @param  traverse
	 *           if {@code true}, the causal chain of {@code exception} will be searched for a {@code BaseException};
	 *           otherwise, only the immediate cause will be tested.
	 * @throws BaseException
	 *           <ul>
	 *             <li>if {@code traverse} is {@code false} and the cause of {@code exception} is a {@code
	 *                 BaseException}, or</li>
	 *             <li>if {@code traverse} is {@code true} and a {@code BaseException} is found in the causal chain of
	 *                 {@code exception}.</li>
	 *           </ul>
	 */

	public static void throwCause(
		Throwable	exception,
		boolean		traverse)
		throws BaseException
	{
		Throwable cause = exception.getCause();
		while (cause != null)
		{
			if (cause instanceof BaseException be)
				throw be;
			if (!traverse)
				break;
			cause = cause.getCause();
		}
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
