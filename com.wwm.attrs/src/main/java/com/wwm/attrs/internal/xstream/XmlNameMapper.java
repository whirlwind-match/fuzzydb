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
package com.wwm.attrs.internal.xstream;

import java.util.TreeMap;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class XmlNameMapper<T> implements Converter {

	private final Class<T> myclazz;
	private final TreeMap<String, T> mappeddata;

	public XmlNameMapper(Class<T> myclazz, TreeMap<String, T> mappedData) {
		this.myclazz = myclazz;
		this.mappeddata = mappedData;
	}

	public boolean canConvert(@SuppressWarnings("rawtypes") Class clazz) {
		return clazz.equals(myclazz);
	}

	public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
		// UNSUPPORTED
		assert false;
	}

	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		String xmlFile = reader.getValue();
		T value = mappeddata.get(xmlFile);
		if (value == null) {
			throw new UnsupportedOperationException("Unknown XML file :: " + xmlFile);
		}
		return value;
	}

}
