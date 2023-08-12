/*====================================================================*\

UriException.java

URI exception class.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.exception;

//----------------------------------------------------------------------


// IMPORTS


import java.net.URI;

//----------------------------------------------------------------------


// URI EXCEPTION CLASS


public class UriException
	extends AppException
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public UriException(AppException.IId id,
						URI              uri)
	{
		super(id);
		this.uri = uri;
	}

	//------------------------------------------------------------------

	public UriException(AppException.IId id,
						URI              uri,
						Throwable        cause)
	{
		super(id, cause);
		this.uri = uri;
	}

	//------------------------------------------------------------------

	public UriException(AppException.IId id,
						URI              uri,
						CharSequence...  replacements)
	{
		super(id, replacements);
		this.uri = uri;
	}

	//------------------------------------------------------------------

	public UriException(AppException.IId id,
						URI              uri,
						Throwable        cause,
						CharSequence...  replacements)
	{
		super(id, cause, replacements);
		this.uri = uri;
	}

	//------------------------------------------------------------------

	public UriException(AppException exception,
						URI          uri)
	{
		this(exception.getId(), uri, exception.getCause(), exception.getReplacements());
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	@Override
	protected String getPrefix()
	{
		return ((uri == null) ? null : uri + "\n");
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public URI getUri()
	{
		return uri;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	URI	uri;

}

//----------------------------------------------------------------------
