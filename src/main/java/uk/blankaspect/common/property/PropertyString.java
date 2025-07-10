/*====================================================================*\

PropertyString.java

Class: property string.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.property;

//----------------------------------------------------------------------


// IMPORTS


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

//----------------------------------------------------------------------


// CLASS: PROPERTY STRING


public class PropertyString
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	public enum SpanKind
	{
		UNKNOWN,
		LITERAL,
		ENVIRONMENT,
		SYSTEM
	}

	public static final		char	KEY_SEPARATOR_CHAR	= '.';
	public static final		String	KEY_SEPARATOR		= Character.toString(KEY_SEPARATOR_CHAR);

	private static final	String	KEY_SUFFIX	= "}";

	private static final	String	ENV_PREFIX_KEY	= "blankaspect" + KEY_SEPARATOR + "envPrefix";
	private static final	String	SYS_PREFIX_KEY	= "blankaspect" + KEY_SEPARATOR + "sysPrefix";

	private static final	String[]	DEFAULT_KEY_PREFIXES	=
	{
		"${",
		"\u00A2{"	// cent sign
	};

	private static final	String	DEFAULT_ENV_PREFIX	= "env" + KEY_SEPARATOR;
	private static final	String	DEFAULT_SYS_PREFIX	= "sys" + KEY_SEPARATOR;

////////////////////////////////////////////////////////////////////////
//  Class variables
////////////////////////////////////////////////////////////////////////

	private static	String	envPrefix	= DEFAULT_ENV_PREFIX;
	private static	String	sysPrefix	= DEFAULT_SYS_PREFIX;

////////////////////////////////////////////////////////////////////////
//  Static initialiser
////////////////////////////////////////////////////////////////////////

	static
	{
		// Initialise prefix of environment variable
		String value = System.getProperty(ENV_PREFIX_KEY);
		if (value != null)
			envPrefix = value.endsWith(KEY_SEPARATOR) ? value : value + KEY_SEPARATOR;

		// Initialise prefix of syatem property
		value = System.getProperty(SYS_PREFIX_KEY);
		if (value != null)
			sysPrefix = value.endsWith(KEY_SEPARATOR) ? value : value + KEY_SEPARATOR;
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	private PropertyString()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static String concatenateKeys(
		CharSequence...	keys)
	{
		// Calculate length of buffer
		int length = -1;
		for (CharSequence key : keys)
			length += key.length() + 1;

		// Concatenate keys
		StringBuilder buffer = new StringBuilder(length);
		for (int i = 0; i < keys.length; i++)
		{
			if (i > 0)
				buffer.append(KEY_SEPARATOR_CHAR);
			buffer.append(keys[i]);
		}
		return buffer.toString();
	}

	//------------------------------------------------------------------

	public static List<Span> getSpans(
		String	str)
	{
		return getSpans(str, DEFAULT_KEY_PREFIXES);
	}

	//------------------------------------------------------------------

	public static List<Span> getSpans(
		String		str,
		String...	keyPrefixes)
	{
		return getSpans(str, List.of(keyPrefixes));
	}

	//------------------------------------------------------------------

	public static List<Span> getSpans(
		String				str,
		Collection<String>	keyPrefixes)
	{
		// Validate arguments
		if (keyPrefixes.isEmpty())
			throw new IllegalArgumentException("No key prefix");

		// Initialise list of spans
		List<Span> spans = new ArrayList<>();

		// Decompose input string into spans
		int index = 0;
		while (index < str.length())
		{
			// Initialise local variables
			SpanKind spanKind = SpanKind.UNKNOWN;
			String spanKey = null;
			String spanText = null;
			int keyPrefixLength = 0;
			int startIndex = index;

			// Search for start of property key
			for (String keyPrefix : keyPrefixes)
			{
				index = str.indexOf(keyPrefix, startIndex);
				if (index >= 0)
				{
					keyPrefixLength = keyPrefix.length();
					break;
				}
			}

			// If no property key was found, add span for literal text ...
			if (index < 0)
			{
				index = str.length();
				if (index > startIndex)
				{
					spanKind = SpanKind.LITERAL;
					spanText = str.substring(startIndex);
				}
			}

			// ... otherwise, search for end of property key; replace property key with value of property
			else
			{
				// Search for end of property key
				index = str.indexOf(KEY_SUFFIX, index + keyPrefixLength);

				// If there is no property-key suffix, add span for literal text ...
				if (index < 0)
				{
					index = str.length();
					if (index > startIndex)
					{
						spanKind = SpanKind.LITERAL;
						spanText = str.substring(startIndex);
					}
				}

				// ... otherwise, replace property key with value of property
				else
				{
					// Extract property key
					startIndex += keyPrefixLength;
					String key = str.substring(startIndex, index);

					// Replace property key with value of property
					try
					{
						// Case: key is environment variable
						if (key.startsWith(envPrefix))
						{
							spanKind = SpanKind.ENVIRONMENT;
							spanKey = key.substring(envPrefix.length());
							spanText = System.getenv(spanKey);
						}

						// Case: key is key of system property
						else if (key.startsWith(sysPrefix))
						{
							spanKind = SpanKind.SYSTEM;
							spanKey = key.substring(sysPrefix.length());
							spanText = System.getProperty(spanKey);
						}

						// Case: key does not start with a recognised prefix
						else
						{
							// Try system property, then environment variable
							spanKey = key;
							if (!key.isEmpty())
							{
								String value = System.getProperty(key);
								if (value != null)
								{
									spanKind = SpanKind.SYSTEM;
									spanText = value;
								}
								else
								{
									value = System.getenv(key);
									if (value != null)
									{
										spanKind = SpanKind.ENVIRONMENT;
										spanText = value;
									}
								}
							}
						}
					}
					catch (SecurityException e)
					{
						// ignore
					}

					// Increment index past end of property key
					index += KEY_SUFFIX.length();
				}
			}

			// Create new span and add it to list
			spans.add(new Span(spanKind, spanKey, spanText));
		}

		// Return list of spans
		return spans;
	}

	//------------------------------------------------------------------

	public static String parse(
		Iterable<? extends Span>	spans)
	{
		StringBuilder buffer = new StringBuilder(256);
		for (Span span : spans)
		{
			if (span.text != null)
				buffer.append(span.text);
		}
		return buffer.toString();
	}

	//------------------------------------------------------------------

	public static String parse(
		String	str)
	{
		return parse(getSpans(str));
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Member records
////////////////////////////////////////////////////////////////////////


	// RECORD: SPAN


	public record Span(
		SpanKind	kind,
		String		key,
		String		text)
	{ }

	//==================================================================

}

//----------------------------------------------------------------------
