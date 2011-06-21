package com.wwm.attrs.converters;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.convert.support.GenericConversionService;

public class WhirlwindConversionService extends GenericConversionService implements InitializingBean {

	public void afterPropertiesSet() throws Exception {
		addConverter(new AttrToBooleanConverter());
		addConverter(new AttrToFloatConverter());
		
	}


}
