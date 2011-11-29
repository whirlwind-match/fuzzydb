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
package com.wwm.attrs.layout;

import gnu.trove.TIntArrayList;

import java.io.Serializable;

import com.wwm.attrs.internal.AttrDefinitionMgr;
import com.wwm.attrs.internal.AttrDefinitionMgr.AttrType;
import com.wwm.db.annotations.Key;
import com.wwm.db.core.Settings;
import com.wwm.db.dao.SimpleDAO;

/*
 * Thinking aloud:
 * AttrDefinitionMgr concerns itself only with names to id mappings.
 * We additionally need to be able to support laying out of objects.
 * 
 * We should be able to update the layout as attributes are added to a map.
 * On the client side, we could do this as the first objects are being created and encoded.  
 * If this were part of the same transaction then would we risk the layout not having been sent until after lots of
 * other objects.  We might.  It seem sensible to go further
 */

// TODO: Work out what's common and what's specific to the type of map being used.
// e.g. The attrIds from AttrDefinitionMgr should work fine for LayoutAttrDefinitionMgr


// We will want to use SyncedAttrDefinitionMgr, but also will want to sync the layout bits too, so perhaps "extends " is correct.

public class LayoutMapConfig implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;


	private static final int ATTRIBUTE_KEY_INT = 0;
	private static final int CONSTRAINT_KEY_INT = 1;
	
	public static final Integer ATTRIBUTE_KEY = Integer.valueOf(ATTRIBUTE_KEY_INT);
	public static final Integer CONSTRAINT_KEY = Integer.valueOf(CONSTRAINT_KEY_INT);

	/** A list of attrIds that have been defined. This is needed to be able to iterate over them, and decode the correct type */
	private TIntArrayList attrIds = new TIntArrayList(); 
	
	/** Against indexes[attrId & SEQUENCE_MASK] we record the offset at which that attribute should be stored
	 * We add one, so that we can have a sparce array, with a value of zero meaning that it isn't allocated */
	private TIntArrayList indicesPlusOne = new TIntArrayList();
	
	/** When an attribute is allocated space, a certain amount is requested.  This records the
	 * length, such that it can be queried as getLength(attrId) */
	private TIntArrayList lengths = new TIntArrayList();
	
	
	/** Keep track of the next unallocated index in ints[] part of map */
	private int nextIntsIndex = 0;
	
	/** Keep track of the next unallocated index in floats[] part of map */
	private int nextFloatsIndex = 0;
	
	/** The key against which we want to be able to retrieve from database */
	@Key private Integer key;

	private Object ref; // the reference that DAO gave us when we created an instance
	
	static private LayoutMapConfig instance;
	
	private LayoutMapConfig constraintMapConfig;

	/**
	 * Get existing instance.  This is assumed to not change, as we'll have finalised the map the first time it
	 * is used (e.g. during db init, or write of first object).  Any changes to a running map will not be reflected in
	 * other instances across the system until they refresh (i.e. restart).
	 * This should work fine for client->server, as client will have fully configured the map and written it to 
	 * the server, before it writes the first object that uses the map (as we sync changes immediately)
	 * NOTE: This is NOT THREAD SAFE.  We assume it's used within a thread managed area
	 * @param key
	 * @return
	 */
	public static LayoutMapConfig getInstance() {
		if ( instance == null){
			instance = getFromStore(); // BEWARE ON SERVER.  WHEN DO WE GET THE INSTANCE ... MUST SHUTDOWN TO ENSURE NO INDEXES BUILT FIRST
		}
		return instance;
	}

    /**
     * Provide access to a data access object that abstracts away simple operations for accessing configuration
     * @return
     */
    @SuppressWarnings("unchecked")
	static public Class<SimpleDAO> getConfigDAOClass(){
		try {
			return (Class<SimpleDAO>) Class.forName(Settings.getInstance().getConfigDAOClassName());
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e); // Fatal error if we can't find it.
		}
    }

	
	/**
	 * Get instance of AttrDefinitionMgr from store, or create one in the store
	 * if one didn't exist
	 */
	private static LayoutMapConfig getFromStore() {
		LayoutMapConfig config = null;
		try {
			SimpleDAO dao = getConfigDAOClass().newInstance();
			dao.begin();
			config = dao.retrieve( LayoutMapConfig.class, ATTRIBUTE_KEY );
			if (config == null){
				 config = new LayoutMapConfig();
				 config.key = ATTRIBUTE_KEY;
				 config.constraintMapConfig = new LayoutMapConfig();
				 config.constraintMapConfig.key = CONSTRAINT_KEY;
				 config.ref = dao.create(config, ATTRIBUTE_KEY);
				 dao.commit();
			}
		} catch (Exception e) {
			throw new RuntimeException(e); // FIXME: not sure what we'll get
		}
		return config;
	}

	
	public LayoutMapConfig getConstraintMapConfig(){
		return constraintMapConfig;
	}
	
	
	private void syncToStore() {
		// we only sync on the master (sorry.. yes.. this got hacked.. they used to both be stored against the two diff keys)
		if (!key.equals(ATTRIBUTE_KEY)) return; 

		try {
			SimpleDAO dao = getConfigDAOClass().newInstance();
			dao.begin();
			dao.update(this, ref );
			dao.commit();
		} catch (Exception e) {
			throw new RuntimeException(e); // If we get here, it's likely that we're on the server trying to update.  THis means we haven't seen all attributes on the client prior to starting our transaction
		}
	}

	
	public TIntArrayList getAttrIds() {
		return attrIds;
	}


	/**
	 * Get (and allocate if necessary) the index for this attribute id.
	 */
	public int getIndex(int attrId) {
		AttrType type = AttrDefinitionMgr.getAttrType(attrId);

		if (key.intValue() == ATTRIBUTE_KEY_INT){
			return getAttrIndex(attrId, type);
		}
		return getConstraintIndex(attrId, type);
	}

	/**
	 * version of getIndex for when we're decoding, and therefore space has already been allocated
	 */
	public int getIndexQuick(int attrId){
		int sequence = AttrDefinitionMgr.getAttrIndex(attrId); // this should be unique across all attributes
		int indexPlusOne = indicesPlusOne.getQuick(sequence);
		return indexPlusOne - 1;
	}
	
	
	private int getAttrIndex(int attrId, AttrType type) throws Error {
		
		switch (type) {
		case booleanValue: // FALLTHRU
		case enumExclusiveValue: // FALLTHRU
		case enumMultiValue:
			return getIntSpace(attrId, 1);

		case floatValue:
			return getFloatSpace(attrId, 1);
			
		case floatRangePrefValue:  // FALLTHRU
		case vectorValue:
			return getFloatSpace(attrId, 3);
		default:
			return -1; // Not implemented yet
		}
	}

	
	private int getConstraintIndex(int attrId, AttrType type) throws Error {
		switch (type) {
		case booleanValue:
			return getIntSpace(attrId, 1);
		case enumExclusiveValue:
			return getIntSpace(attrId, EnumExclConstraintCodec.LENGTH);
		case enumMultiValue:
			return getIntSpace(attrId, EnumMultiConstraintCodec.LENGTH);

		case floatValue:
			return getFloatSpace(attrId, 2);

		case floatRangePrefValue: // FALLTHRU
		case vectorValue:
			return getFloatSpace(attrId, 6);
		default:
			return -1;
		}
	}

	
	
	/**
	 * Get/allocate spaceNeeded entries for the designated integer-based attribute
	 */
	private int getIntSpace(int attrId, int spaceNeeded) {
		int sequence = AttrDefinitionMgr.getAttrIndex(attrId); // this should be unique across all attributes

		
		if (sequence < indicesPlusOne.size()) {
			int indexPlusOne = indicesPlusOne.getQuick(sequence);
			if (indexPlusOne > 0) {
//				assert (getLength(attrId) == spaceNeeded); // dumbo check to ensure not asking for diff size
				return indexPlusOne - 1;
			}
		} else {
			padSpace(indicesPlusOne, sequence);
		}

		// okay, it's not been allocated so ...
		int index = nextIntsIndex;
		nextIntsIndex += spaceNeeded;
		
		indicesPlusOne.set(sequence, index + 1);

		// Add to list of attrIds for iterator
		assert( attrIds.contains(attrId) == false);
		attrIds.add(attrId);
		
		setLength(sequence, spaceNeeded);

		syncToStore(); // we sync here as we've modified
		return index;
	}


	/**
	 * Pad the supplied array, cos Trove doesn't supply the capability
	 * @param array
	 * @param sequence
	 */
	private void padSpace(TIntArrayList array, int sequence) {
		assert ( array.size() <= sequence); // e.g. 0 <= 0, on first add
		while( array.size() <= sequence){ // ensures we add including then value we want to then set
			array.add( -1 );
		}
	}

	/**
	 * Get/allocate spaceNeeded entries for the designated float-based attribute
	 */
	private int getFloatSpace(int attrId, int spaceNeeded) {
		int sequence = AttrDefinitionMgr.getAttrIndex(attrId); // this should be unique across all attributes

		if (sequence < indicesPlusOne.size()) {
			int indexPlusOne = indicesPlusOne.getQuick(sequence);
			if (indexPlusOne > 0) {
				assert (getLength(attrId) == spaceNeeded); // dumbo check to ensure not asking for diff size
				return indexPlusOne - 1;
			}
		} else {
			padSpace(indicesPlusOne, sequence);
		}
		
		// okay, it's not been allocated so ...
		int index = nextFloatsIndex;
		nextFloatsIndex += spaceNeeded;
		
		indicesPlusOne.set(sequence, index + 1);

		// Add to list of attrIds for iterator
		assert( attrIds.contains(attrId) == false);
		attrIds.add(attrId);

		setLength(sequence, spaceNeeded);

		syncToStore(); // we sync here as we've modified
		return index;
	}

	private void setLength(int sequence, int length) {
		if (sequence >= lengths.size()) {
			padSpace(lengths, sequence);
		}

		lengths.set(sequence, length);
	}

	public int getLength( int attrId ){
		int sequence = AttrDefinitionMgr.getAttrIndex(attrId); // this should be unique across all attributes
		return lengths.getQuick(sequence);
	}

	/**
	 * Ensures that this attribute is configured
	 * @param attrId
	 */
	public void allocateAttribute(int attrId) {
		/*ignore result*/ getIndex(attrId);
		/*ignore result*/ getConstraintMapConfig().getIndex(attrId);
	}
	
	@Override
	public String toString() {
		return attrIds.toString();
	}
	
	@Override
	public LayoutMapConfig clone() throws CloneNotSupportedException {
		LayoutMapConfig clone = (LayoutMapConfig) super.clone();
		clone.attrIds = (TIntArrayList) attrIds.clone();
		clone.constraintMapConfig = constraintMapConfig == null ? null : constraintMapConfig.clone();
		clone.indicesPlusOne = (TIntArrayList) indicesPlusOne.clone();
		clone.key = key;
		clone.lengths = (TIntArrayList) lengths.clone();
		clone.nextFloatsIndex = nextFloatsIndex;
		clone.nextIntsIndex = nextIntsIndex;
		return clone;
	}
	
}
