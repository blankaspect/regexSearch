/*====================================================================*\

App.java

Application class.

\*====================================================================*/


// IMPORTS


import java.io.File;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import uk.org.blankaspect.exception.AppException;
import uk.org.blankaspect.exception.ExceptionUtilities;
import uk.org.blankaspect.exception.FileException;

import uk.org.blankaspect.gui.TextRendering;

import uk.org.blankaspect.textfield.TextFieldUtilities;

import uk.org.blankaspect.util.CalendarTime;
import uk.org.blankaspect.util.NoYes;
import uk.org.blankaspect.util.ResourceProperties;

//----------------------------------------------------------------------


// APPLICATION CLASS


class App
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

    public static final     String  SHORT_NAME  = "RegexSearch";
    public static final     String  LONG_NAME   = "Regular-expression search tool";
    public static final     String  NAME_KEY    = "regexSearch";

    private static final    String  DEBUG_PROPERTY_KEY      = "app.debug";
    private static final    String  VERSION_PROPERTY_KEY    = "version";
    private static final    String  BUILD_PROPERTY_KEY      = "build";
    private static final    String  RELEASE_PROPERTY_KEY    = "release";

    private static final    String  BUILD_PROPERTIES_PATHNAME   = "resources/build.properties";

    private static final    String  DEBUG_STR               = " Debug";
    private static final    String  CONFIG_ERROR_STR        = "Configuration error";
    private static final    String  LAF_ERROR1_STR          = "Look-and-feel: ";
    private static final    String  LAF_ERROR2_STR          = "\nThe look-and-feel is not installed.";
    private static final    String  SEARCH_PARAMS_STR       = "Search parameters";
    private static final    String  READ_SEARCH_PARAMS_STR  = "Read search parameters";

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

        FILE_DOES_NOT_EXIST
        ( "The file does not exist." );

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
//  Member classes : inner classes
////////////////////////////////////////////////////////////////////////


    // INITIALISATION CLASS


    /**
     * The run() method of this class creates the main window and performs the remaining initialisation of
     * the application from the event-dispatching thread.
     */

    private class DoInitialisation
        implements Runnable
    {

    ////////////////////////////////////////////////////////////////////
    //  Constructors
    ////////////////////////////////////////////////////////////////////

        private DoInitialisation( )
        {
        }

        //--------------------------------------------------------------

    ////////////////////////////////////////////////////////////////////
    //  Instance methods : Runnable interface
    ////////////////////////////////////////////////////////////////////

        public void run( )
        {
            // Create main window
            mainWindow = new MainWindow( );

            // Create control dialog
            mainWindow.openControlDialog( );

            // Read search parameters
            try
            {
                File file = AppConfig.getInstance( ).getDefaultSearchParamsFile( );
                if ( file != null )
                {
                    if ( !file.isFile( ) )
                        throw new FileException( ErrorId.FILE_DOES_NOT_EXIST, file );
                    searchParams = new SearchParameters( file );
                    mainWindow.getControlDialog( ).updateComponents( );
                }
            }
            catch ( AppException e )
            {
                showErrorMessage( SHORT_NAME + " | " + SEARCH_PARAMS_STR, e );
            }
        }

        //--------------------------------------------------------------

    }

    //==================================================================

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

    private App( )
    {
    }

    //------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

    public static void main( String[] args )
    {
        getInstance( ).init( );
    }

    //------------------------------------------------------------------

    public static App getInstance( )
    {
        if ( instance == null )
            instance = new App( );
        return instance;
    }

    //------------------------------------------------------------------

    public static boolean isDebug( )
    {
        return debug;
    }

    //------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

    public SearchParameters getSearchParams( )
    {
        return searchParams;
    }

    //------------------------------------------------------------------

    public TextSearcher getTextSearcher( )
    {
        return textSearcher;
    }

    //------------------------------------------------------------------

    public MainWindow getMainWindow( )
    {
        return mainWindow;
    }

    //------------------------------------------------------------------

    public String getVersionString( )
    {
        StringBuilder buffer = new StringBuilder( 32 );
        String str = buildProperties.get( VERSION_PROPERTY_KEY );
        if ( str != null )
            buffer.append( str );

        str = buildProperties.get( RELEASE_PROPERTY_KEY );
        if ( str == null )
        {
            long time = System.currentTimeMillis( );
            if ( buffer.length( ) > 0 )
                buffer.append( ' ' );
            buffer.append( 'b' );
            buffer.append( CalendarTime.dateToString( time ) );
            buffer.append( '-' );
            buffer.append( CalendarTime.hoursMinsToString( time ) );
        }
        else
        {
            NoYes release = NoYes.forKey( str );
            if ( (release == null) || !release.toBoolean( ) )
            {
                str = buildProperties.get( BUILD_PROPERTY_KEY );
                if ( str != null )
                {
                    if ( buffer.length( ) > 0 )
                        buffer.append( ' ' );
                    buffer.append( str );
                }
            }
        }

        if ( debug )
            buffer.append( DEBUG_STR );

        return buffer.toString( );
    }

    //------------------------------------------------------------------

    public void showWarningMessage( String titleStr,
                                    Object message )
    {
        showMessageDialog( titleStr, message, JOptionPane.WARNING_MESSAGE );
    }

    //------------------------------------------------------------------

    public void showErrorMessage( String titleStr,
                                  Object message )
    {
        showMessageDialog( titleStr, message, JOptionPane.ERROR_MESSAGE );
    }

    //------------------------------------------------------------------

    public void showMessageDialog( String titleStr,
                                   Object message,
                                   int    messageKind )
    {
        JOptionPane.showMessageDialog( mainWindow, message, titleStr, messageKind );
    }

    //------------------------------------------------------------------

    public void openSearchParams( File file )
        throws AppException
    {
        SearchParameters newSearchParams = new SearchParameters( );
        TaskProgressDialog.showDialog( mainWindow, READ_SEARCH_PARAMS_STR,
                                       new Task.ReadSearchParams( newSearchParams, file ) );
        searchParams = newSearchParams;
    }

    //------------------------------------------------------------------

    private void init( )
    {
        // Set runtime debug flag
        debug = (System.getProperty( DEBUG_PROPERTY_KEY ) != null);

        // Read build properties
        buildProperties = new ResourceProperties( BUILD_PROPERTIES_PATHNAME, getClass( ) );

        // Initialise instance variables
        searchParams = new SearchParameters( );
        textSearcher = new TextSearcher( );

        // Read configuration
        AppConfig config = AppConfig.getInstance( );
        config.read( );

        // Set UNIX style for pathnames in file exceptions
        ExceptionUtilities.setUnixStyle( config.isShowUnixPathnames( ) );

        // Set text antialiasing
        TextRendering.setAntialiasing( config.getTextAntialiasing( ) );

        // Set look-and-feel
        String lookAndFeelName = config.getLookAndFeel( );
        for ( UIManager.LookAndFeelInfo lookAndFeelInfo : UIManager.getInstalledLookAndFeels( ) )
        {
            if ( lookAndFeelInfo.getName( ).equals( lookAndFeelName ) )
            {
                try
                {
                    UIManager.setLookAndFeel( lookAndFeelInfo.getClassName( ) );
                }
                catch ( Exception e )
                {
                    // ignore
                }
                lookAndFeelName = null;
                break;
            }
        }
        if ( lookAndFeelName != null )
            showWarningMessage( SHORT_NAME + " | " + CONFIG_ERROR_STR,
                                LAF_ERROR1_STR + lookAndFeelName + LAF_ERROR2_STR );

        // Select all text when a text field gains focus
        if ( config.isSelectTextOnFocusGained( ) )
            TextFieldUtilities.selectAllOnFocusGained( );

        // Perform remaining initialisation from event-dispatching thread
        SwingUtilities.invokeLater( new DoInitialisation( ) );
    }

    //------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class variables
////////////////////////////////////////////////////////////////////////

    private static  App     instance;
    private static  boolean debug;

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

    private ResourceProperties  buildProperties;
    private SearchParameters    searchParams;
    private TextSearcher        textSearcher;
    private MainWindow          mainWindow;

}

//----------------------------------------------------------------------
