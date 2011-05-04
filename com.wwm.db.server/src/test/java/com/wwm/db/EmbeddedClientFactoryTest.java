package com.wwm.db;

import static org.junit.Assert.assertNotNull;
import java.io.IOException;

import org.junit.Test;

import com.wwm.db.core.exceptions.ArchException;


public class EmbeddedClientFactoryTest {

	
	@Test
	public void createStoreAgainstEmbeddedDatabaseSucceeds() throws ArchException, IOException {
		
		Client client = EmbeddedClientFactory.getInstance().createEmbeddedClient();
		
		Store store = client.createStore("store@" + System.currentTimeMillis());

		assertNotNull(store);
		
		EmbeddedClientFactory.getInstance().shutdownDatabase();
	}
}
