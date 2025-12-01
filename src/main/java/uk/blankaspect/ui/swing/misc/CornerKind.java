/*====================================================================*\

CornerKind.java

Corner kind enumeration.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.swing.misc;

//----------------------------------------------------------------------


// IMPORTS


import java.awt.Point;
import java.awt.Rectangle;

//----------------------------------------------------------------------


// CORNER KIND ENUMERATION


public enum CornerKind
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	TOP_LEFT
	{
		@Override
		public Rectangle getRectangle(Rectangle rect,
									  int       size)
		{
			int width = Math.min(rect.width, size);
			int height = Math.min(rect.height, size);
			return new Rectangle(rect.x, rect.y, width, height);
		}

		//--------------------------------------------------------------

		@Override
		public Point getLocation(Rectangle rect,
								 Rectangle boundingRect,
								 int       size)
		{
			return new Point(getCoord(rect.x, boundingRect.x, boundingRect.width, size),
							 getCoord(rect.y, boundingRect.y, boundingRect.height, size));
		}

		//--------------------------------------------------------------
	},

	TOP_RIGHT
	{
		@Override
		public Rectangle getRectangle(Rectangle rect,
									  int       size)
		{
			int width = Math.min(rect.width, size);
			int height = Math.min(rect.height, size);
			return new Rectangle(rect.x + rect.width - width, rect.y, width, height);
		}

		//--------------------------------------------------------------

		@Override
		public Point getLocation(Rectangle rect,
								 Rectangle boundingRect,
								 int       size)
		{
			return new Point(getCoord(rect.x, rect.width, boundingRect.x, boundingRect.width, size),
							 getCoord(rect.y, boundingRect.y, boundingRect.height, size));
		}

		//--------------------------------------------------------------
	},

	BOTTOM_LEFT
	{
		@Override
		public Rectangle getRectangle(Rectangle rect,
									  int       size)
		{
			int width = Math.min(rect.width, size);
			int height = Math.min(rect.height, size);
			return new Rectangle(rect.x, rect.y + rect.height - height, width, height);
		}

		//--------------------------------------------------------------

		@Override
		public Point getLocation(Rectangle rect,
								 Rectangle boundingRect,
								 int       size)
		{
			return new Point(getCoord(rect.x, boundingRect.x, boundingRect.width, size),
							 getCoord(rect.y, rect.height, boundingRect.y, boundingRect.height, size));
		}

		//--------------------------------------------------------------
	},

	BOTTOM_RIGHT
	{
		@Override
		public Rectangle getRectangle(Rectangle rect,
									  int       size)
		{
			int width = Math.min(rect.width, size);
			int height = Math.min(rect.height, size);
			return new Rectangle(rect.x + rect.width - width, rect.y + rect.height - height,
								 width, height);
		}

		//--------------------------------------------------------------

		@Override
		public Point getLocation(Rectangle rect,
								 Rectangle boundingRect,
								 int       size)
		{
			return new Point(getCoord(rect.x, rect.width, boundingRect.x, boundingRect.width, size),
							 getCoord(rect.y, rect.height, boundingRect.y, boundingRect.height, size));
		}

		//--------------------------------------------------------------
	};

	//------------------------------------------------------------------

	public static final	CornerKind[]	TOP_CORNERS		= { TOP_LEFT,    TOP_RIGHT };
	public static final	CornerKind[]	BOTTOM_CORNERS	= { BOTTOM_LEFT, BOTTOM_RIGHT };
	public static final	CornerKind[]	LEFT_CORNERS	= { TOP_LEFT,    BOTTOM_LEFT };
	public static final	CornerKind[]	RIGHT_CORNERS	= { TOP_RIGHT,   BOTTOM_RIGHT };

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	private CornerKind()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	private static int getCoord(int coord,
								int boundingCoord,
								int boundingDimension,
								int size)
	{
		return Math.max(boundingCoord, Math.min(coord, boundingCoord + boundingDimension - size));
	}

	//------------------------------------------------------------------

	private static int getCoord(int coord,
								int dimension,
								int boundingCoord,
								int boundingDimension,
								int size)
	{
		return Math.min(Math.max(boundingCoord + size, coord + dimension),
						boundingCoord + boundingDimension) - dimension;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Abstract methods
////////////////////////////////////////////////////////////////////////

	public abstract Rectangle getRectangle(Rectangle rect,
										   int       size);

	//------------------------------------------------------------------

	public abstract Point getLocation(Rectangle rect,
									  Rectangle boundingRect,
									  int       size);

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
