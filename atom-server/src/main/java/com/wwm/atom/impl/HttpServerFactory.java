package com.wwm.atom.impl;

public class HttpServerFactory {

	static private HttpServer instance;

	public synchronized static HttpServer getInstance() {
		if (instance == null) {
			try {
				Class.forName("org.eclipse.jetty.server.Server");
			} catch (ClassNotFoundException e) {
				return null;
			}
			instance = new HttpServer();
		}
		return instance;
	}

}
