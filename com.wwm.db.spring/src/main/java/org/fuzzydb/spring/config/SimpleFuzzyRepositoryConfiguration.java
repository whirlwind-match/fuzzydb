package org.fuzzydb.spring.config;

import org.springframework.data.repository.config.AutomaticRepositoryConfigInformation;
import org.springframework.data.repository.config.ManualRepositoryConfigInformation;
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


	public FuzzyRepositoryConfiguration getAutoconfigRepositoryInformation(String interfaceName) {
		return new AutomaticFuzzyRepositoryConfigInformation(interfaceName, this);
	}

	public String getNamedQueriesLocation() {
		return "classpath*:/fuzzy/dummy-named-queries.properties"; // This should get ignored by Data Commons if left at default
	}

	protected FuzzyRepositoryConfiguration createSingleRepositoryConfigInformationFor(Element element) {
		return new ManualFuzzyRepositoryConfigInformation(element, this);
	}
	
	
    private static class AutomaticFuzzyRepositoryConfigInformation
    	extends AutomaticRepositoryConfigInformation<SimpleFuzzyRepositoryConfiguration>
    	implements FuzzyRepositoryConfiguration {

		public AutomaticFuzzyRepositoryConfigInformation(String interfaceName,
				SimpleFuzzyRepositoryConfiguration parent) {

			super(interfaceName, parent);
		}
    }


    private static class ManualFuzzyRepositoryConfigInformation extends
        ManualRepositoryConfigInformation<SimpleFuzzyRepositoryConfiguration>
        implements FuzzyRepositoryConfiguration {
    
        public ManualFuzzyRepositoryConfigInformation(Element element,
                SimpleFuzzyRepositoryConfiguration parent) {
        
            super(element, parent);
        }
    }
}
