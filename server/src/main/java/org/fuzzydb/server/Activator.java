/******************************************************************************
 * Copyright (c) 2005-2008 Whirlwind Match Limited. All rights reserved.
 *
 * This is open source software; you can use, redistribute and/or modify
 * it under the terms of the Open Software Licence v 3.0 as published by the 
 * Open Source Initiative.
 *
 * You should have received a copy of the Open Software Licence along with this
 * application. if not, contact the Open Source Initiative (www.opensource.org)
 *****************************************************************************/
package org.fuzzydb.server;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.fuzzydb.core.LogFactory;
import org.fuzzydb.core.Settings;
import org.fuzzydb.io.packet.layer1.SocketListeningServer;
import org.fuzzydb.server.internal.server.Database;
import org.fuzzydb.server.internal.server.DatabaseFactory;
import org.fuzzydb.server.services.IndexImplementationsService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;


/**
 * This implements the OSGi {@link BundleActivator} interface, and, for the moment,
 * starts a database
 * TODO: The database should depend on {@link InetAddress}, but instead should allow a variety of CommsStacks
 * to be configured.  e.g. we might use an in-VM one.
 *
 */
public class Activator implements BundleActivator {

	private Database db;
	
	private ServiceRegistration registration;
	
	public void start(BundleContext context) throws Exception {
		Settings config = Settings.getInstance();
		String host = "127.0.0.1";
		int port = config.getListenPort();
		
		try {
			db = DatabaseFactory.createDatabase(new SocketListeningServer(new InetSocketAddress(host, port)), true);
			
			// Also want to register indexManager service, which probably should be in own bundle
			IndexImplementationsService service = db.getIndexImplementationsService();
			registration = context.registerService(IndexImplementationsService.class.getName(), service, null);
			db.startServer();
			
		} catch (Throwable e) {
			LogFactory.getLogger(Activator.class).error("Unhandled exception starting database", e);
		}
	}

	public void stop(BundleContext context) throws Exception {
		db.close();
		registration.unregister();
	}
}
