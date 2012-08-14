package org.fuzzydb.spring.config;

import org.fuzzydb.attrs.converters.WhirlwindConversionService;
import org.fuzzydb.attrs.internal.CurrentTxAttrDefinitionMgr;
import org.fuzzydb.spring.repository.support.FuzzyRepositoryFactoryBean;
import org.fuzzydb.spring.transaction.WhirlwindExceptionTranslator;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.data.repository.config.AnnotationRepositoryConfigurationSource;
import org.springframework.data.repository.config.RepositoryConfigurationExtensionSupport;
import org.springframework.data.repository.config.RepositoryConfigurationSource;
import org.springframework.data.repository.config.XmlRepositoryConfigurationSource;

public class FuzzyRepositoryConfigurationExtension extends RepositoryConfigurationExtensionSupport {

    private static final Class<?> EXCEPTION_TRANSLATOR = WhirlwindExceptionTranslator.class;

    @Override
    protected String getModulePrefix() {
    	return "fuzzy";
    }
    
    @Override
	public String getRepositoryFactoryClassName() {
		return FuzzyRepositoryFactoryBean.class.getName();
	}

	@Override
	public void registerBeansForRoot(BeanDefinitionRegistry registry,	RepositoryConfigurationSource source) {
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
	public void postProcess(BeanDefinitionBuilder builder, AnnotationRepositoryConfigurationSource config) {
		super.postProcess(builder, config);

		// Do something with our context...
	}

	
	@Override
	public void postProcess(BeanDefinitionBuilder builder,
			XmlRepositoryConfigurationSource config) {
		super.postProcess(builder, config);

		// Do something with our context...

	}
}
