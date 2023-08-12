/*====================================================================*\

UnexpectedRuntimeException.java

Class: unexpected runtime exception.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.exception;

//----------------------------------------------------------------------


// CLASS: UNEXPECTED RUNTIME EXCEPTION


public class UnexpectedRuntimeException
	extends RuntimeException
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public UnexpectedRuntimeException()
	{
	}

	//------------------------------------------------------------------

	public UnexpectedRuntimeException(
		String	message)
	{
		// Call superclass constructor
		super(message);
	}

	//------------------------------------------------------------------

	public UnexpectedRuntimeException(
		Throwable	cause)
	{
		// Call superclass constructor
		super(cause);
	}

	//------------------------------------------------------------------

	public UnexpectedRuntimeException(
		String		message,
		Throwable	cause)
	{
		// Call superclass constructor
		super(message, cause);
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
