package org.fuzzydb.tests.config;

import java.net.MalformedURLException;

import org.fuzzydb.attrs.config.StoreInitializer;
import org.fuzzydb.client.Store;
import org.fuzzydb.server.EmbeddedClientFactory;
import org.junit.Before;
import org.junit.Test;

public class StoreInitializerTest {

	private Store store;

	@Before
	public void init() throws MalformedURLException {
		store = EmbeddedClientFactory.getInstance().openStore("wwmdb:/test");
	}

	@Test
	public void storeShouldLoadDefaultConfigs() throws Exception {
		StoreInitializer storeInitializer = new StoreInitializer(store);
		storeInitializer.afterPropertiesSet();
	}
}
