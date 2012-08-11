package com.wwm.db;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import org.fuzzydb.attrs.AttributeDefinitionService;
import org.fuzzydb.attrs.internal.SyncedAttrDefinitionMgr;
import org.fuzzydb.client.Client;
import org.fuzzydb.client.Factory;
import org.fuzzydb.client.Store;
import org.fuzzydb.client.exceptions.UnknownStoreException;
import org.fuzzydb.client.internal.StoreImpl;
import org.fuzzydb.core.Settings;
import org.fuzzydb.util.context.JVMAppListener;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import com.wwm.db.EmbeddedClientFactory;
import com.wwm.db.internal.server.Database;
import com.wwm.db.internal.server.DatabaseFactory;
import com.wwm.io.packet.layer1.SocketListeningServer;


public abstract class BaseDatabaseTest {

	protected static boolean useEmbeddedDatabase = true;
	
	private static final String defaultAddress = "127.0.0.1";
//	public static final InetAddress defaultAddress = InetAddress.getLocalHost();
	private static final int serverPort = 5002;

	protected final String storeName = "TestStore";

	private static Database database;
	
	// TODO: make client, store and attrMgr private and use getClient, getStore etc so that they're read only
	protected static Client client; 
	protected Store store;
	
	
	@BeforeClass
	static public void tweakSettings() throws IOException {
		// These affect transaction scavenging.
		Settings.getInstance().setTransactionInactivityTimeoutSecs(30);
		Settings.getInstance().setTransactionTimeToLiveSecs(30);
		// And this is us waiting for server to respond .. which should usually be < 1 sec even with paging.
		Settings.getInstance().setCommandTimeoutSecs(25);
	}

	/** 
	 * This is a workaround to allow {@link BeforeClass} behaviour to occur
	 * after subclass has set useEmbeddedDatabase.
	 * 
	 * Real solution is to switch to TestNG.
	 */
	protected static void connectIfNecessary() throws IOException {
		if (client != null) {
			return;
		}
		if (useEmbeddedDatabase) {
			database = null;
			client = EmbeddedClientFactory.getInstance().createClient();
		}
		else {
			database = startNewDatabase();
			// Make client
			client = Factory.createClient();
			client.connect(new InetSocketAddress(defaultAddress, serverPort));
		}
	}
	
	
	/**
	 * setUp - Establish database with empty store, ready to use.
	 */
	@Before
	public void createStore() throws IOException {
		JVMAppListener.getInstance().preRequest();
		connectIfNecessary();
		store = client.createStore(storeName);
	}
  
	/**
	 * setUp - Establish database with empty store, ready to use.
	 */
	@After
	public void deleteStore() {

		try {
			if (client != null |client.isConnected()) {
				client.deleteStore(storeName);
			}
		} catch (UnknownStoreException e) {
			// ignore
		}
	}

	/**
	 * call this in tests where you're going to use overlapped Txs
	 */
	protected void allowOverlappedTx() {
		((StoreImpl)store).setAllowTxOverlapInThread(true);
	}
	
	
	@AfterClass
	static public void closeDatabase() throws Exception {
		if (client != null) {
			client.disconnect();
			client = null;
		}
		if (useEmbeddedDatabase) {
			EmbeddedClientFactory.getInstance().shutdownDatabase();
		}
		else {
			if (database != null) {
				database.close();
			}
		}
	}
	
	static private Database startNewDatabase() throws IOException {
		// NOTE: We use the single parameter version of InetSocketAddr
		InetSocketAddress anyLocalAddress = new InetSocketAddress(serverPort);
		Database db = DatabaseFactory.createDatabase(new SocketListeningServer(anyLocalAddress), true);
		db.startServer();
		return db;

	}
	
	protected void restartDatabase() throws IOException, UnknownHostException {
		if (useEmbeddedDatabase) {
			EmbeddedClientFactory.getInstance().shutdownDatabase();
			client = EmbeddedClientFactory.getInstance().createClient();
		}
		else {
			database.close();
			// Make server
			database = startNewDatabase();
			
			// Make client
			client = Factory.createClient();
			client.connect(new InetSocketAddress(defaultAddress, serverPort));
		}

		store = client.openStore(storeName);
	}

	protected boolean isDatabaseClosed() {
		if (useEmbeddedDatabase) {
			return EmbeddedClientFactory.getInstance().isDatabaseClosed();
		}
		else {
			return database.isClosed();
		}
	}

	/**
	 * Get latest ADM.  Calling function should not store references on object instances
	 * as this gets refreshed after modifications, so they need to see the mods.
	 * getInstance() actually does a Transaction.refresh(adm)
	 */
	protected AttributeDefinitionService getAttrMgr() {
		return SyncedAttrDefinitionMgr.getInstance(store).getObject();
	}
}