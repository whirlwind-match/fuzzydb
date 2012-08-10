package org.fuzzydb.spring.repository.support;

import java.io.Serializable;

import org.fuzzydb.attrs.AttributeDefinitionService;
import org.fuzzydb.attrs.converters.WhirlwindConversionService;
import org.fuzzydb.spring.repository.FuzzyRepository;
import org.fuzzydb.spring.repository.RawCRUDRepository;
import org.fuzzydb.spring.repository.SimpleMappingFuzzyRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import com.wwm.db.DataOperations;


public class FuzzyRepositoryFactory extends RepositoryFactorySupport {

	private final AttributeDefinitionService attributeDefinitionService;
	
	private final WhirlwindConversionService conversionService;

	private final DataOperations persister;

	
    public FuzzyRepositoryFactory(DataOperations persister, AttributeDefinitionService attributeDefinitionService, WhirlwindConversionService conversionService) {
		this.persister = persister;
		this.attributeDefinitionService = attributeDefinitionService;
		this.conversionService = conversionService;
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
        if (FuzzyRepository.class.isAssignableFrom(repositoryInterface)) {
        	SimpleMappingFuzzyRepository repo = new SimpleMappingFuzzyRepository<T>((Class<T>) metadata.getDomainType(), false, 
        			persister, conversionService, attributeDefinitionService);
        	repo.afterPropertiesSet();
        	return repo;
        }
        
        if (CrudRepository.class.isAssignableFrom(repositoryInterface)) {
        	RawCRUDRepository crudRepository = new RawCRUDRepository(metadata.getDomainType(), persister);
			crudRepository.afterPropertiesSet();
			return crudRepository;
        } 

        throw new UnsupportedOperationException("Cannot (yet) create repository for interface: " + metadata.getRepositoryInterface());
    }


    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {

        Class<?> repositoryInterface = metadata.getRepositoryInterface();

        if (FuzzyRepository.class.isAssignableFrom(repositoryInterface)) {
        	return SimpleMappingFuzzyRepository.class;
        }
        
    	if (CrudRepository.class.isAssignableFrom(repositoryInterface)) {
            return RawCRUDRepository.class;
        }

    	throw new UnsupportedOperationException("Cannot (yet) create repository for interface: " + metadata.getRepositoryInterface());
    }


	@Override
	public <T, ID extends Serializable> EntityInformation<T, ID> getEntityInformation(Class<T> domainClass) {
		return new FuzzyEntityInformation<T,ID>(domainClass);
	}
}
