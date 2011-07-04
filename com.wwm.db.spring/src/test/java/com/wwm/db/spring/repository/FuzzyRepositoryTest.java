package com.wwm.db.spring.repository;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.wwm.db.GenericRef;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:/fuzzy-repository-context.xml"})
public class FuzzyRepositoryTest {
	
	public static class FuzzyItem {
		
		Map<String, Object> attributes = new HashMap<String,Object>();
		
		@Id
		private GenericRef<FuzzyItem> ref;
		
		void populateTestData() {
			attributes.put("isMale", Boolean.FALSE);
			attributes.put("age", 1.1f);
			attributes.put("ageRange", new float[]{25f, 30f, 38f});
		}

		public GenericRef<FuzzyItem> getRef() {
			return ref;
		}
	}

	@Autowired
	private SimpleMappingFuzzyRepository<FuzzyItem> repo;
	
	@Test 
	public void createObjectSucceedInAtTransactionalViaInjectedDataOps(){
		// the action
		FuzzyItem item = saveSomething();
		GenericRef<FuzzyItem> ref = item.getRef();

		// Check a ref got assigned when we saved item
		assertNotNull(ref);
		
		{
			// Retrieve by ref		
			FuzzyItem result = getItem(ref);
			// And check ref got assigned, and is not same object
			assertNotNull(result.getRef());
			assertNotSame(result, item);
		}
		
		{
			// Now modify a field
			item.attributes.put("salary", 21000f);
			FuzzyItem updated = updateItem(item);
			// Will be true when merge supported. assertEquals("ref should be same for same object", ref, updated.getRef());
			FuzzyItem missing = getItem(ref);
			assertNull(missing);
			ref = updated.ref; // TODO: remove when merge supported (https://github.com/whirlwind-match/whirlwind-db/issues/41)
		}

		{
			// Retrieve by ref and check newAttribute exists		
			FuzzyItem result = getItem(ref);
			// And check ref got assigned, and is not same object
			assertNotNull(result.getRef());
			assertNotSame(result, item);
			assertEquals(21000f, result.attributes.get("salary"));
		}
		
		{
			AttributeMatchQuery<FuzzyItem> query = new SimpleAttributeMatchQuery<FuzzyItem>(item, "similarPeople", 10);
			List<FuzzyItem> items = doQuery(query);
			// TODO: Fix this, and get scores too
			assertThat(items.size(), equalTo(1)); 
		}
	}


	@Transactional(readOnly=true) 
	private List<FuzzyItem> doQuery(AttributeMatchQuery<FuzzyItem> query) {
		Iterator<FuzzyItem> items = repo.findMatchesFor(query);
		assertNotNull(items);
		
		List<FuzzyItem> list = new LinkedList<FuzzyItem>();
		for (Iterator<FuzzyItem> iterator = items; iterator.hasNext();) {
			FuzzyItem fuzzyItem = iterator.next();
			list.add(fuzzyItem);
		}
		return list;
	}


	@Transactional 
	private FuzzyItem saveSomething() {
		FuzzyItem external = new FuzzyItem();
		external.populateTestData();
		return repo.save(external);
	}

	@Transactional(readOnly=true) 
	private FuzzyItem getItem(GenericRef<FuzzyItem> ref) {
		return repo.findOne(ref);
	}

	@Transactional 
	private FuzzyItem updateItem(FuzzyItem item) {
		return repo.save(item);
	}
}
