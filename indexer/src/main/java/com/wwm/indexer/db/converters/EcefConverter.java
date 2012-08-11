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
import org.fuzzydb.dto.attributes.Attribute;
import org.fuzzydb.dto.attributes.Point3DAttribute;
import org.fuzzydb.dto.dimensions.IPoint3D;



public class EcefConverter implements AttributeConverter {

    private static final AttributeConverter instance = new EcefConverter();
    
    public static AttributeConverter getInstance() {
    	return instance;
    }

    public Class<EcefVector> getIAttributeClass() {
        return EcefVector.class;
    }

    public Class<Point3DAttribute> getObjectClass() {
        return Point3DAttribute.class;
    }

    public Point3DAttribute convert(String name, IAttribute attribute) {
        return new Point3DAttribute(name, (IPoint3D) attribute);
    }

    public EcefVector convertToInternal(int attrid, Attribute<?> object) {
        Point3DAttribute attr = (Point3DAttribute) object;
        IPoint3D point = attr.getPoint();
        EcefVector result = new EcefVector(attrid, point);
        return result;
    }

}
