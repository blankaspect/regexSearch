/*====================================================================*\

ErrorLogger.java

Class: error logger.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.logging;

//----------------------------------------------------------------------


// IMPORTS


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import java.nio.charset.StandardCharsets;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import java.time.LocalDateTime;

import java.time.format.DateTimeFormatter;

//----------------------------------------------------------------------


// CLASS: ERROR LOGGER


public class ErrorLogger
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	public static final		ErrorLogger	INSTANCE	= new ErrorLogger();

	private static final	String	FILENAME	= "error.log";

	private static final	DateTimeFormatter	TIMESTAMP_FORMATTER	=
			DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss.SSS");

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	private ErrorLogger()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public void write(
		Throwable	exception)
		throws IOException
	{
		// Initialise buffer
		StringBuilder buffer = new StringBuilder(1024);

		// Append timestamp
		buffer.append("[ ");
		buffer.append(TIMESTAMP_FORMATTER.format(LocalDateTime.now()));
		buffer.append(" ]\n");

		// Append stack trace
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		exception.printStackTrace(new PrintStream(outStream, true, StandardCharsets.UTF_8));
		buffer.append(outStream.toString(StandardCharsets.UTF_8).replace("\r\n", "\n"));

		// Write buffer to file
		write(buffer.toString());
	}

	//------------------------------------------------------------------

	public void write(
		String	str)
		throws IOException
	{
		Files.writeString(Path.of(FILENAME), str, StandardOpenOption.WRITE, StandardOpenOption.CREATE,
						  StandardOpenOption.APPEND);
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
