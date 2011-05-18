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
package com.wwm.atom.impl;


import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;


import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.protocol.server.RequestContext;

import com.wwm.atom.elements.AbderaElementFactory;
import com.wwm.atom.elements.AttributeElement;
import com.wwm.atom.elements.EntryDecorator;
import com.wwm.atom.elements.FuzzyRecordProperties;
import com.wwm.attrs.location.EcefVector;
import com.wwm.db.core.LogFactory;
import com.wwm.indexer.Record;
import com.wwm.indexer.internal.RecordImpl;
import com.wwm.model.attributes.Attribute;
import com.wwm.model.attributes.DateAttribute;
import com.wwm.model.attributes.FloatAttribute;
import com.wwm.model.attributes.FloatRangeAttribute;
import com.wwm.model.attributes.Point3DAttribute;
import com.wwm.model.attributes.UnspecifiedTypeAttribute;

/**
 * FIXME: There are bits of this that need reviewing.  The conversion to elements from our beans
 * is not configured, but instead is done here with some defaults.
 */
public class FuzzyRecordBuilder extends EntryDecorator {

    protected static Logger log = LogFactory.getLogger(FuzzyRecordBuilder.class);


    public FuzzyRecordBuilder(Entry entry) {
        super(entry);
    }


    public void add(Record attributes) {
        for (java.util.Map.Entry<String, Attribute<?>> entry : attributes.getAttributes().entrySet() ) {
            addAttribute( entry.getKey(), entry.getValue() );
        }
    }

    private void addAttribute(String key, Attribute<?> value) {

        //		System.out.println("adding: " + key + "=" + value );
        if (value instanceof FloatAttribute){
            addFloat(key, ( (FloatAttribute) value).getValue() );
        }
        else if (value instanceof FloatRangeAttribute){ // FIXME: Age and float needed?
            FloatRangeAttribute fr = (FloatRangeAttribute) value;
            addFloatRange(key, fr.getMin(), fr.getMax(), fr.getPref() );
        }
        else if (value instanceof DateAttribute){
            addDate(key, ((DateAttribute) value).getValue());
        }
        else if (value instanceof Point3DAttribute){
            Point3DAttribute loc = (Point3DAttribute) value;

            String latLon;
            if (loc.getPoint() instanceof EcefVector){
                EcefVector vec = (EcefVector) loc.getPoint();
                latLon = String.format("(%.2f,%.2f)", vec.getLatDegs(), vec.getLonDegs() );
            } else {
                latLon = loc.getPoint().toString(); // whatever comes out!
            }
            addLocation(key, latLon );

        }
        else {
            addSimpleAttribute( key, value.toString() );
        }

    }

    /**
     * Convert the supplied document to a FuzzyRecord
     * @param doc - Abdera Document containing an Entry
     * @return FuzzyRecord
     */
    public static Record getRecord(Document<? extends Entry> doc) throws IOException {

        RecordImpl record = new RecordImpl();

        Entry root = doc.getRoot();
        List<Element> extensions = root.getExtensions(AbderaElementFactory.NS);
        Map<String, Attribute<?>> attributes = new HashMap<String, Attribute<?>>();
        for (Element element : extensions) {
            if (element instanceof FuzzyRecordProperties) {
                FuzzyRecordProperties metadata = (FuzzyRecordProperties)element;
                // FIXME: I don't think we need this.  It's just an attribute...?  Or is it.
                // record.setContentType( metadata.getContentType() );
                record.setPrivateId( metadata.getPrivateId() );
            } else {
                // Assume it's an attribute we can put in the map
                AttributeElement attr = (AttributeElement)element;
                //				BeanFactory bf = BeanFactory.getInstance();
                //				Object object = bf.getObject(attr);
                Attribute<?> object = attr.getAttribute();
                System.out.println( attr.getName() + " : " + object );
                attributes.put(attr.getName(), object);
            }
        }
        record.setAttributes(attributes );
        return record;
    }

    /**
     * Get request parameters into a simple name->string attribute map.
     */
    public static Record getRecord(RequestContext request){
        RecordImpl record = new RecordImpl();
        Map<String, Attribute<?>> attributes = new HashMap<String, Attribute<?>>();

        // TODO: Consider changing to iterate over request parameters turning things like Postcode=CB4+2QW into
        // a new PostcodeAttribute("Postcode", "CB4 2QW");

        for (String key : request.getParameterNames()) {
            if (key.equals("matchStyle") || key.equals("numResults")) {
                continue; // filter out the stuff we don't want for now
            }

            // FIXME: This is rather dumb, and difficult to follow.
            // We could lookup a converter by name -> attr class, and then
            // ensure that the converter can turn a string into the required object.
            // This is not what

            //            try {

            // As we don't know the type, we're specific about this
        	UnspecifiedTypeAttribute value = new UnspecifiedTypeAttribute( key, request.getParameter(key));
            attributes.put(key, value);
            //            } catch (IndexerException e) {
            //                // IGNORE any not found
            //                log.info("Ignoring request param: " + key + " when extracting attributes. Not a defined attr.");
            //            }


            // Assume it's an attribute we can put in the map
            //			AttributeElement attr = (AttributeElement)element;
            //			BeanFactory bf = BeanFactory.getInstance();
            //			System.out.println( attr.getName() + " : " + bf.getObject(attr) );
            //			attributes.put(attr.getName(), bf.getObject(attr));
            // throw new Error("need to look up attribute type to do conversion - should be able to do via AttrDefMgr giving us the class, and then calling a String c'tor on that");
        }
        record.setAttributes(attributes );
        return record;

    }
}
