package com.wwm.attrs.converters;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.support.ConversionServiceFactory;
import org.springframework.core.convert.support.GenericConversionService;

import com.wwm.attrs.AttributeDefinitionService;

public class WhirlwindConversionService extends GenericConversionService implements InitializingBean {

	@Autowired
	private AttributeDefinitionService attrDefinitionService;

	
	// TODO: Refactor to extend ConvSFB instead, so we get the default converters 
	public void afterPropertiesSet() throws Exception {
		// from db to Java
		addConverter(new AttrToBooleanConverter());
		addConverter(new AttrToFloatConverter());
		addConverter(new AttrToFloatArrayConverter());
		addConverter(new EnumExclusiveValueToStringConverter(attrDefinitionService));
		addConverter(new EnumMultiValueToStringArrayConverter(attrDefinitionService));
		
		// from Java to db
		addConverter(new BooleanToAttrConverter());
		addConverter(new FloatToAttrConverter());
		addConverter(new FloatArrayToAttrConverter());
		addConverter(new EnumAttributeToEnumExclusiveValueConverter(attrDefinitionService));
		addConverter(new MultiEnumAttributeToEnumMultipleValueConverter(attrDefinitionService));
		addConverter(new Point3DAttributeToEcefVectorConverter());
		
		addConverter(new StringToRefConverter());
		addConverter(new RefToStringConverter());
		ConversionServiceFactory.addDefaultConverters(this);
	}
	
	
}
