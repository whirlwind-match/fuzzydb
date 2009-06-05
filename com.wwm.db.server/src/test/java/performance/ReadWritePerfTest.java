package performance;


import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

import com.wwm.db.Client;
import com.wwm.db.Factory;
import com.wwm.db.Ref;
import com.wwm.db.Store;
import com.wwm.db.Transaction;
import com.wwm.db.core.exceptions.ArchException;
import com.wwm.db.exceptions.UnknownStoreException;
import com.wwm.db.internal.RefImpl;
import com.wwm.db.internal.server.Database;
import com.wwm.db.userobjects.MutableString;
import com.wwm.io.packet.ClassLoaderInterface;
import com.wwm.io.packet.impl.DummyCli;
import com.wwm.util.MTRandom;

import static org.junit.Assert.fail;

public class ReadWritePerfTest {
	
	static final int serverPort = 5002;
	
	ClassLoaderInterface cli = new DummyCli();
	private final String storeName = "TestStore";

	@Test public void testCreateManyAndRandomAccess() throws IOException, ArchException {
		final int outerLoops = 1;
		final int numberPerLoop = 1000;
		final int numberOfLoops = 50;
		final int numObjects = outerLoops * numberPerLoop * numberOfLoops;
		Ref ref = null; // Last ref retrieved

		for (int count = 0; count < outerLoops; count++) {
			// Make server
			Database database = new Database(new InetSocketAddress(serverPort));
			database.startServer();
			
			// Make client
			Client client = Factory.createClient();
			client.connect(new InetSocketAddress(InetAddress.getLocalHost(), serverPort));

			long testStart = System.currentTimeMillis();
			
			try {
				client.deleteStore(storeName);
			} catch (UnknownStoreException e) { 
				// OKAY FOR EMPTY DB fail("Not sure if this should happen or not"); 
			}
			Store store = client.createStore(storeName);
			
			long start = System.currentTimeMillis();
			
			for (int i = 0; i < numberOfLoops; i++) {
				Transaction t = store.getAuthStore().begin();
				for (int j = 0; j < numberPerLoop; j++) {
					ref = t.create(

					//		new String("Hello World " + i + ' ' + j)
					//		new String("Hello World");
							// approx 500 chars
							new String("Hello World, and all who sail in her and all that asdf asdf asdf asdf asfd asf asdf asdf asf asf Hello World, and all who sail in her and all that asdf asdf asdf asdf asfd asf asdf asdf asf asf Hello World, and all who sail in her and all that asdf asdf asdf asdf asfd asf asdf asdf asf asf Hello World, and all who sail in her and all that asdf asdf asdf asdf asfd asf asdf asdf asf asf Hello World, and all who sail in her and all that asdf asdf asdf asdf asfd asf asdf asdf asf asf asdf asdf as f")
					//		new Integer(42)
					//		al
					);
				}
				t.commit();
			}
	
			long duration = System.currentTimeMillis() - start;
	
			System.out.println(numberPerLoop * numberOfLoops + " Objects created in " + duration + "ms");
			database.close();

			long testDuration = System.currentTimeMillis() - testStart;
			System.out.println("Test duration: " + testDuration + "ms");
			
			try {
				System.gc();
				Thread.sleep(1000);
				System.gc();
			} catch (InterruptedException e) {
				fail(); // shouldn't happen
			}
		}
		
		// if (true) return;  // Normal test ends here

		RefImpl<?> ri = (RefImpl)ref;
		int slice = ri.getSlice();
		int table = ri.getTable();
		
		// SEQUENTIAL READS
		if (false) // comment out this line to enable
		{
			// Make server
			Database database = new Database(new InetSocketAddress(serverPort));

			Client client = Factory.createClient();
			client.connect(new InetSocketAddress(InetAddress.getLocalHost(), serverPort));
			Store store = client.openStore(storeName);
			Transaction t = store.begin();

			int startReads = 1000;
			int numReads = 10000; // numObjects - startReads;
			
			long testStart = System.currentTimeMillis();
			for (int i = startReads; i < startReads + numReads; i++ ){
				Object o = t.retrieve( new RefImpl(slice, table, i)  );
			}

			long testDuration = System.currentTimeMillis() - testStart;

			
			System.out.println("Test duration: " + testDuration + "ms");
			System.out.println("Objects read: " + numReads );
			System.out.println("Average read: " + testDuration/numReads + "ms");
			database.close();
		}

		// RANDOM READS
		{
			// Make server
			Database database = new Database(new InetSocketAddress(serverPort));

			Client client = Factory.createClient();
			client.connect(new InetSocketAddress(InetAddress.getLocalHost(), serverPort));
			Store store = client.openStore(storeName);
			Transaction t = store.begin();

			MTRandom rand = new MTRandom( 1234L );
			int numReads = 10000;
			
			long testStart = System.currentTimeMillis();
			for (int i = 0; i < numReads; i++ ){
				@SuppressWarnings("unused")
				Object o = t.retrieve( new RefImpl(slice, table,rand.nextInt(numObjects)) );
			}

			long testDuration = System.currentTimeMillis() - testStart;

			
			System.out.println("Test duration: " + testDuration + "ms");
			System.out.println("Objects read: " + numReads );
			System.out.println("Average read: " + testDuration/numReads + "ms");
			database.close();
		}
		
		

	}
	

	@Test public void testCreateManyAndReadBack() throws IOException, ArchException {
		final int outerLoops = 1;
		final int numberPerLoop = 1000;
		final int numberOfLoops = 100;


		for (int count = 0; count < outerLoops; count++) {
			// Make server
			Database database = new Database(new InetSocketAddress(serverPort));
			database.startServer();
			
			// Make client
			Client client = Factory.createClient();
			client.connect(new InetSocketAddress(InetAddress.getLocalHost(), serverPort));

			long testStart = System.currentTimeMillis();
			
			try {
				client.deleteStore(storeName);
			} catch (UnknownStoreException e) {
				// Ignore as we might not have created one
			} 
			Store store = client.createStore(storeName);
			
			long start = System.currentTimeMillis();
			
			for (int i = 0; i < numberOfLoops; i++) {
				Transaction t = store.getAuthStore().begin();
				for (int j = 0; j < numberPerLoop; j++) {
					
					t.create(
							new String("Hello World " + (i*numberPerLoop + j))
					);
				}
				t.commit();
			}
	
			System.out.println("Reading back");
			
			for (int i = 0; i < numberOfLoops; i++) {
				Transaction t = store.getAuthStore().begin();
				ArrayList<Ref> al = new ArrayList<Ref>();
				for (int j = 0; j < numberPerLoop; j++) {
					RefImpl ref = new RefImpl(1, 0, i*numberPerLoop + j);
					al.add(ref);
				}
				Map<Ref, Object> map = t.retrieve(al);
				for (int j = 0; j < numberPerLoop; j++) {
					Ref ref = al.get(j);
					String result = (String) map.get(ref);
					Assert.assertEquals("Hello World " + (i*numberPerLoop + j), result);
				}
			}
			
			long duration = System.currentTimeMillis() - start;
	
			System.out.println(numberPerLoop * numberOfLoops + " Objects created, read back and verified in " + duration + "ms");
			database.close();

			long testDuration = System.currentTimeMillis() - testStart;
			System.out.println("Test duration: " + testDuration + "ms");
			
		}

	}
	
	@Test public void testCreateManyAndUpdate() throws IOException, ArchException {
		final int outerLoops = 1;
		final int numberPerLoop = 1000;
		final int numberOfLoops = 100;


		for (int count = 0; count < outerLoops; count++) {
			// Make server
			Database database = new Database(new InetSocketAddress(serverPort));
			database.startServer();
			
			// Make client
			Client client = Factory.createClient();
			client.connect(new InetSocketAddress(InetAddress.getLocalHost(), serverPort));

			long testStart = System.currentTimeMillis();
			
			try {
				client.deleteStore(storeName);
			} catch (UnknownStoreException e) { fail("Not sure if this should happen or not"); }
			Store store = client.createStore(storeName);
			
			long start = System.currentTimeMillis();
			
			for (int i = 0; i < numberOfLoops; i++) {
				Transaction t = store.getAuthStore().begin();
				for (int j = 0; j < numberPerLoop; j++) {
					
					t.create(
							new MutableString("1")
					);
				}
				t.commit();
			}
	
			System.out.println("Updating");
			
			for (int i = 0; i < numberOfLoops; i++) {
				Transaction t = store.getAuthStore().begin();
				ArrayList<Ref> al = new ArrayList<Ref>();
				for (int j = 0; j < numberPerLoop; j++) {
					RefImpl ref = new RefImpl(1, 0, i*numberPerLoop + j);
					al.add(ref);
				}
				Map<Ref, Object> map = t.retrieve(al);
				for (int j = 0; j < numberPerLoop; j++) {
					Ref ref = al.get(j);
					final MutableString result = (MutableString) map.get(ref);
					Assert.assertTrue(result.equals("1"));
					result.value = "2";
					t.update(result);
				}
				t.commit();
			}

			System.out.println("Updating");
			
			MTRandom rnd = new MTRandom();
			ArrayList<Long> ali = new ArrayList<Long>();
			for (long x = 0; x < numberOfLoops*numberPerLoop; x++) {
				ali.add(x);
			}
			
			Collections.shuffle(ali, rnd);
			
			for (int i = 0; i < numberOfLoops; i++) {
				ArrayList<Ref> al = new ArrayList<Ref>();
				for (int j = 0; j < numberPerLoop; j++) {
					Long oid = ali.remove(0);
					RefImpl ref = new RefImpl(1, 0, oid);
					al.add(ref);
				}
				Transaction t = store.getAuthStore().begin();
				Map<Ref, Object> map = t.retrieve(al);
				for (int j = 0; j < numberPerLoop; j++) {
					Ref ref = al.get(j);
					final MutableString result = (MutableString) map.get(ref);
					Assert.assertTrue(result.equals("2"));
					result.value = "3";
					t.update(result);
				}
				t.commit();
			}
			
			System.out.println("Reading back");
			
			for (int i = 0; i < numberOfLoops; i++) {
				Transaction t = store.getAuthStore().begin();
				ArrayList<Ref> al = new ArrayList<Ref>();
				for (int j = 0; j < numberPerLoop; j++) {
					RefImpl ref = new RefImpl(1, 0, i*numberPerLoop + j);
					al.add(ref);
				}
				Map<Ref, Object> map = t.retrieve(al);
				for (int j = 0; j < numberPerLoop; j++) {
					Ref ref = al.get(j);
					MutableString result = (MutableString) map.get(ref);
					Assert.assertTrue(result.equals("3"));
				}
				t.dispose();
			}
			
			long duration = System.currentTimeMillis() - start;
	
			System.out.println(numberPerLoop * numberOfLoops + " Objects created, read back, modified and verified in " + duration + "ms");
			database.close();

			long testDuration = System.currentTimeMillis() - testStart;
			System.out.println("Test duration: " + testDuration + "ms");
			
		}

	}
	
}
