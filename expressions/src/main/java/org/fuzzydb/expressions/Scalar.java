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

import java.io.Serializable;
import java.util.Date;


/**
 * A number on which we want to be able to perform numerical operations
 * .
 * @author Adrian
 *
 */
public final class Scalar implements Comparable<Scalar>, Serializable {

    private static final long serialVersionUID = 1L;

    private WrappedValue value;

    public Scalar(WrappedValue wv) {
        super();
        value = wv;
    }

    public Scalar(int i) {
        super();
        value = new WrappedInteger(i);
    }

    public Scalar(float f) {
        super();
        value = new WrappedFloat(f);
    }

    public Scalar(long l) {
        super();
        value = new WrappedLong(l);
    }

    public Scalar(double d) {
        super();
        value = new WrappedDouble(d);
    }

    public Scalar(Date d) {
        super();
        value = new WrappedDate(d);
    }

    public Scalar(Comparable<?> c) {
        super();
        if (c instanceof Integer) {
        	value = new WrappedInteger(((Integer) c).intValue());
        }
        else if (c instanceof Long) {
        	value = new WrappedLong(((Long) c).longValue());
        }
        else if (c instanceof Float) {
        	value = new WrappedFloat(((Float) c).floatValue());
        }
        else if (c instanceof Double) {
        	value = new WrappedDouble(((Double) c).doubleValue());
        }
        else if (c instanceof Date) {
        	value = new WrappedDate((Date) c);
        }
        else if (c instanceof Enum<?>) {
        	value = new WrappedEnum((Enum<?>) c);
        }
        else {
        	value = new WrappedComparable(c);
        }
    }

    /*
	public Scalar(Date d) {
		super();
		value = new WrappedDate(d);
	}
     */
    public Scalar add(Scalar rhs) {
        return new Scalar(value.add(rhs.getValue()));
    }

    public Scalar sub(Scalar rhs) {
        return new Scalar(value.sub(rhs.getValue()));
    }

    public Scalar mult(Scalar rhs) {
        return new Scalar(value.mult(rhs.getValue()));
    }

    public Scalar div(Scalar rhs) {
        return new Scalar(value.div(rhs.getValue()));
    }

    public WrappedValue getValue() {
        return value;
    }

    public int compareTo(Scalar rhs) {
        return value.compareTo(rhs.getValue());
    }
}
