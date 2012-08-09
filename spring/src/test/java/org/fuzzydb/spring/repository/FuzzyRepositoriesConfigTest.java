package org.fuzzydb.spring.repository;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.fuzzydb.spring.examples.ExampleCrudRepository;
import org.fuzzydb.spring.examples.ExampleFuzzyRepository;
import org.fuzzydb.spring.repository.FuzzyRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:/fuzzy-repositories-context.xml"})
@DirtiesContext
public class FuzzyRepositoriesConfigTest {
	
	
	@Autowired
	private ExampleCrudRepository repo;
	
	@Autowired 
	private ExampleFuzzyRepository fuzzyRepo;
	
	@Test 
	public void repositoryShouldBeCreatedForInterface() {
		assertTrue( repo instanceof CrudRepository);

		repo.save(new PrimaryKeyedItem("email", "passhash"));

		PrimaryKeyedItem item = repo.findOne("email");
		assertThat(item.getPassHash(), is("passhash"));

		// TODO: assert repo is configured correctly and that proxy behaviour is as expected
	}

	@Test 
	public void fuzzyRepositoryShouldBeCreatedForInterface() {
		assertTrue( fuzzyRepo instanceof FuzzyRepository);

		fuzzyRepo.save(new FuzzyItem("some description"));

		FuzzyItem item = fuzzyRepo.findFirst();
		assertThat(item.getDescription(), is("some description"));
	}
}
