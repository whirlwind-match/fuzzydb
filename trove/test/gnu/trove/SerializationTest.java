///////////////////////////////////////////////////////////////////////////////
// Copyright (c) 2001-2006, Eric D. Friedman All Rights Reserved.
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
///////////////////////////////////////////////////////////////////////////////
package gnu.trove;

import gnu.trove.*;
import junit.framework.TestCase;

import java.io.*;


/**
 *
 */
public class SerializationTest extends TestCase {
    public SerializationTest( String name ) {
        super( name );
    }

    public void testP2PMap() {
        // Long-long
        TLongLongHashMap llmap = new TLongLongHashMap();
        assertTrue( serializesCorrectly( llmap ) );
        llmap.put( 0, 1 );
        assertTrue( serializesCorrectly( llmap ) );
        llmap.put( Long.MIN_VALUE, Long.MIN_VALUE );
        assertTrue( serializesCorrectly( llmap ) );
        llmap.put( Long.MAX_VALUE, Long.MAX_VALUE );
        assertTrue( serializesCorrectly( llmap ) );

        // Int-int
        TIntIntHashMap iimap = new TIntIntHashMap();
        assertTrue( serializesCorrectly( iimap ) );
        iimap.put( 0, 1 );
        assertTrue( serializesCorrectly( iimap ) );
        iimap.put( Integer.MIN_VALUE, Integer.MIN_VALUE );
        assertTrue( serializesCorrectly( iimap ) );
        iimap.put( Integer.MAX_VALUE, Integer.MAX_VALUE );
        assertTrue( serializesCorrectly( iimap ) );

        // Double-double
        TDoubleDoubleHashMap ddmap = new TDoubleDoubleHashMap();
        assertTrue( serializesCorrectly( ddmap ) );
        ddmap.put( 0, 1 );
        assertTrue( serializesCorrectly( ddmap ) );
        ddmap.put( Double.MIN_VALUE, Double.MIN_VALUE );
        assertTrue( serializesCorrectly( ddmap ) );
        ddmap.put( Double.MAX_VALUE, Double.MAX_VALUE );
        assertTrue( serializesCorrectly( ddmap ) );
        // NOTE: trove doesn't deal well with NaN
//        ddmap.put( Double.NaN, Double.NaN );
//        assertTrue( serializesCorrectly( ddmap ) );
        ddmap.put( Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY );
        assertTrue( serializesCorrectly( ddmap ) );
        ddmap.put( Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY );
        assertTrue( serializesCorrectly( ddmap ) );

        // Float-float
        TFloatFloatHashMap ffmap = new TFloatFloatHashMap();
        assertTrue( serializesCorrectly( ffmap ) );
        ffmap.put( 0, 1 );
        assertTrue( serializesCorrectly( ffmap ) );
        ffmap.put( Float.MIN_VALUE, Float.MIN_VALUE );
        assertTrue( serializesCorrectly( ffmap ) );
        ffmap.put( Float.MAX_VALUE, Float.MAX_VALUE );
        assertTrue( serializesCorrectly( ffmap ) );
        // NOTE: trove doesn't deal well with NaN
//        ffmap.put( Float.NaN, Float.NaN );
//        assertTrue( serializesCorrectly( ffmap ) );
        ffmap.put( Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY );
        assertTrue( serializesCorrectly( ffmap ) );
        ffmap.put( Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY );
        assertTrue( serializesCorrectly( ffmap ) );
    }


    public void testP2OMap() {
        // Long-long
        TLongObjectHashMap lomap = new TLongObjectHashMap();
        assertTrue( serializesCorrectly( lomap ) );
        lomap.put( 0, Long.valueOf( 1 ) );
        assertTrue( serializesCorrectly( lomap ) );
        lomap.put( Long.MIN_VALUE, Long.valueOf( Long.MIN_VALUE ) );
        assertTrue( serializesCorrectly( lomap ) );
        lomap.put( Long.MAX_VALUE, Long.valueOf( Long.MAX_VALUE ) );
        assertTrue( serializesCorrectly( lomap ) );

        // Int-int
        TIntObjectHashMap iomap = new TIntObjectHashMap();
        assertTrue( serializesCorrectly( iomap ) );
        iomap.put( 0, Integer.valueOf( 1 ) );
        assertTrue( serializesCorrectly( iomap ) );
        iomap.put( Integer.MIN_VALUE, Integer.valueOf( Integer.MIN_VALUE ) );
        assertTrue( serializesCorrectly( iomap ) );
        iomap.put( Integer.MAX_VALUE, Integer.valueOf( Integer.MAX_VALUE ) );
        assertTrue( serializesCorrectly( iomap ) );

        // Double-double
        TDoubleObjectHashMap domap = new TDoubleObjectHashMap();
        assertTrue( serializesCorrectly( domap ) );
        domap.put( 0, Double.valueOf( 1 ) );
        assertTrue( serializesCorrectly( domap ) );
        domap.put( Double.MIN_VALUE, Double.valueOf( Double.MIN_VALUE ) );
        assertTrue( serializesCorrectly( domap ) );
        domap.put( Double.MAX_VALUE, Double.valueOf( Double.MAX_VALUE ) );
        assertTrue( serializesCorrectly( domap ) );
        // NOTE: trove doesn't deal well with NaN
//        ddmap.put( Double.NaN, Double.NaN );
//        assertTrue( serializesCorrectly( ddmap ) );
        domap.put( Double.POSITIVE_INFINITY, Double.valueOf( Double.POSITIVE_INFINITY ) );
        assertTrue( serializesCorrectly( domap ) );
        domap.put( Double.NEGATIVE_INFINITY, Double.valueOf( Double.NEGATIVE_INFINITY ) );
        assertTrue( serializesCorrectly( domap ) );

        // Float-float
        TFloatObjectHashMap fomap = new TFloatObjectHashMap();
        assertTrue( serializesCorrectly( fomap ) );
        fomap.put( 0, Float.valueOf( 1 ) );
        assertTrue( serializesCorrectly( fomap ) );
        fomap.put( Float.MIN_VALUE, Float.valueOf( Float.MIN_VALUE ) );
        assertTrue( serializesCorrectly( fomap ) );
        fomap.put( Float.MAX_VALUE, Float.valueOf( Float.MAX_VALUE ) );
        assertTrue( serializesCorrectly( fomap ) );
        // NOTE: trove doesn't deal well with NaN
//        ffmap.put( Float.NaN, Float.NaN );
//        assertTrue( serializesCorrectly( ffmap ) );
        fomap.put( Float.POSITIVE_INFINITY, Float.valueOf( Float.POSITIVE_INFINITY ) );
        assertTrue( serializesCorrectly( fomap ) );
        fomap.put( Float.NEGATIVE_INFINITY, Float.valueOf( Float.NEGATIVE_INFINITY ) );
        assertTrue( serializesCorrectly( fomap ) );
    }

    public void testO2PMap() {
        // Long-long
        TObjectLongHashMap olmap = new TObjectLongHashMap();
        assertTrue( serializesCorrectly( olmap ) );
        olmap.put( Long.valueOf( 0 ), 1 );
        assertTrue( serializesCorrectly( olmap ) );
        olmap.put( Long.valueOf( Long.MIN_VALUE ), Long.MIN_VALUE );
        assertTrue( serializesCorrectly( olmap ) );
        olmap.put( Long.valueOf( Long.MAX_VALUE ), Long.MAX_VALUE );
        assertTrue( serializesCorrectly( olmap ) );

        // Int-int
        TObjectIntHashMap oimap = new TObjectIntHashMap();
        assertTrue( serializesCorrectly( oimap ) );
        oimap.put( Integer.valueOf( 0 ), 1 );
        assertTrue( serializesCorrectly( oimap ) );
        oimap.put( Integer.valueOf( Integer.MIN_VALUE ), Integer.MIN_VALUE );
        assertTrue( serializesCorrectly( oimap ) );
        oimap.put( Integer.valueOf( Integer.MAX_VALUE ), Integer.MAX_VALUE );
        assertTrue( serializesCorrectly( oimap ) );

        // Double-double
        TObjectDoubleHashMap odmap = new TObjectDoubleHashMap();
        assertTrue( serializesCorrectly( odmap ) );
        odmap.put( Double.valueOf( 0 ), 1 );
        assertTrue( serializesCorrectly( odmap ) );
        odmap.put( Double.valueOf( Double.MIN_VALUE ), Double.MIN_VALUE );
        assertTrue( serializesCorrectly( odmap ) );
        odmap.put( Double.valueOf( Double.MAX_VALUE ), Double.MAX_VALUE );
        assertTrue( serializesCorrectly( odmap ) );
        // NOTE: trove doesn't deal well with NaN
//        ddmap.put( Double.NaN, Double.NaN );
//        assertTrue( serializesCorrectly( ddmap ) );
        odmap.put( Double.valueOf( Double.POSITIVE_INFINITY ), Double.POSITIVE_INFINITY );
        assertTrue( serializesCorrectly( odmap ) );
        odmap.put( Double.valueOf( Double.NEGATIVE_INFINITY ), Double.NEGATIVE_INFINITY );
        assertTrue( serializesCorrectly( odmap ) );

        // Float-float
        TObjectFloatHashMap ofmap = new TObjectFloatHashMap();
        assertTrue( serializesCorrectly( ofmap ) );
        ofmap.put( Float.valueOf( 0 ), 1 );
        assertTrue( serializesCorrectly( ofmap ) );
        ofmap.put( Float.valueOf( Float.MIN_VALUE ), Float.MIN_VALUE );
        assertTrue( serializesCorrectly( ofmap ) );
        ofmap.put( Float.valueOf( Float.MAX_VALUE ), Float.MAX_VALUE );
        assertTrue( serializesCorrectly( ofmap ) );
        // NOTE: trove doesn't deal well with NaN
//        ffmap.put( Float.NaN, Float.NaN );
//        assertTrue( serializesCorrectly( ffmap ) );
        ofmap.put( Float.valueOf( Float.POSITIVE_INFINITY ), Float.POSITIVE_INFINITY );
        assertTrue( serializesCorrectly( ofmap ) );
        ofmap.put( Float.valueOf( Float.NEGATIVE_INFINITY ), Float.NEGATIVE_INFINITY );
        assertTrue( serializesCorrectly( ofmap ) );
    }


    public void testList() {
        // Long-long
        TLongArrayList llist = new TLongArrayList();
        assertTrue( serializesCorrectly( llist ) );
        llist.add( 0 );
        llist.add( 1 );
        assertTrue( serializesCorrectly( llist ) );
        llist.add( Long.MIN_VALUE );
        assertTrue( serializesCorrectly( llist ) );
        llist.add( Long.MAX_VALUE );
        assertTrue( serializesCorrectly( llist ) );

        // Int-int
        TIntArrayList ilist = new TIntArrayList();
        assertTrue( serializesCorrectly( ilist ) );
        ilist.add( 0 );
        ilist.add( 1 );
        assertTrue( serializesCorrectly( ilist ) );
        ilist.add( Integer.MIN_VALUE );
        assertTrue( serializesCorrectly( ilist ) );
        ilist.add( Integer.MAX_VALUE );
        assertTrue( serializesCorrectly( ilist ) );

        // Double-double
        TDoubleArrayList dlist = new TDoubleArrayList();
        assertTrue( serializesCorrectly( dlist ) );
        dlist.add( 0 );
        dlist.add( 1 );
        assertTrue( serializesCorrectly( dlist ) );
        dlist.add( Double.MIN_VALUE );
        assertTrue( serializesCorrectly( dlist ) );
        dlist.add( Double.MAX_VALUE );
        assertTrue( serializesCorrectly( dlist ) );
        // NOTE: trove doesn't deal well with NaN
//        ddmap.add( Double.NaN, Double.NaN );
//        assertTrue( serializesCorrectly( ddmap ) );
        dlist.add( Double.POSITIVE_INFINITY );
        assertTrue( serializesCorrectly( dlist ) );
        dlist.add( Double.NEGATIVE_INFINITY );
        assertTrue( serializesCorrectly( dlist ) );

        // Float-float
        TFloatArrayList flist = new TFloatArrayList();
        assertTrue( serializesCorrectly( flist ) );
        flist.add( 0 );
        flist.add( 1 );
        assertTrue( serializesCorrectly( flist ) );
        flist.add( Float.MIN_VALUE );
        assertTrue( serializesCorrectly( flist ) );
        flist.add( Float.MAX_VALUE );
        assertTrue( serializesCorrectly( flist ) );
        // NOTE: trove doesn't deal well with NaN
//        ffmap.add( Float.NaN );
//        assertTrue( serializesCorrectly( ffmap ) );
        flist.add( Float.POSITIVE_INFINITY );
        assertTrue( serializesCorrectly( flist ) );
        flist.add( Float.NEGATIVE_INFINITY );
        assertTrue( serializesCorrectly( flist ) );
    }


    public void testSet() {
        // Long-long
        TLongHashSet llist = new TLongHashSet();
        assertTrue( serializesCorrectly( llist ) );
        llist.add( 0 );
        llist.add( 1 );
        assertTrue( serializesCorrectly( llist ) );
        llist.add( Long.MIN_VALUE );
        assertTrue( serializesCorrectly( llist ) );
        llist.add( Long.MAX_VALUE );
        assertTrue( serializesCorrectly( llist ) );

        // Int-int
        TIntHashSet ilist = new TIntHashSet();
        assertTrue( serializesCorrectly( ilist ) );
        ilist.add( 0 );
        ilist.add( 1 );
        assertTrue( serializesCorrectly( ilist ) );
        ilist.add( Integer.MIN_VALUE );
        assertTrue( serializesCorrectly( ilist ) );
        ilist.add( Integer.MAX_VALUE );
        assertTrue( serializesCorrectly( ilist ) );

        // Double-double
        TDoubleHashSet dlist = new TDoubleHashSet();
        assertTrue( serializesCorrectly( dlist ) );
        dlist.add( 0 );
        dlist.add( 1 );
        assertTrue( serializesCorrectly( dlist ) );
        dlist.add( Double.MIN_VALUE );
        assertTrue( serializesCorrectly( dlist ) );
        dlist.add( Double.MAX_VALUE );
        assertTrue( serializesCorrectly( dlist ) );
        // NOTE: trove doesn't deal well with NaN
//        ddmap.add( Double.NaN, Double.NaN );
//        assertTrue( serializesCorrectly( ddmap ) );
        dlist.add( Double.POSITIVE_INFINITY );
        assertTrue( serializesCorrectly( dlist ) );
        dlist.add( Double.NEGATIVE_INFINITY );
        assertTrue( serializesCorrectly( dlist ) );

        // Float-float
        TFloatHashSet flist = new TFloatHashSet();
        assertTrue( serializesCorrectly( flist ) );
        flist.add( 0 );
        flist.add( 1 );
        assertTrue( serializesCorrectly( flist ) );
        flist.add( Float.MIN_VALUE );
        assertTrue( serializesCorrectly( flist ) );
        flist.add( Float.MAX_VALUE );
        assertTrue( serializesCorrectly( flist ) );
        // NOTE: trove doesn't deal well with NaN
//        ffmap.add( Float.NaN );
//        assertTrue( serializesCorrectly( ffmap ) );
        flist.add( Float.POSITIVE_INFINITY );
        assertTrue( serializesCorrectly( flist ) );
        flist.add( Float.NEGATIVE_INFINITY );
        assertTrue( serializesCorrectly( flist ) );
    }


    public void testLinkedList() {
        TLinkedList list = new TLinkedList();
        list.add( new LinkedNode( 0 ) );
        list.add( new LinkedNode( 1 ) );
        list.add( new LinkedNode( 2 ) );
        list.add( new LinkedNode( 3 ) );

        assertTrue( serializesCorrectly( list ) );
    }


    private boolean serializesCorrectly( Serializable obj ) {
        assert obj instanceof Externalizable : obj + " is not Externalizable";

        ObjectOutputStream oout = null;
        ObjectInputStream oin = null;
        try {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            oout = new ObjectOutputStream( bout );

            oout.writeObject( obj );
            oout.close();

            ByteArrayInputStream bin = new ByteArrayInputStream( bout.toByteArray() );
            oin = new ObjectInputStream( bin );

            Object new_obj = oin.readObject();
            return obj.equals( new_obj );
        }
        catch( Exception ex ) {
            ex.printStackTrace();
            return false;
        }
        finally {
            if ( oout != null ) {
                try {
                    oout.close();
                }
                catch( IOException ex ) {
                    // ignore
                }
            }
            if ( oin != null ) {
                try {
                    oin.close();
                }
                catch( IOException ex ) {
                    // ignore
                }
            }
        }
    }


    private static class LinkedNode extends TLinkableAdapter {
        private final int value;

        LinkedNode( int value ) {
            this.value = value;
        }

        public boolean equals( Object o ) {
            if ( this == o ) return true;
            if ( o == null || getClass() != o.getClass() ) return false;

            LinkedNode that = ( LinkedNode ) o;

            if ( value != that.value ) return false;

            return true;
        }

        public int hashCode() {
            return value;
        }
    }
}
