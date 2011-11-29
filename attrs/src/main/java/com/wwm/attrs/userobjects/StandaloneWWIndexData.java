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
package com.wwm.attrs.userobjects;

import gnu.trove.TIntObjectHashMap;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.wwm.attrs.AttrsFactory;
import com.wwm.db.annotations.Key;
import com.wwm.db.marker.IAttributeContainer;
import com.wwm.db.marker.IWhirlwindItem;
import com.wwm.db.whirlwind.CardinalAttributeMap;
import com.wwm.db.whirlwind.internal.AttributeCache;
import com.wwm.db.whirlwind.internal.IAttribute;
import com.wwm.db.whirlwind.internal.IAttributeMap;

/**
 * 
 * NOTE: In memory, these will be:
 * - n per LeafNode in WWIndex
 * - 1 or 2 (?) per BTree node
 * - 1 per Element in raw table.
 */
public class StandaloneWWIndexData implements IWhirlwindItem, Serializable {

    /** For use when instance value is null, but want to return a map */
    static private final TIntObjectHashMap<String> emptyStringMap = new TIntObjectHashMap<String>(0);



    private static final long serialVersionUID = 1L;

    public static final String sPrivateId = "privateId";
    @Key( unique=true )
    private String privateId;

    private String description;

    private CardinalAttributeMap<IAttribute> attrs = AttrsFactory.getCardinalAttributeMap();

    // track if we've already compacted this
    private boolean compacted = false;
    private TIntObjectHashMap<String> nonIndexAttrs = null;

    /** Default ctor for serialization libraries */
    @SuppressWarnings("unused")
    private StandaloneWWIndexData() {
        super();
    }

    public StandaloneWWIndexData(String privateId) {
        super();
        this.privateId = privateId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String discription) {
        this.description = discription;
    }

    public String getPrivateId() {
        return privateId;
    }

    public void setPrivateId(String privateId) {
        this.privateId = privateId;
    }

    @SuppressWarnings("unchecked")
    public IAttributeMap<IAttribute> getAttributeMap() {
        return (IAttributeMap<IAttribute>)attrs;  // Server side.  Sound aim to eliminate this.
    }

    public void setAttributeMap(IAttributeContainer attrs) {
        throw new UnsupportedOperationException();
    }

//    public AbstractAttributeMap getAttributeMap(){
//    	return attrs;
//    }
    
    /**
     * Add a non-index string to this object, e.g. Postcode="CB4 2QW"
     * Note: AttrId is used as Strings would be inefficient (esp as they're not
     * easily merged when dealing with serialised I/O)
     */
    public void setNonIndexString(int attrId, String value){
        if (nonIndexAttrs == null) {
            nonIndexAttrs = new TIntObjectHashMap<String>(4, 1.0f); // high load factor to get compact map
        }
        nonIndexAttrs.put(attrId, value);
    }

    public String getNonIndexString(int attrId){
        return nonIndexAttrs == null ? null : nonIndexAttrs.get(attrId);
    }


    public TIntObjectHashMap<String> getNonIndexAttrs(){
        return nonIndexAttrs == null ? emptyStringMap : nonIndexAttrs;
    }


    @Deprecated
    public void setAttributes(IAttributeMap<IAttribute> attributes) {
        throw new UnsupportedOperationException(); // we've wrapped it
    }


    public Object getNominee() {
        throw new UnsupportedOperationException();
    }


    public void setNominee(Object o) {
        throw new UnsupportedOperationException();
    }

    public void mergeDuplicates(AttributeCache cache) {
    	
    	// FIXME: merge attributes too - is this AttributeRemapper??
    	cache.mergeStrings( nonIndexAttrs );
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    	in.defaultReadObject();
    	mergeDuplicates(AttributeCache.getInstance());
    }
    
    private void writeObject(ObjectOutputStream out) throws IOException {
    	if (!compacted){
    		if (nonIndexAttrs != null) nonIndexAttrs.compact(); // reduce mem footprint before sending over wire or to disk
    		compacted = true; // worth the byte to save empty spaces in arrays.
    	}
    	out.defaultWriteObject();
    }
    
    
}
