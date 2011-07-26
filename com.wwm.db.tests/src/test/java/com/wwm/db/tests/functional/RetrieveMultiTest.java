/*
 * Created on 13-Mar-2005
 *
 */
package com.wwm.db.tests.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Map;

import org.junit.Test;

import com.wwm.db.BaseDatabaseTest;
import com.wwm.db.Ref;
import com.wwm.db.Transaction;
import com.wwm.db.exceptions.UnknownObjectException;
import com.wwm.db.userobjects.SampleKeyedObject;
import com.wwm.db.userobjects.SampleUniqueKeyedObject;

/**
 * @author ac
 *
 */
public class RetrieveMultiTest extends BaseDatabaseTest {

	@Test 
	public void testRetrieveMultiNoResults() {
		
		Transaction wt = store.getAuthStore().begin();
		Ref<SampleKeyedObject> ref = wt.create(new SampleKeyedObject(42));
		wt.dispose();
		
        // Now try to retrieve using a Ref that doesn't exist in the database
        // As this is a programming error, we're expecting an exception
		Transaction t = store.begin();
		ArrayList<Ref<SampleKeyedObject>> refs = new ArrayList<Ref<SampleKeyedObject>>();
		refs.add(ref);
        boolean threw = false;
        try {
            t.retrieve(refs);
        } catch (UnknownObjectException e) {
            threw = true;
        }		
        assertTrue( threw );
        t.dispose();
	}

	@Test 
	public void testRetrieveMultiResults() {

		Transaction wt = store.getAuthStore().begin();
		Ref<SampleKeyedObject> ref = wt.create(new SampleKeyedObject(42));
		wt.commit();
		
		Transaction t = store.begin();
		ArrayList<Ref<SampleKeyedObject>> refs = new ArrayList<Ref<SampleKeyedObject>>();
		refs.add(ref);
		Map<Ref<SampleKeyedObject>, SampleKeyedObject> map = t.retrieve(refs);
		
		assertEquals(1, map.size());
		
		SampleKeyedObject so = map.get(ref);
		
		assertEquals(42, so.getMyvalue());
		
		t.dispose();
	}

	@Test 
	public void testRetrieveMultiResults2() {

		Transaction wt = store.getAuthStore().begin();
		Ref<SampleKeyedObject> ref = wt.create(new SampleKeyedObject(42));
		Ref<SampleKeyedObject> ref2 = wt.create(new SampleKeyedObject(94));
		wt.commit();
		
		Transaction t = store.begin();
		ArrayList<Ref<SampleKeyedObject>> refs = new ArrayList<Ref<SampleKeyedObject>>();
		refs.add(ref);
		refs.add(ref2);
		Map<Ref<SampleKeyedObject>, SampleKeyedObject> map = t.retrieve(refs);
		
		assertEquals(2, map.size());
		
		SampleKeyedObject so = map.get(ref);
		SampleKeyedObject so2 = map.get(ref2);
		
		assertEquals(42, so.getMyvalue());
		assertEquals(94, so2.getMyvalue());
		
		t.dispose();
	}

	@Test 
	public void testRetrieveMultiResults3() {

		Transaction wt = store.getAuthStore().begin();
		Ref<?> ref = wt.create(new SampleKeyedObject(42));
		Ref<?> ref2 = wt.create(new SampleKeyedObject(94));
		Ref<?> ref3 = wt.create(new SampleUniqueKeyedObject(1138, 0));
		wt.commit();
		
		Transaction t = store.begin();
		ArrayList<Ref<Object>> refs = new ArrayList<Ref<Object>>();
		refs.add((Ref<Object>) ref);
		refs.add((Ref<Object>) ref2);
		refs.add((Ref<Object>) ref3);
		Map<Ref<Object>, Object> map = t.retrieve(refs);
		
		assertEquals(3, map.size());
		
		SampleKeyedObject so = (SampleKeyedObject)map.get(ref);
		SampleKeyedObject so2 = (SampleKeyedObject)map.get(ref2);
		SampleUniqueKeyedObject so3 = (SampleUniqueKeyedObject)map.get(ref3);
		
		assertEquals(42, so.getMyvalue());
		assertEquals(94, so2.getMyvalue());
		assertEquals(1138, so3.getKey());
		
		t.dispose();
	}
	
}
