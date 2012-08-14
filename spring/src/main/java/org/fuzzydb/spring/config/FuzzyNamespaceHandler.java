package org.fuzzydb.spring.config;

import org.springframework.beans.factory.xml.NamespaceHandler;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.springframework.data.repository.config.RepositoryBeanDefinitionParser;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;

/**
 * {@link NamespaceHandler} for FuzzyDB
 * 
 * @author Neale Upstone
 */
public class FuzzyNamespaceHandler extends NamespaceHandlerSupport {

	@Override
	public void init() {

		registerBeanDefinitionParser("store", new FuzzyStoreConfigParser());
		registerBeanDefinitionParser("embedded-database", new FuzzyStoreConfigParser());
		registerBeanDefinitionParser("initialize", new FuzzyInitializeConfigParser());
		registerBeanDefinitionParser("tx-manager", new FuzzyTxManagerConfigParser());
		registerBeanDefinitionParser("repository", new FuzzyRepositoryConfigParser());
		
        RepositoryConfigurationExtension extension = new FuzzyRepositoryConfigurationExtension();
    	RepositoryBeanDefinitionParser repositoryBeanDefinitionParser = new RepositoryBeanDefinitionParser(extension);

        registerBeanDefinitionParser("repositories", repositoryBeanDefinitionParser);
	}
}
