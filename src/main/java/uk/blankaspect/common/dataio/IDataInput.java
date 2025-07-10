/*====================================================================*\

IDataInput.java

Interface: data input.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.dataio;

//----------------------------------------------------------------------


// INTERFACE: DATA INPUT


public interface IDataInput
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** Miscellaneous strings. */
	String	NULL_DATA_STR				= "Null data";
	String	OFFSET_OUT_OF_BOUNDS_STR	= "Offset out of bounds: ";
	String	LENGTH_OUT_OF_BOUNDS_STR	= "Length out of bounds: ";

////////////////////////////////////////////////////////////////////////
//  Methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the length of the data that is available from this input.
	 *
	 * @return the length of the data that is available from this input.
	 */

	long length();

	//------------------------------------------------------------------

	/**
	 * Resets this input.
	 */

	void reset();

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Enumerated types
////////////////////////////////////////////////////////////////////////


	// ENUMERATION: DATA-INPUT KIND


	public enum Kind
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		/**
		 * A stream of byte data.
		 */
		BYTE_STREAM,

		/**
		 * A source of byte data.
		 */
		BYTE_SOURCE,

		/**
		 * A stream of double-precision floating-point data.
		 */
		DOUBLE_STREAM,

		/**
		 * A source of double-precision floating-point data.
		 */
		DOUBLE_SOURCE;

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		/**
		 * Returns {@code true} if this kind of input is a stream or source of byte data.
		 *
		 * @return {@code true} if this kind of input is a stream or source of byte data.
		 */

		public boolean isByteInput()
		{
			return (this == BYTE_STREAM) || (this == BYTE_SOURCE);
		}

		//--------------------------------------------------------------

		/**
		 * Returns {@code true} if this kind of input is a stream or source of double-precision floating-point data.
		 *
		 * @return {@code true} if this kind of input is a stream or source of double-precision floating-point data.
		 */

		public boolean isDoubleInput()
		{
			return (this == DOUBLE_STREAM) || (this == DOUBLE_SOURCE);
		}

		//--------------------------------------------------------------

	}

	//==================================================================

}

//----------------------------------------------------------------------
