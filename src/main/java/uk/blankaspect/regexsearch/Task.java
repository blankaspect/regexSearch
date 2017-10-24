/*====================================================================*\

Task.java

Task class.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.regexsearch;

//----------------------------------------------------------------------


// IMPORTS


import java.io.File;

import uk.blankaspect.common.exception.AppException;
import uk.blankaspect.common.exception.TaskCancelledException;

//----------------------------------------------------------------------


// TASK CLASS


abstract class Task
	extends uk.blankaspect.common.misc.Task
{

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// START SEARCH TASK CLASS


	public static class StartSearch
		extends Task
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		public StartSearch(TextSearcher.Params params)
		{
			this.params = params;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : Runnable interface
	////////////////////////////////////////////////////////////////////

		public void run()
		{
			// Perform task
			try
			{
				App.INSTANCE.getTextSearcher().startSearch(params);
			}
			catch (TaskCancelledException e)
			{
				// ignore
			}
			catch (AppException e)
			{
				setException(e, false);
			}

			// Remove thread
			removeThread();
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance fields
	////////////////////////////////////////////////////////////////////

		private	TextSearcher.Params	params;

	}

	//==================================================================


	// RESUME SEARCH TASK CLASS


	public static class ResumeSearch
		extends Task
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		public ResumeSearch(TextSearcher.Option option)
		{
			this.option = option;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : Runnable interface
	////////////////////////////////////////////////////////////////////

		public void run()
		{
			// Perform task
			try
			{
				App.INSTANCE.getTextSearcher().resumeSearch(option);
			}
			catch (TaskCancelledException e)
			{
				// ignore
			}
			catch (AppException e)
			{
				setException(e, false);
			}

			// Remove thread
			removeThread();
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance fields
	////////////////////////////////////////////////////////////////////

		private	TextSearcher.Option	option;

	}

	//==================================================================


	// READ SEARCH PARAMETERS TASK CLASS


	public static class ReadSearchParams
		extends Task
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		public ReadSearchParams(SearchParameters searchParams,
								File             file)
		{
			this.searchParams = searchParams;
			this.file = file;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : Runnable interface
	////////////////////////////////////////////////////////////////////

		public void run()
		{
			// Perform task
			try
			{
				searchParams.read(file);
			}
			catch (TaskCancelledException e)
			{
				// ignore
			}
			catch (AppException e)
			{
				setException(e, false);
			}

			// Remove thread
			removeThread();
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance fields
	////////////////////////////////////////////////////////////////////

		private	SearchParameters	searchParams;
		private	File				file;

	}

	//==================================================================


	// WRITE SEARCH PARAMETERS TASK CLASS


	public static class WriteSearchParams
		extends Task
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		public WriteSearchParams(SearchParameters searchParams,
								 File             file)
		{
			this.searchParams = searchParams;
			this.file = file;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : Runnable interface
	////////////////////////////////////////////////////////////////////

		public void run()
		{
			// Perform task
			try
			{
				searchParams.write(file);
			}
			catch (TaskCancelledException e)
			{
				// ignore
			}
			catch (AppException e)
			{
				setException(e, false);
			}

			// Remove thread
			removeThread();
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance fields
	////////////////////////////////////////////////////////////////////

		private	SearchParameters	searchParams;
		private	File				file;

	}

	//==================================================================


	// WRITE CONFIGURATION TASK CLASS


	public static class WriteConfig
		extends Task
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		public WriteConfig(File file)
		{
			this.file = file;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : Runnable interface
	////////////////////////////////////////////////////////////////////

		public void run()
		{
			// Perform task
			try
			{
				AppConfig.INSTANCE.write(file);
			}
			catch (TaskCancelledException e)
			{
				// ignore
			}
			catch (AppException e)
			{
				setException(e, false);
			}

			// Remove thread
			removeThread();
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance fields
	////////////////////////////////////////////////////////////////////

		private	File	file;

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	private Task()
	{
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
