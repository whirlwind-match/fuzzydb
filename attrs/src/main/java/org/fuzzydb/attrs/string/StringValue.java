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
package org.fuzzydb.attrs.string;

import java.io.Serializable;

import org.fuzzydb.attrs.internal.Attribute;
import org.fuzzydb.core.whirlwind.internal.IAttribute;
import org.fuzzydb.core.whirlwind.internal.IMergeable;



public class StringValue extends Attribute<StringValue> implements IMergeable, Comparable<StringValue>, Serializable {

    private static final long serialVersionUID = 1L;

    private boolean delimited = false;
    private char delimiter = ' ';
    private final String value;

    /**
     * Create an instance value from the supplied string from the definition.
     * @param attrId
     * @param definition
     * @param svalue
     */
    public StringValue(int attrId, String value) {
        super(attrId);
        this.value = value;
    }

    public StringValue(int attrId, String value, char delimiter) {
        super(attrId);
        this.delimited = true;
        this.delimiter = delimiter;
        this.value = value;
    }

    public StringValue(StringValue rhs) {
        super(rhs);
        this.value = rhs.value;
        this.delimited = rhs.delimited;
        this.delimiter = rhs.delimiter;
    }

    /**
     * Implement Comparable interface to allow values to be sorted
     */
    @Override
	public int compareTo(StringValue rval) {
        assert(rval.getAttrId() == this.getAttrId()); // Should only be called on matching ID
        return value.compareTo(rval.value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString(){
        return value;
    }

    @Override
	public int compareAttribute(IAttribute rhs) {
        return compareTo((StringValue)rhs);
    }

    @Override
    public StringConstraint createAnnotation() {
        if (delimited)
			return new StringConstraint(getAttrId(), this, delimiter);
        return new StringConstraint(getAttrId(), this);
    }

    @Override
    public StringValue clone() {
        return new StringValue(this);
    }

    public boolean isDelimited() {
        return delimited;
    }

    public char getDelimiter() {
        return delimiter;
    }
}
