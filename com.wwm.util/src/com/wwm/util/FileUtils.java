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
package com.wwm.util;

import java.io.File;
import java.util.logging.Logger;

public class FileUtils {

	static private Logger log;
	
	public static void setLogger(Logger log) {
		FileUtils.log = log;
	}
	
	static public boolean deleteDirectory(File path) {
		boolean deleted = deleteDirectoryInternal(path);
		log.info( deleted ?
				"Delete succeeded for path: " + path :
				"Delete failed for path: " + path
				);
		return deleted;
	}

	// Internal version that does the deleting recursively
	static private boolean deleteDirectoryInternal(File path) {
		if (!path.exists()) {
			return true;
		}

		File[] files = path.listFiles();
		boolean succeeded = true;
		for (File file : files) {
			if (file.isDirectory()) {
				succeeded &= deleteDirectoryInternal(file);
			} else {
				succeeded &= file.delete();
			}
		}
		return succeeded && path.delete(); // Deliberately won't call path.delete() if !succeeded (it'll fail)
	}
}
