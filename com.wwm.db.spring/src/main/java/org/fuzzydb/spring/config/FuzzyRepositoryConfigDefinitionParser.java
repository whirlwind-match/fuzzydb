package org.fuzzydb.spring.config;

import org.fuzzydb.spring.config.SimpleFuzzyRepositoryConfiguration.FuzzyRepositoryConfiguration;
import org.springframework.data.repository.config.AbstractRepositoryConfigDefinitionParser;
import org.w3c.dom.Element;

public class FuzzyRepositoryConfigDefinitionParser 
		extends AbstractRepositoryConfigDefinitionParser<SimpleFuzzyRepositoryConfiguration,FuzzyRepositoryConfiguration> {


	@Override
	protected SimpleFuzzyRepositoryConfiguration getGlobalRepositoryConfigInformation(Element element) {
		return new SimpleFuzzyRepositoryConfiguration(element);
	}

}
