package com.wwm.db.spring.repository;

import java.io.Serializable;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.wwm.db.DataOperations;
import com.wwm.db.Ref;

public class RawCRUDRepository<T,ID extends Serializable & Comparable<ID>> extends AbstractCRUDRepository<T,T,ID> {

	public RawCRUDRepository(Class<T> type) {
		this(type, null);
	}

	public RawCRUDRepository(Class<T> type, DataOperations persister) {
		super(type, persister);
		Assert.isAssignable(Serializable.class, type, "Items being persisted by Raw repositories must be Serializable. ");
	}
	
	@Transactional
	public void delete(ID id) {
		persister.delete(findOne(id));
	}

	@Transactional
	public void delete(T entity) {
		persister.delete(entity); // TODO: This will be fine for attached entities.. detached will need to use ID field and a new delete by key feature
	}

	@Transactional(readOnly=true)
	public boolean exists(ID id) {
		return findOne(id) != null;
	}

	@Transactional(readOnly=true)
	public T findOne(ID id) {
		return persister.retrieve(type, idField.getName(), id);
	}

	@Override
	protected T fromInternal(T internal, Ref<T> ref) {
		return internal;
	}

	@Override
	protected Class<T> getInternalType() {
		return type;
	}

	@Transactional
	public T save(T entity) {
		persister.save(entity);
		return entity;
	}

	@Override
	protected void selectNamespace() {
		// deliberately empty for Raw
	}

	@Override
	protected T toInternal(T external) {
		return external;
	}
}
