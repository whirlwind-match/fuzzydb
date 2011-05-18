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
package com.wwm.indexer.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.wwm.attrs.AttrsFactory;
import com.wwm.attrs.enums.EnumDefinition;
import com.wwm.attrs.internal.AttrDefinitionMgr;
import com.wwm.attrs.internal.SyncedAttrDefinitionMgr;
import com.wwm.attrs.internal.AttrDefinitionMgr.AttrType;
import com.wwm.db.whirlwind.CardinalAttributeMap;
import com.wwm.db.whirlwind.SearchSpec;
import com.wwm.db.whirlwind.internal.IAttribute;
import com.wwm.indexer.IndexerFactory;
import com.wwm.indexer.db.converters.ConversionFactory;
import com.wwm.indexer.db.converters.SearchConverter;
import com.wwm.indexer.db.converters.UKPostcodeToVectorDerivation;
import com.wwm.model.attributes.Attribute;
import com.wwm.model.attributes.NonIndexStringAttribute;
import com.wwm.model.attributes.UnspecifiedTypeAttribute;
import com.wwm.util.DynamicRef;

/**
 * Responsible for:
 * - accepting configuration of how to convert named search attributes usually from a URL (e.g. Postcode=CB4+2QW) into
 *   their IAttribute representation in a SearchSpec.
 * - Performing conversion as configured
 * - Being able to be stored (i.e. must be serialisable... FIXME: eventually!!)
 */
public class SearchParamsConverter {

    private DynamicRef<? extends AttrDefinitionMgr> attrDefsRef = null;

    /**
     * Conversions for a search. These alway return matchable attributes: IAttribute
     * No other conversions are done on a search that is based on a name, value map.
     * FIXME: Refactor this into it's own SearchConversion class including the conversions done.
     */
    private final Map<String, SearchConverter<? extends IAttribute>> searchConversions = new HashMap<String, SearchConverter<? extends IAttribute>>();

    public SearchParamsConverter() {
        // Load the Attributes map
        attrDefsRef = SyncedAttrDefinitionMgr.getInstance(IndexerFactory.getCurrentStore());

        // TODO: Make this user configurable rather than hard-coded
        UKPostcodeToVectorDerivation postcodeToSearch = new UKPostcodeToVectorDerivation("Location");
        searchConversions.put("Postcode", postcodeToSearch);
        searchConversions.put("PostCode", postcodeToSearch);

        postcodeToSearch = new UKPostcodeToVectorDerivation("StartLocation");
        searchConversions.put("StartPostcode", postcodeToSearch);

        postcodeToSearch = new UKPostcodeToVectorDerivation("EndLocation");
        searchConversions.put("EndPostcode", postcodeToSearch);

        System.err.println("=================================");
        System.err.println( getClass().getSimpleName() + ": Warning: Hard-coded Start/EndPostcode->Start/EndLocation conversions in operation!");
        System.err.println("=================================");

    }


    public void buildSearchAttributes(SearchSpec searchSpec, Map<String, Attribute<?>> attributes) {
        CardinalAttributeMap<IAttribute> attrs = AttrsFactory.getCardinalAttributeMap();
        for (Entry<String, Attribute<?>> entry : attributes.entrySet()) {
            deriveSearchAttrs( attrs, entry.getKey(), entry.getValue() );
            addInternalAttribute(attrs, entry.getKey(), entry.getValue() );
            
        }
        searchSpec.setAttributes(attrs);
    }

    /**
     * Adds the internal representation of attr, to the index object.
     */
    private void addInternalAttribute(CardinalAttributeMap<IAttribute> attrs, String name, Attribute<?> value) {
        IAttribute attribute = getIAttribute(name, value);
        attrs.put(attribute.getAttrId(), attribute);
    }

    /**
     * Perform the search conversion for this attr, if it exists
     */
    private void deriveSearchAttrs(CardinalAttributeMap<IAttribute> attrs, String name, Attribute<?> value) {
        SearchConverter<? extends IAttribute> conversion = searchConversions.get(name);
        if (conversion == null) {
            return;
        }
        int attrId = getAttrDefs().getAttrId( conversion.getDerivedAttrName(), conversion.getInboundClass() );
        String str;
        if (value instanceof NonIndexStringAttribute){
        	str = ((NonIndexStringAttribute)value).getValue();
        } else {
        	str = ((UnspecifiedTypeAttribute)value).getValue();
        }
        IAttribute internal = conversion.convertStringToInternal(attrId, str);
        attrs.put(attrId, internal);
    }
    
    
    /**
     *  FIXME: This is duplicate of code from RecordConverter.  There is notable overlap.
     *  Get IAttribute to give to database, given external types (beans?)
     */
    private IAttribute getIAttribute(String name, Attribute<?> value) {

        int attrId = getAttrDefs().getAttrId(name); // Attribute must exist

        AttrType type = AttrDefinitionMgr.getAttrType(attrId);
        
        if (type == AttrType.enumExclusiveValue){ // enumDef must also exist
            EnumDefinition enumDef = getAttrDefs().getEnumDefForAttrId(attrId);
            String str = ((UnspecifiedTypeAttribute)value).getValue();
            return enumDef.getEnumValue(str, attrId);
        } else {
            return ConversionFactory.convert(attrId, value);
        }
    }

	private AttrDefinitionMgr getAttrDefs() {
		return attrDefsRef.getObject();
	}

}
