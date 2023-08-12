/*====================================================================*\

DimensionsSpinnerPanel.java

Dimensions spinner panel class.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.swing.container;

//----------------------------------------------------------------------


// IMPORTS


import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import uk.blankaspect.ui.swing.button.LinkedPairButton;

import uk.blankaspect.ui.swing.font.FontKey;
import uk.blankaspect.ui.swing.font.FontUtils;

import uk.blankaspect.ui.swing.label.FLabel;

import uk.blankaspect.ui.swing.spinner.FIntegerSpinner;

//----------------------------------------------------------------------


// DIMENSIONS SPINNER PANEL CLASS


public class DimensionsSpinnerPanel
	extends JPanel
	implements ActionListener, ChangeListener
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	private static final	char	TIMES_CHAR	= '\u00D7';
	private static final	String	AND_STR		= " and ";

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * @throws IllegalArgumentException
	 */

	public DimensionsSpinnerPanel(int      value1,
								  int      value2,
								  int      minValue,
								  int      maxValue,
								  int      maxLength,
								  String[] text,
								  boolean  linked)
	{
		this(value1, minValue, maxValue, maxLength, value2, minValue, maxValue, maxLength, text, true,
			 linked);
	}

	//------------------------------------------------------------------

	/**
	 * @throws IllegalArgumentException
	 */

	public DimensionsSpinnerPanel(int      value1,
								  int      minValue1,
								  int      maxValue1,
								  int      maxLength1,
								  int      value2,
								  int      minValue2,
								  int      maxValue2,
								  int      maxLength2,
								  String[] text)
	{
		this(value1, minValue1, maxValue1, maxLength1, value2, minValue2, maxValue2, maxLength2, text,
			 false, false);
	}

	//------------------------------------------------------------------

	/**
	 * @throws IllegalArgumentException
	 */

	private DimensionsSpinnerPanel(int      value1,
								   int      minValue1,
								   int      maxValue1,
								   int      maxLength1,
								   int      value2,
								   int      minValue2,
								   int      maxValue2,
								   int      maxLength2,
								   String[] text,
								   boolean  linkable,
								   boolean  linked)
	{
		// Validate arguments
		if ((text != null) && (text.length < 2))
			throw new IllegalArgumentException();

		// Set layout manager
		GridBagLayout gridBag = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		setLayout(gridBag);

		int gridX = 0;

		// Spinner: value1
		value1Spinner = new FIntegerSpinner(value1, minValue1, maxValue1, maxLength1);
		if (linkable)
			value1Spinner.addChangeListener(this);

		gbc.gridx = gridX++;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(0, 0, 0, 0);
		gridBag.setConstraints(value1Spinner, gbc);
		add(value1Spinner);

		// Label: times
		JLabel timesLabel = new FLabel(getTimesString());

		gbc.gridx = gridX++;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(0, 2, 0, 0);
		gridBag.setConstraints(timesLabel, gbc);
		add(timesLabel);

		// Spinner: value 2
		value2Spinner = new FIntegerSpinner(value2, minValue2, maxValue2, maxLength2);
		if (linkable)
			value2Spinner.addChangeListener(this);

		gbc.gridx = gridX++;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(0, 2, 0, 0);
		gridBag.setConstraints(value2Spinner, gbc);
		add(value2Spinner);

		// Button: link
		if (linkable)
		{
			linkButton = new LinkedPairButton((text == null) ? null : text[0] + AND_STR + text[1]);
			linkButton.addActionListener(this);
			if (value1 == value2)
				linkButton.setSelected(linked);

			gbc.gridx = gridX++;
			gbc.gridy = 0;
			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			gbc.weightx = 0.0;
			gbc.weighty = 0.0;
			gbc.anchor = GridBagConstraints.LINE_START;
			gbc.fill = GridBagConstraints.NONE;
			gbc.insets = new Insets(0, 6, 0, 0);
			gridBag.setConstraints(linkButton, gbc);
			add(linkButton);
		}

		// Label: value1 by value2
		if (text != null)
		{
			JLabel byLabel = new FLabel("(" + text[0] + " " + getTimesString() + " " + text[1] + ")");

			gbc.gridx = gridX++;
			gbc.gridy = 0;
			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			gbc.weightx = 0.0;
			gbc.weighty = 0.0;
			gbc.anchor = GridBagConstraints.LINE_START;
			gbc.fill = GridBagConstraints.NONE;
			gbc.insets = new Insets(0, 6, 0, 0);
			gridBag.setConstraints(byLabel, gbc);
			add(byLabel);
		}
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static String getTimesString()
	{
		return (FontUtils.getAppFont(FontKey.MAIN).canDisplay(TIMES_CHAR)
																					? Character.toString(TIMES_CHAR)
																					: "x");
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : ActionListener interface
////////////////////////////////////////////////////////////////////////

	public void actionPerformed(ActionEvent event)
	{
		if (linkButton.isSelected())
			value2Spinner.setIntValue(value1Spinner.getIntValue());
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : ChangeListener interface
////////////////////////////////////////////////////////////////////////

	public void stateChanged(ChangeEvent event)
	{
		Object eventSource = event.getSource();

		// Spinner, value 1
		if (eventSource == value1Spinner)
		{
			if (linkButton.isSelected())
				value2Spinner.setIntValue(value1Spinner.getIntValue());
		}

		// Spinner, value 2
		else if (eventSource == value2Spinner)
		{
			if (linkButton.isSelected())
				value1Spinner.setIntValue(value2Spinner.getIntValue());
		}
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public FIntegerSpinner getValue1Spinner()
	{
		return value1Spinner;
	}

	//------------------------------------------------------------------

	public FIntegerSpinner getValue2Spinner()
	{
		return value2Spinner;
	}

	//------------------------------------------------------------------

	public int getValue1()
	{
		return value1Spinner.getIntValue();
	}

	//------------------------------------------------------------------

	public int getValue2()
	{
		return value2Spinner.getIntValue();
	}

	//------------------------------------------------------------------

	public Dimension getDimensions()
	{
		return new Dimension(value1Spinner.getIntValue(), value2Spinner.getIntValue());
	}

	//------------------------------------------------------------------

	/**
	 * @throws IllegalStateException
	 */

	public boolean isLinked()
	{
		if (linkButton == null)
			throw new IllegalStateException();

		return linkButton.isSelected();
	}

	//------------------------------------------------------------------

	public void setValue1(int value)
	{
		value1Spinner.setIntValue(value);
	}

	//------------------------------------------------------------------

	public void setValue2(int value)
	{
		value2Spinner.setIntValue(value);
	}

	//------------------------------------------------------------------

	public void setValues(int value1,
						  int value2)
	{
		if ((linkButton != null) && (value1 != value2))
			linkButton.setSelected(false);
		value1Spinner.setIntValue(value1);
		value2Spinner.setIntValue(value2);
	}

	//------------------------------------------------------------------

	/**
	 * @throws IllegalStateException
	 */

	public void setLinked(boolean linked)
	{
		if (linkButton == null)
			throw new IllegalStateException();

		linkButton.setSelected(linked);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	FIntegerSpinner		value1Spinner;
	private	FIntegerSpinner		value2Spinner;
	private	LinkedPairButton	linkButton;

}

//----------------------------------------------------------------------
