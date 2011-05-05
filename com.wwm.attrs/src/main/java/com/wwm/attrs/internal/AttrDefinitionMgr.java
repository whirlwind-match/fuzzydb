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

import gnu.trove.TIntObjectHashMap;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.slf4j.Logger;


import com.wwm.attrs.bool.BooleanValue;
import com.wwm.attrs.enums.EnumDefinition;
import com.wwm.attrs.enums.EnumExclusiveValue;
import com.wwm.attrs.enums.EnumMultipleValue;
import com.wwm.attrs.location.EcefVector;
import com.wwm.attrs.location.RangePreference;
import com.wwm.attrs.simple.FloatHave;
import com.wwm.attrs.simple.FloatRangePreference;
import com.wwm.attrs.string.StringValue;
import com.wwm.db.core.LogFactory;
import com.wwm.model.attributes.BooleanAttribute;
import com.wwm.model.attributes.DistanceAttribute;
import com.wwm.model.attributes.EnumAttribute;
import com.wwm.model.attributes.FloatAttribute;
import com.wwm.model.attributes.FloatRangeAttribute;
import com.wwm.model.attributes.IntegerRangeAttribute;
import com.wwm.model.attributes.LocationAttribute;
import com.wwm.model.attributes.MultiEnumAttribute;
import com.wwm.model.attributes.NonIndexStringAttribute;
import com.wwm.model.attributes.Point3DAttribute;
/**
 * Responsible for allocating attribute ids, against a string and class, and allowing
 * extension to support persistence of these mappings.
 * 
 * Use cases:
 * 1) Configuration of an application - adding many attribute ids against their name and
 *    class.  The allocation of a new attribute ID can be low performance, as it is only
 *    done when initialising or modifying configurations (currently less than once per month)
 * 2) Looking up attrIds and class given the string name of that attribute (perf critical)
 * 3) Looking up string name of attribute, given attribute id (perf critical)
 */
public class AttrDefinitionMgr implements Serializable {

    static final Logger log = LogFactory.getLogger(AttrDefinitionMgr.class);

    private static final long serialVersionUID = 1L;

    public enum AttrType {
    	unknownTypeValue,
        booleanValue, floatValue,
        enumExclusiveValue, enumMultiValue,
        stringValue, vectorValue,
        floatRangePrefValue, locationPrefValue,
    }

    /**
     * Store the ids we've already looked up.
     */
    private final Map<String, Integer> ids = new HashMap<String, Integer>();
    private final Map<String, EnumDefinition> defs = new HashMap<String, EnumDefinition>();
    private final TIntObjectHashMap<EnumDefinition> enumDefIdsToDef = new TIntObjectHashMap<EnumDefinition>();
    private final TIntObjectHashMap<EnumDefinition> attrIdsToDef = new TIntObjectHashMap<EnumDefinition>();


    private static final int ATTR_CLASS_MASK =  0x00003F00; // Up to 63 types (plenty!)
    private static final int UNKNOWN_CLASS =    0x00000000; // use zero if we don't know or care about class, but still want an attrId
    private static final int BOOLEAN =          0x00000100;
    private static final int FLOAT = 		    0x00000200;
    private static final int ENUM_EXCLUSIVE =   0x00000300;
    private static final int ENUM_MULTI =       0x00000400;
    private static final int STRING =           0x00000500;
    private static final int VECTOR =           0x00000600;
    private static final int FLOAT_RANGE_PREF = 0x00000700;
    private static final int LOCATION_PREF = 	0x00000800;

    private static final int INDEX_MASK = 0x000000ff; // up to 256 attrs (of each type if we want)
    private int nextId = 0;
    private int nextEnumDef = 0;
    private transient boolean syncDisabled = false;


    // This should only be used by unit tests. Something like SyncedAttrDefinitionMgr should be used instead.
    protected AttrDefinitionMgr() {
        super();
    }



    public static AttrType getAttrType(int attrId) {
        switch (attrId & ATTR_CLASS_MASK) {
        case UNKNOWN_CLASS:
        	return AttrType.unknownTypeValue;
        case BOOLEAN:
            return AttrType.booleanValue;
        case FLOAT:
            return AttrType.floatValue;
        case ENUM_EXCLUSIVE:
            return AttrType.enumExclusiveValue;
        case ENUM_MULTI:
            return AttrType.enumMultiValue;
        case STRING:
            return AttrType.stringValue;
        case VECTOR:
            return AttrType.vectorValue;
        case FLOAT_RANGE_PREF:
            return AttrType.floatRangePrefValue;
        case LOCATION_PREF:
            return AttrType.locationPrefValue;
        default:
            throw new RuntimeException("Type mapping needed.");
        }
    }

    public int getAttrId(String attrName) {
        return getAttrId(attrName, null);
    }

    public String getAttrName(int attrId) {

        for(Entry<String, Integer> entry : ids.entrySet()) {
            if (entry.getValue().equals(attrId)) {
                return entry.getKey();
            }
        }

        //	FIXME : (jc)Should throw a Arch exception?  - (nu) Not really.  it's a programming error.  Don't want to declare/catch exceptions for programming errors.
        throw new RuntimeException("PBC Error");
    }

    /**
     * Get an attribute ID for this attribute name and clazz
     * 
     * This will throw an Error if an attempt is made to re-use a name with
     * a different class.
     * 
     * @param clazz - null allowed
     */
    public int getAttrId(String attrName, Class<?> clazz) {

        int overlay = getAttributeClassCode(clazz);

        // See if we already know this one
        Integer attrId = ids.get( attrName );
        if (attrId != null) {
            if (clazz != null && (attrId & ATTR_CLASS_MASK) != overlay ) {
                throw new RuntimeException( "Cannot re-use the same name with a different class:" + attrName );
            }
            // FIXME: Except we can: Boolean / BooleanConstraint...?
            return attrId;
        }

        // Okay.. don't have it.  So assign next one of a given type.
        attrId = getNextId(attrName, overlay);
        return attrId;
    }

    /**
     * Return an EnumDefinition given a name for the EnumDefinition.
     * Once we have this, we can then create enum values, by supplying
     * an attribute name and value string.
     *    EnumDefinition def = attrDefMgr.getEnumDefinition( "SmokingStates" );
     *    EnumValue = def.getValue( "Smoke", "GivingUp" );
     */
    public EnumDefinition getEnumDefinition(String defName) {
        assert defName != null;
        EnumDefinition def = defs.get(defName);
        if (def != null) {
            return def.setMgr(this);
        }
        return getNextEnumDef(defName);
    }

    public EnumDefinition getEnumDef(short enumDefId) {
        return enumDefIdsToDef.get(enumDefId).setMgr(this);
    }

    /**
     * returns the index bits of the attribute ID, which is the attrWord
     * without type or function encoding.  This is used by codecs.
     * @param attrWord
     * @return
     */
    public static int getAttrIndex(int attrWord) {
        return ( attrWord & INDEX_MASK );
    }

    /**
     * returns the index and type bits of the header, which gives the index
     * without function encoding.  This is used when associating a description,
     * e.g. Gender, with a type and index, Boolean, index = 1, for example.
     * This is valid for both BooleanValue and BooleanConstraint (which are encoded
     * by the function bits).
     * @param headerWord
     * @return
     */
    public static int getAttrIndexAndType(int headerWord) {
        return ( headerWord & (INDEX_MASK | ATTR_CLASS_MASK) );
    }


    /**
     * Returns a value to be overlayed on the index of the attribute (by ORing it with the id)
     * @param clazz
     * @return
     */
    static public int getAttributeClassCode(Class<?> clazz) {
        if ( clazz == null) {
            return UNKNOWN_CLASS;
        }

        if ( clazz.isAssignableFrom(BooleanValue.class)
                || clazz.isAssignableFrom(BooleanAttribute.class)) {
            return BOOLEAN;
        } else if ( clazz.isAssignableFrom(FloatHave.class)
                || clazz.isAssignableFrom(FloatAttribute.class)) {
            return FLOAT;
        } else if ( clazz.isAssignableFrom(FloatRangePreference.class)
                || clazz.isAssignableFrom(FloatRangeAttribute.class)) {
            return FLOAT_RANGE_PREF;
        } else if ( clazz.isAssignableFrom(EnumExclusiveValue.class)
                || clazz.isAssignableFrom(EnumAttribute.class)) {
            return ENUM_EXCLUSIVE;
        } else if ( clazz.isAssignableFrom(EnumMultipleValue.class)
                || clazz.isAssignableFrom(MultiEnumAttribute.class)) {
            return ENUM_MULTI;
        } else if ( clazz.isAssignableFrom(EcefVector.class)
                || clazz.isAssignableFrom(Point3DAttribute.class)) {
            return VECTOR;
        } else if ( clazz.isAssignableFrom(RangePreference.class)
                || clazz.isAssignableFrom(DistanceAttribute.class)) {
            return LOCATION_PREF;
        } else if ( clazz.isAssignableFrom(StringValue.class)) {
            return STRING;
        } else if ( clazz.isAssignableFrom(NonIndexStringAttribute.class)
        		|| clazz.isAssignableFrom(LocationAttribute.class)		// i.e. the Postcode, which doesn't go in the index
                || clazz.isAssignableFrom(IntegerRangeAttribute.class)  // TODO: Need to add support for this including codecs (for now, use FloatRange..)
                || clazz.isAssignableFrom(Date.class)  // FIXME: Need to add DateAttribute and converters to/from Float
                || clazz.isAssignableFrom(String.class)) {
            return UNKNOWN_CLASS; // actually it's known and non-indexed
        }

        log.error("ADM: Unknown class: " + clazz.getCanonicalName());
        assert false : "Need to migrate use of class to a recognised type: " + clazz.getCanonicalName();
        return UNKNOWN_CLASS;
        // FIXME: Default for Jas
        //		throw new UnsupportedOperationException();
    }


    private int getNextId(String attrName, int overlay) {
        Integer attrId;
        synchronized(ids) {
            // First, re-check that no one else created it before we synchronized
            attrId = ids.get( attrName );
            if (attrId != null) {
                if ( (attrId & overlay) != overlay ) {
                    throw new RuntimeException( "Cannot re-use the same name with a different class:" + attrName );
                }
                return attrId;
            }

            // We had to check.  Now get a new one.
            attrId = nextId | overlay ;
            ids.put(attrName, attrId);
            nextId++;
            syncToStore();
        }
        return attrId;
    }

    private EnumDefinition getNextEnumDef(String defName) {
        EnumDefinition def;
        synchronized(defs) {
            // First re-check it's not already there (we've only just synced, so two thread might both have gone for it
            def = defs.get(defName);
            if (def == null) {

                // Def(initely) not there, so create one
                int id = nextEnumDef;
                def = new EnumDefinition(this, defName, (short) id);
                defs.put(defName, def);
                enumDefIdsToDef.put(id, def);
                nextEnumDef++;
                syncToStore();
            }
        }
        return def.setMgr(this);
    }

    /**
     * Associate this attrId with given enumDef, if not already done so.
     * Returns quickly if already done.
     */
	public void associateAttrToEnumDef(int attrId, EnumDefinition enumDef) {
		if (attrIdsToDef.get(attrId) != null){
			return; // already associated
		}
		synchronized (attrIdsToDef) {
			attrIdsToDef.put(attrId, enumDef);
			syncToStore();
		}

		
	}
	
	public EnumDefinition getEnumDefForAttrId( int attrId ){
		return attrIdsToDef.get(attrId);
	}

    final public void syncToStore() {
        if (!syncDisabled){
            syncToStoreInternal();
        }
    }



    protected void syncToStoreInternal() {
        // Do nothing. This should be overridden to sync

    }

    public void disableSync() {
        syncDisabled = true;
    }

    /**
     * Note: You'll usually want enableSyncAndDoSync()
     * This is provided for completeness to allow sync to be enabled when a transaction has been aborted.
     */
    public void enableSync() {
        syncDisabled = false;
    }

    public void enableSyncAndDoSync() {
        syncDisabled = false;
        syncToStore();
    }

    @Override
    public String toString() {
        return "attrids: " + ids.toString();
    }

}