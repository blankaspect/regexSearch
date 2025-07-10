/*====================================================================*\

FilenameSuffixFilter.java

Class: filename-suffix filter.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.misc;

//----------------------------------------------------------------------


// IMPORTS


import java.io.File;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import uk.blankaspect.common.filesystem.PathnameUtils;

//----------------------------------------------------------------------


// CLASS: FILENAME-SUFFIX FILTER


public class FilenameSuffixFilter
	extends javax.swing.filechooser.FileFilter
	implements java.io.FileFilter
{

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	List<String>	suffixes;
	private	String			description;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public FilenameSuffixFilter(
		String		description,
		String...	suffixes)
	{
		this(description, List.of(suffixes));
	}

	//------------------------------------------------------------------

	public FilenameSuffixFilter(
		String				description,
		Iterable<String>	suffixes)
	{
		this.suffixes = new ArrayList<>();
		StringBuilder buffer = new StringBuilder(128);
		buffer.append(description);
		buffer.append(" (");
		for (String suffix : suffixes)
		{
			if (!this.suffixes.isEmpty())
				buffer.append(", ");
			buffer.append('*');
			buffer.append(suffix);
			this.suffixes.add(suffix.toLowerCase());
		}
		buffer.append(')');
		this.description = buffer.toString();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : FileFilter interface
////////////////////////////////////////////////////////////////////////

	@Override
	public boolean accept(
		File	file)
	{
		return file.isDirectory() || accepts(file.getName());
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	@Override
	public boolean equals(
		Object	obj)
	{
		if (this == obj)
			return true;

		return (obj instanceof FilenameSuffixFilter other) && Objects.equals(description, other.description)
				&& Objects.equals(suffixes, other.suffixes);
	}

	//------------------------------------------------------------------

	@Override
	public int hashCode()
	{
		return 31 * description.hashCode() + suffixes.hashCode();
	}

	//------------------------------------------------------------------

	@Override
	public String getDescription()
	{
		return description;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public String getSuffix(
		int	index)
	{
		return suffixes.get(index);
	}

	//------------------------------------------------------------------

	public boolean accepts(
		String	filename)
	{
		return (filename != null) && PathnameUtils.suffixMatches(filename, suffixes);
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
