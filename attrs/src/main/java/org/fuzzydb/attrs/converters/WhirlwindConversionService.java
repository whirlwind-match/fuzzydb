package org.fuzzydb.attrs.converters;

import org.fuzzydb.attrs.AttributeDefinitionService;
import org.fuzzydb.util.geo.GeoInformation;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.convert.support.GenericConversionService;


public class WhirlwindConversionService extends GenericConversionService implements InitializingBean {

	@Autowired
	private AttributeDefinitionService attrDefinitionService;

	@Autowired(required=false)
	private Converter<String,GeoInformation> stringToGeo;
	
	// TODO: Refactor to extend ConvSFB instead, so we get the default converters 
	@Override
	public void afterPropertiesSet() throws Exception {

		// simple attributes
		addConverter(new StringToRefConverter());
		addConverter(new RefToStringConverter());
		
		addConverter(new StringToUuidConverter());
		addConverter(new UuidToStringConverter());

		addConverter(new AttrToBooleanConverter());
		addConverter(new BooleanToAttrConverter());

		addConverter(new AttrToFloatConverter());
		addConverter(new FloatToAttrConverter());

		addConverter(new EnumExclusiveValueToStringConverter(attrDefinitionService));
		addConverter(new EnumAttributeToEnumExclusiveValueConverter(attrDefinitionService));

		addConverterFactory(new EnumExclusiveValueToEnumConverterFactory(attrDefinitionService));

		addConverter(new FloatAttrToDateConverter());
		addConverter(new DateToFloatAttrConverter());

		// arrays
		addConverter(new MultiEnumAttributeToEnumMultipleValueConverter(attrDefinitionService));
		addConverter(new EnumMultiValueToStringArrayConverter(attrDefinitionService));

		addConverter(new AttrToFloatArrayConverter());
		addConverter(new FloatArrayToAttrConverter());

		// more special cases
		addConverter(new Point3DAttributeToEcefVectorConverter());
		
		
		// if we have something to resolve location data, also resolve to 3d point
		if (stringToGeo != null) {
			addConverter(new StringToEcefVectorConverter(stringToGeo));
		}
		
		if (bsonObjectIdAvailable()) {
			addConverter(new StringToObjectIdConverter());
			addConverter(new ObjectIdToStringConverter());
		}
		
		DefaultConversionService.addDefaultConverters(this);
	}

	private boolean bsonObjectIdAvailable() {
		try {
			Class.forName("org.bson.types.ObjectId");
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}
	
	
}
