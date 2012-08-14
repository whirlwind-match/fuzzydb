package org.fuzzydb.spring.repository.support;

import java.lang.annotation.Annotation;

import org.fuzzydb.spring.config.FuzzyRepositoryConfigurationExtension;
import org.springframework.data.repository.config.RepositoryBeanDefinitionRegistrarSupport;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;

class FuzzyRepositoriesRegistrar extends RepositoryBeanDefinitionRegistrarSupport {

	@Override
	protected Class<? extends Annotation> getAnnotation() {
		return EnableFuzzyRepositories.class;
	}

	@Override
	protected RepositoryConfigurationExtension getExtension() {
		return new FuzzyRepositoryConfigurationExtension();
	}
}
