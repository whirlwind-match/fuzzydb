package com.wwm.db.spring.repository;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;

import com.wwm.db.DataOperations;
import com.wwm.db.GenericRef;
import com.wwm.db.exceptions.UnknownObjectException;

/**
 * 
 * @author Neale Upstone
 *
 * @param <I> the internal representation
 * @param <T> the external representation
 * @param <ID> the external ID type
 */
public abstract class AbstractConvertingRepository<I,T,ID extends Serializable> implements CrudRepository<T,ID> {

	@Autowired
	private DataOperations persister;
	
	protected final Class<T> type;
	
	
	public AbstractConvertingRepository(Class<T> type) {
		super();
		this.type = type;
	}

	public T save(T entity) {
		persister.save(toInternal(entity));
		return entity;
	}

	@SuppressWarnings("unchecked")
	public Iterable<T> save(Iterable<? extends T> entities) {
	
		for (T entity : entities) {
			persister.save(toInternal(entity));
		}
		return (Iterable<T>) entities;
	}

	public T findOne(ID id) {
		return fromInternal(persister.retrieve(toInternalId(id)));
	}

	public boolean exists(ID id) {
		try {
			I obj = persister.retrieve(toInternalId(id));
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

	public void delete(ID id) {
		persister.delete(toInternalId(id));
	}

	public void delete(T entity) {
		persister.delete(toInternal(entity));
	}

	public void delete(Iterable<? extends T> entities) {
		throw new UnsupportedOperationException("not yet implemented");
//		persister.delete(entities); // FIXME: Need converting iterator (must be one already surely!)
	}

	public void deleteAll() {
		throw new UnsupportedOperationException("not yet implemented");
	}

	/**
	 * Decode the internal representation (e.g. a binary buffer) to the type for this repository
	 * 
	 * @param internal raw object that has been retrieved from database
	 * @return converted type
	 */
	abstract protected T fromInternal(I internal); 

	/**
	 * Encode the persisted object to its' internal representation
	 * @param external the object that is being persisted to the database
	 * @return an object suitable for persisting
	 */
	abstract protected I toInternal(T external); 

	abstract protected GenericRef<I> toInternalId(ID id);

}