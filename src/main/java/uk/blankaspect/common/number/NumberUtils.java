/*====================================================================*\

NumberUtils.java

Class: number-related utility methods.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.number;

//----------------------------------------------------------------------


// IMPORTS


import java.math.BigInteger;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import java.util.Arrays;
import java.util.Locale;

import uk.blankaspect.common.exception.ValueOutOfBoundsException;

//----------------------------------------------------------------------


// CLASS: NUMBER-RELATED UTILITY METHODS


public class NumberUtils
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** Upper-case digits. */
	public static final		char[]	DIGITS_UPPER	=
	{
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
		'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
	};

	/** Lower-case digits. */
	public static final		char[]	DIGITS_LOWER	=
	{
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
		'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
	};

	/** Powers of ten, {@code int}. */
	public static final		int[]	POWERS_OF_TEN_INT;

	/** Powers of ten, {@code long}. */
	public static final		long[]	POWERS_OF_TEN_LONG;

	/** Formatter for converting a floating-point number to a string without an exponent. */
	public static final		DecimalFormat	FP_NO_EXP_FORMATTER;

	/** A mask for handling an unsigned long value as a {@link BigInteger}. */
	private static final	BigInteger	ULONG_MASK;

	public enum DigitCase
	{
		UPPER,
		LOWER
	}

	private static final	String	OUTPUT_TEXT_TOO_LONG_STR	= "The output text is too long for a string";

////////////////////////////////////////////////////////////////////////
//  Class variables
////////////////////////////////////////////////////////////////////////

	private static	char[]	digits	= DIGITS_UPPER;

////////////////////////////////////////////////////////////////////////
//  Static initialiser
////////////////////////////////////////////////////////////////////////

	static
	{
		// Initialise powers of ten, int
		POWERS_OF_TEN_INT = new int[10];
		int intValue = 1;
		for (int i = 0; i < POWERS_OF_TEN_INT.length; i++)
		{
			POWERS_OF_TEN_INT[i] = intValue;
			intValue *= 10;
		}

		// Initialise powers of ten, long
		POWERS_OF_TEN_LONG = new long[19];
		long longValue = 1;
		for (int i = 0; i < POWERS_OF_TEN_LONG.length; i++)
		{
			POWERS_OF_TEN_LONG[i] = longValue;
			longValue *= 10;
		}

		// Initialise no-exponent floating-point formatter
		FP_NO_EXP_FORMATTER = new DecimalFormat("0.0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
		FP_NO_EXP_FORMATTER.setMaximumFractionDigits(100);

		// Initialise mask for handling unsigned long as BigInteger
		byte[] magnitude = new byte[8];
		Arrays.fill(magnitude, (byte)0xFF);
		ULONG_MASK = new BigInteger(1, magnitude);
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private NumberUtils()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static boolean setUpper()
	{
		if (digits == DIGITS_UPPER)
			return false;
		digits = DIGITS_UPPER;
		return true;
	}

	//------------------------------------------------------------------

	public static boolean setLower()
	{
		if (digits == DIGITS_LOWER)
			return false;
		digits = DIGITS_LOWER;
		return true;
	}

	//------------------------------------------------------------------

	public static DigitCase setDigitCase(
		DigitCase	digitCase)
	{
		DigitCase oldCase = (digits == DIGITS_UPPER) ? DigitCase.UPPER : DigitCase.LOWER;
		switch (digitCase)
		{
			case UPPER:
				digits = DIGITS_UPPER;
				break;

			case LOWER:
				digits = DIGITS_LOWER;
				break;
		}
		return oldCase;
	}

	//------------------------------------------------------------------

	public static int parseDigitUpper(
		char	ch,
		int		radix)
	{
		for (int i = 0; i < radix; i++)
		{
			if (DIGITS_UPPER[i] == ch)
				return i;
		}
		return -1;
	}

	//------------------------------------------------------------------

	public static int parseDigitLower(
		char	ch,
		int		radix)
	{
		for (int i = 0; i < radix; i++)
		{
			if (DIGITS_LOWER[i] == ch)
				return i;
		}
		return -1;
	}

	//------------------------------------------------------------------

	public static boolean isDigitCharUpper(
		char	ch,
		int		radix)
	{
		return (parseDigitUpper(ch, radix) >= 0);
	}

	//------------------------------------------------------------------

	public static boolean isDigitCharLower(
		char	ch,
		int		radix)
	{
		return (parseDigitLower(ch, radix) >= 0);
	}

	//------------------------------------------------------------------

	public static int roundUpQuotientInt(
		int	value,
		int	divisor)
	{
		return (value + divisor - 1) / divisor;
	}

	//------------------------------------------------------------------

	public static long roundUpQuotientLong(
		long	value,
		long	divisor)
	{
		return (value + divisor - 1) / divisor;
	}

	//------------------------------------------------------------------

	public static int roundUpInt(
		int	value,
		int	divisor)
	{
		return (value + divisor - 1) / divisor * divisor;
	}

	//------------------------------------------------------------------

	public static long roundUpLong(
		long	value,
		long	divisor)
	{
		return (value + divisor - 1) / divisor * divisor;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the number of digits in the decimal representation of the specified number.
	 *
	 * @param  value
	 *           the value whose number of decimal digits is required.
	 * @return the number of digits in the decimal representation of {@code value}.
	 */

	public static int getNumDecDigitsInt(
		int	value)
	{
		if (value < 0)
		{
			if (value == Integer.MIN_VALUE)
				return POWERS_OF_TEN_INT.length;
			value = -value;
		}
		int i = 1;
		while (i < POWERS_OF_TEN_INT.length)
		{
			if (value < POWERS_OF_TEN_INT[i])
				break;
			++i;
		}
		return i;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the number of digits in the decimal representation of the specified number.
	 *
	 * @param  value
	 *           the value whose number of decimal digits is required.
	 * @return the number of digits in the decimal representation of {@code value}.
	 */

	public static int getNumDecDigitsLong(
		long	value)
	{
		if (value < 0)
		{
			if (value == Long.MIN_VALUE)
				return POWERS_OF_TEN_LONG.length;
			value = -value;
		}
		int i = 1;
		while (i < POWERS_OF_TEN_LONG.length)
		{
			if (value < POWERS_OF_TEN_LONG[i])
				break;
			++i;
		}
		return i;
	}

	//------------------------------------------------------------------

	public static String byteToHexString(
		int	value)
	{
		return new String(new char[] { digits[(value >> 4) & 0x0F], digits[value & 0x0F] });
	}

	//------------------------------------------------------------------

	public static String uIntToBinString(
		int	value)
	{
		return uIntToBinString(value, 0, '\0');
	}

	//------------------------------------------------------------------

	public static String uIntToBinString(
		int		value,
		int		numDigits,
		char	padChar)
	{
		// Allocate buffer
		char[] buffer = new char[(numDigits > 0) ? numDigits : Integer.SIZE];

		// Convert value to string
		int i = buffer.length;
		while (--i >= 0)
		{
			buffer[i] = (char)((value & 1) + '0');
			value >>>= 1;
			if (value == 0)
				break;
		}

		// If number of digits was specified, fill remainder of buffer and return string
		if (numDigits > 0)
		{
			// Fill remainder of buffer
			while (--i >= 0)
				buffer[i] = padChar;

			// Return string
			return new String(buffer);
		}

		// Return string
		return new String(buffer, i, buffer.length - i);
	}

	//------------------------------------------------------------------

	public static String uLongToBinString(
		long	value)
	{
		return uLongToBinString(value, 0, '\0');
	}

	//------------------------------------------------------------------

	public static String uLongToBinString(
		long	value,
		int		numDigits,
		char	padChar)
	{
		// Allocate buffer
		char[] buffer = new char[(numDigits > 0) ? numDigits : Long.SIZE];

		// Convert value to string
		int i = buffer.length;
		while (--i >= 0)
		{
			buffer[i] = (char)((value & 1) + '0');
			value >>>= 1;
			if (value == 0)
				break;
		}

		// If number of digits was specified, fill remainder of buffer and return string
		if (numDigits > 0)
		{
			// Fill remainder of buffer
			while (--i >= 0)
				buffer[i] = padChar;

			// Return string
			return new String(buffer);
		}

		// Return string
		return new String(buffer, i, buffer.length - i);
	}

	//------------------------------------------------------------------

	/**
	 * Returns a decimal string representation of the specified unsigned number.
	 *
	 * @param  value
	 *           the unsigned number for which a decimal string representation is required.
	 * @return a decimal string representation of {@code value}.
	 */

	public static String uIntToDecString(
		int	value)
	{
		return Long.toString(value & 0xFFFFFFFFL);
	}

	//------------------------------------------------------------------

	/**
	 * Returns a decimal string representation of the specified unsigned number.  The length of the returned string is
	 * determined by the specified number of digits, <i>numDigits</i>:
	 * <ul>
	 *   <li>If <i>numDigits</i> is greater than the length of the string representation of the number, the string will
	 *       be padded on the left with the specified character.</li>
	 *   <li>If <i>numDigits</i> is greater than 0 but less than the length of the string representation of the number,
	 *       the string will be truncated on the left.</li>
	 *   <li>If <i>numDigits</i> is less than or equal to 0, the string representation of the number will not be
	 *       truncated or padded.</li>
	 * </ul>
	 *
	 * @param  value
	 *           the unsigned number for which a decimal string representation is required.
	 * @param  numDigits
	 *           the number of decimal digits in the string representation of {@code value}.  If it is 0 or negative,
	 *           the string representation will not be truncated or padded.
	 * @param  padChar
	 *           the character with which the returned string will be padded on the left if the length of the string
	 *           representation of {@code value} is less than {@code numDigits}.
	 * @return a decimal string representation of {@code value} that may be truncated or padded on the left in the way
	 *         described above.
	 */

	public static String uIntToDecString(
		int		value,
		int		numDigits,
		char	padChar)
	{
		return (numDigits > 0) ? uIntToString(value, numDigits, padChar, 10) : uIntToDecString(value);
	}

	//------------------------------------------------------------------

	public static String uLongToDecString(
		long	value)
	{
		byte[] buffer = new byte[8];
		NumberCodec.uLongToBytesBE(value, buffer, 0, buffer.length);
		return new BigInteger(1, buffer).toString();
	}

	//------------------------------------------------------------------

	public static String uLongToDecString(
		long	value,
		int		numDigits,
		char	padChar)
	{
		return (numDigits > 0) ? uLongToString(value, numDigits, padChar, 10) : uLongToDecString(value);
	}

	//------------------------------------------------------------------

	public static String uIntToHexString(
		int	value)
	{
		return uIntToHexString(value, 0, '\0');
	}

	//------------------------------------------------------------------

	public static String uIntToHexString(
		int		value,
		int		numDigits,
		char	padChar)
	{
		// Allocate buffer
		char[] buffer = new char[(numDigits > 0) ? numDigits : Integer.SIZE >> 2];

		// Convert value to string
		int i = buffer.length;
		while (--i >= 0)
		{
			buffer[i] = digits[value & 0x0F];
			value >>>= 4;
			if (value == 0)
				break;
		}

		// If number of digits was specified, fill remainder of buffer and return string
		if (numDigits > 0)
		{
			// Fill remainder of buffer
			while (--i >= 0)
				buffer[i] = padChar;

			// Return string
			return new String(buffer);
		}

		// Return string
		return new String(buffer, i, buffer.length - i);
	}

	//------------------------------------------------------------------

	public static String uLongToHexString(
		long	value)
	{
		return uLongToHexString(value, 0, '\0');
	}

	//------------------------------------------------------------------

	public static String uLongToHexString(
		long	value,
		int		numDigits,
		char	padChar)
	{
		// Allocate buffer
		char[] buffer = new char[(numDigits > 0) ? numDigits : Long.SIZE >> 2];

		// Convert value to string
		int i = buffer.length;
		while (--i >= 0)
		{
			buffer[i] = digits[(int)value & 0x0F];
			value >>>= 4;
			if (value == 0)
				break;
		}

		// If number of digits was specified, fill remainder of buffer and return string
		if (numDigits > 0)
		{
			// Fill remainder of buffer
			while (--i >= 0)
				buffer[i] = padChar;

			// Return string
			return new String(buffer);
		}

		// Return string
		return new String(buffer, i, buffer.length - i);
	}

	//------------------------------------------------------------------

	/**
	 * Returns a string representation of the specified unsigned number with the specified number of digits and the
	 * specified radix.
	 * <ul>
	 *   <li>If the specified number of digits is less than the length of the string representation of the number, the
	 *       string will be truncated on the left.</li>
	 *   <li>If the specified number of digits is greater than the length of the string representation of the number,
	 *       the string will be padded on the left with the specified character.</li>
	 * </ul>
	 *
	 * @param  value
	 *           the unsigned number for which a string representation is required.
	 * @param  numDigits
	 *           the number of digits in the string representation of {@code value}.
	 * @param  padChar
	 *           the character with which the returned string will be padded on the left if the length of the string
	 *           representation of {@code value} is less than {@code numDigits}.
	 * @param  radix
	 *           the radix of the string representation of {@code value}.
	 * @return a string representation of {@code value} that may be truncated or padded on the left in the way described
	 *         above.
	 */

	public static String uIntToString(
		int		value,
		int		numDigits,
		char	padChar,
		int		radix)
	{
		// If value is negative, handle it as long
		if (value < 0)
			return uLongToString(value & 0xFFFFFFFFL, numDigits, padChar, radix);

		// Validate arguments
		if (numDigits < 1)
			throw new IllegalArgumentException("Number of digits out of bounds: " + numDigits);
		if ((radix < 2) || (radix > digits.length))
			throw new IllegalArgumentException("Radix out of bounds: " + radix);

		// Allocate buffer for digits
		char[] buffer = new char[numDigits];

		// Convert value to string
		int i = buffer.length;
		while (--i >= 0)
		{
			buffer[i] = digits[value % radix];
			value /= radix;
			if (value == 0)
				break;
		}

		// Fill remainder of buffer
		while (--i >= 0)
			buffer[i] = padChar;

		// Return string
		return new String(buffer);
	}

	//------------------------------------------------------------------

	/**
	 * Returns a string representation of the specified unsigned number with the specified number of digits and the
	 * specified radix.
	 * <ul>
	 *   <li>If the specified number of digits is less than the length of the string representation of the number, the
	 *       string will be truncated on the left.</li>
	 *   <li>If the specified number of digits is greater than the length of the string representation of the number,
	 *       the string will be padded on the left with the specified character.</li>
	 * </ul>
	 *
	 * @param  value
	 *           the unsigned number for which a string representation is required.
	 * @param  numDigits
	 *           the number of digits in the string representation of {@code value}.
	 * @param  padChar
	 *           the character with which the returned string will be padded on the left if the length of the string
	 *           representation of {@code value} is less than {@code numDigits}.
	 * @param  radix
	 *           the radix of the string representation of {@code value}.
	 * @return a string representation of {@code value} that may be truncated or padded on the left in the way described
	 *         above.
	 */

	public static String uLongToString(
		long	value,
		int		numDigits,
		char	padChar,
		int		radix)
	{
		// Validate arguments
		if (numDigits < 1)
			throw new IllegalArgumentException("Number of digits out of bounds: " + numDigits);
		if ((radix < 2) || (radix > digits.length))
			throw new IllegalArgumentException("Radix out of bounds: " + radix);

		// If value is negative, handle it as BigInteger
		if (value < 0)
		{
			// Convert value to string
			String str = BigInteger.valueOf(value).and(ULONG_MASK).toString(radix);

			// Convert to upper case, if necessary
			if ((digits == DIGITS_UPPER) && (radix > 10))
				str = str.toUpperCase();

			// Get length of string
			int length = str.length();

			// If string is shorter than required, pad it on left ...
			if (length < numDigits)
				str = Character.toString(padChar).repeat(numDigits - length) + str;

			// ... otherwise, if string is longer than required, truncate it on left
			else if ((numDigits > 0) && (length > numDigits))
				str = str.substring(length - numDigits);

			// Return string
			return str;
		}

		// Allocate buffer for digits
		char[] buffer = new char[numDigits];

		// Convert value to string
		int i = buffer.length;
		while (--i >= 0)
		{
			buffer[i] = digits[(int)(value % radix)];
			value /= radix;
			if (value == 0)
				break;
		}

		// Fill remainder of buffer
		while (--i >= 0)
			buffer[i] = padChar;

		// Return string
		return new String(buffer);
	}

	//------------------------------------------------------------------

	/**
	 * Parses a binary string representation of a signed integer and returns the result.
	 *
	 * @param  str
	 *           the string that is to be parsed.
	 * @return the signed integer represented by the binary string {@code str}.
	 * @throws NumberFormatException
	 *           if {@code str} is not a valid binary representation of an integer.
	 * @throws ValueOutOfBoundsException
	 *           if the result of parsing {@code str} cannot be represented in 32 bits.
	 */

	public static int parseIntBin(
		String	str)
	{
		BigInteger value = new BigInteger(str, 2);
		if (value.bitLength() > 32)
			throw new ValueOutOfBoundsException();
		return value.intValue();
	}

	//------------------------------------------------------------------

	/**
	 * Parses a decimal string representation of a signed integer and returns the result.
	 *
	 * @param  str
	 *           the string that is to be parsed.
	 * @return the signed integer represented by the decimal string {@code str}.
	 * @throws NumberFormatException
	 *           if {@code str} is not a valid decimal representation of an integer.
	 * @throws ValueOutOfBoundsException
	 *           if the result of parsing {@code str} cannot be represented in 32 bits.
	 */

	public static int parseIntDec(
		String	str)
	{
		BigInteger value = new BigInteger(str);
		if (value.bitLength() > 32)
			throw new ValueOutOfBoundsException();
		return value.intValue();
	}

	//------------------------------------------------------------------

	/**
	 * Parses a hexadecimal string representation of a signed integer and returns the result.
	 *
	 * @param  str
	 *           the string that is to be parsed.
	 * @return the signed integer represented by the hexadecimal string {@code str}.
	 * @throws NumberFormatException
	 *           if {@code str} is not a valid hexadecimal representation of an integer.
	 * @throws ValueOutOfBoundsException
	 *           if the result of parsing {@code str} cannot be represented in 32 bits.
	 */

	public static int parseIntHex(
		String	str)
	{
		BigInteger value = new BigInteger(str, 16);
		if (value.bitLength() > 32)
			throw new ValueOutOfBoundsException();
		return value.intValue();
	}

	//------------------------------------------------------------------

	/**
	 * Parses a string representation of a signed integer and returns the result.  The radix of the string
	 * is determined from its prefix:
	 * <ul>
	 *   <li>if the string starts with "0b" or "0B", it is parsed as a binary representation,</li>
	 *   <li>else if it starts with "0x" or "0X", it is parsed as a hexadecimal representation,</li>
	 *   <li>else it is parsed as a decimal representation.</li>
	 * </ul>
	 *
	 * @param  str
	 *           the string that is to be parsed.
	 * @return the signed integer represented by {@code str}.
	 * @throws NumberFormatException
	 *           if {@code str} does not represent a valid integer in any of the supported radices.
	 * @throws ValueOutOfBoundsException
	 *           if the result of parsing {@code str} cannot be represented in 32 bits.
	 */

	public static int parseInt(
		String	str)
	{
		if (str.length() >= Radix.PREFIX_LENGTH)
		{
			String prefix = str.substring(0, Radix.PREFIX_LENGTH).toLowerCase();
			if (prefix.equals(Radix.BINARY.prefix))
				return parseIntBin(str.substring(Radix.PREFIX_LENGTH));
			if (prefix.equals(Radix.HEXADECIMAL.prefix))
				return parseIntHex(str.substring(Radix.PREFIX_LENGTH));
		}
		return parseIntDec(str);
	}

	//------------------------------------------------------------------

	/**
	 * Parses a binary string representation of an unsigned integer and returns the result.
	 *
	 * @param  str
	 *           the string that is to be parsed.
	 * @return the unsigned integer represented by the binary string {@code str}.
	 * @throws NumberFormatException
	 *           if {@code str} is not a valid binary representation of an integer.
	 * @throws ValueOutOfBoundsException
	 *           if the result of parsing {@code str} cannot be represented in 32 bits.
	 */

	public static int parseUIntBin(
		String	str)
	{
		BigInteger value = new BigInteger(str, 2);
		if (value.signum() < 0)
			throw new NumberFormatException();
		if (value.bitLength() > 32)
			throw new ValueOutOfBoundsException();
		return value.intValue();
	}

	//------------------------------------------------------------------

	/**
	 * Parses a decimal string representation of an unsigned integer and returns the result.
	 *
	 * @param  str
	 *           the string that is to be parsed.
	 * @return the unsigned integer represented by the decimal string {@code str}.
	 * @throws NumberFormatException
	 *           if {@code str} is not a valid decimal representation of an integer.
	 * @throws ValueOutOfBoundsException
	 *           if the result of parsing {@code str} cannot be represented in 32 bits.
	 */

	public static int parseUIntDec(
		String	str)
	{
		BigInteger value = new BigInteger(str);
		if (value.signum() < 0)
			throw new NumberFormatException();
		if (value.bitLength() > 32)
			throw new ValueOutOfBoundsException();
		return value.intValue();
	}

	//------------------------------------------------------------------

	/**
	 * Parses a hexadecimal string representation of an unsigned integer and returns the result.
	 *
	 * @param  str
	 *           the string that is to be parsed.
	 * @return the unsigned integer represented by the hexadecimal string {@code str}.
	 * @throws NumberFormatException
	 *           if {@code str} is not a valid hexadecimal representation of an integer.
	 * @throws ValueOutOfBoundsException
	 *           if the result of parsing {@code str} cannot be represented in 32 bits.
	 */

	public static int parseUIntHex(
		String	str)
	{
		BigInteger value = new BigInteger(str, 16);
		if (value.signum() < 0)
			throw new NumberFormatException();
		if (value.bitLength() > 32)
			throw new ValueOutOfBoundsException();
		return value.intValue();
	}

	//------------------------------------------------------------------

	/**
	 * Parses a string representation of an unsigned integer and returns the result.  The radix of the
	 * string is determined from its prefix:
	 * <ul>
	 *   <li>if the string starts with "0b" or "0B", it is parsed as a binary representation,</li>
	 *   <li>else if it starts with "0x" or "0X", it is parsed as a hexadecimal representation,</li>
	 *   <li>else it is parsed as a decimal representation.</li>
	 * </ul>
	 *
	 * @param  str
	 *           the string that is to be parsed.
	 * @return the unsigned integer represented by {@code str}.
	 * @throws NumberFormatException
	 *           if {@code str} does not represent a valid integer in any of the supported radices.
	 * @throws ValueOutOfBoundsException
	 *           if the result of parsing {@code str} cannot be represented in 32 bits.
	 */

	public static int parseUInt(
		String	str)
	{
		if (str.length() >= Radix.PREFIX_LENGTH)
		{
			String prefix = str.substring(0, Radix.PREFIX_LENGTH).toLowerCase();
			if (prefix.equals(Radix.BINARY.prefix))
				return parseUIntBin(str.substring(Radix.PREFIX_LENGTH));
			if (prefix.equals(Radix.HEXADECIMAL.prefix))
				return parseUIntHex(str.substring(Radix.PREFIX_LENGTH));
		}
		return parseUIntDec(str);
	}

	//------------------------------------------------------------------

	/**
	 * Parses a string representation of an unsigned integer and returns the result.  The radix of the
	 * string is determined from its prefix:
	 * <ul>
	 *   <li>if the string starts with "0b" or "0B", it is parsed as a binary representation,</li>
	 *   <li>else if it starts with "0x" or "0X", it is parsed as a hexadecimal representation,</li>
	 *   <li>else it is parsed as a decimal representation.</li>
	 * </ul>
	 * The radix is returned in the first element of an array.
	 *
	 * @param  str
	 *           the string that is to be parsed.
	 * @param  radixBuffer
	 *           an array whose first element will be set to the radix of the string representation.
	 * @return the unsigned integer represented by {@code str}.
	 * @throws NumberFormatException
	 *           if {@code str} does not represent a valid integer in any of the supported radices.
	 * @throws ValueOutOfBoundsException
	 *           if the result of parsing {@code str} cannot be represented in 32 bits.
	 */

	public static int parseUInt(
		String	str,
		Radix[]	radixBuffer)
	{
		int value = 0;
		Radix radix = Radix.DECIMAL;
		if (str.length() >= Radix.PREFIX_LENGTH)
		{
			String prefix = str.substring(0, Radix.PREFIX_LENGTH).toLowerCase();
			if (prefix.equals(Radix.BINARY.prefix))
			{
				radix = Radix.BINARY;
				value = parseUIntBin(str.substring(Radix.PREFIX_LENGTH));
			}
			else if (prefix.equals(Radix.HEXADECIMAL.prefix))
			{
				radix = Radix.HEXADECIMAL;
				value = parseUIntHex(str.substring(Radix.PREFIX_LENGTH));
			}
		}
		if (radix == Radix.DECIMAL)
			value = parseUIntDec(str);
		if (radixBuffer != null)
			radixBuffer[0] = radix;
		return value;
	}

	//------------------------------------------------------------------

	/**
	 * Parses a binary string representation of an unsigned long integer and returns the result.
	 *
	 * @param  str
	 *           the string that is to be parsed.
	 * @return the unsigned long integer represented by the binary string {@code str}.
	 * @throws NumberFormatException
	 *           if {@code str} is not a valid binary representation of a long integer.
	 * @throws ValueOutOfBoundsException
	 *           if the result of parsing {@code str} cannot be represented in 64 bits.
	 */

	public static long parseULongBin(
		String	str)
	{
		BigInteger value = new BigInteger(str, 2);
		if (value.signum() < 0)
			throw new NumberFormatException();
		if (value.bitLength() > 64)
			throw new ValueOutOfBoundsException();
		return value.longValue();
	}

	//------------------------------------------------------------------

	/**
	 * Parses a decimal string representation of an unsigned long integer and returns the result.
	 *
	 * @param  str
	 *           the string that is to be parsed.
	 * @return the unsigned long integer represented by the decimal string {@code str}.
	 * @throws NumberFormatException
	 *           if {@code str} is not a valid decimal representation of a long integer.
	 * @throws ValueOutOfBoundsException
	 *           if the result of parsing {@code str} cannot be represented in 64 bits.
	 */

	public static long parseULongDec(
		String	str)
	{
		BigInteger value = new BigInteger(str);
		if (value.signum() < 0)
			throw new NumberFormatException();
		if (value.bitLength() > 64)
			throw new ValueOutOfBoundsException();
		return value.longValue();
	}

	//------------------------------------------------------------------

	/**
	 * Parses a hexadecimal string representation of an unsigned long integer and returns the result.
	 *
	 * @param  str
	 *           the string that is to be parsed.
	 * @return the unsigned long integer represented by the hexadecimal string {@code str}.
	 * @throws NumberFormatException
	 *           if {@code str} is not a valid hexdecimal representation of a long integer.
	 * @throws ValueOutOfBoundsException
	 *           if the result of parsing {@code str} cannot be represented in 64 bits.
	 */

	public static long parseULongHex(
		String	str)
	{
		BigInteger value = new BigInteger(str, 16);
		if (value.signum() < 0)
			throw new NumberFormatException();
		if (value.bitLength() > 64)
			throw new ValueOutOfBoundsException();
		return value.longValue();
	}

	//------------------------------------------------------------------

	/**
	 * Parses a string representation of an unsigned long integer and returns the result.  The radix of the
	 * string is determined from its prefix:
	 * <ul>
	 *   <li>if the string starts with "0b" or "0B", it is parsed as a binary representation,</li>
	 *   <li>else if it starts with "0x" or "0X", it is parsed as a hexadecimal representation,</li>
	 *   <li>else it is parsed as a decimal representation.</li>
	 * </ul>
	 *
	 * @param  str
	 *           the string that is to be parsed.
	 * @return the unsigned long integer represented by {@code str}.
	 * @throws NumberFormatException
	 *           if {@code str} does not represent a valid long integer in any of the supported radices.
	 * @throws ValueOutOfBoundsException
	 *           if the result of parsing {@code str} cannot be represented in 64 bits.
	 */

	public static long parseULong(
		String	str)
	{
		if (str.length() >= Radix.PREFIX_LENGTH)
		{
			String prefix = str.substring(0, Radix.PREFIX_LENGTH).toLowerCase();
			if (prefix.equals(Radix.BINARY.prefix))
				return parseULongBin(str.substring(Radix.PREFIX_LENGTH));
			if (prefix.equals(Radix.HEXADECIMAL.prefix))
				return parseULongHex(str.substring(Radix.PREFIX_LENGTH));
		}
		return parseULongDec(str);
	}

	//------------------------------------------------------------------

	/**
	 * Parses a string representation of an unsigned long integer and returns the result.  The radix of the
	 * string is determined from its prefix:
	 * <ul>
	 *   <li>if the string starts with "0b" or "0B", it is parsed as a binary representation,</li>
	 *   <li>else if it starts with "0x" or "0X", it is parsed as a hexadecimal representation,</li>
	 *   <li>else it is parsed as a decimal representation.</li>
	 * </ul>
	 * The radix is returned in the first element of an array.
	 *
	 * @param  str
	 *           the string that is to be parsed.
	 * @param  radixBuffer
	 *           an array whose first element will be set to the radix of the string representation.
	 * @return the unsigned long integer represented by {@code str}.
	 * @throws NumberFormatException
	 *           if {@code str} does not represent a valid long integer in any of the supported radices.
	 * @throws ValueOutOfBoundsException
	 *           if the result of parsing {@code str} cannot be represented in 64 bits.
	 */

	public static long parseULong(
		String	str,
		Radix[]	radixBuffer)
	{
		long value = 0;
		Radix radix = Radix.DECIMAL;
		if (str.length() >= Radix.PREFIX_LENGTH)
		{
			String prefix = str.substring(0, Radix.PREFIX_LENGTH).toLowerCase();
			if (prefix.equals(Radix.BINARY.prefix))
			{
				radix = Radix.BINARY;
				value = parseULongBin(str.substring(Radix.PREFIX_LENGTH));
			}
			else if (prefix.equals(Radix.HEXADECIMAL.prefix))
			{
				radix = Radix.HEXADECIMAL;
				value = parseULongHex(str.substring(Radix.PREFIX_LENGTH));
			}
		}
		if (radix == Radix.DECIMAL)
			value = parseULongDec(str);
		if (radixBuffer != null)
			radixBuffer[0] = radix;
		return value;
	}

	//------------------------------------------------------------------

	public static String bytesToHexString(
		byte[]	data)
	{
		return bytesToHexString(data, 0, data.length, 0);
	}

	//------------------------------------------------------------------

	public static String bytesToHexString(
		byte[]	data,
		int		bytesPerLine)
	{
		return bytesToHexString(data, 0, data.length, bytesPerLine);
	}

	//------------------------------------------------------------------

	public static String bytesToHexString(
		byte[]	data,
		int		offset,
		int		length)
	{
		return bytesToHexString(data, offset, length, 0);
	}

	//------------------------------------------------------------------

	public static String bytesToHexString(
		byte[]	data,
		int		offset,
		int		length,
		int		bytesPerLine)
	{
		StringBuilder buffer = new StringBuilder();
		int endOffset = offset + length;
		for (int i = offset; i < endOffset; i++)
		{
			if ((bytesPerLine > 0) && (i > 0) && (i % bytesPerLine == 0))
				buffer.append('\n');
			buffer.append(digits[(data[i] >> 4) & 0x0F]);
			buffer.append(digits[data[i] & 0x0F]);
		}
		return buffer.toString();
	}

	//------------------------------------------------------------------

	public static String bytesToHexString(
		byte[]	data,
		int		bytesPerLine,
		int		numDigits,
		String	separator)
	{
		return bytesToHexString(data, 0, data.length, bytesPerLine, numDigits, separator, null, null);
	}

	//------------------------------------------------------------------

	public static String bytesToHexString(
		byte[]	data,
		int		offset,
		int		length,
		int		bytesPerLine,
		int		numDigits,
		String	separator)
	{
		return bytesToHexString(data, offset, length, bytesPerLine, numDigits, separator, null, null);
	}

	//------------------------------------------------------------------

	public static String bytesToHexString(
		byte[]	data,
		int		bytesPerLine,
		int		numDigits,
		String	separator,
		String	prefix,
		String	suffix)
	{
		return bytesToHexString(data, 0, data.length, bytesPerLine, numDigits, separator, prefix, suffix);
	}

	//------------------------------------------------------------------

	public static String bytesToHexString(
		byte[]	data,
		int		offset,
		int		length,
		int		bytesPerLine,
		int		numDigits,
		String	separator,
		String	prefix,
		String	suffix)
	{
		int charsPerByte = (numDigits == 0) ? 2 : numDigits;
		if (separator != null)
			charsPerByte += separator.length();

		int extraCharsPerLine = 0;
		if (prefix != null)
			extraCharsPerLine += prefix.length();
		if (suffix != null)
			extraCharsPerLine += suffix.length();

		long numLines = (bytesPerLine == 0) ? 1 : roundUpQuotientLong(length, bytesPerLine);
		long bufferLength = (charsPerByte * (long)length + extraCharsPerLine) * numLines;
		if (bufferLength > Integer.MAX_VALUE)
			throw new StringConversionException(OUTPUT_TEXT_TOO_LONG_STR);

		StringBuilder buffer = new StringBuilder((int)bufferLength);
		for (int i = 0; i < length; i++)
		{
			if ((bytesPerLine > 0) && (i % bytesPerLine == 0))
			{
				if (i > 0)
				{
					if (suffix != null)
						buffer.append(suffix);
					buffer.append('\n');
				}
				if (prefix != null)
					buffer.append(prefix);
			}
			else if ((separator != null) && (i > 0))
				buffer.append(separator);

			buffer.append(uIntToHexString(data[offset + i], numDigits, '0'));
		}
		return buffer.toString();
	}

	//------------------------------------------------------------------

	public static String intsToHexString(
		int[]	data,
		int		intsPerLine,
		int		numDigits,
		String	separator)
	{
		return intsToHexString(data, 0, data.length, intsPerLine, numDigits, separator, null, null);
	}

	//------------------------------------------------------------------

	public static String intsToHexString(
		int[]	data,
		int		offset,
		int		length,
		int		intsPerLine,
		int		numDigits,
		String	separator)
	{
		return intsToHexString(data, offset, length, intsPerLine, numDigits, separator, null, null);
	}

	//------------------------------------------------------------------

	public static String intsToHexString(
		int[]	data,
		int		intsPerLine,
		int		numDigits,
		String	separator,
		String	prefix,
		String	suffix)
	{
		return intsToHexString(data, 0, data.length, intsPerLine, numDigits, separator, prefix, suffix);
	}

	//------------------------------------------------------------------

	public static String intsToHexString(
		int[]	data,
		int		offset,
		int		length,
		int		intsPerLine,
		int		numDigits,
		String	separator,
		String	prefix,
		String	suffix)
	{
		int charsPerByte = (numDigits == 0) ? 2 * Integer.BYTES : numDigits;
		if (separator != null)
			charsPerByte += separator.length();

		int extraCharsPerLine = 0;
		if (prefix != null)
			extraCharsPerLine += prefix.length();
		if (suffix != null)
			extraCharsPerLine += suffix.length();

		long numLines = (intsPerLine == 0) ? 1 : roundUpQuotientLong(length, intsPerLine);
		long bufferLength = (charsPerByte * (long)length + extraCharsPerLine) * numLines;
		if (bufferLength > Integer.MAX_VALUE)
			throw new StringConversionException(OUTPUT_TEXT_TOO_LONG_STR);

		StringBuilder buffer = new StringBuilder((int)bufferLength);
		for (int i = 0; i < length; i++)
		{
			if ((intsPerLine > 0) && (i % intsPerLine == 0))
			{
				if (i > 0)
				{
					if (suffix != null)
						buffer.append(suffix);
					buffer.append('\n');
				}
				if (prefix != null)
					buffer.append(prefix);
			}
			else if ((separator != null) && (i > 0))
				buffer.append(separator);

			buffer.append(uIntToHexString(data[offset + i], numDigits, '0'));
		}
		return buffer.toString();
	}

	//------------------------------------------------------------------

	/**
	 * @throws NumberFormatException
	 */

	public static byte[] hexStringToBytes(
		String	str)
	{
		str = str.toUpperCase();
		int length = str.length();
		byte[] bytes = new byte[(length + 1) >>> 1];
		int i = 0;
		int j = 0;
		if (length > bytes.length << 1)
		{
			int value = parseDigitUpper(str.charAt(i++), 16);
			if (value < 0)
				throw new NumberFormatException();

			bytes[j++] = (byte)value;
		}
		while (i < length)
		{
			int value1 = parseDigitUpper(str.charAt(i++), 16);
			if (value1 < 0)
				throw new NumberFormatException();

			int value2 = parseDigitUpper(str.charAt(i++), 16);
			if (value2 < 0)
				throw new NumberFormatException();

			bytes[j++] = (byte)((value1 << 4) | value2);
		}
		return bytes;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Enumerated types
////////////////////////////////////////////////////////////////////////


	// ENUMERATION: RADIX


	public enum Radix
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		BINARY
		(
			2,
			"0b"
		),

		DECIMAL
		(
			10,
			""
		),

		HEXADECIMAL
		(
			16,
			"0x"
		);

		//--------------------------------------------------------------

		private static final	int	PREFIX_LENGTH	= 2;

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	int		value;
		private	String	prefix;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private Radix(
			int		value,
			String	prefix)
		{
			this.value = value;
			this.prefix = prefix;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		public int getValue()
		{
			return value;
		}

		//--------------------------------------------------------------

		public String getPrefix()
		{
			return prefix;
		}

		//--------------------------------------------------------------

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// CLASS: STRING-CONVERSION EXCEPTION


	public static class StringConversionException
		extends RuntimeException
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private StringConversionException()
		{
		}

		//--------------------------------------------------------------

		private StringConversionException(
			String	message)
		{
			// Call superclass constructor
			super(message);
		}

		//--------------------------------------------------------------

	}


	//==================================================================

}

//----------------------------------------------------------------------
