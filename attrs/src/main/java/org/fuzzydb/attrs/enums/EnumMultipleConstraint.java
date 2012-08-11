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



import org.fuzzydb.attrs.internal.BranchConstraint;
import org.fuzzydb.core.whirlwind.internal.IAttribute;
import org.fuzzydb.util.BitSet64;




/**
 * Scoring from EMC -> EMV, if we've OR'ed values to give constraint of the branch, then we can't use that as
 * the item to score, as this might be {Running}, {Skipping}, {Jumping}, for 3 different entries, but would
 * look like a strong match for {Running, Skipping, Jumping}.
 * 
 * To make this more efficient, we've introduced "lowestCount", which then means that
 * if we get {Running} -> {Running, Skipping, Jumping} but also know lowest count = 2,
 * then we know that the max possible reverse score is 1 out of 2.
 *  
 * Scoring from EMV -> EMC, however might be okay, as EMC would represent the largest possible number of matches
 * and actual match might be lower.
 * 
 * @author ac
 *
 */
public class EnumMultipleConstraint extends BranchConstraint {

    private static final long serialVersionUID = 1L;

    BitSet64 bits = new BitSet64();
    int lowestCount = EnumDefinition.MAX_ENTRIES;

	
	public EnumMultipleConstraint(int attrId, EnumMultipleValue value) {
		super(attrId);
		bits = (BitSet64) value.getBitSet().clone();
		lowestCount = (byte) bits.cardinality();
	}

	public EnumMultipleConstraint(int attrId, short defId, long words, boolean inclNS, int lowestCount) {
		super(attrId);
		bits.setWord( words );
		setIncludesNotSpecified(inclNS);
		this.lowestCount = lowestCount;
	}

	public EnumMultipleConstraint(EnumMultipleConstraint clonee) {
		super(clonee);
        bits = (BitSet64)clonee.bits.clone();
        lowestCount = clonee.lowestCount;
	}
	
	@Override
	protected boolean expandNonNull(IAttribute attr) {
        EnumMultipleValue value = (EnumMultipleValue)attr;
        boolean result = ! consistent(attr); // if it wasn't consistent, then we are going to expand it, so return true.

        // Expand happens by ORing the bits into our own local mask
        bits.or(value.getBitSet()); 
        
        // and... setting the lowest cardinality (count of bits that are true) lower if it's lower
        lowestCount = (byte)Math.min(lowestCount, value.getBitSet().cardinality());
        return result;
	}

	@Override
	public boolean equals(Object rhs) {
        if (!(rhs instanceof EnumMultipleConstraint)) {
            return false;
        }
        EnumMultipleConstraint val = (EnumMultipleConstraint) rhs;
        assert(getAttrId() == val.getAttrId() && super.equals(val));
        return bits.equals(val.bits) && lowestCount == val.lowestCount;
	}

	@Override
	public String toString () {
        
        return attrId + ": " +  bits.toString() + ", >= " + lowestCount;
	}

	@Override
	public EnumMultipleConstraint clone() {
		return new EnumMultipleConstraint(this);
	}

	public int getLowestCount() {
		return lowestCount;
	}

	/**
	 * @return a BitSet where bit[i] indicates the enumValue with index, i, exists in this constraint.
	 */
	public BitSet64 getBitSet() {
		return bits;
	}

    
	/* (non-Javadoc)
	 * @see likemynds.db.indextree.attributes.BranchConstraint#consistent(likemynds.db.indextree.attributes.Attribute)
	 */
	@Override
	public boolean consistent(IAttribute attribute) {
		// ALGO:
		// - Is consistent if there are no bit set in attribute that aren't set in this constraint.
		// i.e. it must be true that: attrBits & bcBits == attrBits
		// AND...
		// - The number of bits set in attribute is >= lowestCount
		if (attribute == null){
			return isIncludesNotSpecified();
		}
        EnumMultipleValue v = (EnumMultipleValue)attribute;
        long attrBits = v.getBitSet().getWord();

        return consistent( attrBits );
	}

	/**
	 * Version of consistent that takes an enum multipleAttribute, represented as a word, and checks that it is
	 * consistent.  This is used as support to give direct access from LayoutAttrMap and CompactAttrMap
	 * @return
	 */
	final public boolean consistent(long attrBits) {
		// ALGO:
		// - Is consistent if there are no bit set in attribute that aren't set in this constraint.
		// i.e. it must be true that: attrBits & bcBits == attrBits
		// AND...
		// - The number of bits set in attribute is >= lowestCount
        long maskedBits = attrBits & bits.getWord();

        return maskedBits == attrBits && Long.bitCount(attrBits) >= lowestCount;
	}


}
