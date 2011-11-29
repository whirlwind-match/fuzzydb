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
package com.wwm.db.internal;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

/**
 * A weak reference holder for the an object being used as the key to a map.
 * Key hash/equality is done using System.identityHashCode().
 * Equality is that two keys refer to the same object instance (i.e. not just clones).  
 * 
 * FIXME: I think there is a bug here, in that hashcode is only computed once, but the referent may
 * be modified.  Thus the hashcode may have changed, but the referred object is actually the same
 * one.  It seems possible to put two references to the object into the map.
 */
public class MetaMapKey extends WeakReference<Object> {
	int hashcode;
	
	public MetaMapKey(Object referent, ReferenceQueue<Object> q) {
		super(referent, q);
		hashcode = System.identityHashCode(referent);
	}
	
	@Override
	public boolean equals(Object o) {
		MetaMapKey rhs = (MetaMapKey)o;
		if (this.hashCode() == rhs.hashCode()) {
			if (get() == rhs.get()) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public int hashCode() {
//		int identityHashCode = System.identityHashCode(get());

		// FIXME: This assertion does fail. This needs clarifying
//		assert identityHashCode == hashcode 
//			: "If this fails ever then this fn is correct, and we should not cache hashcode as a field";
//		return identityHashCode;

		// FIXME: Reinstate line below for performance if we discover why it's correct.  We could also profile this function.
		// Reinstated because MetaMap.flush() assertion fails.
		return hashcode;
	}
	
	@Override
	public String toString() {
		return "#=" + hashcode;
	}

}
