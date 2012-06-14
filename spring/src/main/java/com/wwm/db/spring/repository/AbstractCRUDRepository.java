package com.wwm.db.spring.repository;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Iterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.model.MappingException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;

import com.wwm.db.DataOperations;

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
	@Override
	public void afterPropertiesSet() {
		
		ReflectionUtils.doWithFields(type, new FieldCallback() {
			@Override
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



	@Override
	@Transactional(readOnly=true)
	public long count() {
		selectNamespace();
		return persister.count(getInternalType());
	}

	@Override
	@Transactional
	public final void deleteAll() {
		throw new UnsupportedOperationException("not yet implemented");
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


	protected void setId(T entity, ID ref) {
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

	@Override
	@Transactional
	public <S extends T> Iterable<S> save(Iterable<S> entities) {
	
		for (S entity : entities) {
			save(entity);
		}
		return entities;
	}

	@Override
	@Transactional
	public void delete(Iterable<? extends T> entities) {
		for (T entity : entities) {
			delete(entity);
		}
	}


	@Transactional(readOnly=true)
	@Override
	public Iterable<T> findAll(Sort sort) {
		throw new UnsupportedOperationException("not yet implemented - feel free to fork at github.com/whirlwind-match/whirlwind-db");
	}

	@Transactional(readOnly=true)
	@Override
	public Page<T> findAll(Pageable pageable) {
		// Not scaleable!
		
		Iterable<T> results = findAll();
		Iterator<T> iterator = results.iterator(); 
		
		return PageUtils.getPage(iterator, pageable);
	}
}