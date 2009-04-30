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
package com.wwm.db.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;
import java.util.logging.XMLFormatter;
import java.util.zip.GZIPOutputStream;

import com.wwm.util.LogFormatter;


/*
 * Review log:
 *      21-Sep-2005 dnu This breaks from being able to configure the handler.
 *          I think that most of what this is doing would be handled by a
 *          "FlushingFileHandler", derived from FileHandler ...
 *          Suggest we look at refactoring it to allow logging.properties to control
 *          level of handler, rather than code here.
 */

/**
 * Our own LogFactory so that we can deal with logging in our own way if necessary.
 * @author Neale Upstone
 *
 */
public class LogFactory {

	static Handler handler = null;
	static Thread flusher = null;
	static String appName = "unkn";

	private static class Flusher extends Thread {

		Flusher() {
			super("Log Flusher");
			super.setDaemon(true);
		}

		@Override
		public void run() {
			super.run();
			for (;;) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) { e.printStackTrace(); } // FIXME: Document this exception
				handler.flush();
			}
		}
	}

	public synchronized static Logger getLogger( Class<?> clazz ) {

		Logger logger = Logger.getLogger( clazz.getName() );

		if (handler == null) {

			File logRoot = new File(Settings.getInstance().getLogDir());

			if (!logRoot.exists()) {
				if (!logRoot.mkdirs()) {
					System.err.println("FATAL ERROR, Exiting: Unable to make log directories at " +
							logRoot.toString() );
					throw new Error("FATAL ERROR, Exiting: Unable to make log directories at " +
							logRoot.toString() );
					// FIXME: remove this.  left in for the moment while nail down JUnit config
				}
			}

			Date date = new Date();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss_SSS");
			String logfile = Settings.getInstance().getLogDir()
			+ File.separator + formatter.format(date) + "_" + appName;
			try {
				OutputStream os = null;
				Formatter sf = null;
				if (Settings.getInstance().getXmlLogs()) {
					logfile = logfile + ".xml";
					sf = new XMLFormatter();
				} else {
					logfile = logfile + ".txt";
					sf = new LogFormatter();
				}

				if (Settings.getInstance().getCompressLogs()) {
					logfile += ".gz";
					os = new GZIPOutputStream(new FileOutputStream(logfile));
				} else {
					os = new FileOutputStream(logfile);
				}

				handler = new StreamHandler(os, sf);
				flusher = new Flusher();
				flusher.start();
			} catch (Exception e) {
				logger.severe("Failed to open log file " + logfile + ": " + e);
			}
		}

		if (handler != null) {
			logger.addHandler(handler);
		}

		return Logger.getLogger( clazz.getName() );
	}

	public synchronized static void setAppName(String appName) {
		LogFactory.appName = appName;
	}

	public synchronized static void setLevel(Level level) {
		if (handler != null) {
			handler.setLevel(level);
		}
	}

	public synchronized static Logger getLogger(String string, Class<? extends Object> name) {
		setAppName(string);
		return getLogger(name);
	}
}
