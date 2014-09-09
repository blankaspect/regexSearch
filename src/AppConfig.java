/*====================================================================*\

AppConfig.java

Application configuration class.

\*====================================================================*/


// IMPORTS


import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.lang.reflect.Field;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.JFileChooser;
import javax.swing.UIManager;

import uk.org.blankaspect.exception.AppException;
import uk.org.blankaspect.exception.FileException;

import uk.org.blankaspect.gui.Colours;
import uk.org.blankaspect.gui.FontEx;
import uk.org.blankaspect.gui.ProgressView;
import uk.org.blankaspect.gui.TextRendering;

import uk.org.blankaspect.util.FilenameSuffixFilter;
import uk.org.blankaspect.util.FileWritingMode;
import uk.org.blankaspect.util.IntegerRange;
import uk.org.blankaspect.util.NumberUtilities;
import uk.org.blankaspect.util.Property;
import uk.org.blankaspect.util.PropertySet;
import uk.org.blankaspect.util.PropertyString;

//----------------------------------------------------------------------


// APPLICATION CONFIGURATION CLASS


class AppConfig
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

    public static final     int MIN_TEXT_VIEW_NUM_COLUMNS       = 2 * MainWindow.HIGHLIGHT_MARGIN_COLUMNS;
    public static final     int MAX_TEXT_VIEW_NUM_COLUMNS       = 256;
    public static final     int DEFAULT_TEXT_VIEW_NUM_COLUMNS   = 96;

    public static final     int MIN_TEXT_VIEW_NUM_ROWS      = 2 * MainWindow.HIGHLIGHT_MARGIN_ROWS;
    public static final     int MAX_TEXT_VIEW_NUM_ROWS      = 128;
    public static final     int DEFAULT_TEXT_VIEW_NUM_ROWS  = 24;

    public static final     int MIN_TEXT_VIEW_MAX_NUM_COLUMNS       = MIN_TEXT_VIEW_NUM_COLUMNS;
    public static final     int MAX_TEXT_VIEW_MAX_NUM_COLUMNS       = 4096;
    public static final     int DEFAULT_TEXT_VIEW_MAX_NUM_COLUMNS   = 256;

    public static final     int MIN_RESULT_AREA_NUM_ROWS    = 2;
    public static final     int MAX_RESULT_AREA_NUM_ROWS    = 32;

    public static final     int MAX_NUM_TAB_WIDTH_FILTERS   = 64;

    public static final     String  PUNCTUATION_CHARS   = "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";

    private static final    int VERSION                 = 0;
    private static final    int MIN_SUPPORTED_VERSION   = 0;
    private static final    int MAX_SUPPORTED_VERSION   = 0;

    private static final    String  CONFIG_ERROR_STR    = "Configuration error";
    private static final    String  CONFIG_DIR_KEY      = Property.APP_PREFIX + "configDir";
    private static final    String  PROPERTIES_FILENAME = App.NAME_KEY + "-properties" +
                                                                            AppConstants.XML_FILE_SUFFIX;
    private static final    String  FILENAME_BASE       = App.NAME_KEY + "-config";
    private static final    String  CONFIG_FILENAME     = FILENAME_BASE + AppConstants.XML_FILE_SUFFIX;
    private static final    String  CONFIG_OLD_FILENAME = FILENAME_BASE + "-old" +
                                                                            AppConstants.XML_FILE_SUFFIX;

    private static final    String  SAVE_CONFIGURATION_FILE_STR = "Save configuration file";
    private static final    String  WRITING_STR                 = "Writing";

    private interface Key
    {
        String  APPEARANCE                          = "appearance";
        String  BACKGROUND                          = "background";
        String  CHARACTER_ENCODING                  = "characterEncoding";
        String  COMMAND                             = "command";
        String  CONFIGURATION                       = App.NAME_KEY + "Configuration";
        String  CONTROL_DIALOG_LOCATION             = "controlDialogLocation";
        String  COPY_RESULTS_AS_LIST_FILE           = "copyResultsAsListFile";
        String  DEFAULT                             = "default";
        String  DEFAULT_SEARCH_PARAMETERS           = "defaultSearchParameters";
        String  EDITOR                              = "editor";
        String  ESCAPED_METACHARACTERS              = "escapedMetacharacters";
        String  FILE_FILTER                         = "fileFilter";
        String  FILE_WRITING_MODE                   = "fileWritingMode";
        String  FONT                                = "font";
        String  GENERAL                             = "general";
        String  HIDE_CONTROL_DIALOG_WHEN_SEARCHING  = "hideControlDialogWhenSearching";
        String  HIGHLIGHT_BACKGROUND                = "highlightBackground";
        String  HIGHLIGHT_TEXT                      = "highlightText";
        String  IGNORE_FILENAME_CASE                = "ignoreFilenameCase";
        String  LOOK_AND_FEEL                       = "lookAndFeel";
        String  MAIN_WINDOW_LOCATION                = "mainWindowLocation";
        String  PARAMETER_EDITOR_SIZE               = "parameterEditorSize";
        String  PATH                                = "path";
        String  PRESERVE_LINE_SEPARATOR             = "preserveLineSeparator";
        String  REPLACEMENT_ESCAPE_CHARACTER        = "replacementEscapeCharacter";
        String  RESULT_AREA_NUM_ROWS                = "resultAreaNumRows";
        String  SELECT_TEXT_ON_FOCUS_GAINED         = "selectTextOnFocusGained";
        String  SHOW_UNIX_PATHNAMES                 = "showUnixPathnames";
        String  TAB_SURROGATE                       = "tabSurrogate";
        String  TAB_WIDTH                           = "tabWidth";
        String  TARGET_AND_REPLACEMENT              = "targetAndReplacement";
        String  TEXT                                = "text";
        String  TEXT_ANTIALIASING                   = "textAntialiasing";
        String  TEXT_AREA_COLOUR                    = "textAreaColour";
        String  TEXT_VIEW_TEXT_ANTIALIASING         = "textViewTextAntialiasing";
        String  TEXT_VIEW_MAX_NUM_COLUMNS           = "textViewMaxNumColumns";
        String  TEXT_VIEW_VIEWABLE_SIZE             = "textViewViewableSize";
    }

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

        ERROR_READING_PROPERTIES_FILE
        ( "An error occurred when reading the properties file." ),

        NO_CONFIGURATION_FILE
        ( "No configuration file was found at the specified location." ),

        NO_VERSION_NUMBER
        ( "The configuration file does not have a version number." ),

        INVALID_VERSION_NUMBER
        ( "The version number of the configuration file is invalid." ),

        UNSUPPORTED_CONFIGURATION_FILE
        ( "The version of the configuration file (%1) is not supported by this version of " +
            App.SHORT_NAME + "." ),

        FAILED_TO_CREATE_DIRECTORY
        ( "Failed to create the directory for the configuration file." );

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
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


    // CONFIGURATION FILE CLASS


    private static class ConfigFile
        extends PropertySet
    {

    ////////////////////////////////////////////////////////////////////
    //  Constants
    ////////////////////////////////////////////////////////////////////

        private static final    String  CONFIG_FILE1_STR    = "configuration file";
        private static final    String  CONFIG_FILE2_STR    = "Configuration file";

    ////////////////////////////////////////////////////////////////////
    //  Constructors
    ////////////////////////////////////////////////////////////////////

        private ConfigFile( )
        {
        }

        //--------------------------------------------------------------

        private ConfigFile( String versionStr )
            throws AppException
        {
            super( Key.CONFIGURATION, null, versionStr );
        }

        //--------------------------------------------------------------

    ////////////////////////////////////////////////////////////////////
    //  Instance methods : overriding methods
    ////////////////////////////////////////////////////////////////////

        @Override
        public String getSourceName( )
        {
            return CONFIG_FILE2_STR;
        }

        //--------------------------------------------------------------

        @Override
        protected String getFileKindString( )
        {
            return CONFIG_FILE1_STR;
        }

        //--------------------------------------------------------------

    ////////////////////////////////////////////////////////////////////
    //  Instance methods
    ////////////////////////////////////////////////////////////////////

        public void read( File file )
            throws AppException
        {
            // Read file
            read( file, Key.CONFIGURATION );

            // Validate version number
            String versionStr = getVersionString( );
            if ( versionStr == null )
                throw new FileException( ErrorId.NO_VERSION_NUMBER, file );
            try
            {
                int version = Integer.parseInt( versionStr );
                if ( (version < MIN_SUPPORTED_VERSION) || (version > MAX_SUPPORTED_VERSION) )
                    throw new FileException( ErrorId.UNSUPPORTED_CONFIGURATION_FILE, file, versionStr );
            }
            catch ( NumberFormatException e )
            {
                throw new FileException( ErrorId.INVALID_VERSION_NUMBER, file );
            }
        }

        //--------------------------------------------------------------

    }

    //==================================================================

////////////////////////////////////////////////////////////////////////
//  Member classes : inner classes
////////////////////////////////////////////////////////////////////////


    // PROPERTY CLASS: CHARACTER ENCODING


    private class CPCharacterEncoding
        extends Property.StringProperty
    {

    ////////////////////////////////////////////////////////////////////
    //  Constructors
    ////////////////////////////////////////////////////////////////////

        private CPCharacterEncoding( )
        {
            super( concatenateKeys( Key.GENERAL, Key.CHARACTER_ENCODING ) );
        }

        //--------------------------------------------------------------

    ////////////////////////////////////////////////////////////////////
    //  Instance methods : overriding methods
    ////////////////////////////////////////////////////////////////////

        @Override
        public void parse( Input input )
        {
            value = input.getValue( );
            if ( value.isEmpty( ) )
                value = null;
        }

        //--------------------------------------------------------------

    }

    //------------------------------------------------------------------

//--////////////////////////////////////////////////////////////////////
//--//  Instance methods : associated methods in enclosing class
//--////////////////////////////////////////////////////////////////////

    public String getCharacterEncoding( )
    {
        return cpCharacterEncoding.getValue( );
    }

    //------------------------------------------------------------------

    public void setCharacterEncoding( String value )
    {
        cpCharacterEncoding.setValue( value );
    }

    //------------------------------------------------------------------

//--////////////////////////////////////////////////////////////////////
//--//  Instance variables : associated variables in enclosing class
//--////////////////////////////////////////////////////////////////////

    private CPCharacterEncoding cpCharacterEncoding = new CPCharacterEncoding( );

    //==================================================================


    // PROPERTY CLASS: ESCAPED METACHARACTERS


    private class CPEscapedMetacharacters
        extends Property.SimpleProperty<String>
    {

    ////////////////////////////////////////////////////////////////////
    //  Constructors
    ////////////////////////////////////////////////////////////////////

        private CPEscapedMetacharacters( )
        {
            super( concatenateKeys( Key.GENERAL, Key.ESCAPED_METACHARACTERS ) );
            value = "$()*+.?[\\]^{|}";
        }

        //--------------------------------------------------------------

    ////////////////////////////////////////////////////////////////////
    //  Instance methods : overriding methods
    ////////////////////////////////////////////////////////////////////

        @Override
        public void parse( Input input )
            throws AppException
        {
            String str = input.getValue( );
            for ( int i = 0; i < str.length( ); ++i )
            {
                if ( PUNCTUATION_CHARS.indexOf( str.charAt( i ) ) < 0 )
                    throw new IllegalValueException( input );
            }
            value = Util.uniqueCharsString( str );
        }

        //--------------------------------------------------------------

        @Override
        public String toString( )
        {
            return value;
        }

        //--------------------------------------------------------------

    }

    //------------------------------------------------------------------

//--////////////////////////////////////////////////////////////////////
//--//  Instance methods : associated methods in enclosing class
//--////////////////////////////////////////////////////////////////////

    public String getEscapedMetacharacters( )
    {
        return cpEscapedMetacharacters.getValue( );
    }

    //------------------------------------------------------------------

    public void setEscapedMetacharacters( String value )
    {
        cpEscapedMetacharacters.setValue( value );
    }

    //------------------------------------------------------------------

//--////////////////////////////////////////////////////////////////////
//--//  Instance variables : associated variables in enclosing class
//--////////////////////////////////////////////////////////////////////

    private CPEscapedMetacharacters cpEscapedMetacharacters = new CPEscapedMetacharacters( );

    //==================================================================


    // PROPERTY CLASS: REPLACEMENT ESCAPE CHARACTER


    private class CPReplacementEscapeChar
        extends Property.SimpleProperty<Character>
    {

    ////////////////////////////////////////////////////////////////////
    //  Constructors
    ////////////////////////////////////////////////////////////////////

        private CPReplacementEscapeChar( )
        {
            super( concatenateKeys( Key.GENERAL, Key.REPLACEMENT_ESCAPE_CHARACTER ) );
            value = new Character( '\\' );
        }

        //--------------------------------------------------------------

    ////////////////////////////////////////////////////////////////////
    //  Instance methods : overriding methods
    ////////////////////////////////////////////////////////////////////

        @Override
        public void parse( Input input )
            throws AppException
        {
            String str = input.getValue( );
            if ( str.length( ) != 1 )
                throw new IllegalValueException( input );
            char ch = str.charAt( 0 );
            if ( PUNCTUATION_CHARS.indexOf( ch ) < 0 )
                throw new IllegalValueException( input );
            value = new Character( ch );
        }

        //--------------------------------------------------------------

        @Override
        public String toString( )
        {
            return value.toString( );
        }

        //--------------------------------------------------------------

    }

    //------------------------------------------------------------------

//--////////////////////////////////////////////////////////////////////
//--//  Instance methods : associated methods in enclosing class
//--////////////////////////////////////////////////////////////////////

    public char getReplacementEscapeChar( )
    {
        return cpReplacementEscapeChar.getValue( ).charValue( );
    }

    //------------------------------------------------------------------

    public void setReplacementEscapeChar( char value )
    {
        cpReplacementEscapeChar.setValue( new Character( value ) );
    }

    //------------------------------------------------------------------

//--////////////////////////////////////////////////////////////////////
//--//  Instance variables : associated variables in enclosing class
//--////////////////////////////////////////////////////////////////////

    private CPReplacementEscapeChar cpReplacementEscapeChar = new CPReplacementEscapeChar( );

    //==================================================================


    // PROPERTY CLASS: IGNORE FILENAME CASE


    private class CPIgnoreFilenameCase
        extends Property.BooleanProperty
    {

    ////////////////////////////////////////////////////////////////////
    //  Constructors
    ////////////////////////////////////////////////////////////////////

        private CPIgnoreFilenameCase( )
        {
            super( concatenateKeys( Key.GENERAL, Key.IGNORE_FILENAME_CASE ) );
            value = Boolean.FALSE;
        }

        //--------------------------------------------------------------

    }

    //------------------------------------------------------------------

//--////////////////////////////////////////////////////////////////////
//--//  Instance methods : associated methods in enclosing class
//--////////////////////////////////////////////////////////////////////

    public boolean isIgnoreFilenameCase( )
    {
        return cpIgnoreFilenameCase.getValue( );
    }

    //------------------------------------------------------------------

    public void setIgnoreFilenameCase( boolean value )
    {
        cpIgnoreFilenameCase.setValue( value );
    }

    //------------------------------------------------------------------

//--////////////////////////////////////////////////////////////////////
//--//  Instance variables : associated variables in enclosing class
//--////////////////////////////////////////////////////////////////////

    private CPIgnoreFilenameCase    cpIgnoreFilenameCase    = new CPIgnoreFilenameCase( );

    //==================================================================


    // PROPERTY CLASS: FILE-WRITING MODE


    private class CPFileWritingMode
        extends Property.EnumProperty<FileWritingMode>
    {

    ////////////////////////////////////////////////////////////////////
    //  Constructors
    ////////////////////////////////////////////////////////////////////

        private CPFileWritingMode( )
        {
            super( concatenateKeys( Key.GENERAL, Key.FILE_WRITING_MODE ), FileWritingMode.class );
            value = FileWritingMode.USE_TEMP_FILE;
        }

        //--------------------------------------------------------------

    }

    //------------------------------------------------------------------

//--////////////////////////////////////////////////////////////////////
//--//  Instance methods : associated methods in enclosing class
//--////////////////////////////////////////////////////////////////////

    public FileWritingMode getFileWritingMode( )
    {
        return cpFileWritingMode.getValue( );
    }

    //------------------------------------------------------------------

    public void setFileWritingMode( FileWritingMode value )
    {
        cpFileWritingMode.setValue( value );
    }

    //------------------------------------------------------------------

//--////////////////////////////////////////////////////////////////////
//--//  Instance variables : associated variables in enclosing class
//--////////////////////////////////////////////////////////////////////

    private CPFileWritingMode   cpFileWritingMode   = new CPFileWritingMode( );

    //==================================================================


    // PROPERTY CLASS: PRESERVE LINE SEPARATOR


    private class CPPreserveLineSeparator
        extends Property.BooleanProperty
    {

    ////////////////////////////////////////////////////////////////////
    //  Constructors
    ////////////////////////////////////////////////////////////////////

        private CPPreserveLineSeparator( )
        {
            super( concatenateKeys( Key.GENERAL, Key.PRESERVE_LINE_SEPARATOR ) );
            value = Boolean.FALSE;
        }

        //--------------------------------------------------------------

    }

    //------------------------------------------------------------------

//--////////////////////////////////////////////////////////////////////
//--//  Instance methods : associated methods in enclosing class
//--////////////////////////////////////////////////////////////////////

    public boolean isPreserveLineSeparator( )
    {
        return cpPreserveLineSeparator.getValue( );
    }

    //------------------------------------------------------------------

    public void setPreserveLineSeparator( boolean value )
    {
        cpPreserveLineSeparator.setValue( value );
    }

    //------------------------------------------------------------------

//--////////////////////////////////////////////////////////////////////
//--//  Instance variables : associated variables in enclosing class
//--////////////////////////////////////////////////////////////////////

    private CPPreserveLineSeparator cpPreserveLineSeparator = new CPPreserveLineSeparator( );

    //==================================================================


    // PROPERTY CLASS: SHOW UNIX PATHNAMES


    private class CPShowUnixPathnames
        extends Property.BooleanProperty
    {

    ////////////////////////////////////////////////////////////////////
    //  Constructors
    ////////////////////////////////////////////////////////////////////

        private CPShowUnixPathnames( )
        {
            super( concatenateKeys( Key.GENERAL, Key.SHOW_UNIX_PATHNAMES ) );
            value = Boolean.FALSE;
        }

        //--------------------------------------------------------------

    }

    //------------------------------------------------------------------

//--////////////////////////////////////////////////////////////////////
//--//  Instance methods : associated methods in enclosing class
//--////////////////////////////////////////////////////////////////////

    public boolean isShowUnixPathnames( )
    {
        return cpShowUnixPathnames.getValue( );
    }

    //------------------------------------------------------------------

    public void setShowUnixPathnames( boolean value )
    {
        cpShowUnixPathnames.setValue( value );
    }

    //------------------------------------------------------------------

//--////////////////////////////////////////////////////////////////////
//--//  Instance variables : associated variables in enclosing class
//--////////////////////////////////////////////////////////////////////

    private CPShowUnixPathnames cpShowUnixPathnames = new CPShowUnixPathnames( );

    //==================================================================


    // PROPERTY CLASS: SELECT TEXT ON FOCUS GAINED


    private class CPSelectTextOnFocusGained
        extends Property.BooleanProperty
    {

    ////////////////////////////////////////////////////////////////////
    //  Constructors
    ////////////////////////////////////////////////////////////////////

        private CPSelectTextOnFocusGained( )
        {
            super( concatenateKeys( Key.GENERAL, Key.SELECT_TEXT_ON_FOCUS_GAINED ) );
            value = Boolean.TRUE;
        }

        //--------------------------------------------------------------

    }

    //------------------------------------------------------------------

//--////////////////////////////////////////////////////////////////////
//--//  Instance methods : associated methods in enclosing class
//--////////////////////////////////////////////////////////////////////

    public boolean isSelectTextOnFocusGained( )
    {
        return cpSelectTextOnFocusGained.getValue( );
    }

    //------------------------------------------------------------------

    public void setSelectTextOnFocusGained( boolean value )
    {
        cpSelectTextOnFocusGained.setValue( value );
    }

    //------------------------------------------------------------------

//--////////////////////////////////////////////////////////////////////
//--//  Instance variables : associated variables in enclosing class
//--////////////////////////////////////////////////////////////////////

    private CPSelectTextOnFocusGained   cpSelectTextOnFocusGained   = new CPSelectTextOnFocusGained( );

    //==================================================================


    // PROPERTY CLASS: HIDE CONTROL DIALOG WHEN SEARCHING


    private class CPHideControlDialogWhenSearching
        extends Property.BooleanProperty
    {

    ////////////////////////////////////////////////////////////////////
    //  Constructors
    ////////////////////////////////////////////////////////////////////

        private CPHideControlDialogWhenSearching( )
        {
            super( concatenateKeys( Key.GENERAL, Key.HIDE_CONTROL_DIALOG_WHEN_SEARCHING ) );
            value = Boolean.FALSE;
        }

        //--------------------------------------------------------------

    }

    //------------------------------------------------------------------

//--////////////////////////////////////////////////////////////////////
//--//  Instance methods : associated methods in enclosing class
//--////////////////////////////////////////////////////////////////////

    public boolean isHideControlDialogWhenSearching( )
    {
        return cpHideControlDialogWhenSearching.getValue( );
    }

    //------------------------------------------------------------------

    public void setHideControlDialogWhenSearching( boolean value )
    {
        cpHideControlDialogWhenSearching.setValue( value );
    }

    //------------------------------------------------------------------

//--////////////////////////////////////////////////////////////////////
//--//  Instance variables : associated variables in enclosing class
//--////////////////////////////////////////////////////////////////////

    private CPHideControlDialogWhenSearching    cpHideControlDialogWhenSearching    =
                                                                    new CPHideControlDialogWhenSearching( );

    //==================================================================


    // PROPERTY CLASS: COPY RESULTS AS LIST FILE


    private class CPCopyResultsAsListFile
        extends Property.BooleanProperty
    {

    ////////////////////////////////////////////////////////////////////
    //  Constructors
    ////////////////////////////////////////////////////////////////////

        private CPCopyResultsAsListFile( )
        {
            super( concatenateKeys( Key.GENERAL, Key.COPY_RESULTS_AS_LIST_FILE ) );
            value = Boolean.FALSE;
        }

        //--------------------------------------------------------------

    }

    //------------------------------------------------------------------

//--////////////////////////////////////////////////////////////////////
//--//  Instance methods : associated methods in enclosing class
//--////////////////////////////////////////////////////////////////////

    public boolean isCopyResultsAsListFile( )
    {
        return cpCopyResultsAsListFile.getValue( );
    }

    //------------------------------------------------------------------

    public void setCopyResultsAsListFile( boolean value )
    {
        cpCopyResultsAsListFile.setValue( value );
    }

    //------------------------------------------------------------------

//--////////////////////////////////////////////////////////////////////
//--//  Instance variables : associated variables in enclosing class
//--////////////////////////////////////////////////////////////////////

    private CPCopyResultsAsListFile cpCopyResultsAsListFile = new CPCopyResultsAsListFile( );

    //==================================================================


    // PROPERTY CLASS: MAIN WINDOW LOCATION


    private class CPMainWindowLocation
        extends Property.SimpleProperty<Point>
    {

    ////////////////////////////////////////////////////////////////////
    //  Constructors
    ////////////////////////////////////////////////////////////////////

        private CPMainWindowLocation( )
        {
            super( concatenateKeys( Key.GENERAL, Key.MAIN_WINDOW_LOCATION ) );
            value = new Point( );
        }

        //--------------------------------------------------------------

    ////////////////////////////////////////////////////////////////////
    //  Instance methods : overriding methods
    ////////////////////////////////////////////////////////////////////

        @Override
        public void parse( Input input )
            throws AppException
        {
            if ( input.getValue( ).isEmpty( ) )
                value = null;
            else
            {
                int[] outValues = input.parseIntegers( 2, null );
                value = new Point( outValues[0], outValues[1] );
            }
        }

        //--------------------------------------------------------------

        @Override
        public String toString( )
        {
            return ( (value == null) ? new String( ) : value.x + ", " + value.y );
        }

        //--------------------------------------------------------------

    }

    //------------------------------------------------------------------

//--////////////////////////////////////////////////////////////////////
//--//  Instance methods : associated methods in enclosing class
//--////////////////////////////////////////////////////////////////////

    public boolean isMainWindowLocation( )
    {
        return ( getMainWindowLocation( ) != null );
    }

    //------------------------------------------------------------------

    public Point getMainWindowLocation( )
    {
        return cpMainWindowLocation.getValue( );
    }

    //------------------------------------------------------------------

    public void setMainWindowLocation( Point value )
    {
        cpMainWindowLocation.setValue( value );
    }

    //------------------------------------------------------------------

//--////////////////////////////////////////////////////////////////////
//--//  Instance variables : associated variables in enclosing class
//--////////////////////////////////////////////////////////////////////

    private CPMainWindowLocation    cpMainWindowLocation    = new CPMainWindowLocation( );

    //==================================================================


    // PROPERTY CLASS: CONTROL DIALOG LOCATION


    private class CPControlDialogLocation
        extends Property.SimpleProperty<Point>
    {

    ////////////////////////////////////////////////////////////////////
    //  Constructors
    ////////////////////////////////////////////////////////////////////

        private CPControlDialogLocation( )
        {
            super( concatenateKeys( Key.GENERAL, Key.CONTROL_DIALOG_LOCATION ) );
        }

        //--------------------------------------------------------------

    ////////////////////////////////////////////////////////////////////
    //  Instance methods : overriding methods
    ////////////////////////////////////////////////////////////////////

        @Override
        public void parse( Input input )
            throws AppException
        {
            if ( input.getValue( ).isEmpty( ) )
                value = null;
            else
            {
                int[] outValues = input.parseIntegers( 2, null );
                value = new Point( outValues[0], outValues[1] );
            }
        }

        //--------------------------------------------------------------

        @Override
        public String toString( )
        {
            return ( (value == null) ? new String( ) : value.x + ", " + value.y );
        }

        //--------------------------------------------------------------

    }

    //------------------------------------------------------------------

//--////////////////////////////////////////////////////////////////////
//--//  Instance methods : associated methods in enclosing class
//--////////////////////////////////////////////////////////////////////

    public Point getControlDialogLocation( )
    {
        return cpControlDialogLocation.getValue( );
    }

    //------------------------------------------------------------------

    public void setControlDialogLocation( Point value )
    {
        cpControlDialogLocation.setValue( value );
    }

    //------------------------------------------------------------------

//--////////////////////////////////////////////////////////////////////
//--//  Instance variables : associated variables in enclosing class
//--////////////////////////////////////////////////////////////////////

    private CPControlDialogLocation cpControlDialogLocation = new CPControlDialogLocation( );

    //==================================================================


    // PROPERTY CLASS: LOOK-AND-FEEL


    private class CPLookAndFeel
        extends Property.StringProperty
    {

    ////////////////////////////////////////////////////////////////////
    //  Constructors
    ////////////////////////////////////////////////////////////////////

        private CPLookAndFeel( )
        {
            super( concatenateKeys( Key.APPEARANCE, Key.LOOK_AND_FEEL ) );
            value = new String( );
            for ( UIManager.LookAndFeelInfo lookAndFeelInfo : UIManager.getInstalledLookAndFeels( ) )
            {
                if ( lookAndFeelInfo.getClassName( ).
                                            equals( UIManager.getCrossPlatformLookAndFeelClassName( ) ) )
                {
                    value = lookAndFeelInfo.getName( );
                    break;
                }
            }
        }

        //--------------------------------------------------------------

    }

    //------------------------------------------------------------------

//--////////////////////////////////////////////////////////////////////
//--//  Instance methods : associated methods in enclosing class
//--////////////////////////////////////////////////////////////////////

    public String getLookAndFeel( )
    {
        return cpLookAndFeel.getValue( );
    }

    //------------------------------------------------------------------

    public void setLookAndFeel( String value )
    {
        cpLookAndFeel.setValue( value );
    }

    //------------------------------------------------------------------

//--////////////////////////////////////////////////////////////////////
//--//  Instance variables : associated variables in enclosing class
//--////////////////////////////////////////////////////////////////////

    private CPLookAndFeel   cpLookAndFeel   = new CPLookAndFeel( );

    //==================================================================


    // PROPERTY CLASS: TEXT ANTIALIASING


    private class CPTextAntialiasing
        extends Property.EnumProperty<TextRendering.Antialiasing>
    {

    ////////////////////////////////////////////////////////////////////
    //  Constructors
    ////////////////////////////////////////////////////////////////////

        private CPTextAntialiasing( )
        {
            super( concatenateKeys( Key.APPEARANCE, Key.TEXT_ANTIALIASING ),
                   TextRendering.Antialiasing.class );
            value = TextRendering.Antialiasing.DEFAULT;
        }

        //--------------------------------------------------------------

    }

    //------------------------------------------------------------------

//--////////////////////////////////////////////////////////////////////
//--//  Instance methods : associated methods in enclosing class
//--////////////////////////////////////////////////////////////////////

    public TextRendering.Antialiasing getTextAntialiasing( )
    {
        return cpTextAntialiasing.getValue( );
    }

    //------------------------------------------------------------------

    public void setTextAntialiasing( TextRendering.Antialiasing value )
    {
        cpTextAntialiasing.setValue( value );
    }

    //------------------------------------------------------------------

//--////////////////////////////////////////////////////////////////////
//--//  Instance variables : associated variables in enclosing class
//--////////////////////////////////////////////////////////////////////

    private CPTextAntialiasing  cpTextAntialiasing  = new CPTextAntialiasing( );

    //==================================================================


    // PROPERTY CLASS: PARAMETER EDITOR SIZE


    private class CPParameterEditorSize
        extends Property.SimpleProperty<Dimension>
    {

    ////////////////////////////////////////////////////////////////////
    //  Constructors
    ////////////////////////////////////////////////////////////////////

        private CPParameterEditorSize( )
        {
            super( concatenateKeys( Key.APPEARANCE, Key.PARAMETER_EDITOR_SIZE ) );
            value = new Dimension( ParameterEditor.DEFAULT_NUM_COLUMNS, ParameterEditor.DEFAULT_NUM_ROWS );
        }

        //--------------------------------------------------------------

    ////////////////////////////////////////////////////////////////////
    //  Instance methods : overriding methods
    ////////////////////////////////////////////////////////////////////

        @Override
        public void parse( Input input )
            throws AppException
        {
            IntegerRange[] ranges =
            {
                new IntegerRange( ParameterEditor.MIN_NUM_COLUMNS, ParameterEditor.MAX_NUM_COLUMNS ),
                new IntegerRange( ParameterEditor.MIN_NUM_ROWS, ParameterEditor.MAX_NUM_ROWS )
            };
            int[] outValues = input.parseIntegers( 2, ranges );
            value = new Dimension( outValues[0], outValues[1] );
        }

        //--------------------------------------------------------------

        @Override
        public String toString( )
        {
            return ( value.width + ", " + value.height );
        }

        //--------------------------------------------------------------

    }

    //------------------------------------------------------------------

//--////////////////////////////////////////////////////////////////////
//--//  Instance methods : associated methods in enclosing class
//--////////////////////////////////////////////////////////////////////

    public Dimension getParameterEditorSize( )
    {
        return cpParameterEditorSize.getValue( );
    }

    //------------------------------------------------------------------

    public void setParameterEditorSize( Dimension value )
    {
        cpParameterEditorSize.setValue( value );
    }

    //------------------------------------------------------------------

//--////////////////////////////////////////////////////////////////////
//--//  Instance variables : associated variables in enclosing class
//--////////////////////////////////////////////////////////////////////

    private CPParameterEditorSize   cpParameterEditorSize   = new CPParameterEditorSize( );

    //==================================================================


    // PROPERTY CLASS: NUMBER OF ROWS IN RESULT AREA


    private class CPResultAreaNumRows
        extends Property.IntegerProperty
    {

    ////////////////////////////////////////////////////////////////////
    //  Constructors
    ////////////////////////////////////////////////////////////////////

        private CPResultAreaNumRows( )
        {
            super( concatenateKeys( Key.APPEARANCE, Key.RESULT_AREA_NUM_ROWS ),
                   MIN_RESULT_AREA_NUM_ROWS, MAX_RESULT_AREA_NUM_ROWS );
            value = 4;
        }

        //--------------------------------------------------------------

    }

    //------------------------------------------------------------------

//--////////////////////////////////////////////////////////////////////
//--//  Instance methods : associated methods in enclosing class
//--////////////////////////////////////////////////////////////////////

    public int getResultAreaNumRows( )
    {
        return cpResultAreaNumRows.getValue( );
    }

    //------------------------------------------------------------------

    public void setResultAreaNumRows( int value )
    {
        cpResultAreaNumRows.setValue( value );
    }

    //------------------------------------------------------------------

//--////////////////////////////////////////////////////////////////////
//--//  Instance variables : associated variables in enclosing class
//--////////////////////////////////////////////////////////////////////

    private CPResultAreaNumRows cpResultAreaNumRows = new CPResultAreaNumRows( );

    //==================================================================


    // PROPERTY CLASS: TAB SURROGATE


    private class CPTabSurrogate
        extends Property.SimpleProperty<Character>
    {

    ////////////////////////////////////////////////////////////////////
    //  Constants
    ////////////////////////////////////////////////////////////////////

        private static final    int LENGTH  = 4;

    ////////////////////////////////////////////////////////////////////
    //  Constructors
    ////////////////////////////////////////////////////////////////////

        private CPTabSurrogate( )
        {
            super( concatenateKeys( Key.APPEARANCE, Key.TAB_SURROGATE ) );
            value = new Character( '\t' );
        }

        //--------------------------------------------------------------

    ////////////////////////////////////////////////////////////////////
    //  Instance methods : overriding methods
    ////////////////////////////////////////////////////////////////////

        @Override
        public void parse( Input input )
            throws AppException
        {
            String str = input.getValue( );
            if ( str.length( ) != LENGTH )
                throw new IllegalValueException( input );
            try
            {
                value = new Character( (char)Integer.parseInt( str, 16 ) );
            }
            catch ( NumberFormatException e )
            {
                throw new IllegalValueException( input );
            }
        }

        //--------------------------------------------------------------

        @Override
        public String toString( )
        {
            return NumberUtilities.uIntToHexString( value.charValue( ), LENGTH, '0' );
        }

        //--------------------------------------------------------------

    }

    //------------------------------------------------------------------

//--////////////////////////////////////////////////////////////////////
//--//  Instance methods : associated methods in enclosing class
//--////////////////////////////////////////////////////////////////////

    public char getTabSurrogate( )
    {
        return cpTabSurrogate.getValue( ).charValue( );
    }

    //------------------------------------------------------------------

    public void setTabSurrogate( char value )
    {
        cpTabSurrogate.setValue( new Character( value ) );
    }

    //------------------------------------------------------------------

//--////////////////////////////////////////////////////////////////////
//--//  Instance variables : associated variables in enclosing class
//--////////////////////////////////////////////////////////////////////

    private CPTabSurrogate  cpTabSurrogate  = new CPTabSurrogate( );

    //==================================================================


    // PROPERTY CLASS: TEXT VIEW VIEWABLE SIZE


    private class CPTextViewViewableSize
        extends Property.SimpleProperty<Dimension>
    {

    ////////////////////////////////////////////////////////////////////
    //  Constructors
    ////////////////////////////////////////////////////////////////////

        private CPTextViewViewableSize( )
        {
            super( concatenateKeys( Key.APPEARANCE, Key.TEXT_VIEW_VIEWABLE_SIZE ) );
            value = new Dimension( DEFAULT_TEXT_VIEW_NUM_COLUMNS, DEFAULT_TEXT_VIEW_NUM_ROWS );
        }

        //--------------------------------------------------------------

    ////////////////////////////////////////////////////////////////////
    //  Instance methods : overriding methods
    ////////////////////////////////////////////////////////////////////

        @Override
        public void parse( Input input )
            throws AppException
        {
            IntegerRange[] ranges =
            {
                new IntegerRange( MIN_TEXT_VIEW_NUM_COLUMNS, MAX_TEXT_VIEW_NUM_COLUMNS ),
                new IntegerRange( MIN_TEXT_VIEW_NUM_ROWS, MAX_TEXT_VIEW_NUM_ROWS )
            };
            int[] outValues = input.parseIntegers( 2, ranges );
            value = new Dimension( outValues[0], outValues[1] );
        }

        //--------------------------------------------------------------

        @Override
        public String toString( )
        {
            return ( value.width + ", " + value.height );
        }

        //--------------------------------------------------------------

    }

    //------------------------------------------------------------------

//--////////////////////////////////////////////////////////////////////
//--//  Instance methods : associated methods in enclosing class
//--////////////////////////////////////////////////////////////////////

    public Dimension getTextViewViewableSize( )
    {
        return cpTextViewViewableSize.getValue( );
    }

    //------------------------------------------------------------------

    public void setTextViewViewableSize( Dimension value )
    {
        cpTextViewViewableSize.setValue( value );
    }

    //------------------------------------------------------------------

//--////////////////////////////////////////////////////////////////////
//--//  Instance variables : associated variables in enclosing class
//--////////////////////////////////////////////////////////////////////

    private CPTextViewViewableSize  cpTextViewViewableSize  = new CPTextViewViewableSize( );

    //==================================================================


    // PROPERTY CLASS: MAXIMUM NUMBER OF COLUMNS IN TEXT VIEW


    private class CPTextViewMaxNumColumns
        extends Property.IntegerProperty
    {

    ////////////////////////////////////////////////////////////////////
    //  Constructors
    ////////////////////////////////////////////////////////////////////

        private CPTextViewMaxNumColumns( )
        {
            super( concatenateKeys( Key.APPEARANCE, Key.TEXT_VIEW_MAX_NUM_COLUMNS ),
                   MIN_TEXT_VIEW_MAX_NUM_COLUMNS, MAX_TEXT_VIEW_MAX_NUM_COLUMNS );
            value = DEFAULT_TEXT_VIEW_MAX_NUM_COLUMNS;
        }

        //--------------------------------------------------------------

    }

    //------------------------------------------------------------------

//--////////////////////////////////////////////////////////////////////
//--//  Instance methods : associated methods in enclosing class
//--////////////////////////////////////////////////////////////////////

    public int getTextViewMaxNumColumns( )
    {
        return cpTextViewMaxNumColumns.getValue( );
    }

    //------------------------------------------------------------------

    public void setTextViewMaxNumColumns( int value )
    {
        cpTextViewMaxNumColumns.setValue( value );
    }

    //------------------------------------------------------------------

//--////////////////////////////////////////////////////////////////////
//--//  Instance variables : associated variables in enclosing class
//--////////////////////////////////////////////////////////////////////

    private CPTextViewMaxNumColumns cpTextViewMaxNumColumns = new CPTextViewMaxNumColumns( );

    //==================================================================


    // PROPERTY CLASS: TEXT VIEW TEXT ANTIALIASING


    private class CPTextViewTextAntialiasing
        extends Property.EnumProperty<TextRendering.Antialiasing>
    {

    ////////////////////////////////////////////////////////////////////
    //  Constructors
    ////////////////////////////////////////////////////////////////////

        private CPTextViewTextAntialiasing( )
        {
            super( concatenateKeys( Key.APPEARANCE, Key.TEXT_VIEW_TEXT_ANTIALIASING ),
                   TextRendering.Antialiasing.class );
            value = TextRendering.Antialiasing.DEFAULT;
        }

        //--------------------------------------------------------------

    }

    //------------------------------------------------------------------

//--////////////////////////////////////////////////////////////////////
//--//  Instance methods : associated methods in enclosing class
//--////////////////////////////////////////////////////////////////////

    public TextRendering.Antialiasing getTextViewTextAntialiasing( )
    {
        return cpTextViewTextAntialiasing.getValue( );
    }

    //------------------------------------------------------------------

    public void setTextViewTextAntialiasing( TextRendering.Antialiasing value )
    {
        cpTextViewTextAntialiasing.setValue( value );
    }

    //------------------------------------------------------------------

//--////////////////////////////////////////////////////////////////////
//--//  Instance variables : associated variables in enclosing class
//--////////////////////////////////////////////////////////////////////

    private CPTextViewTextAntialiasing  cpTextViewTextAntialiasing  = new CPTextViewTextAntialiasing( );

    //==================================================================


    // PROPERTY CLASS: TEXT AREA TEXT COLOUR


    private class CPTextAreaTextColour
        extends Property.ColourProperty
    {

    ////////////////////////////////////////////////////////////////////
    //  Constructors
    ////////////////////////////////////////////////////////////////////

        private CPTextAreaTextColour( )
        {
            super( concatenateKeys( Key.APPEARANCE, Key.TEXT_AREA_COLOUR, Key.TEXT ) );
            value = Colours.TextArea.FOREGROUND.getColour( );
        }

        //--------------------------------------------------------------

    }

    //------------------------------------------------------------------

//--////////////////////////////////////////////////////////////////////
//--//  Instance methods : associated methods in enclosing class
//--////////////////////////////////////////////////////////////////////

    public Color getTextAreaTextColour( )
    {
        return cpTextAreaTextColour.getValue( );
    }

    //------------------------------------------------------------------

    public void setTextAreaTextColour( Color value )
    {
        cpTextAreaTextColour.setValue( value );
    }

    //------------------------------------------------------------------

//--////////////////////////////////////////////////////////////////////
//--//  Instance variables : associated variables in enclosing class
//--////////////////////////////////////////////////////////////////////

    private CPTextAreaTextColour    cpTextAreaTextColour    = new CPTextAreaTextColour( );

    //==================================================================


    // PROPERTY CLASS: TEXT AREA BACKGROUND COLOUR


    private class CPTextAreaBackgroundColour
        extends Property.ColourProperty
    {

    ////////////////////////////////////////////////////////////////////
    //  Constructors
    ////////////////////////////////////////////////////////////////////

        private CPTextAreaBackgroundColour( )
        {
            super( concatenateKeys( Key.APPEARANCE, Key.TEXT_AREA_COLOUR, Key.BACKGROUND ) );
            value = Colours.TextArea.BACKGROUND.getColour( );
        }

        //--------------------------------------------------------------

    }

    //------------------------------------------------------------------

//--////////////////////////////////////////////////////////////////////
//--//  Instance methods : associated methods in enclosing class
//--////////////////////////////////////////////////////////////////////

    public Color getTextAreaBackgroundColour( )
    {
        return cpTextAreaBackgroundColour.getValue( );
    }

    //------------------------------------------------------------------

    public void setTextAreaBackgroundColour( Color value )
    {
        cpTextAreaBackgroundColour.setValue( value );
    }

    //------------------------------------------------------------------

//--////////////////////////////////////////////////////////////////////
//--//  Instance variables : associated variables in enclosing class
//--////////////////////////////////////////////////////////////////////

    private CPTextAreaBackgroundColour  cpTextAreaBackgroundColour  = new CPTextAreaBackgroundColour( );

    //==================================================================


    // PROPERTY CLASS: TEXT AREA HIGHLIGHT TEXT COLOUR


    private class CPTextAreaHighlightTextColour
        extends Property.ColourProperty
    {

    ////////////////////////////////////////////////////////////////////
    //  Constructors
    ////////////////////////////////////////////////////////////////////

        private CPTextAreaHighlightTextColour( )
        {
            super( concatenateKeys( Key.APPEARANCE, Key.TEXT_AREA_COLOUR, Key.HIGHLIGHT_TEXT ) );
            value = Colours.TextArea.SELECTION_FOREGROUND.getColour( );
        }

        //--------------------------------------------------------------

    }

    //------------------------------------------------------------------

//--////////////////////////////////////////////////////////////////////
//--//  Instance methods : associated methods in enclosing class
//--////////////////////////////////////////////////////////////////////

    public Color getTextAreaHighlightTextColour( )
    {
        return cpTextAreaHighlightTextColour.getValue( );
    }

    //------------------------------------------------------------------

    public void setTextAreaHighlightTextColour( Color value )
    {
        cpTextAreaHighlightTextColour.setValue( value );
    }

    //------------------------------------------------------------------

//--////////////////////////////////////////////////////////////////////
//--//  Instance variables : associated variables in enclosing class
//--////////////////////////////////////////////////////////////////////

    private CPTextAreaHighlightTextColour   cpTextAreaHighlightTextColour   =
                                                                    new CPTextAreaHighlightTextColour( );

    //==================================================================


    // PROPERTY CLASS: TEXT AREA HIGHLIGHT BACKGROUND COLOUR


    private class CPTextAreaHighlightBackgroundColour
        extends Property.ColourProperty
    {

    ////////////////////////////////////////////////////////////////////
    //  Constructors
    ////////////////////////////////////////////////////////////////////

        private CPTextAreaHighlightBackgroundColour( )
        {
            super( concatenateKeys( Key.APPEARANCE, Key.TEXT_AREA_COLOUR, Key.HIGHLIGHT_BACKGROUND ) );
            value = Colours.TextArea.FOCUSED_SELECTION_BACKGROUND.getColour( );
        }

        //--------------------------------------------------------------

    }

    //------------------------------------------------------------------

//--////////////////////////////////////////////////////////////////////
//--//  Instance methods : associated methods in enclosing class
//--////////////////////////////////////////////////////////////////////

    public Color getTextAreaHighlightBackgroundColour( )
    {
        return cpTextAreaHighlightBackgroundColour.getValue( );
    }

    //------------------------------------------------------------------

    public void setTextAreaHighlightBackgroundColour( Color value )
    {
        cpTextAreaHighlightBackgroundColour.setValue( value );
    }

    //------------------------------------------------------------------

//--////////////////////////////////////////////////////////////////////
//--//  Instance variables : associated variables in enclosing class
//--////////////////////////////////////////////////////////////////////

    private CPTextAreaHighlightBackgroundColour cpTextAreaHighlightBackgroundColour =
                                                                new CPTextAreaHighlightBackgroundColour( );

    //==================================================================


    // PROPERTY CLASS: DEFAULT TAB WIDTH


    private class CPDefaultTabWidth
        extends Property.IntegerProperty
    {

    ////////////////////////////////////////////////////////////////////
    //  Constructors
    ////////////////////////////////////////////////////////////////////

        private CPDefaultTabWidth( )
        {
            super( concatenateKeys( Key.TAB_WIDTH, Key.DEFAULT ),
                   TextModel.MIN_TAB_WIDTH, TextModel.MAX_TAB_WIDTH );
            value = TextModel.DEFAULT_TAB_WIDTH;
        }

        //--------------------------------------------------------------

    }

    //------------------------------------------------------------------

//--////////////////////////////////////////////////////////////////////
//--//  Instance methods : associated methods in enclosing class
//--////////////////////////////////////////////////////////////////////

    public int getDefaultTabWidth( )
    {
        return cpDefaultTabWidth.getValue( );
    }

    //------------------------------------------------------------------

    public void setDefaultTabWidth( int value )
    {
        cpDefaultTabWidth.setValue( value );
    }

    //------------------------------------------------------------------

//--////////////////////////////////////////////////////////////////////
//--//  Instance variables : associated variables in enclosing class
//--////////////////////////////////////////////////////////////////////

    private CPDefaultTabWidth   cpDefaultTabWidth   = new CPDefaultTabWidth( );

    //==================================================================


    // PROPERTY CLASS: TAB-WIDTH FILTERS


    private class CPTabWidthFilters
        extends Property.PropertyList<TabWidthFilter>
    {

    ////////////////////////////////////////////////////////////////////
    //  Constructors
    ////////////////////////////////////////////////////////////////////

        private CPTabWidthFilters( )
        {
            super( concatenateKeys( Key.TAB_WIDTH, Key.FILE_FILTER ), MAX_NUM_TAB_WIDTH_FILTERS );
        }

        //--------------------------------------------------------------

    ////////////////////////////////////////////////////////////////////
    //  Instance methods : overriding methods
    ////////////////////////////////////////////////////////////////////

        @Override
        protected void parse( Input input,
                              int   index )
        {
            try
            {
                try
                {
                    TabWidthFilter filter = new TabWidthFilter( input.getValue( ) );
                    int tabWidth = filter.getTabWidth( );
                    if ( (tabWidth < TextModel.MIN_TAB_WIDTH) || (tabWidth > TextModel.MAX_TAB_WIDTH) )
                        throw new ValueOutOfBoundsException( input );
                    values.add( filter );
                }
                catch ( IllegalArgumentException e )
                {
                    throw new IllegalValueException( input );
                }
            }
            catch ( AppException e )
            {
                showWarningMessage( e );
            }
        }

        //--------------------------------------------------------------

        @Override
        protected String toString( int index )
        {
            return values.get( index ).toString( );
        }

        //--------------------------------------------------------------

    }

    //------------------------------------------------------------------

//--////////////////////////////////////////////////////////////////////
//--//  Instance methods : associated methods in enclosing class
//--////////////////////////////////////////////////////////////////////

    public List<TabWidthFilter> getTabWidthFilters( )
    {
        return cpTabWidthFilters.getValues( );
    }

    //------------------------------------------------------------------

    public void setTabWidthFilters( List<TabWidthFilter> filters )
    {
        cpTabWidthFilters.setValues( filters );
    }

    //------------------------------------------------------------------

//--////////////////////////////////////////////////////////////////////
//--//  Instance variables : associated variables in enclosing class
//--////////////////////////////////////////////////////////////////////

    private CPTabWidthFilters   cpTabWidthFilters   = new CPTabWidthFilters( );

    //==================================================================


    // PROPERTY CLASS: TARGET AND REPLACEMENT TAB WIDTH


    private class CPTargetAndReplacementTabWidth
        extends Property.IntegerProperty
    {

    ////////////////////////////////////////////////////////////////////
    //  Constructors
    ////////////////////////////////////////////////////////////////////

        private CPTargetAndReplacementTabWidth( )
        {
            super( concatenateKeys( Key.TAB_WIDTH, Key.TARGET_AND_REPLACEMENT ),
                   TextModel.MIN_TAB_WIDTH, TextModel.MAX_TAB_WIDTH );
            value = TextModel.DEFAULT_TAB_WIDTH;
        }

        //--------------------------------------------------------------

    }

    //------------------------------------------------------------------

//--////////////////////////////////////////////////////////////////////
//--//  Instance methods : associated methods in enclosing class
//--////////////////////////////////////////////////////////////////////

    public int getTargetAndReplacementTabWidth( )
    {
        return cpTargetAndReplacementTabWidth.getValue( );
    }

    //------------------------------------------------------------------

    public void setTargetAndReplacementTabWidth( int value )
    {
        cpTargetAndReplacementTabWidth.setValue( value );
    }

    //------------------------------------------------------------------

//--////////////////////////////////////////////////////////////////////
//--//  Instance variables : associated variables in enclosing class
//--////////////////////////////////////////////////////////////////////

    private CPTargetAndReplacementTabWidth  cpTargetAndReplacementTabWidth  =
                                                                    new CPTargetAndReplacementTabWidth( );

    //==================================================================


    // PROPERTY CLASS: EDITOR COMMAND


    private class CPEditorCommand
        extends Property.StringProperty
    {

    ////////////////////////////////////////////////////////////////////
    //  Constructors
    ////////////////////////////////////////////////////////////////////

        private CPEditorCommand( )
        {
            super( concatenateKeys( Key.EDITOR, Key.COMMAND ) );
        }

        //--------------------------------------------------------------

    }

    //------------------------------------------------------------------

//--////////////////////////////////////////////////////////////////////
//--//  Instance methods : associated methods in enclosing class
//--////////////////////////////////////////////////////////////////////

    public String getEditorCommand( )
    {
        return cpEditorCommand.getValue( );
    }

    //------------------------------------------------------------------

    public void setEditorCommand( String value )
    {
        cpEditorCommand.setValue( value );
    }

    //------------------------------------------------------------------

//--////////////////////////////////////////////////////////////////////
//--//  Instance variables : associated variables in enclosing class
//--////////////////////////////////////////////////////////////////////

    private CPEditorCommand cpEditorCommand = new CPEditorCommand( );

    //==================================================================


    // PROPERTY CLASS: PATHNAME OF DEFAULT SEARCH PARAMETERS FILE


    private class CPDefaultSearchParamsPathname
        extends Property.StringProperty
    {

    ////////////////////////////////////////////////////////////////////
    //  Constructors
    ////////////////////////////////////////////////////////////////////

        private CPDefaultSearchParamsPathname( )
        {
            super( concatenateKeys( Key.PATH, Key.DEFAULT_SEARCH_PARAMETERS ) );
        }

        //--------------------------------------------------------------

    }

    //------------------------------------------------------------------

//--////////////////////////////////////////////////////////////////////
//--//  Instance methods : associated methods in enclosing class
//--////////////////////////////////////////////////////////////////////

    public String getDefaultSearchParamsPathname( )
    {
        return cpDefaultSearchParamsPathname.getValue( );
    }

    //------------------------------------------------------------------

    public File getDefaultSearchParamsFile( )
    {
        String pathname = getDefaultSearchParamsPathname( );
        return ( (pathname == null) ? null : new File( PropertyString.parsePathname( pathname ) ) );
    }

    //------------------------------------------------------------------

    public void setDefaultSearchParamsPathname( String value )
    {
        cpDefaultSearchParamsPathname.setValue( value );
    }

    //------------------------------------------------------------------

//--////////////////////////////////////////////////////////////////////
//--//  Instance variables : associated variables in enclosing class
//--////////////////////////////////////////////////////////////////////

    private CPDefaultSearchParamsPathname   cpDefaultSearchParamsPathname   =
                                                                    new CPDefaultSearchParamsPathname( );

    //==================================================================


    // PROPERTY CLASS: FONTS


    private class CPFonts
        extends Property.PropertyMap<AppFont, FontEx>
    {

    ////////////////////////////////////////////////////////////////////
    //  Constructors
    ////////////////////////////////////////////////////////////////////

        private CPFonts( )
        {
            super( Key.FONT, AppFont.class );
            for ( AppFont font : AppFont.values( ) )
                values.put( font, new FontEx( ) );
        }

        //--------------------------------------------------------------

    ////////////////////////////////////////////////////////////////////
    //  Instance methods : overriding methods
    ////////////////////////////////////////////////////////////////////

        @Override
        public void parse( Input   input,
                           AppFont appFont )
        {
            try
            {
                FontEx font = new FontEx( input.getValue( ) );
                appFont.setFontEx( font );
                values.put( appFont, font );
            }
            catch ( IllegalArgumentException e )
            {
                showWarningMessage( new IllegalValueException( input ) );
            }
            catch ( uk.org.blankaspect.exception.ValueOutOfBoundsException e )
            {
                showWarningMessage( new ValueOutOfBoundsException( input ) );
            }
        }

        //--------------------------------------------------------------

        @Override
        public String toString( AppFont appFont )
        {
            return getValue( appFont ).toString( );
        }

        //--------------------------------------------------------------

    }

    //------------------------------------------------------------------

//--////////////////////////////////////////////////////////////////////
//--//  Instance methods : associated methods in enclosing class
//--////////////////////////////////////////////////////////////////////

    public FontEx getFont( int index )
    {
        return cpFonts.getValue( AppFont.values( )[index] );
    }

    //------------------------------------------------------------------

    public void setFont( int    index,
                         FontEx font )
    {
        cpFonts.setValue( AppFont.values( )[index], font );
    }

    //------------------------------------------------------------------

//--////////////////////////////////////////////////////////////////////
//--//  Instance variables : associated variables in enclosing class
//--////////////////////////////////////////////////////////////////////

    private CPFonts cpFonts = new CPFonts( );

    //==================================================================

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

    private AppConfig( )
    {
    }

    //------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

    public static AppConfig getInstance( )
    {
        if ( instance == null )
            instance = new AppConfig( );
        return instance;
    }

    //------------------------------------------------------------------

    public static void showWarningMessage( AppException exception )
    {
        App.getInstance( ).showWarningMessage( App.SHORT_NAME + " | " + CONFIG_ERROR_STR, exception );
    }

    //------------------------------------------------------------------

    public static void showErrorMessage( AppException exception )
    {
        App.getInstance( ).showErrorMessage( App.SHORT_NAME + " | " + CONFIG_ERROR_STR, exception );
    }

    //------------------------------------------------------------------

    private static File getFile( )
        throws AppException
    {
        File file = null;

        // Get directory of JAR file
        File jarDirectory = null;
        try
        {
            jarDirectory = new File( AppConfig.class.getProtectionDomain( ).getCodeSource( ).getLocation( ).
                                                                                toURI( ) ).getParentFile( );
        }
        catch ( Exception e )
        {
            e.printStackTrace( );
        }

        // Get pathname of configuration directory from properties file
        String pathname = null;
        File propertiesFile = new File( jarDirectory, PROPERTIES_FILENAME );
        if ( propertiesFile.isFile( ) )
        {
            try
            {
                Properties properties = new Properties( );
                properties.loadFromXML( new FileInputStream( propertiesFile ) );
                pathname = properties.getProperty( CONFIG_DIR_KEY );
            }
            catch ( IOException e )
            {
                throw new FileException( ErrorId.ERROR_READING_PROPERTIES_FILE, propertiesFile );
            }
        }

        // Get pathname of configuration directory from system property or set system property to pathname
        try
        {
            if ( pathname == null )
                pathname = System.getProperty( CONFIG_DIR_KEY );
            else
                System.setProperty( CONFIG_DIR_KEY, pathname );
        }
        catch ( SecurityException e )
        {
            // ignore
        }

        // Look for configuration file in default locations
        if ( pathname == null )
        {
            // Look for configuration file in local directory
            file = new File( CONFIG_FILENAME );

            // Look for configuration file in default configuration directory
            if ( !file.isFile( ) )
            {
                file = null;
                pathname = Util.getPropertiesPathname( );
                if ( pathname != null )
                {
                    file = new File( pathname, CONFIG_FILENAME );
                    if ( !file.isFile( ) )
                        file = null;
                }
            }
        }

        // Set configuration file from pathname of configuration directory
        else if ( !pathname.isEmpty( ) )
        {
            file = new File( PropertyString.parsePathname( pathname ), CONFIG_FILENAME );
            if ( !file.isFile( ) )
                throw new FileException( ErrorId.NO_CONFIGURATION_FILE, file );
        }

        return file;
    }

    //------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

    public File chooseFile( Component parent )
    {
        if ( fileChooser == null )
        {
            fileChooser = new JFileChooser( );
            fileChooser.setDialogTitle( SAVE_CONFIGURATION_FILE_STR );
            fileChooser.setFileSelectionMode( JFileChooser.FILES_ONLY );
            fileChooser.setFileFilter( new FilenameSuffixFilter( AppConstants.XML_FILES_STR,
                                                                 AppConstants.XML_FILE_SUFFIX ) );
            selectedFile = file;
        }

        fileChooser.setSelectedFile( (selectedFile == null) ? new File( CONFIG_FILENAME ).getAbsoluteFile( )
                                                            : selectedFile.getAbsoluteFile( ) );
        fileChooser.rescanCurrentDirectory( );
        if ( fileChooser.showSaveDialog( parent ) == JFileChooser.APPROVE_OPTION )
        {
            selectedFile = Util.appendSuffix( fileChooser.getSelectedFile( ),
                                              AppConstants.XML_FILE_SUFFIX );
            return selectedFile;
        }
        return null;
    }

    //------------------------------------------------------------------

    public void read( )
    {
        // Read configuration file
        fileRead = false;
        ConfigFile configFile = null;
        try
        {
            file = getFile( );
            if ( file != null )
            {
                configFile = new ConfigFile( );
                configFile.read( file );
                fileRead = true;
            }
        }
        catch ( AppException e )
        {
            showErrorMessage( e );
        }

        // Get properties
        if ( fileRead )
            getProperties( configFile, Property.getSystemSource( ) );
        else
            getProperties( Property.getSystemSource( ) );

        // Reset changed status of properties
        resetChanged( );
    }

    //------------------------------------------------------------------

    public void write( )
    {
        if ( isChanged( ) )
        {
            try
            {
                if ( file == null )
                {
                    if ( System.getProperty( CONFIG_DIR_KEY ) == null )
                    {
                        String pathname = Util.getPropertiesPathname( );
                        if ( pathname != null )
                        {
                            File directory = new File( pathname );
                            if ( !directory.exists( ) && !directory.mkdirs( ) )
                                throw new FileException( ErrorId.FAILED_TO_CREATE_DIRECTORY, directory );
                            file = new File( directory, CONFIG_FILENAME );
                        }
                    }
                }
                else
                {
                    if ( !fileRead )
                        file.renameTo( new File( file.getParentFile( ), CONFIG_OLD_FILENAME ) );
                }
                if ( file != null )
                {
                    write( file );
                    resetChanged( );
                }
            }
            catch ( AppException e )
            {
                showErrorMessage( e );
            }
        }
    }

    //------------------------------------------------------------------

    public void write( File file )
        throws AppException
    {
        // Initialise progress view
        ProgressView progressView = Task.getProgressView( );
        if ( progressView != null )
        {
            progressView.setInfo( WRITING_STR, file );
            progressView.setProgress( 0, -1.0 );
        }

        // Create new DOM document
        ConfigFile configFile = new ConfigFile( Integer.toString( VERSION ) );

        // Set configuration properties in document
        putProperties( configFile );

        // Write file
        configFile.write( file );
    }

    //------------------------------------------------------------------

    private void getProperties( Property.Source... propertySources )
    {
        for ( Property property : getProperties( ) )
        {
            try
            {
                property.get( propertySources );
            }
            catch ( AppException e )
            {
                showWarningMessage( e );
            }
        }
    }

    //------------------------------------------------------------------

    private void putProperties( Property.Target propertyTarget )
    {
        for ( Property property : getProperties( ) )
            property.put( propertyTarget );
    }

    //------------------------------------------------------------------

    private boolean isChanged( )
    {
        for ( Property property : getProperties( ) )
        {
            if ( property.isChanged( ) )
                return true;
        }
        return false;
    }

    //------------------------------------------------------------------

    private void resetChanged( )
    {
        for ( Property property : getProperties( ) )
            property.setChanged( false );
    }

    //------------------------------------------------------------------

    private List<Property> getProperties( )
    {
        if ( properties == null )
        {
            properties = new ArrayList<>( );
            for ( Field field : getClass( ).getDeclaredFields( ) )
            {
                try
                {
                    if ( field.getName( ).startsWith( Property.FIELD_PREFIX ) )
                        properties.add( (Property)field.get( this ) );
                }
                catch ( IllegalAccessException e )
                {
                    e.printStackTrace( );
                }
            }
        }
        return properties;
    }

    //------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class variables
////////////////////////////////////////////////////////////////////////

    private static  AppConfig   instance;

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

    private File            file;
    private boolean         fileRead;
    private File            selectedFile;
    private JFileChooser    fileChooser;
    private List<Property>  properties;

}

//----------------------------------------------------------------------
