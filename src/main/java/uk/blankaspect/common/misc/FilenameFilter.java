/*====================================================================*\

FilenameFilter.java

Class: filename filter.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.misc;

//----------------------------------------------------------------------


// IMPORTS


import java.io.File;
import java.io.FileFilter;

import java.util.ArrayList;
import java.util.List;

//----------------------------------------------------------------------


// CLASS: FILENAME FILTER


public class FilenameFilter
	implements Comparable<FilenameFilter>, FileFilter
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	public static final	char	SINGLE_WILDCARD_CHAR	= '?';
	public static final	char	MULTIPLE_WILDCARD_CHAR	= '*';

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	String				pattern;
	private	List<PatternToken>	patternTokens;
	private	boolean				ignoreCase;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public FilenameFilter(
		String	pattern)
	{
		this(pattern, false);
	}

	//------------------------------------------------------------------

	public FilenameFilter(
		String	pattern,
		boolean	ignoreCase)
	{
		this.pattern = pattern;
		this.ignoreCase = ignoreCase;

		patternTokens = new ArrayList<>();
		int index = 0;
		int startIndex = index;
		while (index < pattern.length())
		{
			switch (pattern.charAt(index))
			{
				case SINGLE_WILDCARD_CHAR:
					if (index > startIndex)
						patternTokens.add(new PatternToken(pattern, startIndex, index, ignoreCase));
					patternTokens.add(new PatternToken(PatternToken.Kind.SINGLE_WILDCARD));
					startIndex = ++index;
					break;

				case MULTIPLE_WILDCARD_CHAR:
					if (index > startIndex)
						patternTokens.add(new PatternToken(pattern, startIndex, index, ignoreCase));
					patternTokens.add(new PatternToken(PatternToken.Kind.MULTIPLE_WILDCARD));
					startIndex = ++index;
					break;

				default:
					++index;
					break;
			}
		}
		if (index > startIndex)
			patternTokens.add(new PatternToken(pattern, startIndex, index, ignoreCase));
	}

	//------------------------------------------------------------------

	protected FilenameFilter()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : Comparable interface
////////////////////////////////////////////////////////////////////////

	@Override
	public int compareTo(
		FilenameFilter	filter)
	{
		return toString().compareTo(filter.toString());
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : FileFilter interface
////////////////////////////////////////////////////////////////////////

	@Override
	public boolean accept(
		File	file)
	{
		// Don't accept an existing entity that is not a normal file
		if (file.exists() && !file.isFile())
			return false;

		// Accept all files if no pattern has been specified
		if (pattern == null)
			return true;

		// Match filename against pattern
		return match(ignoreCase ? file.getName().toLowerCase() : file.getName(), 0, 0);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	@Override
	public String toString()
	{
		return (pattern == null) ? "" : pattern;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public String getPattern()
	{
		return pattern;
	}

	//------------------------------------------------------------------

	private boolean match(
		String	filename,
		int		filenameIndex,
		int		tokenIndex)
	{
		while (tokenIndex < patternTokens.size())
		{
			PatternToken patternToken = patternTokens.get(tokenIndex++);
			switch (patternToken.kind)
			{
				case LITERAL:
					if (!filename.startsWith(patternToken.value, filenameIndex))
						return false;
					filenameIndex += patternToken.value.length();
					break;

				case SINGLE_WILDCARD:
					if (filenameIndex > filename.length())
						return false;
					++filenameIndex;
					break;

				case MULTIPLE_WILDCARD:
					while (patternToken.kind == PatternToken.Kind.MULTIPLE_WILDCARD)
					{
						if (tokenIndex >= patternTokens.size())
							return true;
						patternToken = patternTokens.get(tokenIndex++);
					}
					--tokenIndex;
					while (filenameIndex < filename.length())
					{
						if (match(filename, filenameIndex, tokenIndex))
							return true;
						++filenameIndex;
					}
					return false;
			}
		}
		return (filenameIndex >= filename.length());
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// CLASS: MULTIPLE FILTER


	public static class MultipleFilter
		extends FilenameFilter
	{

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	FilenameFilter[]	filters;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		public MultipleFilter(
			String...	patterns)
		{
			this(false, patterns);
		}

		//--------------------------------------------------------------

		public MultipleFilter(
			boolean  	ignoreCase,
			String...	patterns)
		{
			filters = new FilenameFilter[patterns.length];
			for (int i = 0; i < filters.length; i++)
				filters[i] = new FilenameFilter(patterns[i], ignoreCase);
		}

		//--------------------------------------------------------------

		public MultipleFilter(
			List<String>	patterns,
			boolean			ignoreCase)
		{
			filters = new FilenameFilter[patterns.size()];
			for (int i = 0; i < filters.length; i++)
				filters[i] = new FilenameFilter(patterns.get(i), ignoreCase);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : FileFilter interface
	////////////////////////////////////////////////////////////////////

		@Override
		public boolean accept(
			File	file)
		{
			for (FilenameFilter filter : filters)
			{
				if (filter.accept(file))
					return true;
			}
			return false;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		public int getNumFilters()
		{
			return filters.length;
		}

		//--------------------------------------------------------------

		public FilenameFilter getFilter(
			int	index)
		{
			return filters[index];
		}

		//--------------------------------------------------------------

	}

	//==================================================================


	// CLASS: PATTERN TOKEN


	private static class PatternToken
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		private enum Kind
		{
			LITERAL,
			SINGLE_WILDCARD,
			MULTIPLE_WILDCARD
		}

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	Kind	kind;
		private	String	value;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private PatternToken(
			Kind	kind)
		{
			this.kind = kind;
		}

		//--------------------------------------------------------------

		private PatternToken(
			String	str,
			int		startIndex,
			int		endIndex,
			boolean	ignoreCase)
		{
			kind = Kind.LITERAL;
			value = str.substring(startIndex, endIndex);
			if (ignoreCase)
				value = value.toLowerCase();
		}

		//--------------------------------------------------------------

	}

	//==================================================================

}

//----------------------------------------------------------------------
