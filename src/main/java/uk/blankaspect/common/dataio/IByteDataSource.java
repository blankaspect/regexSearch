/*====================================================================*\

IByteDataSource.java

Interface: byte-data source.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.dataio;

//----------------------------------------------------------------------


// IMPORTS


import java.io.IOException;

//----------------------------------------------------------------------


// INTERFACE: BYTE-DATA SOURCE


/**
 * This interface defines the methods that must be implemented by a source of byte data.  Data is extracted from a
 * source by calling its {@link #nextData()} method until it returns {@code null}.
 */

public interface IByteDataSource
	extends IDataInput
{

////////////////////////////////////////////////////////////////////////
//  Methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the next available block of byte data.
	 *
	 * @return the next available block of byte data, or {@code null} if no more data is available.
	 * @throws IOException
	 *           if an error occurs when extracting the data from the source.
	 */

	ByteData nextData()
		throws IOException;

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Member records
////////////////////////////////////////////////////////////////////////


	// RECORD: BYTE DATA


	/**
	 * This record encapsulates a block of byte data that is returned by a {@linkplain IByteDataSource#nextData() data
	 * source}.
	 *
	 * @param buffer
	 *          the buffer that contains the byte data.
	 * @param offset
	 *          the offset to the start of the data within {@code buffer}.
	 * @param length
	 *          the length of the data.
	 */

	record ByteData(
		byte[]	buffer,
		int		offset,
		int		length)
	{

	////////////////////////////////////////////////////////////////////
	//  Class methods
	////////////////////////////////////////////////////////////////////

		/**
		 * Creates and returns a new instance of a block of byte data that contains the specified data.  The instance
		 * does not copy the specified data; it keeps a reference to the array that is passed as an argument.
		 *
		 * @param  data
		 *           the array that contains the byte data.
		 * @return a new instance of a block of byte data.
		 */

		public static ByteData of(
			byte[]	data)
		{
			// Validate arguments
			if (data == null)
				throw new IllegalArgumentException(NULL_DATA_STR);

			// Create data block and return it
			return new ByteData(data, 0, data.length);
		}

		//--------------------------------------------------------------

		/**
		 * Creates and returns a new instance of a block of byte data that contains the specified data.  The instance
		 * does not copy the specified data; it keeps a reference to the array that is passed as an argument.
		 *
		 * @param  data
		 *           the array that contains the byte data.
		 * @param  offset
		 *           the offset to the start of the data within {@code data}.
		 * @param  length
		 *           the length of the data.
		 * @return a new instance of a block of byte data.
		 */

		public static ByteData of(
			byte[]	data,
			int		offset,
			int		length)
		{
			// Validate arguments
			if (data == null)
				throw new IllegalArgumentException(NULL_DATA_STR);
			if ((offset < 0) || (offset > data.length))
				throw new IllegalArgumentException(OFFSET_OUT_OF_BOUNDS_STR + offset);
			if ((length < 0) || (length > data.length - offset))
				throw new IllegalArgumentException(LENGTH_OUT_OF_BOUNDS_STR + length);

			// Create data block and return it
			return new ByteData(data, offset, length);
		}

		//--------------------------------------------------------------

	}

	//==================================================================

}

//----------------------------------------------------------------------
