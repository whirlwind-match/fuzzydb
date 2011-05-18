package com.wwm.atom.impl;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * Manage a Jetty server instance configured by default to host the service at http://localhost:9090/fuzz/
 * 
 * @author Neale
 */
public class JettyServer {

	private final Server server = new Server(9090);

	public JettyServer() {
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");
		context.addServlet(new ServletHolder(new FuzzAbderaServlet()),"/fuzz/*");
		server.setHandler(context);
	}
	

	public void start() throws Exception {
		server.start();
	}
	
	public void stop() throws Exception {
		server.stop();
	}

	public static void main(String[] args) throws Exception	{
		JettyServer jetty = new JettyServer();
		jetty.start();
		jetty.server.join(); // block, otherwise will exit
	}
}
