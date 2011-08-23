package org.fuzzydb.spring.repository.support;

import java.io.Serializable;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import com.wwm.db.DataOperations;
import com.wwm.db.spring.repository.RawCRUDRepository;


public class FuzzyRepositoryFactory extends RepositoryFactorySupport {

	private DataOperations persister;

    public FuzzyRepositoryFactory(DataOperations persister) {
		this.persister = persister;
	}


	@Override
    protected Object getTargetRepository(RepositoryMetadata metadata) {

        return createTargetRepository(metadata);
    }


    @SuppressWarnings({ "unchecked", "rawtypes" })
	protected <T, ID extends Serializable> CrudRepository<T, ID> createTargetRepository(
            RepositoryMetadata metadata) {

        Class<?> repositoryInterface = metadata.getRepositoryInterface();
        
        // depending on interface .. create diff implementations
        if (CrudRepository.class.isAssignableFrom(repositoryInterface)) {
        	RawCRUDRepository crudRepository = new RawCRUDRepository(metadata.getDomainClass(), persister);
			crudRepository.afterPropertiesSet();
			return crudRepository;
        } else {
            throw new UnsupportedOperationException("Cannot (yet) create repository for interface: " + metadata.getRepositoryInterface());
        }
    }


    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {

        if (CrudRepository.class.isAssignableFrom(metadata.getRepositoryInterface())) {
            return RawCRUDRepository.class;
        } else {
            throw new UnsupportedOperationException("Cannot (yet) create repository for interface: " + metadata.getRepositoryInterface());
        }
    }


	@Override
	public <T, ID extends Serializable> EntityInformation<T, ID> getEntityInformation(Class<T> domainClass) {
		return new FuzzyEntityInformation<T,ID>(domainClass);
	}
}
