/*====================================================================*\

NumberCodec.java

Class: methods for encoding and decoding numbers.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.number;

//----------------------------------------------------------------------


// CLASS: METHODS FOR ENCODING AND DECODING NUMBERS


/**
 * This class contains utility methods for encoding and decoding numbers.
 */

public class NumberCodec
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private NumberCodec()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Converts the specified signed integer to a big-endian sequence of bytes and stores the sequence in the specified
	 * buffer.
	 *
	 * @param value
	 *          the signed integer value that will be converted to a byte sequence.
	 * @param buffer
	 *          the buffer in which the byte sequence will be stored.
	 */

	public static void intToBytesBE(
		int		value,
		byte[]	buffer)
	{
		intToBytesBE(value, buffer, 0, buffer.length);
	}

	//------------------------------------------------------------------

	/**
	 * Converts the specified signed integer to a big-endian sequence of bytes of the specified length and stores the
	 * sequence in the specified buffer starting at the specified offset.
	 *
	 * @param value
	 *          the signed integer value that will be converted to a byte sequence.
	 * @param buffer
	 *          the buffer in which the byte sequence will be stored.
	 * @param offset
	 *          the offset of the start of the byte sequence in {@code buffer}.
	 * @param length
	 *          the length of the byte sequence.
	 */

	public static void intToBytesBE(
		int		value,
		byte[]	buffer,
		int		offset,
		int		length)
	{
		for (int i = offset + length - 1; i >= offset; i--)
		{
			buffer[i] = (byte)value;
			value >>= Byte.SIZE;
		}
	}

	//------------------------------------------------------------------

	/**
	 * Converts the specified signed integer to a little-endian sequence of bytes and stores the sequence in the
	 * specified buffer.
	 *
	 * @param value
	 *          the signed integer value that will be converted to a byte sequence.
	 * @param buffer
	 *          the buffer in which the byte sequence will be stored.
	 */

	public static void intToBytesLE(
		int		value,
		byte[]	buffer)
	{
		intToBytesLE(value, buffer, 0, buffer.length);
	}

	//------------------------------------------------------------------

	/**
	 * Converts the specified signed integer to a little-endian sequence of bytes of the specified length and stores the
	 * sequence in the specified buffer starting at the specified offset.
	 *
	 * @param value
	 *          the signed integer value that will be converted to a byte sequence.
	 * @param buffer
	 *          the buffer in which the byte sequence will be stored.
	 * @param offset
	 *          the offset of the start of the byte sequence in {@code buffer}.
	 * @param length
	 *          the length of the byte sequence.
	 */

	public static void intToBytesLE(
		int		value,
		byte[]	buffer,
		int		offset,
		int		length)
	{
		int endOffset = offset + length;
		for (int i = offset; i < endOffset; i++)
		{
			buffer[i] = (byte)value;
			value >>= Byte.SIZE;
		}
	}

	//------------------------------------------------------------------

	/**
	 * Converts the specified unsigned integer to a big-endian sequence of bytes and stores the sequence in the
	 * specified buffer.
	 *
	 * @param value
	 *          the unsigned integer value that will be converted to a byte sequence.
	 * @param buffer
	 *          the buffer in which the byte sequence will be stored.
	 */

	public static void uIntToBytesBE(
		int		value,
		byte[]	buffer)
	{
		uIntToBytesBE(value, buffer, 0, buffer.length);
	}

	//------------------------------------------------------------------

	/**
	 * Converts the specified unsigned integer to a big-endian sequence of bytes of the specified length and stores the
	 * sequence in the specified buffer starting at the specified offset.
	 *
	 * @param value
	 *          the unsigned integer value that will be converted to a byte sequence.
	 * @param buffer
	 *          the buffer in which the byte sequence will be stored.
	 * @param offset
	 *          the offset of the start of the byte sequence in {@code buffer}.
	 * @param length
	 *          the length of the byte sequence.
	 */

	public static void uIntToBytesBE(
		int		value,
		byte[]	buffer,
		int		offset,
		int		length)
	{
		for (int i = offset + length - 1; i >= offset; i--)
		{
			buffer[i] = (byte)value;
			value >>>= Byte.SIZE;
		}
	}

	//------------------------------------------------------------------

	/**
	 * Converts the specified unsigned integer to a little-endian sequence of bytes and stores the sequence in the
	 * specified buffer.
	 *
	 * @param value
	 *          the unsigned integer value that will be converted to a byte sequence.
	 * @param buffer
	 *          the buffer in which the byte sequence will be stored.
	 */

	public static void uIntToBytesLE(
		int		value,
		byte[]	buffer)
	{
		uIntToBytesLE(value, buffer, 0, buffer.length);
	}

	//------------------------------------------------------------------

	/**
	 * Converts the specified unsigned integer to a little-endian sequence of bytes of the specified length and stores
	 * the sequence in the specified buffer starting at the specified offset.
	 *
	 * @param value
	 *          the unsigned integer value that will be converted to a byte sequence.
	 * @param buffer
	 *          the buffer in which the byte sequence will be stored.
	 * @param offset
	 *          the offset of the start of the byte sequence in {@code buffer}.
	 * @param length
	 *          the length of the byte sequence.
	 */

	public static void uIntToBytesLE(
		int		value,
		byte[]	buffer,
		int		offset,
		int		length)
	{
		int endOffset = offset + length;
		for (int i = offset; i < endOffset; i++)
		{
			buffer[i] = (byte)value;
			value >>>= Byte.SIZE;
		}
	}

	//------------------------------------------------------------------

	/**
	 * Converts the specified big-endian sequence of bytes to a signed integer and returns the result.
	 *
	 * @param  data
	 *           the byte sequence.
	 * @return the signed integer that is the result of converting {@code data}.
	 */

	public static int bytesToIntBE(
		byte[]	data)
	{
		return bytesToIntBE(data, 0, data.length);
	}

	//------------------------------------------------------------------

	/**
	 * Converts the specified big-endian sequence of bytes to a signed integer and returns the result.
	 *
	 * @param  data
	 *           the array that contains the byte sequence.
	 * @param  offset
	 *           the offset to the start of the byte sequence in {@code data}.
	 * @param  length
	 *           the length of the byte sequence.
	 * @return the signed integer that is the result of converting the specified big-endian sequence of bytes.
	 */

	public static int bytesToIntBE(
		byte[]	data,
		int		offset,
		int		length)
	{
		int endOffset = offset + length;
		int value = data[offset];
		while (++offset < endOffset)
		{
			value <<= Byte.SIZE;
			value |= data[offset] & 0xFF;
		}
		return value;
	}

	//------------------------------------------------------------------

	/**
	 * Converts the specified little-endian sequence of bytes to a signed integer and returns the result.
	 *
	 * @param  data
	 *           the byte sequence.
	 * @return the signed integer that is the result of converting {@code data}.
	 */

	public static int bytesToIntLE(
		byte[]	data)
	{
		return bytesToIntLE(data, 0, data.length);
	}

	//------------------------------------------------------------------

	/**
	 * Converts the specified little-endian sequence of bytes to a signed integer and returns the result.
	 *
	 * @param  data
	 *           the array that contains the byte sequence.
	 * @param  offset
	 *           the offset to the start of the byte sequence in {@code data}.
	 * @param  length
	 *           the length of the byte sequence.
	 * @return the signed integer that is the result of converting the specified little-endian sequence of bytes.
	 */

	public static int bytesToIntLE(
		byte[]	data,
		int		offset,
		int		length)
	{
		int i = offset + length;
		int value = data[--i];
		while (--i >= offset)
		{
			value <<= Byte.SIZE;
			value |= data[i] & 0xFF;
		}
		return value;
	}

	//------------------------------------------------------------------

	/**
	 * Converts the specified big-endian sequence of bytes to an unsigned integer and returns the result.
	 *
	 * @param  data
	 *           the byte sequence.
	 * @return the unsigned integer that is the result of converting {@code data}.
	 */

	public static int bytesToUIntBE(
		byte[]	data)
	{
		return bytesToUIntBE(data, 0, data.length);
	}

	//------------------------------------------------------------------

	/**
	 * Converts the specified big-endian sequence of bytes to an unsigned integer and returns the result.
	 *
	 * @param  data
	 *           the array that contains the byte sequence.
	 * @param  offset
	 *           the offset to the start of the byte sequence in {@code data}.
	 * @param  length
	 *           the length of the byte sequence.
	 * @return the unsigned integer that is the result of converting the specified big-endian sequence of bytes.
	 */

	public static int bytesToUIntBE(
		byte[]	data,
		int		offset,
		int		length)
	{
		int endOffset = offset + length;
		int value = 0;
		while (offset < endOffset)
		{
			value <<= Byte.SIZE;
			value |= data[offset++] & 0xFF;
		}
		return value;
	}

	//------------------------------------------------------------------

	/**
	 * Converts the specified little-endian sequence of bytes to an unsigned integer and returns the result.
	 *
	 * @param  data
	 *           the byte sequence.
	 * @return the unsigned integer that is the result of converting {@code data}.
	 */

	public static int bytesToUIntLE(
		byte[]	data)
	{
		return bytesToUIntLE(data, 0, data.length);
	}

	//------------------------------------------------------------------

	/**
	 * Converts the specified little-endian sequence of bytes to an unsigned integer and returns the result.
	 *
	 * @param  data
	 *           the array that contains the byte sequence.
	 * @param  offset
	 *           the offset to the start of the byte sequence in {@code data}.
	 * @param  length
	 *           the length of the byte sequence.
	 * @return the unsigned integer that is the result of converting the specified little-endian sequence of bytes.
	 */

	public static int bytesToUIntLE(
		byte[]	data,
		int		offset,
		int		length)
	{
		int i = offset + length;
		int value = 0;
		while (--i >= offset)
		{
			value <<= Byte.SIZE;
			value |= data[i] & 0xFF;
		}
		return value;
	}

	//------------------------------------------------------------------

	/**
	 * Converts the specified signed long integer to a big-endian sequence of bytes and stores the sequence in the
	 * specified buffer.
	 *
	 * @param value
	 *          the signed long integer value that will be converted to a byte sequence.
	 * @param buffer
	 *          the buffer in which the byte sequence will be stored.
	 */

	public static void longToBytesBE(
		long	value,
		byte[]	buffer)
	{
		longToBytesBE(value, buffer, 0, buffer.length);
	}

	//------------------------------------------------------------------

	/**
	 * Converts the specified signed long integer to a big-endian sequence of bytes and stores the sequence in the
	 * specified buffer starting at the specified offset.
	 *
	 * @param value
	 *          the long integer value that will be converted to a byte sequence.
	 * @param buffer
	 *          the buffer in which the byte sequence will be stored.
	 * @param offset
	 *          the offset of the start of the byte sequence in {@code buffer}.
	 * @param length
	 *          the length of the byte sequence.
	 */

	public static void longToBytesBE(
		long	value,
		byte[]	buffer,
		int		offset,
		int		length)
	{
		for (int i = offset + length - 1; i >= offset; i--)
		{
			buffer[i] = (byte)value;
			value >>= Byte.SIZE;
		}
	}

	//------------------------------------------------------------------

	/**
	 * Converts the specified signed long integer to a little-endian sequence of bytes and stores the sequence in the
	 * specified buffer.
	 *
	 * @param value
	 *          the signed long integer value that will be converted to a byte sequence.
	 * @param buffer
	 *          the buffer in which the byte sequence will be stored.
	 */

	public static void longToBytesLE(
		long	value,
		byte[]	buffer)
	{
		longToBytesLE(value, buffer, 0, buffer.length);
	}

	//------------------------------------------------------------------

	/**
	 * Converts the specified signed long integer to a little-endian sequence of bytes and stores the sequence in the
	 * specified buffer starting at the specified offset.
	 *
	 * @param value
	 *          the long integer value that will be converted to a byte sequence.
	 * @param buffer
	 *          the buffer in which the byte sequence will be stored.
	 * @param offset
	 *          the offset of the start of the byte sequence in {@code buffer}.
	 * @param length
	 *          the length of the byte sequence.
	 */

	public static void longToBytesLE(
		long	value,
		byte[]	buffer,
		int		offset,
		int		length)
	{
		int endOffset = offset + length;
		for (int i = offset; i < endOffset; i++)
		{
			buffer[i] = (byte)value;
			value >>= Byte.SIZE;
		}
	}

	//------------------------------------------------------------------

	/**
	 * Converts the specified unsigned long integer to a big-endian sequence of bytes and stores the sequence in the
	 * specified buffer.
	 *
	 * @param value
	 *          the unsigned long integer value that will be converted to a byte sequence.
	 * @param buffer
	 *          the buffer in which the byte sequence will be stored.
	 */

	public static void uLongToBytesBE(
		long	value,
		byte[]	buffer)
	{
		uLongToBytesBE(value, buffer, 0, buffer.length);
	}

	//------------------------------------------------------------------

	/**
	 * Converts the specified unsigned long integer to a big-endian sequence of bytes and stores the sequence in the
	 * specified buffer starting at the specified offset.
	 *
	 * @param value
	 *          the unsigned long integer value that will be converted to a byte sequence.
	 * @param buffer
	 *          the buffer in which the byte sequence will be stored.
	 * @param offset
	 *          the offset of the start of the byte sequence in {@code buffer}.
	 * @param length
	 *          the length of the byte sequence.
	 */

	public static void uLongToBytesBE(
		long	value,
		byte[]	buffer,
		int		offset,
		int		length)
	{
		for (int i = offset + length - 1; i >= offset; i--)
		{
			buffer[i] = (byte)value;
			value >>>= Byte.SIZE;
		}
	}

	//------------------------------------------------------------------

	/**
	 * Converts the specified unsigned long integer to a little-endian sequence of bytes and stores the sequence in the
	 * specified buffer.
	 *
	 * @param value
	 *          the unsigned long integer value that will be converted to a byte sequence.
	 * @param buffer
	 *          the buffer in which the byte sequence will be stored.
	 */

	public static void uLongToBytesLE(
		long	value,
		byte[]	buffer)
	{
		uLongToBytesLE(value, buffer, 0, buffer.length);
	}

	//------------------------------------------------------------------

	/**
	 * Converts the specified unsigned long integer to a little-endian sequence of bytes and stores the sequence in the
	 * specified buffer starting at the specified offset.
	 *
	 * @param value
	 *          the unsigned long integer value that will be converted to a byte sequence.
	 * @param buffer
	 *          the buffer in which the byte sequence will be stored.
	 * @param offset
	 *          the offset of the start of the byte sequence in {@code buffer}.
	 * @param length
	 *          the length of the byte sequence.
	 */

	public static void uLongToBytesLE(
		long	value,
		byte[]	buffer,
		int		offset,
		int		length)
	{
		int endOffset = offset + length;
		for (int i = offset; i < endOffset; i++)
		{
			buffer[i] = (byte)value;
			value >>>= Byte.SIZE;
		}
	}

	//------------------------------------------------------------------

	/**
	 * Converts the specified big-endian sequence of bytes to a signed long integer and returns the result.
	 *
	 * @param  data
	 *           the byte sequence.
	 * @return the signed long integer that is the result of converting {@code data}.
	 */

	public static long bytesToLongBE(
		byte[]	data)
	{
		return bytesToLongBE(data, 0, data.length);
	}

	//------------------------------------------------------------------

	/**
	 * Converts the specified big-endian sequence of bytes to a signed long integer and returns the result.
	 *
	 * @param  data
	 *           the array that contains the byte sequence.
	 * @param  offset
	 *           the offset to the start of the byte sequence in {@code data}.
	 * @param  length
	 *           the length of the byte sequence.
	 * @return the signed long integer that is the result of converting the specified big-endian sequence of bytes.
	 */

	public static long bytesToLongBE(
		byte[]	data,
		int		offset,
		int		length)
	{
		int endOffset = offset + length;
		long value = data[offset];
		while (++offset < endOffset)
		{
			value <<= Byte.SIZE;
			value |= data[offset] & 0xFF;
		}
		return value;
	}

	//------------------------------------------------------------------

	/**
	 * Converts the specified little-endian sequence of bytes to a signed long integer and returns the result.
	 *
	 * @param  data
	 *           the byte sequence.
	 * @return the signed long integer that is the result of converting {@code data}.
	 */

	public static long bytesToLongLE(
		byte[]	data)
	{
		return bytesToLongLE(data, 0, data.length);
	}

	//------------------------------------------------------------------

	/**
	 * Converts the specified little-endian sequence of bytes to a signed long integer and returns the result.
	 *
	 * @param  data
	 *           the array that contains the byte sequence.
	 * @param  offset
	 *           the offset to the start of the byte sequence in {@code data}.
	 * @param  length
	 *           the length of the byte sequence.
	 * @return the signed long integer that is the result of converting the specified little-endian sequence of bytes.
	 */

	public static long bytesToLongLE(
		byte[]	data,
		int		offset,
		int		length)
	{
		int i = offset + length;
		long value = data[--i];
		while (--i >= offset)
		{
			value <<= Byte.SIZE;
			value |= data[i] & 0xFF;
		}
		return value;
	}

	//------------------------------------------------------------------

	/**
	 * Converts the specified big-endian sequence of bytes to an unsigned long integer and returns the result.
	 *
	 * @param  data
	 *           the byte sequence.
	 * @return the unsigned long integer that is the result of converting {@code data}.
	 */

	public static long bytesToULongBE(
		byte[]	data)
	{
		return bytesToULongBE(data, 0, data.length);
	}

	//------------------------------------------------------------------

	/**
	 * Converts the specified big-endian sequence of bytes to an unsigned long integer and returns the result.
	 *
	 * @param  data
	 *           the array that contains the byte sequence.
	 * @param  offset
	 *           the offset to the start of the byte sequence in {@code data}.
	 * @param  length
	 *           the length of the byte sequence.
	 * @return the unsigned long integer that is the result of converting the specified big-endian sequence of bytes.
	 */

	public static long bytesToULongBE(
		byte[]	data,
		int		offset,
		int		length)
	{
		int endOffset = offset + length;
		long value = 0;
		while (offset < endOffset)
		{
			value <<= Byte.SIZE;
			value |= data[offset++] & 0xFF;
		}
		return value;
	}

	//------------------------------------------------------------------

	/**
	 * Converts the specified little-endian sequence of bytes to an unsigned long integer and returns the result.
	 *
	 * @param  data
	 *           the byte sequence.
	 * @return the unsigned long integer that is the result of converting {@code data}.
	 */

	public static long bytesToULongLE(
		byte[]	data)
	{
		return bytesToULongLE(data, 0, data.length);
	}

	//------------------------------------------------------------------

	/**
	 * Converts the specified little-endian sequence of bytes to an unsigned long integer and returns the result.
	 *
	 * @param  data
	 *           the array that contains the byte sequence.
	 * @param  offset
	 *           the offset to the start of the byte sequence in {@code data}.
	 * @param  length
	 *           the length of the byte sequence.
	 * @return the unsigned long integer that is the result of converting the specified little-endian sequence of bytes.
	 */

	public static long bytesToULongLE(
		byte[]	data,
		int		offset,
		int		length)
	{
		int i = offset + length;
		long value = 0;
		while (--i >= offset)
		{
			value <<= Byte.SIZE;
			value |= data[i] & 0xFF;
		}
		return value;
	}

	//------------------------------------------------------------------

	/**
	 * Converts the specified single-precision floating-point number to a big-endian sequence of bytes and stores the
	 * sequence in the specified buffer starting at the specified offset.
	 *
	 * @param value
	 *          the single-precision floating-point number that will be converted to a byte sequence.
	 * @param buffer
	 *          the buffer in which the byte sequence will be stored.
	 * @param offset
	 *          the offset of the start of the byte sequence in {@code buffer}.
	 */

	public static void floatToBytesBE(
		float	value,
		byte[]	buffer,
		int		offset)
	{
		intToBytesBE(Float.floatToIntBits(value), buffer, offset, Float.BYTES);
	}

	//------------------------------------------------------------------

	/**
	 * Converts the specified big-endian sequence of bytes to a single-precision floating-point number and returns the
	 * result.
	 *
	 * @param  data
	 *           the array that contains the byte sequence.
	 * @param  offset
	 *           the offset to the start of the byte sequence in {@code data}.
	 * @return the single-precision floating-point number that is the result of converting the specified big-endian
	 *         sequence of bytes.
	 */
	public static float bytesToFloatBE(
		byte[]	data,
		int		offset)
	{
		return Float.intBitsToFloat(bytesToIntBE(data, offset, Float.BYTES));
	}

	//------------------------------------------------------------------

	/**
	 * Converts the specified double-precision floating-point number to a big-endian sequence of bytes and stores the
	 * sequence in the specified buffer.
	 *
	 * @param value
	 *          the double-precision floating-point number that will be converted to a byte sequence.
	 * @param buffer
	 *          the buffer in which the byte sequence will be stored.
	 */

	public static void doubleToBytesBE(
		double	value,
		byte[]	buffer)
	{
		doubleToBytesBE(value, buffer, 0);
	}

	//------------------------------------------------------------------

	/**
	 * Converts the specified double-precision floating-point number to a big-endian sequence of bytes and stores the
	 * sequence in the specified buffer starting at the specified offset.
	 *
	 * @param value
	 *          the double-precision floating-point number that will be converted to a byte sequence.
	 * @param buffer
	 *          the buffer in which the byte sequence will be stored.
	 * @param offset
	 *          the offset of the start of the byte sequence in {@code buffer}.
	 */

	public static void doubleToBytesBE(
		double	value,
		byte[]	buffer,
		int		offset)
	{
		longToBytesBE(Double.doubleToLongBits(value), buffer, offset, Double.BYTES);
	}

	//------------------------------------------------------------------

	/**
	 * Converts the specified double-precision floating-point number to a little-endian sequence of bytes and stores the
	 * sequence in the specified buffer.
	 *
	 * @param value
	 *          the double-precision floating-point number that will be converted to a byte sequence.
	 * @param buffer
	 *          the buffer in which the byte sequence will be stored.
	 */

	public static void doubleToBytesLE(
		double	value,
		byte[]	buffer)
	{
		doubleToBytesLE(value, buffer, 0);
	}

	//------------------------------------------------------------------

	/**
	 * Converts the specified double-precision floating-point number to a little-endian sequence of bytes and stores the
	 * sequence in the specified buffer starting at the specified offset.
	 *
	 * @param value
	 *          the double-precision floating-point number that will be converted to a byte sequence.
	 * @param buffer
	 *          the buffer in which the byte sequence will be stored.
	 * @param offset
	 *          the offset of the start of the byte sequence in {@code buffer}.
	 */

	public static void doubleToBytesLE(
		double	value,
		byte[]	buffer,
		int		offset)
	{
		longToBytesLE(Double.doubleToLongBits(value), buffer, offset, Double.BYTES);
	}

	//------------------------------------------------------------------

	/**
	 * Converts the specified big-endian sequence of bytes to a double-precision floating-point number and returns the
	 * result.
	 *
	 * @param  data
	 *           the byte sequence.
	 * @return the double-precision floating-point number that is the result of converting {@code data}.
	 */

	public static double bytesToDoubleBE(
		byte[]	data)
	{
		return bytesToDoubleBE(data, 0);
	}

	//------------------------------------------------------------------

	/**
	 * Converts the specified big-endian sequence of bytes to a double-precision floating-point number and returns the
	 * result.
	 *
	 * @param  data
	 *           the array that contains the byte sequence.
	 * @param  offset
	 *           the offset to the start of the byte sequence in {@code data}.
	 * @return the double-precision floating-point number that is the result of converting the specified big-endian
	 *         sequence of bytes.
	 */

	public static double bytesToDoubleBE(
		byte[]	data,
		int		offset)
	{
		return Double.longBitsToDouble(bytesToULongBE(data, offset, Double.BYTES));
	}

	//------------------------------------------------------------------

	/**
	 * Converts the specified little-endian sequence of bytes to a double-precision floating-point number and returns
	 * the result.
	 *
	 * @param  data
	 *           the byte sequence.
	 * @return the double-precision floating-point number that is the result of converting {@code data}.
	 */

	public static double bytesToDoubleLE(
		byte[]	data)
	{
		return bytesToDoubleLE(data, 0);
	}

	//------------------------------------------------------------------

	/**
	 * Converts the specified little-endian sequence of bytes to a double-precision floating-point number and returns
	 * the result.
	 *
	 * @param  data
	 *           the array that contains the byte sequence.
	 * @param  offset
	 *           the offset to the start of the byte sequence in {@code data}.
	 * @return the double-precision floating-point number that is the result of converting the specified little-endian
	 *         sequence of bytes.
	 */

	public static double bytesToDoubleLE(
		byte[]	data,
		int		offset)
	{
		return Double.longBitsToDouble(bytesToULongLE(data, offset, Double.BYTES));
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
