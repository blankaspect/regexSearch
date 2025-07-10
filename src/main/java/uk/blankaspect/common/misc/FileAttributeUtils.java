/*====================================================================*\

FileAttributeUtils.java

Class: file-attribute utility methods.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.misc;

//----------------------------------------------------------------------


// IMPORTS


import java.io.File;
import java.io.InputStreamReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import uk.blankaspect.common.exception.AppException;
import uk.blankaspect.common.exception.ExceptionUtils;
import uk.blankaspect.common.exception.FileException;

//----------------------------------------------------------------------


// CLASS: FILE-ATTRIBUTE UTILITY METHODS


public class FileAttributeUtils
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	private static final	int	MAX_PERMISSIONS_VALUE	= (1 << FilePermission.NUM_PERMISSIONS) - 1;

	private static final	int	PROCESS_OUTPUT_BUFFER_LENGTH	= 512;

	private static final	List<String>	SET_ARGUMENTS	= List.of("", "--quiet");
	private static final	List<String>	COPY_ARGUMENTS	= List.of("", "--quiet", "--reference");

	private interface AttrName
	{
		String	GROUP		= "group";
		String	OWNER		= "owner";
		String	PERMISSIONS	= "permissions";
	}
	private interface AttrCommand
	{
		String	CHANGE_GROUP		= "chgrp";
		String	CHANGE_OWNER		= "chown";
		String	CHANGE_PERMISSIONS	= "chmod";
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private FileAttributeUtils()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * @throws IllegalArgumentException
	 * @throws AttributesException
	 */

	public static void setAttributes(
		File	file,
		int		permissions,
		String	owner,
		String	group)
		throws AttributesException
	{
		// Validate arguments
		if (permissions > MAX_PERMISSIONS_VALUE)
			throw new IllegalArgumentException();

		// Test whether file or directory exists
		try
		{
			if (!file.exists())
				throw new AttributesException(ErrorId.FILE_OR_DIRECTORY_DOES_NOT_EXIST, file);
		}
		catch (SecurityException e)
		{
			throw new AttributesException(ErrorId.FILE_ACCESS_NOT_PERMITTED, file);
		}

		// Execute commands to set file attributes
		List<String> arguments = new ArrayList<>();
		arguments.addAll(SET_ARGUMENTS);
		int argIndex = arguments.size();
		arguments.add("");
		arguments.add(getPathname(file));
		try
		{
			// Set permissions
			if (permissions >= 0)
			{
				char digits[] = new char[FilePermission.NUM_SETS];
				int mask = (1 << FilePermission.PERMISSIONS_PER_SET) - 1;
				for (int i = digits.length - 1; i >= 0; i--)
				{
					digits[i] = (char)('0' + (permissions & mask));
					permissions >>>= FilePermission.PERMISSIONS_PER_SET;
				}
				arguments.set(argIndex, new String(digits));
				execProcess(AttrName.PERMISSIONS, AttrCommand.CHANGE_PERMISSIONS, arguments);
			}

			// Set owner and/or group
			if (owner.isEmpty())
			{
				// Set group
				if (!group.isEmpty())
				{
					arguments.set(argIndex, group);
					execProcess(AttrName.GROUP, AttrCommand.CHANGE_GROUP, arguments);
				}
			}

			else
			{
				// Set owner and group
				arguments.set(argIndex, group.isEmpty() ? owner : owner + ":" + group);
				execProcess(AttrName.OWNER, AttrCommand.CHANGE_OWNER, arguments);
			}
		}
		catch (AppException e)
		{
			throw new AttributesException(e, file);
		}
	}

	//------------------------------------------------------------------

	public static void copyAttributes(
		File	sourceFile,
		File	destFile)
		throws AttributesException
	{
		// Test for source file
		try
		{
			if (!sourceFile.exists())
				throw new AttributesException(ErrorId.SOURCE_FILE_DOES_NOT_EXIST, sourceFile);
			if (!sourceFile.isFile())
				throw new AttributesException(ErrorId.NOT_A_FILE, sourceFile);
		}
		catch (SecurityException e)
		{
			throw new AttributesException(ErrorId.FILE_ACCESS_NOT_PERMITTED, sourceFile);
		}

		// Test for destination file
		try
		{
			if (!destFile.exists())
				throw new AttributesException(ErrorId.DESTINATION_FILE_DOES_NOT_EXIST, destFile);
			if (!destFile.isFile())
				throw new AttributesException(ErrorId.NOT_A_FILE, destFile);
		}
		catch (SecurityException e)
		{
			throw new AttributesException(ErrorId.FILE_ACCESS_NOT_PERMITTED, destFile);
		}

		// Execute commands to copy file attributes
		List<String> arguments = new ArrayList<>();
		arguments.addAll(COPY_ARGUMENTS);
		arguments.add(getPathname(sourceFile));
		arguments.add(getPathname(destFile));
		try
		{
			execProcess(AttrName.PERMISSIONS, AttrCommand.CHANGE_PERMISSIONS, arguments);
			execProcess(AttrName.OWNER, AttrCommand.CHANGE_OWNER, arguments);
		}
		catch (AppException e)
		{
			throw new AttributesException(e, sourceFile);
		}
	}

	//------------------------------------------------------------------

	private static String getPathname(
		File	file)
	{
		String pathname = null;
		try
		{
			pathname = file.getCanonicalPath();
		}
		catch (Exception e)
		{
			ExceptionUtils.printTopOfStack(e);
			pathname = file.getAbsolutePath();
		}
		return pathname;
	}

	//------------------------------------------------------------------

	private static void execProcess(
		String			attrName,
		String			command,
		List<String>	arguments)
		throws AppException
	{
		// Start process
		Process process0 = null;
		try
		{
			List<String> args = new ArrayList<>();
			args.add(command);
			args.addAll(arguments);
			process0 = new ProcessBuilder(args).redirectErrorStream(true).start();
		}
		catch (Exception e)
		{
			throw new AppException(ErrorId.FAILED_TO_EXECUTE_COMMAND, e, command);
		}
		Process process = process0;

		// Create thread to handle output from process
		Thread outputThread = new Thread(() ->
		{
			InputStreamReader reader = new InputStreamReader(process.getInputStream());
			char[] buffer = new char[PROCESS_OUTPUT_BUFFER_LENGTH];
			while (!Thread.currentThread().isInterrupted())
			{
				// Display output from process
				try
				{
					while (reader.ready())
					{
						int length = reader.read(buffer, 0, buffer.length);
						if (length > 0)
							System.out.print(new String(buffer, 0, length));
					}
				}
				catch (IOException e)
				{
					// ignore
				}
			}
		});
		outputThread.start();

		// Wait for process to terminate
		try
		{
			while (true)
			{
				// Allow process to execute
				try
				{
					Thread.sleep(100);
				}
				catch (InterruptedException e)
				{
					// ignore
				}

				// Test whether process has terminated
				try
				{
					int exitValue = process.exitValue();
					if (exitValue != 0)
						throw new AppException(ErrorId.FAILED_TO_SET_ATTRIBUTE, attrName);
					break;
				}
				catch (IllegalThreadStateException e)
				{
					// ignore
				}
			}
		}
		finally
		{
			// Stop output thread
			outputThread.interrupt();

			// Wait for output thread to finish
			while (outputThread.isAlive())
			{
				try
				{
					Thread.sleep(50);
				}
				catch (InterruptedException e)
				{
					// ignore
				}
			}
		}
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Enumerated types
////////////////////////////////////////////////////////////////////////


	// ENUMERATION: ERROR IDENTIFIERS


	private enum ErrorId
		implements AppException.IId
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		FILE_OR_DIRECTORY_DOES_NOT_EXIST
		("The file or directory does not exist."),

		SOURCE_FILE_DOES_NOT_EXIST
		("The source file does not exist."),

		DESTINATION_FILE_DOES_NOT_EXIST
		("The destination file does not exist."),

		NOT_A_FILE
		("The pathname does not denote a normal file."),

		FILE_ACCESS_NOT_PERMITTED
		("Access to the file was not permitted."),

		FAILED_TO_EXECUTE_COMMAND
		("Failed to execute the command %1."),

		FAILED_TO_SET_ATTRIBUTE
		("Failed to set the file %1.");

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	String	message;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private ErrorId(
			String	message)
		{
			this.message = message;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : AppException.IId interface
	////////////////////////////////////////////////////////////////////

		@Override
		public String getMessage()
		{
			return message;
		}

		//--------------------------------------------------------------

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// CLASS: ATTRIBUTES EXCEPTION


	public static class AttributesException
		extends FileException
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private AttributesException(
			AppException.IId	id,
			File				file)
		{
			super(id, file);
		}

		//--------------------------------------------------------------

		private AttributesException(
			AppException	exception,
			File			file)
		{
			super(exception, file);
		}

		//--------------------------------------------------------------

	}

	//==================================================================

}

//----------------------------------------------------------------------
