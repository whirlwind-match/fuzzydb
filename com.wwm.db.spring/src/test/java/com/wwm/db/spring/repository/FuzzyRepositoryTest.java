package com.wwm.db.spring.repository;

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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.wwm.db.GenericRef;
import com.wwm.db.query.Result;
import com.wwm.model.attributes.Score;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:/fuzzy-repository-context.xml"})
@DirtiesContext
public class FuzzyRepositoryTest {
	
	
	private Set<GenericRef<FuzzyItem>> toDelete;
	
	@Autowired
	private FuzzyRepository<FuzzyItem> repo;
	
	
	@Before
	public void initTest(){
		toDelete = new HashSet<GenericRef<FuzzyItem>>();
	}
	
	@After
	public void deleteFuzzyItems() {
		for (GenericRef<FuzzyItem> ref : toDelete) {
			try {
				deleteOne(ref);
			} catch (EmptyResultDataAccessException e) {
				// ignore this exception as it's just an item we deleted during our test
			}
		}
	}

	@Transactional
	private void deleteOne(GenericRef<FuzzyItem> ref) {
		repo.delete(ref);
	}
	
	@Test 
	public void createObjectSucceedInAtTransactionalViaInjectedDataOps(){
		// the action
		FuzzyItem matt = createMatt();
		GenericRef<FuzzyItem> ref = matt.getRef();

		// Check a ref got assigned when we saved item
		assertNotNull(ref);
		
		{
			// Retrieve by ref		
			FuzzyItem result = getItem(ref);
			// And check ref got assigned, and is not same object
			assertNotNull(result.getRef());
			assertNotSame(result, matt);
		}
		
		{
			// Now modify a field
			matt.setAttr("salary", 21000f);
			FuzzyItem updated = updateItem(matt);
			assertEquals("ref should be same for same object", ref, updated.getRef());
		}

		{
			// Retrieve by ref and check newAttribute exists		
			FuzzyItem result = getItem(ref);
			// And check ref got assigned, and is not same object
			assertNotNull(result.getRef());
			assertNotSame(result, matt);
			assertEquals(21000f, result.getAttr("salary"));
		}
		
		{
			AttributeMatchQuery<FuzzyItem> query = new SimpleAttributeMatchQuery<FuzzyItem>(matt, "similarPeople", 10);
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
		GenericRef<FuzzyItem> ref = matt.getRef();

		// Check a ref got assigned when we saved item
		assertNotNull(ref);
		
		createMorePeople();
		
		{
			AttributeMatchQuery<FuzzyItem> query = new SimpleAttributeMatchQuery<FuzzyItem>(matt, "similarPeople", 10);
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
		Collection<String> scorerAttrNames = score.getScorerAttrNames();
		
		for (String attr : scorerAttrNames) {
			System.out.println("    " + attr + " : fwd=" + score.getForwardsScore(attr) + ", rev=" + score.getReverseScore(attr));
		}
	}

	@Transactional(readOnly=true) 
	private List<Result<FuzzyItem>> doQuery(AttributeMatchQuery<FuzzyItem> query) {
		Iterator<Result<FuzzyItem>> items = repo.findMatchesFor(query);
		return toList(items);
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


	@Transactional(readOnly=true) 
	private FuzzyItem getItem(GenericRef<FuzzyItem> ref) {
		return repo.findOne(ref);
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

	@Transactional 
	private FuzzyItem updateItem(FuzzyItem item) {
		item = repo.save(item);
		toDelete.add(item.getRef());
		return item;
	}
	
	@Transactional 
	private FuzzyItem createMatt() {
		FuzzyItem matt = new FuzzyItem("Matt");
		matt.setAttr("isMale", Boolean.TRUE);
		matt.setAttr("age", 32f);
		matt.setAttr("ageRange", new float[]{25f, 32f, 38f}); // A perfect match for own age
		matt.setAttr("salary", 500000f);
		matt.setAttr("smoke", "Cigar-smoker");
		matt.setAttr("newspapers", new String[]{"LA Times", "New York Times"});
		return saveOne(matt);
	}

	@Transactional
	private void createMorePeople() {
		
		FuzzyItem angelina = new FuzzyItem("Angelina");
		angelina.setAttr("isMale", Boolean.FALSE);
		angelina.setAttr("age", 35f);
		angelina.setAttr("ageRange", new float[]{30f, 37f, 50f});
		angelina.setAttr("salary", 500000f);
		angelina.setAttr("smoke", "Cigarette-smoker");
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
		neale.setAttr("smoke", "Non-smoker");
		saveOne(neale);
	}
}
