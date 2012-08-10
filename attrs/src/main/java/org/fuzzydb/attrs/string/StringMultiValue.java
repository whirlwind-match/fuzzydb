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
import java.util.ArrayList;

import org.fuzzydb.attrs.internal.Attribute;
import org.fuzzydb.core.whirlwind.internal.IAttribute;
import org.fuzzydb.core.whirlwind.internal.IMergeable;





public class StringMultiValue extends Attribute implements IMergeable, Comparable<StringMultiValue>, Serializable {

    private static final long serialVersionUID = 1L;

    private boolean delimited = false;
    private char delimiter = ' ';
    private ArrayList<String> value;


    /**
     * Create an instance value from the supplied string from the definition.
     * @param attrId
     * @param definition
     * @param svalue
     */
    public StringMultiValue(int attrId, ArrayList<String> value) {
        super(attrId);
        this.value = value;
    }

    public StringMultiValue(int attrId, ArrayList<String> value, char delimiter) {
        super(attrId);
        this.delimited = true;
        this.delimiter = delimiter;
        this.value = value;
    }

    public StringMultiValue(StringMultiValue rhs) {
        super(rhs);
        this.value.addAll(rhs.value);
        this.delimited = rhs.delimited;
        this.delimiter = rhs.delimiter;
    }

    /**
     * Implement Comparable interface to allow values to be sorted
     */
    public int compareTo(StringMultiValue rval) {
        assert(rval.getAttrId() == this.getAttrId()); // Should only be called on matching ID
        if (value.size() > rval.value.size()) {
            return 1;
        } if (value.size() > rval.value.size()) {
            return -1;
        }
        for (int i = 0; i < value.size(); i++) {
            int result = value.get(i).compareTo(rval.value.get(i));
            if (result != 0) {
                return result;
            }
        }
        return 0;
    }

    public ArrayList<String> getValue() {
        return value;
    }

    @Override
    public String toString(){
        return "{ " + value + " }";
    }

    public int compareAttribute(IAttribute rhs) {
        return compareTo((StringMultiValue)rhs);
    }

    @Override
    public StringConstraint createAnnotation() {
        if (delimited) {
            return new StringConstraint(getAttrId(), this, delimiter);
        }
        return new StringConstraint(getAttrId(), this);
    }

    @Override
    public StringMultiValue clone() {
        return new StringMultiValue(this);
    }

    public boolean isDelimited() {
        return delimited;
    }

    public char getDelimiter() {
        return delimiter;
    }
}
