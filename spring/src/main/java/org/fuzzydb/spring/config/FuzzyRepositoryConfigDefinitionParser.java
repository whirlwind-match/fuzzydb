package org.fuzzydb.spring.config;

import org.fuzzydb.spring.config.SimpleFuzzyRepositoryConfiguration.FuzzyRepositoryConfiguration;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.data.repository.config.AbstractRepositoryConfigDefinitionParser;
import org.w3c.dom.Element;

import com.wwm.attrs.converters.WhirlwindConversionService;
import com.wwm.attrs.internal.CurrentTxAttrDefinitionMgr;
import com.wwm.db.spring.transaction.WhirlwindExceptionTranslator;

public class FuzzyRepositoryConfigDefinitionParser extends
		AbstractRepositoryConfigDefinitionParser<SimpleFuzzyRepositoryConfiguration, FuzzyRepositoryConfiguration> {

    private static final Class<?> EXCEPTION_TRANSLATOR = WhirlwindExceptionTranslator.class;
	
	@Override
	protected void registerBeansForRoot(BeanDefinitionRegistry registry, Object source) {
		super.registerBeansForRoot(registry, source);

        if (!hasBean(EXCEPTION_TRANSLATOR, registry)) {
            AbstractBeanDefinition definition =
                    BeanDefinitionBuilder
                            .rootBeanDefinition(EXCEPTION_TRANSLATOR)
                            .getBeanDefinition();

            registerWithSourceAndGeneratedBeanName(registry, definition, source);
        }

		// Conversion service default
        if (!hasBean(WhirlwindConversionService.class, registry)) {
            AbstractBeanDefinition definition =
                    BeanDefinitionBuilder
                            .rootBeanDefinition(WhirlwindConversionService.class)
                            .getBeanDefinition();
            registerWithSourceAndGeneratedBeanName(registry, definition, source);
		}
	
        if (!hasBean(CurrentTxAttrDefinitionMgr.class, registry)) {
            AbstractBeanDefinition definition =
                    BeanDefinitionBuilder
                            .rootBeanDefinition(CurrentTxAttrDefinitionMgr.class)
                            .getBeanDefinition();
            registerWithSourceAndGeneratedBeanName(registry, definition, source);
		}
		

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
