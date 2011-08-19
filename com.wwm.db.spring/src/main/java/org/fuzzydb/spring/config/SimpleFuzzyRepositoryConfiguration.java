package org.fuzzydb.spring.config;

import org.springframework.data.repository.config.RepositoryConfig;
import org.springframework.data.repository.config.SingleRepositoryConfigInformation;
import org.w3c.dom.Element;

public class SimpleFuzzyRepositoryConfiguration  extends
			RepositoryConfig<SimpleFuzzyRepositoryConfiguration.FuzzyRepositoryConfiguration, SimpleFuzzyRepositoryConfiguration> {

    private static final String FACTORY_CLASS =
            "org.fuzzydb.spring.repository.support.FuzzyRepositoryFactoryBean";

	static interface FuzzyRepositoryConfiguration extends
	SingleRepositoryConfigInformation<SimpleFuzzyRepositoryConfiguration> {
	}


    protected SimpleFuzzyRepositoryConfiguration(Element repositoriesElement) {
		super(repositoriesElement, FACTORY_CLASS);
	}


	@Override
	public FuzzyRepositoryConfiguration getAutoconfigRepositoryInformation(String interfaceName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getNamedQueriesLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected FuzzyRepositoryConfiguration createSingleRepositoryConfigInformationFor(Element element) {
		// TODO Auto-generated method stub
		return null;
	}

}
