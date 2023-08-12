/*====================================================================*\

DaemonFactory.java

Class: daemon-thread factory.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.thread;

//----------------------------------------------------------------------


// CLASS: DAEMON-THREAD FACTORY


/**
 * This class provides a factory for daemon threads.
 */

public class DaemonFactory
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private DaemonFactory()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates and returns a new instance of a daemon thread.
	 *
	 * @return a new instance of a daemon thread.
	 */

	public static Thread create()
	{
		Thread thread = new Thread();
		thread.setDaemon(true);
		return thread;
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a new instance of a daemon thread for the specified {@link Runnable}.
	 *
	 * @param  runnable
	 *           the {@link Runnable} for which the thread will be created.
	 * @return a new instance of a daemon thread for {@code runnable}.
	 */

	public static Thread create(
		Runnable	runnable)
	{
		Thread thread = new Thread(runnable);
		thread.setDaemon(true);
		return thread;
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a new instance of a daemon thread with the specified name for the specified {@link Runnable}.
	 *
	 * @param  name
	 *           the name of the thread.
	 * @param  runnable
	 *           the {@link Runnable} for which the thread will be created.
	 * @return a new instance of a daemon thread for {@code runnable}.
	 */

	public static Thread create(
		String		name,
		Runnable	runnable)
	{
		Thread thread = new Thread(runnable, name);
		thread.setDaemon(true);
		return thread;
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
