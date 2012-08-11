package org.fuzzydb.spring.repository;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.fuzzydb.util.ResourcePatternProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.data.repository.CrudRepository;
import org.springframework.oxm.Unmarshaller;


public class RepositoryInitializer<T, ID extends Serializable> implements InitializingBean {

	private static final Logger log = LoggerFactory.getLogger(RepositoryInitializer.class);
	
	private CrudRepository<T, ID> repo;
	
	private String resources;
	
	private Unmarshaller unmarshaller;


	public RepositoryInitializer(CrudRepository<T, ID> repo, Unmarshaller unmarshaller) {
		this.repo = repo;
		this.unmarshaller = unmarshaller;
	}

	/**
	 * @param resources e.g. classpath*:/initObjects/*.xml
	 */
	public void setResources(String resources) { 
		this.resources = resources;
	}

	@Override
	public void afterPropertiesSet() {

        new ResourcePatternProcessor(){
			@SuppressWarnings("unchecked")
			@Override
			protected Closeable process(Resource resource) throws IOException {
				log.info("Loading objects from: {}", resource.getURL());
				
				InputStream inputStream = resource.getInputStream();
				Source source = new StreamSource(inputStream);
				Object object = unmarshaller.unmarshal(source);
				if (object instanceof ArrayList) {
					save((ArrayList<T>)object);
				}
				else {
					save((T)object);
				}
				return inputStream;
			}
        }.runWithResources(resources);
	}

	private void save(final T object) {
		repo.save(object);
	}
	
	private void save(final ArrayList<T> objects) {
		repo.save(objects);
	}
	
}
