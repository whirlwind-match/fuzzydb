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

import org.fuzzydb.core.LogFactory;
import org.fuzzydb.core.Settings;
import org.fuzzydb.io.packet.layer1.SocketListeningServer;
import org.fuzzydb.util.FileUtils;

import com.wwm.db.internal.server.Database;
import com.wwm.db.internal.server.DatabaseFactory;

/**
 * Run the server.
 */
public class RunServer {

	// TODO: Use args4j to clean this up.
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
			Database db = DatabaseFactory.createDatabase(new SocketListeningServer(new InetSocketAddress(host, port)), true);
			db.startServer();
		} catch (Throwable e) {
			LogFactory.getLogger(RunServer.class).error( "Unhandled exception starting database", e);
		}
	}
}
