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
package com.wwm.attrs;


import com.wwm.attrs.internal.IConstraintMap;
import com.wwm.db.core.Settings;
import com.wwm.db.whirlwind.internal.IAttribute;
import com.wwm.db.whirlwind.internal.IAttributeMap;

/**
 * Factory class to give a configurable implementation of an AttributeMap (IAttributeMap interface)
 */
public class AttributeMapFactory {

	@SuppressWarnings("unchecked")
	static public Class<IConstraintMap> getConstraintMapClass() {
	    try {
	        return (Class<IConstraintMap>) Class.forName(Settings.getInstance().getConstraintMapClassName());
	    } catch (ClassNotFoundException e) {
	        throw new RuntimeException(e); // Fatal error if we can't find it.
	    }
	}

	@SuppressWarnings("unchecked")
	static public <T extends IAttribute> Class<? extends IAttributeMap<T>> getAttributeMapClass( Class<T> clazz) {
		try {
			return (Class<? extends IAttributeMap<T>>) Class.forName(Settings.getInstance().getAttributeMapClassName());
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e); // Fatal error if we can't find it.
		}
	}

	public static <T extends IAttribute> IAttributeMap<T> newInstance(Class<T> clazz) {
        try {
        	IAttributeMap<T> map = AttributeMapFactory.getAttributeMapClass(clazz).newInstance();
            return map;
        } catch (InstantiationException e) {
            throw new RuntimeException(e); // Can't instantiate an interface or abstract class
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e); // Need public constructor, or whatever
        }
	}

    public static IConstraintMap newConstraintMap() {
        try {
            IConstraintMap map = AttributeMapFactory.getConstraintMapClass().newInstance();
            return map;
        } catch (InstantiationException e) {
            throw new RuntimeException(e); // Can't instantiate an interface or abstract class
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e); // Need public constructor, or whatever
        }
    }

}
