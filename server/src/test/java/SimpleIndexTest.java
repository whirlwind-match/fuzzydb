

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import org.junit.Ignore;
import org.junit.Test;

import com.wwm.db.BaseDatabaseTest;
import com.wwm.db.Ref;
import com.wwm.db.Transaction;
import com.wwm.db.userobjects.TestIndexClass;
import com.wwm.util.MTRandom;

public class SimpleIndexTest extends BaseDatabaseTest {

	@Test public void testCreateIndexedObject() {
		
		Transaction t = store.getAuthStore().begin();
		Ref ref = t.create(new TestIndexClass(1));
		t.commit();

		t = store.getAuthStore().begin();
		TestIndexClass retrieved = t.retrieve(TestIndexClass.class, "a", 1);
		
		assertEquals(1, retrieved.a);
		assertEquals(ref, t.getRef(retrieved));
		assertEquals(1, t.getVersion(retrieved));
		
		t.dispose();

	}
	
	@Test public void testCreateIndexedObjects() {
		final int count = 1000; 
		
		Transaction t = store.getAuthStore().begin();
		
		for (int i = 0; i < count; i++) {
			t.create(new TestIndexClass(i));
		}
		
		t.commit();

		t = store.getAuthStore().begin();
		
		for (int i = 0; i < count; i++) {
			TestIndexClass retrieved = t.retrieve(TestIndexClass.class, "a", i);
		
			assertEquals(i, retrieved.a);
			assertEquals(1, t.getVersion(retrieved));
		}

//		System.out.println(numberPerLoop * numberOfLoops + " Objects created in " + duration + "ms");
		
	}

	@Test public void testCreateManyIndexedObjectsSequential() {
		final int numberOfLoops = 10; 
		final int numberPerLoop = 1000; 
		
		long start = System.currentTimeMillis();
		
		for (int j = 0; j < numberOfLoops; j++) {
			Transaction t = store.getAuthStore().begin();
			
			for (int i = 0; i < numberPerLoop; i++) {
				t.create(new TestIndexClass(i + numberPerLoop*j));
			}
			
			t.commit();
		}

		long createTime = System.currentTimeMillis() - start;
		System.out.println(numberPerLoop * numberOfLoops + " Indexed Objects created in " + createTime + "ms (Sequential)");

		start = System.currentTimeMillis();
		
		for (int j = 0; j < numberOfLoops; j++) {
			Transaction t = store.getAuthStore().begin();
			
			for (int i = 0; i < numberPerLoop; i++) {
				TestIndexClass retrieved = t.retrieve(TestIndexClass.class, "a", i + numberPerLoop*j);
				
				assertEquals(i + numberPerLoop*j, retrieved.a);
				assertEquals(1, t.getVersion(retrieved));
			}
			
			t.dispose();
		}
		
		long lookupTime = System.currentTimeMillis() - start;
		System.out.println(numberPerLoop * numberOfLoops + " Indexed Objects individually looked up in " + lookupTime + "ms");
	}

	@Test public void testCreateManyIndexedObjectsRandom() {
		final int numberOfLoops = 100; 
		final int numberPerLoop = 1000; 
		
		final int numberOfValues = numberOfLoops*numberPerLoop; 
		ArrayList<Integer> values = new ArrayList<Integer>();
		
		for (int i = 0; i < numberOfValues; i++) {
			values.add(i);
		}
		
		Random rnd = new MTRandom();
		Collections.shuffle(values, rnd);
		
		long start = System.currentTimeMillis();
		
		for (int j = 0; j < numberOfLoops; j++) {
			Transaction t = store.getAuthStore().begin();
			
			for (int i = 0; i < numberPerLoop; i++) {
				t.create(new TestIndexClass(values.get(i + numberPerLoop*j)));
			}
			
			t.commit();
		}

		long createTime = System.currentTimeMillis() - start;
		System.out.println(numberPerLoop * numberOfLoops + " Indexed Objects created in " + createTime + "ms (Random)");

//		start = System.currentTimeMillis();
//		
//		for (int j = 0; j < numberOfLoops; j++) {
//			Transaction t = store.getAuthStore().begin();
//			
//			for (int i = 0; i < numberPerLoop; i++) {
//				TestIndexClass retrieved = t.retrieve(TestIndexClass.class, "a", i + numberPerLoop*j);
//				
//				assertEquals(i + numberPerLoop*j, retrieved.a);
//				assertEquals(1, t.getVersion(retrieved));
//			}
//			
//			t.dispose();
//		}
//		
//		long lookupTime = System.currentTimeMillis() - start;
//		System.out.println(numberPerLoop * numberOfLoops + " Indexed Objects individually looked up in " + lookupTime + "ms");
	}
	
	@Ignore
	@Test public void testDeleteManyObjects() throws Exception {
		fail(); // (NU->NU/AC) Need to write this test.  I've put this placeholder in cos Db1 crashes when deleting a number of objects from SimpleIndex (see ArchiveStats.java in Applications)
		/*
		 Suggest:
		 - createRandom (or sequential.. it might be because of sequential that it failed on Rbt)
		= new tx
		 - query to get iterator
		 - delete objects, and use TransactionBatchProcessor to do them in appropriate sized batches
		 */
	}
}
