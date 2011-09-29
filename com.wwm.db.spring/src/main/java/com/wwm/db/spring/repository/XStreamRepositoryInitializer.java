package com.wwm.db.spring.repository;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.data.repository.CrudRepository;
import com.thoughtworks.xstream.XStream;
import com.wwm.util.ResourcePatternProcessor;

public class XStreamRepositoryInitializer<T, ID extends Serializable> implements InitializingBean {

	private static final Logger log = LoggerFactory.getLogger(XStreamRepositoryInitializer.class);
	
	private CrudRepository<T, ID> repo;
	
	private String resources;

	public XStreamRepositoryInitializer(CrudRepository<T, ID> repo) {
		this.repo = repo;
	}

	public static <T, ID extends Serializable> XStreamRepositoryInitializer<T,ID> forRepository(CrudRepository<T, ID> repo) {
		return new XStreamRepositoryInitializer<T, ID>(repo);
	}

	/**
	 * @param resources e.g. classpath*:/initObjects/*.xml
	 */
	public void setResources(String resources) { 
		this.resources = resources;
	}

	public void afterPropertiesSet() {
		final XStream xStream = new XStream();
		xStream.aliasType("objects", ArrayList.class);

        new ResourcePatternProcessor(){
			@SuppressWarnings("unchecked")
			@Override
			protected Closeable process(Resource resource) throws IOException {
				log.info("Loading objects from: {}", resource.getURL());
				
				InputStream inputStream = resource.getInputStream();
				Object object = xStream.fromXML(inputStream);
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
