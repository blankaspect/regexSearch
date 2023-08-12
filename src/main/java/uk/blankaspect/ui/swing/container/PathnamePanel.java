/*====================================================================*\

PathnamePanel.java

Pathname panel class.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.swing.container;

//----------------------------------------------------------------------


// IMPORTS


import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import java.awt.event.ActionListener;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import uk.blankaspect.ui.swing.button.FButton;

import uk.blankaspect.ui.swing.misc.GuiConstants;

//----------------------------------------------------------------------


// PATHNAME PANEL CLASS


public class PathnamePanel
	extends JPanel
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	private static final	Insets	BROWSE_BUTTON_MARGINS	= new Insets(2, 6, 2, 6);

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public PathnamePanel(JComponent pathnameField,
						 Action     action)
	{
		this(pathnameField, new FButton(action));
	}

	//------------------------------------------------------------------

	public PathnamePanel(JComponent     pathnameField,
						 String         command,
						 ActionListener actionListener)
	{
		this(pathnameField, createBrowseButton(command, actionListener));
	}

	//------------------------------------------------------------------

	public PathnamePanel(JComponent pathnameField,
						 JButton    browseButton)
	{
		// Initialise instance variables
		this.browseButton = browseButton;

		// Set layout manager
		GridBagLayout gridBag = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();

		setLayout(gridBag);

		int gridX = 0;

		// Pathname field
		gbc.gridx = gridX++;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.VERTICAL;
		gbc.insets = new Insets(0, 0, 0, 0);
		gridBag.setConstraints(pathnameField, gbc);
		add(pathnameField);

		// Browse button
		browseButton.setMargin(BROWSE_BUTTON_MARGINS);

		gbc.gridx = gridX++;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.VERTICAL;
		gbc.insets = new Insets(0, 6, 0, 0);
		gridBag.setConstraints(browseButton, gbc);
		add(browseButton);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	private static JButton createBrowseButton(String         command,
											  ActionListener actionListener)
	{
		JButton button = new FButton(GuiConstants.ELLIPSIS_STR);
		if (actionListener == null)
			button.setEnabled(false);
		else
		{
			button.setActionCommand(command);
			button.addActionListener(actionListener);
		}
		return button;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public void setButtonTooltipText(String text)
	{
		browseButton.setToolTipText(text);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	JButton	browseButton;

}

//----------------------------------------------------------------------
