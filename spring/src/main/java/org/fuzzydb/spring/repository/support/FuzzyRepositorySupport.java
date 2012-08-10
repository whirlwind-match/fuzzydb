package org.fuzzydb.spring.repository.support;

import org.fuzzydb.attrs.AttributeDefinitionService;
import org.fuzzydb.attrs.converters.WhirlwindConversionService;
import org.fuzzydb.attrs.internal.CurrentTxAttrDefinitionMgr;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

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
	static public void registerFuzzySupportBeans(BeanDefinitionRegistry registry) {
		
		// Conversion service default
		if (!registry.containsBeanDefinition("whirlwindConversionService")) {
		    BeanDefinitionBuilder conversionServiceBuilder = BeanDefinitionBuilder.genericBeanDefinition(WhirlwindConversionService.class);
		    BeanDefinitionHolder def = new BeanDefinitionHolder(conversionServiceBuilder.getBeanDefinition(), "whirlwindConversionService");
		    BeanDefinitionReaderUtils.registerBeanDefinition(def, registry);      
		}
	
		// Conversion service default
		if (!registry.containsBeanDefinition("attributeDefinitionService")) {
		    BeanDefinitionBuilder attributeDefinitionServiceBuilder = BeanDefinitionBuilder.genericBeanDefinition(CurrentTxAttrDefinitionMgr.class);
		    BeanDefinitionHolder def = new BeanDefinitionHolder(attributeDefinitionServiceBuilder.getBeanDefinition(), "attributeDefinitionService");
		    BeanDefinitionReaderUtils.registerBeanDefinition(def, registry);      
		}
	}

}
