/*====================================================================*\

ChannelUtils.java

Class: channel-related utility methods.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.bytechannel;

//----------------------------------------------------------------------


// IMPORTS


import java.io.IOException;

import java.nio.ByteBuffer;

import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

//----------------------------------------------------------------------


// CLASS: CHANNEL-RELATED UTILITY METHODS


/**
 * This class contains utility methods that relate to byte channels.
 */

public class ChannelUtils
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** Miscellaneous strings. */
	private static final	String	PREMATURE_END_OF_CHANNEL_STR	= "The end of the channel was reached prematurely";

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private ChannelUtils()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Reads bytes from the specified channel and writes them to the specified buffer until the buffer is full.
	 *
	 * @param  channel
	 *           the channel from which bytes will be read.
	 * @param  buffer
	 *           the buffer to which bytes will be written.
	 * @throws IOException
	 *           if an error occurred when reading from the channel.
	 */

	public static void read(
		ReadableByteChannel	channel,
		byte[]				buffer)
		throws IOException
	{
		read(channel, buffer, 0, buffer.length);
	}

	//------------------------------------------------------------------

	/**
	 * Reads the specified number of bytes from the specified channel and writes them to the specified buffer starting
	 * at the specified offset.
	 *
	 * @param  channel
	 *           the channel from which bytes will be read.
	 * @param  buffer
	 *           the buffer to which bytes will be written.
	 * @param  offset
	 *           the offset to {@code buffer} at which the first byte will be written.
	 * @param  length
	 *           the number of bytes that will be read from {@code channel}.
	 * @throws IOException
	 *           if an error occurred when reading from the channel.
	 */

	public static void read(
		ReadableByteChannel	channel,
		byte[]				buffer,
		int					offset,
		int					length)
		throws IOException
	{
		// Validate arguments
		if (buffer == null)
			throw new IllegalArgumentException("Null buffer");
		if ((offset < 0) || (offset > buffer.length))
			throw new IllegalArgumentException("Offset out of bounds: " + offset);
		if ((length < 0) || (length > buffer.length - offset))
			throw new IllegalArgumentException("Length out of bounds: " + length);

		// Read from channel
		read(channel, ByteBuffer.wrap(buffer, offset, length));
	}

	//------------------------------------------------------------------

	/**
	 * Reads bytes from the specified channel and writes them to the specified buffer until the limit of the buffer is
	 * reached.
	 *
	 * @param  channel
	 *           the channel from which bytes will be read.
	 * @param  buffer
	 *           the buffer to which bytes will be written.
	 * @throws IOException
	 *           if an error occurred when reading from the channel.
	 */

	public static void read(
		ReadableByteChannel	channel,
		ByteBuffer			buffer)
		throws IOException
	{
		// Validate arguments
		if (channel == null)
			throw new IllegalArgumentException("Null channel");
		if (buffer == null)
			throw new IllegalArgumentException("Null buffer");

		// Read from channel until limit of buffer is reached
		while (buffer.hasRemaining())
		{
			if (channel.read(buffer) < 0)
				throw new IOException(PREMATURE_END_OF_CHANNEL_STR);
		}
	}

	//------------------------------------------------------------------

	/**
	 * Writes the specified byte data to the specified channel.
	 *
	 * @param  channel
	 *           the channel to which the data will be written.
	 * @param  data
	 *           the data that will be written to the channel.
	 * @throws IOException
	 *           if an error occurred when writing to the channel.
	 */

	public static void write(
		WritableByteChannel	channel,
		byte[]				buffer)
		throws IOException
	{
		write(channel, buffer, 0, buffer.length);
	}

	//------------------------------------------------------------------

	/**
	 * Writes the specified number of bytes of data to the specified channel.
	 *
	 * @param  channel
	 *           the channel to which the data will be written.
	 * @param  data
	 *           the array that contains the data that will be written to the channel.
	 * @param  offset
	 *           the offset to {@code data} at which the first byte will be read.
	 * @param  length
	 *           the number of bytes that will be written to {@code channel}.
	 * @throws IOException
	 *           if an error occurred when writing to the channel.
	 */

	public static void write(
		WritableByteChannel	channel,
		byte[]				data,
		int					offset,
		int					length)
		throws IOException
	{
		// Validate arguments
		if (data == null)
			throw new IllegalArgumentException("Null data");
		if ((offset < 0) || (offset > data.length))
			throw new IllegalArgumentException("Offset out of bounds: " + offset);
		if ((length < 0) || (length > data.length - offset))
			throw new IllegalArgumentException("Length out of bounds: " + length);

		// Write to channel
		write(channel, ByteBuffer.wrap(data, offset, length));
	}

	//------------------------------------------------------------------

	/**
	 * Writes the specified byte data to the specified channel.
	 *
	 * @param  channel
	 *           the channel to which the data will be written.
	 * @param  data
	 *           the data that will be written to the channel.
	 * @throws IOException
	 *           if an error occurred when writing to the channel.
	 */

	public static void write(
		WritableByteChannel	channel,
		ByteBuffer			data)
		throws IOException
	{
		// Validate arguments
		if (channel == null)
			throw new IllegalArgumentException("Null channel");
		if (data == null)
			throw new IllegalArgumentException("Null data");

		// Write to channel until all data has been written
		while (data.hasRemaining())
			channel.write(data);
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
