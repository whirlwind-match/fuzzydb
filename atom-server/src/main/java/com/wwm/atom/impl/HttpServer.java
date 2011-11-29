package com.wwm.atom.impl;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.springframework.context.Lifecycle;

/**
 * Manage a Jetty server instance configured by default to host the service at http://localhost:9090/fuzz/
 * 
 * @author Neale
 */
public class HttpServer implements Lifecycle {

	private static final HttpServer instance = new HttpServer();
	
	private final Server server = new Server(9090);

	public static HttpServer getInstance() {
		return instance;
	}

	
	private HttpServer() {
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");
		context.addServlet(new ServletHolder(new FuzzAbderaServlet()),"/fuzz/*");
		server.setHandler(context);
	}
	
	public boolean isRunning() {
		return server.isRunning();
	}
	
	public void start() {
		try {
			server.start();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public void stop() {
		try {
			server.stop();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void main(String[] args) throws Exception	{
		HttpServer jetty = new HttpServer();
		jetty.start();
		jetty.server.join(); // block, otherwise will exit
	}

}
