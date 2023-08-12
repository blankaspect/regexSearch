/*====================================================================*\

ResultList.java

Result list class.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.regexsearch;

//----------------------------------------------------------------------


// IMPORTS


import java.io.File;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import uk.blankaspect.common.filesystem.PathnameUtils;

import uk.blankaspect.ui.swing.textarea.TextArea;

//----------------------------------------------------------------------


// RESULT LIST CLASS


class ResultList
	implements TextArea.IModel
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	private static final	String	FOUND_STR				= "found ";
	private static final	String	REPLACED_STR			= "replaced ";
	private static final	String	NUM_FILES_STR			= "Number of files searched";
	private static final	String	NUM_MATCHED_FILES_STR	= "Number of files containing matches";
	private static final	String	NUM_MATCHES_STR			= "Number of matches";
	private static final	String	NUM_REPLACEMENTS_STR	= "Number of replacements";
	private static final	String	EQUALS_STR				= " = ";

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// ELEMENT CLASS


	private static class Element
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private Element()
		{
		}

		//--------------------------------------------------------------

		private Element(String text)
		{
			this.text = text;
		}

		//--------------------------------------------------------------

		private Element(String  pathname,
						String  text,
						boolean searchedFile)
		{
			this.pathname = pathname;
			this.text = text;
			this.searchedFile = searchedFile;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		public String toString()
		{
			StringBuilder buffer = new StringBuilder();
			if (pathname != null)
				buffer.append(pathname);
			if ((pathname != null) && (text != null))
				buffer.append("  [");
			if (text != null)
				buffer.append(text);
			if ((pathname != null) && (text != null))
				buffer.append(']');
			return buffer.toString();
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		public String toString(char commentPrefix)
		{
			StringBuilder buffer = new StringBuilder();
			if (pathname != null)
			{
				buffer.append(pathname);
				buffer.append("  ");
			}
			if (text != null)
			{
				buffer.append(commentPrefix);
				buffer.append(' ');
				buffer.append(text);
			}
			return buffer.toString();
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	String	pathname;
		private	String	text;
		private	boolean	searchedFile;
		private	boolean	selected;

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public ResultList()
	{
		elements = new ArrayList<>();
		files = new ArrayList<>();
		changeListeners = new ArrayList<>();
		updateFiles();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : TextArea.IModel interface
////////////////////////////////////////////////////////////////////////

	public int getNumLines()
	{
		return elements.size();
	}

	//------------------------------------------------------------------

	public TextArea.Line[] getLines(int startIndex,
									int endIndex)
	{
		if ((startIndex < 0) || (startIndex > elements.size()))
			startIndex = elements.size();
		if ((endIndex < 0) || (endIndex > elements.size()))
			endIndex = elements.size();

		TextArea.Line[] lines = new TextArea.Line[endIndex - startIndex];
		int index = 0;
		for (int i = startIndex; i < endIndex; i++)
		{
			Element element = elements.get(i);
			int endOffset = (element.selected && (element.pathname != null)) ? element.pathname.length() : 0;
			lines[index++] = new TextArea.Line(elements.get(i).toString(), 0, endOffset, false);
		}
		return lines;
	}

	//------------------------------------------------------------------

	public String getText()
	{
		StringBuilder buffer = new StringBuilder();
		for (Element element : elements)
		{
			buffer.append(element);
			buffer.append('\n');
		}
		return buffer.toString();
	}

	//------------------------------------------------------------------

	public void setText(String text)
	{
		if (text == null)
			elements.clear();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public List<File> getFiles()
	{
		return files;
	}

	//------------------------------------------------------------------

	public boolean isSearchedFiles()
	{
		for (Element element : elements)
		{
			if (element.searchedFile)
				return true;
		}
		return false;
	}

	//------------------------------------------------------------------

	public String getSearchedPathname(int index)
	{
		String pathname = null;
		if ((index >= 0) && (index < elements.size()))
		{
			Element element = elements.get(index);
			if (element.searchedFile)
				pathname = element.pathname;
		}
		return pathname;
	}

	//------------------------------------------------------------------

	public void addFile(TextSearcher.FileResult result)
	{
		StringBuilder buffer = new StringBuilder(32);
		buffer.append(FOUND_STR);
		buffer.append(result.numMatches);
		if (result.numReplacements >= 0)
		{
			buffer.append(", ");
			buffer.append(REPLACED_STR);
			buffer.append(result.numReplacements);
		}
		elements.add(new Element(Utils.getPathname(result.file), buffer.toString(), true));
		fireStateChanged();
	}

	//------------------------------------------------------------------

	public void addAggregate(TextSearcher.AggregateResult result)
	{
		if (!elements.isEmpty())
			elements.add(new Element());
		if (result.numFiles >= 0)
			elements.add(new Element(NUM_FILES_STR + EQUALS_STR + result.numFiles));
		if (result.numMatchedFiles >= 0)
			elements.add(new Element(NUM_MATCHED_FILES_STR + EQUALS_STR + result.numMatchedFiles));
		elements.add(new Element(NUM_MATCHES_STR + EQUALS_STR + result.numMatches));
		if (result.numReplacements >= 0)
			elements.add(new Element(NUM_REPLACEMENTS_STR + EQUALS_STR + result.numReplacements));
		fireStateChanged();
	}

	//------------------------------------------------------------------

	public void addFiles(String     text,
						 List<File> files,
						 boolean    fullPathname)
	{
		if (!elements.isEmpty())
			elements.add(new Element());
		elements.add(new Element("[ " + text + " ]"));
		for (File file : files)
			elements.add(new Element(fullPathname ? Utils.getPathname(file) : file.getPath(), null, false));
		fireStateChanged();
	}

	//------------------------------------------------------------------

	public boolean isEmpty()
	{
		return elements.isEmpty();
	}

	//------------------------------------------------------------------

	public String getText(char commentPrefix)
	{
		StringBuilder buffer = new StringBuilder();
		for (Element element : elements)
		{
			buffer.append(element.toString(commentPrefix));
			buffer.append('\n');
		}
		return buffer.toString();
	}

	//------------------------------------------------------------------

	public void setElementSelected(int     index,
								   boolean selected)
	{
		elements.get(index).selected = selected;
	}

	//------------------------------------------------------------------

	public void updateFiles()
	{
		files.clear();
		for (Element element : elements)
		{
			if (element.searchedFile)
				files.add(new File(PathnameUtils.parsePathname(element.pathname)));
		}
	}

	//------------------------------------------------------------------

	public void addChangeListener(ChangeListener listener)
	{
		changeListeners.add(listener);
	}

	//------------------------------------------------------------------

	protected void fireStateChanged()
	{
		for (int i = changeListeners.size() - 1; i >= 0; i--)
		{
			if (changeEvent == null)
				changeEvent = new ChangeEvent(this);
			changeListeners.get(i).stateChanged(changeEvent);
		}
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	List<Element>			elements;
	private	List<File>				files;
	private	List<ChangeListener>	changeListeners;
	private	ChangeEvent				changeEvent;

}

//----------------------------------------------------------------------
