/*====================================================================*\

XmlValidationException.java

XML validation exception class.

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


// XML VALIDATION EXCEPTION CLASS


public class XmlValidationException
	extends AppException
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	private static final	int	MAX_PATHNAME_LENGTH	= 160;

	private static final	String	FIRST_ERROR_STR	= "First error (of ";

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public XmlValidationException(AppException.IId id,
								  File             file)
	{
		super(id);
		this.file = file;
	}

	//------------------------------------------------------------------

	public XmlValidationException(AppException.IId id,
								  File             file,
								  String...        errorStrings)
	{
		this(id, file);
		this.errorStrings = errorStrings;
	}

	//------------------------------------------------------------------

	public XmlValidationException(AppException.IId id,
								  URL              url)
	{
		super(id);
		this.url = url;
	}

	//------------------------------------------------------------------

	public XmlValidationException(AppException.IId id,
								  URL              url,
								  String...        errorStrings)
	{
		this(id, url);
		this.errorStrings = errorStrings;
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
		StringBuilder buffer = new StringBuilder();
		if (file != null)
		{
			buffer.append(getPathname(file));
			buffer.append('\n');
		}
		else if (url != null)
		{
			buffer.append(url);
			buffer.append('\n');
		}

		return buffer.toString();
	}

	//------------------------------------------------------------------

	@Override
	protected String getSuffix()
	{
		StringBuilder buffer = new StringBuilder();
		if ((errorStrings != null) && (errorStrings.length > 0))
		{
			buffer.append('\n');
			if (errorStrings.length > 1)
			{
				buffer.append(FIRST_ERROR_STR);
				buffer.append(errorStrings.length);
				buffer.append("):\n");
			}
			buffer.append(errorStrings[0]);
		}

		return buffer.toString();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public String[] getErrorStrings()
	{
		return errorStrings;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	File		file;
	private	URL			url;
	private	String[]	errorStrings;

}

//----------------------------------------------------------------------
