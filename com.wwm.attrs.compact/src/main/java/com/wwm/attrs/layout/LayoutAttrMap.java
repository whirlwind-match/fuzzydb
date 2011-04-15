/******************************************************************************
 * Copyright (c) 2005-2008 Whirlwind Match Limited. All rights reserved.
 *
 * This is open source software; you can use, redistribute and/or modify
 * it under the terms of the Open Software Licence v 3.0 as published by the 
 * Open Source Initiative.
 *
 * You should have received a copy of the Open Software Licence along with this
 * application. if not, contact the Open Source Initiative (www.opensource.org)
 *****************************************************************************/
package com.wwm.attrs.layout;


import gnu.trove.TIntArrayList;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

import com.wwm.attrs.internal.AttrDefinitionMgr;
import com.wwm.db.whirlwind.internal.IAttribute;
import com.wwm.db.whirlwind.internal.IAttributeConstraint;
import com.wwm.db.whirlwind.internal.IAttributeMap;

/**
 * LayoutAttrMap implements an encoded attribute map, where attributes are encoded into int[] and float[]
 * at fixed starting locations (e.g. a Vector3D for "Location" might be encoded at float[4] to float[6]).
 * The fixed layout is managed by an attribute definition manager, which can encode the starting offset in 
 * the attrId, if it wishes.
 * 
 * Features needed:
 * - put( int attrId, Object )
 * - get( int attrId )
 * - iterator() - in order to be able to iterate over the attributes, we need a list of defined attrIds.  AttrDefMgr can supply this
 * 
 * @author Neale
 */
public class LayoutAttrMap<T extends IAttribute> implements Cloneable, IAttributeMap<T>, Serializable {

	private static final long serialVersionUID = 1L;

	private int attrsPresent; // bit mask supporting up to 32 attributes per map
	
	private int[] ints = new int[0];
	private float[] floats = new float[0];


	/**
	 * Iterate over compact attributes, and then over fat ones.
	 */
	public class MapIterator implements Iterator<T> {

		/**
		 * Index into array of defined attributes
		 */
		int index = 0;

		/* (non-Javadoc)
		 * @see java.util.Iterator#hasNext()
		 */
		public boolean hasNext() {
			skipMissing();
			return index < getMapConfig().getAttrIds().size();
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#next()
		 */
        @SuppressWarnings("unchecked")
		public T next() {
			skipMissing();
			TIntArrayList attrIds = getMapConfig().getAttrIds();
			if (index >= attrIds.size()){
				// If we've run out of compact attrs, throw NoSuchElementException
				throw new NoSuchElementException();
			}

			// decode next compact one
			int attrId = attrIds.getQuick(index);
			LayoutAttrCodec<T> codec = getCodec(attrId);
			T attr = codec.getDecoded( LayoutAttrMap.this, attrId );
			index++;
			assert( attr != null); // should find that there is one
			return attr;
		}
        
        /** Skip over attributes that aren't in this map */
        private void skipMissing() {
        	TIntArrayList attrIds = getMapConfig().getAttrIds();
			while( index < attrIds.size() && !hasAttribute(attrIds.getQuick(index)) ){
        		index++;
        	}
        }

		/* (non-Javadoc)
		 * @see java.util.Iterator#remove()
		 */
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}



	/**
	 * Add attribute. 
	 */
	@SuppressWarnings("unchecked")
    public void addAttribute(int attrId, Object value) {
		 LayoutAttrCodec<T> codec = getCodec( attrId );
		 codec.encode( this, attrId, value);
	}


	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
    public T findAttr(int attrId) {
		if (!hasAttribute(attrId)){
			return null;
		}

		LayoutAttrCodec<T> codec = getCodec( attrId );
		T attr = codec.getDecoded( this, attrId );
		return attr;
	}


	public T put(int attrId, T value) {

		Object db2Value = value.getAsDb2Attribute();
		addAttribute( attrId, db2Value );
		return null; // TODO: Could return prev value if there was one
	}


	public T putAttr(T attr) {
		return put( attr.getAttrId(), attr);
	}

	public T removeAttr(int attrId) {
		throw new UnsupportedOperationException();
	}

	/**
	 * What should this return?  The number of attrs?  Need to check where it's used.
	 * FIXME: It's used in AttributeMap( IAttributeMap ) clone constructor for
	 * iterating over attributes.  This should therefore tell how many attributes
	 * are stored.
	 */
	public int size() {
		return floats.length + ints.length;
	}

	public Iterator<T> iterator() {
		return new MapIterator();
	}


	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		LayoutAttrMap<T> rhs = (LayoutAttrMap<T>) obj;
		return Arrays.equals(floats, rhs.floats)
				&& Arrays.equals(ints, rhs.ints);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(floats) + Arrays.hashCode(ints);
	}
	
	
	@SuppressWarnings("unchecked")
    @Override
	public LayoutAttrMap clone(){
// FIXME: implement this properly .. currently not used (at least, not as far as start
		try {
			LayoutAttrMap clone = (LayoutAttrMap) super.clone();
			clone.floats = this.floats.clone();
			clone.ints = this.ints.clone();
			return clone;
//			// FIXME: NOTE: Where we're clone()ing arrays, we could use Arrays.copyOf() or arraycopy instead (as clone is supposedly much slower, pre java 7)
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e); // Should always impl
		}
	}


	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		for ( IAttribute ia : this) {
			str.append("  ").append( ia.toString() ).append( System.getProperty( "line.separator") );
		}
		return str.toString();
	}


	/**
	 * Get the LayoutMapConfig that for this map
	 * We will have at least 2 different configs: one for the attributes, and one for the constraints.
	 * This could be overridden in LayoutConstraintMap to get the other one
	 */
	protected LayoutMapConfig getMapConfig() {
		return LayoutMapConfig.getInstance();
	}



	/** 
	 * Expose the array to allow multiple writes to be optimised by the compiler 
	 * (multiple calls to set( index, value ) would be dire)
	 */
	public final int[] getInts() {
		return ints;
	}

	/** 
	 * Expose the array to allow multiple writes to be optimised by the compiler 
	 * (multiple calls to set( index, value ) would be dire)
	 */
	public final float[] getFloats() {
		return floats;
	}


	public final int getIndex(int attrId) {
		int index = getMapConfig().getIndex(attrId);
		return index;
	}

	public final int getIndexQuick(int attrId) {
		return getMapConfig().getIndexQuick(attrId);
	}
	

	/**
	 * Get the index of this attribute when we are encoding to ints[].
	 * We specify the encodedLength so that index can ensure we have sufficient space.
	 * @return
	 */
	public final int getIndexForIntsWrite(int attrId, int encodedLength) {
		markAttributePresent(attrId); // If we get for write, we expect it to be written to
		int index = getMapConfig().getIndex(attrId);
		ensureIntsCapacity(index + encodedLength);
		return index;
	}


	public final int getLength( int attrId ){
		return getMapConfig().getLength(attrId);
	}
	
	/**
	 * Get the index of this attribute when we are encoding to floats[].
	 * We specify the encodedLength so that index can ensure we have sufficient space.
	 * @return
	 */
	public final int getIndexForFloatsWrite(int attrId, int encodedLength) {
		markAttributePresent(attrId); // If we get for write, we expect it to be written to
		int index = getMapConfig().getIndex(attrId);
		ensureFloatsCapacity(index + encodedLength);
		return index;
	}

	
	private void ensureIntsCapacity( int neededSize ) {
		if (neededSize <= ints.length) {
			return;
		}
		// not enough capacity for neededSize, so increase size
		int[] newArray = new int[neededSize];
		System.arraycopy(ints, 0, newArray, 0, ints.length);
		ints = newArray;
	}
	
	private void ensureFloatsCapacity( int neededSize ) {
		if (neededSize <= floats.length) {
			return;
		}
		// not enough capacity for neededSize, so increase size
		float[] newArray = new float[neededSize];
		System.arraycopy(floats, 0, newArray, 0, floats.length);
		// Fill extra space with Float.NaN, so we can detect blank space
		for(int i = floats.length; i < newArray.length; i++) {
			newArray[i] = Float.NaN;
		}
		
		floats = newArray;
	}
	
	private void markAttributePresent( int attrId ){
		int sequence = AttrDefinitionMgr.getAttrIndex(attrId);
		assert( sequence < 30 ); // gone for 30 so I don't have to worry about signed int :)
		
		int flag = 1 << sequence;
		
		attrsPresent |= flag;
	}
	
	public final boolean hasAttribute(int attrId) {
		int sequence = AttrDefinitionMgr.getAttrIndex(attrId);
		assert( sequence < 30 ); // gone for 30 so I don't have to worry about signed int :)
		
		int flag = 1 << sequence;

		return ( attrsPresent & flag) != 0;
	}


	
	@SuppressWarnings("unchecked")
	protected LayoutAttrCodec<T> getCodec(int attrId) {
		return (LayoutAttrCodec<T>) LayoutCodecManager.getCodec( attrId );
	}

    /**
     * Does what used to be done in Branch.qualify
     */
	public boolean consistentFor(IAttributeConstraint constraint, int splitId) {
		 LayoutAttrCodec<T> codec = getCodec( splitId );
		 return codec.consistent( this, splitId, constraint);
	}
}
