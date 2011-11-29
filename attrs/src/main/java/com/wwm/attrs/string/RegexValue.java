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
package com.wwm.attrs.string;

import java.io.Serializable;
import java.util.regex.Pattern;


import com.wwm.attrs.internal.Attribute;
import com.wwm.db.whirlwind.internal.IAttribute;



public class RegexValue extends Attribute implements Comparable<RegexValue>, Serializable {

    private static final long serialVersionUID = 1L;

    private Pattern value;

    /**
     * Create an instance value from the supplied string from the definition.
     * @param attrId
     * @param definition
     * @param svalue
     */
    public RegexValue(int attrId, String value) {
        super(attrId);
        this.value = Pattern.compile(value);
    }

    /**
     * Copy constructor
     * @param rhs
     */
    public RegexValue(RegexValue rhs) {
        super(rhs);
        this.value = rhs.value;
    }

    /**
     * Implement Comparable interface to allow values to be sorted
     */
    public int compareTo(RegexValue rval) {
        assert(rval.getAttrId() == this.getAttrId()); // Should only be called on matching ID
        return value.toString().compareTo(rval.value.toString());
    }

    public Pattern getValue() {
        return value;
    }

    @Override
    public String toString(){
        return value.toString();
    }

    public int compareAttribute(IAttribute rhs) {
        return compareTo((RegexValue)rhs);
    }

    @Override
    public RegexConstraint createAnnotation() {
        return new RegexConstraint(getAttrId());
    }

    @Override
    public RegexValue clone() {
        return new RegexValue(this);
    }
}
