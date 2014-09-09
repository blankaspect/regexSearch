/*====================================================================*\

AppFont.java

Application font enumeration.

\*====================================================================*/


// IMPORTS


import java.awt.Component;
import java.awt.Font;

import uk.org.blankaspect.gui.FontEx;

import uk.org.blankaspect.util.StringKeyed;

//----------------------------------------------------------------------


// APPLICATION FONT ENUMERATION


public enum AppFont
    implements StringKeyed
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

    MAIN
    (
        "main",
        "Main"
    ),

    TEXT_FIELD
    (
        "textField",
        "Text field"
    ),

    COMBO_BOX
    (
        "comboBox",
        "Combo box"
    ),

    TEXT_VIEW
    (
        "textView",
        "Text view"
    ),

    RESULT_AREA
    (
        "resultArea",
        "Result area"
    ),

    PARAMETER_EDITOR
    (
        "parameterEditor",
        "Parameter editor"
    );

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

    private AppFont( String key,
                     String text )
    {
        this.key = key;
        this.text = text;
        fontEx = new FontEx( );
    }

    //------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

    public static int getNumFonts( )
    {
        return values( ).length;
    }

    //------------------------------------------------------------------

    public static String[] getKeys( )
    {
        String[] keys = new String[values( ).length];
        for ( int i = 0; i < keys.length; ++i )
            keys[i] = values( )[i].key;
        return keys;
    }

    //------------------------------------------------------------------

    public static void setFontExs( FontEx[] fontExs )
    {
        for ( AppFont appFont : values( ) )
        {
            FontEx fontEx = fontExs[appFont.ordinal( )];
            if ( fontEx != null )
                appFont.setFontEx( fontEx );
        }
    }

    //------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : StringKeyed interface
////////////////////////////////////////////////////////////////////////

    public String getKey( )
    {
        return key;
    }

    //------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

    @Override
    public String toString( )
    {
        return text;
    }

    //------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

    public FontEx getFontEx( )
    {
        return fontEx;
    }

    //------------------------------------------------------------------

    public Font getFont( )
    {
        return fontEx.toFont( );
    }

    //------------------------------------------------------------------

    public void setFontEx( FontEx fontEx )
    {
        this.fontEx = fontEx;
    }

    //------------------------------------------------------------------

    public void apply( Component component )
    {
        fontEx.applyFont( component );
    }

    //------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

    private String  key;
    private String  text;
    private FontEx  fontEx;

}

//----------------------------------------------------------------------
