/*====================================================================*\

FileTransferHandler.java

File transfer handler class.

\*====================================================================*/


// IMPORTS


import java.awt.Window;

import java.awt.datatransfer.UnsupportedFlavorException;

import java.io.File;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;

import uk.org.blankaspect.exception.AppException;

import uk.org.blankaspect.util.DataImporter;

//----------------------------------------------------------------------


// FILE TRANSFER HANDLER CLASS


class FileTransferHandler
    extends TransferHandler
    implements Runnable
{

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

        FILE_TRANSFER_NOT_SUPPORTED
        ( "File transfer is not supported." ),

        MULTIPLE_FILE_TRANSFER_NOT_SUPPORTED
        ( "The transfer of more than one file is not supported." ),

        ERROR_TRANSFERRING_DATA
        ( "An error occurred while transferring data." );

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

    private FileTransferHandler( )
    {
    }

    //------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

    public static FileTransferHandler getInstance( )
    {
        if ( instance == null )
            instance = new FileTransferHandler( );
        return instance;
    }

    //------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : Runnable interface
////////////////////////////////////////////////////////////////////////

    public void run( )
    {
        AppCommand.IMPORT_FILE.execute( );
    }

    //------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

    @Override
    public boolean canImport( TransferHandler.TransferSupport support )
    {
        boolean supported = !support.isDrop( ) || ((support.getSourceDropActions( ) & COPY) == COPY);
        if ( supported )
            supported = App.getInstance( ).getMainWindow( ).getControlDialog( ).canAddFileSet( ) &&
                        DataImporter.isFileList( support.getDataFlavors( ) );
        if ( support.isDrop( ) && supported )
            support.setDropAction( COPY );
        return supported;
    }

    //------------------------------------------------------------------

    @Override
    public boolean importData( TransferHandler.TransferSupport support )
    {
        if ( canImport( support ) )
        {
            try
            {
                try
                {
                    File[] files = DataImporter.getFiles( support.getTransferable( ) );
                    if ( files != null )
                    {
                        Window window = SwingUtilities.getWindowAncestor( support.getComponent( ) );
                        window.toFront( );
                        if ( files.length > 1 )
                            throw new AppException( ErrorId.MULTIPLE_FILE_TRANSFER_NOT_SUPPORTED );
                        AppCommand.IMPORT_FILE.putValue( AppCommand.Property.FILE, files[0] );
                        SwingUtilities.invokeLater( this );
                        return true;
                    }
                }
                catch ( UnsupportedFlavorException e )
                {
                    throw new AppException( ErrorId.FILE_TRANSFER_NOT_SUPPORTED );
                }
                catch ( IOException e )
                {
                    throw new AppException( ErrorId.ERROR_TRANSFERRING_DATA );
                }
            }
            catch ( AppException e )
            {
                App.getInstance( ).showErrorMessage( App.SHORT_NAME, e );
            }
        }
        return false;
    }

    //------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class variables
////////////////////////////////////////////////////////////////////////

    private static  FileTransferHandler instance;

}

//----------------------------------------------------------------------
