/*====================================================================*\

TextSearcher.java

Class: text searcher.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.regexsearch;

//----------------------------------------------------------------------


// IMPORTS


import java.io.File;
import java.io.FileFilter;

import java.lang.reflect.InvocationTargetException;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import uk.blankaspect.common.collection.ArraySet;

import uk.blankaspect.common.exception.AppException;
import uk.blankaspect.common.exception.FileException;

import uk.blankaspect.common.misc.DirectoryFilter;
import uk.blankaspect.common.misc.FileAttributeUtils;
import uk.blankaspect.common.misc.LineSeparator;
import uk.blankaspect.common.misc.PathnameFilter;

//----------------------------------------------------------------------


// CLASS: TEXT SEARCHER


class TextSearcher
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	enum StopSubstate
	{
		MATCH,
		PREVIEW,
		DONE
	}

	enum Option
	{
		REPLACE,
		REPLACE_FILE,
		REPLACE_GLOBAL,
		SKIP,
		NEXT_FILE,
		PREVIEW,
		KEEP,
		RESTORE,
		CANCEL
	}

	private enum SearchState
	{
		ITERATE_DIRECTORIES,
		ITERATE_FILES,
		READ_FILE,
		GET_TEXT,
		INIT_SEARCH,
		SEARCH,
		REPLACE,
		PREVIEW,
		RESTORE,
		WRITE_FILE,
		PUT_TEXT,
		CANCEL,
		DONE,
		STOP
	}

	private enum ReplacementState
	{
		NEXT_CHAR,
		ESCAPE,
		GROUP_QUALIFIER,
		GROUP_INDEX,
		UNICODE_ESCAPE,
		DONE
	}

	private static final	int		UNICODE_ESCAPE_LENGTH	= 4;

	private static final	String	HEX_DIGITS	= "0123456789ABCDEF";

	private static final	String	DANGLING_ESCAPE_STR			= "The replacement string has a dangling '%s'.";
	private static final	String	ILLEGAL_ESCAPE_STR			= "\" is not a legal escape sequence.";
	private static final	String	ILLEGAL_UNICODE_ESCAPE_STR	= "The Unicode escape is invalid.";

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	String				replacementStr;
	private	boolean				regex;
	private	boolean				replaceGlobal;
	private	boolean				replaceUnprompted;
	private	int					numFiles;
	private	int					numMatchedFiles;
	private	int					numMatches;
	private	int					numMatchesInFile;
	private	int					numReplacements;
	private	int					numReplacementsInFile;
	private	Pattern				pattern;
	private	Matcher				matcher;
	private	FileFilter			exclusionFilter;
	private	Deque<Directory>	directoryStack;
	private	File				targetFile;
	private	StringBuffer		text;
	private	int					textIndex;
	private	LineSeparator		lineSeparator;
	private	SearchState			searchState;
	private	StopSubstate		stopSubstate;
	private	List<File>			targetNotFoundFiles;
	private	List<File>			unprocessedFiles;
	private	List<File>			attributesNotSetFiles;

////////////////////////////////////////////////////////////////////////
//  Enumerated types
////////////////////////////////////////////////////////////////////////


	// ENUMERATION: TEXT CASE


	private enum Case
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		LOWER   ('L'),
		UPPER   ('U');

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	char	key;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private Case(
			char	key)
		{
			this.key = key;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Class methods
	////////////////////////////////////////////////////////////////////

		public static Case forKey(
			char	key)
		{
			return Arrays.stream(values()).filter(value -> value.key == key).findFirst().orElse(null);
		}

		//--------------------------------------------------------------

	}

	//==================================================================


	// ENUMERATION: ERROR IDENTIFIERS


	private enum ErrorId
		implements AppException.IId
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		FAILED_TO_LIST_DIRECTORY_ENTRIES
		("Failed to get a list of directory entries."),

		INVALID_REPLACEMENT_STRING
		("The replacement string is invalid."),

		NOT_ENOUGH_MEMORY_TO_REPLACE
		("There was not enough memory to perform the replacement."),

		NOT_ENOUGH_MEMORY_TO_CONVERT_LINE_SEPARATORS
		("There was not enough memory to convert the line separators in the file."),

		ERROR_DURING_REPLACEMENT
		("An error occurred while performing a replacement.");

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	String	message;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private ErrorId(
			String	message)
		{
			this.message = message;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : AppException.IId interface
	////////////////////////////////////////////////////////////////////

		@Override
		public String getMessage()
		{
			return message;
		}

		//--------------------------------------------------------------

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Member records
////////////////////////////////////////////////////////////////////////


	// RECORD: FILE RESULT


	public record FileResult(
		File	file,
		int		numMatches,
		int		numReplacements)
	{ }

	//==================================================================


	// RECORD: AGGREGATE RESULT


	public record AggregateResult(
		int	numFiles,
		int	numMatchedFiles,
		int	numMatches,
		int	numReplacements)
	{ }

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// CLASS: SEARCH PARAMETERS


	public static class Params
	{

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		List<File>		files;
		List<String>	inclusionPatterns;
		List<String>	exclusionPatterns;
		String			targetStr;
		String			replacementStr;
		boolean			regex;
		boolean			ignoreCase;
		boolean			recordTargetNotFound;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		public Params()
		{
			inclusionPatterns = Collections.emptyList();
			exclusionPatterns = Collections.emptyList();
		}

		//--------------------------------------------------------------

	}

	//==================================================================


	// CLASS: SYNTAX EXCEPTION


	public static class SyntaxException
		extends AppException
	{

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	String	description;
		private	int		index;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		public SyntaxException(
			AppException.IId	id,
			String				description)
		{
			this(id, description, -1);
		}

		//--------------------------------------------------------------

		public SyntaxException(
			AppException.IId	id,
			String				description,
			int					index)
		{
			super(id);
			this.description = description;
			this.index = index;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		protected String getSuffix()
		{
			return "\n(" + description + ")";
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		public int getIndex()
		{
			return index;
		}

		//--------------------------------------------------------------

	}

	//==================================================================


	// CLASS: DIRECTORY


	private static class Directory
	{

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	File[]			directories;
		private	File[]			files;
		private	PathnameFilter	filter;
		private	int				directoryIndex;
		private	int				fileIndex;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private Directory(
			File			file,
			PathnameFilter	filter)
		{
			files = new File[1];
			files[0] = file;
			this.filter = filter;
		}

		//--------------------------------------------------------------

		private Directory(
			File[]			directories,
			File[]			files,
			PathnameFilter	filter)
		{
			this.directories = directories;
			this.files = files;
			this.filter = filter;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		public PathnameFilter getFilter()
		{
			return filter;
		}

		//--------------------------------------------------------------

		public boolean isEmpty()
		{
			return (directories == null) && (files == null);
		}

		//--------------------------------------------------------------

		public File getNextDirectory()
		{
			return ((directories == null) || (directoryIndex >= directories.length))
					? null
					: directories[directoryIndex++];
		}

		//--------------------------------------------------------------

		public File getNextFile()
		{
			return ((files == null) || (fileIndex >= files.length)) ? null : files[fileIndex++];
		}

		//--------------------------------------------------------------

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public TextSearcher()
	{
		directoryStack = new ArrayDeque<>();
		unprocessedFiles = new ArraySet<>();
		attributesNotSetFiles = new ArraySet<>();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static String createReplacementString(
		String	expression,
		Matcher	matcher,
		boolean	regex)
		throws AppException
	{
		StringBuilder buffer = new StringBuilder();
		int index = 0;
		int endIndex = expression.length();
		char escapeChar = AppConfig.INSTANCE.getReplacementEscapeChar();
		Case textCase = null;
		char ch = 0;
		ReplacementState state = ReplacementState.NEXT_CHAR;
		while (state != ReplacementState.DONE)
		{
			switch (state)
			{
				case NEXT_CHAR:
				{
					if (index < endIndex)
					{
						ch = expression.charAt(index++);
						if (ch == escapeChar)
							state = ReplacementState.ESCAPE;
						else
							buffer.append(ch);
					}
					else
						state = ReplacementState.DONE;
					break;
				}

				case ESCAPE:
				{
					if (index >= endIndex)
					{
						String str = String.format(DANGLING_ESCAPE_STR, escapeChar);
						throw new SyntaxException(ErrorId.INVALID_REPLACEMENT_STRING, str, index - 1);
					}
					textCase = null;
					ch = expression.charAt(index);
					if (regex && (ch >= '0') && (ch <= '9'))
						state = ReplacementState.GROUP_INDEX;
					else
					{
						if (ch == escapeChar)
						{
							buffer.append(escapeChar);
							state = ReplacementState.NEXT_CHAR;
						}
						else
						{
							switch (ch)
							{
								case 'n':
									buffer.append('\n');
									state = ReplacementState.NEXT_CHAR;
									break;

								case 't':
									buffer.append('\t');
									state = ReplacementState.NEXT_CHAR;
									break;

								case 'u':
									state = ReplacementState.UNICODE_ESCAPE;
									break;

								default:
									textCase = Case.forKey(ch);
									if (!regex || (textCase == null))
									{
										throw new SyntaxException(ErrorId.INVALID_REPLACEMENT_STRING,
																  "\"" + escapeChar + ch + ILLEGAL_ESCAPE_STR,
																  index - 1);
									}
									state = ReplacementState.GROUP_QUALIFIER;
									break;
							}
						}
						++index;
					}
					break;
				}

				case GROUP_QUALIFIER:
				{
					if (index >= endIndex)
					{
						throw new SyntaxException(ErrorId.INVALID_REPLACEMENT_STRING,
												  "\"" + escapeChar + textCase.key + ILLEGAL_ESCAPE_STR, index - 2);
					}
					ch = expression.charAt(index);
					if ((ch < '0') || (ch > '9'))
					{
						throw new SyntaxException(ErrorId.INVALID_REPLACEMENT_STRING,
												  "\"" + escapeChar + textCase.key + ch + ILLEGAL_ESCAPE_STR,
												  index - 2);
					}
					state = ReplacementState.GROUP_INDEX;
					break;
				}

				case GROUP_INDEX:
				{
					if (matcher == null)
					{
						while (++index < endIndex)
						{
							ch = expression.charAt(index);
							if ((ch < '0') || (ch > '9'))
								break;
						}
					}
					else
					{
						int value = ch - '0';
						while (++index < endIndex)
						{
							ch = expression.charAt(index);
							if ((ch < '0') || (ch > '9'))
								break;
							int newValue = value * 10 + ch - '0';
							if (newValue > matcher.groupCount())
								break;
							value = newValue;
						}
						if (value <= matcher.groupCount())
						{
							String group = matcher.group(value);
							if (group != null)
							{
								if (textCase == null)
									buffer.append(group);
								else
								{
									switch (textCase)
									{
										case LOWER:
											buffer.append(group.toLowerCase());
											break;

										case UPPER:
											buffer.append(group.toUpperCase());
											break;
									}
								}
							}
						}
					}
					state = ReplacementState.NEXT_CHAR;
					break;
				}

				case UNICODE_ESCAPE:
				{
					int startIndex = index - 2;
					int value = 0;
					for (int i = 0; i < UNICODE_ESCAPE_LENGTH; i++)
					{
						if (index >= endIndex)
						{
							throw new SyntaxException(ErrorId.INVALID_REPLACEMENT_STRING, ILLEGAL_UNICODE_ESCAPE_STR,
													  startIndex);
						}
						ch = Character.toUpperCase(expression.charAt(index++));
						int digitValue = HEX_DIGITS.indexOf(ch);
						if (digitValue < 0)
						{
							throw new SyntaxException(ErrorId.INVALID_REPLACEMENT_STRING, ILLEGAL_UNICODE_ESCAPE_STR,
													  startIndex);
						}
						value <<= 4;
						value += digitValue;
					}
					buffer.append((char)value);
					state = ReplacementState.NEXT_CHAR;
					break;
				}

				case DONE:
					// do nothing
					break;
			}
		}
		return buffer.toString();
	}

	//------------------------------------------------------------------

	private static MainWindow getWindow()
	{
		return RegexSearchApp.INSTANCE.getMainWindow();
	}

	//------------------------------------------------------------------

	private static boolean confirmContinue(
		AppException	exception)
	{
		String[] optionStrs = Utils.getOptionStrings(AppConstants.CONTINUE_STR);
		return (JOptionPane.showOptionDialog(getWindow(), exception, RegexSearchApp.SHORT_NAME,
											 JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE, null, optionStrs,
											 optionStrs[1]) == JOptionPane.OK_OPTION);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public StopSubstate getStopSubstate()
	{
		return stopSubstate;
	}

	//------------------------------------------------------------------

	public List<File> getTargetNotFoundFiles()
	{
		return (targetNotFoundFiles == null)
				? Collections.emptyList()
				: Collections.unmodifiableList(targetNotFoundFiles);
	}

	//------------------------------------------------------------------

	public List<File> getUnprocessedFiles()
	{
		return Collections.unmodifiableList(unprocessedFiles);
	}

	//------------------------------------------------------------------

	public List<File> getAttributesNotSetFiles()
	{
		return Collections.unmodifiableList(attributesNotSetFiles);
	}

	//------------------------------------------------------------------

	public AggregateResult getAggregateResult()
	{
		return new AggregateResult(
			(targetFile == null) ? -1 : numFiles,
			(targetFile == null) ? -1 : numMatchedFiles, numMatches,
			(replacementStr == null) ? -1 : numReplacements
		);
	}

	//------------------------------------------------------------------

	public void startSearch(
		Params	params)
		throws AppException
	{
		// Initialise instance variables
		replacementStr = params.replacementStr;
		regex = params.regex;
		replaceGlobal = false;
		numFiles = 0;
		numMatchedFiles = 0;
		numMatches = 0;
		numReplacements = 0;
		targetNotFoundFiles = params.recordTargetNotFound ? new ArraySet<>() : null;
		unprocessedFiles.clear();
		attributesNotSetFiles.clear();

		// Compile search pattern
		try
		{
			int flags = regex ? Pattern.MULTILINE | Pattern.UNIX_LINES
							  : Pattern.LITERAL;
			if (params.ignoreCase)
				flags |= Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE;
			pattern = Pattern.compile(params.targetStr, flags);
		}
		catch (PatternSyntaxException e)
		{
			// No exception should occur because the target string is validated before starting the search
			e.printStackTrace();
		}

		// Initialise search
		if (params.files == null)
			searchState = SearchState.GET_TEXT;
		else
			initSearch(params);

		// Perform search
		search();
	}

	//------------------------------------------------------------------

	public void resumeSearch(
		Option	option)
		throws AppException
	{
		// Set the search state from the user's response in the search dialog
		switch (option)
		{
			case REPLACE:
				searchState = SearchState.REPLACE;
				break;

			case REPLACE_FILE:
				replaceUnprompted = true;
				searchState = SearchState.REPLACE;
				break;

			case REPLACE_GLOBAL:
				replaceGlobal = true;
				replaceUnprompted = true;
				searchState = SearchState.REPLACE;
				break;

			case SKIP:
				searchState = SearchState.SEARCH;
				break;

			case NEXT_FILE:
				searchState = (targetFile == null) ? SearchState.PUT_TEXT : SearchState.WRITE_FILE;
				break;

			case PREVIEW:
				searchState = SearchState.PREVIEW;
				break;

			case KEEP:
				searchState = SearchState.SEARCH;
				break;

			case RESTORE:
				searchState = SearchState.RESTORE;
				break;

			case CANCEL:
				searchState = SearchState.CANCEL;
				break;
		}

		// Resume search
		search();
	}

	//------------------------------------------------------------------

	private void initSearch(
		Params	params)
		throws AppException
	{
		// Initialise inclusion filters
		PathnameFilter.setErrorMode(PathnameFilter.ErrorMode.LIST);
		PathnameFilter.clearErrors();

		boolean fsIgnoreCase = AppConfig.INSTANCE.isIgnoreFilenameCase();
		List<PathnameFilter> inclusionFilters = new ArrayList<>();
		for (File file : params.files)
		{
			if (file.isFile())
				inclusionFilters.add(new PathnameFilter(file.getPath(), fsIgnoreCase));
			else
			{
				for (String pattern : params.inclusionPatterns)
				{
					if (!new File(pattern).isAbsolute())
					{
						inclusionFilters.add(new PathnameFilter(PathnameFilter.toNormalisedPathname(file, pattern),
																fsIgnoreCase));
					}
				}
			}
		}
		for (String pattern : params.inclusionPatterns)
		{
			if (new File(pattern).isAbsolute())
				inclusionFilters.add(new PathnameFilter(pattern, fsIgnoreCase));
		}

		// Initialise exclusion filter
		List<String> patterns = new ArrayList<>();
		for (String pattern : params.exclusionPatterns)
		{
			if (new File(pattern).isAbsolute())
				patterns.add(pattern);
			else
			{
				for (File file : params.files)
				{
					if (file.isDirectory())
						patterns.add(PathnameFilter.toNormalisedPathname(file, pattern));
				}
			}
		}
		exclusionFilter = new PathnameFilter.MultipleFilter(patterns, fsIgnoreCase, true);

		// Initialise directory stack
		directoryStack.clear();
		for (int i = inclusionFilters.size() - 1; i >= 0; i--)
		{
			PathnameFilter filter = inclusionFilters.get(i);
			String[] paths = filter.getPaths();
			File file = new File(paths[0].isEmpty() ? "." : paths[0]);
			if (paths[1].isEmpty() && !file.isDirectory())
				directoryStack.addFirst(new Directory(file, filter));
			else
			{
				Directory directory = getDirectory(file, filter);
				if (directory == null)
					break;
				directoryStack.addFirst(directory);
			}
		}

		// Perform search
		searchState = (directoryStack.size() == inclusionFilters.size()) ? SearchState.ITERATE_DIRECTORIES
																		 : SearchState.DONE;
	}

	//------------------------------------------------------------------

	private void search()
		throws AppException
	{
		// Execute search state machine
		while (searchState != SearchState.STOP)
		{
			// Test whether task has been cancelled
			if (Task.isCancelled())
				searchState = SearchState.DONE;

			// Perform state actions
			switch (searchState)
			{
				case ITERATE_DIRECTORIES:
				{
					if (directoryStack.isEmpty())
						searchState = SearchState.DONE;
					else
					{
						Directory directory = directoryStack.peekFirst();
						File dir = directory.getNextDirectory();
						if (dir == null)
							searchState = SearchState.ITERATE_FILES;
						else
						{
							directory = getDirectory(dir, directory.getFilter());
							if (directory == null)
								searchState = SearchState.DONE;
							else
							{
								if (!directory.isEmpty())
									directoryStack.addFirst(directory);
							}
						}
					}
					break;
				}

				case ITERATE_FILES:
				{
					Directory directory = directoryStack.peekFirst();
					File file = directory.getNextFile();
					if (file == null)
					{
						directoryStack.removeFirst();
						searchState = SearchState.ITERATE_DIRECTORIES;
					}
					else
					{
						targetFile = file;
						searchState = SearchState.READ_FILE;
					}
					break;
				}

				case READ_FILE:
				{
					if (Task.getProgressView() instanceof TaskProgressDialog progressDialog)
						progressDialog.setInfo(targetFile, numFiles + 1);

					try
					{
						TextModel.Content textModelContent = TextModel.read(targetFile);
						text = textModelContent.text;
						lineSeparator = textModelContent.lineSeparator;
						++numFiles;
						searchState = SearchState.INIT_SEARCH;
					}
					catch (AppException e)
					{
						addUnprocessed(targetFile);
						searchState = confirmContinue(e) ? SearchState.ITERATE_FILES : SearchState.DONE;
					}
					break;
				}

				case GET_TEXT:
				{
					if (Task.getProgressView() instanceof TaskProgressDialog progressDialog)
						progressDialog.setInfo(null, 0);

					targetFile = null;
					text = new StringBuffer(Utils.getClipboardText());
					lineSeparator = LineSeparator.LF;
					++numFiles;
					searchState = SearchState.INIT_SEARCH;
					break;
				}

				case INIT_SEARCH:
				{
					textIndex = -1;
					replaceUnprompted = replaceGlobal;
					numMatchesInFile = 0;
					numReplacementsInFile = 0;
					matcher = pattern.matcher(text);

					searchState = SearchState.SEARCH;
					break;
				}

				case SEARCH:
				{
					if ((textIndex < 0) ? matcher.find() : matcher.find(textIndex))
					{
						// Invalidate text index
						textIndex = -1;

						// Initialise text model
						if (numMatchesInFile == 0)
						{
							++numMatchedFiles;
							SwingUtilities.invokeLater(() ->
									getWindow().initTextModel(targetFile, text, !replaceGlobal));
						}

						// Increment number of matches
						++numMatchesInFile;
						++numMatches;

						// If prompting is disabled for this file, replace text ...
						if (replaceUnprompted)
							searchState = SearchState.REPLACE;

						// ... otherwise, highlight matched text and stop the search
						else
						{
							SwingUtilities.invokeLater(() ->
							{
								getWindow().getTextModel().setSelection(matcher.start(), matcher.end());
								getWindow().makeTextSelectionViewable();
							});
							stopSubstate = StopSubstate.MATCH;
							searchState = SearchState.STOP;
						}
					}
					else
						searchState = (targetFile == null)
													? (numMatchesInFile == 0)
															? SearchState.DONE
															: SearchState.PUT_TEXT
													: SearchState.WRITE_FILE;
					break;
				}

				case REPLACE:
				{
					if ((replacementStr != null) && !replace(TextModel.ReplacementKind.REPLACE))
						searchState = SearchState.DONE;
					searchState = SearchState.SEARCH;
					break;
				}

				case PREVIEW:
				{
					if (!replace(TextModel.ReplacementKind.REPLACE_SAVE_STATE))
						searchState = SearchState.DONE;
					else
					{
						stopSubstate = StopSubstate.PREVIEW;
						searchState = SearchState.STOP;
					}
					break;
				}

				case RESTORE:
				{
					searchState = replace(TextModel.ReplacementKind.RESTORE) ? SearchState.SEARCH : SearchState.DONE;
					break;
				}

				case WRITE_FILE:
				{
					if (numMatchesInFile == 0)
					{
						if (targetNotFoundFiles != null)
							targetNotFoundFiles.add(targetFile);
					}
					else
					{
						if (replacementStr != null)
						{
							try
							{
								try
								{
									getWindow().getTextModel().write(lineSeparator);
									numReplacements += numReplacementsInFile;
								}
								catch (OutOfMemoryError e)
								{
									throw new FileException(ErrorId.NOT_ENOUGH_MEMORY_TO_CONVERT_LINE_SEPARATORS,
															targetFile);
								}
							}
							catch (AppException e)
							{
								if (e instanceof FileAttributeUtils.AttributesException)
								{
									numReplacements += numReplacementsInFile;
									attributesNotSetFiles.add(targetFile);
								}
								else
									addUnprocessed(targetFile);
								if (!confirmContinue(e))
									searchState = SearchState.DONE;
							}
						}

						FileResult result = getFileResult();
						SwingUtilities.invokeLater(() -> getWindow().appendResult(result));
					}
					if (searchState != SearchState.DONE)
						searchState = SearchState.ITERATE_FILES;
					break;
				}

				case PUT_TEXT:
				{
					Utils.putClipboardText(getWindow().getTextModel().getText());
					numReplacements += numReplacementsInFile;
					searchState = SearchState.DONE;
					break;
				}

				case CANCEL:
				{
					if (stopSubstate == StopSubstate.PREVIEW)
						replace(TextModel.ReplacementKind.RESTORE);
					numReplacementsInFile = 0;
					if (targetFile != null)
					{
						FileResult result = getFileResult();
						SwingUtilities.invokeLater(() -> getWindow().appendResult(result));
					}
					searchState = SearchState.DONE;
					break;
				}

				case DONE:
				{
					stopSubstate = StopSubstate.DONE;
					searchState = SearchState.STOP;
					break;
				}

				case STOP:
					// do nothing
					break;
			}
		}
	}

	//------------------------------------------------------------------

	private Directory getDirectory(
		File			directory,
		PathnameFilter	filter)
	{
		// Get relative length of filter
		int filterRelativeLength = 0;
		try
		{
			filterRelativeLength = filter.getRelativeLength(directory);
		}
		catch (AppException e)
		{
			addUnprocessed(directory);
			if (!confirmContinue(e))
				return null;
		}

		// Get files
		File[] files = null;
		if (filter.containsPathWildcards() || (filterRelativeLength >= 1))
		{
			try
			{
				files = directory.listFiles(file -> file.isFile() && filter.accept(file)
													&& ((exclusionFilter == null) || !exclusionFilter.accept(file)));
				if (files == null)
					throw new FileException(ErrorId.FAILED_TO_LIST_DIRECTORY_ENTRIES, directory);
				Arrays.sort(files);
			}
			catch (AppException e)
			{
				addUnprocessed(directory);
				if (!confirmContinue(e))
					return null;
			}
		}

		// Get subdirectories
		File[] directories = null;
		if (filter.containsPathWildcards() || (filterRelativeLength >= 2))
		{
			try
			{
				directories = directory.listFiles(DirectoryFilter.INSTANCE);
				if (directories == null)
					throw new FileException(ErrorId.FAILED_TO_LIST_DIRECTORY_ENTRIES, directory);
				Arrays.sort(directories);
			}
			catch (AppException e)
			{
				addUnprocessed(directory);
				if (!confirmContinue(e))
					return null;
			}
		}

		// Return directory object
		return new Directory(directories, files, filter);
	}

	//------------------------------------------------------------------

	private boolean replace(
		TextModel.ReplacementKind	replacementKind)
		throws AppException
	{
		try
		{
			try
			{
				if (replacementKind == TextModel.ReplacementKind.RESTORE)
				{
					SwingUtilities.invokeAndWait(() ->
					{
						getWindow().getTextModel().replace(replacementKind, 0, 0, null);
						getWindow().makeTextSelectionViewable();
					});
					--numReplacementsInFile;
					textIndex = -1;
				}
				else
				{
					String replacement = createReplacementString(replacementStr, matcher, regex);
					SwingUtilities.invokeAndWait(() ->
					{
						getWindow().getTextModel().replace(replacementKind, matcher.start(), matcher.end(),
														   replacement);
						getWindow().makeTextSelectionViewable();
					});
					++numReplacementsInFile;
					textIndex = matcher.start() + replacement.length();
				}
				return true;
			}
			catch (OutOfMemoryError e)
			{
				throw new FileException(ErrorId.NOT_ENOUGH_MEMORY_TO_REPLACE, targetFile);
			}
			catch (InvocationTargetException e)
			{
				throw new FileException(ErrorId.ERROR_DURING_REPLACEMENT, targetFile, e.getCause());
			}
			catch (Exception e)
			{
				throw new FileException(ErrorId.ERROR_DURING_REPLACEMENT, targetFile, e);
			}
		}
		catch (AppException e)
		{
			addUnprocessed(targetFile);
			return confirmContinue(e);
		}
	}

	//------------------------------------------------------------------

	private void addUnprocessed(
		File	file)
	{
		if (file != null)
			unprocessedFiles.add(file);
	}

	//------------------------------------------------------------------

	private FileResult getFileResult()
	{
		return new FileResult(targetFile, numMatchesInFile, (replacementStr == null) ? -1 : numReplacementsInFile);
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
