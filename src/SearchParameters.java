/*====================================================================*\

SearchParameters.java

Search parameters class.

\*====================================================================*/


// IMPORTS


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import uk.org.blankaspect.exception.AppException;
import uk.org.blankaspect.exception.FileException;
import uk.org.blankaspect.exception.TempFileException;
import uk.org.blankaspect.exception.UnexpectedRuntimeException;

import uk.org.blankaspect.gui.ProgressView;

import uk.org.blankaspect.util.NoYes;

import uk.org.blankaspect.xml.Attribute;
import uk.org.blankaspect.xml.XmlConstants;
import uk.org.blankaspect.xml.XmlParseException;
import uk.org.blankaspect.xml.XmlUtilities;
import uk.org.blankaspect.xml.XmlValidationException;
import uk.org.blankaspect.xml.XmlWriter;

//----------------------------------------------------------------------


// SEARCH PARAMETERS CLASS


class SearchParameters
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

    public static final     int MAX_NUM_FILE_SETS       = 256;
    public static final     int MAX_NUM_TARGETS         = 64;
    public static final     int MAX_NUM_REPLACEMENTS    = MAX_NUM_TARGETS;

    private static final    int VERSION                 = 0;
    private static final    int MIN_SUPPORTED_VERSION   = 0;
    private static final    int MAX_SUPPORTED_VERSION   = 0;

    private static final    String          NAMESPACE_NAME  = "http://ns.blankaspect.org.uk/regexSearch-1";
    private static final    List<String>    NAMESPACE_NAMES = Arrays.asList
    (
        "http://ns.puckfist.org/regexSearch-1",
        "http://ns.blankaspect.org/regexSearch-1",
        NAMESPACE_NAME
    );

    private static final    String  READING_STR = "Reading";
    private static final    String  WRITING_STR = "Writing";

    private interface ElementName
    {
        String  REPLACEMENT         = "replacement";
        String  SEARCH_PARAMETERS   = "searchParameters";
        String  TARGET              = "target";
    }

    private interface AttrName
    {
        String  FILE_SET_INDEX      = "fileSetIndex";
        String  IGNORE_CASE         = "ignoreCase";
        String  REGEX               = "regex";
        String  REPLACE             = "replace";
        String  REPLACEMENT_INDEX   = "replacementIndex";
        String  TARGET_INDEX        = "targetIndex";
        String  VERSION             = "version";
        String  XMLNS               = "xmlns";
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

        FAILED_TO_OPEN_FILE
        ( "Failed to open the file." ),

        FAILED_TO_CLOSE_FILE
        ( "Failed to close the file." ),

        FAILED_TO_LOCK_FILE
        ( "Failed to lock the file." ),

        ERROR_READING_FILE
        ( "An error occurred when reading the file." ),

        ERROR_WRITING_FILE
        ( "An error occurred when writing the file." ),

        FILE_ACCESS_NOT_PERMITTED
        ( "Access to the file was not permitted." ),

        NOT_ENOUGH_MEMORY_TO_OPEN_FILE
        ( "There was not enough memory to open the file." ),

        FAILED_TO_CREATE_TEMPORARY_FILE
        ( "Failed to create a temporary file." ),

        FAILED_TO_DELETE_FILE
        ( "Failed to delete the existing file." ),

        FAILED_TO_RENAME_FILE
        ( "Failed to rename the temporary file to the specified filename." ),

        INVALID_DOCUMENT
        ( "The file is not a valid search parameters document." ),

        UNEXPECTED_DOCUMENT_FORMAT
        ( "The document does not have the expected format." ),

        UNSUPPORTED_DOCUMENT_VERSION
        ( "The version of the document (%1) is not supported by this version of " + App.SHORT_NAME + "." ),

        NO_ATTRIBUTE
        ( "The required attribute is missing." ),

        INVALID_ATTRIBUTE
        ( "The attribute is invalid." ),

        MALFORMED_TEXT
        ( "The text content of the element is malformed." );

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

    public SearchParameters( )
    {
        init( );
        targets.add( new String( ) );
        replacements.add( new String( ) );
    }

    //------------------------------------------------------------------

    public SearchParameters( File file )
        throws AppException
    {
        init( );
        read( file );
    }

    //------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

    private static String escape( String str )
    {
        return str.replace( "\\", "\\\\" ).replace( "\t", "\\t" ).replace( "\n", "\\n" );
    }

    //------------------------------------------------------------------

    /**
     * @throws IllegalArgumentException
     */

    private static String unescape( String str )
    {
        StringBuilder buffer = new StringBuilder( );
        for ( int i = 0; i < str.length( ); ++i )
        {
            char ch = str.charAt( i );
            if ( ch == '\\' )
            {
                if ( ++i >= str.length( ) )
                    throw new IllegalArgumentException( );
                ch = str.charAt( i );
                switch ( ch )
                {
                    case 't':
                        ch = '\t';
                        break;

                    case 'n':
                        ch = '\n';
                        break;

                    case '\\':
                        break;

                    default:
                        throw new IllegalArgumentException( );
                }
            }
            buffer.append( ch );
        }
        return buffer.toString( );
    }

    //------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

    public File getFile( )
    {
        return file;
    }

    //------------------------------------------------------------------

    public boolean isChanged( )
    {
        return changed;
    }

    //------------------------------------------------------------------

    public int getNumFileSets( )
    {
        return fileSets.size( );
    }

    //------------------------------------------------------------------

    public FileSet getFileSet( int index )
    {
        return ( (index < 0) || (index >= fileSets.size( )) ? null : fileSets.get( index ) );
    }

    //------------------------------------------------------------------

    public int getFileSetIndex( )
    {
        return fileSetIndex;
    }

    //------------------------------------------------------------------

    public List<String> getTargets( )
    {
        return Collections.unmodifiableList( targets );
    }

    //------------------------------------------------------------------

    public int getTargetIndex( )
    {
        return targetIndex;
    }

    //------------------------------------------------------------------

    public List<String> getReplacements( )
    {
        return Collections.unmodifiableList( replacements );
    }

    //------------------------------------------------------------------

    public int getReplacementIndex( )
    {
        return replacementIndex;
    }

    //------------------------------------------------------------------

    public boolean isReplace( )
    {
        return replace;
    }

    //------------------------------------------------------------------

    public boolean isRegex( )
    {
        return regex;
    }

    //------------------------------------------------------------------

    public boolean isIgnoreCase( )
    {
        return ignoreCase;
    }

    //------------------------------------------------------------------

    public void setFileSet( int     index,
                            FileSet fileSet )
    {
        if ( !fileSets.get( index ).equals( fileSet ) )
        {
            fileSets.set( index, fileSet );
            changed = true;
        }
    }

    //------------------------------------------------------------------

    public void setFileSetIndex( int index )
    {
        if ( fileSetIndex != index )
        {
            fileSetIndex = index;
            changed = true;
        }
    }

    //------------------------------------------------------------------

    public void setTargets( List<String> targets )
    {
        if ( !this.targets.equals( targets ) )
        {
            this.targets = targets;
            changed = true;
        }
    }

    //------------------------------------------------------------------

    public void setTargetIndex( int index )
    {
        if ( targetIndex != index )
        {
            targetIndex = index;
            changed = true;
        }
    }

    //------------------------------------------------------------------

    public void setReplacements( List<String> replacements )
    {
        if ( !this.replacements.equals( replacements ) )
        {
            this.replacements = replacements;
            changed = true;
        }
    }

    //------------------------------------------------------------------

    public void setReplacementIndex( int index )
    {
        if ( replacementIndex != index )
        {
            replacementIndex = index;
            changed = true;
        }
    }

    //------------------------------------------------------------------

    public void setReplace( boolean replace )
    {
        if ( this.replace != replace )
        {
            this.replace = replace;
            changed = true;
        }
    }

    //------------------------------------------------------------------

    public void setRegex( boolean regex )
    {
        if ( this.regex != regex )
        {
            this.regex = regex;
            changed = true;
        }
    }

    //------------------------------------------------------------------

    public void setIgnoreCase( boolean ignoreCase )
    {
        if ( this.ignoreCase != ignoreCase )
        {
            this.ignoreCase = ignoreCase;
            changed = true;
        }
    }

    //------------------------------------------------------------------

    public void addFileSet( int     index,
                            FileSet fileSet )
    {
        if ( fileSets.size( ) < MAX_NUM_FILE_SETS )
        {
            fileSets.add( index, fileSet );
            changed = true;
        }
    }

    //------------------------------------------------------------------

    public FileSet removeFileSet( int index )
    {
        FileSet fileSet = fileSets.remove( index );
        changed = true;
        return fileSet;
    }

    //------------------------------------------------------------------

    public void read( File file )
        throws AppException
    {
        // Initialise progress view
        ProgressView progressView = Task.getProgressView( );
        if ( progressView != null )
        {
            progressView.setInfo( READING_STR, file );
            progressView.setProgress( 0, -1.0 );
        }

        // Run garbage collector to maximise available memory
        System.gc( );

        // Read file
        FileInputStream inStream = null;
        try
        {
            // Open input stream on file
            try
            {
                inStream = new FileInputStream( file );
            }
            catch ( FileNotFoundException e )
            {
                throw new FileException( ErrorId.FAILED_TO_OPEN_FILE, file, e );
            }
            catch ( SecurityException e )
            {
                throw new FileException( ErrorId.FILE_ACCESS_NOT_PERMITTED, file, e );
            }

            // Lock file
            try
            {
                if ( inStream.getChannel( ).tryLock( 0, Long.MAX_VALUE, true ) == null )
                    throw new FileException( ErrorId.FAILED_TO_LOCK_FILE, file );
            }
            catch ( Exception e )
            {
                throw new FileException( ErrorId.FAILED_TO_LOCK_FILE, file, e );
            }

            // Test for XML file
            try
            {
                if ( !XmlUtilities.isXml( file ) )
                    throw new FileException( ErrorId.INVALID_DOCUMENT, file );
            }
            catch ( FileNotFoundException e )
            {
                throw new FileException( ErrorId.FAILED_TO_OPEN_FILE, file, e );
            }
            catch ( IOException e )
            {
                throw new FileException( ErrorId.ERROR_READING_FILE, file, e );
            }
            catch ( SecurityException e )
            {
                throw new FileException( ErrorId.FILE_ACCESS_NOT_PERMITTED, file, e );
            }

            // Invalidate current file
            this.file = null;

            // Read and parse file
            try
            {
                parse( file, inStream );
            }
            catch ( OutOfMemoryError e )
            {
                throw new FileException( ErrorId.NOT_ENOUGH_MEMORY_TO_OPEN_FILE, file );
            }

            // Close input stream
            try
            {
                inStream.close( );
                inStream = null;
            }
            catch ( IOException e )
            {
                throw new FileException( ErrorId.FAILED_TO_CLOSE_FILE, file, e );
            }

            // Indicate that the file has been read and that the parameters are unchanged
            this.file = file;
            changed = false;
        }
        catch ( AppException e )
        {
            if ( inStream != null )
            {
                try
                {
                    inStream.close( );
                }
                catch ( IOException e1 )
                {
                    // ignore
                }
            }
            throw e;
        }
    }

    //------------------------------------------------------------------

    public void write( )
    {
        if ( file != null )
        {
            try
            {
                write( file );
            }
            catch ( AppException e )
            {
                App.getInstance( ).showErrorMessage( App.SHORT_NAME, e );
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

        // Write file
        File tempFile = null;
        XmlWriter writer = null;
        boolean oldFileDeleted = false;
        try
        {
            // Create temporary file
            try
            {
                tempFile = File.createTempFile( AppConstants.TEMP_FILE_PREFIX, null,
                                                file.getAbsoluteFile( ).getParentFile( ) );
            }
            catch ( Exception e )
            {
                throw new AppException( ErrorId.FAILED_TO_CREATE_TEMPORARY_FILE, e );
            }

            // Open XML writer on temporary file
            try
            {
                writer = new XmlWriter( tempFile, XmlConstants.ENCODING_NAME_UTF8 );
            }
            catch ( FileNotFoundException e )
            {
                throw new FileException( ErrorId.FAILED_TO_OPEN_FILE, tempFile, e );
            }
            catch ( SecurityException e )
            {
                throw new FileException( ErrorId.FILE_ACCESS_NOT_PERMITTED, tempFile, e );
            }
            catch ( UnsupportedEncodingException e )
            {
                throw new UnexpectedRuntimeException( e );
            }

            // Write file
            try
            {
                // Write XML declaration
                writer.writeXmlDeclaration( AppConstants.XML_VERSION_STR, XmlConstants.ENCODING_NAME_UTF8,
                                            XmlWriter.Standalone.NO );

                // Write root element start tag
                List<Attribute> attributes = new ArrayList<>( );
                attributes.add( new Attribute( AttrName.XMLNS, NAMESPACE_NAME ) );
                attributes.add( new Attribute( AttrName.VERSION, VERSION ) );
                attributes.add( new Attribute( AttrName.REPLACE, replace ) );
                attributes.add( new Attribute( AttrName.REGEX, regex ) );
                attributes.add( new Attribute( AttrName.IGNORE_CASE, ignoreCase ) );
                if ( !fileSets.isEmpty( ) )
                    attributes.add( new Attribute( AttrName.FILE_SET_INDEX, fileSetIndex ) );
                if ( !targets.isEmpty( ) )
                    attributes.add( new Attribute( AttrName.TARGET_INDEX, targetIndex ) );
                if ( !replacements.isEmpty( ) )
                    attributes.add( new Attribute( AttrName.REPLACEMENT_INDEX, replacementIndex ) );
                writer.writeElementStart( ElementName.SEARCH_PARAMETERS, attributes, 0, true, true );

                // Write file sets
                for ( FileSet fileSet : fileSets )
                {
                    writer.writeEol( );
                    fileSet.write( writer, 2 );
                }

                // Write targets
                if ( !targets.isEmpty( ) )
                {
                    writer.writeEol( );
                    for ( String target : targets )
                        writer.writeEscapedTextElement( ElementName.TARGET, 2, escape( target ) );
                }

                // Write replacements
                if ( !replacements.isEmpty( ) )
                {
                    writer.writeEol( );
                    for ( String replacement : replacements )
                        writer.writeEscapedTextElement( ElementName.REPLACEMENT, 2, escape( replacement ) );
                }

                // Write root element end tag
                if ( !(fileSets.isEmpty( ) && targets.isEmpty( ) && replacements.isEmpty( )) )
                    writer.writeEol( );
                writer.writeElementEnd( ElementName.SEARCH_PARAMETERS, 0 );
            }
            catch ( IOException e )
            {
                throw new FileException( ErrorId.ERROR_WRITING_FILE, tempFile, e );
            }

            // Close output stream
            try
            {
                writer.close( );
                writer = null;
            }
            catch ( IOException e )
            {
                throw new FileException( ErrorId.FAILED_TO_CLOSE_FILE, tempFile, e );
            }

            // Delete any existing file
            try
            {
                if ( file.exists( ) && !file.delete( ) )
                    throw new FileException( ErrorId.FAILED_TO_DELETE_FILE, file );
                oldFileDeleted = true;
            }
            catch ( SecurityException e )
            {
                throw new FileException( ErrorId.FAILED_TO_DELETE_FILE, file, e );
            }

            // Rename temporary file
            try
            {
                if ( !tempFile.renameTo( file ) )
                    throw new TempFileException( ErrorId.FAILED_TO_RENAME_FILE, file, tempFile );
            }
            catch ( SecurityException e )
            {
                throw new TempFileException( ErrorId.FAILED_TO_RENAME_FILE, file, e, tempFile );
            }

            // Indicate that the file has been written and that the parameters are unchanged
            this.file = file;
            changed = false;
        }
        catch ( AppException e )
        {
            // Close output stream
            try
            {
                if ( writer != null )
                    writer.close( );
            }
            catch ( Exception e1 )
            {
                // ignore
            }

            // Delete temporary file
            try
            {
                if ( !oldFileDeleted && (tempFile != null) && tempFile.exists( ) )
                    tempFile.delete( );
            }
            catch ( Exception e1 )
            {
                // ignore
            }

            // Rethrow exception
            throw e;
        }
    }

    //------------------------------------------------------------------

    private void init( )
    {
        fileSets = new ArrayList<>( );
        targets = new ArrayList<>( );
        replacements = new ArrayList<>( );
    }

    //------------------------------------------------------------------

    private void parse( File        file,
                        InputStream inStream )
        throws AppException
    {
        // Create DOM document
        Document document = XmlUtilities.createDocument( inStream );
        if ( !XmlUtilities.getErrorHandler( ).isEmpty( ) )
            throw new XmlValidationException( ErrorId.INVALID_DOCUMENT, file,
                                              XmlUtilities.getErrorHandler( ).getErrorStrings( ) );

        // Test document format
        Element rootElement = document.getDocumentElement( );
        if ( !rootElement.getNodeName( ).equals( ElementName.SEARCH_PARAMETERS ) )
            throw new FileException( ErrorId.UNEXPECTED_DOCUMENT_FORMAT, file );
        String elementPath = ElementName.SEARCH_PARAMETERS;

        // Attribute: namespace
        String attrName = AttrName.XMLNS;
        String attrKey = XmlUtilities.appendAttributeName( elementPath, attrName );
        String attrValue = XmlUtilities.getAttribute( rootElement, attrName );
        if ( attrValue == null )
            throw new XmlParseException( ErrorId.NO_ATTRIBUTE, file, attrKey );
        if ( !NAMESPACE_NAMES.contains( attrValue ) )
            throw new FileException( ErrorId.UNEXPECTED_DOCUMENT_FORMAT, file );

        // Attribute: version
        attrName = AttrName.VERSION;
        attrKey = XmlUtilities.appendAttributeName( elementPath, attrName );
        attrValue = XmlUtilities.getAttribute( rootElement, attrName );
        if ( attrValue == null )
            throw new XmlParseException( ErrorId.NO_ATTRIBUTE, file, attrKey );
        try
        {
            int version = Integer.parseInt( attrValue );
            if ( (version < MIN_SUPPORTED_VERSION) || (version > MAX_SUPPORTED_VERSION) )
                throw new FileException( ErrorId.UNSUPPORTED_DOCUMENT_VERSION, file, attrValue );
        }
        catch ( NumberFormatException e )
        {
            throw new XmlParseException( ErrorId.INVALID_ATTRIBUTE, file, attrKey, attrValue );
        }

        // Attribute: replace
        attrName = AttrName.REPLACE;
        attrKey = XmlUtilities.appendAttributeName( elementPath, attrName );
        attrValue = XmlUtilities.getAttribute( rootElement, attrName );
        if ( attrValue == null )
            throw new XmlParseException( ErrorId.NO_ATTRIBUTE, file, attrKey );
        NoYes booleanValue = NoYes.forKey( attrValue );
        if ( booleanValue == null )
            throw new XmlParseException( ErrorId.INVALID_ATTRIBUTE, file, attrKey, attrValue );
        replace = booleanValue.toBoolean( );

        // Attribute: regex
        attrName = AttrName.REGEX;
        attrKey = XmlUtilities.appendAttributeName( elementPath, attrName );
        attrValue = XmlUtilities.getAttribute( rootElement, attrName );
        if ( attrValue == null )
            throw new XmlParseException( ErrorId.NO_ATTRIBUTE, file, attrKey );
        booleanValue = NoYes.forKey( attrValue );
        if ( booleanValue == null )
            throw new XmlParseException( ErrorId.INVALID_ATTRIBUTE, file, attrKey, attrValue );
        regex = booleanValue.toBoolean( );

        // Attribute: ignore case
        attrName = AttrName.IGNORE_CASE;
        attrKey = XmlUtilities.appendAttributeName( elementPath, attrName );
        attrValue = XmlUtilities.getAttribute( rootElement, attrName );
        if ( attrValue == null )
            throw new XmlParseException( ErrorId.NO_ATTRIBUTE, file, attrKey );
        booleanValue = NoYes.forKey( attrValue );
        if ( booleanValue == null )
            throw new XmlParseException( ErrorId.INVALID_ATTRIBUTE, file, attrKey, attrValue );
        ignoreCase = booleanValue.toBoolean( );

        // Attribute: file-set index
        attrName = AttrName.FILE_SET_INDEX;
        attrKey = XmlUtilities.appendAttributeName( elementPath, attrName );
        attrValue = XmlUtilities.getAttribute( rootElement, attrName );
        if ( attrValue != null )
        {
            try
            {
                fileSetIndex = Integer.parseInt( attrValue );
            }
            catch ( NumberFormatException e )
            {
                throw new XmlParseException( ErrorId.INVALID_ATTRIBUTE, attrKey, attrValue );
            }
        }

        // Attribute: target index
        attrName = AttrName.TARGET_INDEX;
        attrKey = XmlUtilities.appendAttributeName( elementPath, attrName );
        attrValue = XmlUtilities.getAttribute( rootElement, attrName );
        if ( attrValue != null )
        {
            try
            {
                targetIndex = Integer.parseInt( attrValue );
            }
            catch ( NumberFormatException e )
            {
                throw new XmlParseException( ErrorId.INVALID_ATTRIBUTE, attrKey, attrValue );
            }
        }

        // Attribute: replacement index
        attrName = AttrName.REPLACEMENT_INDEX;
        attrKey = XmlUtilities.appendAttributeName( elementPath, attrName );
        attrValue = XmlUtilities.getAttribute( rootElement, attrName );
        if ( attrValue != null )
        {
            try
            {
                replacementIndex = Integer.parseInt( attrValue );
            }
            catch ( NumberFormatException e )
            {
                throw new XmlParseException( ErrorId.INVALID_ATTRIBUTE, attrKey, attrValue );
            }
        }

        // Parse file sets, targets and replacements
        fileSets.clear( );
        targets.clear( );
        replacements.clear( );
        try
        {
            NodeList childNodes = rootElement.getChildNodes( );
            for ( int i = 0; i < childNodes.getLength( ); ++i )
            {
                Node node = childNodes.item( i );
                if ( node.getNodeType( ) == Node.ELEMENT_NODE )
                {
                    Element element = (Element)node;
                    String elementName = node.getNodeName( );

                    // File set
                    if ( elementName.equals( FileSet.getElementName( ) ) )
                        fileSets.add( new FileSet( element ) );

                    // Target
                    else if ( elementName.equals( ElementName.TARGET ) )
                    {
                        try
                        {
                            targets.add( unescape( element.getTextContent( ) ) );
                        }
                        catch ( IllegalArgumentException e )
                        {
                            throw new XmlParseException( ErrorId.MALFORMED_TEXT, elementName );
                        }
                    }

                    // Replacement
                    else if ( elementName.equals( ElementName.REPLACEMENT ) )
                    {
                        try
                        {
                            replacements.add( unescape( element.getTextContent( ) ) );
                        }
                        catch ( IllegalArgumentException e )
                        {
                            throw new XmlParseException( ErrorId.MALFORMED_TEXT, elementName );
                        }
                    }
                }
            }
        }
        catch ( XmlParseException e )
        {
            throw new XmlParseException( e, file );
        }

        // Fix up file-set index, target index and replacement index
        fileSetIndex = Math.max( 0, Math.min( fileSetIndex, fileSets.size( ) - 1 ) );
        targetIndex = Math.min( Math.max( 0, targetIndex ), targets.size( ) - 1 );
        replacementIndex = Math.min( Math.max( 0, replacementIndex ), replacements.size( ) - 1 );
    }

    //------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

    private File            file;
    private boolean         changed;
    private List<FileSet>   fileSets;
    private int             fileSetIndex;
    private List<String>    targets;
    private int             targetIndex;
    private List<String>    replacements;
    private int             replacementIndex;
    private boolean         replace;
    private boolean         regex;
    private boolean         ignoreCase;

}

//----------------------------------------------------------------------
