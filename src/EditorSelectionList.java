/*====================================================================*\

EditorSelectionList.java

Editor selection list class.

\*====================================================================*/


// IMPORTS


import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;

import java.util.List;

import uk.org.blankaspect.gui.GuiUtilities;
import uk.org.blankaspect.gui.SingleSelectionList;
import uk.org.blankaspect.gui.TextRendering;

import uk.org.blankaspect.util.TextUtilities;

//----------------------------------------------------------------------


// EDITOR SELECTION LIST CLASS


class EditorSelectionList
    extends SingleSelectionList<String>
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

    private static final    int NUM_VIEWABLE_ROWS   = 8;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

    public EditorSelectionList( List<String> elements,
                                boolean      isPathname )
    {
        super( getNumColumns( ), NUM_VIEWABLE_ROWS, AppFont.TEXT_FIELD.getFont( ), elements );
        this.isPathname = isPathname;
    }

    //------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

    private static int getNumColumns( )
    {
        int numColumns = App.getInstance( ).getMainWindow( ).getControlDialog( ).
                                                                            getParameterEditorSize( ).width;
        return Math.min( Math.max( ParameterEditor.MIN_NUM_COLUMNS, numColumns ),
                         ParameterEditor.MAX_NUM_COLUMNS );
    }

    //------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

    @Override
    protected void drawElement( Graphics gr,
                                int      index )
    {
        // Create copy of graphics context
        gr = gr.create( );

        // Set rendering hints for text antialiasing and fractional metrics
        TextRendering.setHints( (Graphics2D)gr );

        // Get text and truncate it if it is too wide
        String text = getElement( index ).toString( );
        FontMetrics fontMetrics = gr.getFontMetrics( );
        int maxTextWidth = getWidth( ) - 2 * getHorizontalMargin( );
        if ( isPathname )
        {
            if ( fontMetrics.stringWidth( text ) > maxTextWidth )
                text = TextUtilities.getLimitedWidthPathname( text, fontMetrics, maxTextWidth,
                                                              Util.getFileSeparatorChar( ) );
        }
        else
            text = truncateText( text, fontMetrics, maxTextWidth );

        // Draw text
        int rowHeight = getRowHeight( );
        gr.setColor( getForegroundColour( index ) );
        gr.drawString( text, getHorizontalMargin( ),
                       index * rowHeight + GuiUtilities.getBaselineOffset( rowHeight, fontMetrics ) );
    }

    //------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

    private boolean isPathname;

}

//----------------------------------------------------------------------
