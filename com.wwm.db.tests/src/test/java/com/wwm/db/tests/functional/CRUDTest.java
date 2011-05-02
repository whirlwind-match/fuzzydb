package com.wwm.db.tests.functional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.junit.Test;

import com.wwm.db.Ref;
import com.wwm.db.Transaction;
import com.wwm.db.core.exceptions.ArchException;
import com.wwm.db.exceptions.UnknownObjectException;
import com.wwm.db.exceptions.WriteCollisionException;
import com.wwm.db.userobjects.MutableString;
import com.wwm.db.userobjects.SampleObject;
import com.wwm.io.core.ClassLoaderInterface;
import com.wwm.io.core.impl.DummyCli;
import com.wwm.util.Stopwatch;

import static org.junit.Assert.*;

public class CRUDTest extends BaseDatabaseTest {
	
	static final int serverPort = 5002;
	
	ClassLoaderInterface cli = new DummyCli();

	@Test public void testCreate2ObjectsNamespaced() throws ArchException {
		Ref ref1 = null;
		Ref ref2 = null;
		
		{
			Transaction t = store.getAuthStore().begin();

			String s1 = new String("Hello World 1");
			String s2 = new String("Hello World 2");

			boolean thrown = false;
			try {
				t.getVersion(s1);
			} catch (UnknownObjectException e) {
				thrown = true;
			}
			assertTrue(thrown);
			
			t.setNamespace("ns1");
			ref1 = t.create(s1);
			
			assertEquals(ref1, t.getRef(s1));
			assertEquals(0, t.getVersion(s1));
			
			t.setNamespace("ns2");
			ref2 = t.create(s2);
			assertEquals(ref2, t.getRef(s2));
			assertEquals(0, t.getVersion(s2));

			t.commit();
			
			assertEquals(1, t.getVersion(s1));
			assertEquals(1, t.getVersion(s2));
		}
		
		{
			Transaction t = store.begin();
			Object o = t.retrieve(ref1);
			String s = (String) o;
			assertEquals("Hello World 1", s);
			assertEquals(1, t.getVersion(s));
			o = t.retrieve(ref2);
			s = (String) o;
			assertEquals("Hello World 2", s);
			assertEquals(1, t.getVersion(s));
    		t.dispose();
		}
		
		// Test multi retrieve
		{
			ArrayList<Ref> refs = new ArrayList<Ref>();
			refs.add(ref1);
			refs.add(ref2);

			Transaction t = store.begin();
			Map<Ref, Object> result = t.retrieve(refs);
			
			assertTrue(result.containsKey(ref1));
			assertTrue(result.containsKey(ref2));
			assertEquals("Hello World 1", result.get(ref1));
			assertEquals("Hello World 2", result.get(ref2));
    		t.dispose();
		}
	}
	
	@Test public void testCreateObject() throws ArchException {
		Ref ref = null;
		
		{
			Transaction t = store.getAuthStore().begin();
			ref = t.create(new String("Hello World"));
			t.commit();
		}
		
		{
			Transaction t = store.begin();
			Object o = t.retrieve(ref);
			String s = (String) o;
			assertEquals("Hello World", s);
		}
	}

	@Test public void testCreateRestartObject() throws IOException, ArchException {
		Ref ref = null;		
		{
			// Database already running and connected to store.
			{
				Transaction t = store.getAuthStore().begin();
				ref = t.create(new String("Hello World"));
				t.commit();
			}

//			try {
//				Thread.sleep(2000);
//			} catch (InterruptedException e) {
//				fail();
//			}
			
		}
		
		restartDatabase();
		
		{
			Transaction t = store.begin();
			Object o = t.retrieve(ref);
			String s = (String) o;
			assertEquals("Hello World", s);
		}
	}

	@Test public void testDeleteObject() throws ArchException {
		Ref ref = null;
		
		{
			Transaction t = store.getAuthStore().begin();
			ref = t.create(new String("Hello World"));
			t.commit();
		}
		
		{
			Transaction t = store.begin();
			Object o = t.retrieve(ref);
			String s = (String) o;
			assertEquals("Hello World", s);
			t.dispose();
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
	
	@Test public void testDoubleDeleteObject() throws ArchException {
		Ref ref = null;
		
		{
			Transaction t = store.getAuthStore().begin();
			ref = t.create(new String("Hello World"));
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

	@Test public void testRetrieveFirstOf() throws ArchException {
		Ref ref = null;
		
		{
			Transaction t = store.getAuthStore().begin();
			ref = t.create(new String("Hello World"));
			t.commit();
		}
		
		{
			Transaction t = store.begin();
			String s = t.retrieveFirstOf(String.class);
			assertEquals("Hello World", s);
			assertEquals(ref, t.getRef(s));
		}
	}
	
	@Test public void testRetrieveFirstOfAfterDelete() throws ArchException {
		Ref ref1 = null;
		Ref ref2 = null;
		
		{
			Transaction t = store.getAuthStore().begin();
			ref1 = t.create(new String("Hello World 1"));
			ref2 = t.create(new String("Hello World 2"));
			t.commit();
		}

		{
			Transaction t = store.getAuthStore().begin();
			t.delete(ref1);
			t.commit();
		}
		
		{
			Transaction t = store.begin();
			String s = t.retrieveFirstOf(String.class);
			assertEquals("Hello World 2", s);
			assertEquals(ref2, t.getRef(s));
		}
	}

	@Test public void testRetrieveFirstOfNull() throws ArchException {
		{
			Transaction t = store.begin();
			String s = t.retrieveFirstOf(String.class);
			assertNull(s);
		}
	}

	@Test public void testRetrieveFirstOfNullAfterDelete() throws ArchException {
		Ref ref1 = null;
		
		{
			Transaction t = store.getAuthStore().begin();
			ref1 = t.create(new String("Hello World 1"));
			t.commit();
		}

		{
			Transaction t = store.getAuthStore().begin();
			t.delete(ref1);
			t.commit();
		}
		
		{
			Transaction t = store.begin();
			String s = t.retrieveFirstOf(String.class);
			assertNull(s);
		}
	}
	
	/**
     * Test that when an object is retreived multiple times, that we get
     * different instances retrieved. 
     */
    @Test public void testRetrieveGivesNewInstances() throws ArchException {
    
    	for(int count=0; count < 10; count++) {
    		Transaction wt = store.getAuthStore().begin();	// start write transaction
    		SampleObject newObject = new SampleObject(count);
    		Ref rval = wt.create(newObject);	// add object to DB
    		wt.commit();	// Commit the transaction, it will rollback otherwise
    		
    		Transaction t = store.begin();       // start readonly transaction
    		Object result = t.retrieve(rval);    // get the object using the ref the WT gave us
    		SampleObject so = (SampleObject)result;
    		assertTrue(so.getTest() == count);	 // make sure field value is same
    		assertTrue(t.getRef(so).equals(rval));
    
    		Object result2 = t.retrieve(rval);   // get the object using the ref the WT gave us
    		SampleObject so2 = (SampleObject)result2;
    		assertTrue(so2.getTest() == count);	 // make sure field value is same
    		assertTrue(so != so2);	             // Different instance please!
    		assertTrue(!so.equals(so2));
    		assertTrue(t.getVersion(so) == t.getVersion(so2));	// but refs are the same
    		t.dispose();
    	}
    	//deleteStore();
    }

	@Test public void testRollback() throws ArchException {
    
    	for(int count=0; count < 10; count++) {
    		
    		Ref ref;
    		
    		{	// Write new object
    			Transaction wt = store.getAuthStore().begin();	// start write transaction
    			SampleObject newObject = new SampleObject(count);
    			ref = wt.create(newObject);	// add object to DB
    			wt.commit();	// Commit the transaction, it will rollback otherwise
    		}
    
    		SampleObject so;
    		
    		{	// Get object
    			Transaction t = store.begin();	// start readonly transaction
    			Object result = t.retrieve(ref);	// get the object using the ref the WT gave us
    			so = (SampleObject)result;
    			assertTrue(so.getTest() == count);	// make sure field value is same
        		t.dispose();
    		}
    		
    		{	// Update object
    			Transaction wt2 = store.getAuthStore().begin();
    			so.setTest(100 + count);
    			wt2.update(so);
    			wt2.dispose();
    		}
    		
    		{	// Get object
    			SampleObject updated;
    			Transaction t = store.begin();	// start readonly transaction
    			Object result = t.retrieve(ref);	// get the object using the ref the WT gave us
    			updated = (SampleObject)result;
    			assertTrue(updated.getTest() == count);	// make sure field value is as we originally created it
    			assertTrue(t.getVersion(updated) == 1);	// should be v1
        		t.dispose();
    		}
    	}
    
    }
	
	@Test public void testShutdown() throws ArchException {
		final long timeout = 10000;
		
		assertFalse(isDatabaseClosed());
		
		client.shutdownServer();
		
		long start = System.currentTimeMillis();
		long waiting;
		do {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) { fail(); } 
			
			waiting = System.currentTimeMillis() - start;
		} while (!isDatabaseClosed() && waiting < timeout);
		
		assertTrue(isDatabaseClosed());
	}

	@Test public void testTransactionOverlap() throws ArchException {
		allowOverlappedTx();
		
		Ref ref = null;
		
		// Create it
		{
			Transaction t = store.getAuthStore().begin();
			ref = t.create(new MutableString("Hello World"));
			t.commit();
		}
		
		Transaction oldTransaction = store.getAuthStore().begin();
		oldTransaction.forceStart();
		
		// Update it
		{
			Transaction t = store.getAuthStore().begin();
			MutableString s = (MutableString)t.retrieve(ref);
			assertEquals(new MutableString("Hello World"), s);
			assertEquals(1, t.getVersion(s));
			s.value = "Hello World Updated";
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
			Object o = oldTransaction.retrieve(ref);
			MutableString s = (MutableString) o;
			assertEquals("Hello World", s.value);
			assertEquals(1, store.getVersion(s));
			
		}
		
		// Read it with new transaction
		{
			Object o = newTransaction.retrieve(ref);
			MutableString s = (MutableString) o;
			assertEquals("Hello World Updated", s.value);
			assertEquals(2, store.getVersion(s));
		}
	}

    @Test public void testUpdateDeleted() throws ArchException {
		allowOverlappedTx();

    	for(int count=0; count<10; count++) {
    		
    		Ref ref;
    		
    		{	// Write new object
    			Transaction wt = store.getAuthStore().begin();	// start write transaction
    			SampleObject newobject = new SampleObject(count);
    			ref = wt.create(newobject);	// add object to DB
    			wt.commit();	// Commit the transaction, it will rollback otherwise
    		}
    
    		// Get object
    		SampleObject so2;
    		Transaction told = store.begin();	// start readonly transaction
    		Object result2 = told.retrieve(ref);	// get the object using the ref the WT gave us
    		so2 = (SampleObject)result2;
    		assertTrue(so2 != null);	// make sure object came out
    					
    		{	// delete object
    			Transaction wt = store.getAuthStore().begin();	// start write transaction
    			wt.delete(ref);
    			wt.commit();
    		}
    		
    		{	// Update deleted object
    			so2.setTest(count + 10);
    			Transaction t = store.getAuthStore().begin();	// start write transaction
    			
    			boolean exceptionThrown = false;
    			try {
    				t.update(so2);	// update object that doesn't exist
    				t.commit();		// should never happen
    			} catch (ArchException e) {
    				exceptionThrown = true;
    			}
    			assertTrue( exceptionThrown );
    		}
    		
    		told.dispose();
    		
    	}
    
    }

    @Test public void testUpdateObject() throws ArchException {
		Ref ref = null;
		
		// Create it
		{
			Transaction t = store.getAuthStore().begin();
			ref = t.create(new MutableString("Hello World"));
			t.commit();
		}
		
		// Update it
		{
			Transaction t = store.getAuthStore().begin();
			MutableString s = (MutableString)t.retrieve(ref);
			assertEquals(new MutableString("Hello World"), s);
			assertEquals(1, t.getVersion(s));
			s.value = "Hello World Updated";
			assertEquals(1, t.getVersion(s));
			t.update(s);
			assertEquals(1, t.getVersion(s));
			t.commit();
			assertEquals(2, t.getVersion(s));
		}
		
		// Read it
		{
			Transaction t = store.begin();
			Object o = t.retrieve(ref);
			MutableString s = (MutableString) o;
			assertEquals("Hello World Updated", s.value);
    		t.dispose();
		}
		
		// Test using retrieveFirstOf
		{
			Transaction t = store.begin();
			Object o = t.retrieveFirstOf(MutableString.class);
			MutableString s = (MutableString) o;
			assertEquals("Hello World Updated", s.value);
    		t.dispose();
		}
	}

    @Test public void testUpdateOverlap() throws ArchException {
		allowOverlappedTx();
		Ref ref = null;
		
		// Create it
		{
			Transaction t = store.getAuthStore().begin();
			ref = t.create(new MutableString("Hello World"));
			t.commit();
		}
		
		Transaction oldTransaction = store.getAuthStore().begin();
		oldTransaction.forceStart();
		
		// Update it
		{
			Transaction t = store.getAuthStore().begin();
			MutableString s = (MutableString)t.retrieve(ref);
			assertEquals(new MutableString("Hello World"), s);
			assertEquals(1, t.getVersion(s));
			s.value = "Hello World Updated";
			assertEquals(1, t.getVersion(s));
			t.update(s);
			assertEquals(1, t.getVersion(s));
			t.commit();
			assertEquals(2, t.getVersion(s));
		}

		// Write it with old transaction
		{
			Object o = oldTransaction.retrieve(ref);
			MutableString s = (MutableString) o;
			s.value = "This should collide";
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

    /**
     * Variation of an update test.
     * Test doing an update by:
     * - Create object
     * - Get on a read-only tx
     * - Update it
     * - commit using write tx
     * - Check it got updated 
	 */
    @Test public void testUpdateViaGet() throws ArchException {
    
        for(int count=0; count < 10; count++) {
            
            Ref ref;
            
            {   // Write new object
                Transaction wt = store.getAuthStore().begin();  // start write transaction
                SampleObject newobject = new SampleObject(count);
                ref = wt.create(newobject); // add object to DB
                wt.commit();    // Commit the transaction, it will rollback otherwise
            }
    
            SampleObject so;
            
            {   // Get object
                Transaction t = store.begin();  // start readonly transaction
                Object result = t.retrieve(ref);    // get the object using the ref the WT gave us
                so = (SampleObject)result;
                assertTrue(so.getTest() == count);   // make sure field value is same
        		t.dispose();
           }
            
            {   // Update object
                Transaction wt2 = store.getAuthStore().begin();
                so.setTest(100+count);
                wt2.update(so);
                wt2.commit();   // Commit the transaction, it will rollback otherwise
            }
            
            {   // Get object
                SampleObject updated;
                Transaction t = store.begin();  // start readonly transaction
                Object result = t.retrieve(ref);    // get the object using the ref the WT gave us
                updated = (SampleObject)result;
                assertTrue(updated.getTest() == 100+count);  // make sure field value is as we updated it
                assertTrue(t.getVersion(updated) == 2);  // should be v2
        		t.dispose();
            }
        }
    }

    @Test public void testVersion() throws ArchException {
    
    	Ref ref = null;
    	
    	for(int count=0; count < 10; count++) {
    		{	// Write new object
    			Transaction wt = store.getAuthStore().begin();	// start write transaction
    			SampleObject newobject = new SampleObject(count);
    			ref = wt.create(newobject);	// add object to DB
    			wt.commit();	// Commit the transaction, it will rollback otherwise
    			assertTrue(wt.getVersion(newobject) == 1);
    		}
    
    		SampleObject so;
    		
    		{	// Get object
    			Transaction t = store.begin();	// start readonly transaction
    			Object result = t.retrieve(ref);	// get the object using the ref the WT gave us
    			so = (SampleObject)result;
    			assertTrue(so.getTest() == count);	// make sure field value is same
    			assertTrue(t.getVersion(so) == 1);
        		t.dispose();
    		}
    		
    		{	// Update object
    			Transaction wt2 = store.getAuthStore().begin();
    			so.setTest(100+count);
    			wt2.update(so);
    			wt2.commit();	// Commit the transaction, it will rollback otherwise
    			assertTrue(wt2.getVersion(so) == 2);
    		}
    		
    		{	// Get object
    			SampleObject updated;
    			Transaction t = store.begin();	// start readonly transaction
    			Object result = t.retrieve(ref);	// get the object using the ref the WT gave us
    			updated = (SampleObject)result;
    			assertTrue(updated.getTest() == 100 + count);	// make sure field value is as we updated it
    			assertTrue(t.getVersion(updated) == 2);	// should be v2
        		t.dispose();
    		}
    	}
    	// speed test
    	// AC - this tests a different getVersion method from above and should probably have its own test.
    	// getVersion(Object) looks the object up int he local cache but getVersion(Ref) does a DB fetch
    	Transaction t = store.begin();
    	
    	Stopwatch timer = new Stopwatch();
    	timer.start();
    	for (int i=0; i < 100; i++) {
    		t.getVersion(ref);
    	}
    	timer.stop();
		t.dispose();
    
    }
}
