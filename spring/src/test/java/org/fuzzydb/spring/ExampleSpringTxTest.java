package org.fuzzydb.spring;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import com.wwm.db.Ref;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:/annotation-driven-proxy-tx-context.xml"})
@DirtiesContext
public class ExampleSpringTxTest {
	
	@Autowired
	private NativeWhirlwindServiceImpl service;
	
	@Test 
	public void createObjectSucceedInAtTransactionalViaInjectedDataOps(){

		// Create several items
		{
			IndexedMap item = new IndexedMap("Short");
			item.put("height", Integer.valueOf(141));
			service.insertSomething(item);
		}
		
		IndexedMap originalItem = new IndexedMap("Tall");
		originalItem.put("height", Integer.valueOf(181));
		Ref<IndexedMap> ref = service.insertSomething(originalItem);

		{
			IndexedMap item = new IndexedMap("Baby");
			item.put("height", Integer.valueOf(30));
			service.insertSomething(item);
		}

		// Retrieve by ref
		IndexedMap item = service.retrieveByRef(ref);
		assertThat(item.getKey(), equalTo("Tall"));
		assertThat(item.get("height"),equalTo((Object)Integer.valueOf(181)));
		
		// Do an index lookup
		IndexedMap item2 = service.retrieveByKey("Tall");
		assertThat(item2.getKey(), equalTo("Tall"));
		assertThat(item2.get("height"),equalTo((Object)Integer.valueOf(181)));
		
	}

}
