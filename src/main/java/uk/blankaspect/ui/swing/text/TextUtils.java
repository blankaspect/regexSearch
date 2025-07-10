/*====================================================================*\

TextUtils.java

Text utility methods class.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.swing.text;

//----------------------------------------------------------------------


// IMPORTS


import java.awt.FontMetrics;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

//----------------------------------------------------------------------


// TEXT UTILITY METHODS CLASS


public class TextUtils
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	public enum RemovalMode
	{
		START,
		MIDDLE,
		END
	}

	private static final	String	ELLIPSIS_STR	= "...";

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// STRING-WIDTH PAIR CLASS


	public static class StringWidth
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private StringWidth()
		{
			str = "";
		}

		//--------------------------------------------------------------

		private StringWidth(String str,
							int    width)
		{
			this.str = str;
			this.width = width;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		public	String	str;
		public	int		width;

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private TextUtils()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static StringWidth getWidestString(FontMetrics      fontMetrics,
											  Iterable<String> strs)
	{
		int maxWidth = 0;
		String widestStr = null;
		for (String str : strs)
		{
			int width = fontMetrics.stringWidth(str);
			if (maxWidth < width)
			{
				widestStr = str;
				maxWidth = width;
			}
		}
		return new StringWidth(widestStr, maxWidth);
	}

	//------------------------------------------------------------------

	public static StringWidth getWidestString(FontMetrics fontMetrics,
											  String...   strs)
	{
		return getWidestString(fontMetrics, List.of(strs));
	}

	//------------------------------------------------------------------

	public static String getLimitedWidthString(String      str,
											   FontMetrics fontMetrics,
											   int         maxWidth)
	{
		return getLimitedWidthString(str, fontMetrics, maxWidth, RemovalMode.END, null);
	}

	//------------------------------------------------------------------

	public static String getLimitedWidthString(String      str,
											   FontMetrics fontMetrics,
											   int         maxWidth,
											   RemovalMode removalMode)
	{
		return getLimitedWidthString(str, fontMetrics, maxWidth, removalMode, null);
	}

	//------------------------------------------------------------------

	/**
	 * @throws IllegalArgumentException
	 */

	public static String getLimitedWidthString(String      str,
											   FontMetrics fontMetrics,
											   int         maxWidth,
											   RemovalMode removalMode,
											   boolean[]   truncated)
	{
		if (removalMode == RemovalMode.MIDDLE)
			throw new IllegalArgumentException();

		if (truncated != null)
			truncated[0] = false;

		String outStr = null;
		if (str != null)
		{
			// If the width of the input string exceeds the maximum width, remove characters from the string
			// until it fits ...
			if (!str.isEmpty() && (fontMetrics.stringWidth(str) > maxWidth))
			{
				// Initialise variables
				int maxW = maxWidth - fontMetrics.stringWidth(ELLIPSIS_STR);
				if (maxW > 0)
				{
					// Remove characters from string
					char[] chars = str.toCharArray();
					int length = chars.length;
					switch (removalMode)
					{
						case START:
						{
							int offset = 0;
							while (++offset < length)
							{
								if (fontMetrics.charsWidth(chars, offset, length - offset) <= maxW)
									break;
							}
							if (offset < length)
								outStr = ELLIPSIS_STR + new String(chars, offset, length - offset);
							break;
						}

						case MIDDLE:
							// do nothing
							break;

						case END:
						{
							while (--length > 0)
							{
								if (fontMetrics.charsWidth(chars, 0, length) <= maxW)
									break;
							}
							if (length > 0)
								outStr = new String(chars, 0, length) + ELLIPSIS_STR;
							break;
						}
					}
				}

				if (outStr == null)
				{
					switch (removalMode)
					{
						case START:
							outStr = ELLIPSIS_STR + str.charAt(str.length() - 1);
							while (!outStr.isEmpty())
							{
								if (fontMetrics.stringWidth(outStr) <= maxWidth)
									break;
								outStr = outStr.substring(1);
							}
							break;

						case MIDDLE:
							// do nothing
							break;

						case END:
						{
							outStr = str.charAt(0) + ELLIPSIS_STR;
							while (!outStr.isEmpty())
							{
								if (fontMetrics.stringWidth(outStr) <= maxWidth)
									break;
								outStr = outStr.substring(0, outStr.length() - 1);
							}
							break;
						}
					}
				}

				if (truncated != null)
					truncated[0] = true;
			}

			// ... otherwise, set the result to the entire input string
			else
				outStr = str;
		}
		return outStr;
	}

	//------------------------------------------------------------------

	public static String getLimitedWidthPathname(String      pathname,
												 FontMetrics fontMetrics,
												 int         maxWidth)
	{
		return getLimitedWidthPathname(pathname, fontMetrics, maxWidth, File.separatorChar,
									   RemovalMode.MIDDLE);
	}

	//------------------------------------------------------------------

	public static String getLimitedWidthPathname(String      pathname,
												 FontMetrics fontMetrics,
												 int         maxWidth,
												 char        separatorChar)
	{
		return getLimitedWidthPathname(pathname, fontMetrics, maxWidth, separatorChar,
									   RemovalMode.MIDDLE);
	}

	//------------------------------------------------------------------

	public static String getLimitedWidthPathname(String      pathname,
												 FontMetrics fontMetrics,
												 int         maxWidth,
												 char        separatorChar,
												 RemovalMode removalMode)
	{
		String outPathname = null;
		if (pathname != null)
		{
			// If the width of the pathname exceeds the maximum width, create a reduced pathname ...
			if (fontMetrics.stringWidth(pathname) > maxWidth)
			{
				// Create a list of pathname elements.  If the pathname starts or ends with a separator, the separator
				// is treated as belonging to the first or last element respectively.
				List<StringWidth> stringWidths = new ArrayList<>();
				int separatorWidth = fontMetrics.charWidth(separatorChar);
				int totalWidth = 0;
				int index = 0;
				while (index < pathname.length())
				{
					int startIndex = index;
					index = pathname.indexOf(separatorChar, index);
					if (index == 0)
						index = pathname.indexOf(separatorChar, 1);
					if ((index < 0) || (index == pathname.length() - 1))
						index = pathname.length();
					else
						totalWidth += separatorWidth;
					if (index == startIndex)
						stringWidths.add(new StringWidth());
					else
					{
						String str = pathname.substring(startIndex, index);
						int width = fontMetrics.stringWidth(str);
						stringWidths.add(new StringWidth(str, width));
						totalWidth += width;
					}
					++index;
				}

				// Create a reduced pathname from the pathname elements
				int ellipsisWidth = fontMetrics.stringWidth(ELLIPSIS_STR);
				StringBuilder buffer = new StringBuilder(pathname.length());
				switch (removalMode)
				{
					case START:
					{
						// Remove elements from the pathname while it exceeds the maximum width
						index = 0;
						while (index < stringWidths.size())
						{
							int width = stringWidths.get(index++).width;
							totalWidth -= width;
							if (totalWidth + ellipsisWidth <= maxWidth)
								break;
							totalWidth -= separatorWidth;
						}

						// Concatenate an ellipsis and the remaining elements
						if (index < stringWidths.size())
						{
							buffer.append(ELLIPSIS_STR);
							for (int i = index; i < stringWidths.size(); i++)
							{
								buffer.append(separatorChar);
								buffer.append(stringWidths.get(i).str);
							}
							outPathname = buffer.toString();
						}
						break;
					}

					case MIDDLE:
					{
						// Find the widest and next widest pathname elements, excluding the first and last elements
						int i0 = -1;
						int maxWidth0 = 0;
						int i1 = -1;
						int maxWidth1 = 0;
						for (int i = 1; i < stringWidths.size() - 1; i++)
						{
							int width = stringWidths.get(i).width;
							if (maxWidth0 < width)
							{
								i1 = i0;
								maxWidth1 = maxWidth0;
								i0 = i;
								maxWidth0 = width;
							}
						}

						// Start with the widest element if it's at least twice as wide as the next widest; otherwise,
						// start with the middle element
						index = ((i0 >= 0) && (i1 >= 0) && (maxWidth0 >= 2 * maxWidth1))
																		? i0
																		: (stringWidths.size() - 1) / 2;

						// Remove elements from the pathname while it exceeds the maximum width
						i0 = index;
						i1 = i0 + 1;
						while (true)
						{
							int width = stringWidths.get(index).width;
							totalWidth -= width;
							if (totalWidth + ellipsisWidth <= maxWidth)
								break;
							totalWidth -= separatorWidth;

							if ((i0 == 0) && (i1 == stringWidths.size()))
								break;
							index = (i0 < stringWidths.size() - i1) ? i1++ : --i0;
						}

						// Concatenate the remaining elements and an ellipsis
						if ((i0 > 0) || (i1 < stringWidths.size()))
						{
							for (int i = 0; i < i0; i++)
							{
								buffer.append(stringWidths.get(i).str);
								buffer.append(separatorChar);
							}
							buffer.append(ELLIPSIS_STR);
							for (int i = i1; i < stringWidths.size(); i++)
							{
								buffer.append(separatorChar);
								buffer.append(stringWidths.get(i).str);
							}
							outPathname = buffer.toString();
						}
						break;
					}

					case END:
					{
						// Remove elements from the pathname while it exceeds the maximum width
						index = stringWidths.size();
						while (index > 0)
						{
							int width = stringWidths.get(--index).width;
							totalWidth -= width;
							if (totalWidth + ellipsisWidth <= maxWidth)
								break;
							totalWidth -= separatorWidth;
						}

						// Concatenate the remaining elements and an ellipsis
						if (index > 0)
						{
							for (int i = 0; i < index; i++)
							{
								buffer.append(stringWidths.get(i).str);
								buffer.append(separatorChar);
							}
							buffer.append(ELLIPSIS_STR);
							outPathname = buffer.toString();
						}
						break;
					}
				}

				// Remove characters from the most significant element
				if (outPathname == null)
				{
					String str = pathname;
					switch (removalMode)
					{
						case START:
						case MIDDLE:
						{
							if (!stringWidths.isEmpty())
								str = stringWidths.get(stringWidths.size() - 1).str;

							char[] chars = str.toCharArray();
							int length = chars.length;
							int offset = 0;
							while (++offset < length)
							{
								int width = fontMetrics.charsWidth(chars, offset, length - offset);
								if (ellipsisWidth + width <= maxWidth)
									break;
							}
							outPathname = (offset < length)
											? ELLIPSIS_STR + new String(chars, offset, length - offset)
											: ELLIPSIS_STR;
							break;
						}

						case END:
						{
							if (!stringWidths.isEmpty())
								str = stringWidths.get(0).str;

							char[] chars = str.toCharArray();
							int length = chars.length;
							while (--length > 0)
							{
								int width = fontMetrics.charsWidth(chars, 0, length);
								if (width + ellipsisWidth <= maxWidth)
									break;
							}
							outPathname = (length > 0) ? new String(chars, 0, length) + ELLIPSIS_STR
													   : ELLIPSIS_STR;
							break;
						}
					}
				}
			}

			// ... otherwise, set the result to the entire pathname
			else
				outPathname = pathname;
		}
		return outPathname;
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
