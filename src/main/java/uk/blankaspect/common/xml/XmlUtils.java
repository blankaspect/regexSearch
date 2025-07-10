/*====================================================================*\

XmlUtils.java

XML utility methods class.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.xml;

//----------------------------------------------------------------------


// IMPORTS


import java.io.ByteArrayInputStream;
import java.io.CharArrayReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.StringReader;

import java.net.URI;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import uk.blankaspect.common.collection.ArraySet;

import uk.blankaspect.common.exception.AppException;

import uk.blankaspect.common.string.StringUtils;

//----------------------------------------------------------------------


// XML UTILITY METHODS CLASS


public class XmlUtils
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	private static final	String	XML_DECLARATION_PREFIX	= "<?xml";

	private interface AttrName
	{
		String	XMLNS	= "xmlns";
	}

////////////////////////////////////////////////////////////////////////
//  Enumerated types
////////////////////////////////////////////////////////////////////////


	// ERROR IDENTIFIERS


	private enum ErrorId
		implements AppException.IId
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		FAILED_TO_INSTANTIATE_DOCUMENT_BUILDER_FACTORY
		("Failed to instantiate a DOM document builder factory."),

		FAILED_TO_CREATE_DOCUMENT_BUILDER
		("Failed to create a DOM document builder."),

		ERROR_PARSING_FILE
		("An error occurred when parsing the file.");

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

		public String getMessage()
		{
			return message;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	String	message;

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Member interfaces
////////////////////////////////////////////////////////////////////////


	// ELEMENT FILTER INTERFACE


	@FunctionalInterface
	public interface IElementFilter
	{

	////////////////////////////////////////////////////////////////////
	//  Methods
	////////////////////////////////////////////////////////////////////

		public boolean acceptElement(Element element);

		//--------------------------------------------------------------

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// PARSE EXCEPTION CLASS


	public static class ParseException
		extends AppException
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		private static final	String	LINE_STR	= "Line: ";
		private static final	String	COLUMN_STR	= "column: ";

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private ParseException(AppException.IId id,
							   SAXException     cause)
		{
			super(id, cause);
			if (cause instanceof SAXParseException)
			{
				SAXParseException e = (SAXParseException)cause;
				lineNum = e.getLineNumber();
				columnNum = e.getColumnNumber();
			}
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		protected String getPrefix()
		{
			if (lineNum > 0)
			{
				StringBuilder buffer = new StringBuilder(256);
				buffer.append(LINE_STR);
				buffer.append(lineNum);
				buffer.append(", ");
				buffer.append(COLUMN_STR);
				buffer.append(columnNum);
				buffer.append('\n');
				return buffer.toString();
			}
			return super.getPrefix();
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	int	lineNum;
		private	int	columnNum;

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	private XmlUtils()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static ErrorLogger getErrorHandler()
	{
		if (errorHandler == null)
			errorHandler = new ErrorLogger();
		return errorHandler;
	}

	//------------------------------------------------------------------

	public static String escape(char ch)
	{
		switch (ch)
		{
			case '<':
				return XmlConstants.Entity.LT;

			case '>':
				return XmlConstants.Entity.GT;

			case '\'':
				return XmlConstants.Entity.APOS;

			case '"':
				return XmlConstants.Entity.QUOT;

			case '&':
				return XmlConstants.Entity.AMP;

			default:
				return Character.toString(ch);
		}
	}

	//------------------------------------------------------------------

	public static String escape(CharSequence charSeq)
	{
		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < charSeq.length(); i++)
			buffer.append(escape(charSeq.charAt(i)));
		return buffer.toString();
	}

	//------------------------------------------------------------------

	public static String concatenatePath(String... strs)
	{
		StringBuilder buffer = new StringBuilder(256);
		for (int i = 0; i < strs.length; i++)
		{
			if (i > 0)
				buffer.append(XmlConstants.PATH_SEPARATOR_CHAR);
			buffer.append(strs[i]);
		}
		return buffer.toString();
	}

	//------------------------------------------------------------------

	public static String appendAttributeName(String path,
											 String attrName)
	{
		return (path + XmlConstants.ATTRIBUTE_PREFIX + attrName);
	}

	//------------------------------------------------------------------

	public static String getElementPath(Element element)
	{
		List<String> strs = new ArrayList<>();
		while (element != null)
		{
			strs.add(element.getNodeName());
			Node parent = element.getParentNode();
			element = (parent instanceof Element) ? (Element)parent : null;
		}

		StringBuilder buffer = new StringBuilder(strs.size() << 4);
		for (int i = strs.size() - 1; i >= 0; i--)
		{
			buffer.append(strs.get(i));
			if (i > 0)
				buffer.append(XmlConstants.PATH_SEPARATOR_CHAR);
		}
		return buffer.toString();
	}

	//------------------------------------------------------------------

	public static ArraySet<Short> getChildNodeTypes(Element element)
	{
		ArraySet<Short> nodeTypes = new ArraySet<>();
		NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++)
			nodeTypes.add(nodes.item(i).getNodeType());
		return nodeTypes;
	}

	//------------------------------------------------------------------

	public static boolean hasChildren(Element element,
									  short   nodeType)
	{
		return hasChildren(element, Collections.singleton(nodeType));
	}

	//------------------------------------------------------------------

	public static boolean hasChildren(Element           element,
									  Collection<Short> nodeTypes)
	{
		if (element.hasChildNodes())
		{
			NodeList nodes = element.getChildNodes();
			for (int i = 0; i < nodes.getLength(); i++)
			{
				if (nodeTypes.contains(nodes.item(i).getNodeType()))
					return true;
			}
		}
		return false;
	}

	//------------------------------------------------------------------

	public static boolean isXml(File file)
		throws FileNotFoundException, IOException, SecurityException
	{
		if (file.length() < XML_DECLARATION_PREFIX.length())
			return false;

		byte[] buffer = new byte[XML_DECLARATION_PREFIX.length()];
		RandomAccessFile raFile = new RandomAccessFile(file, "r");
		raFile.readFully(buffer);
		raFile.close();
		return XML_DECLARATION_PREFIX.equals(new String(buffer, XmlConstants.ENCODING_NAME_UTF8));
	}

	//------------------------------------------------------------------

	public static DocumentBuilder createDocumentBuilder(boolean validate)
		throws AppException
	{
		try
		{
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newNSInstance();
			docBuilderFactory.setValidating(validate);
			docBuilderFactory.setXIncludeAware(true);
			return docBuilderFactory.newDocumentBuilder();
		}
		catch (FactoryConfigurationError e)
		{
			throw new AppException(ErrorId.FAILED_TO_INSTANTIATE_DOCUMENT_BUILDER_FACTORY, e);
		}
		catch (ParserConfigurationException e)
		{
			throw new AppException(ErrorId.FAILED_TO_CREATE_DOCUMENT_BUILDER, e);
		}
	}

	//------------------------------------------------------------------

	public static Document createDocument()
		throws AppException
	{
		return createDocumentBuilder(false).newDocument();
	}

	//------------------------------------------------------------------

	public static Document createDocument(byte[] data)
		throws AppException
	{
		return createDocument(data, null, false);
	}

	//------------------------------------------------------------------

	public static Document createDocument(byte[]  data,
										  URI     baseUri,
										  boolean validate)
		throws AppException
	{
		InputSource inputSource = new InputSource(new ByteArrayInputStream(data));
		inputSource.setEncoding(XmlConstants.ENCODING_NAME_UTF8);
		return createDocument(inputSource, baseUri, validate);
	}

	//------------------------------------------------------------------

	public static Document createDocument(char[] data)
		throws AppException
	{
		return createDocument(data, null, false);
	}

	//------------------------------------------------------------------

	public static Document createDocument(char[]  data,
										  URI     baseUri,
										  boolean validate)
		throws AppException
	{
		return createDocument(new InputSource(new CharArrayReader(data)), baseUri, validate);
	}

	//------------------------------------------------------------------

	public static Document createDocument(String data)
		throws AppException
	{
		return createDocument(data, null, false);
	}

	//------------------------------------------------------------------

	public static Document createDocument(String  data,
										  URI     baseUri,
										  boolean validate)
		throws AppException
	{
		return createDocument(new InputSource(new StringReader(data)), baseUri, validate);
	}

	//------------------------------------------------------------------

	public static Document createDocument(InputStream inStream)
		throws AppException
	{
		return createDocument(inStream, null, false);
	}

	//------------------------------------------------------------------

	public static Document createDocument(InputStream inStream,
										  URI         baseUri,
										  boolean     validate)
		throws AppException
	{
		return createDocument(new InputSource(inStream), baseUri, validate);
	}

	//------------------------------------------------------------------

	public static Document createDocument(InputSource inputSource,
										  URI         baseUri,
										  boolean     validate)
		throws AppException
	{
		try
		{
			if (baseUri != null)
				inputSource.setSystemId(baseUri.toString());
			DocumentBuilder documentBuilder = createDocumentBuilder(validate);
			getErrorHandler().clear();
			documentBuilder.setErrorHandler(getErrorHandler());
			return documentBuilder.parse(inputSource);
		}
		catch (SAXException e)
		{
			throw new ParseException(ErrorId.ERROR_PARSING_FILE, e);
		}
		catch (IOException e)
		{
			throw new AppException(ErrorId.ERROR_PARSING_FILE, e);
		}
	}

	//------------------------------------------------------------------

	public static String getAttribute(Element element,
									  String  attributeName)
	{
		return (element.hasAttribute(attributeName) ? element.getAttribute(attributeName) : null);
	}

	//------------------------------------------------------------------

	public static String getAttribute(Element element,
									  String  namespaceName,
									  String  attributeName)
	{
		return (element.hasAttributeNS(namespaceName, attributeName)
													? element.getAttributeNS(namespaceName, attributeName)
													: null);
	}

	//------------------------------------------------------------------

	public static List<Element> getChildElements(Element element)
	{
		return getChildElements(element, e -> true, false);
	}

	//------------------------------------------------------------------

	public static List<Element> getChildElements(Element      element,
												 final String name)
	{
		return getChildElements(element, name, false);
	}

	//------------------------------------------------------------------

	public static List<Element> getChildElements(Element      element,
												 final String name,
												 boolean      recursive)
	{
		return getChildElements(element, e -> e.getTagName().equals(name), recursive);
	}

	//------------------------------------------------------------------

	public static List<Element> getChildElements(Element        element,
												 IElementFilter filter)
	{
		return getChildElements(element, filter, false);
	}

	//------------------------------------------------------------------

	public static List<Element> getChildElements(Element        element,
												 IElementFilter filter,
												 boolean        recursive)
	{
		List<Element> elements = new ArrayList<>();
		appendElements(elements, element, filter, recursive);
		return elements;
	}

	//------------------------------------------------------------------

	public static Element findElement(Document document,
									  String   elementPath)
	{
		return findElement(document.getDocumentElement(), elementPath);
	}

	//------------------------------------------------------------------

	public static Element findElement(Element element,
									  String  elementPath)
	{
		List<String> pathComponents = StringUtils.split(StringUtils.getPrefixLast(elementPath,
																				  XmlConstants.ATTRIBUTE_PREFIX_CHAR),
														XmlConstants.PATH_SEPARATOR_CHAR);

		Node node = element;
		int pathIndex = 0;
		while (pathIndex < pathComponents.size())
		{
			String elementName = pathComponents.get(pathIndex);
			NodeList nodes = node.getChildNodes();
			int nodeIndex = 0;
			while (nodeIndex < nodes.getLength())
			{
				node = nodes.item(nodeIndex);
				if ((node.getNodeType() == Node.ELEMENT_NODE) && node.getNodeName().equals(elementName))
					break;
				++nodeIndex;
			}
			if (nodeIndex >= nodes.getLength())
				break;
			++pathIndex;
		}

		return ((pathIndex < pathComponents.size()) ? null : (Element)node);
	}

	//------------------------------------------------------------------

	public static void setNamespaceAttribute(Element element)
	{
		// Set namespace declaration attribute on this element
		String namespaceName = element.getNamespaceURI();
		if (namespaceName != null)
		{
			Node parent = element.getParentNode();
			if ((parent == null) || !namespaceName.equals(parent.getNamespaceURI()))
			{
				String prefix = element.getPrefix();
				String attrName = (prefix == null) ? AttrName.XMLNS : AttrName.XMLNS + ":" + prefix;
				element.setAttribute(attrName, namespaceName);
			}
		}

		// Set namespace declaration attribute on child elements
		NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++)
		{
			Node node = nodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE)
				setNamespaceAttribute((Element)node);
		}
	}

	//------------------------------------------------------------------

	private static void appendElements(List<Element>  elements,
									   Element        element,
									   IElementFilter filter,
									   boolean        recursive)
	{
		// Add filtered child elements to list
		NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++)
		{
			Node node = nodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE)
			{
				Element childElement = (Element)node;
				if (filter.acceptElement(childElement))
					elements.add(childElement);
			}
		}

		// Recursively apply filter to descendant elements
		if (recursive)
		{
			for (int i = 0; i < nodes.getLength(); i++)
			{
				Node node = nodes.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE)
					appendElements(elements, (Element)node, filter, true);
			}
		}
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class variables
////////////////////////////////////////////////////////////////////////

	private static	ErrorLogger	errorHandler;

}

//----------------------------------------------------------------------
