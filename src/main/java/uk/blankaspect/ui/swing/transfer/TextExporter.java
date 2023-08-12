/*====================================================================*\

TextExporter.java

Text exporter class.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.swing.transfer;

//----------------------------------------------------------------------


// IMPORTS


import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;

//----------------------------------------------------------------------


// TEXT EXPORTER CLASS


public class TextExporter
	extends TransferHandler
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	protected TextExporter()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	@Override
	public int getSourceActions(JComponent component)
	{
		return COPY_OR_MOVE;
	}

	//------------------------------------------------------------------

	@Override
	protected Transferable createTransferable(JComponent component)
	{
		Transferable transferable = null;
		if (component instanceof JTextComponent)
		{
			JTextComponent textComponent = (JTextComponent)component;
			int start = textComponent.getSelectionStart();
			int end = textComponent.getSelectionEnd();
			if (start < end)
			{
				try
				{
					Document document = textComponent.getDocument();
					p0 = document.createPosition(start);
					p1 = document.createPosition(end);
					transferable = new StringSelection(textComponent.getSelectedText());
				}
				catch (BadLocationException e)
				{
					e.printStackTrace();
				}
			}
		}
		return transferable;
	}

	//------------------------------------------------------------------

	@Override
	protected void exportDone(JComponent   component,
							  Transferable data,
							  int          action)
	{
		if ((component instanceof JTextComponent) && (action == MOVE) && (p0 != null) && (p1 != null) &&
			 (p0.getOffset() != p1.getOffset()))
		{
			try
			{
				((JTextComponent)component).getDocument().
											remove(p0.getOffset(), p1.getOffset() - p0.getOffset());
			}
			catch (BadLocationException e)
			{
				e.printStackTrace();
			}
		}
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	Position	p0;
	private	Position	p1;

}

//----------------------------------------------------------------------
