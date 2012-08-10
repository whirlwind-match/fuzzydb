/******************************************************************************
 * Copyright (c) 2004-2008 Whirlwind Match Limited. All rights reserved.
 *
 * This is open source software; you can use, redistribute and/or modify
 * it under the terms of the Open Software Licence v 3.0 as published by the 
 * Open Source Initiative.
 *
 * You should have received a copy of the Open Software Licence along with this
 * application. if not, contact the Open Source Initiative (www.opensource.org)
 *****************************************************************************/
package org.fuzzydb.attrs.enums;


import java.io.Serializable;

import com.wwm.db.whirlwind.internal.IAttribute;
import com.wwm.util.BitSet64;



/**
 * EnumValue that contains multiple selections from a given enumeration,
 * e.g. from the EnumDefinition {Skiing, Hiking, WatchingTV} we could have an
 * EnumMutlipleValue {Hiking, WatchingTV}.
 * 
 * @author ac (stands for Absent Comments :o)
 */
public class EnumMultipleValue extends EnumValue implements Serializable, Comparable<EnumMultipleValue> {

    private static final long serialVersionUID = 1L;

    private BitSet64 bits = new BitSet64();

    /**
     * Constructor to create an enum representing a set of values, based on
     * NOTE: Definition must already be configured. This isn't useful for lazy config
     * @param attrId
     * @param definition
     * @param strings
     */
    public EnumMultipleValue(int attrId, EnumDefinition definition, String[] strings) {
        super(attrId, definition.getEnumDefId());
        for (String string : strings) {
            short index = definition.findIndex( string );
            bits.set(index);
        }
    }

    /**
     * Constructor for an empty one.  Shouldn't need this.
     * @param attrId
     * @param definition
     */
    public EnumMultipleValue(int attrId, EnumDefinition definition) {
        super(attrId, definition.getEnumDefId());
    }


    /**
     * Constructor to help migrate from Db1 to Db2 versions
     * @param attrId
     * @param def
     * @param values
     */
    public EnumMultipleValue(int attrId, short defId, short[] values) {
        super(attrId, defId);
        // set bits
        for (short v : values){
            bits.set(v);
        }
    }

    public EnumMultipleValue(int attrId, short defId, long values ){
        super(attrId, defId);
        bits.setWord(values);
    }


    /**
     * Copy constructor.  Must make copy of array, not just copy reference.
     * @param clonee
     */
    public EnumMultipleValue(EnumMultipleValue clonee) {
        super(clonee);
        bits = (BitSet64) clonee.bits.clone();
    }


    /**
     * @return a BitSet where bit[i] indicates the enumValue with index, i, exists in this constraint.
     */
    public BitSet64 getBitSet() {
        return bits;
    }

    /**
     * Get the values in this EMV.  NOTE: this is a low performance operation.
     * Where the bit representation is possible, use getBits()/getBits().getWord() instead.
     * @return
     */
    public short[] getValues() {
        short[] result = new short[bits.cardinality()];
        int i = 0;
        short pos = 0;
        long word = bits.getWord();
        while( i < result.length ){
            if ((word & 0x01L) != 0){
                result[i++] = pos;
            }
            pos++;
            word >>>= 1;
        }
        return result;
    }


    /**
     * add a value, ignoring if it is already added
     * @param value
     */
    public void addValue(short value) {
        bits.set( value );
    }



    /**
     * Check if specified value exists.
     * @param value
     * @return true if found
     */
    public boolean contains(short value) {
        if (bits.get(value)){
            return true; // or even return bits.get(value);
        }

        // didn't find it, so return false
        return false;
    }

    /**
     * Get the Strings defined in the Multi Enum
     * @return String versions of the values
     */
    //    public String[] getStringValues(){
    //        String[] stringValues = new String[values.length];
    //
    //        int i = 0;
    //        for (short value : values) {
    //            stringValues[i] = definition.find(value);
    //            i++;
    //        }
    //
    //        return stringValues;
    //    }

    //    public String getStringValue(short value){
    //        return definition.find(value);
    //    }


    public int compareAttribute(IAttribute rhs) {
        return compareTo((EnumMultipleValue)rhs);
    }


    /**
     * Implement Comparable interface so we can sort
     * @param rhs
     * @return
     */
    public int compareTo(EnumMultipleValue rhs) {

        long diff = bits.getWord() - rhs.bits.getWord();
        if (diff < 0) {
            return -1;
        }
        if (diff > 0) {
            return 1;
        }

        return 0;	// two arrays are identical
    }


    @Override
    public EnumMultipleConstraint createAnnotation() {
        return new EnumMultipleConstraint(getAttrId(), new EnumMultipleValue(this));
    }


    @Override
    public EnumMultipleValue clone() {
        return new EnumMultipleValue(this);
    }

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((bits == null) ? 0 : bits.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		EnumMultipleValue other = (EnumMultipleValue) obj;
		if (bits == null) {
			if (other.bits != null)
				return false;
		}
		else if (!bits.equals(other.bits))
			return false;
		return true;
	}
}
