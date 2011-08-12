package com.wwm.db.spring.repository;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.thoughtworks.xstream.XStream;
import com.wwm.util.ResourcePatternProcessor;

public class XStreamRepositoryInitializer<T, ID extends Serializable> implements InitializingBean {

	private static final Logger log = LoggerFactory.getLogger(XStreamRepositoryInitializer.class);
	
	private CrudRepository<T, ID> repo;
	
	private String resources;

	private PlatformTransactionManager transactionManager;
	
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

	@Autowired
	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
	
	private void save(final T object) {
		new TransactionTemplate(transactionManager).execute(new TransactionCallback<T>() {
			public T doInTransaction(TransactionStatus status) {
				return repo.save(object);
			}
		});
	}
	
	private void save(final ArrayList<T> objects) {
		new TransactionTemplate(transactionManager).execute(new TransactionCallback<Iterable<T>>() {
			public Iterable<T> doInTransaction(TransactionStatus status) {
				return repo.save(objects);
			}
		});
	}
	
}
