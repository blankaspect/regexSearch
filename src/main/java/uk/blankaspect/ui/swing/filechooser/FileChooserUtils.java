/*====================================================================*\

FileChooserUtils.java

Class: utility methods related to Swing file choosers.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.swing.filechooser;

//----------------------------------------------------------------------


// IMPORTS


import java.io.File;

import java.util.List;

import javax.swing.JFileChooser;

import javax.swing.filechooser.FileFilter;

//----------------------------------------------------------------------


// CLASS: UTILITY METHODS RELATED TO SWING FILE CHOOSERS


/**
 * This class contains utility methods that relate to {@linkplain JFileChooser file choosers}.
 */

public class FileChooserUtils
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	public static final	FileFilter	ALL_FILTER	= new FileFilter()
	{
		@Override
		public boolean accept(
			File	file)
		{
			return true;
		}

		@Override
		public String getDescription()
		{
			return "All files";
		}
	};

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private FileChooserUtils()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static void setFilter(
		JFileChooser	fileChooser,
		FileFilter		filter)
	{
		setFilters(fileChooser, List.of(filter), 0, true);
	}

	//------------------------------------------------------------------

	public static void setFilter(
		JFileChooser	fileChooser,
		FileFilter		filter,
		boolean			includeAcceptAll)
	{
		setFilters(fileChooser, List.of(filter), 0, includeAcceptAll);
	}

	//------------------------------------------------------------------

	public static void setFilters(
		JFileChooser	fileChooser,
		int				filterIndex,
		FileFilter...	filters)
	{
		setFilters(fileChooser, List.of(filters), filterIndex, true);
	}

	//------------------------------------------------------------------

	public static void setFilters(
		JFileChooser	fileChooser,
		int				filterIndex,
		boolean			includeAcceptAll,
		FileFilter...	filters)
	{
		setFilters(fileChooser, List.of(filters), filterIndex, includeAcceptAll);
	}

	//------------------------------------------------------------------

	public static void setFilters(
		JFileChooser	fileChooser,
		FileFilter		selectedFilter,
		FileFilter...	filters)
	{
		setFilters(fileChooser, List.of(filters), selectedFilter, true);
	}

	//------------------------------------------------------------------

	public static void setFilters(
		JFileChooser	fileChooser,
		FileFilter		selectedFilter,
		boolean			includeAcceptAll,
		FileFilter...	filters)
	{
		setFilters(fileChooser, List.of(filters), selectedFilter, includeAcceptAll);
	}

	//------------------------------------------------------------------

	public static void setFilters(
		JFileChooser				fileChooser,
		List<? extends FileFilter>	filters,
		FileFilter					selectedFilter)
	{
		setFilters(fileChooser, filters, filters.indexOf(selectedFilter), true);
	}

	//------------------------------------------------------------------

	public static void setFilters(
		JFileChooser				fileChooser,
		List<? extends FileFilter>	filters,
		FileFilter					selectedFilter,
		boolean						includeAcceptAll)
	{
		setFilters(fileChooser, filters, filters.indexOf(selectedFilter), includeAcceptAll);
	}

	//------------------------------------------------------------------

	public static void setFilters(
		JFileChooser					fileChooser,
		Iterable<? extends FileFilter>	filters)
	{
		setFilters(fileChooser, filters, 0, true);
	}

	//------------------------------------------------------------------

	public static void setFilters(
		JFileChooser					fileChooser,
		Iterable<? extends FileFilter>	filters,
		boolean							includeAcceptAll)
	{
		setFilters(fileChooser, filters, 0, includeAcceptAll);
	}

	//------------------------------------------------------------------

	public static void setFilters(
		JFileChooser					fileChooser,
		Iterable<? extends FileFilter>	filters,
		int								filterIndex)
	{
		setFilters(fileChooser, filters, filterIndex, true);
	}

	//------------------------------------------------------------------

	public static void setFilters(
		JFileChooser					fileChooser,
		Iterable<? extends FileFilter>	filters,
		int								filterIndex,
		boolean							includeAcceptAll)
	{
		// Set filters
		fileChooser.resetChoosableFileFilters();
		fileChooser.setAcceptAllFileFilterUsed(false);
		for (FileFilter filter : filters)
			fileChooser.addChoosableFileFilter(filter);
		if (includeAcceptAll)
			fileChooser.addChoosableFileFilter(ALL_FILTER);

		// Select filter
		int index = 0;
		for (FileFilter filter : filters)
		{
			if (index == filterIndex)
			{
				fileChooser.setFileFilter(filter);
				break;
			}
			++index;
		}
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
