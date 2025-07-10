/*====================================================================*\

ValueOutOfBoundsException.java

Class: 'value out of bounds' exception.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.exception2;

//----------------------------------------------------------------------


// CLASS: 'VALUE OUT OF BOUNDS' EXCEPTION


public class ValueOutOfBoundsException
	extends RuntimeException
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public ValueOutOfBoundsException()
	{
	}

	//------------------------------------------------------------------

	public ValueOutOfBoundsException(
		String	message)
	{
		// Call superclass constructor
		super(message);
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
