package org.fuzzydb.spring.config;

import org.springframework.data.repository.config.AbstractRepositoryConfigDefinitionParser;
import org.w3c.dom.Element;

public class FuzzyRepositoryConfigDefinitionParser extends AbstractRepositoryConfigDefinitionParser<GlobalFuzzyRepositoryConfigInformation,SingleFuzzyRepositoryConfigInformation> {

	@Override
	protected GlobalFuzzyRepositoryConfigInformation getGlobalRepositoryConfigInformation(Element element) {
		// TODO Auto-generated method stub
		return null;
	}

}
