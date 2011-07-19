package com.wwm.db.spring.repository;

import java.io.Serializable;
import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.mapping.model.MappingException;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;

import com.wwm.db.DataOperations;
import com.wwm.db.Ref;
import com.wwm.db.exceptions.UnknownObjectException;

public abstract class AbstractCRUDRepository<I, T, ID extends Serializable> {

	protected Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	protected DataOperations persister;
	
	protected final Class<T> type;
	
	protected Field idField;

	
	public AbstractCRUDRepository(Class<T> type) {
		this.type = type;
	}

	/**
	 * Initialise access to ref
	 */
	public void afterPropertiesSet() throws Exception {
		ReflectionUtils.doWithFields(type, new FieldCallback() {
			public void doWith(Field field) throws IllegalArgumentException,
					IllegalAccessException {
				if (field.isAnnotationPresent(Id.class) && field.getType().isAssignableFrom(Ref.class)) {
					ReflectionUtils.makeAccessible(field);
					idField = field;
				}
			}
		});
		if (idField == null) {
			throw new MappingException(type.getCanonicalName() + " must have an @Id field of type Ref");
		}
	}


	public Iterable<T> findAll() {
		throw new UnsupportedOperationException("not yet implemented");
	}

	public long count() {
		selectNamespace();
		return persister.count(getInternalType());
	}

	public final void deleteAll() {
		throw new UnsupportedOperationException("not yet implemented");
	}

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

	abstract protected Ref<I> toInternalId(ID id);
	

}