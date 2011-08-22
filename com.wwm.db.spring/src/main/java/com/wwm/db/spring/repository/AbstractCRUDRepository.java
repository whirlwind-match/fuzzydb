package com.wwm.db.spring.repository;

import java.io.Serializable;
import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.mapping.model.MappingException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;

import com.wwm.db.DataOperations;
import com.wwm.db.Ref;

public abstract class AbstractCRUDRepository<I, T, ID extends Serializable> implements WhirlwindCrudRepository<T,ID>, InitializingBean {

	protected Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	protected DataOperations persister;
	
	protected final Class<T> type;
	
	protected Field idField;

	
	public AbstractCRUDRepository(Class<T> type) {
		this.type = type;
	}

	public AbstractCRUDRepository(Class<T> type, DataOperations persister) {
		this.type = type;
		this.persister = persister;
	}

	/**
	 * Initialise access to id field
	 */
	public void afterPropertiesSet() throws Exception {
		ReflectionUtils.doWithFields(type, new FieldCallback() {
			public void doWith(Field field) throws IllegalArgumentException,
					IllegalAccessException {
				if (field.isAnnotationPresent(Id.class)) {
					ReflectionUtils.makeAccessible(field);
					idField = field;
				}
			}
		});
		if (idField == null) {
			throw new MappingException(type.getCanonicalName() + " must have an @Id annotated field");
		}
	}


	@Transactional(readOnly=true)
	public Iterable<T> findAll() {
		throw new UnsupportedOperationException("not yet implemented");
	}

	@Transactional(readOnly=true)
	public long count() {
		selectNamespace();
		return persister.count(getInternalType());
	}

	@Transactional
	public final void deleteAll() {
		throw new UnsupportedOperationException("not yet implemented");
	}

	@Transactional(readOnly=true)
	public T findFirst() {
		selectNamespace();
		I internalResult = persister.retrieveFirstOf(getInternalType());
		return internalResult == null ? null : fromInternal(internalResult, persister.getRef(internalResult));
	}

	protected final DataOperations getPersister() {
		return persister;
	}

	/**
	 * Allows namespace to be selected for the type of repository,
	 * which allows the same namespace to be selected for a whole
	 * class hierarchy.
	 */
	abstract protected void selectNamespace();

	abstract protected Class<I> getInternalType();

	/**
	 * Decode the internal representation (e.g. a binary buffer) to the type for this repository
	 * 
	 * @param internal raw object that has been retrieved from database
	 * @param ref 
	 * @return converted type
	 */
	abstract protected T fromInternal(I internal, Ref<I> ref);

	/**
	 * Encode the persisted object to its' internal representation.
	 * 
	 * @param external the object that is being persisted to the database
	 * @return an object suitable for persisting
	 */
	abstract protected I toInternal(T external);


	protected void setId(T entity, Ref<I> ref) {
		try {
			idField.set(entity, ref);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	protected ID getId(T entity) {
		try {
			return (ID) idField.get(entity);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	@Transactional
	@SuppressWarnings("unchecked")
	public Iterable<T> save(Iterable<? extends T> entities) {
	
		for (T entity : entities) {
			save(entity);
		}
		return (Iterable<T>) entities;
	}

	@Transactional
	public void delete(Iterable<? extends T> entities) {
		for (T entity : entities) {
			delete(entity);
		}
	}
}