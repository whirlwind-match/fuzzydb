package com.wwm.db.tests.performance;


import java.io.IOException;
import java.net.InetSocketAddress;

import junit.framework.Assert;

import org.junit.Test;

import com.wwm.db.internal.comms.messages.EchoCmd;
import com.wwm.db.internal.comms.messages.EchoRsp;
import com.wwm.db.internal.server.Database;
import com.wwm.io.core.Authority;
import com.wwm.io.core.ClassLoaderInterface;
import com.wwm.io.core.impl.DummyCli;
import com.wwm.io.core.layer1.ClientConnectionManager;
import com.wwm.io.core.messages.Response;
import com.wwm.io.packet.layer1.ClientConnectionManagerImpl;
import com.wwm.io.packet.layer1.SocketListeningServer;

public class EchoPerfTest {
	protected static int serverPort = 5002;
	
	private final ClassLoaderInterface cli = new DummyCli();
	

	private void doEchoLoops(ClientConnectionManager client, int loops) {
		
		for (int i = 0; i < loops; i++) {
			EchoCmd ec = new EchoCmd(0, i, "HelloWorld");
			Response response = client.execute(Authority.Authoritative, ec);
			
			EchoRsp er = (EchoRsp) response;
			
			Assert.assertEquals(ec.getMessage(), er.getMessage());
			Assert.assertEquals(ec.getCommandId(), er.getCommandId());
			Assert.assertEquals(ec.getStoreId(), er.getStoreId());
		}
	}
	
	@Test(timeout=5000)
	public void testEcho() throws IOException {
		final int loops = 1000;
		// Make server
		Database database = new Database(new SocketListeningServer(new InetSocketAddress(serverPort)));
		database.startServer();
		
		// Make client
		InetSocketAddress isa = new InetSocketAddress("localhost", serverPort);	// connect to main adaptor
		ClientConnectionManagerImpl client = new ClientConnectionManagerImpl(isa, cli);
		
		doEchoLoops(client, 100);
		
		long start = System.currentTimeMillis();
		doEchoLoops(client, loops);
		long duration = System.currentTimeMillis() - start;
		System.out.println(loops + " echo loops took " + duration + "ms");
		
		database.close();
		
	}
}
