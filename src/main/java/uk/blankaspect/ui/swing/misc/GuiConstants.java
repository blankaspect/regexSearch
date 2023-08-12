/*====================================================================*\

GuiConstants.java

Interface: GUI constants.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.swing.misc;

//----------------------------------------------------------------------


// IMPORTS


import java.awt.BasicStroke;
import java.awt.Insets;
import java.awt.Stroke;

//----------------------------------------------------------------------


// INTERFACE: GUI CONSTANTS


public interface GuiConstants
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	// Component constants
	Insets	COMPONENT_INSETS	= new Insets(2, 3, 2, 3);

	// Strings
	String	ELLIPSIS_STR	= "...";
	String	OK_STR			= "OK";
	String	CANCEL_STR		= "Cancel";
	String	CLOSE_STR		= "Close";

	// Strokes
	Stroke	BASIC_DASH	= new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f,
										  new float[] { 1.0f, 1.0f }, 0.5f);

}

//----------------------------------------------------------------------
