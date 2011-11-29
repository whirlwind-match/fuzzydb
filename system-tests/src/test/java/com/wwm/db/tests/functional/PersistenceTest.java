package com.wwm.db.tests.functional;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.wwm.db.BaseDatabaseTest;
import com.wwm.db.EmbeddedClientFactory;
import com.wwm.db.Ref;
import com.wwm.db.Transaction;
import com.wwm.io.core.ClassLoaderInterface;
import com.wwm.io.core.impl.DummyCli;
import static org.junit.Assert.*;

public class PersistenceTest extends BaseDatabaseTest {
	
	static final int serverPort = 5002;
	
	ClassLoaderInterface cli = new DummyCli();

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
