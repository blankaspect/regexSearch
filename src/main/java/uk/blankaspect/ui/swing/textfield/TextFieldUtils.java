/*====================================================================*\

TextFieldUtils.java

Class: text-field utility methods.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.swing.textfield;

//----------------------------------------------------------------------


// IMPORTS


import java.awt.KeyboardFocusManager;

import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

//----------------------------------------------------------------------


// CLASS: TEXT-FIELD UTILITY METHODS


public class TextFieldUtils
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	private static final	String	PERMANENT_FOCUS_OWNER_PROPERTY_KEY	= "permanentFocusOwner";

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	private TextFieldUtils()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static void selectAllOnFocusGained()
	{
		KeyboardFocusManager.getCurrentKeyboardFocusManager()
												.addPropertyChangeListener(PERMANENT_FOCUS_OWNER_PROPERTY_KEY, event ->
		{
			// If old focus owner was text field, clear its selection
			Object oldValue = event.getOldValue();
			if (oldValue instanceof JTextField)
			{
				SwingUtilities.invokeLater(() ->
				{
					JTextField field = (JTextField)oldValue;
					int pos = field.getCaretPosition();
					field.setCaretPosition(pos);
					field.moveCaretPosition(pos);
				});
			}

			// If new focus owner is text field, select all its text
			Object newValue = event.getNewValue();
			if ((newValue instanceof JTextField) && !(newValue instanceof JPasswordField))
				SwingUtilities.invokeLater(() -> ((JTextField)newValue).selectAll());
		});
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
