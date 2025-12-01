/*====================================================================*\

PathnameField.java

Class: pathname field.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.swing.textfield;

//----------------------------------------------------------------------


// IMPORTS


import java.io.File;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;

import javax.swing.text.Document;

import uk.blankaspect.common.exception.ExceptionUtils;

import uk.blankaspect.common.filesystem.PathnameUtils;

import uk.blankaspect.common.property.Property;

import uk.blankaspect.ui.swing.transfer.DataImporter;
import uk.blankaspect.ui.swing.transfer.TextExporter;

//----------------------------------------------------------------------


// CLASS: PATHNAME FIELD


public class PathnameField
	extends JTextField
	implements Property.IObserver
{

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	boolean					unixStyle;
	private	List<IImportListener>	importListeners;
	private	ImportEvent				importEvent;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public PathnameField(int numColumns)
	{
		super(numColumns);
		setTransferHandler(new FileTransferHandler(getTransferHandler()));
	}

	//------------------------------------------------------------------

	public PathnameField(String pathname,
						 int    numColumns)
	{
		this(numColumns);
		setPathname(pathname);
	}

	//------------------------------------------------------------------

	public PathnameField(File file,
						 int  numColumns)
	{
		this(numColumns);
		if (file != null)
			setFile(file);
	}

	//------------------------------------------------------------------

	public PathnameField(File    file,
						 int     numColumns,
						 boolean unixStyle)
	{
		this(numColumns);
		this.unixStyle = unixStyle;
		if (file != null)
			setFile(file);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	protected static String getPathname(File file)
	{
		String pathname = null;
		if (file != null)
		{
			try
			{
				pathname = file.getCanonicalPath();
			}
			catch (Exception e)
			{
				ExceptionUtils.printTopOfStack(e);
				pathname = file.getAbsolutePath();
			}
		}
		return pathname;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : Property.IObserver interface
////////////////////////////////////////////////////////////////////////

	public void propertyChanged(Property property)
	{
		setUnixStyle(((Property.BooleanProperty)property).getValue());
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public boolean isUnixStyle()
	{
		return unixStyle;
	}

	//------------------------------------------------------------------

	public String getPathname()
	{
		return PathnameUtils.parsePathname(getText());
	}

	//------------------------------------------------------------------

	public File getFile()
	{
		return new File(getPathname());
	}

	//------------------------------------------------------------------

	public File getCanonicalFile()
	{
		File file = getFile();
		try
		{
			file = file.getCanonicalFile();
		}
		catch (Exception e)
		{
			ExceptionUtils.printTopOfStack(e);
			file = file.getAbsoluteFile();
		}
		return file;
	}

	//------------------------------------------------------------------

	public boolean isEmpty()
	{
		Document document = getDocument();
		return (document == null) ? true : (document.getLength() == 0);
	}

	//------------------------------------------------------------------

	public void setPathname(String pathname)
	{
		setText(convertPathname(pathname));
	}

	//------------------------------------------------------------------

	public void setFile(File file)
	{
		setPathname(getPathname(file));
	}

	//------------------------------------------------------------------

	public void setUnixStyle(boolean unixStyle)
	{
		if (this.unixStyle != unixStyle)
		{
			this.unixStyle = unixStyle;
			setPathname(getPathname());
		}
	}

	//------------------------------------------------------------------

	public void addImportListener(IImportListener listener)
	{
		if (importListeners == null)
			importListeners = new ArrayList<>();
		importListeners.add(listener);
	}

	//------------------------------------------------------------------

	public void removeImportListener(IImportListener listener)
	{
		if (importListeners != null)
			importListeners.remove(listener);
	}

	//------------------------------------------------------------------

	protected void fireDataImported()
	{
		if (importListeners != null)
		{
			if (importEvent == null)
				importEvent = new ImportEvent(this);
			for (int i = importListeners.size() - 1; i >= 0; i--)
				importListeners.get(i).dataImported(importEvent);
		}
	}

	//------------------------------------------------------------------

	protected String convertPathname(String pathname)
	{
		return (pathname == null)
						? null
						: unixStyle
								? PathnameUtils.toUnixStyle(pathname, true)
								: pathname.replace('/', File.separatorChar);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Member interfaces
////////////////////////////////////////////////////////////////////////


	// INTERFACE: IMPORT LISTENER


	@FunctionalInterface
	public interface IImportListener
	{

	////////////////////////////////////////////////////////////////////
	//  Methods
	////////////////////////////////////////////////////////////////////

		void dataImported(ImportEvent event);

		//--------------------------------------------------------------

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// CLASS: IMPORT EVENT


	public static class ImportEvent
		extends EventObject
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private ImportEvent(PathnameField source)
		{
			super(source);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		public PathnameField getSource()
		{
			return (PathnameField)source;
		}

		//--------------------------------------------------------------

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Member classes : inner classes
////////////////////////////////////////////////////////////////////////


	// CLASS: FILE-TRANSFER HANDLER


	private class FileTransferHandler
		extends TextExporter
		implements Runnable
	{

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	TransferHandler	oldTransferHandler;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private FileTransferHandler(TransferHandler oldTransferHandler)
		{
			this.oldTransferHandler = oldTransferHandler;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : Runnable interface
	////////////////////////////////////////////////////////////////////

		public void run()
		{
			fireDataImported();
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		public boolean canImport(TransferHandler.TransferSupport support)
		{
			boolean supported = !support.isDrop() || ((support.getSourceDropActions() & COPY) == COPY);
			if (supported)
				supported = isEnabled() &&
							(DataImporter.isFileList(support.getDataFlavors()) ||
							 ((oldTransferHandler != null) && oldTransferHandler.canImport(support)));
			if (support.isDrop() && supported)
				support.setDropAction(COPY);
			return supported;
		}

		//--------------------------------------------------------------

		@Override
		public boolean importData(TransferHandler.TransferSupport support)
		{
			// Import the pathname of the first file of a list of files
			if (DataImporter.isFileList(support.getDataFlavors()))
			{
				try
				{
					List<File> files = DataImporter.getFiles(support.getTransferable());
					if (!files.isEmpty())
					{
						String pathname = convertPathname(getPathname(files.get(0)));
						if (support.isDrop())
							setText(pathname);
						else
							replaceSelection(pathname);
						SwingUtilities.invokeLater(this);
						return true;
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
					return false;
				}
			}

			// Import transferred data with the old transfer handler
			return (oldTransferHandler == null) ? false : oldTransferHandler.importData(support);
		}

		//--------------------------------------------------------------

	}

	//==================================================================

}

//----------------------------------------------------------------------
