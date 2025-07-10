/*====================================================================*\

MaxValueMap.java

Class: maximum-value map.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.misc;

//----------------------------------------------------------------------


// IMPORTS


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//----------------------------------------------------------------------


// CLASS: MAXIMUM-VALUE MAP


public class MaxValueMap
{

////////////////////////////////////////////////////////////////////////
//  Class variables
////////////////////////////////////////////////////////////////////////

	private static	Map<String, List<IEntry>>	map	= new HashMap<>();

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	private MaxValueMap()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static List<IEntry> getEntries(
		String	key)
	{
		return map.get(key);
	}

	//------------------------------------------------------------------

	public static void removeAll(
		String	key)
	{
		map.remove(key);
	}

	//------------------------------------------------------------------

	public static void add(
		String	key,
		IEntry	entry)
	{
		if (key != null)
		{
			List<IEntry> entries = map.get(key);
			if (entries == null)
			{
				entries = new ArrayList<>();
				map.put(key, entries);
			}
			entries.add(entry);
		}
	}

	//------------------------------------------------------------------

	public static void update(
		String	key)
	{
		List<IEntry> entries = map.get(key);
		if (entries != null)
		{
			int maxValue = 0;
			for (IEntry entry : entries)
			{
				int value = entry.getValue();
				if (maxValue < value)
					maxValue = value;
			}

			for (IEntry entry : entries)
				entry.setValue(maxValue);
		}
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Member interfaces
////////////////////////////////////////////////////////////////////////


	// INTERFACE: MAP ENTRY


	public interface IEntry
	{

	////////////////////////////////////////////////////////////////////
	//  Methods
	////////////////////////////////////////////////////////////////////

		int getValue();

		//--------------------------------------------------------------

		void setValue(
			int	value);

		//--------------------------------------------------------------

	}

	//==================================================================

}

//----------------------------------------------------------------------
