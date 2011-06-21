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

import gnu.trove.TIntObjectHashMap;
import gnu.trove.TIntObjectIterator;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

import com.wwm.attrs.AttributeDefinitionService;
import com.wwm.attrs.enums.EnumDefinition;
import com.wwm.attrs.enums.EnumValue;
import com.wwm.attrs.internal.AttrDefinitionMgr;
import com.wwm.attrs.internal.SyncedAttrDefinitionMgr;
import com.wwm.attrs.userobjects.StandaloneWWIndexData;
import com.wwm.db.core.LogFactory;
import com.wwm.db.whirlwind.internal.IAttribute;
import com.wwm.indexer.IndexerFactory;
import com.wwm.indexer.Record;
import com.wwm.indexer.db.converters.ConversionFactory;
import com.wwm.indexer.db.converters.InboundDerivation;
import com.wwm.indexer.db.converters.UKPostcodeToPlaceDerivation;
import com.wwm.indexer.db.converters.UKPostcodeToVectorDerivation;
import com.wwm.indexer.exceptions.IndexerException;
import com.wwm.model.attributes.Attribute;
import com.wwm.model.attributes.EnumeratedAttribute;
import com.wwm.model.attributes.NonIndexStringAttribute;
import com.wwm.model.attributes.NonIndexedAttribute;
import com.wwm.util.DynamicRef;

/**
 * This is where all the to and from conversion goes on.
 * Responsibilities of this class:
 * - converting a public "Record" to internal index data, and other internal objects
 * - generating derived attributes for storage (e.g. Location from Postcode)
 * - converting all internal stuff into public stuff
 * - generating public view of internal attributes: e.g. generating (lat,lon) from EcefVector
 *
 * FIXME: We'll need to accept user configuration and persist it at some point
 * ... for now, we just hard code some defaults.
 */
public class RecordConverter {

    static private Logger log = LogFactory.getLogger(RecordConverter.class);

    private DynamicRef<? extends AttrDefinitionMgr> attrDefsRef = null;

    private final Map<String, List<InboundDerivation<?>>> inboundDerivations = new HashMap<String, List<InboundDerivation<?>>>();

    public RecordConverter() {
        // Load the Attributes map
    	attrDefsRef = SyncedAttrDefinitionMgr.getInstance(IndexerFactory.getCurrentStore());

        // TODO: Make this user configurable rather than hard-coded
        List<InboundDerivation<?>> postcodeDerivations = new LinkedList<InboundDerivation<?>>();
        postcodeDerivations.add( new UKPostcodeToVectorDerivation("Location"));
        postcodeDerivations.add( new UKPostcodeToPlaceDerivation("Place"));
        inboundDerivations.put("Postcode", postcodeDerivations);
        inboundDerivations.put("PostCode", postcodeDerivations); // to cope with spelling variation

        postcodeDerivations = new LinkedList<InboundDerivation<?>>();
        postcodeDerivations.add( new UKPostcodeToVectorDerivation("StartLocation"));
        postcodeDerivations.add( new UKPostcodeToPlaceDerivation("StartPlace"));
        inboundDerivations.put("StartPostcode", postcodeDerivations);

        postcodeDerivations = new LinkedList<InboundDerivation<?>>();
        postcodeDerivations.add( new UKPostcodeToVectorDerivation("EndLocation"));
        postcodeDerivations.add( new UKPostcodeToPlaceDerivation("EndPlace"));
        inboundDerivations.put("EndPostcode", postcodeDerivations);
    }


    /**
     * Add Record data to an object to be stored in the index
     */
    public void convertRecordToInternal(StandaloneWWIndexData index, Record record) {
        index.setDescription(record.getTitle());
        for (Attribute<?> attr : record.getAttributes().values()) {
            addInternalAttribute(index, attr);
            addDerivedAttributes(index, attr);
        }
    }


    /**
     * Adds the internal representation of attr, to the index object.
     */
    private void addInternalAttribute(StandaloneWWIndexData index, Attribute<?> attr) {
        String name = attr.getName();
        if (attr instanceof NonIndexedAttribute){
            // NOTE: Only String supported so far.
            NonIndexStringAttribute str = (NonIndexStringAttribute) attr;
            int attrid = getAttrDefs().getAttrId(name, str.getClass());
            index.setNonIndexString(attrid, str.getValue());
        } else {
            IAttribute attribute = getIAttribute(name, attr);
            if (attribute==null){
                log.error("null attr: " + attr.toString());
                attribute = getIAttribute(name, attr); // was for debugging only
            }
            index.getAttributeMap().putAttr(attribute);
        }
    }

    /**
     * Iterate over inbound derivations that are configured for this attr,
     * and store them either as index attrs, or as strings.
     */
    private void addDerivedAttributes(StandaloneWWIndexData index, Attribute<?> attr) {
        String name = attr.getName();
        List<InboundDerivation<?>> derivations = inboundDerivations.get(name);
        if (derivations == null) {
            return;
        }
        for (InboundDerivation<?> derivation : derivations) {
            int attrId = getAttrDefs().getAttrId( derivation.getDerivedAttrName(), derivation.getInboundClass() );
            Object derived = derivation.convertToInternal(attrId, attr);
            // If derivation returns an index attribute, then stick it in the index, else add non-indexed string
            if (derived instanceof IAttribute) {
                index.getAttributeMap().putAttr((IAttribute) derived);
            } else {
                index.setNonIndexString(attrId, (String) derived);
            }
        }
    }


    public void convertInternalToRecord(RecordImpl record, StandaloneWWIndexData index) {
        record.setTitle(index.getDescription());

        for (IAttribute attr : index.getAttributeMap()) {

            String name = getAttrDefs().getAttrName(attr.getAttrId());
            if (name == null) {
                throw new IndexerException("Indexer Error. buildRecord " + attr.getAttrId() + " not Found");
            }
            if (attr instanceof EnumValue){
                EnumValue enumValue = (EnumValue) attr;
                EnumDefinition def = getAttrDefs().getEnumDef(enumValue.getEnumDefId());
                record.getAttributes().put(name, ConversionFactory.getInstance().convert(name, def, enumValue));
            } else {
                record.getAttributes().put(name, ConversionFactory.getInstance().convert(name, attr));
            }
        }

        // now do non index attrs
        TIntObjectHashMap<String> map = index.getNonIndexAttrs();
        TIntObjectIterator<String> iterator = map.iterator();
        for (int i = map.size(); i-- > 0;) {
            iterator.advance();
            String name = getAttrDefs().getAttrName(iterator.key());
            NonIndexStringAttribute value = new NonIndexStringAttribute(name, iterator.value());
			record.getAttributes().put(name, value );
            // new NonIndexStringAttribute(name, iterator.value() ) );
        }
    }


    /** 
     *  Get IAttribute to give to database, given external types (beans?)
     */
    private IAttribute getIAttribute(String name, Attribute<?> value) {

        int attrid = getAttrDefs().getAttrId(name, value.getClass());
        // Was ,null) and comment: // we're expecting the attribute to have been configured, so we pass null
        // Now. Supply class so can give it correct encoding

        if (value instanceof EnumeratedAttribute){
            EnumeratedAttribute<?> enumAttr = (EnumeratedAttribute<?>) value;
            EnumDefinition enumDef = getAttrDefs().getEnumDefinition(enumAttr.getEnumName());
            return ConversionFactory.getInstance().convert(attrid, enumDef, enumAttr);
        } else {
            return ConversionFactory.getInstance().convert(attrid, value);
        }
    }


	private AttributeDefinitionService getAttrDefs() {
		return attrDefsRef.getObject();
	}
}
