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
package com.wwm.db.annotations;

/**
 * Used to mark an Index field.
 * Works on all Comparable and primitive types except boolean.
 * The Mode (Ref or Value) determines how each instance is placed in the index. Value causes a copy of the entire object to be
 * placed in the index, improving the performance when retrieving many objects. Ref causes the index to reference the object in its class table,
 * which requires one additional lookup by ref for each retrieved object. This is almost as fast when only retrieving one object.
 * 
 * When lookups will normally be done by equality and single objects retrieved, ie with id's, use Ref.
 * When ranges of keys will be looked up to return collections, use Value. 
 */

@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Target(value = java.lang.annotation.ElementType.FIELD)
public @interface Key {
	public enum Mode { Ref, Value }	// Value is faster, especially when querying for multiple objects. Ref uses much less storage space.
	Mode type() default Mode.Value;
	boolean unique() default false;
}
