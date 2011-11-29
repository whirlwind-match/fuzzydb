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
package com.wwm.attrs.bool;

import gnu.trove.TIntObjectHashMap;

import com.wwm.attrs.internal.Attribute;
import com.wwm.db.whirlwind.internal.IAttribute;
import com.wwm.db.whirlwind.internal.IMergeable;

/**
 * @author Neale
 */
public class BooleanValue extends Attribute implements IBooleanValue, IMergeable {

	private static final long serialVersionUID = 3616447890939983923L;
	private final boolean isTrue;

    public BooleanValue(){
    	super( 0 ); // illegal
        this.isTrue = true; // default
    }
    
    public BooleanValue(int attrId, boolean isTrue) {
    	super( attrId );
    	this.isTrue = isTrue;
    }

    public BooleanValue(BooleanValue clonee){
    	super( clonee );
        this.isTrue = clonee.isTrue;
    }
    
    @Override 
    public final BooleanValue clone() {
    	return new BooleanValue(this);
    }
    
    public boolean isTrue() {
    	return this.isTrue;
    }

	public int compareAttribute(IAttribute rhs) {
		BooleanValue r = (BooleanValue)rhs;
		if (isTrue != r.isTrue) return isTrue ? -1 : 1;
		return 0;
	}

	@Override
	public BooleanConstraint createAnnotation() {
		return new BooleanConstraint(getAttrId(), isTrue);
	}

	@Override
	public Object getAsDb2Attribute() {
		return Boolean.valueOf(isTrue);
	}
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BooleanValue)) {
            return false;
        }
        BooleanValue bv = (BooleanValue)obj;
        return (bv.attrId == attrId && bv.isTrue == isTrue);
    }

    @Override
    public int hashCode() {
    	// Add something unique'ish for true
    	return super.hashCode() + ((this.isTrue) ? 0x80000041 : 0 ) ;
    }
    
	// As BooleanValues are intransient, then we'll keep a cache of them to save GC work
	private final static TIntObjectHashMap<BooleanValue> trueValues = new TIntObjectHashMap<BooleanValue>();
	private final static TIntObjectHashMap<BooleanValue> falseValues = new TIntObjectHashMap<BooleanValue>();

	public static BooleanValue valueOf(int attrId, boolean isTrue) {
		if (isTrue){
			return getCachedInstance( attrId, trueValues, true );
		} else {
			return getCachedInstance( attrId, falseValues, false);
		}
	}
	
	private static BooleanValue getCachedInstance(int attrId, TIntObjectHashMap<BooleanValue> cache, boolean isTrue) {
		BooleanValue bv = cache.get(attrId);
		if (bv == null) {
			bv = new BooleanValue( attrId, isTrue);
			cache.put(attrId, bv);
		}
		return bv;
	}
	
}
