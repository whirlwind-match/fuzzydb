package org.fuzzydb.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

import com.wwm.db.DataOperations;
import com.wwm.db.Ref;

public class NativeWhirlwindServiceImpl {
	
	@Autowired
	@Qualifier("org.fuzzydb.DefaultStore")
	private DataOperations dataOperations;

	@Transactional 
	public Ref<IndexedMap> insertSomething(IndexedMap item) {
		return dataOperations.create(item);
	}
	
	@Transactional(readOnly=true)
	public <T> T retrieveByRef(Ref<T> ref) {
		return dataOperations.retrieve(ref);
	}

	@Transactional(readOnly=true)
	public IndexedMap retrieveByKey(String key) {
		return dataOperations.retrieve(IndexedMap.class, "key", key);
	}

}
