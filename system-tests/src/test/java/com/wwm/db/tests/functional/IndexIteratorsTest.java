package com.wwm.db.tests.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.fuzzydb.client.Ref;
import org.fuzzydb.client.Transaction;
import org.fuzzydb.client.userobjects.SampleKeyedObject;
import org.junit.Test;
import com.wwm.db.BaseDatabaseTest;
import com.wwm.db.query.ResultSet;
import com.wwm.expressions.LogicExpr;
import com.wwm.expressions.QueryFactory;

public class IndexIteratorsTest extends BaseDatabaseTest {

	@Test 
	public void testSingleObjectReturned() throws SecurityException, NoSuchFieldException {
		
		Transaction wt = store.getAuthStore().begin();
		Ref<SampleKeyedObject> ref = wt.create(new SampleKeyedObject(42));
		ArrayList<Ref<SampleKeyedObject>> refs = new ArrayList<Ref<SampleKeyedObject>>();
		refs.add(ref);
		wt.commit();
		
		Transaction t = store.begin();
		QueryFactory qf = new QueryFactory(SampleKeyedObject.class);
		LogicExpr le = qf.moreThan(SampleKeyedObject.class.getDeclaredField("myvalue"), 41);
		ResultSet<SampleKeyedObject> results = t.query(SampleKeyedObject.class, le, null);

		int resultCount = 0;
		for (SampleKeyedObject o : results)
		{
			assertEquals(42, o.getMyvalue());
			assertTrue( t.getVersion(o) != 0);
			resultCount++;
		}

		assertEquals(1, resultCount);
		
        t.dispose();
	}

	@Test 
	public void testSingleObjectNotReturned() throws SecurityException, NoSuchFieldException {
		
		Transaction wt = store.getAuthStore().begin();
		Ref<SampleKeyedObject> ref = wt.create(new SampleKeyedObject(42));
		ArrayList<Ref<SampleKeyedObject>> refs = new ArrayList<Ref<SampleKeyedObject>>();
		refs.add(ref);
		wt.commit();
		
		Transaction t = store.begin();
		QueryFactory qf = new QueryFactory(SampleKeyedObject.class);
		LogicExpr le = qf.moreThan(SampleKeyedObject.class.getDeclaredField("myvalue"), 42);
		ResultSet<SampleKeyedObject> results = t.query(SampleKeyedObject.class, le, null);

		int resultCount = 0;
		for (SampleKeyedObject o : results)
		{
			assertEquals(42, o.getMyvalue());
			assertTrue( t.getVersion(o) != 0);
			resultCount++;
		}

		assertEquals(0, resultCount);
		
        t.dispose();
	}

	@Test 
	public void testMultiObjectSome() throws SecurityException, NoSuchFieldException {
		
		Transaction wt = store.getAuthStore().begin();
		wt.create(new SampleKeyedObject(40));
		wt.create(new SampleKeyedObject(41));
		wt.create(new SampleKeyedObject(42));
		wt.create(new SampleKeyedObject(43));
		wt.create(new SampleKeyedObject(44));
		wt.commit();
		
		Transaction t = store.begin();
		QueryFactory qf = new QueryFactory(SampleKeyedObject.class);
		LogicExpr le = qf.moreThan(SampleKeyedObject.class.getDeclaredField("myvalue"), 42);
		ResultSet<SampleKeyedObject> results = t.query(SampleKeyedObject.class, le, null);

		int resultCount = 0;
		for (SampleKeyedObject o : results)
		{
			assertTrue( "results should all be > 42. Got:" + o.getMyvalue() , o.getMyvalue() > 42);
			assertTrue( t.getVersion(o) != 0);
			resultCount++;
		}

		assertEquals(2, resultCount);
		
        t.dispose();
	}

	@Test 
	public void testEmptyDb() throws SecurityException, NoSuchFieldException {
		
		Transaction t = store.begin();
		QueryFactory qf = new QueryFactory(SampleKeyedObject.class);
		LogicExpr le = qf.moreThan(SampleKeyedObject.class.getDeclaredField("myvalue"), 42);
		ResultSet<SampleKeyedObject> results = t.query(SampleKeyedObject.class, le, null);

		int resultCount = 0;
		for (@SuppressWarnings("unused") SampleKeyedObject o : results) {
			fail();
		}

		assertEquals(0, resultCount);
		
        t.dispose();
	}
	
	@Test 
	public void nullIndexExpressionShouldReturnAll() throws SecurityException, NoSuchFieldException {
		
		Transaction wt = store.getAuthStore().begin();
		wt.create(new SampleKeyedObject(40));
		wt.create(new SampleKeyedObject(41));
		wt.create(new SampleKeyedObject(42));
		wt.create(new SampleKeyedObject(43));
		wt.create(new SampleKeyedObject(44));
		wt.commit();
		
		Transaction t = store.begin();
		LogicExpr le = null;
		ResultSet<SampleKeyedObject> results = t.query(SampleKeyedObject.class, le, null);

		int resultCount = 0;
		for (SampleKeyedObject o : results)
		{
			assertTrue( t.getVersion(o) != 0);
			resultCount++;
		}

		assertEquals(5, resultCount);
		
        t.dispose();
	}
}
