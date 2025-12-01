/*====================================================================*\

FileSet.java

File set class.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.regexsearch;

//----------------------------------------------------------------------


// IMPORTS


import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import uk.blankaspect.common.exception.AppException;

import uk.blankaspect.common.misc.EscapedTextUtils;

import uk.blankaspect.common.xml.AttributeList;
import uk.blankaspect.common.xml.XmlParseException;
import uk.blankaspect.common.xml.XmlUtils;
import uk.blankaspect.common.xml.XmlWriter;

//----------------------------------------------------------------------


// FILE SET CLASS


class FileSet
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	public static final	int	MAX_NUM_PATHNAMES		= 64;
	public static final	int	MAX_NUM_FILTERS			= 64;
	public static final	int	MAX_NUM_FILTER_PATTERNS	= 64;

	private interface ElementName
	{
		String	EXCLUDE		= "exclude";
		String	FILE_SET	= "fileSet";
		String	INCLUDE		= "include";
		String	PATHNAME	= "pathname";
		String	PATTERN		= "pattern";
	}

	private interface AttrName
	{
		String	EXCLUDE_INDEX	= "excludeIndex";
		String	INCLUDE_INDEX	= "includeIndex";
		String	KIND			= "kind";
		String	PATHNAME_INDEX	= "pathnameIndex";
	}

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	Kind				kind;
	private	List<String>		pathnames;
	private	int					pathnameIndex;
	private	List<List<String>>	inclusionFilters;
	private	int					inclusionFilterIndex;
	private	List<List<String>>	exclusionFilters;
	private	int					exclusionFilterIndex;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public FileSet()
	{
		kind = Kind.FILE;
		pathnames = new ArrayList<>();
		inclusionFilters = new ArrayList<>();
		exclusionFilters = new ArrayList<>();
	}

	//------------------------------------------------------------------

	public FileSet(Kind               kind,
				   List<String>       pathnames,
				   int                pathnameIndex,
				   List<List<String>> inclusionPatterns,
				   int                inclusionFilterIndex,
				   List<List<String>> exclusionPatterns,
				   int                exclusionFilterIndex)
	{
		this.kind = kind;
		this.pathnames = pathnames;
		this.pathnameIndex = pathnameIndex;
		this.inclusionFilters = inclusionPatterns;
		this.inclusionFilterIndex = inclusionFilterIndex;
		this.exclusionFilters = exclusionPatterns;
		this.exclusionFilterIndex = exclusionFilterIndex;
	}

	//------------------------------------------------------------------

	public FileSet(Element element)
		throws XmlParseException
	{
		String elementPath = XmlUtils.getElementPath(element);

		// Attribute: kind
		String attrName = AttrName.KIND;
		String attrKey = XmlUtils.appendAttributeName(elementPath, attrName);
		String attrValue = XmlUtils.getAttribute(element, attrName);
		if (attrValue == null)
			throw new XmlParseException(ErrorId.NO_ATTRIBUTE, attrKey);
		kind = Kind.forKey(attrValue);
		if (kind == null)
			throw new XmlParseException(ErrorId.INVALID_ATTRIBUTE, attrKey, attrValue);

		// Attribute: pathname index
		attrName = AttrName.PATHNAME_INDEX;
		attrKey = XmlUtils.appendAttributeName(elementPath, attrName);
		attrValue = XmlUtils.getAttribute(element, attrName);
		if (attrValue != null)
		{
			try
			{
				pathnameIndex = Integer.parseInt(attrValue);
			}
			catch (NumberFormatException e)
			{
				throw new XmlParseException(ErrorId.INVALID_ATTRIBUTE, attrKey, attrValue);
			}
		}

		// Attribute: inclusion filter index
		attrName = AttrName.INCLUDE_INDEX;
		attrKey = XmlUtils.appendAttributeName(elementPath, attrName);
		attrValue = XmlUtils.getAttribute(element, attrName);
		if (attrValue != null)
		{
			try
			{
				inclusionFilterIndex = Integer.parseInt(attrValue);
			}
			catch (NumberFormatException e)
			{
				throw new XmlParseException(ErrorId.INVALID_ATTRIBUTE, attrKey, attrValue);
			}
		}

		// Attribute: exclusion filter index
		attrName = AttrName.EXCLUDE_INDEX;
		attrKey = XmlUtils.appendAttributeName(elementPath, attrName);
		attrValue = XmlUtils.getAttribute(element, attrName);
		if (attrValue != null)
		{
			try
			{
				exclusionFilterIndex = Integer.parseInt(attrValue);
			}
			catch (NumberFormatException e)
			{
				throw new XmlParseException(ErrorId.INVALID_ATTRIBUTE, attrKey, attrValue);
			}
		}

		// Parse pathnames, inclusion filters and exclusion filters
		pathnames = new ArrayList<>();
		inclusionFilters = new ArrayList<>();
		exclusionFilters = new ArrayList<>();
		NodeList childNodes = element.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++)
		{
			Node node = childNodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE)
			{
				Element childElement = (Element)node;
				String elementName = node.getNodeName();

				// Pathname
				if (kind.hasPathname() && elementName.equals(ElementName.PATHNAME))
					pathnames.add(childElement.getTextContent());

				// Filters
				if (kind.hasFilters())
				{
					// Inclusion filter
					if (elementName.equals(ElementName.INCLUDE))
						inclusionFilters.add(parseFilter(childElement));

					// Exclusion filter
					else if (elementName.equals(ElementName.EXCLUDE))
						exclusionFilters.add(parseFilter(childElement));
				}
			}
		}

		// Fix up pathname index and filter indices
		pathnameIndex = Math.min(Math.max(0, pathnameIndex), pathnames.size() - 1);
		inclusionFilterIndex = Math.min(Math.max(0, inclusionFilterIndex),
										inclusionFilters.size() - 1);
		exclusionFilterIndex = Math.min(Math.max(0, exclusionFilterIndex),
										exclusionFilters.size() - 1);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static String getElementName()
	{
		return ElementName.FILE_SET;
	}

	//------------------------------------------------------------------

	public static List<String> stringToPatterns(String str)
	{
		List<String> patterns = new ArrayList<>();
		for (String s : EscapedTextUtils.split(str, ' '))
		{
			if (!s.isEmpty())
				patterns.add(s);
		}
		return patterns;
	}

	//------------------------------------------------------------------

	public static String patternsToString(List<String> patterns)
	{
		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < patterns.size(); i++)
		{
			if (i > 0)
				buffer.append(' ');
			buffer.append(EscapedTextUtils.escapeSeparator(patterns.get(i), ' '));
		}
		return buffer.toString();
	}

	//------------------------------------------------------------------

	private static List<String> parseFilter(Element element)
		throws XmlParseException
	{
		List<String> patterns = new ArrayList<>();
		NodeList childNodes = element.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++)
		{
			Node node = childNodes.item(i);
			if ((node.getNodeType() == Node.ELEMENT_NODE) &&
				 node.getNodeName().equals(ElementName.PATTERN))
				patterns.add(((Element)node).getTextContent());
		}
		return patterns;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof FileSet)
		{
			FileSet fileSet = (FileSet)obj;
			return ((kind == fileSet.kind) && pathnames.equals(fileSet.pathnames) &&
					 (pathnameIndex == fileSet.pathnameIndex) &&
					 inclusionFilters.equals(fileSet.inclusionFilters) &&
					 (inclusionFilterIndex == fileSet.inclusionFilterIndex) &&
					 exclusionFilters.equals(fileSet.exclusionFilters) &&
					 (exclusionFilterIndex == fileSet.exclusionFilterIndex));
		}
		return false;
	}

	//------------------------------------------------------------------

	@Override
	public int hashCode()
	{
		int code = kind.ordinal();
		code = 31 * code + pathnames.hashCode();
		code = 31 * code + pathnameIndex;
		code = 31 * code + inclusionFilters.hashCode();
		code = 31 * code + inclusionFilterIndex;
		code = 31 * code + exclusionFilters.hashCode();
		code = 31 * code + exclusionFilterIndex;
		return code;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public Kind getKind()
	{
		return kind;
	}

	//------------------------------------------------------------------

	public String getPathname(int index)
	{
		return pathnames.get(index);
	}

	//------------------------------------------------------------------

	public List<String> getPathnames()
	{
		return Collections.unmodifiableList(pathnames);
	}

	//------------------------------------------------------------------

	public int getPathnameIndex()
	{
		return pathnameIndex;
	}

	//------------------------------------------------------------------

	public List<String> getInclusionFilter(int index)
	{
		return Collections.unmodifiableList(inclusionFilters.get(index));
	}

	//------------------------------------------------------------------

	public List<List<String>> getInclusionFilters()
	{
		List<List<String>> filters = new ArrayList<>();
		for (int i = 0; i < inclusionFilters.size(); i++)
			filters.add(getInclusionFilter(i));
		return filters;
	}

	//------------------------------------------------------------------

	public int getInclusionFilterIndex()
	{
		return inclusionFilterIndex;
	}

	//------------------------------------------------------------------

	public List<String> getExclusionFilter(int index)
	{
		return Collections.unmodifiableList(exclusionFilters.get(index));
	}

	//------------------------------------------------------------------

	public List<List<String>> getExclusionFilters()
	{
		List<List<String>> filters = new ArrayList<>();
		for (int i = 0; i < exclusionFilters.size(); i++)
			filters.add(getExclusionFilter(i));
		return filters;
	}

	//------------------------------------------------------------------

	public int getExclusionFilterIndex()
	{
		return exclusionFilterIndex;
	}

	//------------------------------------------------------------------

	public void setInclusionFilters(List<List<String>> filters)
	{
		inclusionFilters = filters;
	}

	//------------------------------------------------------------------

	public void setExclusionFilters(List<List<String>> filters)
	{
		exclusionFilters = filters;
	}

	//------------------------------------------------------------------

	public void write(XmlWriter writer,
					  int       indent)
		throws IOException
	{
		// Write root element start tag
		AttributeList attributes = new AttributeList();
		attributes.add(AttrName.KIND, kind.getKey());
		if (!pathnames.isEmpty())
			attributes.add(AttrName.PATHNAME_INDEX, pathnameIndex);
		if (kind.hasFilters())
		{
			if (!inclusionFilters.isEmpty())
				attributes.add(AttrName.INCLUDE_INDEX, inclusionFilterIndex);
			if (!exclusionFilters.isEmpty())
				attributes.add(AttrName.EXCLUDE_INDEX, exclusionFilterIndex);
		}
		writer.writeElementStart(ElementName.FILE_SET, attributes, indent, true, true);

		// Write pathnames
		if (kind.hasPathname())
		{
			for (String pathname : pathnames)
				writer.writeEscapedTextElement(ElementName.PATHNAME, indent + 2, pathname);
		}

		// Write filters
		if (kind.hasFilters())
		{
			// Write inclusion filters
			for (List<String> filter : inclusionFilters)
			{
				writer.writeElementStart(ElementName.INCLUDE, null, indent + 2, true, false);
				for (String pattern : filter)
					writer.writeEscapedTextElement(ElementName.PATTERN, indent + 4, pattern);
				writer.writeElementEnd(ElementName.INCLUDE, indent + 2);
			}

			// Write exclusion filters
			for (List<String> filter : exclusionFilters)
			{
				writer.writeElementStart(ElementName.EXCLUDE, null, indent + 2, true, false);
				for (String pattern : filter)
					writer.writeEscapedTextElement(ElementName.PATTERN, indent + 4, pattern);
				writer.writeElementEnd(ElementName.EXCLUDE, indent + 2);
			}
		}

		// Write root element end tag
		writer.writeElementEnd(ElementName.FILE_SET, indent);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Enumerated types
////////////////////////////////////////////////////////////////////////


	// KIND OF FILE SET


	enum Kind
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		FILE
		(
			"file",
			"File"
		),

		DIRECTORY
		(
			"directory",
			"Directory"
		),

		LIST
		(
			"list",
			"List"
		),

		RESULTS
		(
			"results",
			"Results"
		),

		CLIPBOARD
		(
			"clipboard",
			"Clipboard"
		);

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	String	key;
		private	String	text;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private Kind(String key,
					 String text)
		{
			this.key = key;
			this.text = text;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Class methods
	////////////////////////////////////////////////////////////////////

		public static Kind forKey(String key)
		{
			for (Kind value : values())
			{
				if (value.key.equals(key))
					return value;
			}
			return null;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		public String toString()
		{
			return text;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		public String getKey()
		{
			return key;
		}

		//--------------------------------------------------------------

		public boolean hasPathname()
		{
			return (this != RESULTS) && (this != CLIPBOARD);
		}

		//--------------------------------------------------------------

		public boolean hasFilters()
		{
			return (this == DIRECTORY) || (this == LIST);
		}

		//--------------------------------------------------------------

	}

	//==================================================================


	// KIND OF FILTER


	enum FilterKind
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		INCLUSION
		(
			"Inclusion"
		),

		EXCLUSION
		(
			"Exclusion"
		);

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	String	text;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private FilterKind(String text)
		{
			this.text = text;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		public String toString()
		{
			return text;
		}

		//--------------------------------------------------------------

	}

	//==================================================================


	// ERROR IDENTIFIERS


	private enum ErrorId
		implements AppException.IId
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		NO_ATTRIBUTE
		("The required attribute is missing."),

		INVALID_ATTRIBUTE
		("The attribute is invalid.");

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	String	message;

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

		@Override
		public String getMessage()
		{
			return message;
		}

		//--------------------------------------------------------------

	}

	//==================================================================

}

//----------------------------------------------------------------------
