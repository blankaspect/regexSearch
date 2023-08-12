/*====================================================================*

Images.java

Class: images.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.regexsearch;

//----------------------------------------------------------------------


// IMPORTS


import java.awt.Image;

import java.util.Arrays;
import java.util.List;

import uk.blankaspect.ui.swing.image.ImageCache;

//----------------------------------------------------------------------


// CLASS: IMAGES


public class Images
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** A list of images for the icons that represent the application. */
	public static final		List<Image>	APP_ICON_IMAGES;

	/** The directory that contains image files. */
	private static final	String	DIRECTORY	= "images/";

	/** Filename stems of images that are used in this class. */
	private interface ImageFilename
	{
		String	APP_16	= "app-16x16";
		String	APP_32	= "app-32x32";
		String	APP_48	= "app-48x48";
		String	APP_256	= "app-256x256";
	}

////////////////////////////////////////////////////////////////////////
//  Static initialiser
////////////////////////////////////////////////////////////////////////

	static
	{
		ImageCache.addDirectory(Images.class, DIRECTORY);

		APP_ICON_IMAGES = Arrays.asList
		(
			ImageCache.getImage(ImageFilename.APP_16),
			ImageCache.getImage(ImageFilename.APP_32),
			ImageCache.getImage(ImageFilename.APP_48),
			ImageCache.getImage(ImageFilename.APP_256)
		);
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	private Images()
	{
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
