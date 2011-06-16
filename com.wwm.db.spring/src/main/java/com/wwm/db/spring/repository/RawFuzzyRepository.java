package com.wwm.db.spring.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;

import com.wwm.db.DataOperations;
import com.wwm.db.GenericRef;
import com.wwm.db.exceptions.UnknownObjectException;

/**
 * A Repository implementation that performs no conversion.
 * 
 * This is adequate for objects not requiring any fuzzy matching.  The absence of
 * any conversion therefore requires that the repository is for an embedded instance,
 * or that the Class being persisted is available on the server's classpath.
 * 
 * @author Neale Upstone
 *
 * @param <T>
 */
public class RawFuzzyRepository<T> implements CrudRepository<T, GenericRef<T>> {

	@Autowired
	private DataOperations persister;
	
	private final Class<T> type;

	public RawFuzzyRepository(Class<T> type) {
		this.type = type;
	}
	
	
	public T save(T entity) {
		persister.save(entity);
		return entity;
	}

	public Iterable<T> save(Iterable<? extends T> entities) {

		for (T entity : entities) {
			persister.save(entity);
		}
		return (Iterable<T>) entities;
	}

	public T findOne(GenericRef<T> id) {
		return persister.retrieve(id);
	}

	public boolean exists(GenericRef<T> id) {
		try {
			T obj = persister.retrieve(id);
			return obj != null;
		} catch (UnknownObjectException e){
			return false;
		}
	}

	public Iterable<T> findAll() {
		throw new UnsupportedOperationException("not yet implemented");
	}

	public long count() {
		return persister.count(type);
	}

	public void delete(GenericRef<T> id) {
		persister.delete(id);
	}

	public void delete(T entity) {
		persister.delete(entity);
	}

	public void delete(Iterable<? extends T> entities) {
		persister.delete(entities);
	}

	public void deleteAll() {
		throw new UnsupportedOperationException("not yet implemented");
	}
}
