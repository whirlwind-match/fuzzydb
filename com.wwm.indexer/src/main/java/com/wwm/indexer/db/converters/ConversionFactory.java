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


import com.thoughtworks.xstream.XStream;
import com.wwm.attrs.bool.BooleanValue;
import com.wwm.attrs.enums.EnumDefinition;
import com.wwm.attrs.enums.EnumExclusiveValue;
import com.wwm.attrs.enums.EnumMultipleValue;
import com.wwm.attrs.enums.EnumValue;
import com.wwm.attrs.internal.AttrDefinitionMgr;
import com.wwm.attrs.internal.AttrDefinitionMgr.AttrType;
import com.wwm.attrs.location.EcefVector;
import com.wwm.attrs.simple.FloatHave;
import com.wwm.attrs.simple.FloatRangePreference;
import com.wwm.attrs.string.StringValue;
import com.wwm.db.whirlwind.internal.IAttribute;
import com.wwm.model.attributes.Attribute;
import com.wwm.model.attributes.BooleanAttribute;
import com.wwm.model.attributes.DateAttribute;
import com.wwm.model.attributes.DateRangeAttribute;
import com.wwm.model.attributes.EnumAttribute;
import com.wwm.model.attributes.EnumeratedAttribute;
import com.wwm.model.attributes.FloatAttribute;
import com.wwm.model.attributes.FloatRangeAttribute;
import com.wwm.model.attributes.IntegerAttribute;
import com.wwm.model.attributes.MultiEnumAttribute;
import com.wwm.model.attributes.NonIndexStringAttribute;
import com.wwm.model.attributes.Point3DAttribute;


public class ConversionFactory {

    public static Map<Integer, AttributeConverter> idConverters = new TreeMap<Integer, AttributeConverter>();
    public static Map<String, AttributeConverter> classConverters = new TreeMap<String, AttributeConverter>();

    public static Map<Integer, EnumeratedConverter> enumIdConverters = new TreeMap<Integer, EnumeratedConverter>();
    public static Map<String, EnumeratedConverter> enumClassConverters = new TreeMap<String, EnumeratedConverter>();
 
    static {

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
        register(FloatHave.class, floatConverter);
 
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
        register(DateAttribute.class, DateConverter.getInstance() ); // uses FloatHave
        register(DateRangeAttribute.class, DateRangeConverter.getInstance() ); // uses FloatRangePreference
        register(IntegerAttribute.class, IntegerConverter.getInstance() ); // uses FloatHave

    }

    public static void save(String xmlPath) {
        try {
            new XStream().toXML(idConverters, new FileWriter(xmlPath + File.separator + "idconverters.xml"));
            new XStream().toXML(classConverters, new FileWriter(xmlPath + File.separator + "classConverters.xml"));
        } catch (IOException e) {
            throw new Error(e);
        }
    }
    @SuppressWarnings("unchecked")
    public static void load(String xmlPath) {
        try {
            FileReader reader = new FileReader(xmlPath + File.separator + "idconverters.xml");
            idConverters = (TreeMap<Integer, AttributeConverter>) new XStream().fromXML(reader);
            classConverters = (TreeMap<String, AttributeConverter>) new XStream().fromXML(new FileReader(xmlPath + File.separator + "classConverters.xml"));
        } catch (FileNotFoundException e) {
            throw new Error(e);
        }
    }


    private static AttributeConverter getConverter(int attrId, Object value) {

        // Get converter based on attribute type - If attribute type fully encodes type, then shouldn't
    	// need 'idConverters' to be hand set.
    	AttributeConverter c = getConverterFromAttrType(attrId);
    	return c;

    	// FIXME: remvoe this after testing
//    	// Try Attribute Id
//        AttributeConverter c = idConverters.get(attrId);
//        if (c != null) {
//            return c;
//        }
//
//        // Try Default Class conversion
//        c = classConverters.get(value.getClass().getSimpleName());
//        if (c != null) {
//            // Add the converter to the id converters to ensure they are symmetrical
//            register(attrId, c);
//            return c;
//        }
//
//        throw new UnsupportedOperationException( "Unknown convertion for Attribute " + attrId + " of type " + value.getClass().getSimpleName());
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

        // FIXME: this is old and can go after some testing. 
//    	// Try Attribute Id
//        EnumeratedConverter c = enumIdConverters.get(attrId);
//        if (c != null) {
//            return c;
//        }
//        // Try Default Class conversion
//        c = enumClassConverters.get(value.getClass().getSimpleName());
//        if (c != null) {
//            // Add the converter to the id converters to ensure they are symmetrical
//            register(attrId, c);
//            return c;
//        }

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
    public static Attribute convert(String name, IAttribute attribute) {
        return getConverter(attribute.getAttrId(), attribute).convert(name, attribute);
    }

    public static IAttribute convert(int attrid, Attribute object) {
        return getConverter(attrid, object).convertToInternal(attrid, object);
    }

    public static EnumeratedAttribute convert(String name, EnumDefinition def, EnumValue enumValue) {
        return getEnumConverter(enumValue.getAttrId(), enumValue).convert(name, def, enumValue);
    }

    public static IAttribute convert(int attrid, EnumDefinition enumDef, EnumeratedAttribute enumAttr) {
        return getEnumConverter(attrid, enumAttr).convertToInternal(attrid, enumDef, enumAttr);
    }

    public static Class<?> getIAttributeClass(int attrid, Class<?> clazz) {
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


    private static void register(Class<?> clazz, AttributeConverter converter) {
        if (converter.getIAttributeClass() != clazz && converter.getObjectClass() != clazz) {
            throw new UnsupportedOperationException("Converter " + converter.getClass().getSimpleName() + " is not valid for class " + clazz.getSimpleName());
        }

        if (classConverters.get(clazz.getSimpleName()) != null) {
            throw new UnsupportedOperationException("Converter for class " + clazz.getSimpleName() + " is already registered");
        }
        classConverters.put(clazz.getSimpleName(), converter);
    }

    private static void registerEnum(Class<?> clazz, EnumeratedConverter converter) {
        if (converter.getIAttributeClass() != clazz && converter.getObjectClass() != clazz) {
            throw new UnsupportedOperationException("Converter " + converter.getClass().getSimpleName() + " is not valid for class " + clazz.getSimpleName());
        }

        if (enumClassConverters.get(clazz.getSimpleName()) != null) {
            throw new UnsupportedOperationException("EnumConverter for class " + clazz.getSimpleName() + " is already registered");
        }
        enumClassConverters.put(clazz.getSimpleName(), converter);
    }


    public static void register(int attrId, AttributeConverter converter) {
        idConverters.put(attrId, converter);
    }

    public static void register(int attrId, EnumeratedConverter converter) {
        enumIdConverters.put(attrId, converter);
    }

    //    public static void register(int attrId, String name) {
    //        if (classConverters.get(name) != null) {
    //            throw new UnsupportedOperationException("Unknown convert type " + name);
    //        }
    //        idConverters.put(attrId, classConverters.get(name));
    //    }
}
