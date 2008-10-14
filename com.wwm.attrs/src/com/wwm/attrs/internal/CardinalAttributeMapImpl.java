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
package com.wwm.attrs.internal;

import java.util.Collection;
import java.util.Map;
import java.util.Set;


import com.wwm.db.whirlwind.AttributeRemapper;
import com.wwm.db.whirlwind.CardinalAttributeMap;
import com.wwm.db.whirlwind.StringAttributeMap;
import com.wwm.db.whirlwind.internal.IAttribute;
import com.wwm.db.whirlwind.internal.IAttributeMap;


/**
 * Implements view as seen at client, but also view wanted at server end.
 * @author Neale
 *
 */
public class CardinalAttributeMapImpl extends AttributeMap<IAttribute> implements CardinalAttributeMap<IAttribute> {

	private static final long serialVersionUID = 1L;

	public CardinalAttributeMapImpl() {
		super();
	}

	public CardinalAttributeMapImpl(IAttributeMap<IAttribute> attributes) {
		super(attributes);
	}


	public StringAttributeMap<IAttribute> remap(AttributeRemapper remapper) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IAttribute put(int key, IAttribute value) {
		// Allows attrId to have not been set yet.
		value.setAttrId(key);
		return super.putAttr(value);
	}

	public IAttribute get(int key) {
		return super.findAttr(key);
	}


	public IAttribute remove(Object key) {
		return super.removeAttr((Integer) key);
	}


	public void clear() {
		throw new UnsupportedOperationException();
	}

	public boolean containsKey(Object key) {
		throw new UnsupportedOperationException();
	}

	public boolean containsValue(Object value) {
		throw new UnsupportedOperationException();
	}

	public Set<java.util.Map.Entry<Integer, IAttribute>> entrySet() {
		throw new UnsupportedOperationException();
	}

	public boolean isEmpty() {
		throw new UnsupportedOperationException();
	}

	public Set<Integer> keySet() {
		throw new UnsupportedOperationException();
	}

	public void putAll(Map<? extends Integer, ? extends IAttribute> t) {
		throw new UnsupportedOperationException();
	}


	public Collection<IAttribute> values() {
		throw new UnsupportedOperationException();
	}

	public IAttributeMap<IAttribute> getAttributeMap() {
		return this;
	}


}
