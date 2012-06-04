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
package com.wwm.model.attributes;

import com.wwm.model.dimensions.IPoint3D;

public class Point3DAttribute extends Attribute<IPoint3D> {

	private static final long serialVersionUID = 1L;

    private IPoint3D point;

    public Point3DAttribute(String name, IPoint3D vec) {
        super(name);
        this.point = vec;
    }

    public void setPoint(IPoint3D vec) {
        this.point = vec;
    }

    public IPoint3D getPoint() {
        return point;
    }

    @Override
    public String toString() {
        return point.toString();
    }

	@Override
	public IPoint3D getValueAsObject() {
		return point;
	}
}
