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
import com.thoughtworks.xstream.io.path.PathTracker;
import com.thoughtworks.xstream.io.path.PathTrackingReader;
import com.wwm.attrs.AttrIdClassMapper;
import com.wwm.attrs.internal.AttrDefinitionMgr;
import com.wwm.util.DynamicRef;

public class AttributeIdMapper implements Converter {

    private DynamicRef<? extends AttrDefinitionMgr> attrDefMgrRef;

    public AttributeIdMapper(DynamicRef<? extends AttrDefinitionMgr> attrDefs) {
        this.attrDefMgrRef = attrDefs;
    }

    @SuppressWarnings("unchecked")
    public boolean canConvert(Class clazz) {
        return clazz.equals(Integer.class);
    }

    public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
        // UNSUPPORTED
        assert false;
    }

    // Read the value, and if it is an integer, use that, otherwise, assume it
    // is a string, and use that to look up an attributeId.
    // FIXME: Problem: getAttrId( str, clazz) must first have been called, otherwise
    // the class of the object is not known.
    // This could be overcome by having XML specify attribute as <attr:name="Postcode" value="PostcodeAttribute">
    public Object unmarshal(HierarchicalStreamReader reader,UnmarshallingContext context) {
        String attrName = reader.getValue();

        try {
            // TODO: Do we ever want Integer now!
            return Integer.valueOf(attrName);
        } catch (NumberFormatException e) {
            // fallthru
        }
        String attrIdFieldName = reader.getNodeName();

        // NOTE!: Do not use moveUp/moveDown.. they change the state!
        PathTrackingReader ptr = (PathTrackingReader) reader;
        PathTracker tracker = ptr.getPathTracker();
        String path = tracker.getPath().toString();
        int end = path.lastIndexOf('['); // duplicates are indexed i.e. parent[2] and we don't want the index
        if (end == -1) {
            end = path.lastIndexOf('/');
        }
        int start = path.lastIndexOf('/', end - 1) + 1;
        String parent = path.substring(start, end);

        Class<?> attrClass = AttrIdClassMapper.getAttrClass(parent, attrIdFieldName);
        return attrDefMgrRef.getObject().getAttrId(attrName, attrClass); // attrClass==null if unrecognised context
    }

}
