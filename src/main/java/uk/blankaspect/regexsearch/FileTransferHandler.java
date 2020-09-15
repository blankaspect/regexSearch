/*====================================================================*\

FileTransferHandler.java

File transfer handler class.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.regexsearch;

//----------------------------------------------------------------------


// IMPORTS


import java.awt.Window;

import java.awt.datatransfer.UnsupportedFlavorException;

import java.io.File;
import java.io.IOException;

import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;

import uk.blankaspect.common.exception.AppException;

import uk.blankaspect.common.swing.transfer.DataImporter;

//----------------------------------------------------------------------


// FILE TRANSFER HANDLER CLASS


class FileTransferHandler
	extends TransferHandler
	implements Runnable
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	public static final	FileTransferHandler	INSTANCE	= new FileTransferHandler();

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

		FILE_TRANSFER_NOT_SUPPORTED
		("File transfer is not supported."),

		MULTIPLE_FILE_TRANSFER_NOT_SUPPORTED
		("The transfer of more than one file is not supported."),

		ERROR_TRANSFERRING_DATA
		("An error occurred while transferring data.");

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
//  Constructors
////////////////////////////////////////////////////////////////////////

	private FileTransferHandler()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : Runnable interface
////////////////////////////////////////////////////////////////////////

	public void run()
	{
		AppCommand.IMPORT_FILE.execute();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	@Override
	public boolean canImport(TransferHandler.TransferSupport support)
	{
		boolean supported = !support.isDrop() || ((support.getSourceDropActions() & COPY) == COPY);
		if (supported)
			supported = App.INSTANCE.getMainWindow().getControlDialog().canAddFileSet() &&
						DataImporter.isFileList(support.getDataFlavors());
		if (support.isDrop() && supported)
			support.setDropAction(COPY);
		return supported;
	}

	//------------------------------------------------------------------

	@Override
	public boolean importData(TransferHandler.TransferSupport support)
	{
		if (canImport(support))
		{
			try
			{
				try
				{
					List<File> files = DataImporter.getFiles(support.getTransferable());
					if (!files.isEmpty())
					{
						Window window = SwingUtilities.getWindowAncestor(support.getComponent());
						window.toFront();
						if (files.size() > 1)
							throw new AppException(ErrorId.MULTIPLE_FILE_TRANSFER_NOT_SUPPORTED);
						AppCommand.IMPORT_FILE.putValue(AppCommand.Property.FILE, files.get(0));
						SwingUtilities.invokeLater(this);
						return true;
					}
				}
				catch (UnsupportedFlavorException e)
				{
					throw new AppException(ErrorId.FILE_TRANSFER_NOT_SUPPORTED);
				}
				catch (IOException e)
				{
					throw new AppException(ErrorId.ERROR_TRANSFERRING_DATA);
				}
			}
			catch (AppException e)
			{
				App.INSTANCE.showErrorMessage(App.SHORT_NAME, e);
			}
		}
		return false;
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
