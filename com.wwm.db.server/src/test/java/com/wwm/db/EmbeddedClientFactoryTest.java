package com.wwm.db;

import static org.junit.Assert.assertNotNull;
import java.io.IOException;

import org.junit.Test;


public class EmbeddedClientFactoryTest {

	
	@Test
	public void createStoreAgainstEmbeddedDatabaseSucceeds() throws IOException {
		
		Client client = EmbeddedClientFactory.getInstance().createEmbeddedClient();
		
		Store store = client.createStore("store@" + System.currentTimeMillis());

		assertNotNull(store);
		
		EmbeddedClientFactory.getInstance().shutdownDatabase();
	}
}
