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
package com.wwm.db.internal.server;

import java.io.Serializable;

import org.fuzzydb.client.marker.MergeableContainer;
import org.fuzzydb.core.whirlwind.internal.AttributeCache;


/**
 * A tuple storing an object and it's sequential version.
 *
 * @param <T>
 */
public class VersionedObject<T> implements Serializable, MergeableContainer {

	private static final long serialVersionUID = 1L;
	
	private final int version; // NOTE: Was Long, but an app surely shouldn't update 4 billion times!
	private final T object;
	
	
	public static <V> VersionedObject<V> nextVersion(V obj, int objectVersion) {
		return new VersionedObject<V>(obj, objectVersion + 1);
	}

	/**
	 * Constructs VersionedObject for the first version.
	 * This should be used for create() operations only.
	 * @param object
	 */
	public VersionedObject(T object ){
		this(object, 1);
	}
	
	private VersionedObject(T object, int version) {
		this.version = version;
		this.object = object;
	}

	public T getObject() {
		return object;
	}

	public int getVersion() {
		return version;
	}

	public void mergeDuplicates(AttributeCache cache) {
		if (object instanceof MergeableContainer){
			((MergeableContainer)object).mergeDuplicates(cache);
		} 
		// FIXME: Also deal with other mergeables, such as common strings, but not triggered by java.lang.String
	}

}
