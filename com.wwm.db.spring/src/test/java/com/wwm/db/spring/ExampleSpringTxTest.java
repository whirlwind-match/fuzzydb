package com.wwm.db.spring;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.wwm.db.DataOperations;
import com.wwm.db.GenericRef;
import com.wwm.db.Store;
import com.wwm.db.Transaction;
import com.wwm.db.userobjects.MutableString;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:/annotation-driven-aspectj-tx-context.xml"})
public class ExampleSpringTxTest {
	
	@Autowired
	private Store store;
	
	@Autowired
	private DataOperations dataOperations;
	
	// one way of doing things.
	@Test
	public void storeShouldBeAutowired(){
	
		Assert.assertNotNull(store);
		Transaction transaction = store.begin();
		// blah
		transaction.commit();
	}
	
	
	@Test
	public void createObjectSucceedInAtTransactionalViaInjectedDataOps(){
		GenericRef<MutableString> ref = insertSomething();
		
		MutableString ms = retrieveSomething(ref);
		
		assertThat(ms.toString(), equalTo("Hello"));
	}


	@Transactional
	private MutableString retrieveSomething(GenericRef<MutableString> ref) {
		MutableString ms = dataOperations.retrieve(ref);
		return ms;
	}


	@Transactional
	private GenericRef<MutableString> insertSomething() {
		GenericRef<MutableString> ref = dataOperations.createGeneric(new MutableString("Hello"));
		return ref;
	}
	

}
