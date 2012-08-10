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
package org.fuzzydb.attrs.internal;


import org.fuzzydb.attrs.AttributeDefinitionService;
import org.fuzzydb.attrs.bool.BooleanValue;
import org.fuzzydb.attrs.enums.EnumDefinition;
import org.fuzzydb.attrs.enums.EnumExclusiveValue;
import org.fuzzydb.attrs.simple.FloatValue;
import org.fuzzydb.client.Store;
import org.fuzzydb.client.whirlwind.StringAttributeMap;

import com.wwm.db.whirlwind.internal.IAttribute;
import com.wwm.db.whirlwind.internal.IAttributeMap;
import com.wwm.model.attributes.Attribute;
import com.wwm.model.attributes.BooleanAttribute;
import com.wwm.model.attributes.EnumAttribute;
import com.wwm.model.attributes.FloatAttribute;


/*
 * Strategy: create initial class in nice easily understandable code, and then consider overriding readObject()
 * to return a more compact implementation on read from stream.  e.g. ByteBuffer could become byte[] of fixed length on
 * read. 
 */


public class CartmanStringAttributeMapImpl extends AttributeMap<IAttribute> implements StringAttributeMap<Object> {

	private static final long serialVersionUID = 1L;

	transient private Store store;

	public CartmanStringAttributeMapImpl(Store store) {
		this.store = store;
	}

	public IAttributeMap<IAttribute> getAttributeMap() {
		return this; // a fancy cast
	}

	public void put(Attribute attr1) {
		// FIXME: This is a big mess, and we don't use StringAttributeMap...
		int attrId = getAttrDefinitions().getAttrId( attr1.getName(), attr1.getClass() );
		if (attr1 instanceof BooleanAttribute){
			IAttribute attr = new BooleanValue( attrId, ((BooleanAttribute) attr1).getValue());
			putAttr( attr );
		}
		else if ( attr1 instanceof FloatAttribute) {
			IAttribute attr = new FloatValue( attrId, ((FloatAttribute) attr1).getValue());
			putAttr( attr );
		}
		else if ( attr1 instanceof EnumAttribute) {
			// If supplied our existing attributes, then just set the attrId, allowing Jason to supply zero
			EnumAttribute enumAttribute = (EnumAttribute) attr1;
			EnumDefinition def = getAttrDefinitions().getEnumDefinition( enumAttribute.getEnumName());
			
			EnumExclusiveValue attr = def.getEnumValue(enumAttribute.getEnumName(), attrId);
			attr.setAttrId(attrId);
			putAttr( attr );
		}
		else {
			throw new UnsupportedOperationException();
		}
	}



	
	
	// Ignore this for now.
	public void addBoolean(String attrName, boolean value) {
		int attrId = getAttrDefinitions().getAttrId( attrName, Boolean.class );
		BooleanValue attr = new BooleanValue( attrId, value );
		putAttr( attr );
	}

	private AttributeDefinitionService getAttrDefinitions() {
		SyncedAttrDefinitionMgr adm = SyncedAttrDefinitionMgr.getInstance( store ).getObject();
		return adm;
	}

}
