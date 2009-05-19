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
package com.wwm.attrs.internal;

import java.io.Serializable;

import com.wwm.db.whirlwind.internal.IAttribute;
import com.wwm.db.whirlwind.internal.IAttributeConstraint;




public class AttributeConfiguration implements Serializable {

    private static final long serialVersionUID = 7242818719540923415L;

    private int id;
	private Class<? extends IAttribute> attrClass;
	private IAttributeConstraint attrConstraint;

	public AttributeConfiguration(int id, Class<? extends IAttribute> haveClass, 
			IAttributeConstraint haveConstraintClass) {
		this.id = id;
		this.attrClass = haveClass;
		this.attrConstraint = haveConstraintClass;
	}

	public int getId() {
		return id;
	}

	public Class<? extends IAttribute> getAttrClass() {
		return attrClass;
	}

	public void setHaveClass(Class<? extends IAttribute> haveClass) {
		this.attrClass = haveClass;
	}

	public IAttributeConstraint getAttrConstraint() {
		return attrConstraint;
	}

	public void setAttrConstraint(IAttributeConstraint attrConstraint) {
		this.attrConstraint = attrConstraint;
	}

}
