/*====================================================================*\

InputModifiers.java

Input modifiers enumeration.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.swing.modifiers;

//----------------------------------------------------------------------


// IMPORTS


import java.awt.event.InputEvent;

//----------------------------------------------------------------------


// INPUT MODIFIERS


public enum InputModifiers
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	NONE            (0),
	SHIFT           (InputEvent.SHIFT_DOWN_MASK),
	CTRL            (InputEvent.CTRL_DOWN_MASK),
	ALT             (InputEvent.ALT_DOWN_MASK),
	CTRL_SHIFT      (InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK),
	ALT_SHIFT       (InputEvent.ALT_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK),
	ALT_CTRL        (InputEvent.ALT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK),
	ALT_CTRL_SHIFT  (InputEvent.ALT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK),
	UNRECOGNISED    (0);

	//------------------------------------------------------------------

	public static final	int	MASK	= InputEvent.ALT_DOWN_MASK | InputEvent.ALT_GRAPH_DOWN_MASK |
													InputEvent.CTRL_DOWN_MASK | InputEvent.META_DOWN_MASK |
													InputEvent.SHIFT_DOWN_MASK;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	private InputModifiers(int value)
	{
		this.value = value;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static InputModifiers forModifiers(int modifiers)
	{
		int targetValue = modifiers & MASK;
		for (InputModifiers mod : values())
		{
			if (mod.value == targetValue)
				return mod;
		}
		return UNRECOGNISED;
	}

	//------------------------------------------------------------------

	public static InputModifiers forEvent(InputEvent event)
	{
		return forModifiers(event.getModifiersEx());
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public boolean isShift()
	{
		return ((value & InputEvent.SHIFT_DOWN_MASK) != 0);
	}

	//------------------------------------------------------------------

	public boolean isControl()
	{
		return ((value & InputEvent.CTRL_DOWN_MASK) != 0);
	}

	//------------------------------------------------------------------

	public boolean isAlt()
	{
		return ((value & InputEvent.ALT_DOWN_MASK) != 0);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	int	value;

}

//----------------------------------------------------------------------
