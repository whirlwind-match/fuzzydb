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
package com.wwm.attrs.userobjects;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.wwm.attrs.AttributeDefinitionService;
import com.wwm.attrs.AttrsFactory;
import com.wwm.db.marker.IWhirlwindItem;
import com.wwm.db.whirlwind.CardinalAttributeMap;
import com.wwm.db.whirlwind.internal.AttributeCache;
import com.wwm.db.whirlwind.internal.IAttribute;
import com.wwm.db.whirlwind.internal.IAttributeMap;

/**
 * A {@link IWhirlwindItem} that stores the attribute map plus any BLOB of data associated with it.
 * The BLOB might, for example be a serialized form of the originating object, or a byte buffer
 * for the object mapped using MessagePack.  
 */
public class BlobStoringWhirlwindItem implements IWhirlwindItem, Serializable {


    private static final long serialVersionUID = 1L;

    private byte[] blob = null;
    
    /**
     * Map containing the key/values for this that represent unique keys to be indexed in the database.
     * TODO: IMplement support for @Key against a Map and then enable
     */
//    public static final String sUniqueKeys = "uniqueKeys";
//    @Key( unique=true )
//    private Map<String, String> uniqueKeys = null;

//    public static final String sNonUniqueKeys = "nonUniqueKeys";
//    @Key( unique=false )
//    private Map<String, String> nonUniqueKeys = null;

    
    private CardinalAttributeMap<IAttribute> attrs = AttrsFactory.getCardinalAttributeMap();

    // track if we've already compacted this
//    private boolean compacted = false;
    private HashMap<String,String> nonIndexAttrs = null;


    @SuppressWarnings("unchecked")
    public IAttributeMap<IAttribute> getAttributeMap() {
        return (IAttributeMap<IAttribute>)attrs;  // Server side.  Should aim to eliminate this.
    }

    @SuppressWarnings("unchecked")
	public void setAttributeMap(IAttributeMap<IAttribute> attrs) {
        this.attrs = (CardinalAttributeMap<IAttribute>)attrs;
    }

    public void setBlob(byte[] blob) {
    	this.blob = blob;
    }
    
    public byte[] getBlob() {
    	return blob;
    }

    /**
     * Add a non-index string to this object, e.g. Postcode="CB4 2QW"
     * Note: The use of Strings for keys is inefficient.  This could be an attribute id once we have
     * a decent {@link AttributeDefinitionService} available.
     */
    public void setNonIndexString(String name, String value) {
        if (nonIndexAttrs == null) {
            nonIndexAttrs = new HashMap<String, String>(4, 1.0f); // high load factor to get compact map
        }
        nonIndexAttrs.put(name, value);
    }

    public String getNonIndexString(String name){
        return nonIndexAttrs == null ? null : nonIndexAttrs.get(name);
    }

    public Map<String, String> getNonIndexAttrs() {
		if (nonIndexAttrs != null) {
			return nonIndexAttrs;
		} else {
			return Collections.emptyMap();
		}
	}
    
    public Object getNominee() {
        throw new UnsupportedOperationException(); // TODO: Deserialze the blob..?  or is this just the buffer
    }


    public void setNominee(Object o) {
        throw new UnsupportedOperationException();
    }

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
    
    
}
