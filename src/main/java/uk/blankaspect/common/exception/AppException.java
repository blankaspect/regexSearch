/*====================================================================*\

AppException.java

Class: application exception.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.exception;

//----------------------------------------------------------------------


// CLASS: APPLICATION EXCEPTION


public class AppException
	extends Exception
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The prefix of a placeholder in an input string. */
	public static final	char	PLACEHOLDER_PREFIX_CHAR	= '%';

	/** The character that represents the minimum index of a substitution. */
	public static final	char	MIN_SUBSTITUTION_INDEX_CHAR	= '1';

	/** The character that represents the maximum index of a substitution. */
	public static final	char	MAX_SUBSTITUTION_INDEX_CHAR	= '9';

	/** The default maximum length of a line of a <i>cause</i> message. */
	private static final	int		DEFAULT_MAX_CAUSE_MESSAGE_LINE_LENGTH	= 160;

	/** Miscellaneous strings. */
	private static final	String	NO_ERROR_STR	= "No error";

////////////////////////////////////////////////////////////////////////
//  Class variables
////////////////////////////////////////////////////////////////////////

	private static	int	maxCauseMessageLineLength	= DEFAULT_MAX_CAUSE_MESSAGE_LINE_LENGTH;

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	IId				id;
	private	CharSequence[]	replacements;
	private	String			parentPrefix;
	private	String			parentSuffix;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public AppException()
	{
	}

	//------------------------------------------------------------------

	public AppException(
		String	messageStr)
	{
		this(new AnonymousId(messageStr));
	}

	//------------------------------------------------------------------

	public AppException(
		String			messageStr,
		CharSequence...	replacements)
	{
		this(new AnonymousId(messageStr), replacements);
	}

	//------------------------------------------------------------------

	public AppException(
		String		messageStr,
		Throwable	cause)
	{
		this(new AnonymousId(messageStr), cause);
	}

	//------------------------------------------------------------------

	public AppException(
		String			messageStr,
		Throwable		cause,
		CharSequence...	replacements)
	{
		this(new AnonymousId(messageStr), cause, replacements);
	}

	//------------------------------------------------------------------

	public AppException(
		IId	id)
	{
		this(id, (Throwable)null);
	}

	//------------------------------------------------------------------

	public AppException(
		IId				id,
		CharSequence...	replacements)
	{
		this(id);
		setReplacements(replacements);
	}

	//------------------------------------------------------------------

	public AppException(
		IId			id,
		Throwable	cause)
	{
		super(getString(id), cause);
		this.id = id;
	}

	//------------------------------------------------------------------

	public AppException(
		IId				id,
		Throwable		cause,
		CharSequence...	replacements)
	{
		this(id, cause);
		setReplacements(replacements);
	}

	//------------------------------------------------------------------

	public AppException(
		AppException	exception)
	{
		this(exception, false);
	}

	//------------------------------------------------------------------

	public AppException(
		AppException	exception,
		boolean			ignorePrefixAndSuffix)
	{
		this(exception.id, exception.getCause(), exception.replacements);
		parentPrefix = exception.parentPrefix;
		parentSuffix = exception.parentSuffix;
		if (!ignorePrefixAndSuffix)
		{
			String prefix = exception.getPrefix();
			if (prefix != null)
				parentPrefix = (parentPrefix == null) ? prefix : prefix + parentPrefix;

			String suffix = exception.getSuffix();
			if (suffix != null)
				parentSuffix = (parentSuffix == null) ? suffix : suffix + parentSuffix;
		}
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static int getMaxCauseMessageLineLength()
	{
		return maxCauseMessageLineLength;
	}

	//------------------------------------------------------------------

	public static String getString(
		IId	id)
	{
		return (id == null) ? NO_ERROR_STR : id.getMessage();
	}

	//------------------------------------------------------------------

	public static void setMaxCauseMessageLineLength(
		int	length)
	{
		maxCauseMessageLineLength = length;
	}

	//------------------------------------------------------------------

	/**
	 * Substitutes the specified replacement sequences for occurrences of placeholders in the specified string, and
	 * returns the resulting string.  A placeholder has the form "%<i>n</i>", where <i>n</i> is a decimal-digit
	 * character in the range '1'..'9'.  Each placeholder is replaced by the specified sequence whose zero-based index
	 * is <i>n</i>-1; for example, the placeholder "%3" will be replaced by the sequence at index 2.  A placeholder that
	 * does not have a corresponding replacement sequence is replaced by an empty string.
	 * <p>
	 * The special role of "%" may be escaped by prefixing another "%" to it (ie, "%%").
	 * </p>
	 *
	 * @param  str
	 *           the string on which substitutions will be performed.
	 * @param  replacements
	 *           the sequences that will replace placeholders in {@code str}.
	 * @return a transformation of the input string in which each placeholder is replaced by the element of
	 *         {@code replacements} at the corresponding index.
	 */

	private static String substitute(
		String			str,
		CharSequence...	replacements)
	{
		// If there are no replacement sequences, return the input string
		if (replacements.length == 0)
			return str;

		// Allocate a buffer for the output string
		StringBuilder buffer = new StringBuilder(str.length() + 32);

		// Perform substitutions on the input string
		int index = 0;
		while (index < str.length())
		{
			// Set the start index to the end of the last placeholder
			int startIndex = index;

			// Get the index of the next placeholder prefix
			index = str.indexOf(PLACEHOLDER_PREFIX_CHAR, index);

			// If there are no more placeholder prefixes, set the index to the end of the input string
			if (index < 0)
				index = str.length();

			// Get the substring of the input string from the end of the last placeholder to the current placeholder
			// prefix, and append it to the output buffer
			if (index > startIndex)
				buffer.append(str.substring(startIndex, index));

			// Increment the index past the current placeholder prefix
			++index;

			// If the end of the input string has not been reached, process the character after the placeholder prefix
			if (index < str.length())
			{
				// Get the next character from the input string
				char ch = str.charAt(index);

				// If the placeholder prefix is followed by a substitution index, perform a substitution ...
				if ((ch >= MIN_SUBSTITUTION_INDEX_CHAR) && (ch <= MAX_SUBSTITUTION_INDEX_CHAR))
				{
					// Parse the substitution index
					int subIndex = ch - MIN_SUBSTITUTION_INDEX_CHAR;

					// If there is a replacement sequence for the substitution index, append it to the output buffer
					if (subIndex < replacements.length)
					{
						CharSequence replacement = replacements[subIndex];
						if (replacement != null)
							buffer.append(replacement);
					}

					// Increment the index past the substitution index
					++index;
				}

				// ... otherwise, append a placeholder prefix to the output buffer
				else
				{
					// Append a placeholder prefix to the output buffer
					buffer.append(PLACEHOLDER_PREFIX_CHAR);

					// If the placeholder prefix is followed by another one, skip it
					if (ch == PLACEHOLDER_PREFIX_CHAR)
						++index;
				}
			}

			// If the last character in the input string is a placeholder prefix, append it to the output buffer
			else if (index == str.length())
				buffer.append(PLACEHOLDER_PREFIX_CHAR);
		}

		// Return the output string
		return buffer.toString();
	}

	//------------------------------------------------------------------

	protected static String createString(
		String			message,
		String			prefix,
		String			suffix,
		CharSequence[]	replacements,
		Throwable		cause)
	{
		// Append the detail message with prefix, suffix and any substitutions
		StringBuilder buffer = new StringBuilder();
		if (message != null)
		{
			if (prefix != null)
				buffer.append(prefix);
			buffer.append((replacements == null) ? message : substitute(message, replacements));
			if (suffix != null)
				buffer.append(suffix);
		}

		// Wrap the text of the detail message of the cause and append the text to the detail message
		while (cause != null)
		{
			String str = cause.getMessage();
			if ((str == null) || (cause instanceof AppException))
				str = cause.toString();
			buffer.append("\n- ");
			int index = 0;
			while (index < str.length())
			{
				boolean space = false;
				int breakIndex = index;
				int endIndex = index + Math.max(1, maxCauseMessageLineLength);
				for (int i = index; (i <= endIndex) || (breakIndex == index); i++)
				{
					if (i == str.length())
					{
						if (!space)
							breakIndex = i;
						break;
					}
					if (str.charAt(i) == ' ')
					{
						if (!space)
						{
							space = true;
							breakIndex = i;
						}
					}
					else
						space = false;
				}
				if (breakIndex - index > 0)
					buffer.append(str.substring(index, breakIndex));
				buffer.append("\n  ");
				for (index = breakIndex; index < str.length(); index++)
				{
					if (str.charAt(index) != ' ')
						break;
				}
			}
			index = buffer.length();
			while (--index >= 0)
			{
				if (!Character.isWhitespace(buffer.charAt(index)))
					break;
			}
			buffer.setLength(++index);

			// Get next exception in chain of causes
			cause = cause.getCause();
		}

		return buffer.toString();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	@Override
	public String toString()
	{
		// Get the combined prefix
		String prefix = getPrefix();
		if (parentPrefix != null)
			prefix = (prefix == null) ? parentPrefix : prefix + parentPrefix;

		// Get the combined suffix
		String suffix = getSuffix();
		if (parentSuffix != null)
			suffix = (suffix == null) ? parentSuffix : suffix + parentSuffix;

		// Create the string from its components
		return createString(getMessage(), prefix, suffix, replacements, getCause());
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public IId getId()
	{
		return id;
	}

	//------------------------------------------------------------------

	public CharSequence getReplacement(
		int	index)
	{
		return replacements[index];
	}

	//------------------------------------------------------------------

	public CharSequence[] getReplacements()
	{
		return replacements;
	}

	//------------------------------------------------------------------

	public void clearReplacements()
	{
		replacements = null;
	}

	//------------------------------------------------------------------

	public void setReplacement(
		int		index,
		String	str)
	{
		replacements[index] = str;
	}

	//------------------------------------------------------------------

	public void setReplacement(
		int	value)
	{
		setReplacements(Integer.toString(value));
	}

	//------------------------------------------------------------------

	public void setReplacements(
		CharSequence...	strs)
	{
		replacements = strs;
	}

	//------------------------------------------------------------------

	protected String getPrefix()
	{
		return null;
	}

	//------------------------------------------------------------------

	protected String getSuffix()
	{
		return null;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Member interfaces
////////////////////////////////////////////////////////////////////////


	// INTERFACE: EXCEPTION IDENTIFIER


	@FunctionalInterface
	public interface IId
	{

	////////////////////////////////////////////////////////////////////
	//  Methods
	////////////////////////////////////////////////////////////////////

		String getMessage();

		//--------------------------------------------------------------

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// CLASS: ANONYMOUS IDENTIFIER


	protected static class AnonymousId
		implements IId
	{

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	String	message;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		protected AnonymousId(
			String	message)
		{
			this.message = message;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : Id interface
	////////////////////////////////////////////////////////////////////

		@Override
		public String getMessage()
		{
			return message;
		}

		//--------------------------------------------------------------

	}

	//==================================================================

}

//----------------------------------------------------------------------
