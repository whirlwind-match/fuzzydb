package com.wwm.db.spring.repository;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.wwm.db.spring.examples.SimpleCrudRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:/fuzzy-repositories-context.xml"})
@DirtiesContext
public class FuzzyRepositoriesConfigTest {
	
	
	@Autowired
	private SimpleCrudRepository repo;
	
	
	@Test 
	public void repositoryShouldBeCreatedForInterface() {
//		assertThat( repo.getClass(), IsInstanceOf.instanceOf(RawCRUDRepository.class)); // TODO: it's a proxy, so need to  
		assertTrue( repo instanceof CrudRepository);

		repo.save(new PrimaryKeyedItem("email", "passhash"));

		PrimaryKeyedItem item = repo.findOne("email");
		assertThat(item.getPassHash(), is("passhash"));

		// TODO: assert repo is configured correctly and that proxy behaviour is as expected
	}

}
