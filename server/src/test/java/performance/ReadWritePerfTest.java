package performance;


import static org.junit.Assert.fail;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import junit.framework.Assert;

import org.fuzzydb.client.Ref;
import org.fuzzydb.client.Transaction;
import org.fuzzydb.client.internal.RefImpl;
import org.fuzzydb.client.userobjects.MutableString;
import org.fuzzydb.server.BaseDatabaseTest;
import org.fuzzydb.server.EmbeddedClientFactory;
import org.fuzzydb.util.MTRandom;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

//@RunWith(SpringJUnit4ClassRunner.class)
//@TestExecutionListeners(listeners=)
public class ReadWritePerfTest extends BaseDatabaseTest {
	
	
	static int randomReadsPerSecond = -1;
	static int createAndSequentialReadPerSecond = -1;
	static int createReadModReadPerSecond = -1; 
	
	
	
//	*** Set persistent is being called after database is started!
//	Perhaps a classrule might help - or a spring test execution listener 
	@BeforeClass
	static public void setPersistent() {
		EmbeddedClientFactory.getInstance().setPersistent(true);
	}
	
	@AfterClass
	static public void setNonPersistent() {
		EmbeddedClientFactory.getInstance().setPersistent(false);
	}
	
	@AfterClass
	static public void showPerf() {
		System.out.println("Random reads per second = " + randomReadsPerSecond);
		System.out.println("Create and sequential reads per second = " + createAndSequentialReadPerSecond);
	}

	@Test(timeout=25000) 
	public void testCreateManyAndRandomAccess() throws IOException {
		final int numberPerCreate = 1;
		final String[] toCommit = new String[numberPerCreate];
		final int numberOfCreates = 1000 / numberPerCreate;
		final int numberOfLoops = 3;
		final int numObjects = numberOfCreates * numberOfLoops * numberPerCreate;
		Ref<String> ref[] = null; // Last ref retrieved
		

		{
			long start = System.currentTimeMillis();
			
			for (int i = 0; i < numberOfLoops; i++) {
				Transaction t = store.getAuthStore().begin();
				for (int j = 0; j < numberOfCreates; j++) {
					for (int k = 0; k < numberPerCreate; k++) {
						toCommit[k] = 
								//		new String("Hello World " + i + ' ' + j);
								// approx 500 chars
								new String("Hello World, and all who sail in her and all that asdf asdf asdf asdf asfd asf asdf asdf asf asf Hello World, and all who sail in her and all that asdf asdf asdf asdf asfd asf asdf asdf asf asf Hello World, and all who sail in her and all that asdf asdf asdf asdf asfd asf asdf asdf asf asf Hello World, and all who sail in her and all that asdf asdf asdf asdf asfd asf asdf asdf asf asf Hello World, and all who sail in her and all that asdf asdf asdf asdf asfd asf asdf asdf asf asf asdf asdf as f");
					}
					ref = t.create(toCommit);
				}
				t.commit();
			}
	
			long duration = System.currentTimeMillis() - start;
			System.out.println(numObjects + " Objects created in " + duration + "ms");
		}
		
		//====================================
		restartDatabase();
		
		try {
			System.gc();
			Thread.sleep(500);
			System.gc();
		} catch (InterruptedException e) {
			fail(); // shouldn't happen
		}

		
		
		// if (true) return;  // Normal test ends here

		RefImpl<?> ri = (RefImpl<?>)ref[0];
		int slice = ri.getSlice();
		int table = ri.getTable();
		
		// SEQUENTIAL READS
		if (false) // comment out this line to enable
		{
			Transaction t = store.begin();

			int startReads = 1000;
			int numReads = 10000; // numObjects - startReads;
			
			long testStart = System.currentTimeMillis();
			for (int i = startReads; i < startReads + numReads; i++ ){
				Object o = t.retrieve( new RefImpl<Object>(slice, table, i)  );
			}

			long testDuration = System.currentTimeMillis() - testStart;

			
			System.out.println("Test duration: " + testDuration + "ms");
			System.out.println("Objects read: " + numReads );
			System.out.println("Average read: " + testDuration/numReads + "ms");
		}

		// RANDOM READS
		{
			Transaction t = store.begin();

			MTRandom rand = new MTRandom( 1234L );
			int numReads = 10000;
			
			long testStart = System.currentTimeMillis();
			for (int i = 0; i < numReads; i++ ){
				@SuppressWarnings("unused")
				Object o = t.retrieve( new RefImpl<Object>(slice, table,rand.nextInt(numObjects)) );
			}

			long duration = System.currentTimeMillis() - testStart;

			
			System.out.println("Test duration: " + duration + "ms");
			System.out.println("Objects read: " + numReads );
			System.out.println("Average read: " + duration/numReads + "ms");

			randomReadsPerSecond = (int) (numReads * 1000 / duration);
		}
	}
	

	@Test(timeout=24000) 
	public void testCreateManyAndSequentialAccess() throws IOException {
		final int numberPerTransaction = 1000;
		final int numberOfLoops = 30;

		{
			long start = System.currentTimeMillis();
			
			for (int i = 0; i < numberOfLoops; i++) {
				Transaction t = store.getAuthStore().begin();
				for (int j = 0; j < numberPerTransaction; j++) {
					
					t.create(
							new String("Hello World " + (i*numberPerTransaction + j))
					);
				}
				t.commit();
			}
	
			System.out.println("Reading back");
			
			for (int i = 0; i < numberOfLoops; i++) {
				Transaction t = store.getAuthStore().begin();
				ArrayList<Ref<Object>> al = new ArrayList<Ref<Object>>();
				for (int j = 0; j < numberPerTransaction; j++) {
					RefImpl<Object> ref = new RefImpl<Object>(1, 0, i*numberPerTransaction + j);
					al.add(ref);
				}
				Map<Ref<Object>, Object> map = t.retrieve(al);
				for (int j = 0; j < numberPerTransaction; j++) {
					Ref<Object> ref = al.get(j);
					String result = (String) map.get(ref);
					Assert.assertEquals("Hello World " + (i*numberPerTransaction + j), result);
				}
				t.dispose();
			}
			
			long duration = System.currentTimeMillis() - start;
	
			int numObjects = numberPerTransaction * numberOfLoops;
			System.out.println(numObjects + " Objects created, read back and verified in " + duration + "ms");
			System.out.println(" Ave = " + duration * 1000 / numObjects + "us");
			createAndSequentialReadPerSecond = (int) (numObjects * 1000 / duration);
		}
	}
	
	@Test(timeout=15000) 
	public void testCreateManyAndUpdate() throws IOException {
		final int numberPerTransaction = 1000;
		final int numberOfLoops = 5;

		{
			long start = System.currentTimeMillis();
			
			for (int i = 0; i < numberOfLoops; i++) {
				Transaction t = store.getAuthStore().begin();
				for (int j = 0; j < numberPerTransaction; j++) {
					
					t.create(
							new MutableString("1")
					);
				}
				t.commit();
			}
	
			System.out.println("Updating in sequential order");
			
			for (int i = 0; i < numberOfLoops; i++) {
				Transaction t = store.getAuthStore().begin();
				ArrayList<Ref<Object>> al = new ArrayList<Ref<Object>>();
				for (int j = 0; j < numberPerTransaction; j++) {
					RefImpl<Object> ref = new RefImpl<Object>(1, 0, i*numberPerTransaction + j);
					al.add(ref);
				}
				Map<Ref<Object>, Object> map = t.retrieve(al);
				for (int j = 0; j < numberPerTransaction; j++) {
					Ref<Object> ref = al.get(j);
					final MutableString result = (MutableString) map.get(ref);
					Assert.assertTrue(result.equals("1"));
					result.value = "2";
					t.update(result);
				}
				t.commit();
			}

			System.out.println("Updating in random order");
			
			// Create big array in random order
			MTRandom rnd = new MTRandom();
			ArrayList<Long> ali = new ArrayList<Long>();
			for (long x = 0; x < numberOfLoops*numberPerTransaction; x++) {
				ali.add(x);
			}
			
			Collections.shuffle(ali, rnd);
			
			// Go through the array in transacted chunks doing retrieve,update on each one
			for (int i = 0; i < numberOfLoops; i++) {
				ArrayList<Ref<Object>> al = new ArrayList<Ref<Object>>();
				for (int j = 0; j < numberPerTransaction; j++) {
					Long oid = ali.remove(0);
					RefImpl<Object> ref = new RefImpl<Object>(1, 0, oid);
					al.add(ref);
				}
				Transaction t = store.getAuthStore().begin();
				Map<Ref<Object>, Object> map = t.retrieve(al);
				for (int j = 0; j < numberPerTransaction; j++) {
					Ref<Object> ref = al.get(j);
					final MutableString result = (MutableString) map.get(ref);
					Assert.assertTrue(result.equals("2"));
					result.value = "3";
					t.update(result);
				}
				t.commit();
			}
			
			System.out.println("Reading back sequentially");
			
			for (int i = 0; i < numberOfLoops; i++) {
				Transaction t = store.getAuthStore().begin();
				ArrayList<Ref<Object>> al = new ArrayList<Ref<Object>>();
				for (int j = 0; j < numberPerTransaction; j++) {
					RefImpl<Object> ref = new RefImpl<Object>(1, 0, i*numberPerTransaction + j);
					al.add(ref);
				}
				Map<Ref<Object>, Object> map = t.retrieve(al);
				for (int j = 0; j < numberPerTransaction; j++) {
					Ref<Object> ref = al.get(j);
					MutableString result = (MutableString) map.get(ref);
					Assert.assertTrue(result.equals("3"));
				}
				t.dispose();
			}
			
			long duration = System.currentTimeMillis() - start;
	
			int numObjects = numberPerTransaction * numberOfLoops;
			System.out.println(numObjects + " Objects created, read back, modified and verified in " + duration + "ms");
			System.out.println(" Ave = " + duration * 1000 / numObjects + "us");
		}
	}
}
