package org.fuzzydb.spring.config;

import org.springframework.beans.factory.xml.NamespaceHandler;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * {@link NamespaceHandler} for FuzzyDB
 *
 * @author Neale Upstone
 */
public class FuzzyNamespaceHandler extends NamespaceHandlerSupport {

	public void init() {

		registerBeanDefinitionParser("store", new FuzzyStoreConfigParser());
	}
}
