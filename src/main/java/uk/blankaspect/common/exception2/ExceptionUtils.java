/*====================================================================*\

ExceptionUtils.java

Class: exception-related utilities.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.exception2;

//----------------------------------------------------------------------


// IMPORTS


import java.io.CharArrayWriter;
import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.List;

import java.util.stream.Collectors;

import uk.blankaspect.common.stack.StackUtils;

//----------------------------------------------------------------------


// CLASS: EXCEPTION-RELATED UTILITIES


public class ExceptionUtils
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	private ExceptionUtils()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a list of string representations of the stack traces of the specified exception and its chain of causes.
	 *
	 * @param  exception
	 *           the exception for which string representations of stack traces are required.
	 * @return a list of string representations of the stack traces of {@code exception} and its chain of causes.
	 */

	public static List<String> getStackTraceStrings(
		Throwable	exception)
	{
		// Initialise list of string representations of stack traces
		List<String> strs = new ArrayList<>();

		// Initialise underlying writer for stack trace
		CharArrayWriter writer = new CharArrayWriter(1024);

		// Add string representations of stack traces to list
		while (exception != null)
		{
			// Reset underlying writer
			writer.reset();

			// Get string representation of stack trace of exception
			exception.printStackTrace(new PrintWriter(writer, true));

			// Add string representation of stack trace to list
			strs.add(writer.toString().replace("\r\n", "\n"));

			// Get next exception in chain of causes
			exception = exception.getCause();
		}

		// Return list of string representations of stack traces
		return strs;
	}

	//------------------------------------------------------------------

	/**
	 * Returns a list of string representations of the chain of causes whose first element is the specified exception.
	 *
	 * @param  cause
	 *           the proximate cause.
	 * @return a list of string representations of the chain of causes whose first element is {@code cause}.
	 */

	public static List<String> getCauseStrings(
		Throwable	cause)
	{
		// Initialise list of cause strings
		List<String> causes = new ArrayList<>();

		// Add cause strings to list
		while (cause != null)
		{
			// Get detail message of cause
			String str = cause.getMessage();

			// If there is no detail message, use string representation of cause
			if (str == null)
				str = cause.toString();

			// Add string to list
			causes.add(str);

			// Get next exception in chain of causes
			cause = cause.getCause();
		}

		// Return list of cause strings
		return causes;
	}

	//------------------------------------------------------------------

	/**
	 * Returns a composite string representation of the chain of causes whose first element is the specified exception.
	 * The string representations of adjacent component causes are separated by a LF (U+000A).
	 *
	 * @param  cause
	 *           the proximate cause.
	 * @param  prefix
	 *           the string that will be prefixed to the string representations of each component cause.
	 * @return a composite string representation of the chain of causes whose first element is {@code cause}.
	 */

	public static String getCompositeCauseString(
		Throwable	cause,
		String		prefix)
	{
		List<String> causeStrs = getCauseStrings(cause);
		return causeStrs.isEmpty() ? "" : causeStrs.stream().collect(Collectors.joining("\n" + prefix, prefix, ""));
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a string representation of the specified {@linkplain Throwable exception}.
	 *
	 * @param  exception
	 *           the exception for which a string representation will be created.
	 * @return a string representation of {@code exception}.
	 */

	public static String exceptionToString(
		Throwable	exception)
	{
		// Initialise buffer
		StringBuilder buffer = new StringBuilder(256);

		// Append detail message
		String message = exception.getMessage();
		if (message != null)
			buffer.append(message);

		// Append string representation of chain of causes
		Throwable cause = exception.getCause();
		if (cause != null)
		{
			if (!buffer.isEmpty())
				buffer.append('\n');
			buffer.append(getCompositeCauseString(cause, "- "));
		}

		// Return string
		return buffer.toString();
	}

	//------------------------------------------------------------------

	/**
	 * Writes the following items to the standard error stream on separate lines:
	 * <ul style="margin-top: 0.25em;">
	 *   <li>a string representation of the location of the caller of this method, and</li>
	 *   <li>a string representation of the specified object.</li>
	 * </ul>
	 *
	 * @param obj
	 *          the object whose string representation will be written to the standard error stream.
	 */

	public static void printStderrLocated(
		Object	obj)
	{
		printStderrLocated(obj, 1);
	}

	//------------------------------------------------------------------

	/**
	 * Writes the following items to the standard error stream on separate lines:
	 * <ul style="margin-top: 0.25em;">
	 *   <li>
		   a string representation of the location of the element of the call stack at the specified index relative to
	 *     the caller of this method (index = 0), and
	 *   </li>
	 *   <li>
	 *     a string representation of the specified object.
	 *   </li>
	 * </ul>
	 *
	 * @param obj
	 *          the object whose string representation will be written to the standard error stream.
	 * @param stackIndex
	 *          the index of the element of the call stack relative to the caller of this method (index = zero), a
	 *          string representation of whose location will be written to the standard error stream.
	 */

	public static void printStderrLocated(
		Object	obj,
		int		stackIndex)
	{
		System.err.println(StackUtils.toStackTraceString(StackUtils.stackFrame(2)));
		System.err.println(obj);
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------

