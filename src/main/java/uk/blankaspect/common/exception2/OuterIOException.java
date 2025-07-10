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
	 * Creates a new instance of an I/O exception that wraps the specified exception.
	 *
	 * @param exception
	 *          the underlying exception, which may be {@code null}.
	 */

	public OuterIOException(
		Throwable	exception)
	{
		// Call superclass constructor
		super(exception);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of an I/O exception that wraps the specified exception.  The wrapping exception has the
	 * specified detail message.
	 *
	 * @param message
	 *          the detail message of the exception.
	 * @param exception
	 *          the underlying exception, which may be {@code null}.
	 */

	public OuterIOException(
		String		message,
		Throwable	exception)
	{
		// Call superclass constructor
		super(message, exception);
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
