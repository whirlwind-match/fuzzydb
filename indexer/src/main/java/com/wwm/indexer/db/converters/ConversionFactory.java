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
package com.wwm.indexer.db.converters;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import org.fuzzydb.attrs.bool.BooleanValue;
import org.fuzzydb.attrs.enums.EnumDefinition;
import org.fuzzydb.attrs.enums.EnumExclusiveValue;
import org.fuzzydb.attrs.enums.EnumMultipleValue;
import org.fuzzydb.attrs.enums.EnumValue;
import org.fuzzydb.attrs.internal.AttrDefinitionMgr;
import org.fuzzydb.attrs.internal.AttrDefinitionMgr.AttrType;
import org.fuzzydb.attrs.location.EcefVector;
import org.fuzzydb.attrs.simple.FloatRangePreference;
import org.fuzzydb.attrs.simple.FloatValue;
import org.fuzzydb.attrs.string.StringValue;
import org.fuzzydb.core.whirlwind.internal.IAttribute;
import org.fuzzydb.dto.attributes.Attribute;
import org.fuzzydb.dto.attributes.BooleanAttribute;
import org.fuzzydb.dto.attributes.DateAttribute;
import org.fuzzydb.dto.attributes.DateRangeAttribute;
import org.fuzzydb.dto.attributes.EnumAttribute;
import org.fuzzydb.dto.attributes.EnumeratedAttribute;
import org.fuzzydb.dto.attributes.FloatAttribute;
import org.fuzzydb.dto.attributes.FloatRangeAttribute;
import org.fuzzydb.dto.attributes.IntegerAttribute;
import org.fuzzydb.dto.attributes.MultiEnumAttribute;
import org.fuzzydb.dto.attributes.NonIndexStringAttribute;
import org.fuzzydb.dto.attributes.Point3DAttribute;


import com.thoughtworks.xstream.XStream;


public class ConversionFactory {

	static private final ConversionFactory instance = new ConversionFactory();
	
	public static ConversionFactory getInstance() {
		return instance;
	}
	
    private Map<Integer, AttributeConverter> idConverters = new TreeMap<Integer, AttributeConverter>();
    private Map<String, AttributeConverter> classConverters = new TreeMap<String, AttributeConverter>();

    private Map<Integer, EnumeratedConverter> enumIdConverters = new TreeMap<Integer, EnumeratedConverter>();
    private Map<String, EnumeratedConverter> enumClassConverters = new TreeMap<String, EnumeratedConverter>();
 
    {

        // ---------------------------------------
        // Default Converters
        // ---------------------------------------

        // Register the default conversion for classes
        BooleanConverter booleanConverter = BooleanConverter.getInstance();
        register(BooleanAttribute.class, booleanConverter);
        register(BooleanValue.class, booleanConverter);

        EnumConverter enumConverter = EnumConverter.getInstance();;
        registerEnum(EnumAttribute.class, enumConverter);
        registerEnum(EnumExclusiveValue.class, enumConverter);

        MultiEnumConverter multiConverter = MultiEnumConverter.getInstance();;
        registerEnum(MultiEnumAttribute.class, multiConverter);
        registerEnum(EnumMultipleValue.class, multiConverter);

        FloatConverter floatConverter = FloatConverter.getInstance();
        register(FloatAttribute.class, floatConverter);
        register(FloatValue.class, floatConverter);
 
        FloatRangeConverter floatRangeConverter = FloatRangeConverter.getInstance();
        register(FloatRangeAttribute.class, floatRangeConverter);
        register(FloatRangePreference.class, floatRangeConverter);

        // Not the most useful of converters in the forward case... prob not well tested.
        register(Point3DAttribute.class, EcefConverter.getInstance() );
        register(EcefVector.class, EcefConverter.getInstance() ); // EcefVector -> EcefVector (for now)

 
        StringConverter stringConverter = StringConverter.getInstance();
        register(NonIndexStringAttribute.class, stringConverter);
        register(StringValue.class, stringConverter);


        // Non symmetrical classes. I.e IAttribute already defined above
        // Need to register reverse convert against attribute Id
        register(DateAttribute.class, DateConverter.getInstance() ); // uses FloatValue
        register(DateRangeAttribute.class, DateRangeConverter.getInstance() ); // uses FloatRangePreference
        register(IntegerAttribute.class, IntegerConverter.getInstance() ); // uses FloatValue

    }

    public void save(String xmlPath) {
        try {
            new XStream().toXML(idConverters, new FileWriter(xmlPath + File.separator + "idconverters.xml"));
            new XStream().toXML(classConverters, new FileWriter(xmlPath + File.separator + "classConverters.xml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @SuppressWarnings("unchecked")
    public void load(String xmlPath) {
        try {
            FileReader reader = new FileReader(xmlPath + File.separator + "idconverters.xml");
            idConverters = (TreeMap<Integer, AttributeConverter>) new XStream().fromXML(reader);
            classConverters = (TreeMap<String, AttributeConverter>) new XStream().fromXML(new FileReader(xmlPath + File.separator + "classConverters.xml"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    private static AttributeConverter getConverter(int attrId, Object value) {

        // Get converter based on attribute type - If attribute type fully encodes type, then shouldn't
    	// need 'idConverters' to be hand set.
    	AttributeConverter c = getConverterFromAttrType(attrId);
    	return c;
    }
    
    /**
     * based on the AttrType for the given attrId, return an appropriate converter
     * @param attrId
     * @return AttributeConverter
     */
	private static AttributeConverter getConverterFromAttrType(int attrId) {
		AttrType type = AttrDefinitionMgr.getAttrType(attrId);

    	switch (type){
    	case unknownTypeValue: return StringConverter.getInstance();
    	case booleanValue: return BooleanConverter.getInstance();
    	case dateValue: return DateConverter.getInstance();
    	case floatValue: return FloatConverter.getInstance();
    	case floatRangePrefValue: return FloatRangeConverter.getInstance();
    	case stringValue: return StringConverter.getInstance();
    	case vectorValue: return EcefConverter.getInstance();
    	default: throw new Error("No converter is supported for AttrType: " + type);
    	}
	}

    /**
     * based on the AttrType for the given attrId, return an appropriate enum converter
     * @param attrId
     * @return EnumeratedConverter
     */
	private static EnumeratedConverter getEnumConverterFromAttrType(int attrId) {
		AttrType type = AttrDefinitionMgr.getAttrType(attrId);
    	switch (type){
    	case enumExclusiveValue: return EnumConverter.getInstance();
    	case enumMultiValue: return MultiEnumConverter.getInstance();
    	default: throw new Error("Only Enum types are supported here: not " + type);
    	}
	}

	
    private static EnumeratedConverter getEnumConverter(int attrId, Object value) {

        // Try Attribute Id
        EnumeratedConverter c = getEnumConverterFromAttrType(attrId);
        if (c != null) {
            return c;
        }

        throw new UnsupportedOperationException( "Unknown convertion for Attribute " + attrId + " of type " + value.getClass().getSimpleName());
    }

    /**
     * AttrName is supplied so that it can be encapsulated in the object where
     * converters support it (which should be always, as Object should become
     * Attribute.
     * FIXME: I think this should return Attribute, not Object 
     * @param name
     * @param attribute
     * @return
     */
    public Attribute<?> convert(String name, IAttribute attribute) {
        return getConverter(attribute.getAttrId(), attribute).convert(name, attribute);
    }

    public IAttribute convert(int attrid, Attribute<?> object) {
        return getConverter(attrid, object).convertToInternal(attrid, object);
    }

    public EnumeratedAttribute<?> convert(String name, EnumDefinition def, EnumValue enumValue) {
        return getEnumConverter(enumValue.getAttrId(), enumValue).convert(name, def, enumValue);
    }

    public IAttribute convert(int attrid, EnumDefinition enumDef, EnumeratedAttribute<?> enumAttr) {
        return getEnumConverter(attrid, enumAttr).convertToInternal(attrid, enumDef, enumAttr);
    }

    @Deprecated // want to get rid of idConverters as we can do on class alone
    public Class<?> getIAttributeClass(int attrid, Class<?> clazz) {
        AttributeConverter c = idConverters.get(attrid);
        if (c != null) {
            return c.getIAttributeClass();
        }

        // Try Default Class conversion
        c = classConverters.get(clazz.getSimpleName());
        if (c != null) {
            return c.getIAttributeClass();
        }

        throw new UnsupportedOperationException( "Unknown convertion for Attribute " + attrid + " of type " + clazz.getSimpleName());
    }


    private void register(Class<?> clazz, AttributeConverter converter) {
        if (converter.getIAttributeClass() != clazz && converter.getObjectClass() != clazz) {
            throw new UnsupportedOperationException("Converter " + converter.getClass().getSimpleName() + " is not valid for class " + clazz.getSimpleName());
        }

        if (classConverters.get(clazz.getSimpleName()) != null) {
            throw new UnsupportedOperationException("Converter for class " + clazz.getSimpleName() + " is already registered");
        }
        classConverters.put(clazz.getSimpleName(), converter);
    }

    private void registerEnum(Class<?> clazz, EnumeratedConverter converter) {
        if (converter.getIAttributeClass() != clazz && converter.getObjectClass() != clazz) {
            throw new UnsupportedOperationException("Converter " + converter.getClass().getSimpleName() + " is not valid for class " + clazz.getSimpleName());
        }

        if (enumClassConverters.get(clazz.getSimpleName()) != null) {
            throw new UnsupportedOperationException("EnumConverter for class " + clazz.getSimpleName() + " is already registered");
        }
        enumClassConverters.put(clazz.getSimpleName(), converter);
    }


    public void register(int attrId, AttributeConverter converter) {
        idConverters.put(attrId, converter);
    }

    public void register(int attrId, EnumeratedConverter converter) {
        enumIdConverters.put(attrId, converter);
    }
}
