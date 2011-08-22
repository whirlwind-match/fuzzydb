package org.fuzzydb.spring.repository.support;

import java.io.Serializable;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.core.support.TransactionalRepositoryFactoryBeanSupport;


public class FuzzyRepositoryFactoryBean<T extends CrudRepository<S, ID>, S, ID extends Serializable>
        extends TransactionalRepositoryFactoryBeanSupport<T, S, ID> {


    @Override
    protected RepositoryFactorySupport doCreateRepositoryFactory() {

        return new FuzzyRepositoryFactory();
    }
}
