/*====================================================================*\

ResourceProperties.java

Class: resource properties.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.resource;

//----------------------------------------------------------------------


// IMPORTS


import java.io.InputStream;
import java.io.IOException;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import uk.blankaspect.common.exception2.ExceptionUtils;
import uk.blankaspect.common.exception2.LocationException;

//----------------------------------------------------------------------


// CLASS: RESOURCE PROPERTIES


/**
 * This class implements a set of properties that are loaded from a resource.
 */

public class ResourceProperties
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** Error messages. */
	private interface ErrorMsg
	{
		String	ERROR_READING_PROPERTIES =
				"An error occurred when reading the properties.";
	}

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** A map of properties. */
	private	Map<String, String>	map;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of a set of properties from the resource file at the specified location.
	 *
	 * @param  pathname
	 *           the pathname of the resource file.
	 * @throws LocationException
	 *           if an error occurs when reading the properties from the resource.
	 */

	public ResourceProperties(String pathname)
		throws LocationException
	{
		// Initialise instance variables
		map = new HashMap<>();

		// Open input stream on resource
		InputStream inStream = getClass().getResourceAsStream(pathname);
		if (inStream != null)
		{
			try
			{
				// Read properties from stream
				Properties properties = new Properties();
				properties.load(inStream);

				// Add properties to map
				for (String key : properties.stringPropertyNames())
					map.put(key, properties.getProperty(key));
			}
			catch (IOException e)
			{
				throw new LocationException(ErrorMsg.ERROR_READING_PROPERTIES, e, pathname);
			}
			finally
			{
				// Close input stream
				try
				{
					inStream.close();
				}
				catch (IOException e)
				{
					ExceptionUtils.printStderrLocated(e);
				}
			}
		}
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the value of the property that is associated with the specified key.
	 * @param  key
	 *           the key whose associated value is required.
	 * @return the value of the property that is associated {@code key}.
	 */

	public String get(String key)
	{
		return get(key, null);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the value of the property that is associated with the specified key, or the specified default value if no
	 * property is associated with the specified key.
	 *
	 * @param  key
	 *           the key whose associated value is required.
	 * @param  defaultValue
	 *           the key that will be returned if no property is associated with {@code key}.
	 * @return the value of the property that is associated with {@code key}.
	 */

	public String get(String key,
					  String defaultValue)
	{
		String value = map.get(key);
		return (value == null) ? defaultValue : value;
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
