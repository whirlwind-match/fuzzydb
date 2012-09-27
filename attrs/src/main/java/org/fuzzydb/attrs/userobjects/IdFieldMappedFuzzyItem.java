/******************************************************************************
 * Copyright (c) 2004-2011 Whirlwind Match Limited. All rights reserved.
 *
 * This is open source software; you can use, redistribute and/or modify
 * it under the terms of the Open Software Licence v 3.0 as published by the 
 * Open Source Initiative.
 *
 * You should have received a copy of the Open Software Licence along with this
 * application. if not, contact the Open Source Initiative (www.opensource.org)
 *****************************************************************************/
package org.fuzzydb.attrs.userobjects;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.fuzzydb.attrs.AttributeDefinitionService;
import org.fuzzydb.attrs.AttrsFactory;
import org.fuzzydb.client.marker.IWhirlwindItem;
import org.fuzzydb.client.whirlwind.CardinalAttributeMap;
import org.fuzzydb.core.whirlwind.internal.AttributeCache;
import org.fuzzydb.core.whirlwind.internal.IAttribute;
import org.fuzzydb.core.whirlwind.internal.IAttributeMap;
import org.springframework.data.annotation.Id;


/**
 * A {@link IWhirlwindItem} that can store fuzzy indexable attributes, but also
 * any non-indexable string attributes.  This item's maintains a primary key of type
 * KEY.
 */
public class IdFieldMappedFuzzyItem implements MappedItem, IWhirlwindItem, Serializable {


    private static final long serialVersionUID = 1L;

    @Id
    private Comparable<?> id;
    
    private CardinalAttributeMap<IAttribute> attrs = AttrsFactory.getCardinalAttributeMap();

    // track if we've already compacted this
//    private boolean compacted = false;
    private HashMap<String,String> nonIndexAttrs = null;


    public Comparable<?> getId() {
		return id;
	}
    
    public void setId(Comparable<?> id) {
		this.id = id;
	}
    
    @Override
	@SuppressWarnings("unchecked")
    public IAttributeMap<IAttribute> getAttributeMap() {
        return (IAttributeMap<IAttribute>)attrs;  // Server side.  Should aim to eliminate this.
    }

    @Override
	@SuppressWarnings("unchecked")
	public void setAttributeMap(IAttributeMap<IAttribute> attrs) {
        this.attrs = (CardinalAttributeMap<IAttribute>)attrs;
    }

    /**
     * Add a non-index string to this object, e.g. Postcode="CB4 2QW"
     * Note: The use of Strings for keys is inefficient.  This could be an attribute id once we have
     * a decent {@link AttributeDefinitionService} available.
     */
    @Override
	public void setNonIndexString(String name, String value) {
        if (nonIndexAttrs == null) {
            nonIndexAttrs = new HashMap<String, String>(4, 1.0f); // high load factor to get compact map
        }
        nonIndexAttrs.put(name, value);
    }

    public String getNonIndexString(String name){
        return nonIndexAttrs == null ? null : nonIndexAttrs.get(name);
    }

    @Override
	public Map<String, String> getNonIndexAttrs() {
		if (nonIndexAttrs != null) {
			return nonIndexAttrs;
		} else {
			return Collections.emptyMap();
		}
	}
    
    @Override
	public Object getNominee() {
        throw new UnsupportedOperationException(); // TODO: Deserialze the blob..?  or is this just the buffer
    }


    @Override
	public void setNominee(Object o) {
        throw new UnsupportedOperationException();
    }

	@Override
	public void mergeDuplicates(AttributeCache cache) {
    	
    	// FIXME: merge attributes too - is this AttributeRemapper??
//    	cache.mergeStrings( nonIndexAttrs );
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    	in.defaultReadObject();
    	mergeDuplicates(AttributeCache.getInstance());
    }
    
    private void writeObject(ObjectOutputStream out) throws IOException {
//    	if (!compacted){
//    		if (nonIndexAttrs != null) nonIndexAttrs.compact(); // reduce mem footprint before sending over wire or to disk
//    		compacted = true; // worth the byte to save empty spaces in arrays.
//    	}
    	out.defaultWriteObject();
    }
    
    @Override
    public <T extends MappedItem> void mergeFrom(T toMerge) {
		IdFieldMappedFuzzyItem other = (IdFieldMappedFuzzyItem) toMerge;
		attrs = other.attrs;
		nonIndexAttrs = other.nonIndexAttrs;
	}
}
