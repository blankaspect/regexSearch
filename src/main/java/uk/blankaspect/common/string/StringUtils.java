/*====================================================================*\

StringUtils.java

Class: string-related utility methods.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.string;

//----------------------------------------------------------------------


// IMPORTS


import java.util.ArrayList;
import java.util.List;

//----------------------------------------------------------------------


// CLASS: STRING-RELATED UTILITY METHODS


/**
 * This class contains utility methods that relate to {@linkplain String strings} and {@linkplain CharSequence character
 * sequences}.
 */

public class StringUtils
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The prefix that is recognised by the {@link #escape(CharSequence, String)} method. */
	public static final	char	ESCAPE_PREFIX_CHAR	= '\\';

	/**
	 * This is an enumeration of the ways in which an input string may be split by the <code>split*(&hellip;)</code>
	 * methods.
	 */
	public enum SplitMode
	{
		/**
		 * The separator is discarded after splitting.
		 */
		NONE,

		/**
		 * The separator is the last character of the prefix after splitting.
		 */
		PREFIX,

		/**
		 * The separator is the first character of the suffix after splitting.
		 */
		SUFFIX
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private StringUtils()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns {@code true} if the specified string is {@code null} or empty.
	 *
	 * @param  str
	 *           the string that will be tested.
	 * @return {@code true} if {@code str} is {@code null} or empty.
	 */

	public static boolean isNullOrEmpty(
		String	str)
	{
		return (str == null) || str.isEmpty();
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if the specified string is {@code null} or empty or it contains only {@linkplain
	 * Character#isWhitespace(int) whitespace} characters.
	 *
	 * @param  str
	 *           the string that will be tested.
	 * @return {@code true} if {@code str} is {@code null} or empty or it contains only {@linkplain
	 *         Character#isWhitespace(int) whitespace} characters.
	 */

	public static boolean isNullOrBlank(
		String	str)
	{
		return (str == null) || str.isBlank();
	}

	//------------------------------------------------------------------

	/**
	 * Returns the maximum length of the specified character sequences.
	 *
	 * @param  seqs
	 *           the character sequences whose maximum length is desired.
	 * @return the maximum length of {@code seqs}.
	 */

	public static int getMaxLength(
		Iterable<? extends CharSequence>	seqs)
	{
		int maxLength = 0;
		for (CharSequence seq : seqs)
		{
			int length = seq.length();
			if (maxLength < length)
				maxLength = length;
		}
		return maxLength;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the maximum length of the specified character sequences.
	 *
	 * @param  seqs
	 *           the character sequences whose maximum length is desired.
	 * @return the maximum length of {@code seqs}.
	 */

	public static int getMaxLength(
		CharSequence...	seqs)
	{
		return getMaxLength(List.of(seqs));
	}

	//------------------------------------------------------------------

	/**
	 * Returns the specified character sequence as a string that is padded with leading spaces (U+0020), if necessary,
	 * so that the string has the specified length.  If the length of the input sequence is greater than or equal to the
	 * specified length, the input sequence is returned as a string with no additional padding.
	 *
	 * @param  seq
	 *           the input sequence of characters.
	 * @param  length
	 *           the desired length of the padded output string.
	 * @return {@code seq} as a string, padded with leading spaces, if necessary, so that the length of the returned
	 *         string is {@code length}.
	 */

	public static String padBefore(
		CharSequence	seq,
		int				length)
	{
		return padBefore(seq, length, ' ');
	}

	//------------------------------------------------------------------

	/**
	 * Returns the specified character sequence as a string that is padded at the start with repetitions of the
	 * specified character, if necessary, so that the string has the specified length.  If the length of the input
	 * sequence is greater than or equal to the specified length, the input sequence is returned as a string with no
	 * additional padding.
	 *
	 * @param  seq
	 *           the input sequence of characters.
	 * @param  length
	 *           the desired length of the padded output string.
	 * @param  ch
	 *           the character with which the string will be padded.
	 * @return {@code seq} as a string, padded at the start with repetitions of {@code ch}, if necessary, so that the
	 *         length of the returned string is {@code length}.
	 */

	public static String padBefore(
		CharSequence	seq,
		int				length,
		char			ch)
	{
		int padLength = length - seq.length();
		return (padLength > 0) ? Character.toString(ch).repeat(padLength) + seq.toString() : seq.toString();
	}

	//------------------------------------------------------------------

	/**
	 * Returns the specified character sequence as a string that is padded with trailing spaces (U+0020), if necessary,
	 * so that the string has the specified length.  If the length of the input sequence is greater than or equal to the
	 * specified length, the input sequence is returned as a string with no additional padding.
	 *
	 * @param  seq
	 *           the input sequence of characters.
	 * @param  length
	 *           the desired length of the padded output string.
	 * @return {@code seq} as a string, padded with trailing spaces, if necessary, so that the length of the returned
	 *         string is {@code length}.
	 */

	public static String padAfter(
		CharSequence	seq,
		int				length)
	{
		return padAfter(seq, length, ' ');
	}

	//------------------------------------------------------------------

	/**
	 * Returns the specified character sequence as a string that is padded at the end with repetitions of the specified
	 * character, if necessary, so that the string has the specified length.  If the length of the input sequence is
	 * greater than or equal to the specified length, the input sequence is returned as a string with no additional
	 * padding.
	 *
	 * @param  seq
	 *           the input sequence of characters.
	 * @param  length
	 *           the desired length of the padded output string.
	 * @param  ch
	 *           the character with which the string will be padded.
	 * @return {@code seq} as a string, padded at the end with repetitions of {@code ch}, if necessary, so that the
	 *         length of the returned string is {@code length}.
	 */

	public static String padAfter(
		CharSequence	seq,
		int				length,
		char			ch)
	{
		int padLength = length - seq.length();
		return (padLength > 0) ? seq.toString() + Character.toString(ch).repeat(padLength) : seq.toString();
	}

	//------------------------------------------------------------------

	/**
	 * Returns a list of the substrings of the specified string that result from splitting the input string at each
	 * occurrence of the specified separator character and removing the separators.
	 *
	 * @param  str
	 *           the string that is to be split.
	 * @param  separator
	 *           the separator character.
	 * @return a list of the substrings of {@code str} that result from splitting the input string at each occurrence of
	 *         {@code separator}.
	 */

	public static List<String> split(
		String	str,
		char	separator)
	{
		return split(str, separator, false);
	}

	//------------------------------------------------------------------

	/**
	 * Returns a list of the substrings of the specified string that result from splitting the input string at each
	 * occurrence of the specified separator character and removing the separators.  The last element of the list of
	 * substrings may optionally be discarded if it is the empty string.
	 *
	 * @param  str
	 *           the string that is to be split.
	 * @param  separator
	 *           the separator character.
	 * @param  discardFinalEmptyElement
	 *           if {@code true}, the last element of the list of substrings will be discarded if it is the empty
	 *           string.
	 * @return a list of the substrings of {@code str} that result from splitting the input string at each occurrence of
	 *         {@code separator} and optionally discarding the last element if it is empty.
	 */

	public static List<String> split(
		String	str,
		char	separator,
		boolean	discardFinalEmptyElement)
	{
		List<String> strs = new ArrayList<>();
		int startIndex = 0;
		int endIndex = str.length();
		int index = 0;
		while (index < endIndex)
		{
			index = str.indexOf(separator, index);
			if (index < 0)
				index = endIndex;
			strs.add(str.substring(startIndex, index));
			startIndex = ++index;
		}

		if ((startIndex == endIndex) && !discardFinalEmptyElement)
			strs.add("");
		return strs;
	}

	//------------------------------------------------------------------

	/**
	 * <p style="margin-bottom: 0.25em;">
	 * Returns the two substrings that result from splitting the specified string at the specified index.  The character
	 * at the specified index in the input string may optionally be
	 * </p>
	 * <ul style="margin-top: 0.25em; margin-bottom: 0.25em;">
	 *   <li>included in the first substring (the <i>prefix</i>),</li>
	 *   <li>included in the second substring (the <i>suffix</i>) or</li>
	 *   <li>discarded,</li>
	 * </ul>
	 * <p style="margin-top: 0.25em;">
	 * according to the specified <i>split mode</i>.
	 *
	 * @param  str
	 *           the string that is to be split.
	 * @param  index
	 *           the index of the character at which {@code str} is to be split.  If it is less than 0, the result is as
	 *           described below.
	 * @param  splitMode
	 *           the way in which the character of {@code str} at {@code index} will be treated.
	 * @return an array containing the two substrings that result from splitting {@code str} at {@code index}, taking
	 *         into account {@code splitMode}.  If {@code index} is less than 0, the two returned values will be
	 *         <ul>
	 *           <li>{@code str} and {@code null}, if {@code splitMode} is {@code NONE}, or</li>
	 *           <li>{@code str} and the empty string, if {@code splitMode} is {@code PREFIX} or {@code SUFFIX}.</li>
	 *         </ul>
	 */

	public static String[] splitAt(
		String		str,
		int			index,
		SplitMode	splitMode)
	{
		return (index < 0) ? new String[] { str, (splitMode == SplitMode.NONE) ? null : "" }
						   : new String[] { str.substring(0, (splitMode == SplitMode.PREFIX) ? index + 1 : index),
											str.substring((splitMode == SplitMode.SUFFIX) ? index : index + 1) };
	}

	//------------------------------------------------------------------

	/**
	 * Returns the two substrings that result from splitting the specified string at the first occurrence of the
	 * specified character, which is discarded.
	 *
	 * @param  str
	 *           the string that is to be split.
	 * @param  ch
	 *           the character at which {@code str} is to be split.
	 * @return an array containing the two substrings that result from splitting {@code str} at the first occurrence of
	 *         {@code ch}.  If {@code str} does not contain {@code ch}, the two returned values will be {@code str} and
	 *         {@code null}.
	 */

	public static String[] splitAtFirst(
		String	str,
		char	ch)
	{
		return splitAt(str, str.indexOf(ch), SplitMode.NONE);
	}

	//------------------------------------------------------------------

	/**
	 * <p style="margin-bottom: 0.25em;">
	 * Returns the two substrings that result from splitting the specified string at the first occurrence of the
	 * specified character, which may optionally be
	 * </p>
	 * <ul style="margin-top: 0.25em; margin-bottom: 0.25em;">
	 *   <li>included in the first substring (the <i>prefix</i>),</li>
	 *   <li>included in the second substring (the <i>suffix</i>) or</li>
	 *   <li>discarded,</li>
	 * </ul>
	 * <p style="margin-top: 0.25em;">
	 * according to the specified <i>split mode</i>.
	 * </p>
	 *
	 * @param  str
	 *           the string that is to be split.
	 * @param  ch
	 *           the character at which {@code str} is to be split.
	 * @param  splitMode
	 *           the way in which the first occurrence of {@code ch} will be treated.
	 * @return an array containing the two substrings that result from splitting {@code str} at the first occurrence of
	 *         {@code ch}.  If {@code str} does not contain {@code ch}, the two returned values will be
	 *         <ul>
	 *           <li>{@code str} and {@code null}, if {@code splitMode} is {@code NONE}, or</li>
	 *           <li>{@code str} and the empty string, if {@code splitMode} is {@code PREFIX} or {@code SUFFIX}.</li>
	 *         </ul>
	 */

	public static String[] splitAtFirst(
		String		str,
		char		ch,
		SplitMode	splitMode)
	{
		return splitAt(str, str.indexOf(ch), splitMode);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the two substrings that result from splitting the specified string at the last occurrence of the
	 * specified character, which is discarded.
	 *
	 * @param  str
	 *           the string that is to be split.
	 * @param  ch
	 *           the character at which {@code str} is to be split.
	 * @return an array containing the two substrings that result from splitting {@code str} at the last occurrence of
	 *         {@code ch}.  If {@code str} does not contain {@code ch}, the two returned values will be {@code str} and
	 *         {@code null}.
	 */

	public static String[] splitAtLast(
		String	str,
		char	ch)
	{
		return splitAt(str, str.lastIndexOf(ch), SplitMode.NONE);
	}

	//------------------------------------------------------------------

	/**
	 * <p style="margin-bottom: 0.25em;">
	 * Returns the two substrings that result from splitting the specified string at the last occurrence of the
	 * specified character, which may optionally be
	 * </p>
	 * <ul style="margin-top: 0.25em; margin-bottom: 0.25em;">
	 *   <li>included in the first substring (the <i>prefix</i>),</li>
	 *   <li>included in the second substring (the <i>suffix</i>) or</li>
	 *   <li>discarded,</li>
	 * </ul>
	 * <p style="margin-top: 0.25em;">
	 * according to the specified <i>split mode</i>.
	 * </p>
	 *
	 * @param  str
	 *           the string that is to be split.
	 * @param  ch
	 *           the character at which {@code str} is to be split.
	 * @param  splitMode
	 *           the way in which the last occurrence of {@code ch} will be treated.
	 * @return an array containing the two substrings that result from splitting {@code str} at the last occurrence of
	 *         {@code ch}.  If {@code str} does not contain {@code ch}, the two returned values will be
	 *         <ul>
	 *           <li>{@code str} and {@code null}, if {@code splitMode} is {@code NONE}, or</li>
	 *           <li>{@code str} and the empty string, if {@code splitMode} is {@code PREFIX} or {@code SUFFIX}.</li>
	 *         </ul>
	 */

	public static String[] splitAtLast(
		String		str,
		char		ch,
		SplitMode	splitMode)
	{
		return splitAt(str, str.lastIndexOf(ch), splitMode);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the substring of the specified string from the start of the string to (but not including) the first
	 * occurrence of the specified character.
	 *
	 * @param  str
	 *           the string from which a leading substring is to be extracted.
	 * @param  ch
	 *           the character whose first occurrence denotes the exclusive end of the prefix.
	 * @return the substring of {@code str} from the start of the string to (but not including) the first occurrence of
	 *         {@code ch}.  If {@code str} does not contain {@code ch}, {@code str} is returned.
	 */

	public static String getPrefixFirst(
		String	str,
		char	ch)
	{
		int index = str.indexOf(ch);
		return (index < 0) ? str : str.substring(0, index);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the substring of the specified string from the start of the string to (but not including) the last
	 * occurrence of the specified character.
	 *
	 * @param  str
	 *           the string from which a leading substring is to be extracted.
	 * @param  ch
	 *           the character whose last occurrence denotes the exclusive end of the prefix.
	 * @return the substring of {@code str} from the start of the string to (but not including) the last occurrence of
	 *         {@code ch}.  If {@code str} does not contain {@code ch}, {@code str} is returned.
	 */

	public static String getPrefixLast(
		String	str,
		char	ch)
	{
		int index = str.lastIndexOf(ch);
		return (index < 0) ? str : str.substring(0, index);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the substring of the specified string from (and including) the first occurrence of the specified
	 * character to the end of the string.
	 *
	 * @param  str
	 *           the string from which a trailing substring is to be extracted.
	 * @param  ch
	 *           the character whose first occurrence denotes the inclusive start of the suffix.
	 * @return the substring of {@code str} from (and including) the first occurrence of {@code ch} to the end of the
	 *         string.  If {@code str} does not contain {@code ch}, {@code str} is returned.
	 */

	public static String getSuffixFirst(
		String	str,
		char	ch)
	{
		int index = str.indexOf(ch);
		return (index < 0) ? str : str.substring(index);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the substring of the specified string from (but not including) the first occurrence of the specified
	 * character to the end of the string.
	 *
	 * @param  str
	 *           the string from which a trailing substring is to be extracted.
	 * @param  ch
	 *           the character whose first occurrence denotes the exclusive start of the suffix.
	 * @return the substring of {@code str} from (but not including) the first occurrence of {@code ch} to the end of
	 *         the string.  If {@code str} does not contain {@code ch}, {@code str} is returned.
	 */

	public static String getSuffixAfterFirst(
		String	str,
		char	ch)
	{
		int index = str.indexOf(ch);
		return (index < 0) ? str : str.substring(index + 1);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the substring of the specified string from (and including) the last occurrence of the specified character
	 * to the end of the string.
	 *
	 * @param  str
	 *           the string from which a trailing substring is to be extracted.
	 * @param  ch
	 *           the character whose last occurrence denotes the inclusive start of the suffix.
	 * @return the substring of {@code str} from (and including) the last occurrence of {@code ch} to the end of the
	 *         string.  If {@code str} does not contain {@code ch}, {@code str} is returned.
	 */

	public static String getSuffixLast(
		String	str,
		char	ch)
	{
		int index = str.lastIndexOf(ch);
		return (index < 0) ? str : str.substring(index);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the substring of the specified string from (but not including) the last occurrence of the specified
	 * character to the end of the string.
	 *
	 * @param  str
	 *           the string from which a trailing substring is to be extracted.
	 * @param  ch
	 *           the character whose last occurrence denotes the exclusive start of the suffix.
	 * @return the substring of {@code str} from (but not including) the last occurrence of {@code ch} to the end of
	 *         the string.  If {@code str} does not contain {@code ch}, {@code str} is returned.
	 */

	public static String getSuffixAfterLast(
		String	str,
		char	ch)
	{
		int index = str.lastIndexOf(ch);
		return (index < 0) ? str : str.substring(index + 1);
	}

	//------------------------------------------------------------------

	/**
	 * Removes the specified prefix (ie, leading substring) from the specified string, if the string starts with the
	 * prefix, and returns the resulting string.
	 *
	 * @param  str
	 *           the string from which to remove {@code prefix}.
	 * @param  prefix
	 *           the leading substring that is to be removed from {@code str}.
	 * @return {@code str} without {@code prefix}, if {@code str} starts with {@code prefix}; otherwise, {@code str}.
	 */

	public static String removePrefix(
		String	str,
		String	prefix)
	{
		return (str.isEmpty() || prefix.isEmpty() || !str.startsWith(prefix)) ? str : str.substring(prefix.length());
	}

	//------------------------------------------------------------------

	/**
	 * Removes the specified suffix (ie, trailing substring) from the specified string, if the string ends with the
	 * suffix, and returns the resulting string.
	 *
	 * @param  str
	 *           the string from which to remove {@code suffix}.
	 * @param  suffix
	 *           the trailing substring that is to be removed from {@code str}.
	 * @return {@code str} without {@code suffix}, if {@code str} ends with {@code suffix}; otherwise, {@code str}.
	 */

	public static String removeSuffix(
		String	str,
		String	suffix)
	{
		return (str.isEmpty() || suffix.isEmpty() || !str.endsWith(suffix))
						? str
						: str.substring(0, str.length() - suffix.length());
	}

	//------------------------------------------------------------------

	/**
	 * Returns a string that consists of the specified character sequences in order, with the specified separator
	 * between adjacent character sequences.
	 *
	 * @param  separator
	 *           the character that will separate adjacent elements of {@code seqs} in the output string.
	 * @param  seqs
	 *           the character sequences to be joined together.
	 * @return a string that consists of the elements of {@code seqs}, with {@code separator} between adjacent elements.
	 */

	public static String join(
		char		    separator,
		CharSequence...	seqs)
	{
		return join(separator, false, List.of(seqs));
	}

	//------------------------------------------------------------------

	/**
	 * Returns a string that consists of the specified character sequences in order, with the specified separator
	 * between adjacent character sequences.
	 *
	 * @param  separator
	 *           the character that will separate adjacent elements of {@code seqs} in the output string.
	 * @param  seqs
	 *           the character sequences to be joined together.
	 * @return a string that consists of the elements of {@code seqs}, with {@code separator} between adjacent elements.
	 */

	public static String join(
		char								separator,
		Iterable<? extends CharSequence>	seqs)
	{
		return join(separator, false, seqs);
	}

	//------------------------------------------------------------------

	/**
	 * Returns a string that consists of the specified character sequences in order, with the specified optional
	 * separator between adjacent character sequences.
	 *
	 * @param  separator
	 *           the character sequence that will separate adjacent elements of {@code seqs} in the output string.  If
	 *           it is {@code null}, the elements of {@code seqs} will be concatenated without a separator.
	 * @param  seqs
	 *           the character sequences to be joined together.
	 * @return a string that consists of the elements of {@code seqs}, with {@code separator} between adjacent elements.
	 */

	public static String join(
		CharSequence	separator,
		CharSequence...	seqs)
	{
		return join(separator, false, List.of(seqs));
	}

	//------------------------------------------------------------------

	/**
	 * Returns a string that consists of the specified character sequences in order, with the specified optional
	 * separator between adjacent character sequences.
	 *
	 * @param  separator
	 *           the character sequence that will separate adjacent elements of {@code seqs} in the output string.  If
	 *           it is {@code null}, the elements of {@code seqs} will be concatenated without a separator.
	 * @param  seqs
	 *           the character sequences to be joined together.
	 * @return a string that consists of the elements of {@code seqs}, with {@code separator} between adjacent elements.
	 */

	public static String join(
		CharSequence						separator,
		Iterable<? extends CharSequence>	seqs)
	{
		return join(separator, false, seqs);
	}

	//------------------------------------------------------------------

	/**
	 * Returns a string that consists of the specified character sequences in order, with the specified separator
	 * between adjacent character sequences and an optional separator after the last character sequence.
	 *
	 * @param  separator
	 *           the character that will separate adjacent elements of {@code seqs} in the output string.
	 * @param  includeTrailingSeparator
	 *           if {@code true}, a {@code separator} will be appended to the last element of {@code seqs}.
	 * @param  seqs
	 *           the character sequences to be joined together.
	 * @return a string that consists of the elements of {@code seqs}, with {@code separator} between adjacent elements
	 *         and an optional {@code separator} after the last element.
	 */

	public static String join(
		char			separator,
		boolean			includeTrailingSeparator,
		CharSequence...	seqs)
	{
		return join(separator, includeTrailingSeparator, List.of(seqs));
	}

	//------------------------------------------------------------------

	/**
	 * Returns a string that consists of the specified character sequences in order, with the specified separator
	 * between adjacent character sequences and an optional separator after the last character sequence.
	 *
	 * @param  separator
	 *           the character that will separate adjacent elements of {@code seqs} in the output string.
	 * @param  includeTrailingSeparator
	 *           if {@code true}, a {@code separator} will be appended to the last element of {@code seqs}.
	 * @param  seqs
	 *           the character sequences to be joined together.
	 * @return a string that consists of the elements of {@code seqs}, with {@code separator} between adjacent elements
	 *         and an optional {@code separator} after the last element.
	 */

	public static String join(
		char								separator,
		boolean								includeTrailingSeparator,
		Iterable<? extends CharSequence>	seqs)
	{
		// Calculate length of buffer
		int length = 0;
		for (CharSequence seq : seqs)
		{
			length += seq.length();
			++length;
		}

		// Concatenate character sequences
		StringBuilder buffer = new StringBuilder(length);
		for (CharSequence seq : seqs)
		{
			buffer.append(seq);
			buffer.append(separator);
		}
		if (!includeTrailingSeparator && (buffer.length() > 0))
			buffer.setLength(buffer.length() - 1);

		// Return concatenated sequences
		return buffer.toString();
	}

	//------------------------------------------------------------------

	/**
	 * Returns a string that consists of the specified character sequences in order, with the specified optional
	 * separator between adjacent character sequences and an optional separator after the last character sequence.
	 *
	 * @param  separator
	 *           the character sequence that will separate adjacent elements of {@code seqs} in the output string.  If
	 *           it is {@code null}, the elements of {@code seqs} will be concatenated without a separator.
	 * @param  includeTrailingSeparator
	 *           if {@code true}, a {@code separator} will be appended to the last element of {@code seqs}.
	 * @param  seqs
	 *           the character sequences to be joined together.
	 * @return a string that consists of the elements of {@code seqs}, with {@code separator} between adjacent elements
	 *         and an optional {@code separator} after the last element.
	 */

	public static String join(
		CharSequence	separator,
		boolean			includeTrailingSeparator,
		CharSequence...	seqs)
	{
		return join(separator, includeTrailingSeparator, List.of(seqs));
	}

	//------------------------------------------------------------------

	/**
	 * Returns a string that consists of the specified character sequences in order, with the specified optional
	 * separator between adjacent character sequences and an optional separator after the last character sequence.
	 *
	 * @param  separator
	 *           the character sequence that will separate adjacent elements of {@code seqs} in the output string.  If
	 *           it is {@code null}, the elements of {@code seqs} will be concatenated without a separator.
	 * @param  includeTrailingSeparator
	 *           if {@code true}, a {@code separator} will be appended to the last element of {@code seqs}.
	 * @param  seqs
	 *           the character sequences to be joined together.
	 * @return a string that consists of the elements of {@code seqs}, with {@code separator} between adjacent elements
	 *         and an optional {@code separator} after the last element.
	 */

	public static String join(
		CharSequence						separator,
		boolean								includeTrailingSeparator,
		Iterable<? extends CharSequence>	seqs)
	{
		// Calculate length of buffer
		int length = 0;
		if (separator == null)
		{
			for (CharSequence seq : seqs)
				length += seq.length();
		}
		else
		{
			int separatorLength = separator.length();
			for (CharSequence seq : seqs)
			{
				length += seq.length();
				length += separatorLength;
			}
		}

		// Concatenate character sequences
		StringBuilder buffer = new StringBuilder(length);
		if (separator == null)
		{
			for (CharSequence seq : seqs)
				buffer.append(seq);
		}
		else
		{
			for (CharSequence seq : seqs)
			{
				buffer.append(seq);
				buffer.append(separator);
			}
			if (!includeTrailingSeparator && (buffer.length() > 0))
				buffer.setLength(buffer.length() - separator.length());
		}

		// Return concatenated sequences
		return buffer.toString();
	}

	//------------------------------------------------------------------

	/**
	 * Splits the specified text at line separators and returns the resulting list of lines.  The line separators that
	 * are recognised are CR+LF (U+000D, U+000A) and LF (U+000A).  The line separators are not included in the output
	 * list.
	 *
	 * @param  text
	 *           the text that will be split into lines at line separators.
	 * @return a list of the strings that result from splitting {@code text} at line separators.
	 */

	public static List<String> extractLines(
		CharSequence	text)
	{
		// Initialise list of lines
		List<String> lines = new ArrayList<>();

		// Extract lines from input sequence
		int index = 0;
		int startIndex = 0;
		char prevCh = '\0';
		while (index < text.length())
		{
			char ch = text.charAt(index);
			if (ch == '\n')
			{
				lines.add(text.subSequence(startIndex, (prevCh == '\r') ? index - 1 : index).toString());
				startIndex = index + 1;
			}
			prevCh = ch;
			++index;
		}
		lines.add(text.subSequence(startIndex, index).toString());

		// Return list of lines
		return lines;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the string that results from splitting the specified character sequence into substrings at <i>word
	 * breaks</i> that maximise the length of each substring (with the word break removed) while not exceeding the
	 * specified maximum length, then joining adjacent substrings with a line feed (U+000A).  A word break is a sequence
	 * of one or more space characters (U+0020).
	 * <p>
	 * The length of a substring may exceed the specified limit if the substring does not contain a space character.
	 * </p>
	 *
	 * @param  seq
	 *           the character sequence that is to be split into substrings at appropriate word breaks.
	 * @param  maxLineLength
	 *           the maximum length of a substring when splitting the input sequence.
	 * @return the string that results from splitting {@code seq} into substrings at word breaks that maximise the
	 *         length of each substring while not exceeding {@code maxLineLength}, then joining adjacent substrings with
	 *         a line feed (U+000A).
	 */

	public static String wrap(
		CharSequence	seq,
		int				maxLineLength)
	{
		return join('\n', wrapLines(seq, maxLineLength));
	}

	//------------------------------------------------------------------

	/**
	 * Returns the list of strings that results from splitting the specified character sequence into substrings at
	 * <i>word breaks</i> that maximise the length of each substring (with the word break removed) while not exceeding
	 * the specified maximum length.  A word break is a sequence of one or more space characters (U+0020).
	 * <p>
	 * The length of a substring may exceed the specified limit if the substring does not contain a space character.
	 * </p>
	 *
	 * @param  seq
	 *           the character sequence that is to be split into substrings at appropriate word breaks.
	 * @param  maxLineLength
	 *           the maximum length of a substring when splitting the input sequence.
	 * @return the list of strings that results from splitting {@code seq} into substrings at word breaks that maximise
	 *         the length of each substring while not exceeding {@code maxLineLength}.
	 */

	public static List<String> wrapLines(
		CharSequence	seq,
		int				maxLineLength)
	{
		// Initialise list of lines
		List<String> lines = new ArrayList<>();

		// Split input sequence at word breaks
		int index = 0;
		while (index < seq.length())
		{
			// Initialise loop variables
			boolean wordBreak = false;
			int startIndex = index;
			int endIndex = startIndex + maxLineLength;
			int breakIndex = startIndex;

			// Find next line break
			for (int i = startIndex; (i <= endIndex) || (breakIndex == startIndex); i++)
			{
				// If end of input, mark line break; stop
				if (i == seq.length())
				{
					if (!wordBreak)
						breakIndex = i;
					break;
				}

				// If character is space ...
				if (seq.charAt(i) == ' ')
				{
					// If not already in a word break, mark start of break
					if (!wordBreak)
					{
						wordBreak = true;
						breakIndex = i;
					}
				}

				// ... otherwise, clear 'word break' flag
				else
					wordBreak = false;
			}

			// Add line to list
			if (breakIndex - startIndex > 0)
				lines.add(seq.subSequence(startIndex, breakIndex).toString());

			// Advance to next non-space after line break
			for (index = breakIndex; index < seq.length(); index++)
			{
				if (seq.charAt(index) != ' ')
					break;
			}
		}

		// Return list of lines
		return lines;
	}

	//------------------------------------------------------------------

	/**
	 * Returns a string that is derived from the specified string by converting its first character to lower case.
	 *
	 * @param  str
	 *           the string to be converted.
	 * @return {@code str} with its first character converted to lower case.
	 * @throws NullPointerException
	 *           if {@code str} is {@code null}.
	 * @throws IndexOutOfBoundsException
	 *           if {@code str} is empty.
	 */

	public static String firstCharToLowerCase(
		String	str)
	{
		return str.substring(0, 1).toLowerCase() + str.substring(1);
	}

	//------------------------------------------------------------------

	/**
	 * Returns a string that is derived from the specified string by converting its first character to upper case.
	 *
	 * @param  str
	 *           the string to be converted.
	 * @return {@code str} with its first character converted to upper case.
	 * @throws NullPointerException
	 *           if {@code str} is {@code null}.
	 * @throws IndexOutOfBoundsException
	 *           if {@code str} is empty.
	 */

	public static String firstCharToUpperCase(
		String	str)
	{
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}

	//------------------------------------------------------------------

	/**
	 * Converts the specified text to camel case and returns the resulting string.
	 * <p>
	 * The conversion is described below.  In the description,
	 * <ul>
	 *   <li>a <i>digit</i> is a character for which {@link Character#isDigit(char)} returns {@code true},</li>
	 *   <li>a <i>letter</i> is a character for which {@link Character#isLetter(char)} returns {@code true},</li>
	 *   <li>a <i>non-alphanumeric character</i> is a character that is neither a <i>digit</i> nor a <i>letter</i>.</li>
	 * </ul>
	 * <ol>
	 *   <li>
	 *     Each letter that immediately follows a digit or a non-alphanumeric character is converted to upper case
	 *     except for a letter that follows a sequence of a letter and an apostrophe (U+0027).
	 *   </li>
	 *   <li>All other letters are converted to lower case.</li>
	 *   <li>All non-alphanumeric characters are removed.</li>
	 * </ol>
	 *
	 * @param  text
	 *           the text that is to be converted.
	 * @return the camel-case string that results from the conversion of {@code text}.
	 */

	public static String toCamelCase(
		CharSequence	text)
	{
		// Initialise buffer for output string
		StringBuilder buffer = new StringBuilder(text.length());

		// Convert input sequence to camel case
		boolean lowerCase = true;
		for (int i = 0; i < text.length(); i++)
		{
			char ch = text.charAt(i);
			if (Character.isLetter(ch))
			{
				buffer.append(lowerCase ? Character.toLowerCase(ch) : Character.toUpperCase(ch));
				lowerCase = true;
			}
			else
			{
				if (Character.isDigit(ch))
					buffer.append(ch);
				lowerCase = buffer.isEmpty() || ((ch == '\'') && Character.isLetter(text.charAt(i - 1)));
			}
		}

		// Return output string
		return buffer.toString();
	}

	//------------------------------------------------------------------

	/**
	 * Returns a string that is derived from the specified string by applying the specified prefix in a way that depends
	 * on whether or not the prefix ends with an underscore ('_', U+005F):
	 * <ul>
	 *   <li>
	 *     If the prefix ends with an underscore, the returned string consists of the prefix followed by the input
	 *     string.
	 *   </li>
	 *   <li>
	 *     If the prefix does not end with an underscore, the returned string consists of the prefix followed by the
	 *     input string with its first character converted to upper case.
	 *   </li>
	 * </ul>
	 *
	 * @param  str
	 *           the string to which {@code prefix} will be applied.
	 * @param  prefix
	 *           the prefix that will be applied to {@code str}.
	 * @return the result of applying {@code prefix} to {@code str} in the way described above.
	 */

	public static String applyPrefix(
		String	str,
		String	prefix)
	{
		if (isNullOrEmpty(prefix))
			return str;
		return prefix + (prefix.endsWith("_") ? str : firstCharToUpperCase(str));
	}

	//------------------------------------------------------------------

	/**
	 * Returns a string that consists of the specified character sequence with each occurrence of any of the specified
	 * set of metacharacters immediately preceded by a backslash ('\', U+005C).
	 * <dl>
	 *   <dt>Example:</dt>
	 *   <dd>
	 *     If the input sequence is <code>"${name}"</code> and the metacharacters are <code>"${}"</code>, the returned
	 *     value is <code>"\$\{name\}"</code>.
	 *   </dd>
	 * </dl>
	 *
	 * @param  seq
	 *           the string to be processed.
	 * @param  metachars
	 *           a string containing the metacharacters that will be preceded by a backslash in the output string.
	 * @return a string consisting of {@code seq} with each occurrence of any of {@code metachars} preceded by a
	 *         backslash.
	 */

	public static String escape(
		CharSequence	seq,
		String			metachars)
	{
		StringBuilder buffer = new StringBuilder(2 * seq.length());
		for (int i = 0; i < seq.length(); i++)
		{
			char ch = seq.charAt(i);
			if (metachars.indexOf(ch) >= 0)
				buffer.append(ESCAPE_PREFIX_CHAR);
			buffer.append(ch);
		}
		return buffer.toString();
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if the specified collection of strings contains the specified target string, ignoring letter
	 * case when comparing strings.
	 *
	 * @param  target
	 *           the target string.
	 * @param  strs
	 *           the strings that will be searched for {@code target}.
	 * @return {@code true} if {@code strs} contains {@code target}, ignoring letter case.
	 */

	public static boolean containsIgnoreCase(
		String				target,
		Iterable<String>	strs)
	{
		return (indexOfIgnoreCase(target, strs) >= 0);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the index of the first occurrence of the specified target string in the specified collection of strings,
	 * ignoring letter case when comparing strings.
	 *
	 * @param  target
	 *           the target string.
	 * @param  strs
	 *           the strings that will be searched for {@code target}.
	 * @return the index of the first occurrence of {@code target} in {@code strs}, or -1 if {@code strs} does not
	 *         contain {@code target}.
	 */

	public static int indexOfIgnoreCase(
		String				target,
		Iterable<String>	strs)
	{
		int index = 0;
		for (String str : strs)
		{
			if (target.equalsIgnoreCase(str))
				return index;
			++index;
		}
		return -1;
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
