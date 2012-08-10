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

import java.util.TreeSet;

import org.fuzzydb.attrs.internal.BranchConstraint;
import org.fuzzydb.core.whirlwind.internal.IAttribute;





public class StringConstraint extends BranchConstraint {

    private static final long serialVersionUID = 1L;

    private boolean delimited = false;
    private char delimiter = ' '; 
    private TreeSet<String> values = new TreeSet<String>();     
    
    public TreeSet<String> getValues() {
        return values;
    }
    
	/* (non-Javadoc)
	 * @see likemynds.db.indextree.attributes.BranchConstraint#consistent(likemynds.db.indextree.attributes.Attribute)
	 */
	@Override
	public boolean consistent(IAttribute attribute) {
		if (attribute == null){
			return isIncludesNotSpecified();
		}
		return true;
	}
	
	/**
	 * @param attrId
	 */
    public StringConstraint(int attrId, StringValue value) {
        super(attrId);
        expandNonNull(value);
    }

    public StringConstraint(int attrId, StringMultiValue value) {
        super(attrId);
        expandNonNull(value);
    }

    public StringConstraint(int attrId, StringValue value, char delimter) {
        super(attrId);
        this.delimited = true;
        this.delimiter = delimter;
        expandNonNull(value);
    }

    public StringConstraint(int attrId, StringMultiValue value, char delimter) {
        super(attrId);
        this.delimited = true;
        this.delimiter = delimter;
        expandNonNull(value);
    }

	public StringConstraint(StringConstraint clonee) {
		super(clonee);
        delimited = clonee.delimited;
        delimiter = clonee.delimiter; 
        values.addAll(clonee.values);
	}
	
	@Override
	protected boolean expandNonNull(IAttribute value) {
        if (!delimited) {
            return true;
        }
        
        if (value instanceof StringValue) {
            return expand(((StringValue) value).getValue());
        } else {
            StringMultiValue val = (StringMultiValue) value;
            boolean result = false;
            for (String str: val.getValue()) {
                if (expand(str) == true) {
                    result = true;
                }
            }
            return result;
        }
	}

    private boolean expand(String value) {
        String start = value.split(String.valueOf(delimiter), 2)[0];
        if (!values.contains(start)) {
            values.add(start);
            return true;    
        }
        return false;
    }    
    
	@Override
	public boolean equals(Object rhs) {
        if(!(rhs instanceof StringConstraint)) {
            return false;
        }
 		StringConstraint val = (StringConstraint) rhs;
		assert(getAttrId() == val.getAttrId() && super.equals(val));
        return values.equals(val.values);
	}

	@Override
	public String toString () {
        if (delimited) {
            return "{ " + values + " }";
        }
        return "StringConstraint";
	}

	@Override
	public StringConstraint clone() {
		return new StringConstraint(this);
	}

	@Override
	public boolean isExpandedByNonNull(IAttribute value) {
        if (!delimited) {
            return true;
        }
        if (value instanceof StringValue) {
            return canExpand(((StringValue) value).getValue());
        } else {
            StringMultiValue val = (StringMultiValue) value;
            for (String str: val.getValue()) {
                if (canExpand(str) == true) {
                    return true;
                }
            }
        }
        return false;
	}
    private boolean canExpand(String value) {
        String start = value.split(String.valueOf(delimiter), 2)[0];
        if (!values.contains(start)) {
            return true;    
        }
        return false;
    }    

    
    public boolean isDelimited() {
        return delimited;
    }

    public char getDelimiter() {
        return delimiter;
    }
}
