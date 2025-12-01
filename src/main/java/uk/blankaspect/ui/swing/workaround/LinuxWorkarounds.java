/*====================================================================*\

LinuxWorkarounds.java

Class: workarounds for bugs that have been observed on Linux platforms.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.swing.workaround;

//----------------------------------------------------------------------


// IMPORTS


import java.awt.Point;
import java.awt.Window;

import javax.swing.Timer;

import uk.blankaspect.common.os.OsUtils;

//----------------------------------------------------------------------


// CLASS: WORKAROUNDS FOR BUGS THAT HAVE BEEN OBSERVED ON LINUX PLATFORMS


public class LinuxWorkarounds
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	private static final	int	FIX_WINDOW_Y_COORD_DELAY	= 100;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private LinuxWorkarounds()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the location of the specified window to a location that is derived from the specified location and the
	 * current location of the window by adjusting the <i>y</i> coordinate before the location is set.
	 * <p>
	 * This is a workaround for a bug that has been observed on Linux/GNOME whereby, when the location of a window is
	 * set with {@link Window#setLocation(Point)} or  {@link Window#setLocation(int, int)}, the actual <i>y</i>
	 * coordinate is greater than the specifed value by the height of the title bar of the window.
	 * </p>
	 * <p>
	 * If the current <i>y</i> coordinate is greater than the <i>y</i> coordinate of the specified location, the
	 * difference is subtracted from the specified <i>y</i> coordinate before the location of the window is set.  (The
	 * specified <i>x</i> coordinate is not changed.)  If the current <i>y</i> coordinate is less than or equal to the
	 * <i>y</i> coordinate of the specified location, this method has no effect.
	 * </p>
	 *
	 * @param window
	 *          the window of interest.
	 * @param location
	 *          the desired location of the window, ignored if {@code null}.
	 */

	public static void fixWindowYCoord(
		Window	window,
		Point	location)
	{
		if ((location != null) && OsUtils.isUnixLike())
		{
			Timer timer = new Timer(FIX_WINDOW_Y_COORD_DELAY, event ->
			{
				if (window.isShowing())
				{
					int dy = window.getLocationOnScreen().y - location.y;
					if (dy > 0)
						window.setLocation(new Point(location.x, location.y - dy));
				}
			});
			timer.setRepeats(false);
			timer.start();
		}
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
