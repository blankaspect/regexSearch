/*====================================================================*\

OuterIOException.java

Class: I/O exception that wraps another exception.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.exception2;

//----------------------------------------------------------------------


// IMPORTS


import java.io.IOException;

//----------------------------------------------------------------------


// CLASS: I/O EXCEPTION THAT WRAPS ANOTHER EXCEPTION


/**
 * This class is an extension of {@link IOException} whose purpose is to wrap another exception.
 */

public class OuterIOException
	extends IOException
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of an exception with the specified cause.
	 *
	 * @param cause
	 *          the underlying cause of the exception, which may be {@code null}.
	 */

	public OuterIOException(
		Throwable	cause)
	{
		// Call superclass constructor
		super(cause);
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

	public OuterIOException(
		String		message,
		Throwable	cause)
	{
		// Call superclass constructor
		super(message, cause);
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
