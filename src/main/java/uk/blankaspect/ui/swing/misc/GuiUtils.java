/*====================================================================*\

GuiUtils.java

Class: GUI utility methods.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.swing.misc;

//----------------------------------------------------------------------


// IMPORTS


import java.awt.AWTError;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.IllegalComponentStateException;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;

import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SwingUtilities;

import javax.swing.text.JTextComponent;

import uk.blankaspect.common.exception.UnexpectedRuntimeException;

import uk.blankaspect.common.geometry.VHPos;

import uk.blankaspect.ui.swing.colour.Colours;

//----------------------------------------------------------------------


// CLASS: GUI UTILITY METHODS


public class GuiUtils
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	private static final	int	DEFAULT_BORDER_PADDING	= 6;

	private static final	int	DEFAULT_TEXT_COMPONENT_VERTICAL_MARGIN	= 2;
	private static final	int	DEFAULT_TEXT_COMPONENT_HORIZONAL_MARGIN	= 4;

	private static final	int	DEFAULT_CORNER_SIZE	= 24;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	private GuiUtils()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static Window getWindow(Component component)
	{
		return (component == null)
						? null
						: (component instanceof Window window)
								? window
								: SwingUtilities.getWindowAncestor(component);
	}

	//------------------------------------------------------------------

	public static Toolkit getToolkit(Component component)
	{
		Toolkit toolkit = null;
		if (component != null)
			toolkit = component.getToolkit();
		try
		{
			if (toolkit == null)
				toolkit = Toolkit.getDefaultToolkit();
		}
		catch (AWTError e)
		{
			// ignore
		}
		return toolkit;
	}

	//------------------------------------------------------------------

	public static GraphicsConfiguration getGraphicsConfiguration(Component component)
	{
		GraphicsConfiguration graphicsConfig = null;
		if (component != null)
			graphicsConfig = component.getGraphicsConfiguration();
		try
		{
			if (graphicsConfig == null)
			{
				graphicsConfig =
						GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
			}
		}
		catch (HeadlessException e)
		{
			// ignore
		}
		return graphicsConfig;
	}

	//------------------------------------------------------------------

	public static Insets getScreenInsets(Component component)
	{
		Insets insets = null;
		Toolkit toolkit = getToolkit(component);
		if (toolkit != null)
		{
			GraphicsConfiguration graphicsConfig = getGraphicsConfiguration(component);
			if (graphicsConfig != null)
				insets = toolkit.getScreenInsets(graphicsConfig);
		}
		return insets;
	}

	//------------------------------------------------------------------

	public static List<Rectangle> getScreenBounds(Component component)
	{
		List<Rectangle> screenRects = new ArrayList<>();
		Toolkit toolkit = getToolkit(component);
		try
		{
			for (GraphicsDevice graphicsDevice : GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices())
			{
				for (GraphicsConfiguration graphicsConfig : graphicsDevice.getConfigurations())
				{
					Rectangle screenRect = graphicsConfig.getBounds();
					if (toolkit != null)
					{
						Insets insets = toolkit.getScreenInsets(graphicsConfig);
						screenRect.x += insets.left;
						screenRect.y += insets.top;
						screenRect.width -= insets.left + insets.right;
						screenRect.height -= insets.top + insets.bottom;
					}
					screenRects.add(screenRect);
				}
			}
		}
		catch (HeadlessException e)
		{
			// ignore
		}
		return screenRects;
	}

	//------------------------------------------------------------------

	public static Rectangle getComponentScreenBounds(Component component)
	{
		List<Rectangle> screenRects = getScreenBounds(component);
		Rectangle screenRect = getMaxIntersection(screenRects, component.getBounds());
		return (screenRect == null)
							? screenRects.isEmpty()
									? new Rectangle()
									: new Rectangle(screenRects.get(0))
							: screenRect;
	}

	//------------------------------------------------------------------

	public static Rectangle getVirtualScreenBounds()
	{
		Rectangle virtualBounds = new Rectangle();
		try
		{
			GraphicsDevice[] graphicsDevices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
			for (GraphicsDevice graphicsDevice : graphicsDevices)
			{
				for (GraphicsConfiguration graphicsConfig : graphicsDevice.getConfigurations())
					virtualBounds = virtualBounds.union(graphicsConfig.getBounds());
			}
		}
		catch (HeadlessException e)
		{
			// ignore
		}
		return virtualBounds;
	}

	//------------------------------------------------------------------

	public static Rectangle getVirtualScreenBounds(Component component)
	{
		if (component == null)
			return getVirtualScreenBounds();

		Rectangle virtualBounds = new Rectangle();
		for (Rectangle screenRect : getScreenBounds(component))
			virtualBounds = virtualBounds.union(screenRect);
		return virtualBounds;
	}

	//------------------------------------------------------------------

	public static Point getLocationWithinScreen(Component component,
												Point     point)
	{
		return getLocationWithinScreen(component, point, CornerKind.TOP_CORNERS, DEFAULT_CORNER_SIZE);
	}

	//------------------------------------------------------------------

	public static Point getLocationWithinScreen(Component    component,
												Point        point,
												CornerKind[] cornerKinds,
												int          cornerSize)
	{
		if ((component == null) || (point == null) || ((cornerKinds != null) && (cornerSize < 1)))
			throw new IllegalArgumentException();

		Rectangle rect = new Rectangle(point, component.getSize());
		List<Rectangle> screenRects = getScreenBounds(component);
		if ((cornerKinds == null) || (cornerKinds.length == 0))
		{
			for (Rectangle screenRect : screenRects)
			{
				if (screenRect.intersects(rect))
					return rect.getLocation();
			}
		}
		else
		{
			for (CornerKind cornerKind : cornerKinds)
			{
				Rectangle cornerRect = cornerKind.getRectangle(rect, cornerSize);
				for (Rectangle screenRect : screenRects)
				{
					if (screenRect.contains(cornerRect))
						return rect.getLocation();
				}
			}
			for (CornerKind cornerKind : cornerKinds)
			{
				Rectangle cornerRect = cornerKind.getRectangle(rect, cornerSize);
				for (Rectangle screenRect : screenRects)
				{
					if (screenRect.intersects(cornerRect))
						return cornerKind.getLocation(rect, screenRect, cornerSize);
				}
			}
		}
		return screenRects.isEmpty() ? new Point() : screenRects.get(0).getLocation();
	}

	//------------------------------------------------------------------

	public static Point getComponentLocation(Component component,
											 Point     point)
	{
		Rectangle screenRect = getComponentScreenBounds(component);
		return new Point(Math.max(screenRect.x, Math.min(point.x, screenRect.x + screenRect.width - component.getWidth())),
						 Math.max(screenRect.y, Math.min(point.y, screenRect.y + screenRect.height - component.getHeight())));
	}

	//------------------------------------------------------------------

	public static Point getComponentLocation(Component component,
											 Component relativeLocator)
	{
		return getComponentLocation(component, relativeLocator, VHPos.CENTRE_CENTRE);
	}

	//------------------------------------------------------------------

	public static Point getComponentLocation(Component component,
											 Component relativeLocator,
											 VHPos     pos)
	{
		return getComponentLocation(component, (relativeLocator == null) ? null : relativeLocator.getBounds(), pos);
	}

	//------------------------------------------------------------------

	public static Point getComponentLocation(Component component)
	{
		return getComponentLocation(component, VHPos.CENTRE_CENTRE);
	}

	//------------------------------------------------------------------

	public static Point getComponentLocation(Component component,
											 VHPos     pos)
	{
		return getComponentLocation(component, (Rectangle)null, pos);
	}

	//------------------------------------------------------------------

	public static Point getComponentLocation(Component component,
											 Rectangle rect)
	{
		return getComponentLocation(component, rect, VHPos.CENTRE_CENTRE);
	}

	//------------------------------------------------------------------

	public static Point getComponentLocation(Component component,
											 Rectangle rect,
											 VHPos     pos)
	{
		// Get screen bounds
		Rectangle screenRect = getComponentScreenBounds(component);

		// If no reference rectangle, use screen bounds
		if (rect == null)
			rect = screenRect;

		// Get dimensions of component
		int width = component.getWidth();
		int height = component.getHeight();

		// Calculate x coordinate
		int x1 = rect.x;
		int dx = rect.width - width;
		int x = 0;
		switch (pos.getH())
		{
		case LEFT:
			x = x1;
			break;

		case CENTRE:
			x = x1 + dx / 2;
			break;

		case RIGHT:
			x = x1 + dx;
			break;
		}

		// Calculate y coordinate
		int y1 = rect.y;
		int dy = rect.height - height;
		int y = 0;
		switch (pos.getV())
		{
		case TOP:
			y = y1;
			break;

		case CENTRE:
			y = y1 + dy / 2;
			break;

		case BOTTOM:
			y = y1 + dy;
			break;
		}

		// Return location
		return new Point(Math.max(screenRect.x, Math.min(x, screenRect.x + screenRect.width - width)),
						 Math.max(screenRect.y, Math.min(y, screenRect.y + screenRect.height - height)));
	}

	//------------------------------------------------------------------

	public static void restoreFrame(JFrame frame)
	{
		try
		{
			frame.setExtendedState(JFrame.NORMAL);
		}
		catch (IllegalComponentStateException e)
		{
			// ignore
		}
	}

	//------------------------------------------------------------------

	public static Point getFrameLocation(JFrame frame)
	{
		Point location = null;
		try
		{
			frame.setExtendedState(JFrame.NORMAL);
			location = frame.getLocation();
		}
		catch (IllegalComponentStateException e)
		{
			// ignore
		}
		return location;
	}

	//------------------------------------------------------------------

	public static Dimension getFrameSize(JFrame frame)
	{
		Dimension size = null;
		try
		{
			frame.setExtendedState(JFrame.NORMAL);
			size = frame.getSize();
		}
		catch (IllegalComponentStateException e)
		{
			// ignore
		}
		return size;
	}

	//------------------------------------------------------------------

	public static void setAllEnabled(Component component,
									 boolean   enabled)
	{
		if (component instanceof Container container)
		{
			for (Component child : container.getComponents())
				setAllEnabled(child, enabled);
		}
		if ((component instanceof AbstractButton button) && (button.getAction() != null))
			button.getAction().setEnabled(enabled);
		else
			component.setEnabled(enabled);
	}

	//------------------------------------------------------------------

	public static Graphics2D copyGraphicsContext(Graphics gr)
	{
		if (gr.create() instanceof Graphics2D gr2d)
			return gr2d;
		throw new UnexpectedRuntimeException("Graphics2D expected");
	}

	//------------------------------------------------------------------

	public static boolean containsFocus(Window    window,
										Component component)
	{
		if (window != null)
		{
			Component focusOwner = window.getFocusOwner();
			if (focusOwner != null)
			{
				while (component != null)
				{
					if (component == focusOwner)
						return true;
					component = component.getParent();
				}
			}
		}
		return false;
	}

	//------------------------------------------------------------------

	public static boolean setFocus(JComponent component)
	{
		if (component.requestFocusInWindow())
		{
			if (component instanceof JSpinner spinner)
				component = spinner.getEditor();
			if (component instanceof JTextComponent textComponent)
				textComponent.setCaretPosition(textComponent.getDocument().getLength());
			return true;
		}
		return false;
	}

	//------------------------------------------------------------------

	public static Box.Filler createFiller()
	{
		return createFiller(1, 1);
	}

	//------------------------------------------------------------------

	public static Box.Filler createFiller(int width,
										  int height)
	{
		Dimension size = new Dimension(width, height);
		return new Box.Filler(size, size, size);
	}

	//------------------------------------------------------------------

	public static void setPaddedLineBorder(JComponent component)
	{
		setPaddedLineBorder(component, Colours.LINE_BORDER);
	}

	//------------------------------------------------------------------

	public static void setPaddedLineBorder(JComponent component,
										   int        padding)
	{
		setPaddedLineBorder(component, padding, Colours.LINE_BORDER);
	}

	//------------------------------------------------------------------

	public static void setPaddedLineBorder(JComponent component,
										   int        vertical,
										   int        horizontal)
	{
		setPaddedLineBorder(component, vertical, horizontal, Colours.LINE_BORDER);
	}

	//------------------------------------------------------------------

	public static void setPaddedLineBorder(JComponent component,
										   int        top,
										   int        left,
										   int        bottom,
										   int        right)
	{
		setPaddedLineBorder(component, top, left, bottom, right, Colours.LINE_BORDER);
	}

	//------------------------------------------------------------------

	public static void setPaddedLineBorder(JComponent component,
										   Color      colour)
	{
		setPaddedLineBorder(component, DEFAULT_BORDER_PADDING, DEFAULT_BORDER_PADDING, DEFAULT_BORDER_PADDING,
							DEFAULT_BORDER_PADDING, colour);
	}

	//------------------------------------------------------------------

	public static void setPaddedLineBorder(JComponent component,
										   int        padding,
										   Color      colour)
	{
		setPaddedLineBorder(component, padding, padding, padding, padding, colour);
	}

	//------------------------------------------------------------------

	public static void setPaddedLineBorder(JComponent component,
										   int        vertical,
										   int        horizontal,
										   Color      colour)
	{
		setPaddedLineBorder(component, vertical, horizontal, vertical, horizontal, colour);
	}

	//------------------------------------------------------------------

	public static void setPaddedLineBorder(JComponent component,
										   int        top,
										   int        left,
										   int        bottom,
										   int        right,
										   Color      colour)
	{
		component.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(colour),
															   BorderFactory.createEmptyBorder(top, left, bottom, right)));
	}

	//------------------------------------------------------------------

	public static void setPaddedRaisedBevelBorder(JComponent component)
	{
		setPaddedRaisedBevelBorder(component, DEFAULT_BORDER_PADDING, DEFAULT_BORDER_PADDING, DEFAULT_BORDER_PADDING,
								   DEFAULT_BORDER_PADDING);
	}

	//------------------------------------------------------------------

	public static void setPaddedRaisedBevelBorder(JComponent component,
												  int        padding)
	{
		setPaddedRaisedBevelBorder(component, padding, padding, padding, padding);
	}

	//------------------------------------------------------------------

	public static void setPaddedRaisedBevelBorder(JComponent component,
												  int        vertical,
												  int        horizontal)
	{
		setPaddedRaisedBevelBorder(component, vertical, horizontal, vertical, horizontal);
	}

	//------------------------------------------------------------------

	public static void setPaddedRaisedBevelBorder(JComponent component,
												  int        top,
												  int        left,
												  int        bottom,
												  int        right)
	{
		component.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(),
															   BorderFactory.createEmptyBorder(top, left, bottom, right)));
	}

	//------------------------------------------------------------------

	public static void setViewportBorder(JScrollPane scrollPane,
										 int         vertical,
										 int         horizontal)
	{
		scrollPane.setViewportBorder(BorderFactory.createMatteBorder(vertical, horizontal, vertical, horizontal,
																	 scrollPane.getViewport().getView().getBackground()));
	}

	//------------------------------------------------------------------

	public static void setTextComponentMargins(JTextComponent component)
	{
		setTextComponentMargins(component, DEFAULT_TEXT_COMPONENT_VERTICAL_MARGIN, DEFAULT_TEXT_COMPONENT_HORIZONAL_MARGIN);
	}

	//------------------------------------------------------------------

	public static void setTextComponentMargins(JTextComponent component,
											   int            vertical,
											   int            horizontal)
	{
		Insets margins = component.getMargin();
		margins.set(Math.max(vertical, margins.top), Math.max(horizontal, margins.left),
					Math.max(vertical, margins.bottom), Math.max(horizontal, margins.right));
		component.setMargin(margins);
	}

	//------------------------------------------------------------------

	public static void updatePreferredSize(List<Component> components)
	{
		// Get maximum preferred width and height of components
		int maxWidth = 0;
		int maxHeight = 0;
		for (int i = 0; i < components.size(); i++)
		{
			Component component = components.get(i);

			int width = component.getPreferredSize().width;
			if (maxWidth < width)
				maxWidth = width;

			int height = component.getPreferredSize().height;
			if (maxHeight < height)
				maxHeight = height;
		}

		// Set preferred size of components
		for (Component component : components)
			component.setPreferredSize(new Dimension(maxWidth, maxHeight));
	}

	//------------------------------------------------------------------

	private static int getIntersection(Rectangle rect1,
									   Rectangle rect2)
	{
		int ax1 = rect1.x;
		int ax2 = ax1 + rect1.width;
		int ay1 = rect1.y;
		int ay2 = ay1 + rect1.height;

		int bx1 = rect2.x;
		int bx2 = bx1 + rect2.width;
		int by1 = rect2.y;
		int by2 = by1 + rect2.height;

		return ((bx1 < ax2) && (bx2 > ax1) && (by1 < ay2) && (by2 > ay1))
							? (Math.min(ax2, bx2) - Math.max(ax1, bx1)) * (Math.min(ay2, by2) - Math.max(ay1, by1))
							: 0;
	}

	//------------------------------------------------------------------

	private static Rectangle getMaxIntersection(List<Rectangle> targetRects,
												Rectangle       rect)
	{
		int maxIntersection = 0;
		Rectangle result = null;
		for (Rectangle targetRect : targetRects)
		{
			int intersection = getIntersection(targetRect, rect);
			if (maxIntersection < intersection)
			{
				maxIntersection = intersection;
				result = targetRect;
			}
		}
		return result;
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
