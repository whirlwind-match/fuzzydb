package com.wwm.db.spring.repository;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.wwm.db.GenericRef;
import com.wwm.db.internal.whirlwind.RefAware;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:/fuzzy-repository-context.xml"})
public class FuzzyRepositoryTest {
	
	public static class FuzzyItem implements RefAware<FuzzyItem> {
		
		Map<String, Object> attributes = new HashMap<String,Object>();
		
		@Id // Not doing anything with this yet. Instead using RefAware 
		private GenericRef<FuzzyItem> ref;
		
		void populateTestData() {
			attributes.put("isMale", Boolean.FALSE);
			attributes.put("age", 1.1f);
			attributes.put("ageRange", new float[]{25f, 30f, 38f});
		}

		public void setImmutable() {
			throw new UnsupportedOperationException();
		}

		public void setRef(GenericRef<FuzzyItem> ref) {
			this.ref = ref;
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
		
		// Retreive by ref		
		FuzzyItem result = getItem(ref);
		
		// And check ref got assigned, and is not same object
		assertNotNull(result.getRef());
		assertNotSame(result, item);
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


	
}
