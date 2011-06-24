package com.wwm.db.spring.repository;

import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.wwm.db.GenericRef;
import com.wwm.db.internal.whirlwind.RefAware;
import com.wwm.db.spring.repository.SimpleMappingFuzzyRepositoryTest.FuzzyItem;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:/fuzzy-repository-context.xml"})
public class FuzzyRepositoryTest {
	
	public static class FuzzyItem implements RefAware<FuzzyItem> {
		
		Map<String, Object> attributes = new HashMap<String,Object>();
		
		void populateTestData() {
			attributes.put("isMale", Boolean.FALSE);
			attributes.put("age", 1.1f);
			attributes.put("ageRange", new float[]{25f, 30f, 38f});
		}

		public void setImmutable() {
			// TODO Auto-generated method stub
			
		}

		public void setRef(GenericRef<FuzzyItem> ref) {
			// TODO Auto-generated method stub
			
		}

		public GenericRef<FuzzyItem> getRef() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	@Autowired
	private SimpleMappingFuzzyRepository<FuzzyItem> repo;
	
//	@Ignore
	@Test 
	public void createObjectSucceedInAtTransactionalViaInjectedDataOps(){
		// the action
		FuzzyItem item = saveSomething();

		assertNotNull(item.getRef());

	}


	@Transactional 
	private FuzzyItem saveSomething() {
		FuzzyItem external = new FuzzyItem();
		external.populateTestData();
		return repo.save(external);
	}


	
}
