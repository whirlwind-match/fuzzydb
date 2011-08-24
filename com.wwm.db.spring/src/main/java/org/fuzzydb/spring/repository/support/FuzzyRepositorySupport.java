package org.fuzzydb.spring.repository.support;

import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.xml.ParserContext;

import com.wwm.attrs.AttributeDefinitionService;
import com.wwm.attrs.converters.WhirlwindConversionService;
import com.wwm.attrs.internal.CurrentTxAttrDefinitionMgr;

/**
 * Support methods for use when creating repositories
 * 
 * @author Neale Upstone
 *
 */
public abstract class FuzzyRepositorySupport {

	/**
	 * Creates {@link WhirlwindConversionService} and {@link AttributeDefinitionService} instances
	 * for autowiring.
	 */
	static public void registerFuzzySupportBeans(ParserContext parserContext) {
		// Conversion service default
		if (!parserContext.getRegistry().containsBeanDefinition("whirlwindConversionService")) {
		    BeanDefinitionBuilder conversionServiceBuilder = BeanDefinitionBuilder.genericBeanDefinition(WhirlwindConversionService.class);
		    BeanDefinitionHolder def = new BeanDefinitionHolder(conversionServiceBuilder.getBeanDefinition(), "whirlwindConversionService");
		    BeanDefinitionReaderUtils.registerBeanDefinition(def, parserContext.getRegistry());      
		}
	
		// Conversion service default
		if (!parserContext.getRegistry().containsBeanDefinition("attributeDefinitionService")) {
		    BeanDefinitionBuilder attributeDefinitionServiceBuilder = BeanDefinitionBuilder.genericBeanDefinition(CurrentTxAttrDefinitionMgr.class);
		    BeanDefinitionHolder def = new BeanDefinitionHolder(attributeDefinitionServiceBuilder.getBeanDefinition(), "attributeDefinitionService");
		    BeanDefinitionReaderUtils.registerBeanDefinition(def, parserContext.getRegistry());      
		}
	}

}
