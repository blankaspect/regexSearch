/*====================================================================*\

TextFile.java

Text file class.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.misc;

//----------------------------------------------------------------------


// IMPORTS


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

import java.nio.channels.FileChannel;
import java.nio.channels.OverlappingFileLockException;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import uk.blankaspect.common.exception.AppException;
import uk.blankaspect.common.exception.FileException;
import uk.blankaspect.common.exception.TaskCancelledException;
import uk.blankaspect.common.exception.TempFileException;
import uk.blankaspect.common.exception.UriException;

import uk.blankaspect.common.filesystem.FilenameUtils;

//----------------------------------------------------------------------


// TEXT FILE CLASS


public class TextFile
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	private static final	int	BLOCK_LENGTH	= 1 << 16;  // 65536

	private static final	int	URI_CONNECTION_TIMEOUT	= 30_000;	// 30 seconds
	private static final	int	URI_READ_TIMEOUT		= 30_000;	// 30 seconds

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

		FAILED_TO_OPEN_FILE
		("Failed to open the file."),

		FAILED_TO_CLOSE_FILE
		("Failed to close the file."),

		FAILED_TO_LOCK_FILE
		("Failed to lock the file."),

		ERROR_READING_FILE
		("An error occurred when reading the file."),

		ERROR_WRITING_FILE
		("An error occurred when writing the file."),

		FAILED_TO_CREATE_TEMPORARY_FILE
		("Failed to create a temporary file."),

		FAILED_TO_DELETE_FILE
		("Failed to delete the existing file."),

		FAILED_TO_RENAME_FILE
		("Failed to rename the temporary file to the specified filename."),

		FILE_ACCESS_NOT_PERMITTED
		("Access to the file was not permitted."),

		WRITING_NOT_PERMITTED
		("Writing to the file was not permitted."),

		NOT_A_FILE
		("The pathname does not denote a normal file."),

		NOT_ENOUGH_MEMORY_TO_READ_FILE
		("There was not enough memory to read the file."),

		UNSUPPORTED_ENCODING
		("This implementation of Java does not support the %1 character encoding."),

		FILE_IS_TOO_LONG
		("The file is too long to be read by this program."),

		URI_IS_NOT_ABSOLUTE
		("The URI is not absolute."),

		FAILED_TO_CONVERT_URI_TO_URL
		("Failed to convert the URI to a URL."),

		FAILED_TO_CONNECT_TO_URI
		("Failed to open a connection to the URI."),

		READING_FROM_URI_NOT_SUPPORTED
		("Reading from the URI is not supported."),

		TIMED_OUT_READING_FROM_URI
		("Timed out when reading from the URI.");

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
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// TEXT BUFFER CLASS


	private static class TextBuffer
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private TextBuffer(boolean sync,
						   int     capacity)
		{
			this.sync = sync;
			if (sync)
				syncBuffer = new StringBuffer(capacity);
			else
				nonSyncBuffer = new StringBuilder(capacity);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		private Object getBuffer()
		{
			return (sync ? syncBuffer : nonSyncBuffer);
		}

		//--------------------------------------------------------------

		private int getLength()
		{
			return (sync ? syncBuffer.length() : nonSyncBuffer.length());
		}

		//--------------------------------------------------------------

		private void append(char[] data,
							int    length)
		{
			if (sync)
				syncBuffer.append(data, 0, length);
			else
				nonSyncBuffer.append(data, 0, length);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	boolean			sync;
		private	StringBuilder	nonSyncBuffer;
		private	StringBuffer	syncBuffer;

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * @throws IllegalArgumentException
	 *           if {@code file} is {@code null}.
	 */

	public TextFile(File file)
	{
		this(file, Charset.defaultCharset());
	}

	//------------------------------------------------------------------

	/**
	 * @throws IllegalArgumentException
	 *           if {@code file} is {@code null} or {@code charEncoding} is {@code null}.
	 */

	public TextFile(File    file,
					Charset charEncoding)
	{
		if ((file == null) || (charEncoding == null))
			throw new IllegalArgumentException();

		this.file = file;
		this.charEncoding = charEncoding;
		progressListeners = new ArrayList<>();
	}

	//------------------------------------------------------------------

	/**
	 * @throws NullPointerException
	 */

	public TextFile(URI uri)
	{
		this(uri, null);
	}

	//------------------------------------------------------------------

	/**
	 * @throws NullPointerException
	 */

	public TextFile(URI     uri,
					Charset charEncoding)
	{
		if (uri == null)
			throw new NullPointerException();

		this.uri = uri;
		this.charEncoding = charEncoding;
		progressListeners = new ArrayList<>();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static StringBuilder read(File file)
		throws AppException
	{
		return new TextFile(file).read();
	}

	//------------------------------------------------------------------

	public static StringBuilder read(URI uri)
		throws AppException
	{
		return new TextFile(uri).read();
	}

	//------------------------------------------------------------------

	public static StringBuilder read(File    file,
									 Charset charEncoding)
		throws AppException
	{
		return new TextFile(file, charEncoding).read();
	}

	//------------------------------------------------------------------

	public static StringBuilder read(URI     uri,
									 Charset charEncoding)
		throws AppException
	{
		return new TextFile(uri, charEncoding).read();
	}

	//------------------------------------------------------------------

	public static StringBuffer readSync(File file)
		throws AppException
	{
		return new TextFile(file).readSync();
	}

	//------------------------------------------------------------------

	public static StringBuffer readSync(URI uri)
		throws AppException
	{
		return new TextFile(uri).readSync();
	}

	//------------------------------------------------------------------

	public static StringBuffer readSync(File    file,
										Charset charEncoding)
		throws AppException
	{
		return new TextFile(file, charEncoding).readSync();
	}

	//------------------------------------------------------------------

	public static StringBuffer readSync(URI     uri,
										Charset charEncoding)
		throws AppException
	{
		return new TextFile(uri, charEncoding).readSync();
	}

	//------------------------------------------------------------------

	public static List<String> readLines(File file)
		throws AppException
	{
		return new TextFile(file).readLines();
	}

	//------------------------------------------------------------------

	public static List<String> readLines(URI uri)
		throws AppException
	{
		return new TextFile(uri).readLines();
	}

	//------------------------------------------------------------------

	public static List<String> readLines(File    file,
										 Charset charEncoding)
		throws AppException
	{
		return new TextFile(file, charEncoding).readLines();
	}

	//------------------------------------------------------------------

	public static List<String> readLines(URI     uri,
										 Charset charEncoding)
		throws AppException
	{
		return new TextFile(uri, charEncoding).readLines();
	}

	//------------------------------------------------------------------

	public static List<String> readLines(File file,
										 int  maxNumLines)
		throws AppException
	{
		return new TextFile(file).readLines(maxNumLines);
	}

	//------------------------------------------------------------------

	public static List<String> readLines(URI uri,
										 int maxNumLines)
		throws AppException
	{
		return new TextFile(uri).readLines(maxNumLines);
	}

	//------------------------------------------------------------------

	public static List<String> readLines(File    file,
										 Charset charEncoding,
										 int     maxNumLines)
		throws AppException
	{
		return new TextFile(file, charEncoding).readLines(maxNumLines);
	}

	//------------------------------------------------------------------

	public static List<String> readLines(URI     uri,
										 Charset charEncoding,
										 int     maxNumLines)
		throws AppException
	{
		return new TextFile(uri, charEncoding).readLines(maxNumLines);
	}

	//------------------------------------------------------------------

	public static void write(File         file,
							 CharSequence text)
		throws AppException
	{
		new TextFile(file).write(text, FileWritingMode.DIRECT);
	}

	//------------------------------------------------------------------

	public static void write(File         file,
							 Charset      charEncoding,
							 CharSequence text)
		throws AppException
	{
		new TextFile(file, charEncoding).write(text, FileWritingMode.DIRECT);
	}

	//------------------------------------------------------------------

	public static void write(File            file,
							 CharSequence    text,
							 FileWritingMode writeMode)
		throws AppException
	{
		new TextFile(file).write(text, writeMode);
	}

	//------------------------------------------------------------------

	public static void write(File            file,
							 Charset         charEncoding,
							 CharSequence    text,
							 FileWritingMode writeMode)
		throws AppException
	{
		new TextFile(file, charEncoding).write(text, writeMode);
	}

	//------------------------------------------------------------------

	public static EnumMap<LineSeparator, Integer> changeLineSeparators(StringBuilder text,
																	   boolean       count)
	{
		// Initialise counts and indices
		EnumMap<LineSeparator, Integer> lineSeparatorCounts = null;
		int inIndex = 0;
		int outIndex = 0;
		int endIndex = text.length();


		//----  Count and change line separators

		if (count)
		{
			// Initialise line separator counts
			int[] counts = new int[LineSeparator.values().length];

			// Count and change line separators
			while (inIndex < endIndex)
			{
				char ch = text.charAt(inIndex++);
				if (ch == '\r')
				{
					if ((inIndex < endIndex) && (text.charAt(inIndex) == '\n'))
					{
						++inIndex;
						++counts[LineSeparator.CR_LF.ordinal()];
					}
					else
						++counts[LineSeparator.CR.ordinal()];
					ch = '\n';
				}
				else
				{
					if (ch == '\n')
						++counts[LineSeparator.LF.ordinal()];
				}
				text.setCharAt(outIndex++, ch);
			}

			// Set line separator counts
			lineSeparatorCounts = new EnumMap<>(LineSeparator.class);
			for (LineSeparator lineSeparator : LineSeparator.values())
			{
				int numSeparators = counts[lineSeparator.ordinal()];
				if (numSeparators > 0)
					lineSeparatorCounts.put(lineSeparator, numSeparators);
			}
		}


		//----  Change line separators without counting them

		else
		{
			while (inIndex < endIndex)
			{
				char ch = text.charAt(inIndex++);
				if (ch == '\r')
				{
					if ((inIndex < endIndex) && (text.charAt(inIndex) == '\n'))
						++inIndex;
					ch = '\n';
				}
				text.setCharAt(outIndex++, ch);
			}
		}

		// Set length of text
		text.setLength(outIndex);

		// Return line separator counts
		return lineSeparatorCounts;
	}

	//------------------------------------------------------------------

	public static EnumMap<LineSeparator, Integer> changeLineSeparators(StringBuffer text,
																	   boolean      count)
	{
		// Initialise counts and indices
		EnumMap<LineSeparator, Integer> lineSeparatorCounts = null;
		int inIndex = 0;
		int outIndex = 0;
		int endIndex = text.length();


		//----  Count and change line separators

		if (count)
		{
			// Initialise line separator counts
			int[] counts = new int[LineSeparator.values().length];

			// Count and change line separators
			while (inIndex < endIndex)
			{
				char ch = text.charAt(inIndex++);
				if (ch == '\r')
				{
					if ((inIndex < endIndex) && (text.charAt(inIndex) == '\n'))
					{
						++inIndex;
						++counts[LineSeparator.CR_LF.ordinal()];
					}
					else
						++counts[LineSeparator.CR.ordinal()];
					ch = '\n';
				}
				else
				{
					if (ch == '\n')
						++counts[LineSeparator.LF.ordinal()];
				}
				text.setCharAt(outIndex++, ch);
			}

			// Set line separator counts
			lineSeparatorCounts = new EnumMap<>(LineSeparator.class);
			for (LineSeparator lineSeparator : LineSeparator.values())
			{
				int numSeparators = counts[lineSeparator.ordinal()];
				if (numSeparators > 0)
					lineSeparatorCounts.put(lineSeparator, numSeparators);
			}
		}


		//----  Change line separators without counting them

		else
		{
			while (inIndex < endIndex)
			{
				char ch = text.charAt(inIndex++);
				if (ch == '\r')
				{
					if ((inIndex < endIndex) && (text.charAt(inIndex) == '\n'))
						++inIndex;
					ch = '\n';
				}
				text.setCharAt(outIndex++, ch);
			}
		}

		// Set length of text
		text.setLength(outIndex);

		// Return line separator counts
		return lineSeparatorCounts;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public StringBuilder read()
		throws AppException
	{
		return (StringBuilder)(isFile() ? readFile(false, false) : readUri(false, false));
	}

	//------------------------------------------------------------------

	public StringBuffer readSync()
		throws AppException
	{
		return (StringBuffer)(isFile() ? readFile(false, true) : readUri(false, true));
	}

	//------------------------------------------------------------------

	public List<String> readLines()
		throws AppException
	{
		return isFile() ? readLinesFile(false, 0) : readLinesUri(false, 0);
	}

	//------------------------------------------------------------------

	public List<String> readLines(int maxNumLines)
		throws AppException
	{
		return isFile() ? readLinesFile(false, maxNumLines) : readLinesUri(false, maxNumLines);
	}

	//------------------------------------------------------------------

	/**
	 * @throws IllegalStateException
	 * @throws AppException
	 */

	public void write(CharSequence    text,
					  FileWritingMode writeMode)
		throws AppException
	{
		write(text, writeMode, false);
	}

	//------------------------------------------------------------------

	public void addProgressListener(IProgressListener listener)
	{
		progressListeners.add(listener);
	}

	//------------------------------------------------------------------

	public void removeProgressListener(IProgressListener listener)
	{
		progressListeners.remove(listener);
	}

	//------------------------------------------------------------------

	public IProgressListener[] getProgressListeners()
	{
		return progressListeners.toArray(IProgressListener[]::new);
	}

	//------------------------------------------------------------------

	protected boolean isFile()
	{
		return (file != null);
	}

	//------------------------------------------------------------------

	protected Object readFile(boolean compressed,
							  boolean sync)
		throws AppException
	{
		// Test file length
		if (file.length() >= Integer.MAX_VALUE)
			throw new FileException(ErrorId.FILE_IS_TOO_LONG, file);
		int fileLength = (int)file.length();

		// Read file
		FileInputStream inStream = null;
		try
		{
			// Open input stream on file
			FileChannel fileChannel = null;
			try
			{
				inStream = new FileInputStream(file);
				fileChannel = inStream.getChannel();
			}
			catch (SecurityException e)
			{
				throw new FileException(ErrorId.FILE_ACCESS_NOT_PERMITTED, file, e);
			}
			catch (FileNotFoundException e)
			{
				throw new FileException(ErrorId.FAILED_TO_OPEN_FILE, file, e);
			}

			// Lock file
			try
			{
				if (fileChannel.tryLock(0, Long.MAX_VALUE, true) == null)
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

			// Read file and return buffer
			try
			{
				return read(inStream, charEncoding, fileLength, compressed, sync);
			}
			catch (AppException e)
			{
				throw new FileException(e, file);
			}
		}
		catch (AppException e)
		{
			// Close input stream
			try
			{
				if (inStream != null)
					inStream.close();
			}
			catch (IOException e1)
			{
				// ignore
			}

			// Rethrow exception
			throw e;
		}
	}

	//------------------------------------------------------------------

	protected Object readUri(boolean compressed,
							 boolean sync)
		throws AppException
	{
		InputStream inStream = null;
		try
		{
			// Open connection to object specified by URI
			URLConnection connection = openConnection();

			// Get input stream for connection
			try
			{
				inStream = connection.getInputStream();
			}
			catch (IOException e)
			{
				throw new UriException(ErrorId.READING_FROM_URI_NOT_SUPPORTED, uri);
			}

			// Read file from connection and return buffer
			try
			{
				return read(inStream, getCharEncoding(connection), connection.getContentLength(), compressed, sync);
			}
			catch (AppException e)
			{
				throw new UriException(e, uri);
			}
		}
		catch (AppException e)
		{
			// Close input stream
			try
			{
				if (inStream != null)
					inStream.close();
			}
			catch (IOException e1)
			{
				// ignore
			}

			// Rethrow exception
			throw e;
		}
	}

	//------------------------------------------------------------------

	protected List<String> readLinesFile(boolean compressed,
										 int     maxNumLines)
		throws AppException
	{
		FileInputStream inStream = null;
		try
		{
			// Open input stream on file
			FileChannel fileChannel = null;
			try
			{
				inStream = new FileInputStream(file);
				fileChannel = inStream.getChannel();
			}
			catch (SecurityException e)
			{
				throw new FileException(ErrorId.FILE_ACCESS_NOT_PERMITTED, file, e);
			}
			catch (FileNotFoundException e)
			{
				throw new FileException(ErrorId.FAILED_TO_OPEN_FILE, file, e);
			}

			// Lock file
			try
			{
				if (fileChannel.tryLock(0, Long.MAX_VALUE, true) == null)
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

			// Read file and return lines
			try
			{
				return readLines(inStream, charEncoding, compressed, maxNumLines);
			}
			catch (AppException e)
			{
				throw new FileException(e, file);
			}
		}
		catch (AppException e)
		{
			// Close input stream
			try
			{
				if (inStream != null)
					inStream.close();
			}
			catch (IOException e1)
			{
				// ignore
			}

			// Rethrow exception
			throw e;
		}
	}

	//------------------------------------------------------------------

	protected List<String> readLinesUri(boolean compressed,
										int     maxNumLines)
		throws AppException
	{
		InputStream inStream = null;
		try
		{
			// Open connection to object specified by URI
			URLConnection connection = openConnection();

			// Get input stream for connection
			try
			{
				inStream = connection.getInputStream();
			}
			catch (IOException e)
			{
				throw new UriException(ErrorId.READING_FROM_URI_NOT_SUPPORTED, uri);
			}

			// Read file from connection and return buffer
			try
			{
				return readLines(inStream, getCharEncoding(connection), compressed, maxNumLines);
			}
			catch (AppException e)
			{
				throw new UriException(e, uri);
			}
		}
		catch (AppException e)
		{
			// Close input stream
			try
			{
				if (inStream != null)
					inStream.close();
			}
			catch (IOException e1)
			{
				// ignore
			}

			// Rethrow exception
			throw e;
		}
	}

	//------------------------------------------------------------------

	/**
	 * @throws IllegalStateException
	 * @throws AppException
	 */

	protected void write(CharSequence    text,
						 FileWritingMode writeMode,
						 boolean         compressed)
		throws AppException
	{
		// Test for file
		if (!isFile())
			throw new IllegalStateException();

		// Test whether file exists
		boolean exists = false;
		try
		{
			exists = file.exists();
		}
		catch (SecurityException e)
		{
			throw new FileException(ErrorId.FILE_ACCESS_NOT_PERMITTED, file, e);
		}

		// Write file
		if (exists)
		{
			switch (writeMode)
			{
				case DIRECT:
					writeDirect(text, compressed);
					break;

				case USE_TEMP_FILE:
					writeUsingTempFile(text, compressed, false);
					break;

				case USE_TEMP_FILE_PRESERVE_ATTRS:
					writeUsingTempFile(text, compressed, true);
					break;
			}
		}
		else
			writeDirect(text, compressed);
	}

	//------------------------------------------------------------------

	protected void writeDirect(CharSequence text,
							   boolean      compressed)
		throws AppException
	{
		OutputStreamWriter outStream = null;
		try
		{
			// Test for write access
			if (file.exists() && !file.canWrite())
				throw new FileException(ErrorId.WRITING_NOT_PERMITTED, file);

			// Open output stream on file
			FileChannel fileChannel = null;
			try
			{
				FileOutputStream outStream1 = new FileOutputStream(file);
				fileChannel = outStream1.getChannel();
				OutputStream outStream2 = compressed ? new GZIPOutputStream(outStream1) : outStream1;
				outStream = new OutputStreamWriter(outStream2, charEncoding);
			}
			catch (SecurityException e)
			{
				throw new FileException(ErrorId.FILE_ACCESS_NOT_PERMITTED, file, e);
			}
			catch (IOException e)
			{
				throw new FileException(ErrorId.FAILED_TO_OPEN_FILE, file, e);
			}

			// Lock file
			try
			{
				if (fileChannel.tryLock() == null)
					throw new FileException(ErrorId.FAILED_TO_LOCK_FILE, file);
			}
			catch (IOException e)
			{
				throw new FileException(ErrorId.FAILED_TO_LOCK_FILE, file, e);
			}

			// Write file
			try
			{
				writeText(text, outStream);
			}
			catch (IOException e)
			{
				throw new FileException(ErrorId.ERROR_WRITING_FILE, file, e);
			}

			// Close output stream
			try
			{
				outStream.close();
				outStream = null;
			}
			catch (IOException e)
			{
				throw new FileException(ErrorId.FAILED_TO_CLOSE_FILE, file, e);
			}
		}
		catch (AppException e)
		{
			// Close output stream
			try
			{
				if (outStream != null)
					outStream.close();
			}
			catch (Exception e1)
			{
				// ignore
			}

			// Rethrow exception
			throw e;
		}
	}

	//------------------------------------------------------------------

	protected void writeUsingTempFile(CharSequence text,
									  boolean      compressed,
									  boolean      preserveAttrs)
		throws AppException
	{
		File tempFile = null;
		OutputStreamWriter outStream = null;
		boolean oldFileDeleted = false;
		try
		{
			// Test for file
			if (!file.isFile())
				throw new FileException(ErrorId.NOT_A_FILE, file);

			// Test for write access
			if (!file.canWrite())
				throw new FileException(ErrorId.WRITING_NOT_PERMITTED, file);

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

			// Open output stream on temporary file
			FileChannel fileChannel = null;
			try
			{
				FileOutputStream outStream1 = new FileOutputStream(tempFile);
				fileChannel = outStream1.getChannel();
				OutputStream outStream2 = compressed ? new GZIPOutputStream(outStream1) : outStream1;
				outStream = new OutputStreamWriter(outStream2, charEncoding);
			}
			catch (SecurityException e)
			{
				throw new FileException(ErrorId.FILE_ACCESS_NOT_PERMITTED, tempFile, e);
			}
			catch (IOException e)
			{
				throw new FileException(ErrorId.FAILED_TO_OPEN_FILE, tempFile, e);
			}

			// Lock file
			try
			{
				if (fileChannel.tryLock() == null)
					throw new FileException(ErrorId.FAILED_TO_LOCK_FILE, tempFile);
			}
			catch (IOException e)
			{
				throw new FileException(ErrorId.FAILED_TO_LOCK_FILE, tempFile, e);
			}

			// Write file
			try
			{
				writeText(text, outStream);
			}
			catch (IOException e)
			{
				throw new FileException(ErrorId.ERROR_WRITING_FILE, tempFile, e);
			}

			// Close output stream
			try
			{
				outStream.close();
				outStream = null;
			}
			catch (IOException e)
			{
				throw new FileException(ErrorId.FAILED_TO_CLOSE_FILE, tempFile, e);
			}

			// Copy file attributes from current file to temporary file
			AppException fileAttributesException = null;
			if (preserveAttrs)
			{
				try
				{
					FileAttributeUtils.copyAttributes(file, tempFile);
				}
				catch (FileAttributeUtils.AttributesException e)
				{
					fileAttributesException = e;
				}
			}

			// Delete existing file
			try
			{
				if (!file.delete())
					throw new FileException(ErrorId.FAILED_TO_DELETE_FILE, file);
				oldFileDeleted = true;
			}
			catch (SecurityException e)
			{
				throw new FileException(ErrorId.FAILED_TO_DELETE_FILE, file, e);
			}

			// Rename temporary file
			try
			{
				if (!tempFile.renameTo(file))
					throw new TempFileException(ErrorId.FAILED_TO_RENAME_FILE, file, tempFile);
			}
			catch (SecurityException e)
			{
				throw new TempFileException(ErrorId.FAILED_TO_RENAME_FILE, file, e, tempFile);
			}

			// Throw any exception from copying file attributes
			if (fileAttributesException != null)
				throw fileAttributesException;
		}
		catch (AppException e)
		{
			// Close output stream
			try
			{
				if (outStream != null)
					outStream.close();
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

			// Rethrow exception
			throw e;
		}
	}

	//------------------------------------------------------------------

	private URLConnection openConnection()
		throws AppException
	{
		// Convert URI to URL
		URL url = null;
		try
		{
			url = uri.toURL();
		}
		catch (IllegalArgumentException e)
		{
			throw new UriException(ErrorId.URI_IS_NOT_ABSOLUTE, uri);
		}
		catch (MalformedURLException e)
		{
			throw new UriException(ErrorId.FAILED_TO_CONVERT_URI_TO_URL, uri, e);
		}

		// Open connection to object specified by URL
		URLConnection connection = null;
		try
		{
			connection = url.openConnection();
		}
		catch (IOException e)
		{
			throw new UriException(ErrorId.FAILED_TO_CONNECT_TO_URI, uri, e);
		}

		// Set connection and read timeouts
		connection.setConnectTimeout(URI_CONNECTION_TIMEOUT);
		connection.setReadTimeout(URI_READ_TIMEOUT);

		// Return connection
		return connection;
	}

	//------------------------------------------------------------------

	private Charset getCharEncoding(URLConnection connection)
	{
		Charset charEncoding = this.charEncoding;
		if (charEncoding == null)
		{
			String encodingName = connection.getContentEncoding();
			charEncoding = Charset.isSupported(encodingName) ? Charset.forName(encodingName) : StandardCharsets.UTF_8;
		}
		return charEncoding;
	}

	//------------------------------------------------------------------

	private Object read(InputStream inStream,
						Charset     charEncoding,
						int         length,
						boolean     compressed,
						boolean     sync)
		throws AppException
	{
		// Read file
		InputStreamReader reader = null;
		try
		{
			// Open reader on input stream
			try
			{
				reader = new InputStreamReader(compressed ? new GZIPInputStream(inStream) : inStream, charEncoding);
			}
			catch (IOException e)
			{
				throw new AppException(ErrorId.FAILED_TO_OPEN_FILE, e);
			}

			// Read file
			TextBuffer buffer = null;
			try
			{
				// Allocate buffer for file
				buffer = new TextBuffer(sync, (length < 0) ? BLOCK_LENGTH : length + 1);

				// Read file
				try
				{
					char[] readBuf = new char[BLOCK_LENGTH];
					while (true)
					{
						// Test whether task has been cancelled by a monitor
						for (IProgressListener listener : progressListeners)
						{
							if (listener.isTaskCancelled())
								throw new TaskCancelledException();
						}

						// Read block of data from input stream
						int readLength = reader.read(readBuf);
						if (readLength <= 0)
							break;
						buffer.append(readBuf, readLength);

						// Notify monitors of progress
						double progress = (length < 0) ? -1.0 : (double)buffer.getLength() / (double)length;
						for (IProgressListener listener : progressListeners)
							listener.setProgress(progress);
					}
				}
				catch (SocketTimeoutException e)
				{
					throw new AppException(ErrorId.TIMED_OUT_READING_FROM_URI);
				}
				catch (IOException e)
				{
					throw new AppException(ErrorId.ERROR_READING_FILE, e);
				}
			}
			catch (OutOfMemoryError e)
			{
				throw new AppException(ErrorId.NOT_ENOUGH_MEMORY_TO_READ_FILE);
			}

			// Close input stream
			try
			{
				reader.close();
				reader = null;
			}
			catch (IOException e)
			{
				throw new AppException(ErrorId.FAILED_TO_CLOSE_FILE, e);
			}

			// Return buffer
			return buffer.getBuffer();
		}
		catch (AppException e)
		{
			// Close input stream
			try
			{
				if (reader != null)
					reader.close();
			}
			catch (IOException e1)
			{
				// ignore
			}

			// Rethrow exception
			throw e;
		}
	}

	//------------------------------------------------------------------

	private List<String> readLines(InputStream inStream,
								   Charset     charEncoding,
								   boolean     compressed,
								   int         maxNumLines)
		throws AppException
	{
		List<String> lines = new ArrayList<>();
		BufferedReader reader = null;
		try
		{
			// Open reader on input stream
			try
			{
				reader = new BufferedReader(new InputStreamReader(compressed ? new GZIPInputStream(inStream) : inStream,
																  charEncoding));
			}
			catch (IOException e)
			{
				throw new AppException(ErrorId.FAILED_TO_OPEN_FILE, e);
			}

			// Read file
			while ((maxNumLines == 0) || (lines.size() < maxNumLines))
			{
				try
				{
					try
					{
						// Test whether task has been cancelled by a monitor
						for (IProgressListener listener : progressListeners)
						{
							if (listener.isTaskCancelled())
								throw new TaskCancelledException();
						}

						// Read line from input stream
						String line = reader.readLine();
						if (line == null)
							break;
						lines.add(line);

						// Notify monitors of progress
						double progress = (maxNumLines == 0) ? -1.0 : (double)lines.size() / (double)maxNumLines;
						for (IProgressListener listener : progressListeners)
							listener.setProgress(progress);
					}
					catch (SocketTimeoutException e)
					{
						throw new AppException(ErrorId.TIMED_OUT_READING_FROM_URI);
					}
					catch (IOException e)
					{
						throw new AppException(ErrorId.ERROR_READING_FILE, e);
					}
				}
				catch (OutOfMemoryError e)
				{
					throw new AppException(ErrorId.NOT_ENOUGH_MEMORY_TO_READ_FILE);
				}
			}

			// Close input stream
			try
			{
				reader.close();
				reader = null;
			}
			catch (IOException e)
			{
				throw new AppException(ErrorId.FAILED_TO_CLOSE_FILE, e);
			}

			// Return lines of text
			return lines;
		}
		catch (AppException e)
		{
			// Close input stream
			try
			{
				if (reader != null)
					reader.close();
			}
			catch (IOException e1)
			{
				// ignore
			}

			// Rethrow exception
			throw e;
		}
	}

	//------------------------------------------------------------------

	private void writeText(CharSequence       text,
						   OutputStreamWriter outStream)
		throws IOException, TaskCancelledException
	{
		int textLength = text.length();
		int offset = 0;
		while (offset < textLength)
		{
			// Test whether task has been cancelled by a monitor
			for (IProgressListener listener : progressListeners)
			{
				if (listener.isTaskCancelled())
					throw new TaskCancelledException();
			}

			// Write block of data to output stream
			int endOffset = Math.min(offset + BLOCK_LENGTH, textLength);
			outStream.append(text, offset, endOffset);
			offset = endOffset;

			// Notify monitor of progress
			for (IProgressListener listener : progressListeners)
				listener.setProgress((double)offset / (double)textLength);
		}
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	URI						uri;
	private	File					file;
	private	Charset					charEncoding;
	private	List<IProgressListener>	progressListeners;

}

//----------------------------------------------------------------------
