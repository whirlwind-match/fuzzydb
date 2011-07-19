package com.wwm.db.spring.repository;

import java.io.Serializable;
import java.util.Iterator;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.annotation.Id;

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
public abstract class AbstractConvertingRepository<I,T,ID extends Serializable> extends AbstractCRUDRepository<I, T, ID> implements WhirlwindCrudRepository<T,ID>, InitializingBean, WhirlwindSearch<T> {

	
	public AbstractConvertingRepository(Class<T> type) {
		super(type);
	}

	/**
	 * [Should be on interface javadoc] If the field annotated with {@link Id} is set, then this is an update, and
	 * and update is therefore done, otherwise a fresh instance is created.
	 * 
	 * NOTE: For now, the old object is deleted and it's ref becomes stale.  A new object with new Ref is created.
	 * (i.e. new {@link Id}).
	 * <p>
	 * 
	 * {@inheritDoc}
	 */
	public T save(T entity) {
		selectNamespace();
		I toWrite = toInternal(entity);
		Ref<I> existingRef = getRef(entity);
		if (existingRef != null) {
			I merged = merge(toWrite, existingRef); 
			try {
				persister.update(merged);
				return entity;
			} catch (UnknownObjectException e) {
				log.warn("save() - update of detached entity detected, with no merge support so doing delete/create instead on {}", existingRef);
				persister.delete(existingRef);
			}
		}
		Ref ref = persister.save(toWrite);
		setRef(entity, ref);
		return entity;
	}

	/**
	 * Should do anything needed to merge an existing back in with
	 * existingRef from the current transaction
	 */
	abstract protected I merge(I toWrite, Ref<I> existingRef); 
	
	
	protected void setRef(T entity, Ref ref) {
		try {
			idField.set(entity, ref);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	protected Ref<I> getRef(T entity) {
		try {
			return (Ref<I>) idField.get(entity);
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
		selectNamespace();
		Ref<I> ref = toInternalId(id);
		T entity;
		try {
			entity = fromInternal(persister.retrieve(ref), ref);
		} catch (UnknownObjectException e) {
			return null;
		}
		setRef(entity, ref);
		return entity;
	}

	public boolean exists(ID id) {
		selectNamespace();
		try {
			I obj = persister.retrieve(toInternalId(id));
			return obj != null;
		} catch (UnknownObjectException e){
			return false;
		}
	}

	public void delete(ID id) {
		selectNamespace();
		persister.delete(toInternalId(id));
	}

	public void delete(T entity) {
		selectNamespace();
		persister.delete(toInternal(entity));
	}

	public final void delete(Iterable<? extends T> entities) {
		throw new UnsupportedOperationException("not yet implemented");
		//		persister.delete(entities); // FIXME: Need converting iterator (must be one already surely!)
	}

	public Iterator<Result<T>> findMatchesFor(AttributeMatchQuery<T> query) {
		selectNamespace();
		I internal = toInternal(query.getQueryTarget());
		return findMatchesInternal(internal, query.getMatchStyle(), query.getMaxResults());
	}

	protected Iterator<Result<T>> findMatchesInternal(I internal, String matchStyle, int maxResults) {
		throw new UnsupportedOperationException("Override to provide an implementation");
	}


}
