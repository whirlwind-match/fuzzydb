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
package com.wwm.attrs.simple;


import com.wwm.attrs.internal.Attribute;
import com.wwm.db.whirlwind.internal.IAttribute;

/**
 * @author Neale
 */
public class FloatHave extends Attribute implements Comparable<FloatHave>, IFloat {

    private static final long serialVersionUID = 3257285842249857075L;
    protected float value;

    /**
     * Default constructor.  Only for use in instantiating an object, so we can find the selector class.
     */
    public FloatHave() {
        super( 0 );
        assert(false); // We want to know if we're accidentally using this!
        value = Float.NaN;
    }

    /**
     * @param attrId
     */
    public FloatHave(int attrId) {
        super(attrId);
    }

    /**
     * @param attrId
     */
    public FloatHave(int attrId, float value) {
        super(attrId);
        this.value = value;
    }

    public FloatHave(FloatHave clonee) {
        super(clonee);
        this.value = clonee.value;
    }

    /* (non-Javadoc)
     * @see com.wwm.attrs.simple.IFloat#getValue()
     */
    public float getValue() {
        return value;
    }

    public int compareTo(FloatHave fv) {
        if (value < fv.value) {
            return -1;
        } else if (value == fv.value) {
            return 0;
        } else {
            return 1;
        }
    }

    public int compareAttribute(IAttribute rhs) {
        return compareTo((FloatHave)rhs);
    }

    @Override
    public FloatConstraint createAnnotation() {
        return new FloatConstraint(getAttrId(), value, value);
    }

    @Override
    public FloatHave clone() {
        return new FloatHave(this);
    }

    @Override
    public Object getAsDb2Attribute() {
        return new Float(value);
    }
}
