/*====================================================================*\

TextModel.java

Text model class.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.regexsearch;

//----------------------------------------------------------------------


// IMPORTS


import java.io.File;

import java.nio.charset.Charset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import uk.blankaspect.common.exception.AppException;

import uk.blankaspect.common.gui.TextArea;

import uk.blankaspect.common.misc.LineSeparator;
import uk.blankaspect.common.misc.StringUtils;
import uk.blankaspect.common.misc.TextFile;

//----------------------------------------------------------------------


// TEXT MODEL CLASS


class TextModel
	implements TextArea.IModel
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	public static final		int	MIN_TAB_WIDTH		= 0;
	public static final		int	MAX_TAB_WIDTH		= 256;
	public static final		int	DEFAULT_TAB_WIDTH	= 4;

	enum ReplacementKind
	{
		REPLACE,
		REPLACE_SAVE_STATE,
		RESTORE
	}

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// SELECTION CLASS


	public static class Selection
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		public Selection(int startRow,
						 int startColumn,
						 int endRow,
						 int endColumn)
		{
			this.startRow = startRow;
			this.startColumn = startColumn;
			this.endRow = endRow;
			this.endColumn = endColumn;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance fields
	////////////////////////////////////////////////////////////////////

		int	startRow;
		int	startColumn;
		int	endRow;
		int	endColumn;

	}

	//==================================================================


	// CONTENT CLASS


	public static class Content
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		public Content(StringBuffer  text,
					   LineSeparator lineSeparator)
		{
			this.text = text;
			this.lineSeparator = lineSeparator;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance fields
	////////////////////////////////////////////////////////////////////

		StringBuffer	text;
		LineSeparator	lineSeparator;

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public TextModel(File         file,
					 StringBuffer text,
					 char         tabGlyphChar)
	{
		// Initialise instance fields
		this.file = file;
		this.tabGlyphChar = tabGlyphChar;
		changeListeners = new ArrayList<>();

		// Initialise the buffer and line offsets
		buffer = (text == null) ? new StringBuffer() : text;
		lineOffsets = getLineOffsets(0, buffer.length());

		// Set the tab width according to the filename
		tabWidth = -1;
		if (file != null)
		{
			for (TabWidthFilter filter : AppConfig.INSTANCE.getTabWidthFilters())
			{
				tabWidth = filter.getTabWidth(file);
				if (tabWidth >= 0)
					break;
			}
		}
		if (tabWidth < 0)
			tabWidth = AppConfig.INSTANCE.getDefaultTabWidth();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static Content read(File file)
		throws AppException
	{
		// Read the file
		StringBuffer text = TextFile.readSync(file, getCharacterEncoding());

		// Change line separators to LFs
		EnumMap<LineSeparator, Integer> lineSeparatorCounts =
									TextFile.changeLineSeparators(text, AppConfig.INSTANCE.
																			isPreserveLineSeparator());

		// Get the predominant kind of line separator
		LineSeparator lineSeparator = LineSeparator.LF;
		if (lineSeparatorCounts != null)
		{
			int maxCount = 0;
			for (LineSeparator ls : lineSeparatorCounts.keySet())
			{
				int count = lineSeparatorCounts.get(ls);
				if (maxCount < count)
				{
					maxCount = count;
					lineSeparator = ls;
				}
			}
		}

		// Return the text and kind of line separator
		return new Content(text, lineSeparator);
	}

	//------------------------------------------------------------------

	public static String getCharacterEncoding()
	{
		String encodingName = AppConfig.INSTANCE.getCharacterEncoding();
		if (encodingName == null)
			encodingName = Charset.defaultCharset().name();
		return encodingName;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : TextArea.IModel interface
////////////////////////////////////////////////////////////////////////

	public int getNumLines()
	{
		int length = buffer.length();
		int numLines = lineOffsets.length;
		if ((length == 0) || (buffer.charAt(length - 1) != '\n'))
			--numLines;
		return numLines;
	}

	//------------------------------------------------------------------

	public TextArea.Line[] getLines(int startIndex,
									int endIndex)
	{
		int i0 = Math.min(Math.max(0, startIndex), lineOffsets.length - 1);
		int i1 = Math.min(Math.max(0, endIndex), lineOffsets.length - 1);
		TextArea.Line[] lines = new TextArea.Line[i1 - i0];
		StringBuilder lineBuffer = new StringBuilder(1024);
		char[] spaces = StringUtils.createCharArray(' ', tabWidth);
		int index = 0;
		for (int i = i0; i < i1; i++)
		{
			lineBuffer.setLength(0);
			int selStartOffset = 0;
			int selEndOffset = 0;
			int outIndex = 0;
			int offset = lineOffsets[i];
			int endOffset = lineOffsets[i + 1] - 1;
			if (buffer.charAt(endOffset) != '\n')
				++endOffset;

			while (offset < endOffset)
			{
				if (offset == selectionStart)
					selStartOffset = outIndex;
				if (offset == selectionEnd)
					selEndOffset = outIndex;
				char ch = buffer.charAt(offset++);
				if (ch == '\t')
				{
					if (tabWidth == 0)
					{
						lineBuffer.append(tabGlyphChar);
						++outIndex;
					}
					else
					{
						int numSpaces = tabWidth - outIndex % tabWidth;
						lineBuffer.append(spaces, 0, numSpaces);
						outIndex += numSpaces;
					}
				}
				else
				{
					lineBuffer.append(ch);
					++outIndex;
				}
			}

			if ((selectionStart < selectionEnd) && (selectionStart <= endOffset) &&
				 (selectionEnd > lineOffsets[i]))
			{
				if (selectionStart == endOffset)
					selStartOffset = outIndex;
				if (selEndOffset == 0)
					selEndOffset = outIndex;
				boolean eol = ((selectionEnd > endOffset) && (buffer.charAt(endOffset) == '\n'));
				lines[index] = new TextArea.Line(lineBuffer.toString(), selStartOffset, selEndOffset,
												 eol);
			}
			else
				lines[index] = new TextArea.Line(lineBuffer.toString());

			++index;
		}
		return lines;
	}

	//------------------------------------------------------------------

	public String getText()
	{
		return buffer.toString();
	}

	//------------------------------------------------------------------

	public void setText(String text)
	{
		// do nothing
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public File getFile()
	{
		return file;
	}

	//------------------------------------------------------------------

	public int getSelectionStart()
	{
		return selectionStart;
	}

	//------------------------------------------------------------------

	public int getSelectionEnd()
	{
		return selectionEnd;
	}

	//------------------------------------------------------------------

	public Selection getSelection()
	{
		// Get the row and column of the start of the selection
		int startRow = Arrays.binarySearch(lineOffsets, selectionStart);
		if (startRow < 0)
			startRow = -2 - startRow;
		TextArea.Line[] lines = getLines(startRow, startRow + 1);
		int startColumn = (lines.length == 0) ? 0 : lines[0].highlightStartOffset;

		// Get the row and column of the end of the selection
		int endRow = Arrays.binarySearch(lineOffsets, selectionEnd);
		if (endRow < 0)
			endRow = -2 - endRow;
		else
			--endRow;
		if (endRow != startRow)
			lines = getLines(endRow, endRow + 1);
		int endColumn = (lines.length == 0) ? 0 : lines[0].highlightEndOffset;

		// Return the rows and columns of the selection
		return new Selection(startRow, startColumn, endRow, endColumn);
	}

	//------------------------------------------------------------------

	public void setSelection(int start,
							 int end)
	{
		if ((selectionStart != start) || (selectionEnd != end))
		{
			selectionStart = start;
			selectionEnd = end;
			fireStateChanged();
		}
	}

	//------------------------------------------------------------------

	public void replace(ReplacementKind replacementKind,
						int             startIndex,
						int             endIndex,
						String          replacement)
	{
		// If the original text is to be restored, set the replacement variables accordingly
		if (replacementKind == ReplacementKind.RESTORE)
		{
			startIndex = selectionStart;
			endIndex = selectionEnd;
			replacement = oldText;
		}

		// Save the current state
		if (replacementKind == ReplacementKind.REPLACE_SAVE_STATE)
		{
			oldText = buffer.substring(startIndex, endIndex);
			oldChanged = changed;
		}

		// Test whether the replacement is different from the matched text
		if (replacement.equals(buffer.substring(startIndex, endIndex)))
			return;

		// Replace the matched text
		buffer.replace(startIndex, endIndex, replacement);

		// Set the "model has changed" flag
		changed = (replacementKind == ReplacementKind.RESTORE) ? oldChanged : true;

		// Get the index of the start line of the matched text
		int startLineIndex = Arrays.binarySearch(lineOffsets, startIndex);
		if (startLineIndex < 0)
			startLineIndex = -2 - startLineIndex;

		// If the start line is past the end of the buffer and the file doesn't end with a line separator,
		// start at the previous line
		if ((startLineIndex > 0) && (buffer.charAt(lineOffsets[startLineIndex] - 1) != '\n'))
			--startLineIndex;

		// Get the index of the end line of the matched text
		int endLineIndex = Arrays.binarySearch(lineOffsets, endIndex);
		if (endLineIndex < 0)
			endLineIndex = -1 - endLineIndex;

		// Get the line offsets of the replacement region
		int[] offsets = getLineOffsets(lineOffsets[startLineIndex], startIndex + replacement.length());

		// Fix up the offsets of the lines after the replacement
		int deltaLength = replacement.length() - (endIndex - startIndex);
		for (int i = endLineIndex; i < lineOffsets.length; i++)
			lineOffsets[i] += deltaLength;

		// Increment the index of the end line until the fixed-up offset of the end line is beyond the last
		// line offset of the replacement region
		int endOffset = offsets[offsets.length - 1];
		while ((endLineIndex < lineOffsets.length) && (lineOffsets[endLineIndex] <= endOffset))
			++endLineIndex;

		// Update the line offsets by concatenating the old offsets before the replacement region, the
		// offsets of the replacement region and the fixed-up offsets after the replacement region
		int[] newLineOffsets = new int[startLineIndex + offsets.length + lineOffsets.length - endLineIndex];
		System.arraycopy(lineOffsets, 0, newLineOffsets, 0, startLineIndex);
		System.arraycopy(offsets, 0, newLineOffsets, startLineIndex, offsets.length);
		System.arraycopy(lineOffsets, endLineIndex, newLineOffsets, startLineIndex + offsets.length,
						 lineOffsets.length - endLineIndex);
		lineOffsets = newLineOffsets;

		// Select the replacement
		selectionStart = startIndex;
		selectionEnd = startIndex + replacement.length();

		// Notify listeners of a change to the text model
		fireStateChanged();
	}

	//------------------------------------------------------------------

	public void write(LineSeparator lineSeparator)
		throws AppException
	{
		if (changed)
		{
			StringBuffer outBuffer = buffer;
			switch (lineSeparator)
			{
				case CR:
				{
					int length = buffer.length();
					outBuffer = new StringBuffer(length);
					for (int i = 0; i < length; i++)
					{
						char ch = buffer.charAt(i);
						if (ch == '\n')
							ch = '\r';
						outBuffer.append(ch);
					}
					break;
				}

				case LF:
					// do nothing
					break;

				case CR_LF:
				{
					int length = buffer.length();
					outBuffer = new StringBuffer(length + lineOffsets.length);
					for (int i = 0; i < length; i++)
					{
						char ch = buffer.charAt(i);
						if (ch == '\n')
							outBuffer.append('\r');
						outBuffer.append(ch);
					}
					break;
				}
			}
			TextFile.write(file, getCharacterEncoding(), outBuffer,
						   AppConfig.INSTANCE.getFileWritingMode());
		}
	}

	//------------------------------------------------------------------

	public void addChangeListener(ChangeListener listener)
	{
		changeListeners.add(listener);
	}

	//------------------------------------------------------------------

	private void fireStateChanged()
	{
		for (int i = changeListeners.size() - 1; i >= 0; i--)
		{
			if (changeEvent == null)
				changeEvent = new ChangeEvent(this);
			changeListeners.get(i).stateChanged(changeEvent);
		}
	}

	//------------------------------------------------------------------

	private int[] getLineOffsets(int startOffset,
								 int endOffset)
	{
		final	int	NUM_BITS		= 8;
		final	int	ARRAY_LENGTH	= 1 << NUM_BITS;
		final	int	MASK			= ARRAY_LENGTH - 1;

		List<int[]> offsetArrays = new ArrayList<>();
		int[] offsets = null;
		int numLines = 0;
		int offset = startOffset;
		while (offset < endOffset)
		{
			if ((numLines & MASK) == 0)
			{
				offsets = new int[ARRAY_LENGTH];
				offsetArrays.add(offsets);
			}
			offsets[numLines & MASK] = offset;
			++numLines;
			offset = buffer.indexOf("\n", offset);
			if (offset < 0)
				offset = buffer.length();
			else
				++offset;
		}

		offsets = new int[numLines + 1];
		int offsetArrayIndex = 0;
		int lineIndex = 0;
		while (lineIndex < numLines)
		{
			int length = Math.min(numLines - lineIndex, ARRAY_LENGTH);
			System.arraycopy(offsetArrays.get(offsetArrayIndex++), 0, offsets, lineIndex, length);
			lineIndex += length;
		}
		offsets[numLines] = offset;
		return offsets;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance fields
////////////////////////////////////////////////////////////////////////

	private	File					file;
	private	StringBuffer			buffer;
	private	int[]					lineOffsets;
	private	int						tabWidth;
	private	char					tabGlyphChar;
	private	int						selectionStart;
	private	int						selectionEnd;
	private	boolean					changed;
	private	List<ChangeListener>	changeListeners;
	private	ChangeEvent				changeEvent;
	private	String					oldText;
	private	boolean					oldChanged;

}

//----------------------------------------------------------------------
