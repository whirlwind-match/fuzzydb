package org.fuzzydb.spring.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.wwm.db.DataOperations;
import com.wwm.db.Ref;
import com.wwm.db.exceptions.UnknownObjectException;
import com.wwm.db.query.Result;
import com.wwm.db.query.ResultSet;

/**
 * 
 * @author Neale Upstone
 *
 * @param <I> the internal representation
 * @param <T> the external representation
 * @param <ID> the external ID type
 */
public abstract class AbstractConvertingRepository<I,T,ID extends Serializable> extends AbstractCRUDRepository<I, T, ID> implements WhirlwindSearch<T> {

	
	public AbstractConvertingRepository(Class<T> type) {
		super(type);
	}
	
	public AbstractConvertingRepository(Class<T> type, DataOperations persister) {
		super(type, persister);
	}

	/**
	 * Decode the internal representation (e.g. a binary buffer) to the type for this repository
	 * 
	 * @param internal raw object that has been retrieved from database
	 * @return converted type
	 */
	abstract protected T fromInternal(I internal);

	/**
	 * Encode the persisted object to its' internal representation.
	 * 
	 * @param external the object that is being persisted to the database
	 * @return an object suitable for persisting
	 */
	abstract protected I toInternal(T external);

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
	@Override
	@Transactional
	public <S extends T> S  save(S entity) {
		assertValidTypeForRepository(entity);
		selectNamespace();
		I toWrite = toInternal(entity);
		ID existingRef = getId(entity);
		if (existingRef != null) {
			I merged = merge(toWrite, toInternalId(existingRef)); 
			try {
				persister.update(merged);
				return entity;
			} catch (UnknownObjectException e) {
				log.warn("save() - update of detached entity detected, with no merge support so doing delete/create instead on {}", existingRef);
				persister.delete(existingRef);
			}
		}
		Ref<I> ref = persister.save(toWrite);
		
		setId(entity, toExternalId(ref));
		return entity;
	}

	abstract protected ID toExternalId(Ref<I> ref);

	/**
	 * Should do anything needed to merge an existing back in with
	 * existingRef from the current transaction
	 */
	abstract protected I merge(I toWrite, Ref<I> existingId); 
	
	abstract protected Ref<I> toInternalId(ID id);
	
	@Override
	@Transactional(readOnly=true)
	public T findOne(ID id) {
		selectNamespace();
		Ref<I> ref = toInternalId(id);
		T entity;
		try {
			entity = fromInternal(persister.retrieve(ref));
		} catch (UnknownObjectException e) {
			return null;
		}
		setId(entity, toExternalId(ref));
		return entity;
	}

	@Override
	@Transactional(readOnly=true)
	public T findFirst() {
		selectNamespace();
		I internalResult = persister.retrieveFirstOf(getInternalType());
		return internalResult == null ? null : fromInternal(internalResult);
	}

	@Override
	@Transactional(readOnly=true)
	public boolean exists(ID id) {
		selectNamespace();
		try {
			I obj = persister.retrieve(toInternalId(id));
			return obj != null;
		} catch (UnknownObjectException e){
			return false;
		}
	}

	@Override
	@Transactional
	public void delete(ID id) {
		selectNamespace();
		persister.delete(toInternalId(id));
	}

	@Override
	@Transactional
	public void delete(T entity) {
		selectNamespace();
		persister.delete(toInternal(entity));
	}
	

	@Override
	@Transactional(readOnly=true)
	public Iterable<T> findAll() {
		selectNamespace();
		final ResultSet<I> all = persister.query(getInternalType(), null, null);
		return asExternalIterable(all);
	}


	@Override
	@Transactional(readOnly=true)
	public Iterable<T> findAll(final Iterable<ID> ids) {
		selectNamespace();
		Iterable<Ref<I>> iterable = new Iterable<Ref<I>>(){
			@Override
			public Iterator<Ref<I>> iterator() {
				
				return new ConvertingIterator<ID,Ref<I>>(ids.iterator()) {
					
					@Override
					protected Ref<I> convert(ID internal) {
						return toInternalId(internal);
					}
				};
			}
		};	
		Collection<Ref<I>> refs = new ArrayList<Ref<I>>();
		for (Ref<I> ref : iterable) {
			refs.add(ref);
		}
		Map<Ref<I>, I> retrieve = persister.retrieve(refs);
		return asExternalIterable(retrieve.values());
	}

	
	@Override
	@Transactional(readOnly=true, propagation=Propagation.MANDATORY)
	public Iterator<Result<T>> findMatchesFor(AttributeMatchQuery<T> query) {
		selectNamespace();
		I internal = toInternal(query.getQueryTarget());
		return findMatchesInternal(internal, query.getMatchStyle(), query.getMaxResults());
	}

	@Override
	@Transactional(readOnly=true, propagation=Propagation.MANDATORY)
	public Page<Result<T>> findMatchesFor(AttributeMatchQuery<T> query, Pageable pageable) {
		selectNamespace();
		I internal = toInternal(query.getQueryTarget());
		Iterator<Result<T>> resultIterator = findMatchesInternal(internal, query.getMatchStyle(), query.getMaxResults());
		return PageUtils.getPage(resultIterator, pageable);
		
	}

	protected Iterable<T> asExternalIterable(final Iterable<I> all) {
		return new Iterable<T>(){

			@Override
			public Iterator<T> iterator() {
				return new ConvertingIterator<I,T>(all.iterator()) {
					
					@Override
					protected T convert(I internal) {
						return fromInternal(internal);
					}
				};
			}
		};
	}

	protected Iterator<Result<T>> findMatchesInternal(I internal, String matchStyle, int maxResults) {
		throw new UnsupportedOperationException("Override to provide an implementation");
	}
}
