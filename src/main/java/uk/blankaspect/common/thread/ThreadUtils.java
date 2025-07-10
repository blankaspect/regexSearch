/*====================================================================*\

ThreadUtils.java

Class: thread-related utilti methods.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.thread;

//----------------------------------------------------------------------


// IMPORTS


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import java.util.stream.Collectors;

//----------------------------------------------------------------------


// CLASS: THREAD-RELATED UTILITY METHODS


/**
 * This class contains utility methods that relate to {@linkplain Thread threads}.
 */

public class ThreadUtils
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private ThreadUtils()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the names of active threads as a string.  The names are separated with an LF (U+000A), and an asterisk is
	 * prefixed to the name of each non-daemon thread.
	 *
	 * @return the names of active threads as a string.
	 */

	public static String getThreadNames()
	{
		StringBuilder buffer = new StringBuilder(256);
		for (Thread thread : Thread.getAllStackTraces().keySet())
		{
			if (!buffer.isEmpty())
				buffer.append('\n');
			buffer.append(thread.isDaemon() ? ' ' : '*');
			buffer.append(' ');
			buffer.append(thread.getName());
		}
		return buffer.toString();
	}

	//------------------------------------------------------------------

	/**
	 * Returns a string representation of the elements of a stack trace of the specified thread.
	 *
	 * @param  thread
	 *           the thread for which a string representation of the elements of stack trace is required, or {@code
	 *           null} for the current thread.
	 * @return a string representation of the elements of a stack trace of {@code thread}.
	 */

	public static String getStackTraceString(
		Thread	thread)
	{
		return joinStackTraceStrings(getStackTraceStrings(thread, 1, Integer.MAX_VALUE));
	}

	//------------------------------------------------------------------

	/**
	 * Returns a string representation of the elements of a stack trace of the specified thread.  The string
	 * representation will include no more than the specified number of elements.
	 *
	 * @param  thread
	 *           the thread for which a string representation of the elements of stack trace is required, or {@code
	 *           null} for the current thread.
	 * @param  maxNumElements
	 *           the maximum number of stack-trace elements that will be included in the string representation.
	 * @return a string representation of the elements of a stack trace of {@code thread}.
	 */

	public static String getStackTraceString(
		Thread	thread,
		int		maxNumElements)
	{
		return joinStackTraceStrings(getStackTraceStrings(thread, 1, maxNumElements));
	}

	//------------------------------------------------------------------

	/**
	 * Returns a string representation of the elements of a stack trace of the specified thread.  The string
	 * representation will start with the stack-trace element at the specified index, and it will include no more than
	 * the specified number of elements.
	 *
	 * @param  thread
	 *           the thread for which a string representation of the elements of stack trace is required, or {@code
	 *           null} for the current thread.
	 * @param  index
	 *           the index of the first stack-trace element that will be included in the string representation.  An
	 *           index of 0 corresponds to the call to this method.
	 * @param  maxNumElements
	 *           the maximum number of stack-trace elements that will be included in the string representation.
	 * @return a string representation of the elements of a stack trace of {@code thread}.
	 */

	public static String getStackTraceString(
		Thread	thread,
		int		index,
		int		maxNumElements)
	{
		return joinStackTraceStrings(getStackTraceStrings(thread, index + 1, maxNumElements));
	}

	//------------------------------------------------------------------

	/**
	 * Returns a list of string representations of the elements of a stack trace of the specified thread.
	 *
	 * @param  thread
	 *           the thread for which string representations of the elements of stack trace are required, or {@code
	 *           null} for the current thread.
	 * @return a list of string representations of the elements of a stack trace of {@code thread}.
	 */

	public static List<String> getStackTraceStrings(
		Thread	thread)
	{
		return getStackTraceStrings(thread, 1, Integer.MAX_VALUE);
	}

	//------------------------------------------------------------------

	/**
	 * Returns a list of string representations of the elements of a stack trace of the specified thread.  The list will
	 * contain no more than the specified number of elements.
	 *
	 * @param  thread
	 *           the thread for which string representations of the elements of stack trace are required, or {@code
	 *           null} for the current thread.
	 * @param  maxNumElements
	 *           the maximum number of stack-trace elements for which string representations will be returned.
	 * @return a list of string representations of the elements of a stack trace of {@code thread}.
	 */

	public static List<String> getStackTraceStrings(
		Thread	thread,
		int		maxNumElements)
	{
		return getStackTraceStrings(thread, 1, maxNumElements);
	}

	//------------------------------------------------------------------

	/**
	 * Returns a list of string representations of the elements of a stack trace of the specified thread.  The list will
	 * start with the stack-trace element at the specified index and contain no more than the specified number of
	 * elements.
	 *
	 * @param  thread
	 *           the thread for which string representations of the elements of stack trace are required, or {@code
	 *           null} for the current thread.
	 * @param  index
	 *           the index of the first stack-trace element for which a string representation is required.  An index of
	 *           0 corresponds to the call to this method.
	 * @param  maxNumElements
	 *           the maximum number of stack-trace elements for which string representations will be returned.
	 * @return a list of string representations of the elements of a stack trace of {@code thread}.
	 */

	public static List<String> getStackTraceStrings(
		Thread	thread,
		int		index,
		int		maxNumElements)
	{
		// Validate arguments
		if (index < 0)
			throw new IllegalArgumentException("Index out of bounds: " + index);

		// Replace null thread with current thread
		if (thread == null)
			thread = Thread.currentThread();

		// Initialise list of string representations of stack-trace elements
		List<String> strs = new ArrayList<>();

		// Create list of string representations of stack-trace elements
		StackTraceElement[] elements = thread.getStackTrace();
		index += 2;
		int endIndex = Math.min(index + maxNumElements, elements.length);
		while (index < endIndex)
			strs.add(elements[index++].toString());

		// Return list of string representations of stack-trace elements
		return strs;
	}

	//------------------------------------------------------------------

	/**
	 * Applies the conventional stack-trace prefix (a tab character followed by "at ") to each of the specified strings,
	 * joins adjacent strings with a line feed (U+000A) and returns the result.
	 *
	 * @param  strs
	 *           the strings that will be joined.
	 * @return {@code strs} after the conventional stack-trace prefix has been applied to each element and adjacent
	 *         elements have been joined with a line feed.
	 */

	public static String joinStackTraceStrings(
		Collection<String>	strs)
	{
		String prefix = "\tat ";
		return strs.stream().collect(Collectors.joining("\n" + prefix, prefix, ""));
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
