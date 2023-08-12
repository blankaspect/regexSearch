/*====================================================================*\

AbstractNonEditableTextAreaDialog.java

Abstract non-editable text area dialog class.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.swing.dialog;

//----------------------------------------------------------------------


// IMPORTS


import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Window;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import uk.blankaspect.ui.swing.button.FButton;

import uk.blankaspect.ui.swing.font.FontKey;
import uk.blankaspect.ui.swing.font.FontUtils;

import uk.blankaspect.ui.swing.misc.GuiUtils;

//----------------------------------------------------------------------


// ABSTRACT NON-EDITABLE TEXT AREA DIALOG CLASS


public abstract class AbstractNonEditableTextAreaDialog
	extends JDialog
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	private static final	int	TEXT_AREA_VERTICAL_MARGIN	= 2;
	private static final	int	TEXT_AREA_HORIZONTAL_MARGIN	= 4;

	private static final	int	BUTTON_GAP	= 16;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	protected AbstractNonEditableTextAreaDialog(Window       owner,
												String       titleStr,
												String       key,
												int          numColumns,
												int          numRows,
												List<Action> commands,
												String       defaultButtonKey,
												String       text)
	{

		// Call superclass constructor
		super(owner, titleStr, Dialog.ModalityType.APPLICATION_MODAL);

		// Set icons
		if (owner != null)
			setIconImages(owner.getIconImages());

		// Initialise instance variables
		this.key = key;


		//----  Text area scroll pane

		// Text area
		textArea = new JTextArea(text);
		String fontKey = FontKey.TEXT_AREA;
		if (!FontUtils.isAppFont(fontKey))
			fontKey = FontKey.TEXT_FIELD;
		FontUtils.setAppFont(fontKey, textArea);
		textArea.setBorder(null);
		textArea.setEditable(false);

		// Set text area attributes
		setTextAreaAttributes();

		// Scroll pane: text area
		JScrollPane textAreaScrollPane = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
														 JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		FontMetrics fontMetrics = textArea.getFontMetrics(textArea.getFont());
		int width = numColumns * FontUtils.getCharWidth('0', fontMetrics);
		int height = numRows * fontMetrics.getHeight();
		textAreaScrollPane.getViewport().setPreferredSize(new Dimension(width, height));
		GuiUtils.setViewportBorder(textAreaScrollPane, TEXT_AREA_VERTICAL_MARGIN, TEXT_AREA_HORIZONTAL_MARGIN);


		//----  Button panel

		JPanel buttonPanel = new JPanel(new GridLayout(1, 0, BUTTON_GAP, 0));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));

		// Buttons
		buttons = new HashMap<>();
		for (Action command : commands)
		{
			JButton button = new FButton(command);
			buttons.put((String)command.getValue(Action.ACTION_COMMAND_KEY), button);
			buttonPanel.add(button);
		}


		//----  Main panel

		GridBagLayout gridBag = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();

		JPanel mainPanel = new JPanel(gridBag);

		int gridY = 0;

		gbc.gridx = 0;
		gbc.gridy = gridY++;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(1, 0, 0, 0);
		gridBag.setConstraints(textAreaScrollPane, gbc);
		mainPanel.add(textAreaScrollPane);

		gbc.gridx = 0;
		gbc.gridy = gridY++;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(3, 0, 2, 0);
		gridBag.setConstraints(buttonPanel, gbc);
		mainPanel.add(buttonPanel);


		//----  Window

		// Set content pane
		setContentPane(mainPanel);

		// Update components
		updateComponents();

		// Dispose of window explicitly
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		// Handle window closing
		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent event)
			{
				onClose();
			}
		});

		// Prevent dialog from being resized
		setResizable(false);

		// Resize dialog to its preferred size
		pack();

		// Set location of dialog box
		Point location = locations.get(key);
		if (location == null)
			location = GuiUtils.getComponentLocation(this, owner);
		setLocation(location);

		// Set default button
		if (defaultButtonKey != null)
			getRootPane().setDefaultButton(getButton(defaultButtonKey));

	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public void setCaretToStart()
	{
		textArea.setCaretPosition(0);
	}

	//------------------------------------------------------------------

	public void setCaretToEnd()
	{
		textArea.setCaretPosition(getTextLength());
	}

	//------------------------------------------------------------------

	protected int getTextLength()
	{
		return textArea.getDocument().getLength();
	}

	//------------------------------------------------------------------

	protected String getText()
	{
		return textArea.getText();
	}

	//------------------------------------------------------------------

	protected JTextArea getTextArea()
	{
		return textArea;
	}

	//------------------------------------------------------------------

	protected JButton getButton(String key)
	{
		return buttons.get(key);
	}

	//------------------------------------------------------------------

	protected void setTextAreaAttributes()
	{
		// do nothing
	}

	//------------------------------------------------------------------

	protected void updateComponents()
	{
		// do nothing
	}

	//------------------------------------------------------------------

	protected void onClose()
	{
		locations.put(key, getLocation());
		setVisible(false);
		dispose();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class variables
////////////////////////////////////////////////////////////////////////

	private static	Map<String, Point>	locations	= new Hashtable<>();

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	String					key;
	private	JTextArea				textArea;
	private	Map<String, JButton>	buttons;

}

//----------------------------------------------------------------------
