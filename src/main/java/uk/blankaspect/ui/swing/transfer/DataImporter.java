/*====================================================================*\

DataImporter.java

Class: data importer.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.swing.transfer;

//----------------------------------------------------------------------


// IMPORTS


import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

import java.io.File;
import java.io.IOException;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.ArrayList;
import java.util.List;

import uk.blankaspect.common.exception2.ExceptionUtils;

//----------------------------------------------------------------------


// CLASS: DATA IMPORTER


public class DataImporter
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	private static final	String		FILE_SCHEME_STR			= "file";
	private static final	String		URI_LIST_MIME_TYPE_STR	= "text/uri-list;class=java.lang.String";
	private static final	DataFlavor	URI_LIST_FLAVOUR		= getUriListFlavour();

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	private DataImporter()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static boolean isString(
		DataFlavor[]	flavours)
	{
		for (DataFlavor flavour : flavours)
		{
			if (flavour.equals(DataFlavor.stringFlavor))
				return true;
		}
		return false;
	}

	//------------------------------------------------------------------

	public static boolean isFileList(
		DataFlavor[]	flavours)
	{
		for (DataFlavor flavour : flavours)
		{
			if (flavour.isFlavorJavaFileListType() || flavour.equals(URI_LIST_FLAVOUR))
				return true;
		}
		return false;
	}

	//------------------------------------------------------------------

	public static List<File> getFiles(
		Transferable	transferable)
		throws IOException, UnsupportedFlavorException
	{
		return getFiles(transferable, false);
	}

	//------------------------------------------------------------------

	public static List<File> getFiles(
		Transferable	transferable,
		boolean			allowAnyUri)
		throws IOException, UnsupportedFlavorException
	{
		List<File> files = new ArrayList<>();
		if ((transferable != null) && isFileList(transferable.getTransferDataFlavors()))
		{
			if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
			{
				Object transferData = transferable.getTransferData(DataFlavor.javaFileListFlavor);
				if (transferData instanceof List<?> list)
				{
					for (Object obj : list)
					{
						if (obj instanceof File file)
							files.add(file);
					}
				}
			}
			else if (transferable.getTransferData(URI_LIST_FLAVOUR) instanceof String text)
			{
				String[] strs = text.split("[\\r\\n]+");
				for (String str : strs)
				{
					try
					{
						URI uri = new URI(str);
						if (FILE_SCHEME_STR.equals(uri.getScheme()))
							files.add(new File(uri));
						else if (allowAnyUri)
							files.add(new File(str));
					}
					catch (URISyntaxException e)
					{
						ExceptionUtils.printStderrLocated(e);
					}
				}
			}
		}
		return files;
	}

	//------------------------------------------------------------------

	private static DataFlavor getUriListFlavour()
	{
		DataFlavor flavour = null;
		try
		{
			flavour = new DataFlavor(URI_LIST_MIME_TYPE_STR);
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		return flavour;
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
