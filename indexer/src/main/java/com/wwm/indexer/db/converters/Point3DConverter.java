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
package com.wwm.indexer.db.converters;


import org.fuzzydb.attrs.location.EcefVector;
import org.fuzzydb.core.whirlwind.internal.IAttribute;

import com.wwm.model.attributes.Attribute;
import com.wwm.model.attributes.Point3DAttribute;


public class Point3DConverter implements AttributeConverter {

    private static final Point3DConverter instance = new Point3DConverter();
    
    public static Point3DConverter getInstance() {
    	return instance;
    }

	public Class<EcefVector> getIAttributeClass() {
		return EcefVector.class;
	}

	public Class<Point3DAttribute> getObjectClass() {
		return Point3DAttribute.class;
	}

	public Point3DAttribute convert(String name, IAttribute attribute) {
		return new Point3DAttribute(name, (EcefVector) attribute);
	}

	public EcefVector convertToInternal(int attrid, Attribute<?> object) {
		Point3DAttribute p3d = (Point3DAttribute) object;
		return new EcefVector(attrid, p3d.getPoint().getX(), p3d.getPoint().getY(), p3d.getPoint().getZ());
	}
}
