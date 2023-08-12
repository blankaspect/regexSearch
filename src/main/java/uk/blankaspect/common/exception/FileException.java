/*====================================================================*\

FileException.java

File exception class.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.exception;

//----------------------------------------------------------------------


// IMPORTS


import java.io.File;

//----------------------------------------------------------------------


// FILE EXCEPTION CLASS


public class FileException
	extends AppException
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	private static final	int	MAX_PATHNAME_LENGTH	= 160;

	private static final	String	LOCATION_STR	= "Location: ";

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public FileException(AppException.IId id,
						 File             file)
	{
		super(id);
		this.file = file;
	}

	//------------------------------------------------------------------

	public FileException(AppException.IId id,
						 File             file,
						 Throwable        cause)
	{
		super(id, cause);
		this.file = file;
	}

	//------------------------------------------------------------------

	public FileException(AppException.IId id,
						 File             file,
						 CharSequence...  replacements)
	{
		super(id, replacements);
		this.file = file;
	}

	//------------------------------------------------------------------

	public FileException(AppException.IId id,
						 File             file,
						 Throwable        cause,
						 CharSequence...  replacements)
	{
		super(id, cause, replacements);
		this.file = file;
	}

	//------------------------------------------------------------------

	public FileException(AppException exception,
						 File         file)
	{
		this(exception.getId(), file, exception.getCause(), exception.getReplacements());
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
		return ((file == null) ? null : LOCATION_STR + getPathname() + "\n");
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public File getFile()
	{
		return file;
	}

	//------------------------------------------------------------------

	public String getPathname()
	{
		return getPathname(file);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	File	file;

}

//----------------------------------------------------------------------
