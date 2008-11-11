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
package com.wwm.db;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.logging.Level;

import com.wwm.db.core.LogFactory;
import com.wwm.db.core.Settings;
import com.wwm.db.internal.server.Database;
import com.wwm.db.services.IndexImplementationsService;
import com.wwm.util.FileUtils;

/**
 * Run the server.
 */
public class RunServer {

	public static void main(String[] args) {
		Settings config = Settings.getInstance();

		String host = "127.0.0.1";
		if (args.length >= 1) {
			host = args[0];
		}

		int port = config.getListenPort();
		if (args.length >= 2) {
			port = Integer.valueOf(args[1]);
		}
		
		// Delete old database if needed (only for JUnit testing)
		if ( config.getDbCleanOnStart() ) {
			FileUtils.setLogger(LogFactory.getLogger(FileUtils.class));
			boolean deletedTx = FileUtils.deleteDirectory( new File(config.getTxDir()) );
			boolean deletedRepos = FileUtils.deleteDirectory( new File(config.getReposDir()) );
			/*ignore result*/ FileUtils.deleteDirectory( new File(config.getLogDir()) ); // it'll return false as we're writing new log now
			if ((deletedTx && deletedRepos) != true) {
				System.exit(1); // don't start server if delete failed (locked file or something)
			}
		}
		
		try {
			Database db = new Database(new InetSocketAddress(host, port));
			IndexImplementationsService service = new IndexImplementationsService();
			db.setIndexImplsService(service);
			db.startServer();
		} catch (Throwable e) {
			LogFactory.getLogger(RunServer.class).log(Level.SEVERE, "Unhandled exception starting database", e);
		}
	}
}
