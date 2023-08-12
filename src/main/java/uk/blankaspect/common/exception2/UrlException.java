/*====================================================================*\

UrlException.java

Class: URL exception.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.exception2;

//----------------------------------------------------------------------


// IMPORTS


import java.net.URL;

//----------------------------------------------------------------------


// CLASS: URL EXCEPTION


/**
 * This class implements a checked exception that is associated with a URL.
 */

public class UrlException
	extends LocationException
{

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** The URL with which this exception is associated. */
	private	URL	url;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of an exception with the specified detail message and associated URL.
	 *
	 * @param message
	 *          the detail message.
	 * @param url
	 *          the URL with which the exception will be associated, which may be {@code null}.
	 * @param replacements
	 *          the objects whose string representations will replace placeholders in {@code message}.
	 */

	public UrlException(
		String		message,
		URL			url,
		Object...	replacements)
	{
		// Call alternative constructor
		this(message, null, url, replacements);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of an exception with the specified detail message, cause and associated URL.
	 *
	 * @param message
	 *          the detail message.
	 * @param cause
	 *          the underlying cause of the exception, which may be {@code null}.
	 * @param url
	 *          the URL with which the exception will be associated, which may be {@code null}.
	 * @param replacements
	 *          the objects whose string representations will replace placeholders in {@code message}.
	 */

	public UrlException(
		String		message,
		Throwable	cause,
		URL			url,
		Object...	replacements)
	{
		// Call superclass constructor
		super(message, cause, (url == null) ? null : url.toString(), replacements);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of an exception from the detail message and cause of the specified exception and the
	 * specified URL.
	 *
	 * @param exception
	 *          the exception that will provide the detail message and underlying cause.
	 * @param url
	 *          the URL with which the exception will be associated, which may be {@code null}.
	 * @param replacements
	 *          the objects whose string representations will replace placeholders in {@code message}.
	 */

	public UrlException(
		BaseException	exception,
		URL				url,
		Object...		replacements)
	{
		// Call alternative constructor
		this(exception.getMessage(), exception.getCause(), url, replacements);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the URL with which this exception is associated.
	 *
	 * @return the URL with which this exception is associated.
	 */

	public URL getUrl()
	{
		return url;
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
