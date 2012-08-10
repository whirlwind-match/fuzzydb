package org.fuzzydb.spring;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.fuzzydb.client.Ref;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:/annotation-driven-proxy-tx-context.xml"})
@DirtiesContext
public class ExampleSpringFuzzyTest {
	
	
	@Autowired
	private NativeWhirlwindServiceImpl service;
	
	@Test 
	public void createObjectSucceedInAtTransactionalViaInjectedDataOps(){

		// Create an item
		IndexedMap originalItem = new IndexedMap("Hello");
		originalItem.put("height", Integer.valueOf(181));
		Ref<IndexedMap> ref = service.insertSomething(originalItem);

		// Retrieve by ref
		IndexedMap item = service.retrieveByRef(ref);
		assertThat(item.getKey(), equalTo("Hello"));
		assertThat(item.get("height"),equalTo((Object)Integer.valueOf(181)));
		
		// Do an index lookup
		IndexedMap item2 = service.retrieveByKey("Hello");
		assertThat(item2.getKey(), equalTo("Hello"));
		assertThat(item2.get("height"),equalTo((Object)Integer.valueOf(181)));
		
	}
}
