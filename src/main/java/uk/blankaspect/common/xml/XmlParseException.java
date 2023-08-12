/*====================================================================*\

XmlParseException.java

XML parse exception class.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.xml;

//----------------------------------------------------------------------


// IMPORTS


import java.io.File;

import java.net.URL;

import uk.blankaspect.common.exception.AppException;
import uk.blankaspect.common.exception.ExceptionUtils;

//----------------------------------------------------------------------


// XML PARSE EXCEPTION CLASS


public class XmlParseException
	extends AppException
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	private static final	int	MAX_PATHNAME_LENGTH	= 160;

	private static final	String	LOCATION_STR	= "Location: ";
	private static final	String	VALUE_STR		= "Value: ";

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public XmlParseException(AppException.IId id,
							 String           location)
	{
		super(id);
		this.location = location;
	}

	//------------------------------------------------------------------

	public XmlParseException(AppException.IId id,
							 String           location,
							 CharSequence...  replacements)
	{
		super(id, replacements);
		this.location = location;
	}

	//------------------------------------------------------------------

	public XmlParseException(AppException.IId id,
							 String           location,
							 String           value)
	{
		this(id, location);
		this.value = value;
	}

	//------------------------------------------------------------------

	public XmlParseException(AppException.IId id,
							 String           location,
							 String           value,
							 CharSequence...  replacements)
	{
		this(id, location, replacements);
		this.value = value;
	}

	//------------------------------------------------------------------

	public XmlParseException(AppException.IId id,
							 File             file,
							 String           location)
	{
		this(id, location);
		this.file = file;
	}

	//------------------------------------------------------------------

	public XmlParseException(AppException.IId id,
							 URL              url,
							 String           location)
	{
		this(id, location);
		this.url = url;
	}

	//------------------------------------------------------------------

	public XmlParseException(AppException.IId id,
							 File             file,
							 String           location,
							 String           value)
	{
		this(id, file, location);
		this.value = value;
	}

	//------------------------------------------------------------------

	public XmlParseException(AppException.IId id,
							 URL              url,
							 String           location,
							 String           value)
	{
		this(id, url, location);
		this.value = value;
	}

	//------------------------------------------------------------------

	public XmlParseException(AppException.IId id,
							 File             file,
							 CharSequence...  replacements)
	{
		super(id, replacements);
		this.file = file;
	}

	//------------------------------------------------------------------

	public XmlParseException(AppException.IId id,
							 URL              url,
							 CharSequence...  replacements)
	{
		super(id, replacements);
		this.url = url;
	}

	//------------------------------------------------------------------

	public XmlParseException(AppException.IId id,
							 File             file,
							 String           location,
							 CharSequence...  replacements)
	{
		this(id, file, replacements);
		this.location = location;
	}

	//------------------------------------------------------------------

	public XmlParseException(AppException.IId id,
							 URL              url,
							 String           location,
							 CharSequence...  replacements)
	{
		this(id, url, replacements);
		this.location = location;
	}

	//------------------------------------------------------------------

	public XmlParseException(AppException.IId id,
							 File             file,
							 String           location,
							 String           value,
							 CharSequence...  replacements)
	{
		this(id, file, location, replacements);
		this.value = value;
	}

	//------------------------------------------------------------------

	public XmlParseException(AppException.IId id,
							 URL              url,
							 String           location,
							 String           value,
							 CharSequence...  replacements)
	{
		this(id, url, location, replacements);
		this.value = value;
	}

	//------------------------------------------------------------------

	public XmlParseException(XmlParseException exception,
							 File              file)
	{
		this(exception.getId(), file, exception.location, exception.value, exception.getReplacements());
	}

	//------------------------------------------------------------------

	public XmlParseException(XmlParseException exception,
							 URL               url)
	{
		this(exception.getId(), url, exception.location, exception.value, exception.getReplacements());
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	protected static String getPathname(File file)
	{
		return ExceptionUtils.getLimitedPathname(file, MAX_PATHNAME_LENGTH);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	@Override
	protected String getPrefix()
	{
		StringBuilder buffer = new StringBuilder(256);
		if (file != null)
		{
			buffer.append(getPathname(file));
			buffer.append('\n');
		}
		if (url != null)
		{
			buffer.append(url);
			buffer.append('\n');
		}
		if (location != null)
		{
			buffer.append(LOCATION_STR);
			buffer.append(location);
			buffer.append('\n');
		}
		if (value != null)
		{
			buffer.append(VALUE_STR);
			buffer.append(value);
			buffer.append('\n');
		}

		return buffer.toString();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	File	file;
	private	URL		url;
	private	String	location;
	private	String	value;

}

//----------------------------------------------------------------------
