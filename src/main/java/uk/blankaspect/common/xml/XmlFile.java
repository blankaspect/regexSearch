/*====================================================================*\

XmlFile.java

XML file class.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.xml;

//----------------------------------------------------------------------


// IMPORTS


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;

import java.net.URI;
import java.net.URL;

import java.nio.channels.OverlappingFileLockException;

import java.nio.charset.StandardCharsets;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import uk.blankaspect.common.exception.AppException;
import uk.blankaspect.common.exception.FileException;
import uk.blankaspect.common.exception.TempFileException;
import uk.blankaspect.common.exception.UrlException;

import uk.blankaspect.common.filesystem.FilenameUtils;

//----------------------------------------------------------------------


// XML FILE CLASS


public class XmlFile
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	private static final	String	XML_VERSION_STR	= "1.0";

	private static final	String	FILE_STR	= "file";

////////////////////////////////////////////////////////////////////////
//  Enumerated types
////////////////////////////////////////////////////////////////////////


	// ERROR IDENTIFIERS


	private enum ErrorId
		implements AppException.IId
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		FILE_DOES_NOT_EXIST
		("The %1 does not exist."),

		FAILED_TO_OPEN_FILE
		("Failed to open the %1."),

		FAILED_TO_CLOSE_FILE
		("Failed to close the %1."),

		FAILED_TO_LOCK_FILE
		("Failed to lock the %1."),

		ERROR_READING_FILE
		("An error occurred while reading the %1."),

		ERROR_WRITING_FILE
		("An error occurred while writing the %1."),

		FILE_ACCESS_NOT_PERMITTED
		("Access to the %1 was not permitted."),

		FAILED_TO_CREATE_DIRECTORY
		("Failed to create the directory."),

		FAILED_TO_CREATE_TEMPORARY_FILE
		("Failed to create a temporary %1."),

		FAILED_TO_DELETE_FILE
		("Failed to delete the existing %1."),

		FAILED_TO_RENAME_FILE
		("Failed to rename the temporary %1."),

		NOT_ENOUGH_MEMORY
		("There was not enough memory to read the %1."),

		INVALID_DOCUMENT
		("The document is not valid."),

		UNEXPECTED_DOCUMENT_FORMAT
		("The document does not have the expected format.");

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private ErrorId(String message)
		{
			this.message = message;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : AppException.IId interface
	////////////////////////////////////////////////////////////////////

		public String getMessage()
		{
			return message;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	String	message;

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Member interfaces
////////////////////////////////////////////////////////////////////////


	// ELEMENT WRITER INTERFACE


	public interface IElementWriter
	{

	////////////////////////////////////////////////////////////////////
	//  Methods
	////////////////////////////////////////////////////////////////////

		void writeElement(XmlWriter writer,
						  Element   element,
						  int       indent)
			throws IOException;

		//--------------------------------------------------------------

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	private XmlFile()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static Document read(File file)
		throws AppException
	{
		return read(file, null, false);
	}

	//------------------------------------------------------------------

	public static Document read(File file,
								URI  baseUri)
		throws AppException
	{
		return read(file, baseUri, false);
	}

	//------------------------------------------------------------------

	public static Document read(File    file,
								URI     baseUri,
								boolean validate)
		throws AppException
	{
		FileInputStream inStream = null;
		try
		{
			// Test for file
			try
			{
				if (!file.isFile())
					throw new FileException(ErrorId.FILE_DOES_NOT_EXIST, file);
			}
			catch (SecurityException e)
			{
				throw new FileException(ErrorId.FILE_ACCESS_NOT_PERMITTED, file);
			}

			// Open input stream on file
			try
			{
				inStream = new FileInputStream(file);
			}
			catch (FileNotFoundException e)
			{
				throw new FileException(ErrorId.FAILED_TO_OPEN_FILE, file);
			}
			catch (SecurityException e)
			{
				throw new FileException(ErrorId.FILE_ACCESS_NOT_PERMITTED, file);
			}

			// Lock file
			try
			{
				if (inStream.getChannel().tryLock(0, Long.MAX_VALUE, true) == null)
					throw new FileException(ErrorId.FAILED_TO_LOCK_FILE, file);
			}
			catch (OverlappingFileLockException e)
			{
				// ignore
			}
			catch (IOException e)
			{
				throw new FileException(ErrorId.FAILED_TO_LOCK_FILE, file, e);
			}

			// Test for XML file
			try
			{
				if (!XmlUtils.isXml(file))
					throw new FileException(ErrorId.UNEXPECTED_DOCUMENT_FORMAT, file);
			}
			catch (FileNotFoundException e)
			{
				throw new FileException(ErrorId.FAILED_TO_OPEN_FILE, file, e);
			}
			catch (IOException e)
			{
				throw new FileException(ErrorId.ERROR_READING_FILE, file, e);
			}
			catch (SecurityException e)
			{
				throw new FileException(ErrorId.FILE_ACCESS_NOT_PERMITTED, file, e);
			}

			// Read and parse file
			XmlUtils.getErrorHandler().clear();
			Document document = null;
			try
			{
				document = XmlUtils.createDocument(inStream, baseUri, validate);
			}
			catch (OutOfMemoryError e)
			{
				throw new FileException(ErrorId.NOT_ENOUGH_MEMORY, file);
			}
			catch (AppException e)
			{
				throw new FileException(e, file);
			}
			if (!XmlUtils.getErrorHandler().isEmpty())
				throw new XmlValidationException(ErrorId.INVALID_DOCUMENT, file,
												 XmlUtils.getErrorHandler().getErrorStrings());

			// Close input stream
			try
			{
				inStream.close();
				inStream = null;
			}
			catch (IOException e)
			{
				throw new FileException(ErrorId.FAILED_TO_CLOSE_FILE, file);
			}

			// Return document
			return document;
		}
		catch (AppException e)
		{
			// Close input stream
			try
			{
				if (inStream != null)
					inStream.close();
			}
			catch (Exception e1)
			{
				// ignore
			}

			// Set default file type in exception
			e.setReplacements(FILE_STR);

			// Rethrow exception
			throw e;
		}
	}

	//------------------------------------------------------------------

	public static Document read(URL url)
		throws AppException
	{
		return read(url, null, false);
	}

	//------------------------------------------------------------------

	public static Document read(URL url,
								URI baseUri)
		throws AppException
	{
		return read(url, baseUri, false);
	}

	//------------------------------------------------------------------

	public static Document read(URL     url,
								URI     baseUri,
								boolean validate)
		throws AppException
	{
		InputStream inStream = null;
		try
		{
			// Open input stream on URL
			try
			{
				inStream = url.openStream();
			}
			catch (SecurityException e)
			{
				throw new UrlException(ErrorId.FILE_ACCESS_NOT_PERMITTED, url);
			}
			catch (IOException e)
			{
				throw new UrlException(ErrorId.FAILED_TO_OPEN_FILE, url);
			}

			// Read and parse file
			XmlUtils.getErrorHandler().clear();
			Document document = null;
			try
			{
				document = XmlUtils.createDocument(inStream, baseUri, validate);
			}
			catch (OutOfMemoryError e)
			{
				throw new UrlException(ErrorId.NOT_ENOUGH_MEMORY, url);
			}
			catch (AppException e)
			{
				throw new UrlException(e, url);
			}
			if (!XmlUtils.getErrorHandler().isEmpty())
				throw new XmlValidationException(ErrorId.INVALID_DOCUMENT, url,
												 XmlUtils.getErrorHandler().getErrorStrings());

			// Close input stream
			try
			{
				inStream.close();
				inStream = null;
			}
			catch (IOException e)
			{
				throw new UrlException(ErrorId.FAILED_TO_CLOSE_FILE, url);
			}

			// Return document
			return document;
		}
		catch (AppException e)
		{
			// Close input stream
			try
			{
				if (inStream != null)
					inStream.close();
			}
			catch (Exception e1)
			{
				// ignore
			}

			// Set default file type in exception
			e.setReplacements(FILE_STR);

			// Rethrow exception
			throw e;
		}
	}

	//------------------------------------------------------------------

	/**
	 * @throws IllegalArgumentException
	 * @throws AppException
	 */

	public static void write(File           file,
							 Document       document,
							 IElementWriter elementWriter)
		throws AppException
	{
		write(file, document, XmlWriter.Standalone.NONE, elementWriter);
	}

	//------------------------------------------------------------------

	/**
	 * @throws IllegalArgumentException
	 * @throws AppException
	 */

	public static void write(File                 file,
							 Document             document,
							 XmlWriter.Standalone standalone,
							 IElementWriter       elementWriter)
		throws AppException
	{
		// Validate arguments
		if ((file == null) || (document == null))
			throw new IllegalArgumentException();

		// Write file
		File tempFile = null;
		XmlWriter writer = null;
		boolean oldFileDeleted = false;
		try
		{
			// Create parent directory of output file
			File directory = file.getAbsoluteFile().getParentFile();
			if ((directory != null) && !directory.exists())
			{
				try
				{
					if (!directory.mkdirs())
						throw new FileException(ErrorId.FAILED_TO_CREATE_DIRECTORY, directory);
				}
				catch (SecurityException e)
				{
					throw new FileException(ErrorId.FAILED_TO_CREATE_DIRECTORY, directory, e);
				}
			}

			// Create temporary file
			try
			{
				tempFile = FilenameUtils.tempLocation(file);
				tempFile.createNewFile();
			}
			catch (Exception e)
			{
				throw new AppException(ErrorId.FAILED_TO_CREATE_TEMPORARY_FILE, e);
			}

			// Open XML writer on temporary file
			try
			{
				writer = new XmlWriter(tempFile, StandardCharsets.UTF_8);
			}
			catch (FileNotFoundException e)
			{
				throw new FileException(ErrorId.FAILED_TO_OPEN_FILE, tempFile, e);
			}
			catch (SecurityException e)
			{
				throw new FileException(ErrorId.FILE_ACCESS_NOT_PERMITTED, tempFile, e);
			}

			// Lock file
			try
			{
				if (writer.getFileOutStream().getChannel().tryLock() == null)
					throw new FileException(ErrorId.FAILED_TO_LOCK_FILE, tempFile);
			}
			catch (IOException e)
			{
				throw new FileException(ErrorId.FAILED_TO_LOCK_FILE, tempFile, e);
			}

			// Write file
			try
			{
				writer.writeXmlDeclaration(XML_VERSION_STR, XmlConstants.ENCODING_NAME_UTF8, standalone);
				elementWriter.writeElement(writer, document.getDocumentElement(), 0);
			}
			catch (IOException e)
			{
				throw new FileException(ErrorId.ERROR_WRITING_FILE, tempFile, e);
			}

			// Close output stream
			try
			{
				writer.close();
				writer = null;
			}
			catch (IOException e)
			{
				throw new FileException(ErrorId.FAILED_TO_CLOSE_FILE, tempFile, e);
			}

			// Delete any existing file
			try
			{
				if (file.exists() && !file.delete())
					throw new FileException(ErrorId.FAILED_TO_DELETE_FILE, file);
				oldFileDeleted = true;
			}
			catch (SecurityException e)
			{
				throw new FileException(ErrorId.FAILED_TO_DELETE_FILE, file);
			}

			// Rename temporary file
			try
			{
				if (!tempFile.renameTo(file))
					throw new TempFileException(ErrorId.FAILED_TO_RENAME_FILE, file, tempFile);
			}
			catch (SecurityException e)
			{
				throw new TempFileException(ErrorId.FAILED_TO_RENAME_FILE, file, tempFile);
			}
		}
		catch (AppException e)
		{
			// Close output stream
			try
			{
				if (writer != null)
					writer.close();
			}
			catch (Exception e1)
			{
				// ignore
			}

			// Delete temporary file
			try
			{
				if (!oldFileDeleted && (tempFile != null) && tempFile.exists())
					tempFile.delete();
			}
			catch (Exception e1)
			{
				// ignore
			}

			// Set default file type in exception
			e.setReplacements(FILE_STR);

			// Rethrow exception
			throw e;
		}
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
