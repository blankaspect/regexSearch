/*====================================================================*\

StackUtils.java

Class: stack-related utility methods.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.stack;

//----------------------------------------------------------------------


// IMPORTS


import java.util.Optional;

//----------------------------------------------------------------------


// CLASS: STACK-RELATED UTILITY METHODS


/**
 * This class provides some utility methods that relate to the call stack.
 */

public class StackUtils
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private StackUtils()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static StackWalker.StackFrame stackFrame()
	{
		return stackFrame_(1).get();
	}

	//------------------------------------------------------------------

	public static StackWalker.StackFrame stackFrame(
		int	index)
	{
		return stackFrame_(index + 1).get();
	}

	//------------------------------------------------------------------

	public static Optional<StackWalker.StackFrame> stackFrame_()
	{
		return stackFrame_(1);
	}

	//------------------------------------------------------------------

	public static Optional<StackWalker.StackFrame> stackFrame_(
		int	index)
	{
		return StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
				.walk(frames -> frames.skip(index + 1).findFirst());
	}

	//------------------------------------------------------------------

	public static String callerName()
	{
		return stackFrame(1).getMethodName();
	}

	//------------------------------------------------------------------

	public static String callerName(
		int	stackIndex)
	{
		return stackFrame(stackIndex + 1).getMethodName();
	}

	//------------------------------------------------------------------

	public static Optional<String> callerName_()
	{
		return stackFrame_(1).map(StackWalker.StackFrame::getMethodName);
	}

	//------------------------------------------------------------------

	public static Optional<String> callerName_(
		int	stackIndex)
	{
		return stackFrame_(stackIndex + 1).map(StackWalker.StackFrame::getMethodName);
	}

	//------------------------------------------------------------------

	public static String toStackTraceString(
		StackWalker.StackFrame	stackFrame)
	{
		return new StringBuilder(256)
				.append(stackFrame.getClassName())
				.append('.')
				.append(stackFrame.getMethodName())
				.append('(')
				.append(stackFrame.getFileName())
				.append(':')
				.append(stackFrame.getLineNumber())
				.append(')')
				.toString();
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
