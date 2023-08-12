/*====================================================================*\

UrlException.java

URL exception class.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.exception;

//----------------------------------------------------------------------


// IMPORTS


import java.net.URL;

//----------------------------------------------------------------------


// URL EXCEPTION CLASS


public class UrlException
	extends AppException
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public UrlException(AppException.IId id,
						URL              url)
	{
		super(id);
		this.url = url;
	}

	//------------------------------------------------------------------

	public UrlException(AppException.IId id,
						URL              url,
						Throwable        cause)
	{
		super(id, cause);
		this.url = url;
	}

	//------------------------------------------------------------------

	public UrlException(AppException.IId id,
						URL              url,
						CharSequence...  replacements)
	{
		super(id, replacements);
		this.url = url;
	}

	//------------------------------------------------------------------

	public UrlException(AppException.IId id,
						URL              url,
						Throwable        cause,
						CharSequence...  replacements)
	{
		super(id, cause, replacements);
		this.url = url;
	}

	//------------------------------------------------------------------

	public UrlException(AppException exception,
						URL          url)
	{
		this(exception.getId(), url, exception.getCause(), exception.getReplacements());
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	@Override
	protected String getPrefix()
	{
		return ((url == null) ? null : url + "\n");
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public URL getUrl()
	{
		return url;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	URL	url;

}

//----------------------------------------------------------------------
