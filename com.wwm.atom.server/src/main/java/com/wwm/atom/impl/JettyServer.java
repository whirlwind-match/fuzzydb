package com.wwm.atom.impl;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class JettyServer {
    public static void main(String[] args) throws Exception
    {
        Server server = new Server(9090);
 
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);
 
        context.addServlet(new ServletHolder(new FuzzAbderaServlet()),"/fuzz/*");
 
        server.start();
        server.join();
    }

}
