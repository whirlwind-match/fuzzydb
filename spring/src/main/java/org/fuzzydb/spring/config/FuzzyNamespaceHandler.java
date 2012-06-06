package org.fuzzydb.spring.config;

import org.springframework.beans.factory.xml.NamespaceHandler;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

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
		registerBeanDefinitionParser("repositories", new FuzzyRepositoryConfigDefinitionParser());
	}
}
