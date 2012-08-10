package com.wwm.db;

import static org.junit.Assert.assertNotNull;
import java.io.IOException;
import java.net.MalformedURLException;

import org.fuzzydb.client.Client;
import org.fuzzydb.client.Store;
import org.fuzzydb.client.WWMDBProtocolHander;
import org.junit.Test;


public class EmbeddedClientFactoryTest {

	
	@Test
	public void createStoreAgainstEmbeddedDatabaseShouldReturnNonNullStore() throws IOException {
		
		Client client = EmbeddedClientFactory.getInstance().createClient();
		
		Store store = client.createStore("store@" + System.currentTimeMillis());

		assertNotNull(store);
		
		client.deleteStore(store.getStoreName());
		EmbeddedClientFactory.getInstance().shutdownDatabase(); // TODO: do this via appropriate lifecycle hook for the application
	}
	
	@Test 
	public void openUrlForEmbeddedStore() throws MalformedURLException {
		final String storeName = "store@" + System.currentTimeMillis();
		final String url = "wwmdb:/" + storeName;
		
		Store store = EmbeddedClientFactory.getInstance().openStore(WWMDBProtocolHander.getAsURL(url));
		
		assertNotNull(store);
		
		Client client = EmbeddedClientFactory.getInstance().createClient();
		client.deleteStore(storeName);
		EmbeddedClientFactory.getInstance().shutdownDatabase(); // TODO: do this via appropriate lifecycle hook for the application
		
	}
}
