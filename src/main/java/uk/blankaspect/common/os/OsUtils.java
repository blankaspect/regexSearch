/*====================================================================*\

OsUtils.java

Class: utility methods that relate to operating systems.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.os;

//----------------------------------------------------------------------


// IMPORTS


import java.io.File;

//----------------------------------------------------------------------


// CLASS: UTILITY METHODS THAT RELATE TO OPERATING SYSTEMS


/**
 * This class contains utility methods that relate to operating systems.
 */

public class OsUtils
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private OsUtils()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns {@code true} if the operating system is UNIX-like.  The method returns {@code true} if {@linkplain
	 * File#separatorChar File.separatorChar} is '/' (U+002F).
	 *
	 * @return {@code true} if the operating system is UNIX-like based on {@code File.separatorChar}.
	 */

	public static boolean isUnixLike()
	{
		return File.separatorChar == '/';
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if the operating system is likely to be Windows.  The method returns {@code true} if
	 * {@linkplain File#separatorChar File.separatorChar} is '\' (U+005C).
	 *
	 * @return {@code true} if the operating system is likely to be Windows based on {@code File.separatorChar}.
	 */

	public static boolean isWindows()
	{
		return File.separatorChar == '\\';
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
