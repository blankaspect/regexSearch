/*====================================================================*\

TabWidthFilter.java

Tab-width filter class.

\*====================================================================*/


// IMPORTS


import java.io.File;

import java.util.ArrayList;
import java.util.List;

import uk.org.blankaspect.util.FilenameFilter;

//----------------------------------------------------------------------


// TAB-WIDTH FILTER CLASS


class TabWidthFilter
    implements Comparable<TabWidthFilter>
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

    private static final    char    WIDTH_SEPARATOR = ':';

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

    /**
     * @throws IllegalArgumentException
     */

    public TabWidthFilter( String str )
    {
        String[] strs = str.split( Character.toString( WIDTH_SEPARATOR ), -1 );
        if ( strs.length != 2 )
            throw new IllegalArgumentException( );

        setFilters( strs[0] );
        tabWidth = Integer.parseInt( strs[1].trim( ) );
    }

    //------------------------------------------------------------------

    public TabWidthFilter( String filterStr,
                           int    tabWidth )
    {
        setFilters( filterStr );
        this.tabWidth = tabWidth;
    }

    //------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

    public static boolean isWidthSeparator( char ch )
    {
        return ( ch == WIDTH_SEPARATOR );
    }

    //------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : Comparable interface
////////////////////////////////////////////////////////////////////////

    public int compareTo( TabWidthFilter filter )
    {
        return ( (tabWidth == filter.tabWidth) ? 0
                                               : (tabWidth < filter.tabWidth) ? -1 : 1 );
    }

    //------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

    @Override
    public String toString( )
    {
        StringBuilder buffer = new StringBuilder( );
        buffer.append( getFilterString( ) );
        buffer.append( WIDTH_SEPARATOR );
        buffer.append( ' ' );
        buffer.append( tabWidth );
        return buffer.toString( );
    }

    //------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

    public int getTabWidth( )
    {
        return tabWidth;
    }

    //------------------------------------------------------------------

    public int getTabWidth( File file )
    {
        for ( FilenameFilter filter : filters )
        {
            if ( filter.accept( file ) )
                return tabWidth;
        }
        return -1;
    }

    //------------------------------------------------------------------

    public String getFilterString( )
    {
        StringBuilder buffer = new StringBuilder( );
        for ( FilenameFilter filter : filters )
        {
            if ( buffer.length( ) > 0 )
                buffer.append( ' ' );
            buffer.append( filter.toString( ) );
        }
        return buffer.toString( );
    }

    //------------------------------------------------------------------

    private void setFilters( String filterStr )
    {
        boolean ignoreCase = AppConfig.getInstance( ).isIgnoreFilenameCase( );
        filters = new ArrayList<>( );
        for ( String str : filterStr.split( "\\s+" ) )
        {
            if ( !str.isEmpty( ) )
                filters.add( new FilenameFilter( str, ignoreCase ) );
        }
    }

    //------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

    private List<FilenameFilter>    filters;
    private int                     tabWidth;

}

//----------------------------------------------------------------------
