/*====================================================================*\

Attribute.java

XML attribute class.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.xml;

//----------------------------------------------------------------------


// IMPORTS


import java.text.NumberFormat;

import org.w3c.dom.Element;

//----------------------------------------------------------------------


// XML ATTRIBUTE CLASS


public class Attribute
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public Attribute(String  name,
					 boolean value)
	{
		this(name, Boolean.toString(value));
	}

	//------------------------------------------------------------------

	public Attribute(String name,
					 int    value)
	{
		this(name, Integer.toString(value));
	}

	//------------------------------------------------------------------

	public Attribute(String name,
					 long   value)
	{
		this(name, Long.toString(value));
	}

	//------------------------------------------------------------------

	public Attribute(String name,
					 double value)
	{
		this(name, Double.toString(value));
	}

	//------------------------------------------------------------------

	public Attribute(String       name,
					 double       value,
					 NumberFormat format)
	{
		this(name, format.format(value));
	}

	//------------------------------------------------------------------

	public Attribute(String name,
					 Object value)
	{
		this(name, value.toString());
	}

	//------------------------------------------------------------------

	public Attribute(String  name,
					 Object  value,
					 boolean escape)
	{
		this(name, value.toString(), escape);
	}

	//------------------------------------------------------------------

	public Attribute(String name,
					 String value)
	{
		this.name = name;
		this.value = value;
	}

	//------------------------------------------------------------------

	public Attribute(String  name,
					 String  value,
					 boolean escape)
	{
		this(name, escape ? XmlUtils.escape(value) : value);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public String getName()
	{
		return name;
	}

	//------------------------------------------------------------------

	public String getValue()
	{
		return value;
	}

	//------------------------------------------------------------------

	public void set(Element element)
	{
		element.setAttribute(name, value);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	String	name;
	private	String	value;

}

//----------------------------------------------------------------------
