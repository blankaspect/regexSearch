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
	 *           if an error occurred when extracting the data from the source.
	 */

	ByteData nextData()
		throws IOException;

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// CLASS: BYTE DATA


	/**
	 * This class encapsulates a block of byte data that is returned by a {@linkplain IByteDataSource#nextData() data
	 * source}.
	 */

	public static class ByteData
	{

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		/** The buffer that contains the data. */
		private	byte[]	buffer;

		/** The offset to the start of the data within {@link #buffer}. */
		private	int		offset;

		/** The length of the data. */
		private	int		length;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		/**
		 * Creates a new instance of a block of byte data that contains the specified data.  The instance does not copy
		 * the specified data; it keeps a reference to the array that is passed as an argument.
		 *
		 * @param data
		 *          the array that contains the byte data.
		 */

		public ByteData(
			byte[]	data)
		{
			// Validate arguments
			if (data == null)
				throw new IllegalArgumentException("Null data");

			// Initialise instance variables
			buffer = data;
			length = data.length;
		}

		//--------------------------------------------------------------

		/**
		 * Creates a new instance of a block of byte data that contains the specified data.  The instance does not copy
		 * the specified data; it keeps a reference to the array that is passed as an argument.
		 *
		 * @param data
		 *          the array that contains the byte data.
		 * @param offset
		 *          the offset to the start of the data within {@code data}.
		 * @param length
		 *          the length of the data.
		 */

		public ByteData(
			byte[]	data,
			int		offset,
			int		length)
		{
			// Validate arguments
			if (data == null)
				throw new IllegalArgumentException("Null data");
			if ((offset < 0) || (offset > data.length))
				throw new IllegalArgumentException("Offset out of bounds: " + offset);
			if ((length < 0) || (length > data.length - offset))
				throw new IllegalArgumentException("Length out of bounds: " + length);

			// Initialise instance variables
			buffer = data;
			this.offset = offset;
			this.length = length;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		/**
		 * Returns the array that contains the byte data.
		 *
		 * @return the array that contains the byte data.
		 */

		public byte[] getBuffer()
		{
			return buffer;
		}

		//--------------------------------------------------------------

		/**
		 * Returns the offset to the start of the byte data within the {@linkplain #getBuffer() buffer}.
		 *
		 * @return the offset to the start of the byte data within the buffer.
		 */

		public int getOffset()
		{
			return offset;
		}

		//--------------------------------------------------------------

		/**
		 * Returns the length of the byte data within the {@linkplain #getBuffer() buffer}.
		 *
		 * @return the length of the byte data within the buffer.
		 */

		public int getLength()
		{
			return length;
		}

		//--------------------------------------------------------------

	}

	//==================================================================

}

//----------------------------------------------------------------------
