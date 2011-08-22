package org.fuzzydb.spring.repository.support;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.core.support.TransactionalRepositoryFactoryBeanSupport;

import com.wwm.db.DataOperations;


public class FuzzyRepositoryFactoryBean<T extends CrudRepository<S, ID>, S, ID extends Serializable>
        extends TransactionalRepositoryFactoryBeanSupport<T, S, ID> {

	@Autowired
	private DataOperations persister;
	

    @Override
    protected RepositoryFactorySupport doCreateRepositoryFactory() {
        return new FuzzyRepositoryFactory(persister);
    }
}
