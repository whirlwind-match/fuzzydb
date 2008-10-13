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

import java.io.Serializable;

import com.wwm.db.MetaData;


/**
 * Wrapper for Object, Ref and version, which gives us the unique value of an object at a given database version.
 * This is never exposed to the client, so RefImpl is used throughout to avoid class-cast checks on
 * the server.
 */
public class MetaObject<T> implements MetaData, Serializable {
	private static final long serialVersionUID = 1L;
	private RefImpl<T> ref;
	/** Sequential object version incremented on each update of the object */
	private int objectVersion; // TODO: Note: this was long, but seemed a bit OTT that we'd have 4 billion updates of an individual object. It'd have to be a poor scaling app to do that
	private T object;

	public MetaObject(RefImpl<T> ref, int version, T object) {
		super();
		this.ref = ref;
		this.objectVersion = version;
		this.object = object;
	}

	public RefImpl<T> getRef() {
		return ref;
	}

	public int getVersion() {
		return objectVersion;
	}

	public void setVersion(int version) {
		this.objectVersion = version;
	}

	public void incrementVersion() {
		objectVersion++;
	}

	public T getObject() {
		return object;
	}


	@Override
	public String toString() {
		return ref.toString()+ ":v" + objectVersion + "->" + object;
	}

}
