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
package org.fuzzydb.expressions;

import java.util.Date;

public class WrappedDate implements WrappedValue {

    private static final long serialVersionUID = 3833461825370271799L;

    private Date date;

    public WrappedDate() {
        super();
        date = new Date();
    }

    public WrappedDate(Date date) {
        super();
        this.date = date;
    }

    public int compareTo(WrappedValue arg0) {
        if (arg0 instanceof WrappedDate) {
            WrappedDate rhs = (WrappedDate) arg0;
            return date.compareTo(rhs.getDate());
        }
        return 0; // FIXME: Please document why this returns items as being equal, when they re not of the same class!!
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return date.toString();
    }

    public Comparable getComparable() {
        return date;
    }

    public WrappedValue add(WrappedValue rhs) {
        throw new UnsupportedOperationException("Adding Date objects");
    }

    public WrappedValue div(WrappedValue rhs) {
        throw new UnsupportedOperationException("Dividing Date objects");
    }

    public WrappedValue mult(WrappedValue rhs) {
        throw new UnsupportedOperationException("Multiplying Date objects");
    }

    public WrappedValue sub(WrappedValue rhs) {
        throw new UnsupportedOperationException("Subtracting Date objects");
    }
}
