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
 * A {@link IWhirlwindItem} that stores the attribute map plus any BLOB of data associated with it.
 * The BLOB might, for example be a serialized form of the originating object, or a byte buffer
 * for the object mapped using MessagePack.  
 */
public class BlobStoringWhirlwindItem implements IWhirlwindItem, Serializable {

    /** For use when instance value is null, but want to return a map */
    static private final TIntObjectHashMap<String> emptyStringMap = new TIntObjectHashMap<String>(0);



    private static final long serialVersionUID = 1L;

    /**
     * Field to use for Spring Data Id annotation
     */
    public static final String PRIMARY_KEY_FIELD_NAME = "primaryKey";
    @Key( unique=true )
    private String primaryKey;

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
    private boolean compacted = false;
    private TIntObjectHashMap<String> nonIndexAttrs = null;


    public BlobStoringWhirlwindItem(String primaryKey) {
        super();
        this.primaryKey = primaryKey;
    }

    @SuppressWarnings("unchecked")
    public IAttributeMap<IAttribute> getAttributeMap() {
        return (IAttributeMap<IAttribute>)attrs;  // Server side.  Sound aim to eliminate this.
    }

    public void setAttributeMap(IAttributeContainer attrs) {
        throw new UnsupportedOperationException();
    }

    public void setBlob(byte[] blob) {
    	this.blob = blob;
    }
    
    public byte[] getBlob() {
    	return blob;
    }

    public String getPrimaryKey() {
		return primaryKey;
	}
    
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

    
    public Object getNominee() {
        throw new UnsupportedOperationException(); // TODO: Deserialze the blob..?  or is this just the buffer
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
