package org.fuzzydb.spring.repository;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertThat;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.fuzzydb.core.query.Result;
import org.fuzzydb.spring.repository.AttributeMatchQuery;
import org.fuzzydb.spring.repository.FuzzyRepository;
import org.fuzzydb.spring.repository.SubjectMatchQuery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.wwm.model.attributes.Score;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:/fuzzy-repository-context.xml"})
@DirtiesContext
public class FuzzyRepositoryTest {
	
	
	private Set<String> toDelete;
	
	@Autowired
	private FuzzyRepository<FuzzyItem, String> repo;
	
	@Autowired
	private PlatformTransactionManager transactionManager;
	
	
	@Before
	public void initTest(){
		toDelete = new HashSet<String>();
	}
	
	@After
	public void deleteFuzzyItems() {
		for (String ref : toDelete) {
			try {
				repo.delete(ref);
			} catch (EmptyResultDataAccessException e) {
				// ignore this exception as it's just an item we deleted during our test
			}
		}
	}

	@Test 
	public void createObjectSucceedInAtTransactionalViaInjectedDataOps(){
		// the action
		FuzzyItem matt = createMatt();
		String ref = matt.getRef();

		// Check a ref got assigned when we saved item
		assertNotNull(ref);
		
		{
			// Retrieve by ref		
			FuzzyItem result = repo.findOne(ref);
			// And check ref got assigned, and is not same object
			assertNotNull(result.getRef());
			assertNotSame(result, matt);
		}
		
		{
			// Now modify a field
			matt.setAttr("salary", 21000f);
			matt.setSmoke("Non-smoker");
			FuzzyItem updated = updateItem(matt);
			assertEquals("ref should be same for same object", ref, updated.getRef());
		}

		{
			// Retrieve by ref and check newAttribute exists		
			FuzzyItem result = repo.findOne(ref);
			// And check ref got assigned, and is not same object
			assertNotNull(result.getRef());
			assertNotSame(result, matt);
			assertEquals(21000f, result.getAttr("salary"));
			assertEquals("Non-smoker", result.getSmoke());
		}
		
		{
			AttributeMatchQuery<FuzzyItem> query = new SubjectMatchQuery<FuzzyItem>(matt, "similarPeople", 10);
			List<Result<FuzzyItem>> items = doQuery(query);
			assertThat(items.size(), equalTo(1));
			Result<FuzzyItem> firstResult = items.get(0);
			printScores(firstResult);
			assertEquals("Score matching against self should be 1", 1f, firstResult.getScore().total(), 0.0001f );
		}
	}

	@Test 
	public void matchesForAPersonAreInOrder(){
		// the action
		FuzzyItem matt = createMatt();
		String ref = matt.getRef();

		// Check a ref got assigned when we saved item
		assertNotNull(ref);
		
		createMorePeople();
		
		{
			AttributeMatchQuery<FuzzyItem> query = new SubjectMatchQuery<FuzzyItem>(matt, "similarPeople", 10);
			List<Result<FuzzyItem>> items = doQuery(query);

			for (Result<FuzzyItem> result : items) {
				printScores(result);
			}
			assertThat(items.size(), equalTo(4));
		}
	}


	static public void printScores(Result<?> result) {
		Score score = result.getScore();
		System.out.println("Item: " + result.getItem().toString() + ", score = " + score.total());
		Collection<String> scoreEntryNames = score.getScoreEntryNames();
		
		for (String attr : scoreEntryNames) {
			System.out.println("    " + attr + " : fwd=" + score.getForwardsScore(attr) + ", rev=" + score.getReverseScore(attr));
		}
	}

	private List<Result<FuzzyItem>> doQuery(final AttributeMatchQuery<FuzzyItem> query) {
		
		return new TransactionTemplate(transactionManager).execute(new TransactionCallback<List<Result<FuzzyItem>>>() {
			@Override
			public List<Result<FuzzyItem>> doInTransaction(TransactionStatus status) {
				
				Iterator<Result<FuzzyItem>> items = repo.findMatchesFor(query);
				return toList(items);
			}
		});
	}

	public static <T> List<T> toList(Iterator<T> items) {
		assertNotNull(items);
		
		List<T> list = new LinkedList<T>();
		for (Iterator<T> iterator = items; iterator.hasNext();) {
			T item = iterator.next();
			list.add(item);
		}
		return list;
	}


	/**
	 * Always use this when saving so that we've got all the refs we need to delete after the test
	 * @return 
	 */
	private FuzzyItem saveOne(FuzzyItem item) {
		item = repo.save(item);
		toDelete.add(item.getRef());
		return item;
	}

	private FuzzyItem updateItem(FuzzyItem item) {
		item = repo.save(item);
		toDelete.add(item.getRef());
		return item;
	}
	
	private FuzzyItem createMatt() {
		FuzzyItem matt = new FuzzyItem("Matt");
		matt.setAttr("isMale", Boolean.TRUE);
		matt.setAttr("age", 32f);
		matt.setAttr("ageRange", new float[]{25f, 32f, 38f}); // A perfect match for own age
		matt.setAttr("salary", 500000f);
		matt.setSmoke("Cigar-smoker");
		matt.setAttr("newspapers", new String[]{"LA Times", "New York Times"});
		return saveOne(matt);
	}

	private void createMorePeople() {
		
		FuzzyItem angelina = new FuzzyItem("Angelina");
		angelina.setAttr("isMale", Boolean.FALSE);
		angelina.setAttr("age", 35f);
		angelina.setAttr("ageRange", new float[]{30f, 37f, 50f});
		angelina.setAttr("salary", 500000f);
		angelina.setSmoke("Cigarette-smoker");
		angelina.setAttr("newspapers", new String[]{"Guardian", "New York Times"});
		saveOne(angelina);

		FuzzyItem brad = new FuzzyItem("Brad");
		brad.setAttr("isMale", Boolean.TRUE);
		brad.setAttr("age", 37f);
		brad.setAttr("ageRange", new float[]{22f, 30f, 40f});
		brad.setAttr("salary", 550000f);
		saveOne(brad);

		FuzzyItem neale = new FuzzyItem("Neale");
		neale.setAttr("isMale", Boolean.TRUE);
		neale.setAttr("age", 21f); // I wish (sort of)
		neale.setAttr("salary", 25000f);
		neale.setSmoke("Non-smoker");
		saveOne(neale);
	}
}
