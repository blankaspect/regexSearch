/*====================================================================*\

IntegerSpinner.java

Integer spinner class.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.swing.spinner;

//----------------------------------------------------------------------


// IMPORTS


import java.awt.Font;

import uk.blankaspect.ui.swing.font.FontUtils;

import uk.blankaspect.ui.swing.textfield.IntegerValueField;

//----------------------------------------------------------------------


// INTEGER SPINNER CLASS


public class IntegerSpinner
	extends AbstractIntegerSpinner
{

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// EDITOR CLASS


	private static class Editor
		extends IntegerValueField
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		private static final	String	VALID_CHARS	= "-0123456789";

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private Editor(int     maxLength,
					   boolean signed)
		{
			super(maxLength);
			this.signed = signed;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		/**
		 * @throws NumberFormatException
		 */

		@Override
		public int getValue()
		{
			try
			{
				if (signed)
					return Integer.parseInt(getText());

				long value = Long.parseLong(getText());
				if (value > 0xFFFFFFFFL)
					throw new NumberFormatException();
				return (int)value;
			}
			catch (NumberFormatException e)
			{
				setInvalid(true);
				throw e;
			}
		}

		//--------------------------------------------------------------

		@Override
		public void setValue(int value)
		{
			setText(signed ? Integer.toString(value) : Long.toString(value & 0xFFFFFFFFL));
		}

		//--------------------------------------------------------------

		@Override
		protected boolean acceptCharacter(char ch,
										  int  index)
		{
			int i = VALID_CHARS.indexOf(ch);
			return ((i > 0) || (signed && (i == 0)));
		}

		//--------------------------------------------------------------

		@Override
		protected int getColumnWidth()
		{
			return (FontUtils.getCharWidth('0', getFontMetrics(getFont())) + 1);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		public void setSigned(boolean signed)
		{
			this.signed = signed;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	boolean	signed;

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public IntegerSpinner(int value,
						  int minValue,
						  int maxValue,
						  int maxLength)
	{
		this(value, minValue, maxValue, maxLength, false);
	}

	//------------------------------------------------------------------

	public IntegerSpinner(int     value,
						  int     minValue,
						  int     maxValue,
						  int     maxLength,
						  boolean signed)
	{
		super(value, minValue, maxValue, new Editor(maxLength, signed));
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	@Override
	public void setFont(Font font)
	{
		super.setFont(font);
		if (editor != null)
			editor.setFont(font);
	}

	//------------------------------------------------------------------

	@Override
	protected boolean isEditorInvalid()
	{
		return ((Editor)editor).isInvalid();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public void setSigned(boolean signed)
	{
		((Editor)editor).setSigned(signed);
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
