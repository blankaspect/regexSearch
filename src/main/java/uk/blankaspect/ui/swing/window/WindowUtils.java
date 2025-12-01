/*====================================================================*\

WindowUtils.java

Class: window-related utility methods.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.swing.window;

//----------------------------------------------------------------------


// IMPORTS


import java.awt.Component;

import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;

import javax.swing.SwingUtilities;

//----------------------------------------------------------------------


// CLASS: WINDOW-RELATED UTILITY METHODS


public class WindowUtils
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	private WindowUtils()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static void addRunOnAddedToWindow(
		Component	component,
		Runnable	runnable)
	{
		component.addHierarchyListener(new HierarchyListener()
		{
			boolean	hasRun;

			@Override
			public void hierarchyChanged(
				HierarchyEvent	event)
			{
				if (!hasRun && ((event.getChangeFlags() & HierarchyEvent.PARENT_CHANGED) != 0)
						&& (SwingUtilities.getWindowAncestor(component) != null))
				{
					runnable.run();
					hasRun = true;
				}
			}
		});
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
