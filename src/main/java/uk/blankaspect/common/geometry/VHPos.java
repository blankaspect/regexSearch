/*====================================================================*\

VHPos.java

Enumeration: pairings of a vertical position and a horizontal position.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.geometry;

//----------------------------------------------------------------------


// ENUMERATION: PAIRINGS OF A VERTICAL POSITION AND A HORIZONTAL POSITION


/**
 * This is an enumeration of the nine pairings of three vertical positions ({@code TOP}, {@code CENTRE}, {@code BOTTOM})
 * with three horizontal positions ({@code LEFT}, {@code CENTRE}, {@code RIGHT}).
 */

public enum VHPos
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/**
	 * Vertical position = {@code TOP}, horizontal position = {@code LEFT}
	 */
	TOP_LEFT
	(
		V.TOP,
		H.LEFT
	),

	/**
	 * Vertical position = {@code TOP}, horizontal position = {@code CENTRE}
	 */
	TOP_CENTRE
	(
		V.TOP,
		H.CENTRE
	),

	/**
	 * Vertical position = {@code TOP}, horizontal position = {@code RIGHT}
	 */
	TOP_RIGHT
	(
		V.TOP,
		H.RIGHT
	),

	/**
	 * Vertical position = {@code CENTRE}, horizontal position = {@code LEFT}
	 */
	CENTRE_LEFT
	(
		V.CENTRE,
		H.LEFT
	),

	/**
	 * Vertical position = {@code CENTRE}, horizontal position = {@code CENTRE}
	 */
	CENTRE_CENTRE
	(
		V.CENTRE,
		H.CENTRE
	),

	/**
	 * Vertical position = {@code CENTRE}, horizontal position = {@code RIGHT}
	 */
	CENTRE_RIGHT
	(
		V.CENTRE,
		H.RIGHT
	),

	/**
	 * Vertical position = {@code BOTTOM}, horizontal position = {@code LEFT}
	 */
	BOTTOM_LEFT
	(
		V.BOTTOM,
		H.LEFT
	),

	/**
	 * Vertical position = {@code BOTTOM}, horizontal position = {@code CENTRE}
	 */
	BOTTOM_CENTRE
	(
		V.BOTTOM,
		H.CENTRE
	),

	/**
	 * Vertical position = {@code BOTTOM}, horizontal position = {@code RIGHT}
	 */
	BOTTOM_RIGHT
	(
		V.BOTTOM,
		H.RIGHT
	);

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** The vertical position. */
	private	V	v;

	/** The horizontal position. */
	private	H	h;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of a pairing of a vertical position and a horizontal position.
	 *
	 * @param v
	 *          the vertical position.
	 * @param h
	 *          the horizontal position.
	 */

	private VHPos(
		V	v,
		H	h)
	{
		// Initialise instance variables
		this.v = v;
		this.h = h;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates and returns a new instance of a pairing of the specified vertical position and horizontal position.
	 *
	 * @param v
	 *          the vertical position.
	 * @param h
	 *          the horizontal position.
	 */

	public static VHPos of(
		V	v,
		H	h)
	{
		// Validate arguments
		if (v == null)
			throw new IllegalArgumentException("Null vertical position");
		if (h == null)
			throw new IllegalArgumentException("Null horizontal position");

		// Return vertical-horizontal position
		switch (v)
		{
			case TOP:
				switch (h)
				{
					case LEFT:
						return VHPos.TOP_LEFT;

					case CENTRE:
						return VHPos.TOP_CENTRE;

					case RIGHT:
						return VHPos.TOP_RIGHT;
				}
				break;

			case CENTRE:
				switch (h)
				{
					case LEFT:
						return VHPos.CENTRE_LEFT;

					case CENTRE:
						return VHPos.CENTRE_CENTRE;

					case RIGHT:
						return VHPos.CENTRE_RIGHT;
				}
				break;

			case BOTTOM:
				switch (h)
				{
					case LEFT:
						return VHPos.BOTTOM_LEFT;

					case CENTRE:
						return VHPos.BOTTOM_CENTRE;

					case RIGHT:
						return VHPos.BOTTOM_RIGHT;
				}
				break;
		}

		return null;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////


	/**
	 * Returns the vertical position of this pairing of a vertical position and a horizontal position.
	 *
	 * @return the vertical position of this pairing of a vertical position and a horizontal position.
	 */

	public V getV()
	{
		return v;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the horizontal position of this pairing of a vertical position and a horizontal position.
	 *
	 * @return the horizontal position of this pairing of a vertical position and a horizontal position.
	 */

	public H getH()
	{
		return h;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Enumerated types
////////////////////////////////////////////////////////////////////////


	// ENUMERATION: VERTICAL POSITION


	/**
	 * This is an enumeration of the vertical positions of {@link VHPos}.
	 */

	public enum V
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		TOP
		{
			/**
			 * {@inheritDoc}
			 */

			@Override
			public boolean isOpposite(
				V	other)
			{
				return (other == BOTTOM);
			}

			//----------------------------------------------------------

		},

		CENTRE
		{
			/**
			 * {@inheritDoc}
			 */

			@Override
			public boolean isOpposite(
				V	other)
			{
				return false;
			}

			//----------------------------------------------------------

		},

		BOTTOM
		{
			/**
			 * {@inheritDoc}
			 */

			@Override
			public boolean isOpposite(
				V	other)
			{
				return (other == TOP);
			}

			//----------------------------------------------------------

		};

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		/**
		 * Creates a new instance of an enumeration constant of a vertical position.
		 */

		private V()
		{
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Abstract methods
	////////////////////////////////////////////////////////////////////

		/**
		 * Returns {@code true} if the specified vertical position is opposite this vertical position.
		 *
		 * @param  other
		 *           the vertical position of interest.
		 * @return {@code true} if {@code other} is opposite this vertical position.
		 */

		public abstract boolean isOpposite(
			V	other);

		//--------------------------------------------------------------

	}

	//==================================================================


	// ENUMERATION: HORIZONTAL POSITION


	/**
	 * This is an enumeration of the horizontal positions of {@link VHPos}.
	 */

	public enum H
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		LEFT
		{
			/**
			 * {@inheritDoc}
			 */

			@Override
			public boolean isOpposite(H other)
			{
				return (other == RIGHT);
			}

			//----------------------------------------------------------

		},

		CENTRE
		{
			/**
			 * {@inheritDoc}
			 */

			@Override
			public boolean isOpposite(H other)
			{
				return false;
			}

			//----------------------------------------------------------

		},

		RIGHT
		{
			/**
			 * {@inheritDoc}
			 */

			@Override
			public boolean isOpposite(H other)
			{
				return (other == LEFT);
			}

			//----------------------------------------------------------

		};

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		/**
		 * Creates a new instance of an enumeration constant of a horizontal position.
		 */

		private H()
		{
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Abstract methods
	////////////////////////////////////////////////////////////////////

		/**
		 * Returns {@code true} if the specified horizontal position is opposite this horizontal position.
		 *
		 * @param  other
		 *           the horizontal position of interest.
		 * @return {@code true} if {@code other} is opposite this horizontal position.
		 */

		public abstract boolean isOpposite(H other);

		//--------------------------------------------------------------

	}

	//==================================================================

}

//----------------------------------------------------------------------
