package org.fuzzydb.spring.config;

import org.fuzzydb.spring.config.SimpleFuzzyRepositoryConfiguration.FuzzyRepositoryConfiguration;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.data.repository.config.AbstractRepositoryConfigDefinitionParser;
import org.w3c.dom.Element;

public class FuzzyRepositoryConfigDefinitionParser 
		extends AbstractRepositoryConfigDefinitionParser<SimpleFuzzyRepositoryConfiguration,FuzzyRepositoryConfiguration> {


	@Override
	protected SimpleFuzzyRepositoryConfiguration getGlobalRepositoryConfigInformation(Element element) {
		return new SimpleFuzzyRepositoryConfiguration(element);
	}

	@Override
	protected void postProcessBeanDefinition(FuzzyRepositoryConfiguration context, BeanDefinitionBuilder builder,
			BeanDefinitionRegistry registry, Object beanSource) {
		super.postProcessBeanDefinition(context, builder, registry, beanSource);
		
		// Do something with our context...
	}
	
	
}
