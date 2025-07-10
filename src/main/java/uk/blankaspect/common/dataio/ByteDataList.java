/*====================================================================*\

ByteDataList.java

Class: list of byte-data blocks.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.dataio;

//----------------------------------------------------------------------


// IMPORTS


import java.nio.ByteBuffer;

import java.util.ArrayList;
import java.util.List;

//----------------------------------------------------------------------


// CLASS: LIST OF BYTE-DATA BLOCKS


/**
 * This class implements a list of blocks of byte data.
 */

public class ByteDataList
	implements IByteDataSource
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** Miscellaneous strings. */
	private static final	String	NULL_BUFFER_STR	= "Null buffer";

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** A list of blocks of byte data. */
	private	List<ByteData>	dataBlocks;

	/** The index of the element of {@link #dataBlocks} that will be returned by the next call to {@link
		#nextData()}. */
	private	int				outIndex;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of a list of blocks of byte data.
	 */

	public ByteDataList()
	{
		// Initialise instance variables
		dataBlocks = new ArrayList<>();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of a list of blocks of byte data, adds the specified block of data to the list, and
	 * returns the list
	 *
	 * @param  data
	 *           the block of byte data that will be added to this list.
	 * @return a new instance of a list of blocks of byte data that contains the specified block as its only element.
	 */

	public static ByteDataList of(
		byte[]	data)
	{
		// Create list
		ByteDataList list = new ByteDataList();

		// Add block to list
		list.add(data);

		// Return list
		return list;
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a list of blocks of byte data, adds the specified block of data to the list, and
	 * returns the list
	 *
	 * @param  data
	 *           the array that contains the block of byte data.
	 * @param  offset
	 *           the offset to the start of the block of byte data within {@code data}.
	 * @param  length
	 *           the length of the block of byte data.
	 * @return a new instance of a list of blocks of byte data that contains the specified block as its only element.
	 */

	public static ByteDataList of(
		byte[]	data,
		int		offset,
		int		length)
	{
		// Create list
		ByteDataList list = new ByteDataList();

		// Add block to list
		list.add(data, offset, length);

		// Return list
		return list;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : IByteDataSource interface
////////////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 */

	@Override
	public long length()
	{
		long length = 0;
		for (ByteData dataBlock : dataBlocks)
			length += dataBlock.length();
		return length;
	}

	//------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */

	@Override
	public void reset()
	{
		outIndex = 0;
	}

	//------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */

	@Override
	public ByteData nextData()
	{
		return (outIndex < dataBlocks.size()) ? dataBlocks.get(outIndex++) : null;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the element at the specified index in this list.
	 *
	 * @param  index
	 *           the index of the desired element.
	 * @return the element at {@code index} in this list.
	 * @throws IndexOutOfBoundsException
	 *           if {@code index} is negative or {@code index} is greater than or equal to the length of this list.
	 */

	public byte get(
		long	index)
	{
		long startIndex = 0;
		long endIndex = 0;
		int numBlocks = dataBlocks.size();
		for (int i = 0; i < numBlocks; i++)
		{
			ByteData block = dataBlocks.get(i);
			endIndex = startIndex + block.length();
			if (index < endIndex)
				return block.buffer()[block.offset() + (int)(index - startIndex)];
			startIndex = endIndex;
		}
		throw new IndexOutOfBoundsException(index);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the number of blocks of byte data in this list.
	 *
	 * @return the number of blocks of byte data in this list.
	 */

	public int getNumBlocks()
	{
		return dataBlocks.size();
	}

	//------------------------------------------------------------------

	/**
	 * Returns the block of byte data at the specified index in this list.
	 *
	 * @param  index
	 *           the index of the desired block of byte data.
	 * @return the block of byte data at {@code index} in this list.
	 */

	public ByteData getBlock(
		int	index)
	{
		return dataBlocks.get(index);
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if this list is empty.
	 *
	 * @return {@code true} if this list is empty.
	 */

	public boolean isEmpty()
	{
		return dataBlocks.isEmpty();
	}

	//------------------------------------------------------------------

	/**
	 * Adds the specified block of byte data to this list.
	 *
	 * @param data
	 *          the block of byte data that will be added to this list.
	 */

	public void add(
		byte[]	data)
	{
		// Validate arguments
		if (data == null)
			throw new IllegalArgumentException(NULL_DATA_STR);

		// Add block to list
		if (data.length > 0)
			dataBlocks.add(ByteData.of(data));
	}

	//------------------------------------------------------------------

	/**
	 * Adds the specified block of byte data to this list.
	 *
	 * @param data
	 *          the array that contains the block of byte data.
	 * @param offset
	 *          the offset to the start of the block of byte data within {@code data}.
	 * @param length
	 *          the length of the block of byte data.
	 */

	public void add(
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

		// Add block to list
		if (length > 0)
			dataBlocks.add(new ByteData(data, offset, length));
	}

	//------------------------------------------------------------------

	/**
	 * Creates a copy of the specified block of byte data and adds the copy to this list.
	 *
	 * @param data
	 *          the block of byte data, a copy of which will be added to this list.
	 */

	public void addCopy(
		byte[]	data)
	{
		// Validate arguments
		if (data == null)
			throw new IllegalArgumentException(NULL_DATA_STR);

		// Add block to list
		if (data.length > 0)
			dataBlocks.add(ByteData.of(data.clone()));
	}

	//------------------------------------------------------------------

	/**
	 * Creates a copy of the specified block of byte data and adds the copy to this list.
	 *
	 * @param data
	 *          the array that contains the source block of byte data.
	 * @param offset
	 *          the offset to the start of the source block of byte data within {@code data}.
	 * @param length
	 *          the length of the block of byte data.
	 */

	public void addCopy(
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

		// Add block to list
		if (length > 0)
		{
			byte[] copy = new byte[length];
			System.arraycopy(data, offset, copy, 0, length);
			dataBlocks.add(ByteData.of(copy));
		}
	}

	//------------------------------------------------------------------

	/**
	 * Concatenates the blocks of byte data of this list and returns the result in a newly allocated array.
	 *
	 * @return an array containing the concatenation of the blocks of byte data of this list.
	 */

	public byte[] getData()
	{
		// Test length of data
		long length = length();
		if (length > Integer.MAX_VALUE)
			throw new IllegalStateException("Data is too long");

		// Allocate buffer for data
		byte[] buffer = new byte[(int)length];

		// Concatenate data in buffer
		getData(buffer, 0, buffer.length);

		// Return data
		return buffer;
	}

	//------------------------------------------------------------------

	/**
	 * Concatenates the blocks of byte data of this list in the specified buffer and returns the length of the
	 * concatenated data, which is the smaller of the total length of the blocks and the length of the buffer.
	 *
	 * @param  buffer
	 *           the array in which the blocks of byte data of this list will be concatenated.
	 * @return the length of the concatenated data: the smaller of the total length of the blocks of this list and the
	 *         length of {@code buffer}.
	 */

	public int getData(
		byte[]	buffer)
	{
		// Validate arguments
		if (buffer == null)
			throw new IllegalArgumentException(NULL_BUFFER_STR);

		// Concatenate data blocks in buffer and return length of resulting data
		return getData(buffer, 0, buffer.length);
	}

	//------------------------------------------------------------------

	/**
	 * Concatenates the blocks of byte data of this list in the specified buffer and returns the length of the
	 * concatenated data, which is the smaller of the total length of the blocks and the specified length.
	 *
	 * @param  buffer
	 *           the array in which the blocks of byte data of this list will be concatenated.
	 * @param  offset
	 *           the offset to the start of the concatenated byte data within {@code buffer}.
	 * @param  length
	 *           the maximum length of the concatenated byte data.
	 * @return the length of the concatenated data: the smaller of the total length of the blocks of this list and
	 *         {@code length}.
	 */

	public int getData(
		byte[]	buffer,
		int		offset,
		int		length)
	{
		// Validate arguments
		if (buffer == null)
			throw new IllegalArgumentException(NULL_BUFFER_STR);
		if ((offset < 0) || (offset > buffer.length))
			throw new IllegalArgumentException(OFFSET_OUT_OF_BOUNDS_STR + offset);
		if ((length < 0) || (length > buffer.length - offset))
			throw new IllegalArgumentException(LENGTH_OUT_OF_BOUNDS_STR + length);

		// Concatenate data blocks in buffer
		int startOffset = offset;
		int endOffset = offset + length;
		for (ByteData dataBlock : dataBlocks)
		{
			if (offset >= endOffset)
				break;
			int blockLength = Math.min(dataBlock.length(), endOffset - offset);
			System.arraycopy(dataBlock.buffer(), dataBlock.offset(), buffer, offset, blockLength);
			offset += blockLength;
		}

		// Return length of data
		return offset - startOffset;
	}

	//------------------------------------------------------------------

	/**
	 * Concatenates the blocks of byte data of this list in the specified buffer and returns the length of the
	 * concatenated data, which is the smaller of the total length of the blocks and the length of the buffer.
	 *
	 * @param  buffer
	 *           the array in which the blocks of byte data of this list will be concatenated.
	 * @return the length of the concatenated data: the smaller of the total length of the blocks of this list and the
	 *         length of {@code buffer}.
	 */

	public int getData(
		ByteBuffer	buffer)
	{
		// Validate arguments
		if (buffer == null)
			throw new IllegalArgumentException(NULL_BUFFER_STR);

		// Add data blocks to buffer
		int offset = 0;
		for (ByteData dataBlock : dataBlocks)
		{
			int blockLength = dataBlock.length();
			buffer.put(dataBlock.buffer(), dataBlock.offset(), blockLength);
			offset += blockLength;
		}

		// Return length of data
		return offset;
	}

	//------------------------------------------------------------------

	/**
	 * Removes all the blocks from this list.
	 */

	public void clear()
	{
		dataBlocks.clear();
		outIndex = 0;
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
