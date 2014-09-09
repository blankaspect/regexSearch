/*====================================================================*\

FPathnameField.java

Pathname field class.

\*====================================================================*/


// IMPORTS


import java.io.File;

import uk.org.blankaspect.gui.GuiUtilities;

import uk.org.blankaspect.textfield.PathnameField;

//----------------------------------------------------------------------


// PATHNAME FIELD CLASS


class FPathnameField
    extends PathnameField
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

    private static final    int NUM_COLUMNS = 40;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

    public FPathnameField( )
    {
        super( NUM_COLUMNS );
        init( );
    }

    //------------------------------------------------------------------

    public FPathnameField( String pathname )
    {
        super( pathname, NUM_COLUMNS );
        init( );
    }

    //------------------------------------------------------------------

    public FPathnameField( File file )
    {
        super( file, NUM_COLUMNS );
        init( );
    }

    //------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

    @Override
    public void setFile( File file )
    {
        setFile( file, AppConfig.getInstance( ).isShowUnixPathnames( ) );
    }

    //------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

    private void init( )
    {
        AppFont.TEXT_FIELD.apply( this );
        GuiUtilities.setTextComponentMargins( this );
    }

    //------------------------------------------------------------------

}

//----------------------------------------------------------------------
