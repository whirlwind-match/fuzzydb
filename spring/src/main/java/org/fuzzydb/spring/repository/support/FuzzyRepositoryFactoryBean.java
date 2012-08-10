package org.fuzzydb.spring.repository.support;

import java.io.Serializable;

import org.fuzzydb.attrs.AttributeDefinitionService;
import org.fuzzydb.attrs.converters.WhirlwindConversionService;
import org.fuzzydb.client.DataOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.core.support.TransactionalRepositoryFactoryBeanSupport;



public class FuzzyRepositoryFactoryBean<T extends CrudRepository<S, ID>, S, ID extends Serializable>
        extends TransactionalRepositoryFactoryBeanSupport<T, S, ID> {

	@Autowired
	private DataOperations persister;
	
	@Autowired
	private AttributeDefinitionService attributeDefinitionService;
	
	@Autowired
	private WhirlwindConversionService conversionService;
	

    @Override
    protected RepositoryFactorySupport doCreateRepositoryFactory() {
        return new FuzzyRepositoryFactory(persister, attributeDefinitionService, conversionService);
    }
}
