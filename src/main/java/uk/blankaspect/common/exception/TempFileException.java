/*====================================================================*\

TempFileException.java

Temporary file exception class.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.exception;

//----------------------------------------------------------------------


// IMPORTS


import java.io.File;

//----------------------------------------------------------------------


// TEMPORARY FILE EXCEPTION CLASS


public class TempFileException
	extends FileException
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	private static final	String	SAVED_STR	= "\nThe file was saved as:\n";

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	File	tempFile;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public TempFileException(AppException.IId id,
							 File             outFile,
							 File             tempFile)
	{
		super(id, outFile);
		this.tempFile = tempFile;
	}

	//------------------------------------------------------------------

	public TempFileException(AppException.IId id,
							 File             outFile,
							 Throwable        cause,
							 File             tempFile)
	{
		super(id, outFile, cause);
		this.tempFile = tempFile;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	@Override
	protected String getSuffix()
	{
		return SAVED_STR + getPathname(tempFile);
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
