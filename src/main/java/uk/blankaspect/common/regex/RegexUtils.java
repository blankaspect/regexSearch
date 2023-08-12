/*====================================================================*\

RegexUtils.java

Regular-expression utility methods class.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.regex;

//----------------------------------------------------------------------


// IMPORTS


import java.util.Arrays;

import java.util.regex.PatternSyntaxException;

//----------------------------------------------------------------------


// REGULAR-EXPRESSION UTILITY METHODS CLASS


/**
 * This class contains utility methods that relate to regular expressions.
 */

public class RegexUtils
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The metacharacters of regular expressions. */
	public static final		String	METACHARACTERS	= "$()*+.?[\\]^{|}";

	/** The character that is prefixed to a metacharacter in a regular expression to remove the special meaning of the
		metacharacter. */
	public static final		char	ESCAPE_PREFIX_CHAR	= '\\';

	/** Miscellaneous strings. */
	private static final	String	INDEX_STR	= " near index ";

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private RegexUtils()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Escapes the specified character by prefixing a backslash to it if it is a regular-expression metacharacter,
	 * thereby replacing a metacharacter with its corresponding literal character.
	 *
	 * @param  ch
	 *           the character that will be escaped.
	 * @return a string consisting of the input character with a backslash prefixed to it if it is a metacharacter.
	 */

	public static String escape(
		char	ch)
	{
		return (METACHARACTERS.indexOf(ch) < 0) ? Character.toString(ch)
												: new String(new char[] { ESCAPE_PREFIX_CHAR, ch });
	}

	//------------------------------------------------------------------

	/**
	 * Escapes the specified character sequence by prefixing a backslash to each of the regular-expression
	 * metacharacters in the sequence, thereby replacing each metacharacter with its corresponding literal character.
	 *
	 * @param  seq
	 *           the sequence that will be escaped.
	 * @return the sequence of characters with a backslash prefixed to each metacharacter in the sequence.
	 */

	public static String escape(
		CharSequence	seq)
	{
		StringBuilder buffer = new StringBuilder(2 * seq.length());
		for (int i = 0; i < seq.length(); i++)
		{
			char ch = seq.charAt(i);
			if (METACHARACTERS.indexOf(ch) >= 0)
				buffer.append(ESCAPE_PREFIX_CHAR);
			buffer.append(ch);
		}
		return buffer.toString();
	}

	//------------------------------------------------------------------

	/**
	 * Returns a message for the specified {@link PatternSyntaxException}.  The message consists of a concatenation of
	 * the {@linkplain PatternSyntaxException#getDescription() description} of the exception and, if the exception has a
	 * non-negative {@linkplain PatternSyntaxException#getIndex() index}, a string representation of the index.
	 *
	 * @param  exception
	 *           the exception for which a message is required.
	 * @return a message for {@code exception}.
	 */

	public static String getExceptionMessage(
		PatternSyntaxException	exception)
	{
		StringBuilder buffer = new StringBuilder(128);
		buffer.append(exception.getDescription());
		int index = exception.getIndex();
		if (index >= 0)
		{
			buffer.append(INDEX_STR);
			buffer.append(index);
		}
		return buffer.toString();
	}

	//------------------------------------------------------------------

	/**
	 * Returns a regular expression that is created from the specified character sequences as follows:
	 * <ol>
	 *   <li>The sequences are joined together with the alternation operator, "|".</li>
	 *   <li>The composite string is enclosed in parentheses to form a capturing group.</li>
	 *   <li>Word-boundary matchers are added before and after the capturing group.</li>
	 * </ol>
	 * For example, the input sequences { "cat", "dog", "wombat" } will result in the pattern "\b(cat|dog|wombat)\b".
	 *
	 * @param  words
	 *           the sequences that will be combined into a regular expression.
	 * @return a regular expression that is created from {@code words} as described above.
	 */

	public static String joinAlternatives(
		Iterable<? extends CharSequence>	words)
	{
		return "\\b(" + String.join("|", words) + ")\\b";
	}

	//------------------------------------------------------------------

	/**
	 * Returns a regular expression that is created from the specified character sequences as follows:
	 * <ol>
	 *   <li>The sequences are joined together with the alternation operator, "|".</li>
	 *   <li>The composite string is enclosed in parentheses to form a capturing group.</li>
	 *   <li>Word-boundary matchers are added before and after the capturing group.</li>
	 * </ol>
	 * For example, the input sequences { "cat", "dog", "wombat" } will result in the pattern "\b(cat|dog|wombat)\b".
	 *
	 * @param  words
	 *           the sequences that will be combined into a regular expression.
	 * @return a regular expression that is created from {@code words} as described above.
	 */

	public static String joinAlternatives(
		CharSequence...	words)
	{
		return joinAlternatives(Arrays.asList(words));
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
