/*====================================================================*\

TabWidthFilter.java

Tab-width filter class.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.regexsearch;

//----------------------------------------------------------------------


// IMPORTS


import java.io.File;

import java.util.ArrayList;
import java.util.List;

import uk.blankaspect.common.misc.FilenameFilter;

import uk.blankaspect.common.string.StringUtils;

//----------------------------------------------------------------------


// TAB-WIDTH FILTER CLASS


class TabWidthFilter
	implements Comparable<TabWidthFilter>
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	private static final	char	WIDTH_SEPARATOR_CHAR	= ':';

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * @throws IllegalArgumentException
	 */

	public TabWidthFilter(String str)
	{
		List<String> strs = StringUtils.split(str, WIDTH_SEPARATOR_CHAR);
		if (strs.size() != 2)
			throw new IllegalArgumentException();

		setFilters(strs.get(0));
		tabWidth = Integer.parseInt(strs.get(1).trim());
	}

	//------------------------------------------------------------------

	public TabWidthFilter(String filterStr,
						  int    tabWidth)
	{
		setFilters(filterStr);
		this.tabWidth = tabWidth;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static boolean isWidthSeparator(char ch)
	{
		return (ch == WIDTH_SEPARATOR_CHAR);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : Comparable interface
////////////////////////////////////////////////////////////////////////

	@Override
	public int compareTo(TabWidthFilter other)
	{
		return Integer.compare(tabWidth, other.tabWidth);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	@Override
	public String toString()
	{
		StringBuilder buffer = new StringBuilder();
		buffer.append(getFilterString());
		buffer.append(WIDTH_SEPARATOR_CHAR);
		buffer.append(' ');
		buffer.append(tabWidth);
		return buffer.toString();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public int getTabWidth()
	{
		return tabWidth;
	}

	//------------------------------------------------------------------

	public int getTabWidth(File file)
	{
		for (FilenameFilter filter : filters)
		{
			if (filter.accept(file))
				return tabWidth;
		}
		return -1;
	}

	//------------------------------------------------------------------

	public String getFilterString()
	{
		StringBuilder buffer = new StringBuilder();
		for (FilenameFilter filter : filters)
		{
			if (!buffer.isEmpty())
				buffer.append(' ');
			buffer.append(filter.toString());
		}
		return buffer.toString();
	}

	//------------------------------------------------------------------

	private void setFilters(String filterStr)
	{
		boolean ignoreCase = AppConfig.INSTANCE.isIgnoreFilenameCase();
		filters = new ArrayList<>();
		for (String str : filterStr.split("\\s+"))
		{
			if (!str.isEmpty())
				filters.add(new FilenameFilter(str, ignoreCase));
		}
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	List<FilenameFilter>	filters;
	private	int						tabWidth;

}

//----------------------------------------------------------------------
