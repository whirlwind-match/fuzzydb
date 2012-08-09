package org.fuzzydb.spring.repository;

import static org.junit.Assert.assertNotNull;

import org.fuzzydb.spring.repository.FuzzyRepository;
import org.junit.Test;
import org.springframework.context.support.GenericXmlApplicationContext;

/**
 * Test to manually load context, as we need to ensure that our context loads correctly
 * without the extra post processors that Spring Test adds.
 * 
 * @see <a href="https://jira.springsource.org/browse/SPR-8503">Spring Test bug</a>
 * 
 * @author Neale Upstone
 */
public class RepositoryContextLoadTest {
	
	private static final String[] locations = {"classpath:/fuzzy-repository-context.xml"};

	@Test 
	public void beanDefsLoadSuccessfullyInUnPollutedApplicationContext(){
		GenericXmlApplicationContext context = new GenericXmlApplicationContext();
		context.load(locations);
		context.refresh();
		context.start();
		
		FuzzyRepository<?,?> repo = context.getBean(FuzzyRepository.class);
		assertNotNull(repo);
		
		context.close();
	}


}
