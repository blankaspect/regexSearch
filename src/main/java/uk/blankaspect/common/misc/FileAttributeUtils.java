/*====================================================================*\

FileAttributeUtils.java

File-attribute utility methods class.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.misc;

//----------------------------------------------------------------------


// IMPORTS


import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import uk.blankaspect.common.exception.AppException;
import uk.blankaspect.common.exception.ExceptionUtils;
import uk.blankaspect.common.exception.FileException;

//----------------------------------------------------------------------


// FILE-ATTRIBUTE UTILITY METHODS CLASS


public class FileAttributeUtils
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	private static final	int	MAX_PERMISSIONS_VALUE	= (1 << FilePermission.NUM_PERMISSIONS) - 1;

	private interface AttrName
	{
		String	GROUP		= "group";
		String	OWNER		= "owner";
		String	PERMISSIONS	= "permissions";
	}
	private interface AttrCommand
	{
		String	GROUP		= "chgrp";
		String	OWNER		= "chown";
		String	PERMISSIONS	= "chmod";
	}

	private static final	String[]	SET_ARGUMENTS	= { "", "--quiet" };
	private static final	String[]	COPY_ARGUMENTS	= { "", "--quiet", "--reference" };

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


	// ATTRIBUTES EXCEPTION CLASS


	public static class AttributesException
		extends FileException
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private AttributesException(AppException.IId id,
									File             file)
		{
			super(id, file);
		}

		//--------------------------------------------------------------

		private AttributesException(AppException exception,
									File         file)
		{
			super(exception, file);
		}

		//--------------------------------------------------------------

	}

	//==================================================================

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

	public static void setAttributes(File   file,
									 int    permissions,
									 String owner,
									 String group)
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
		Collections.addAll(arguments, SET_ARGUMENTS);
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
				execProcess(arguments, AttrName.PERMISSIONS, AttrCommand.PERMISSIONS);
			}

			// Set owner and/or group
			if (owner.isEmpty())
			{
				// Set group
				if (!group.isEmpty())
				{
					arguments.set(argIndex, group);
					execProcess(arguments, AttrName.GROUP, AttrCommand.GROUP);
				}
			}

			else
			{
				// Set owner and group
				arguments.set(argIndex, group.isEmpty() ? owner : owner + ":" + group);
				execProcess(arguments, AttrName.OWNER, AttrCommand.OWNER);
			}
		}
		catch (AppException e)
		{
			throw new AttributesException(e, file);
		}
	}

	//------------------------------------------------------------------

	public static void copyAttributes(File sourceFile,
									  File destFile)
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
		Collections.addAll(arguments, COPY_ARGUMENTS);
		arguments.add(getPathname(sourceFile));
		arguments.add(getPathname(destFile));
		try
		{
			execProcess(arguments, AttrName.PERMISSIONS, AttrCommand.PERMISSIONS);
			execProcess(arguments, AttrName.OWNER, AttrCommand.OWNER);
		}
		catch (AppException e)
		{
			throw new AttributesException(e, sourceFile);
		}
	}

	//------------------------------------------------------------------

	private static String getPathname(File file)
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

	private static void execProcess(List<String> arguments,
									String       attrName,
									String       command)
		throws AppException
	{
		// Start process
		Process process = null;
		try
		{
			arguments.set(0, command);
			ProcessBuilder processBuilder = new ProcessBuilder(arguments);
			processBuilder.redirectErrorStream(true);
			process = processBuilder.start();
		}
		catch (IOException e)
		{
			throw new AppException(ErrorId.FAILED_TO_EXECUTE_COMMAND, e, command);
		}

		// Wait for process to terminate
		BufferedReader inReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		try
		{
			while (true)
			{
				// Read output from process and write to standard output
				try
				{
					while (inReader.ready())
						System.out.println(inReader.readLine());
				}
				catch (IOException e)
				{
					// ignore
				}

				// Allow process to execute
				Thread.yield();

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
		catch (AppException e)
		{
			throw e;
		}
		finally
		{
			// Read residual output from process and write to standard output
			try
			{
				while (inReader.ready())
					System.out.println(inReader.readLine());
			}
			catch (IOException e)
			{
				// ignore
			}
		}
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
