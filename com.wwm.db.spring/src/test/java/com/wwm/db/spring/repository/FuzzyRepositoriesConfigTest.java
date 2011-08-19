package com.wwm.db.spring.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:/fuzzy-repositories-context.xml"})
@DirtiesContext
public class FuzzyRepositoriesConfigTest {
	
	
//	@Autowired
	private FuzzyRepository<FuzzyItem> repo;
	
	
	
	
	@Test 
	public void repositoryShouldBeCreatedForInterface(){
	}

}
