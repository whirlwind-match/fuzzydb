package org.fuzzydb.tests.functional;

import java.io.IOException;

import org.fuzzydb.client.Ref;
import org.fuzzydb.client.Transaction;
import org.fuzzydb.server.BaseDatabaseTest;
import org.fuzzydb.server.EmbeddedClientFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class PersistenceTest extends BaseDatabaseTest {
	
	@BeforeClass 
	static public void setPersitent() {
		EmbeddedClientFactory.getInstance().setPersistent(true);
	}
	
	// NOTE: Assumes that default is false
	@AfterClass
	static public void setNonPersistent() {
		EmbeddedClientFactory.getInstance().setPersistent(false);
	}
	
	
	@Test public void testCreateRestartObject() throws IOException {
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

}
