package com.wwm.io.packet;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.Collection;
import java.util.Iterator;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.wwm.io.core.Message;
import com.wwm.io.core.SourcedMessage;
import com.wwm.io.packet.exceptions.NotListeningException;
import com.wwm.io.packet.impl.DummyCli;
import com.wwm.io.packet.layer1.Server;
import com.wwm.io.packet.layer1.ServerImpl;
import com.wwm.io.packet.layer1.SocketListeningServer;

public class CommsStressTest {
	protected static final String defaultAddress = "127.0.0.1";
	protected static int serverPort = 5001;
	
	private ClassLoaderInterface cli = new DummyCli();
	
	@SuppressWarnings("serial")
	private static class TestMessage extends Message {
		int value;
		@SuppressWarnings("unused") // 
		byte[] bytes = new byte[10];
		
		public TestMessage(int value) {
			super(0,0);
			this.value = value;
		}
	}
	
	@Before
	public void setUp() {
		// Increment server port on each test, as it takes some time to free up connections under windows (240secs)
		serverPort++;
	}
	
	private void doConnectLoops(Server server, int loops) throws IOException, NotListeningException
	{
		for (int loop = 0; loop < loops; loop++) {
			// Make client
			InetSocketAddress isa = new InetSocketAddress(defaultAddress, serverPort);	// connect to main adaptor
			CommsStack client = new TCPStack(SocketChannel.open(isa), cli);
			
			TestMessage tm = new TestMessage(42);
			client.getMessageInterface().send(tm);
			
			Collection<SourcedMessage> messages = server.waitForMessage(1000);
			
			Iterator<SourcedMessage> i = messages.iterator();
			TestMessage rx = (TestMessage)i.next().getMessage();
			
			Assert.assertEquals(tm.value, rx.value);
			
			client.getMessageInterface().close();
		}		
		
	}

	private void doMessageLoops(Server server, int loops) throws IOException, NotListeningException
	{
		// Make client
		InetSocketAddress isa = new InetSocketAddress(defaultAddress, serverPort);	// connect to main adaptor
		CommsStack client = new TCPStack(SocketChannel.open(isa), cli);

		for (int loop = 0; loop < loops; loop++) {
			
			TestMessage tm = new TestMessage(42);
			client.getMessageInterface().send(tm);
			
			Collection<SourcedMessage> messages = server.waitForMessage(1000);
			
			Iterator<SourcedMessage> i = messages.iterator();
			TestMessage rx = (TestMessage)i.next().getMessage();
			
			Assert.assertEquals(tm.value, rx.value);
			
		}		
		client.getMessageInterface().close();
		
	}

	@Test 
	public void testManyMessages() throws IOException, NotListeningException {
		final int loops = 10000;
		
		// Make server
		ServerImpl server = new SocketListeningServer(cli, new InetSocketAddress(serverPort));
		server.start();
		
		doMessageLoops(server, loops);
		
		long start = System.currentTimeMillis();
		doMessageLoops(server, loops);
		long duration = System.currentTimeMillis() - start;
		System.out.println(loops + " messages took " + duration + "ms. Time per message: " + (float)duration/loops + "ms");
		server.close();
	}
	
	@Test 
	public void testManyConnects() throws IOException, NotListeningException {
		// This test will fail with a high number of loops due to the sockets hanging around.
		// By default Win XP seems to only allow about 5000 sockets, and they hang around for 240 secs in the TIMED_WAIT state.
		// See http://support.microsoft.com/kb/314053/ for info on how to change TcpTimedWaitDelay and possibly TcpNumConnections. 
		
		final int loops = 1500;
		
		// Make server
		ServerImpl server = new SocketListeningServer(cli, new InetSocketAddress(serverPort));
		server.start();
		
		doConnectLoops(server, 100); // warm-up
		
		// benchmark
		long start = System.currentTimeMillis();
		doConnectLoops(server, loops);
		long duration = System.currentTimeMillis() - start;
		System.out.println(loops + " connect loops took " + duration + "ms");
		server.close();
	}
}
