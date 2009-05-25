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
package com.wwm.indexer.internal.xstream;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.wwm.attrs.enums.EnumDefinition;
import com.wwm.attrs.enums.EnumExclusiveValue;
import com.wwm.attrs.enums.EnumPreferenceMap;
import com.wwm.attrs.internal.AttrDefinitionMgr;
import com.wwm.util.DynamicRef;

public class TableToPreferenceMapConverter implements Converter {

    private DynamicRef<? extends AttrDefinitionMgr> mgr;

	public TableToPreferenceMapConverter(DynamicRef<? extends AttrDefinitionMgr> wrapper) {
		mgr = wrapper;
	}

	@SuppressWarnings("unchecked")
    public boolean canConvert(Class clazz) {
        return clazz.equals(EnumPreferenceMap.class);
    }

    public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
        // UNSUPPORTED
        assert false;
	}

	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		
		final EnumDefinition scorerEnumDef = mgr.getObject().getEnumDefinition( reader.getAttribute("scorerEnumDefinition") );
		final EnumDefinition otherEnumDef = mgr.getObject().getEnumDefinition( reader.getAttribute("otherEnumDefinition") );
		
		// Read into a table
		HtmlTableReader table = new HtmlTableReader(reader);
		table.read();

		// Create a preference map and write to it
		final EnumPreferenceMap map = new EnumPreferenceMap();
		table.foreachCell( new CellCallback(){
			public void doCell(int row, String rowHeading, int col, String colHeading, String value) {
				EnumExclusiveValue scorerAttr = scorerEnumDef.getEnumValue(rowHeading, -1); 
				EnumExclusiveValue otherAttr = otherEnumDef.getEnumValue(colHeading, -1); 
				map.add(scorerAttr, otherAttr, Float.valueOf(value));
			}});
		
		return map;
    }
}
