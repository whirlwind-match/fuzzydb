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
package org.fuzzydb.attrs.enums;

import java.io.Serializable;
import java.util.ArrayList;

import org.fuzzydb.attrs.internal.AttrDefinitionMgr;

import com.wwm.model.attributes.OptionsSource;


public class EnumDefinition implements Serializable, OptionsSource {

    /**
     * Provide some bounds to how many values an enum can have.
     * EnumMultiValue will be encoded as a bit-field to make scoring easier, so
     * we're going to lose out when someone selects perhaps 3 values out of 64, as
     * we'd have been able to use 3 bytes to achieve the same thing.
     */
    public static final int MAX_ENTRIES = 64; // Can use a single int

    private static final long serialVersionUID = 1L;

    private final String name;
    private final short enumDefId;
    private final ArrayList<String> strValues = new ArrayList<String>();

    /**
     * The parent AttrDefinitionMgr update definition to, if this object changes.
     * NOTE: We must patch AttrDefinitionMgr when retrieving from database ...
     */
    private transient AttrDefinitionMgr mgr;

    /** Default ctor for serialization libraries */
    @SuppressWarnings("unused")
    private EnumDefinition() {
        this.mgr = null;
        this.name = null;
        this.enumDefId = 0;
    }
    
    public EnumDefinition(AttrDefinitionMgr mgr, String name, short enumDefId) {
        assert name != null;
        this.mgr = mgr;
        this.name = name;
        this.enumDefId = enumDefId;
    }

    public String getName() {
        return name;
    }

    public short getEnumDefId() {
        return enumDefId;
    }

    /**
     * Get an EnumExclusiveValue object representing the option 'option' within this enum.
     * If it doesn't exist, it is added to this definition.
     * We have to ensure that this is updated to the database when a new value is created.
     * As an EnumDefinition can only have been found via AttrDefintionMgr for a given store, then
     * we do this by reading the store.
     */
    public EnumExclusiveValue getEnumValue(String option, int attrId) {
        assert( option != null);  // FIXME: Review what we're doing here. Do we explicitly add (null) = null ? So that getValues can present a 'null' option?? Don't think so.. but...

        if (mgr != null) {
            mgr.associateAttrToEnumDef(attrId, this);
        }
        
        EnumExclusiveValue enumVal = find( option, attrId );

        if (enumVal != null) {
            return enumVal;  // already exists.  We expect this most often.
        }

        // Doesn't yet exist, so carefully create it.  We avoid sync'ing most of the time.
        synchronized (strValues) {
            enumVal = find( option, attrId );
            if (enumVal != null) {
                return enumVal;
            }
            int index = strValues.size(); // i.e. this one will go in at values[index] when added to end
            if (index >= MAX_ENTRIES){
                throw new RuntimeException( "Exceeded limit of enum definitions for '" + name + "'. Max is: " + MAX_ENTRIES);
            }
            enumVal = new EnumExclusiveValue( attrId, enumDefId, (short)index );

            strValues.add(option);

            if (mgr != null) {
                mgr.syncToStore();
            }
        }
        return enumVal;
    }



    /**
     * Get EnumValue representation of an option given the name
     */
    public EnumExclusiveValue find(String option, int attrId) {
        if (mgr != null) {
            mgr.associateAttrToEnumDef(attrId, this);
        }

        // Lookup in strings, and then return cached instance
        int index = findIndex(option);
        if (index == -1){
            return null;
        }
        return new EnumExclusiveValue( attrId, enumDefId, (short)index );
    }

    /**
     * Get an EnumMultiValue representing the given strings, allocated as part of this EnumDefinition
     * @param values
     * @param attrId
     * @return
     */
    public EnumMultipleValue getMultiEnum(String[] values, int attrId) {
        assert mgr != null; // Not much use if it's not getting updated.
        
        if (mgr != null) {
            mgr.associateAttrToEnumDef(attrId, this);
        }

        EnumMultipleValue emv = new EnumMultipleValue(attrId, this);
        for (String value : values){
            // Quick lookup, but if first time we've seen the value, will update enumDefs to persistent storage
            EnumExclusiveValue eev = getEnumValue(value, attrId);

            emv.addValue(eev.getEnumIndex());
        }
        return emv;
    }


    /**
     * Find the integer index for a String Enumeration option
     * @param value
     * @return index of value, or -1 if not found
     */
    public short findIndex(String value) {
        return (short)strValues.indexOf(value);
    }

    @Override
	public String findAsString(short index) {
        assert( index >= -1 );

        if (index == -1 || index >= strValues.size()) {
            return null; // FIXME:??
        }
        return strValues.get(index);
    }


    @Override
	public int size() {
        return strValues.size();
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();

        for (int i = 0; i < strValues.size(); i++) {
            str.append( strValues.get(i) );
            str.append( ", ");
        }

        return str.toString();
    }

    public ArrayList<String> getValues() {
        return strValues;
    }

    public String[] getStrings(short[] values) {
        String[] strs = new String[values.length];

        for (int i = 0; i < values.length; i++) {
            strs[i] = strValues.get(values[i]);
        }
        return strs;
    }

    /**
     * Set mgr
     * @param attrDefinitionMgr
     * @return this (to allow chaining calls)
     */
    public EnumDefinition setMgr(AttrDefinitionMgr attrDefinitionMgr) {
        mgr = attrDefinitionMgr;
        return this;
    }
}
