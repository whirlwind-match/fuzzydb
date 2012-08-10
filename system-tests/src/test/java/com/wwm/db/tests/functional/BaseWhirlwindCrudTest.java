package com.wwm.db.tests.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.GregorianCalendar;


import org.fuzzydb.attrs.AttrsFactory;
import org.fuzzydb.attrs.simple.FloatValue;
import org.fuzzydb.attrs.userobjects.AugmentedAttributeMap;
import org.fuzzydb.client.Ref;
import org.fuzzydb.client.Transaction;
import org.fuzzydb.client.exceptions.UnknownObjectException;
import org.fuzzydb.client.exceptions.WriteCollisionException;
import org.fuzzydb.client.util.TransactionBlockProcessor;
import org.fuzzydb.client.whirlwind.CardinalAttributeMap;
import org.junit.Ignore;
import org.junit.Test;

import com.wwm.db.query.Result;
import com.wwm.db.query.ResultSet;
import com.wwm.db.whirlwind.SearchSpec;
import com.wwm.db.whirlwind.internal.IAttribute;
import com.wwm.util.BlockProcessor;
import com.wwm.util.MTRandom;

abstract public class BaseWhirlwindCrudTest<T extends AugmentedAttributeMap> extends BaseWWTest {
	

	private static final int NUM_INSERTS = 10000;
	private static final float TEST_VAL = 2.0f;

	private static final int BATCH_SIZE = 100; 

	protected final Class<T> entryClass;

	protected final String scorerConfig;
	
	public BaseWhirlwindCrudTest(Class<T> entryClass, String scorerConfig) {
		this.entryClass = entryClass;
		this.scorerConfig = scorerConfig;
	}

	/**
	 * Create a number of sequential objects, and commit the transaction / transactions
	 */
	private void createSequentialObjects(int numObjects) throws Exception {
		
		BlockProcessor bp = new TransactionBlockProcessor( store, BATCH_SIZE ) {
			@Override
			public void everyTime(Transaction t, int count) {
				t.create(createEntry(count));
			}
		};
		
		bp.process(numObjects);
	}
	
	private void createRandomObjects(int numObjects) {
		BlockProcessor bp = new TransactionBlockProcessor( store, BATCH_SIZE ) {
			MTRandom random = new MTRandom( new GregorianCalendar().getTimeInMillis() );

			@Override
			public void everyTime(Transaction t, int count) {
				T item = createEntry(random.nextFloat() * 1e4f);
				t.create(item);
			}
		};
		
		bp.process(numObjects);
	}

	private void doReadBack(final int expectedNumObjects) {
		{
			// Test that they're in the WW index
			Transaction t = store.begin();
			SearchSpec searchSpec = AttrsFactory.createSearchSpec(entryClass);
			searchSpec.setScorerConfig(scorerConfig);
			CardinalAttributeMap<IAttribute> attrs = createAttributeMap();
			attrs.put(floatId, new FloatValue(floatId, 2000f));
			searchSpec.setAttributes(attrs);
			searchSpec.setTargetNumResults(expectedNumObjects + 2); // Allow for more than we expect
			ResultSet<Result<T>> results = t.query(entryClass, searchSpec);
			int count = 0;
			@SuppressWarnings("unused")
			float scoreTotal = 0f;
			for ( Result<T> result : results ) {
				count++;
				scoreTotal += result.getScore().total();
			}
			assertEquals(expectedNumObjects, count);
		}
	}

	abstract protected CardinalAttributeMap<IAttribute> createAttributeMap();

	
	// FIXME: We get a ClassCastException at the server...
	// AND: There is a 5 minute wait for command to time out, rather than the exception going back to the client
	@Test 
	public void testCreateObject() {
		Ref<T> ref = null;
		
		{
			Transaction t = store.getAuthStore().begin();
			ref = t.create(createEntry(TEST_VAL));
			t.commit();
		}
		
		// Test it's in the database
		{
			Transaction t = store.begin();
			T s = t.retrieve(ref);
			assertEquals(TEST_VAL, s.getFloat(floatId));
		}
		
		// Test that it's in the WW index
		doReadBack(1);
	}

	@Ignore("Need to look into createSequentialObjects()  These break db by creating hugely deep tree")
	@Test public void testCreateManyObjects() throws Exception {
		
		createSequentialObjects(NUM_INSERTS);

		doReadBack(NUM_INSERTS);
	}

	@Ignore("Need to look into createSequentialObjects()  These break db by creating hugely deep tree")
	@Test public void testCreateManyObjectsWithShutdown() throws Exception {

		createSequentialObjects(NUM_INSERTS);

		restartDatabase();
		
		doReadBack(NUM_INSERTS);
	}

	@Test 
	public void testCreateRandomObjects() {
		
		createRandomObjects(NUM_INSERTS);
		
		doReadBack(NUM_INSERTS);
	}
	
	@Test 
	public void testCreateRandomObjectsWithShutdown() throws Exception {

		createRandomObjects(NUM_INSERTS);

		restartDatabase();
		
		doReadBack(NUM_INSERTS);
	}




	@Test 
	public void testUpdateObject() {
		Ref<T> ref = null;
		
		// Create it
		{
			Transaction t = store.getAuthStore().begin();
			ref = t.create(createEntry(TEST_VAL));
			t.commit();
		}
		
		// Update it
		{
			Transaction t = store.getAuthStore().begin();
			T s = t.retrieve(ref);
			assertEquals(TEST_VAL, s.getFloat(floatId));
			assertEquals(1, t.getVersion(s));
			s.setFloat(floatId, 2);
			assertEquals(1, t.getVersion(s));
			t.update(s);
			assertEquals(1, t.getVersion(s));
			t.commit();
			assertEquals(2, t.getVersion(s));
		}
		
		// Read it
		{
			Transaction t = store.begin();
			T s = t.retrieve(ref);
			assertEquals(TEST_VAL, s.getFloat(floatId));
		}
	}


	@Test 
	public void testTransactionOverlap() {
		
		Ref<T> ref = null;
		
		// Create it
		{
			Transaction t = store.getAuthStore().begin();
			ref = t.create(createEntry(TEST_VAL));
			t.commit();
		}
		
		Transaction oldTransaction = store.getAuthStore().begin();
		oldTransaction.forceStart();
		
		// Update it
		{
			Transaction t = store.getAuthStore().begin();
			T s = t.retrieve(ref);
			assertEquals(TEST_VAL, s.getFloat(floatId));
			assertEquals(1, t.getVersion(s));
			s.setFloat(floatId,2);
			assertEquals(1, t.getVersion(s));
			t.update(s);
			assertEquals(1, t.getVersion(s));
			t.commit();
			assertEquals(2, t.getVersion(s));
		}

		Transaction newTransaction = store.getAuthStore().begin();
		newTransaction.forceStart();
		
		// Read it with old transaction
		{
			T s = oldTransaction.retrieve(ref);
			assertEquals(TEST_VAL, s.getFloat(floatId));
			assertEquals(1, store.getVersion(s));
			
		}
		
		// Read it with new transaction
		{
			T s = newTransaction.retrieve(ref);
			assertEquals(TEST_VAL, s.getFloat(floatId));
			assertEquals(2, store.getVersion(s));
		}
	}
	
	@Test 
	public void testDeleteObject() {
		Ref<T> ref = null;
		
		{
			Transaction t = store.getAuthStore().begin();
			ref = t.create(createEntry(TEST_VAL));
			t.commit();
		}
		
		{
			Transaction t = store.begin();
			T s = t.retrieve(ref);
			assertEquals(TEST_VAL, s.getFloat(floatId));
		}

		{
			Transaction t = store.getAuthStore().begin();
			t.delete(ref);
			t.commit();
		}

		boolean threw = false;
		
		try {
			Transaction t = store.begin();
			t.retrieve(ref);
		} catch (UnknownObjectException e) {
			threw = true;
		}
		
		assertTrue(threw);
	}

	@Test 
	public void testDeleteWithReadTransaction() {
		Ref<T> ref = null;
		
		{
			Transaction t = store.getAuthStore().begin();
			ref = t.create(createEntry(TEST_VAL));
			t.commit();
		}
		
		// Start a new transaction so that something should be able to 
		// still find the deleted object in the index
		{
			Transaction t2 = store.begin();
			t2.forceStart();
		}
		
		{
			Transaction t = store.begin();
			T s = t.retrieve(ref);
			assertEquals(TEST_VAL, s.getFloat(floatId));
		}

		{
			Transaction t = store.getAuthStore().begin();
			t.delete(ref);
			t.commit();
		}

		boolean threw = false;
		
		try {
			Transaction t = store.begin();
			t.retrieve(ref);
		} catch (UnknownObjectException e) {
			threw = true;
		}
		
		assertTrue(threw);
	}
	
	

	@Test 
	public void testDoubleDeleteObject() {
		Ref<T> ref = null;
		
		{
			Transaction t = store.getAuthStore().begin();
			ref = t.create(createEntry(TEST_VAL));
			t.commit();
		}
		
		{
			Transaction t = store.getAuthStore().begin();
			t.delete(ref);
			t.commit();
		}

		{
			Transaction t = store.getAuthStore().begin();
			t.delete(ref);
			boolean threw = false;
			
			try {
				t.commit();
			} catch (UnknownObjectException e) {
				threw = true;
			}

			assertTrue(threw);

		}
	}
	
	@Test 
	public void testUpdateOverlap() {
		Ref<T> ref = null;
		
		// Create it
		{
			Transaction t = store.getAuthStore().begin();
			ref = t.create(createEntry(TEST_VAL));
			t.commit();
		}
		
		Transaction oldTransaction = store.getAuthStore().begin();
		oldTransaction.forceStart();
		
		// Update it
		{
			Transaction t = store.getAuthStore().begin();
			T s = t.retrieve(ref);
			assertEquals(TEST_VAL, s.getFloat(floatId));
			assertEquals(1, t.getVersion(s));
			s.setFloat(floatId,2);
			assertEquals(1, t.getVersion(s));
			t.update(s);
			assertEquals(1, t.getVersion(s));
			t.commit();
			assertEquals(2, t.getVersion(s));
		}

		// Write it with old transaction
		{
			T s = oldTransaction.retrieve(ref);
			s.setFloat(floatId,666); // should collide
			oldTransaction.update(s);

			boolean threw = false;
			
			try {
				oldTransaction.commit();
			} catch (WriteCollisionException e) {
				threw = true;
			}

			assertTrue(threw);
			
		}
	}

	abstract protected T createEntry(float value);
}
