package com.wwm.attrs.converters;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.convert.support.GenericConversionService;

public class WhirlwindConversionService extends GenericConversionService implements InitializingBean {

	public void afterPropertiesSet() throws Exception {
		// from db to Java
		addConverter(new AttrToBooleanConverter());
		addConverter(new AttrToFloatConverter());
		
		// from Java to db
		addConverter(new BooleanToAttrConverter());
		addConverter(new FloatToAttrConverter());
	}
}
