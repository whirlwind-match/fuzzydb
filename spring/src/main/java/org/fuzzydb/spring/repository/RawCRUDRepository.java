package org.fuzzydb.spring.repository;

import java.io.Serializable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.wwm.db.DataOperations;
import com.wwm.db.query.ResultSet;

public class RawCRUDRepository<T,ID extends Serializable & Comparable<ID>> extends AbstractCRUDRepository<T,T,ID> {

	public RawCRUDRepository(Class<T> type) {
		this(type, null);
	}

	public RawCRUDRepository(Class<T> type, DataOperations persister) {
		super(type, persister);
		Assert.isAssignable(Serializable.class, type, "Items being persisted by Raw repositories must be Serializable. ");
	}
	
	@Override
	@Transactional
	public void delete(ID id) {
		persister.delete(findOne(id));
	}

	@Override
	@Transactional
	public void delete(T entity) {
		persister.delete(entity); // TODO: This will be fine for attached entities.. detached will need to use ID field and a new delete by key feature
	}

	@Override
	@Transactional(readOnly=true)
	public boolean exists(ID id) {
		return findOne(id) != null;
	}

	@Override
	@Transactional(readOnly=true)
	public T findOne(ID id) {
		return persister.retrieve(type, idField.getName(), id);
	}

	@Override
	@Transactional(readOnly=true)
	public T findFirst() {
		selectNamespace();
		T internalResult = persister.retrieveFirstOf(getInternalType());
		return internalResult;
	}

	@Override
	@Transactional(readOnly=true)
	public Iterable<T> findAll() {
		selectNamespace();
		final ResultSet<T> all = persister.query(getInternalType(), null, null);
		return all;
	}


	@Override
	@Transactional(readOnly=true)
	public Iterable<T> findAll(final Iterable<ID> ids) {
		throw new UnsupportedOperationException("findAll(Iterable) not available on RawCRUDRepository");
	}

	@Override
	protected Class<T> getInternalType() {
		return type;
	}

	@Override
	@Transactional
	public <S extends T> S save(S entity) {
		persister.save(entity);
		return entity;
	}

	@Override
	protected void selectNamespace() {
		// deliberately empty for Raw
	}
}
