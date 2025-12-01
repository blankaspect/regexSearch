/*====================================================================*\

XmlWriter.java

XML writer class.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.xml;

//----------------------------------------------------------------------


// IMPORTS


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import java.nio.charset.Charset;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import uk.blankaspect.common.misc.SystemUtils;

//----------------------------------------------------------------------


// XML WRITER CLASS


public class XmlWriter
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	public enum Standalone
	{
		NONE,
		NO,
		YES
	}

	public static final		int		INDENT_INCREMENT	= 2;

	public static final		String	COMMENT_PREFIX	= "<!--";
	public static final		String	COMMENT_SUFFIX	= "-->";

	private static final	String	DEFAULT_LINE_SEPARATOR	= "\n";

	private static final	String	XML_DECL_PREFIX	= "<?xml";

	private static final	String	VERSION_STR		= " version=";
	private static final	String	ENCODING_STR	= " encoding=";
	private static final	String	STANDALONE_STR	= " standalone=";

	private static final	String	DOCTYPE_STR	= "<!DOCTYPE ";
	private static final	String	PUBLIC_STR	= "PUBLIC ";
	private static final	String	SYSTEM_STR	= "SYSTEM ";

	private static final	String	NO_STR	= "no";
	private static final	String	YES_STR	= "yes";

	private interface AttrName
	{
		String	XMLNS	= "xmlns";
	}

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	FileOutputStream	fileOutStream;
	private	Writer				outStream;
	private	StringBuilder		outBuffer;
	private	String				lineSeparator;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public XmlWriter(
		File	file,
		Charset	encoding)
		throws FileNotFoundException, SecurityException
	{
		fileOutStream = new FileOutputStream(file);
		init(new BufferedWriter(new OutputStreamWriter(fileOutStream, encoding)));
	}

	//------------------------------------------------------------------

	public XmlWriter(
		OutputStream	outStream,
		Charset			encoding)
	{
		init(new BufferedWriter(new OutputStreamWriter(outStream, encoding)));
	}

	//------------------------------------------------------------------

	public XmlWriter(
		Writer	writer)
	{
		init(writer);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static List<Attribute> createAttributeList(
		Element	element)
	{
		List<Attribute> attributes = new ArrayList<>();
		NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++)
		{
			Node node = attrs.item(i);
			Attribute attribute = new Attribute(node.getNodeName(), node.getNodeValue(), true);
			if (node.getNodeName().startsWith(AttrName.XMLNS))
				attributes.add(0, attribute);
			else
				attributes.add(attribute);
		}
		return attributes;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public FileOutputStream getFileOutStream()
	{
		return fileOutStream;
	}

	//------------------------------------------------------------------

	public String getLineSeparator()
	{
		return lineSeparator;
	}

	//------------------------------------------------------------------

	public int getOutLength()
	{
		return outBuffer.length();
	}

	//------------------------------------------------------------------

	public void setLineSeparator(
		String	lineSeparator)
	{
		this.lineSeparator = (lineSeparator == null) ? SystemUtils.lineSeparator() : lineSeparator;
	}

	//------------------------------------------------------------------

	public void close()
		throws IOException
	{
		try
		{
			if (outStream != null)
			{
				outStream.append(outBuffer);
				outStream.close();
			}
		}
		catch (IOException e)
		{
			throw e;
		}
		finally
		{
			outStream = null;
			outBuffer.setLength(0);
		}
	}

	//------------------------------------------------------------------

	public void writeXmlDeclaration(
		CharSequence	versionStr,
		CharSequence	encodingName,
		Standalone		standalone)
		throws IOException
	{
		write(XML_DECL_PREFIX);
		if (versionStr != null)
		{
			write(VERSION_STR);
			writeQuoted(versionStr);
		}
		if (encodingName != null)
		{
			write(ENCODING_STR);
			writeQuoted(encodingName);
		}
		if (standalone != Standalone.NONE)
		{
			write(STANDALONE_STR);
			writeQuoted((standalone == Standalone.NO) ? NO_STR : YES_STR);
		}
		write("?>");
		writeEol();
	}

	//------------------------------------------------------------------

	public void writeDocumentType(
		CharSequence	documentName,
		CharSequence	systemId,
		CharSequence	publicId)
		throws IOException
	{
		write(DOCTYPE_STR);
		write(documentName);
		writeEol();
		writeSpaces(INDENT_INCREMENT + 1);
		if (publicId != null)
		{
			write(PUBLIC_STR);
			writeQuoted(publicId);
			writeEol();
			writeSpaces(INDENT_INCREMENT + 1);
		}
		else
			write(SYSTEM_STR);
		writeQuoted(systemId);
		write('>');
		writeEol();
	}

	//------------------------------------------------------------------

	public void writeProcessingInstruction(
		CharSequence	target,
		CharSequence	data)
		throws IOException
	{
		write("<?");
		write(target);
		write(' ');
		write(data);
		write("?>");
		writeEol();
	}

	//------------------------------------------------------------------

	public void writeElementStart(
		CharSequence	name,
		int				indent,
		boolean			elementNewLine)
		throws IOException
	{
		writeElementStart(name, null, indent, elementNewLine, false);
	}

	//------------------------------------------------------------------

	public void writeElementStart(
		CharSequence		name,
		Iterable<Attribute>	attributes,
		int					indent,
		boolean				elementNewLine,
		boolean				attrNewLine)
		throws IOException
	{
		writeSpaces(indent);
		write('<');
		write(name);
		if (attributes != null)
			writeAttributes(attributes, attrNewLine ? indent + INDENT_INCREMENT : 0);
		write('>');
		if (elementNewLine)
			writeEol();
	}

	//------------------------------------------------------------------

	public void writeEndTag(
		CharSequence	name)
		throws IOException
	{
		write("</");
		write(name);
		write('>');
	}

	//------------------------------------------------------------------

	public void writeElementEnd(
		CharSequence	name,
		int				indent)
		throws IOException
	{
		writeSpaces(indent);
		writeEndTag(name);
		writeEol();
	}

	//------------------------------------------------------------------

	public void writeEmptyElement(
		CharSequence	name,
		int				indent)
		throws IOException
	{
		writeEmptyElement(name, null, indent, true, false);
	}

	//------------------------------------------------------------------

	public void writeEmptyElement(
		CharSequence		name,
		Iterable<Attribute>	attributes,
		int					indent,
		boolean				attrNewLine)
		throws IOException
	{
		writeEmptyElement(name, attributes, indent, true, attrNewLine);
	}

	//------------------------------------------------------------------

	public void writeEmptyElement(
		CharSequence		name,
		Iterable<Attribute>	attributes,
		int					indent,
		boolean				elementNewLine,
		boolean				attrNewLine)
		throws IOException
	{
		writeSpaces(indent);
		write('<');
		write(name);
		if (attributes != null)
			writeAttributes(attributes, attrNewLine ? indent + INDENT_INCREMENT : 0);
		write("/>");
		if (elementNewLine)
			writeEol();
	}

	//------------------------------------------------------------------

	public void writeTextElement(
		CharSequence	name,
		int				indent,
		CharSequence	text)
		throws IOException
	{
		writeTextElement(name, null, indent, false, text);
	}

	//------------------------------------------------------------------

	public void writeTextElement(
		CharSequence		name,
		Iterable<Attribute>	attributes,
		int					indent,
		boolean				attrNewLine,
		CharSequence		text)
		throws IOException
	{
		writeElementStart(name, attributes, indent, false, attrNewLine);
		write(text);
		writeElementEnd(name, 0);
	}

	//------------------------------------------------------------------

	public void writeEscapedTextElement(
		CharSequence	name,
		int				indent,
		CharSequence	text)
		throws IOException
	{
		writeEscapedTextElement(name, null, indent, false, text);
	}

	//------------------------------------------------------------------

	public void writeEscapedTextElement(
		CharSequence		name,
		Iterable<Attribute>	attributes,
		int					indent,
		boolean				attrNewLine,
		CharSequence		text)
		throws IOException
	{
		writeElementStart(name, attributes, indent, false, attrNewLine);
		writeEscaped(text);
		writeElementEnd(name, 0);
	}

	//------------------------------------------------------------------

	public void writeComment(
		CharSequence	comment,
		int				indent)
		throws IOException
	{
		writeSpaces(indent);
		write(COMMENT_PREFIX);
		write(' ');
		write(comment);
		write(' ');
		write(COMMENT_SUFFIX);
		writeEol();
	}

	//------------------------------------------------------------------

	public void writeSpaces(
		int	numSpaces)
		throws IOException
	{
		writeChars(' ', numSpaces);
	}

	//------------------------------------------------------------------

	public void write(
		char	ch)
		throws IOException
	{
		if (ch == '\n')
		{
			outBuffer.append(lineSeparator);
			outStream.append(outBuffer);
			outBuffer.setLength(0);
		}
		else
			outBuffer.append(ch);
	}

	//------------------------------------------------------------------

	public void write(
		CharSequence	charSeq)
		throws IOException
	{
		for (int i = 0; i < charSeq.length(); i++)
			write(charSeq.charAt(i));
	}

	//------------------------------------------------------------------

	public void writeEscaped(
		CharSequence	charSeq)
		throws IOException
	{
		write(XmlUtils.escape(charSeq));
	}

	//------------------------------------------------------------------

	public void writeQuoted(
		CharSequence	charSeq)
		throws IOException
	{
		write('"');
		write(charSeq);
		write('"');
	}

	//------------------------------------------------------------------

	public void writeEol()
		throws IOException
	{
		write('\n');
	}

	//------------------------------------------------------------------

	public void writeChars(
		char	ch,
		int		count)
		throws IOException
	{
		for (int i = 0; i < count; i++)
			write(ch);
	}

	//------------------------------------------------------------------

	public void writeElement(
		Element	element,
		int		indent)
		throws IOException
	{
		writeElement(element, indent, false);
	}

	//------------------------------------------------------------------

	public void writeElement(
		Element	element,
		int		indent,
		boolean	attrNewLine)
		throws IOException
	{
		// Create list of attributes
		List<Attribute> attributes = createAttributeList(element);

		// Write element
		Set<Short> nodeTypes = XmlUtils.getChildNodeTypes(element);
		boolean hasChildElements = nodeTypes.contains(Node.ELEMENT_NODE);
		if (hasChildElements || nodeTypes.contains(Node.TEXT_NODE))
		{
			// Write element start tag
			writeElementStart(element.getTagName(), attributes, indent, hasChildElements, attrNewLine);

			// Write child elements
			NodeList nodes = element.getChildNodes();
			for (int i = 0; i < nodes.getLength(); i++)
			{
				Node node = nodes.item(i);
				switch (node.getNodeType())
				{
					case Node.ELEMENT_NODE:
						writeElement((Element)node, indent + INDENT_INCREMENT, attrNewLine);
						break;

					case Node.TEXT_NODE:
						write(node.getNodeValue());
						break;
				}
			}

			// Write element end tag
			writeElementEnd(element.getTagName(), hasChildElements ? indent : 0);
		}
		else
			writeEmptyElement(element.getTagName(), attributes, indent, attrNewLine);
	}

	//------------------------------------------------------------------

	private void init(
		Writer	writer)
	{
		outStream = writer;
		outBuffer = new StringBuilder(256);
		lineSeparator = DEFAULT_LINE_SEPARATOR;
	}

	//------------------------------------------------------------------

	private void writeAttributes(
		Iterable<Attribute>	attributes,
		int					indent)
		throws IOException
	{
		for (Attribute attr : attributes)
		{
			if (indent > 0)
			{
				writeEol();
				writeSpaces(indent);
			}
			write(' ');
			write(attr.getName());
			write('=');
			writeQuoted(attr.getValue());
		}
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
