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
package com.wwm.db.internal.comms.messages;

public class RetrieveByKeyCmd extends TransactionCommand {

	private static final long serialVersionUID = 1L;

	private final Comparable<?> key;
	private final String fieldName;

	private final String namespace;

	private final Class<?> forClass;
	
	public RetrieveByKeyCmd(int storeId, String namespace, int cid, int tid, Class<?> forClass, final Comparable<?> key, final String fieldName) {
		super(storeId, cid, tid);
		this.key = key;
		this.fieldName = fieldName;
		this.namespace = namespace;
		this.forClass = forClass;
	}

	public String getFieldName() {
		return fieldName;
	}

	public Comparable<?> getKey() {
		return key;
	}

	public String getNamespace() {
		return namespace;
	}

	public Class<?> getForClass() {
		return forClass;
	}
}
