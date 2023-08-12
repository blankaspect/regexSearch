/*====================================================================*\

IntegerValueField.java

Integer value field class.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.swing.textfield;

//----------------------------------------------------------------------


// INTEGER VALUE FIELD CLASS


public abstract class IntegerValueField
	extends ConstrainedTextField
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	protected IntegerValueField(int maxLength)
	{
		super(maxLength);
	}

	//------------------------------------------------------------------

	protected IntegerValueField(int    maxLength,
								String text)
	{
		super(maxLength, text);
	}

	//------------------------------------------------------------------

	protected IntegerValueField(int maxLength,
								int columns)
	{
		super(maxLength, columns);
	}

	//------------------------------------------------------------------

	protected IntegerValueField(int    maxLength,
								int    columns,
								String text)
	{
		super(maxLength, columns, text);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Abstract methods
////////////////////////////////////////////////////////////////////////

	public abstract int getValue();

	//------------------------------------------------------------------

	public abstract void setValue(int value);

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
