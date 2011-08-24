package org.fuzzydb.spring.config;

import org.fuzzydb.spring.config.SimpleFuzzyRepositoryConfiguration.FuzzyRepositoryConfiguration;
import org.fuzzydb.spring.repository.support.FuzzyRepositorySupport;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.data.repository.config.AbstractRepositoryConfigDefinitionParser;
import org.w3c.dom.Element;

public class FuzzyRepositoryConfigDefinitionParser extends
		AbstractRepositoryConfigDefinitionParser<SimpleFuzzyRepositoryConfiguration, FuzzyRepositoryConfiguration> {

	/**
	 * Override parse to pick up on beans we need to register at this point.
	 */
	@Override
	public BeanDefinition parse(Element element, ParserContext parser) {
		BeanDefinition beanDefinition = super.parse(element, parser);
		FuzzyRepositorySupport.registerFuzzySupportBeans(parser);
		return beanDefinition;
	}

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
