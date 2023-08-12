/*====================================================================*\

XmlConstants.java

Interface: XML constants

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.xml;

//----------------------------------------------------------------------


// INTERFACE: XML CONSTANTS


public interface XmlConstants
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The prefix of an entity */
	String	ENTITY_PREFIX	= "&";

	/** The suffix of an entity */
	String	ENTITY_SUFFIX	= ";";

	/**
	 * The names of predefined entities
	 */
	interface EntityName
	{
		String	AMP		= "amp";
		String	APOS	= "apos";
		String	GT		= "gt";
		String	LT		= "lt";
		String	QUOT	= "quot";
	}

	/**
	 * Predefined entities
	 */
	interface Entity
	{
		String	AMP		= ENTITY_PREFIX + EntityName.AMP  + ENTITY_SUFFIX;
		String	APOS	= ENTITY_PREFIX + EntityName.APOS + ENTITY_SUFFIX;
		String	GT		= ENTITY_PREFIX + EntityName.GT   + ENTITY_SUFFIX;
		String	LT		= ENTITY_PREFIX + EntityName.LT   + ENTITY_SUFFIX;
		String	QUOT	= ENTITY_PREFIX + EntityName.QUOT + ENTITY_SUFFIX;
	}

	/** The path-separator character */
	char	PATH_SEPARATOR_CHAR	= '/';

	/** The path separator */
	String	PATH_SEPARATOR		= Character.toString(PATH_SEPARATOR_CHAR);

	/** The attribute-prefix character */
	char	ATTRIBUTE_PREFIX_CHAR	= '@';

	/** The attribute prefix */
	String	ATTRIBUTE_PREFIX		= Character.toString(ATTRIBUTE_PREFIX_CHAR);

	/** The name of the UTF-8 character encoding */
	String	ENCODING_NAME_UTF8	= "UTF-8";
}

//----------------------------------------------------------------------
