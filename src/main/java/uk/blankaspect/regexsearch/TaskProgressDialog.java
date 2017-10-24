/*====================================================================*\

TaskProgressDialog.java

Task progress dialog box class.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.regexsearch;

//----------------------------------------------------------------------


// IMPORTS


import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Window;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import uk.blankaspect.common.exception.AppException;

import uk.blankaspect.common.gui.FButton;
import uk.blankaspect.common.gui.GuiUtils;
import uk.blankaspect.common.gui.IProgressView;
import uk.blankaspect.common.gui.TextRendering;

import uk.blankaspect.common.misc.KeyAction;
import uk.blankaspect.common.misc.TextUtils;

//----------------------------------------------------------------------


// TASK PROGRESS DIALOG BOX CLASS


class TaskProgressDialog
	extends JDialog
	implements ActionListener, IProgressView
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	private static final	int	INFO_FIELD_HORIZONTAL_MARGIN	= 2;
	private static final	int	INFO_FIELD_WIDTH				= 480;

	// Commands
	private interface Command
	{
		String	CLOSE	= "close";
	}

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// INFORMATION FIELD CLASS


	private static class InfoField
		extends JComponent
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private InfoField()
		{
			AppFont.MAIN.apply(this);
			setPreferredSize(new Dimension(INFO_FIELD_WIDTH,
										   getFontMetrics(getFont()).getHeight()));
			setOpaque(true);
			setFocusable(false);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		protected void paintComponent(Graphics gr)
		{
			// Create copy of graphics context
			gr = gr.create();

			// Draw background
			gr.setColor(getBackground());
			gr.fillRect(0, 0, getWidth(), getHeight());

			// Draw text
			if (text != null)
			{
				// Set rendering hints for text antialiasing and fractional metrics
				TextRendering.setHints((Graphics2D)gr);

				// Draw text
				gr.setColor(Color.BLACK);
				gr.drawString(text, 0, gr.getFontMetrics().getAscent());
			}
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		public void setText(String text)
		{
			this.text = text;
			repaint();
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance fields
	////////////////////////////////////////////////////////////////////

		private	String	text;

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Member classes : inner classes
////////////////////////////////////////////////////////////////////////


	// SET INFO CLASS


	private class DoSetInfo
		implements Runnable
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private DoSetInfo(File file,
						  int  numFiles)
		{
			this.file = file;
			this.numFiles = numFiles;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : Runnable interface
	////////////////////////////////////////////////////////////////////

		public void run()
		{
			FontMetrics fontMetrics = infoField1.getFontMetrics(infoField1.getFont());
			if (file == null)
			{
				infoField1.setText(AppConstants.CLIPBOARD_STR);
				infoField2.setText(null);
			}
			else
			{
				infoField1.setText(TextUtils.getLimitedWidthPathname(Utils.getPathname(file),
																	 fontMetrics,
																	 infoField1.getWidth(),
																	 Utils.getFileSeparatorChar()));
				infoField2.setText("[ " + numFiles + " ]");
			}
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance fields
	////////////////////////////////////////////////////////////////////

		private	File	file;
		private	int		numFiles;

	}

	//==================================================================


	// WINDOW EVENT HANDLER CLASS


	private class WindowEventHandler
		extends WindowAdapter
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private WindowEventHandler()
		{
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		public void windowOpened(WindowEvent event)
		{
			Task.setProgressView((TaskProgressDialog)event.getWindow());
			Task.setException(null, true);
			Task.setCancelled(false);
			task.start();
		}

		//--------------------------------------------------------------

		@Override
		public void windowClosing(WindowEvent event)
		{
			location = getLocation();
			if (stopped)
				dispose();
			else
				Task.setCancelled(true);
		}

		//--------------------------------------------------------------

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	private TaskProgressDialog(Window owner,
							   String titleStr,
							   Task   task)
		throws AppException
	{

		// Call superclass constructor
		super(owner, titleStr, Dialog.ModalityType.APPLICATION_MODAL);

		// Set icons
		if (owner != null)
			setIconImages(owner.getIconImages());

		// Initialise instance fields
		this.task = task;


		//----  Info fields

		infoField1 = new InfoField();
		infoField2 = new InfoField();


		//----  Button panel

		JPanel buttonPanel = new JPanel(new GridLayout(1, 0, 0, 0));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));

		// Button: cancel
		cancelButton = new FButton(AppConstants.CANCEL_STR);
		cancelButton.setActionCommand(Command.CLOSE);
		cancelButton.addActionListener(this);
		buttonPanel.add(cancelButton);


		//----  Main panel

		GridBagLayout gridBag = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();

		JPanel mainPanel = new JPanel(gridBag);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));

		int gridY = 0;

		gbc.gridx = 0;
		gbc.gridy = gridY++;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(8, INFO_FIELD_HORIZONTAL_MARGIN, 3, INFO_FIELD_HORIZONTAL_MARGIN);
		gridBag.setConstraints(infoField1, gbc);
		mainPanel.add(infoField1);

		gbc.gridx = 0;
		gbc.gridy = gridY++;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(3, INFO_FIELD_HORIZONTAL_MARGIN, 8, INFO_FIELD_HORIZONTAL_MARGIN);
		gridBag.setConstraints(infoField2, gbc);
		mainPanel.add(infoField2);

		gbc.gridx = 0;
		gbc.gridy = gridY++;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(3, 0, 0, 0);
		gridBag.setConstraints(buttonPanel, gbc);
		mainPanel.add(buttonPanel);

		// Add commands to action map
		KeyAction.create(mainPanel, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
						 KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), Command.CLOSE, this);


		//----  Window

		// Set content pane
		setContentPane(mainPanel);

		// Dispose of window explicitly
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		// Handle window events
		addWindowListener(new WindowEventHandler());

		// Prevent dialog from being resized
		setResizable(false);

		// Resize dialog to its preferred size
		pack();

		// Set location of dialog box
		if (location == null)
			location = GuiUtils.getComponentLocation(this, owner);
		setLocation(location);

		// Set default button
		getRootPane().setDefaultButton(cancelButton);

		// Show dialog
		setVisible(true);

		// Throw any exception from task thread
		Task.throwIfException();

	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static void showDialog(Component parent,
								  String    titleStr,
								  Task      task)
		throws AppException
	{
		new TaskProgressDialog(GuiUtils.getWindow(parent), titleStr, task);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : ActionListener interface
////////////////////////////////////////////////////////////////////////

	public void actionPerformed(ActionEvent event)
	{
		if (event.getActionCommand().equals(Command.CLOSE))
			onClose();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : IProgressView interface
////////////////////////////////////////////////////////////////////////

	public void setInfo(String str)
	{
		// do nothing
	}

	//------------------------------------------------------------------

	public void setInfo(String str,
						File   file)
	{
		// do nothing
	}

	//------------------------------------------------------------------

	public void setProgress(int    index,
							double value)
	{
		// do nothing
	}

	//------------------------------------------------------------------

	public void waitForIdle()
	{
		EventQueue eventQueue = getToolkit().getSystemEventQueue();
		while (eventQueue.peekEvent() != null)
		{
			// do nothing
		}
	}

	//------------------------------------------------------------------

	public void close()
	{
		stopped = true;
		dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public void setInfo(File file,
						int  numFiles)
	{
		SwingUtilities.invokeLater(new DoSetInfo(file, numFiles));
	}

	//------------------------------------------------------------------

	private void onClose()
	{
		cancelButton.setEnabled(false);
		dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class fields
////////////////////////////////////////////////////////////////////////

	private static	Point	location;

////////////////////////////////////////////////////////////////////////
//  Instance fields
////////////////////////////////////////////////////////////////////////

	private	Task		task;
	private	boolean		stopped;
	private	InfoField	infoField1;
	private	InfoField	infoField2;
	private	JButton		cancelButton;

}

//----------------------------------------------------------------------
