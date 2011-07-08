package com.wwm.db.spring.repository;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.mapping.model.MappingException;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;

import com.wwm.db.DataOperations;
import com.wwm.db.GenericRef;
import com.wwm.db.Ref;
import com.wwm.db.exceptions.UnknownObjectException;
import com.wwm.db.query.Result;

/**
 * 
 * @author Neale Upstone
 *
 * @param <I> the internal representation
 * @param <T> the external representation
 * @param <ID> the external ID type
 */
public abstract class AbstractConvertingRepository<I,T,ID extends Serializable> implements WhirlwindCrudRepository<T,ID>, InitializingBean, WhirlwindSearch<T> {

	private Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private DataOperations persister;

	protected final Class<T> type;

	private Field idField;

	public AbstractConvertingRepository(Class<T> type) {
		super();
		this.type = type;
	}

	/**
	 * Initialise access to ref
	 */
	public void afterPropertiesSet() throws Exception {
		ReflectionUtils.doWithFields(type, new FieldCallback() {
			public void doWith(Field field) throws IllegalArgumentException,
					IllegalAccessException {
				if (field.isAnnotationPresent(Id.class) && field.getType().isAssignableFrom(GenericRef.class)) {
					ReflectionUtils.makeAccessible(field);
					idField = field;
				}
			}
		});
		if (idField == null) {
			throw new MappingException(type.getCanonicalName() + " must have an @Id field of type GenericRef");
		}
	}
	
	/**
	 * [Should be on interface javadoc] If the field annotated with {@link Id} is set, then this is an update, and
	 * and update is therefore done, otherwise a fresh instance is created.
	 * 
	 * NOTE: For now, the old object is deleted and it's ref becomes stale.  A new object with new Ref is created.
	 * (i.e. new {@link Id}).
	 */
	public T save(T entity) {
		I toWrite = toInternal(entity);
		GenericRef<T> existingRef = getRef(entity);
		if (existingRef != null) {
			// FIXME: Need detached entity support (see https://github.com/whirlwind-match/whirlwind-db/issues/41)
			log.debug("save() - update detected, with no merge support so doing delete/create instead on {}", existingRef);
			persister.delete(existingRef);
		}
		Ref ref = persister.save(toWrite);
		setRef(entity, ref);
		return entity;
	}

	private void setRef(Object entity, Ref ref) {
		try {
			idField.set(entity, ref);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	private <R> GenericRef<R> getRef(R entity) {
		try {
			return (GenericRef<R>) idField.get(entity);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public Iterable<T> save(Iterable<? extends T> entities) {

		for (T entity : entities) {
			save(entity);
		}
		return (Iterable<T>) entities;
	}

	public T findOne(ID id) {
		GenericRef<I> ref = toInternalId(id);
		T entity;
		try {
			entity = fromInternal(persister.retrieve(ref));
		} catch (UnknownObjectException e) {
			return null;
		}
		setRef(entity, ref);
		return entity;
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

	public final void delete(Iterable<? extends T> entities) {
		throw new UnsupportedOperationException("not yet implemented");
		//		persister.delete(entities); // FIXME: Need converting iterator (must be one already surely!)
	}

	public final void deleteAll() {
		throw new UnsupportedOperationException("not yet implemented");
	}
	
	public T findFirst() {
		return persister.retrieveFirstOf(type);
	}
	
	
	public Iterator<Result<T>> findMatchesFor(AttributeMatchQuery<T> query) {
		I internal = toInternal(query.getQueryTarget());
		return findMatchesInternal(internal, query.getMatchStyle(), query.getMaxResults());
	}

	protected Iterator<Result<T>> findMatchesInternal(I internal, String matchStyle, int maxResults) {
		throw new UnsupportedOperationException("Override to provide an implementation");
	}

	protected final DataOperations getPersister() {
		return persister;
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