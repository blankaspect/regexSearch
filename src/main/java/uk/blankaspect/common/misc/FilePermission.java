/*====================================================================*\

FilePermission.java

File permission interface.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.misc;

//----------------------------------------------------------------------


// FILE PERMISSION INTERFACE


interface FilePermission
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	int	OTHER_EXECUTE	= 1 << 0;
	int	OTHER_WRITE		= 1 << 1;
	int	OTHER_READ		= 1 << 2;
	int	GROUP_EXECUTE	= 1 << 3;
	int	GROUP_WRITE		= 1 << 4;
	int	GROUP_READ		= 1 << 5;
	int	USER_EXECUTE	= 1 << 6;
	int	USER_WRITE		= 1 << 7;
	int	USER_READ		= 1 << 8;

	int	PERMISSIONS_PER_SET	= 3;
	int	NUM_SETS			= 3;
	int	NUM_PERMISSIONS		= NUM_SETS * PERMISSIONS_PER_SET;

}

//----------------------------------------------------------------------
