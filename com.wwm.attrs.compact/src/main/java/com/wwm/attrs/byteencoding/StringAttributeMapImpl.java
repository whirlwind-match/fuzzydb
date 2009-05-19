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

package com.wwm.attrs.byteencoding;
import com.wwm.attrs.internal.AttrDefinitionMgr;
import com.wwm.attrs.internal.SyncedAttrDefinitionMgr;
import com.wwm.db.Store;
import com.wwm.db.whirlwind.AttributeRemapper;
import com.wwm.db.whirlwind.CardinalAttributeMap;
import com.wwm.db.whirlwind.StringAttributeMap;
import com.wwm.db.whirlwind.internal.IAttribute;
import com.wwm.db.whirlwind.internal.IAttributeMap;
import com.wwm.model.attributes.Attribute;
import com.wwm.util.DynamicRef;

/*
 * Strategy: create initial class in nice easily understandable code, and then consider overriding readObject()
 * to return a more compact implementation on read from stream.  e.g. ByteBuffer could become byte[] of fixed length on
 * read. 
 */


public class StringAttributeMapImpl extends CompactAttrMap<IAttribute> implements StringAttributeMap<Object> {

	private static final long serialVersionUID = 1L;

	/**
	 * Store not needed at server end as it was only needed this end to resolve strings
	 * to attribute Id's.
	 * TODO: What about when we read back from server to client.  Will we want
	 * to decode id's to strings... probably!
	 */
	transient private Store store;

	public StringAttributeMapImpl(Store store) {
		this.store = store;
	}

	public IAttributeMap<IAttribute> getAttributeMap() {
		return this; // a fancy cast
	}

	// TODO: I think that remap is going to disappear... as we'll just support a single internal impl.
	public CardinalAttributeMap<Object> remap(AttributeRemapper remapper) {
		throw new UnsupportedOperationException();
	}

	
	public void put(Attribute attr) {
		int attrId = getAttrDefinitions().getObject().getAttrId( attr.getName(), attr.getClass() );
		addAttribute(attrId, attr );
	}

	private DynamicRef<? extends AttrDefinitionMgr> getAttrDefinitions() {
		return SyncedAttrDefinitionMgr.getInstance( store );
	}
	
	
	@Override
	public boolean equals(Object obj) {
		return super.equals(obj); // i.e. ignore attribute specified in this wrapper.
	}

	/**
	 * Get an empty attribute map, for use with the default (/current?) namespace on the specified store.
	 * @param store
	 * 		used to supply the AttributeMap with access to the database to find or add attribute configs.
	 */
	public static StringAttributeMap<Object> getStringAttributeMap(Store store) {
	    return new StringAttributeMapImpl( store );
	}

}
