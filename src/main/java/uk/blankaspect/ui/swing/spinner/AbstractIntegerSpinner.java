/*====================================================================*\

AbstractIntegerSpinner.java

Abstract integer spinner class.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.swing.spinner;

//----------------------------------------------------------------------


// IMPORTS


import javax.swing.BorderFactory;
import javax.swing.SpinnerNumberModel;

import uk.blankaspect.ui.swing.textfield.IntegerValueField;

//----------------------------------------------------------------------


// ABSTRACT INTEGER SPINNER CLASS


public abstract class AbstractIntegerSpinner
	extends AbstractSpinner
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	private static final	int	STEP_SIZE	= 1;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public AbstractIntegerSpinner(int value,
								  int minValue,
								  int maxValue)
	{
		this(value, minValue, maxValue, false);
	}

	//------------------------------------------------------------------

	public AbstractIntegerSpinner(int     value,
								  int     minValue,
								  int     maxValue,
								  boolean alwaysUpdate)
	{
		super(new SpinnerNumberModel(value, minValue, maxValue, STEP_SIZE), alwaysUpdate);
	}

	//------------------------------------------------------------------

	public AbstractIntegerSpinner(int               value,
								  int               minValue,
								  int               maxValue,
								  IntegerValueField editor)
	{
		this(value, minValue, maxValue, editor, false);
	}

	//------------------------------------------------------------------

	public AbstractIntegerSpinner(int               value,
								  int               minValue,
								  int               maxValue,
								  IntegerValueField editor,
								  boolean           alwaysUpdate)
	{
		this(value, minValue, maxValue, alwaysUpdate);
		initEditor(editor);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	@Override
	public boolean requestFocusInWindow()
	{
		return (editor == null) ? super.requestFocusInWindow() : editor.requestFocusInWindow();
	}

	//------------------------------------------------------------------

	@Override
	public void updateValue()
	{
		try
		{
			int value = getEditorValue();
			int boundedValue = clampValue(value);
			setIntValue(boundedValue);
			if (alwaysUpdate || (value != boundedValue) || isEditorInvalid())
				setEditorValue(boundedValue);
		}
		catch (IllegalArgumentException e)
		{
			// ignore
		}
	}

	//------------------------------------------------------------------

	@Override
	public void updateEditorValue()
	{
		setEditorValue(getIntValue());
	}

	//------------------------------------------------------------------

	@Override
	protected void incrementValue(int increment)
	{
		long value = Math.min(Math.max((long)Integer.MIN_VALUE, (long)getIntValue() + (long)increment),
							  (long)Integer.MAX_VALUE);
		setIntValue(clampValue((int)value));
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public int getIntValue()
	{
		return (Integer)getValue();
	}

	//------------------------------------------------------------------

	public void setIntValue(int value)
	{
		setValue(value);
	}

	//------------------------------------------------------------------

	public void setMinimum(int minValue)
	{
		((SpinnerNumberModel)getModel()).setMinimum(minValue);
		setIntValue(clampValue(getIntValue()));
	}

	//------------------------------------------------------------------

	public void setMaximum(int maxValue)
	{
		((SpinnerNumberModel)getModel()).setMaximum(maxValue);
		setIntValue(clampValue(getIntValue()));
	}

	//------------------------------------------------------------------

	protected void initEditor(IntegerValueField editor)
	{
		// Set editor
		this.editor = editor;
		editor.setBorder(BorderFactory.createEmptyBorder(SpinnerConstants.VERTICAL_MARGIN,
														 SpinnerConstants.HORIZONTAL_MARGIN,
														 SpinnerConstants.VERTICAL_MARGIN,
														 SpinnerConstants.HORIZONTAL_MARGIN));
		setEditor(editor);
		updateEditorValue();

		// Add actions to editor
		addEditorActions();

		// Add listeners
		editor.setActionCommand(Command.COMMIT_EDIT);
		editor.addActionListener(this);
		editor.addFocusListener(this);
	}

	//------------------------------------------------------------------

	/**
	 * @throws IllegalArgumentException
	 */

	protected int getEditorValue()
	{
		return editor.getValue();
	}

	//------------------------------------------------------------------

	protected void setEditorValue(int value)
	{
		editor.setValue(value);
	}

	//------------------------------------------------------------------

	protected boolean isEditorInvalid()
	{
		return false;
	}

	//------------------------------------------------------------------

	protected int clampValue(int value)
	{
		SpinnerNumberModel model = (SpinnerNumberModel)getModel();
		int minValue = (Integer)model.getMinimum();
		int maxValue = (Integer)model.getMaximum();
		return Math.min(Math.max(minValue, value), maxValue);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	protected	IntegerValueField	editor;

}

//----------------------------------------------------------------------
