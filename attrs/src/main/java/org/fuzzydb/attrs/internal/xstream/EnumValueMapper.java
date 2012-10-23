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
package org.fuzzydb.attrs.internal.xstream;

import org.fuzzydb.attrs.AttributeDefinitionService;
import org.fuzzydb.attrs.enums.EnumExclusiveValue;
import org.fuzzydb.util.DynamicRef;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Given access to an {@link AttributeDefinitionService},
 * convert
 *
 * <pre> &lt;enumValue>enum&lt;/enumValue>
 *
 * into an EnumExclusiveValue supplied by resolving the required enum def
 * AttrIdClassMapper.getAttrClass("ParentClass", "fieldName"), and then
 * using that class to get the attrId for attrName.
 * </pre>
 *
 * @author Neale Upstone
 */
public class EnumValueMapper implements Converter {

    private final DynamicRef<? extends AttributeDefinitionService> attrDefMgrRef;

    public EnumValueMapper(DynamicRef<? extends AttributeDefinitionService> attrDefs) {
        this.attrDefMgrRef = attrDefs;
    }

    @Override
	public boolean canConvert(@SuppressWarnings("rawtypes") Class clazz) {
        return clazz.equals(EnumExclusiveValue.class);
    }

    @Override
	public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
        // UNSUPPORTED
        assert false;
    }

    @Override
	public Object unmarshal(HierarchicalStreamReader reader,UnmarshallingContext context) {
        String attrName = reader.getAttribute("attrId");
        String enumName = reader.getValue();
        int attrId = attrDefMgrRef.getObject().getAttrId(attrName);

        return attrDefMgrRef.getObject().getEnumDefForAttrId(attrId).getEnumValue(enumName, attrId);
    }

}
