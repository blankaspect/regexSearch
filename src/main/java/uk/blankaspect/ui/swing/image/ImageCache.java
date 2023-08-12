/*====================================================================*

ImageCache.java

Class: image cache.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.swing.image;

//----------------------------------------------------------------------


// IMPORTS


import java.awt.Image;

import java.awt.image.BufferedImage;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;

import java.util.HashMap;
import java.util.LinkedList;

import javax.imageio.ImageIO;

import javax.swing.ImageIcon;

import uk.blankaspect.common.exception2.ExceptionUtils;

import uk.blankaspect.common.resource.ResourceUtils;

import uk.blankaspect.common.thread.ThreadUtils;

//----------------------------------------------------------------------


// CLASS: IMAGE CACHE


/**
 * This class implements a means of loading named images from the resources of registered classes, and caching those
 * images so that they are loaded only once.
 */

public class ImageCache
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The default image. */
	public static final		BufferedImage	DEFAULT_IMAGE;

	/** The directory that contains image files. */
	private static final	String	DIRECTORY	= "../images/";

	/** The default filename extension of an image file. */
	private static final	String	DEFAULT_FILENAME_EXTENSION	= ".png";

	/** Miscellaneous strings. */
	private static final	String	FAILED_TO_FIND_IMAGE_STR	= "Failed to find the image '%s'; "
																	+ "substituting the default 16x16 image.";

	/** The width and height of the default image. */
	private static final	int		DEFAULT_IMAGE_SIZE	= 16;

////////////////////////////////////////////////////////////////////////
//  Class variables
////////////////////////////////////////////////////////////////////////

	/** A list of pathnames of directories that contain images. */
	private static	LinkedList<String>		directories	= new LinkedList<>();

	/** The cache of images in which an image is associated with the name of the file from which it was loaded. */
	private static	HashMap<String, Image>	images		= new HashMap<>();

////////////////////////////////////////////////////////////////////////
//  Static initialiser
////////////////////////////////////////////////////////////////////////

	static
	{
		// Add the image directory of this class to the list
		addDirectory(ImageCache.class, DIRECTORY);

		// Initialise the default image
		BufferedImage image = null;
		try
		{
			image = ImageIO.read(new ByteArrayInputStream(ImageData.DEFAULT_IMAGE));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		DEFAULT_IMAGE = (image == null)
								? new BufferedImage(DEFAULT_IMAGE_SIZE, DEFAULT_IMAGE_SIZE, BufferedImage.TYPE_INT_ARGB)
								: image;
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private ImageCache()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@linkplain Image image} for the file with the specified filename.  If the filename does not have an
	 * extension (ie, it is a filename stem), the default extension ({@code .png}) will be appended to the filename.
	 * <p>
	 * If the named image was returned by a previous call to this method, its cached copy will be returned by subsequent
	 * calls; otherwise, the directories that have been registered with {@link #addDirectory(Class, String)} are
	 * searched for the first occurrence of the named file in the reverse order of registration (ie, the last directory
	 * to be added is searched first).  If the named file is found in any of the directories, the associated class
	 * loader attempts to load it and to create an image from it; if successful, the image is added to the cache.
	 * </p>
	 * <p>
	 * If an image with the specified filename is not found in the cache or in any of the directories, a default image
	 * is returned.
	 * </p>
	 *
	 * @param  filename
	 *           the filename or filename stem of the required image file.
	 * @return the image that corresponds to the specified filename, or a default image if the specified image was not
	 *         found by the class loader.
	 * @see    #getImageIcon(String)
	 */

	public static Image getImage(
		String	filename)
	{
		return getImage(filename, true);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the {@linkplain Image image} for the file with the specified filename.  If the filename does not have an
	 * extension (ie, it is a filename stem), the default extension ({@code .png}) will be appended to the filename.
	 * <p>
	 * If the named image was returned by a previous call to this method, its cached copy will be returned by subsequent
	 * calls; otherwise, the directories that have been registered with {@link #addDirectory(Class, String)} are
	 * searched for the first occurrence of the named file in the reverse order of registration (ie, the last directory
	 * to be added is searched first).  If the named file is found in any of the directories, the associated class
	 * loader attempts to load it and to create an image from it; if successful, the image is added to the cache.
	 * </p>
	 * <p>
	 * If an image with the specified filename is not found in the cache or in any of the directories, either a default
	 * image or {@code null} is returned, according to the specified flag.
	 * </p>
	 *
	 * @param  filename
	 *           the filename or filename stem of the required image file.
	 * @param  defaultIfNotFound
	 *           if {@code true}, a default image will be returned if the specified image was not found; otherwise,
	 *           {@code null} will be returned if the specified image was not found.
	 * @return the image that corresponds to the specified filename, or, if the specified image was not found, either a
	 *         default image or {@code null}, according to the <i>defaultIfNotFound</i> flag.
	 * @see    #getImageIcon(String, boolean)
	 */

	public static Image getImage(
		String	filename,
		boolean	defaultIfNotFound)
	{
		// Append default filename extension to a filename that doesn't have an extension
		if (filename.indexOf('.') < 0)
			filename += DEFAULT_FILENAME_EXTENSION;

		// Get image from cache
		Image image = images.get(filename);

		// If image is not in cache, try to load image from resource file
		if (image == null)
		{
			for (String directory : directories)
			{
				// Get pathname of image file
				String pathname = directory;
				pathname += pathname.endsWith("/") ? filename : "/" + filename;

				// Open input stream on resource
				InputStream inStream = ImageCache.class.getResourceAsStream(pathname);

				// If resource was found, create image from it and add image to cache
				if (inStream != null)
				{
					// Create image
					try
					{
						image = ImageIO.read(inStream);
					}
					catch (IOException e)
					{
						ExceptionUtils.printStderrLocated(e);
					}

					// Add image to cache
					if (image != null)
						images.put(filename, image);

					// Close input stream
					try
					{
						inStream.close();
					}
					catch (IOException e)
					{
						ExceptionUtils.printStderrLocated(e);
					}
					break;
				}
			}
		}

		// If image is not in cache and its resource was not found, substitute default image
		if ((image == null) && defaultIfNotFound)
		{
			// Use default image
			image = DEFAULT_IMAGE;

			// Report failure
			System.err.println(String.format(FAILED_TO_FIND_IMAGE_STR, filename));
			System.err.println(ThreadUtils.getStackTraceString(null, 1, 5));
		}

		// Return image
		return image;
	}

	//------------------------------------------------------------------

	/**
	 * Returns a new instance of an {@link ImageIcon} containing the {@linkplain Image image} whose file has the
	 * specified filename.  The image is loaded with {@link #getImage(String)}.  If the filename does not have an
	 * extension (ie, it is a filename stem), the default extension ({@code .png}) will be appended to the filename.
	 *
	 * @param  filename
	 *           the filename or filename stem of the required image file.
	 * @return a new instance of an {@link ImageIcon} containing the image that corresponds to <i>filename</i>, or a
	 *         default image if the specified image was not found by the class loader.
	 * @see    #getImage(String)
	 */

	public static ImageIcon getImageIcon(
		String	filename)
	{
		return getImageIcon(filename, true);
	}

	//------------------------------------------------------------------

	/**
	 * Returns a new instance of an {@link ImageIcon} containing the {@linkplain Image image} whose file has the
	 * specified filename.  The image is loaded with {@link #getImage(String)}.  If the filename does not have an
	 * extension (ie, it is a filename stem), the default extension ({@code .png}) will be appended to the filename.
	 *
	 * @param  filename
	 *           the filename or filename stem of the required image file.
	 * @param  defaultIfNotFound
	 *           if {@code true}, an image view containing a default image will be returned if the specified image was
	 *           not found; otherwise, {@code null} will be returned if the specified image was not found.
	 * @return a new instance of an {@link ImageIcon} containing the image that corresponds to <i>filename</i>, or, if
	 *         the specified image was not found, either a default image or {@code null}, according to the
	 *         <i>defaultIfNotFound</i> flag.
	 * @see    #getImage(String, boolean)
	 */

	public static ImageIcon getImageIcon(
		String	filename,
		boolean	defaultIfNotFound)
	{
		Image image = getImage(filename, defaultIfNotFound);
		return (image == null) ? null : new ImageIcon(image);
	}

	//------------------------------------------------------------------

	/**
	 * Registers the specified class and the pathname of the directory that contains images for the class.  The
	 * directory will be included in the search of uncached images that is performed by {@link #getImage(String)}.
	 * Directories are searched in reverse order of registration (ie, the last directory to be added by this method is
	 * searched first).
	 *
	 * @param cls
	 *          the class whose images are located in the directory denoted by {@code pathname}.
	 * @param pathname
	 *          the pathname of the directory that contains images for {@code cls}.
	 */

	public static void addDirectory(
		Class<?>	cls,
		String		pathname)
	{
		directories.addFirst(ResourceUtils.normalisedPathname(cls, pathname));
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Image data
////////////////////////////////////////////////////////////////////////

	/** PNG image data. */
	private interface ImageData
	{
		byte[]	DEFAULT_IMAGE	=
		{
			(byte)0x89, (byte)0x50, (byte)0x4E, (byte)0x47, (byte)0x0D, (byte)0x0A, (byte)0x1A, (byte)0x0A,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x0D, (byte)0x49, (byte)0x48, (byte)0x44, (byte)0x52,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x10, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x10,
			(byte)0x08, (byte)0x06, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x1F, (byte)0xF3, (byte)0xFF,
			(byte)0x61, (byte)0x00, (byte)0x00, (byte)0x01, (byte)0x4F, (byte)0x49, (byte)0x44, (byte)0x41,
			(byte)0x54, (byte)0x78, (byte)0x5E, (byte)0x95, (byte)0x93, (byte)0x4B, (byte)0x4B, (byte)0xC3,
			(byte)0x50, (byte)0x10, (byte)0x85, (byte)0x2F, (byte)0x54, (byte)0xB7, (byte)0xDA, (byte)0xFE,
			(byte)0x02, (byte)0xAB, (byte)0x2B, (byte)0x37, (byte)0xE2, (byte)0xD6, (byte)0x5F, (byte)0x25,
			(byte)0xB5, (byte)0xDD, (byte)0xD8, (byte)0xEE, (byte)0xA4, (byte)0x52, (byte)0xB0, (byte)0x9B,
			(byte)0xBA, (byte)0x12, (byte)0x71, (byte)0xEB, (byte)0xDE, (byte)0x8D, (byte)0x0F, (byte)0x6C,
			(byte)0xA5, (byte)0xA0, (byte)0x68, (byte)0x7D, (byte)0xAE, (byte)0x04, (byte)0x23, (byte)0x3E,
			(byte)0xB0, (byte)0xC6, (byte)0x8B, (byte)0xB1, (byte)0xA8, (byte)0x28, (byte)0x69, (byte)0x5A,
			(byte)0xD2, (byte)0xA4, (byte)0x79, (byte)0x8C, (byte)0x99, (byte)0x59, (byte)0xC4, (byte)0xE6,
			(byte)0x9A, (byte)0x90, (byte)0x7A, (byte)0x60, (byte)0x36, (byte)0x73, (byte)0xCE, (byte)0x7C,
			(byte)0x19, (byte)0xC8, (byte)0x1D, (byte)0xF6, (byte)0x9C, (byte)0x61, (byte)0x69, (byte)0x5E,
			(byte)0x48, (byte)0x5E, (byte)0xBC, (byte)0x2C, (byte)0x24, (byte)0x6C, (byte)0x39, (byte)0xC3,
			(byte)0x60, (byte)0x98, (byte)0xC2, (byte)0x2C, (byte)0xCF, (byte)0xA7, (byte)0xCE, (byte)0x9B,
			(byte)0x39, (byte)0x36, (byte)0xC1, (byte)0x78, (byte)0x3E, (byte)0x79, (byte)0xA9, (byte)0x1E,
			(byte)0x94, (byte)0x1C, (byte)0xD7, (byte)0x36, (byte)0x61, (byte)0x58, (byte)0x61, (byte)0x56,
			(byte)0xAD, (byte)0x2D, (byte)0x3B, (byte)0x08, (byte)0x61, (byte)0x48, (byte)0x0B, (byte)0x1B,
			(byte)0xEE, (byte)0xB7, (byte)0x24, (byte)0xD0, (byte)0x4E, (byte)0xD6, (byte)0x40, (byte)0x6B,
			(byte)0xAC, (byte)0x43, (byte)0xFF, (byte)0xE3, (byte)0x5E, (byte)0xB4, (byte)0xC1, (byte)0xB5,
			(byte)0x0C, (byte)0xDA, (byte)0x84, (byte)0xE1, (byte)0x4A, (byte)0xA2, (byte)0xD4, (byte)0xFD,
			(byte)0x25, (byte)0x90, (byte)0xB3, (byte)0x89, (byte)0xDF, (byte)0x95, (byte)0x73, (byte)0xA3,
			(byte)0xA0, (byte)0x1D, (byte)0xAD, (byte)0x8A, (byte)0x31, (byte)0xF2, (byte)0xFE, (byte)0x00,
			(byte)0x8C, (byte)0x66, (byte)0x83, (byte)0x8C, (byte)0x56, (byte)0x65, (byte)0x0E, (byte)0x7A,
			(byte)0x0F, (byte)0x75, (byte)0xD0, (byte)0xEF, (byte)0xAA, (byte)0xF0, (byte)0x56, (byte)0x9E,
			(byte)0x25, (byte)0xA0, (byte)0xA9, (byte)0x5C, (byte)0x07, (byte)0xB2, (byte)0xA1, (byte)0x00,
			(byte)0x5C, (byte)0x5B, (byte)0xCE, (byte)0x8E, (byte)0x80, (byte)0xF5, (byte)0xF9, (byte)0xE8,
			(byte)0xF7, (byte)0x8C, (byte)0xA7, (byte)0x63, (byte)0x0A, (byte)0xB7, (byte)0x0F, (byte)0x2B,
			(byte)0x03, (byte)0xC9, (byte)0x08, (byte)0x00, (byte)0xB8, (byte)0x2E, (byte)0xD8, (byte)0x9D,
			(byte)0xF7, (byte)0x40, (byte)0x0B, (byte)0xD7, (byte)0xC7, (byte)0x5C, (byte)0xF7, (byte)0x6A,
			(byte)0x33, (byte)0xD0, (byte)0x0F, (byte)0x07, (byte)0x0C, (byte)0xCA, (byte)0xB1, (byte)0xA1,
			(byte)0x5D, (byte)0x2F, (byte)0xD3, (byte)0xFA, (byte)0xAF, (byte)0xC5, (byte)0x29, (byte)0x70,
			(byte)0xCD, (byte)0x6E, (byte)0xC0, (byte)0x8E, (byte)0x05, (byte)0x7C, (byte)0x6D, (byte)0xCD,
			(byte)0x53, (byte)0x48, (byte)0x29, (byte)0x4D, (byte)0x83, (byte)0xF5, (byte)0x2D, (byte)0x8B,
			(byte)0x76, (byte)0x3C, (byte)0x40, (byte)0x59, (byte)0x99, (byte)0xF1, (byte)0xBE, (byte)0x3C,
			(byte)0xE9, (byte)0xFD, (byte)0xB2, (byte)0x9E, (byte)0x68, (byte)0x91, (byte)0x62, (byte)0x01,
			(byte)0x9D, (byte)0xD3, (byte)0x0D, (byte)0xAA, (byte)0x28, (byte)0xC5, (byte)0x02, (byte)0xF4,
			(byte)0x9B, (byte)0x6D, (byte)0xD0, (byte)0xA5, (byte)0x1D, (byte)0xB1, (byte)0xED, (byte)0x2B,
			(byte)0x16, (byte)0xC0, (byte)0x17, (byte)0xC7, (byte)0xBC, (byte)0x1A, (byte)0x17, (byte)0xDB,
			(byte)0xBE, (byte)0x08, (byte)0x10, (byte)0xF5, (byte)0x94, (byte)0x51, (byte)0xBA, (byte)0xB4,
			(byte)0x0B, (byte)0xFA, (byte)0xED, (byte)0x9E, (byte)0xD8, (byte)0x26, (byte)0xF9, (byte)0x4F,
			(byte)0x19, (byte)0x2F, (byte)0x11, (byte)0x0F, (byte)0x23, (byte)0x0A, (byte)0x12, (byte)0x26,
			(byte)0x3A, (byte)0xA6, (byte)0x6A, (byte)0xD1, (byte)0xE6, (byte)0x85, (byte)0xD4, (byte)0x19,
			(byte)0xC3, (byte)0x93, (byte)0xC4, (byte)0xAB, (byte)0xFA, (byte)0xF7, (byte)0x39, (byte)0x7B,
			(byte)0xC3, (byte)0x38, (byte)0xFB, (byte)0x03, (byte)0xEF, (byte)0x1F, (byte)0x63, (byte)0xFD,
			(byte)0xA2, (byte)0x02, (byte)0xF3, (byte)0xF0, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
			(byte)0x49, (byte)0x45, (byte)0x4E, (byte)0x44, (byte)0xAE, (byte)0x42, (byte)0x60, (byte)0x82
		};
	}

	//==================================================================

}

//----------------------------------------------------------------------
