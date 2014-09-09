/*====================================================================*\

Util.java

Utility methods class.

\*====================================================================*/


// IMPORTS


import java.awt.Color;
import java.awt.Component;
import java.awt.Toolkit;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;

import java.awt.event.MouseEvent;

import java.io.File;
import java.io.IOException;

import javax.swing.JPopupMenu;
import javax.swing.UIManager;

import uk.org.blankaspect.exception.AppException;

import uk.org.blankaspect.util.ColourUtilities;
import uk.org.blankaspect.util.PropertiesPathname;
import uk.org.blankaspect.util.SystemUtilities;

//----------------------------------------------------------------------


// UTILITY METHODS CLASS


class Util
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

    private static final    String  USER_HOME_PREFIX            = "~";
    private static final    String  FAILED_TO_GET_PATHNAME_STR  = "Failed to get the canonical pathname " +
                                                                    "for the file or directory.";

    private static final    String  PANEL_BACKGROUND_COLOUR_KEY = "Panel.background";

////////////////////////////////////////////////////////////////////////
//  Enumerated types
////////////////////////////////////////////////////////////////////////


    // ERROR IDENTIFIERS


    private enum ErrorId
        implements AppException.Id
    {

    ////////////////////////////////////////////////////////////////////
    //  Constants
    ////////////////////////////////////////////////////////////////////

        CLIPBOARD_IS_UNAVAILABLE
        ( "The clipboard is currently unavailable." ),

        FAILED_TO_GET_CLIPBOARD_DATA
        ( "Failed to get data from the clipboard." ),

        NO_TEXT_ON_CLIPBOARD
        ( "There is no text on the clipboard." );

    ////////////////////////////////////////////////////////////////////
    //  Constructors
    ////////////////////////////////////////////////////////////////////

        private ErrorId( String message )
        {
            this.message = message;
        }

        //--------------------------------------------------------------

    ////////////////////////////////////////////////////////////////////
    //  Instance methods : AppException.Id interface
    ////////////////////////////////////////////////////////////////////

        public String getMessage( )
        {
            return message;
        }

        //--------------------------------------------------------------

    ////////////////////////////////////////////////////////////////////
    //  Instance variables
    ////////////////////////////////////////////////////////////////////

        private String  message;

    }

    //==================================================================

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

    private Util( )
    {
    }

    //------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

    public static int indexOf( Object   target,
                               Object[] values )
    {
        for ( int i = 0; i < values.length; ++i )
        {
            if ( values[i].equals( target ) )
                return i;
        }
        return -1;
    }

    //------------------------------------------------------------------

    public static String uniqueCharsString( String str )
    {
        StringBuilder buffer = new StringBuilder( str.length( ) );
        for ( int i = str.length( ) - 1; i >= 0; --i )
        {
            char ch = str.charAt( i );
            if ( str.substring( 0, i ).indexOf( ch ) < 0 )
                buffer.append( ch );
        }
        return buffer.reverse( ).toString( );
    }

    //------------------------------------------------------------------

    public static char getFileSeparatorChar( )
    {
        return ( AppConfig.getInstance( ).isShowUnixPathnames( ) ? '/' : File.separatorChar );
    }

    //------------------------------------------------------------------

    public static String getPathname( File file )
    {
        return getPathname( file, AppConfig.getInstance( ).isShowUnixPathnames( ) );
    }

    //------------------------------------------------------------------

    public static String getPathname( File    file,
                                      boolean unixStyle )
    {
        String pathname = null;
        if ( file != null )
        {
            try
            {
                try
                {
                    pathname = file.getCanonicalPath( );
                }
                catch ( Exception e )
                {
                    System.err.println( file.getPath( ) );
                    System.err.println( FAILED_TO_GET_PATHNAME_STR );
                    System.err.println( "(" + e + ")" );
                    pathname = file.getAbsolutePath( );
                }
            }
            catch ( SecurityException e )
            {
                System.err.println( e );
                pathname = file.getPath( );
            }

            if ( unixStyle )
            {
                try
                {
                    String userHome = SystemUtilities.getUserHomePathname( );
                    if ( (userHome != null) && pathname.startsWith( userHome ) )
                        pathname = USER_HOME_PREFIX + pathname.substring( userHome.length( ) );
                }
                catch ( SecurityException e )
                {
                    // ignore
                }
                pathname = pathname.replace( File.separatorChar, '/' );
            }
        }
        return pathname;
    }

    //------------------------------------------------------------------

    public static String getPropertiesPathname( )
    {
        String pathname = PropertiesPathname.getPathname( );
        if ( pathname != null )
            pathname += App.NAME_KEY;
        return pathname;
    }

    //------------------------------------------------------------------

    public static File appendSuffix( File   file,
                                     String suffix )
    {
        String filename = file.getName( );
        if ( !filename.isEmpty( ) && (filename.indexOf( '.' ) < 0) )
            file = new File( file.getParentFile( ), filename + suffix );
        return file;
    }

    //------------------------------------------------------------------

    public static String[] getOptionStrings( String... optionStrs )
    {
        String[] strs = new String[optionStrs.length + 1];
        System.arraycopy( optionStrs, 0, strs, 0, optionStrs.length );
        strs[optionStrs.length] = AppConstants.CANCEL_STR;
        return strs;
    }

    //------------------------------------------------------------------

    public static String getClipboardText( )
        throws AppException
    {
        try
        {
            return (String)Toolkit.getDefaultToolkit( ).getSystemClipboard( ).getContents( null ).
                                                                getTransferData( DataFlavor.stringFlavor );
        }
        catch ( IllegalStateException e )
        {
            throw new AppException( ErrorId.CLIPBOARD_IS_UNAVAILABLE, e );
        }
        catch ( UnsupportedFlavorException e )
        {
            throw new AppException( ErrorId.NO_TEXT_ON_CLIPBOARD );
        }
        catch ( IOException e )
        {
            throw new AppException( ErrorId.FAILED_TO_GET_CLIPBOARD_DATA, e );
        }
    }

    //------------------------------------------------------------------

    public static void putClipboardText( String text )
        throws AppException
    {
        try
        {
            StringSelection selection = new StringSelection( text );
            Toolkit.getDefaultToolkit( ).getSystemClipboard( ).setContents( selection, selection );
        }
        catch ( IllegalStateException e )
        {
            throw new AppException( ErrorId.CLIPBOARD_IS_UNAVAILABLE, e );
        }
    }

    //------------------------------------------------------------------

    public static void showContextMenu( JPopupMenu contextMenu,
                                        Component  invoker,
                                        MouseEvent event )
    {
        if ( event == null )
            contextMenu.show( invoker, 0, 0 );
        else
            contextMenu.show( event.getComponent( ), event.getX( ), event.getY( ) );
    }

    //------------------------------------------------------------------

    public static Color getDisabledTextColour( )
    {
        Color colour = UIManager.getColor( PANEL_BACKGROUND_COLOUR_KEY );
        return ( (colour == null) ? AppConstants.DISABLED_TEXT_COLOUR
                                  : ColourUtilities.scaleBrightness( colour, 0.75f ) );
    }

    //------------------------------------------------------------------

}

//----------------------------------------------------------------------
