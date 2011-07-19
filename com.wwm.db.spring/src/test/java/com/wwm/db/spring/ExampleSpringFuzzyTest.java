package com.wwm.db.spring;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.Serializable;
import java.util.HashMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.wwm.db.DataOperations;
import com.wwm.db.Ref;
import com.wwm.db.annotations.Key;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:/annotation-driven-aspectj-tx-context.xml"})
@DirtiesContext
public class ExampleSpringFuzzyTest {
	
	static public class IndexedMap implements Serializable {  // FIXME : Make implement IWhirlwind blah
		private static final long serialVersionUID = 1L;

		private final @Key(unique=true) String key;
		
		private final HashMap<String, Object> stuff = new HashMap<String, Object>();
		
		public IndexedMap(String key) {
			this.key = key;
		}
		
		public String getKey() {
			return key;
		}
		
		public void put(String key, Object value) {
			stuff.put(key, value);
		}
		
		public Object get(String key) {
			return stuff.get(key);
		}
	}
	
	
	@Autowired
	@Qualifier("org.fuzzydb.DefaultStore")
	private DataOperations dataOperations;
	
	@Test 
	public void createObjectSucceedInAtTransactionalViaInjectedDataOps(){

		// Create an item
		IndexedMap originalItem = new IndexedMap("Hello");
		originalItem.put("height", Integer.valueOf(181));
		Ref<IndexedMap> ref = insertSomething(originalItem);

		// Retrieve by ref
		IndexedMap item = retrieveByRef(ref);
		assertThat(item.getKey(), equalTo("Hello"));
		assertThat(item.get("height"),equalTo((Object)Integer.valueOf(181)));
		
		// Do an index lookup
		IndexedMap item2 = retrieveByKey("Hello");
		assertThat(item2.getKey(), equalTo("Hello"));
		assertThat(item2.get("height"),equalTo((Object)Integer.valueOf(181)));
		
	}

	@Transactional 
	private Ref<IndexedMap> insertSomething(IndexedMap item) {
		return dataOperations.create(item);
	}
	
	@Transactional(readOnly=true)
	private <T> T retrieveByRef(Ref<T> ref) {
		return dataOperations.retrieve(ref);
	}

	@Transactional(readOnly=true)
	private IndexedMap retrieveByKey(String key) {
		return dataOperations.retrieve(IndexedMap.class, "key", key);
	}
}
