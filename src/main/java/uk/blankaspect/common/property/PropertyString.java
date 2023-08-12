/*====================================================================*\

PropertyString.java

Class: property string.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.property;

//----------------------------------------------------------------------


// IMPORTS


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

//----------------------------------------------------------------------


// CLASS: PROPERTY STRING


public class PropertyString
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	public static final		char	KEY_SEPARATOR_CHAR	= '.';
	public static final		String	KEY_SEPARATOR		= Character.toString(KEY_SEPARATOR_CHAR);

	private static final	String	KEY_SUFFIX	= "}";

	private static final	int		KEY_SUFFIX_LENGTH	= KEY_SUFFIX.length();

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
		return getSpans(str, Arrays.asList(keyPrefixes));
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

		// Populate list of spans
		int index = 0;
		while (index < str.length())
		{
			Span.Kind spanKind = Span.Kind.UNKNOWN;
			String spanKey = null;
			String spanValue = null;
			int keyPrefixLength = 0;
			int startIndex = index;
			for (String keyPrefix : keyPrefixes)
			{
				index = str.indexOf(keyPrefix, startIndex);
				if (index >= 0)
				{
					keyPrefixLength = keyPrefix.length();
					break;
				}
			}
			if (index < 0)
			{
				index = str.length();
				if (index > startIndex)
				{
					spanKind = Span.Kind.LITERAL;
					spanValue = str.substring(startIndex);
				}
			}
			else
			{
				index = str.indexOf(KEY_SUFFIX, index + keyPrefixLength);
				if (index < 0)
				{
					index = str.length();
					if (index > startIndex)
					{
						spanKind = Span.Kind.LITERAL;
						spanValue = str.substring(startIndex);
					}
				}
				else
				{
					startIndex += keyPrefixLength;
					String key = str.substring(startIndex, index);
					try
					{
						if (key.startsWith(envPrefix))
						{
							spanKind = Span.Kind.ENVIRONMENT;
							spanKey = key.substring(envPrefix.length());
							spanValue = System.getenv(spanKey);
						}
						else if (key.startsWith(sysPrefix))
						{
							spanKind = Span.Kind.SYSTEM;
							spanKey = key.substring(sysPrefix.length());
							spanValue = System.getProperty(spanKey);
						}
						else
						{
							spanKey = key;
							if (!key.isEmpty())
							{
								String value = System.getProperty(key);
								if (value == null)
								{
									value = System.getenv(key);
									if (value != null)
									{
										spanKind = Span.Kind.ENVIRONMENT;
										spanValue = value;
									}
								}
								else
								{
									spanKind = Span.Kind.SYSTEM;
									spanValue = value;
								}
							}
						}
					}
					catch (SecurityException e)
					{
						// ignore
					}
					index += KEY_SUFFIX_LENGTH;
				}
			}
			spans.add(new Span(spanKind, spanKey, spanValue));
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
			if (span.value != null)
				buffer.append(span.value);
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
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// CLASS: SPAN


	public static class Span
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		public enum Kind
		{
			UNKNOWN,
			LITERAL,
			ENVIRONMENT,
			SYSTEM
		}

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	Kind	kind;
		private	String	key;
		private	String	value;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		public Span()
		{
			kind = Kind.UNKNOWN;
		}

		//--------------------------------------------------------------

		public Span(
			Kind	kind,
			String	key,
			String	value)
		{
			this.kind = kind;
			this.key = key;
			this.value = value;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		public Kind getKind()
		{
			return kind;
		}

		//--------------------------------------------------------------

		public String getKey()
		{
			return key;
		}

		//--------------------------------------------------------------

		public String getValue()
		{
			return value;
		}

		//--------------------------------------------------------------

	}

	//==================================================================

}

//----------------------------------------------------------------------
