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
			if (event.getOldValue() instanceof JTextField textField)
			{
				SwingUtilities.invokeLater(() ->
				{
					int pos = textField.getCaretPosition();
					textField.setCaretPosition(pos);
					textField.moveCaretPosition(pos);
				});
			}

			// If new focus owner is text field, select all its text
			if ((event.getNewValue() instanceof JTextField textField) && !(textField instanceof JPasswordField))
				SwingUtilities.invokeLater(textField::selectAll);
		});
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
