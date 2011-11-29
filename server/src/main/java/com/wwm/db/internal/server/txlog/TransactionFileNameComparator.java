package com.wwm.db.internal.server.txlog;

import java.io.File;
import java.util.Comparator;


public class TransactionFileNameComparator implements Comparator<File> {

	private static final TransactionFileNameComparator instance = new TransactionFileNameComparator();
	
	public static Comparator<? super File> getInstance() {
		return instance;
	}

	
	public int compare(File o1, File o2) {
		long diff = getDbVerFromFilename(o1) - getDbVerFromFilename(o2);
		return Long.signum(diff);
	}


	/**
	 * Extract first digits from between initial 't' and '_'. e.g. 123 from filename "t123_dontcare".
	 * @return long 
	 */
	static long getDbVerFromFilename(File file) {
		String filename = file.getName();
		int index = filename.indexOf('_');
		if (index < 2 || !filename.startsWith("t")) {
			return -1; // some random file we don't want
		}
	
		try {
			return Long.parseLong(filename.substring(1, index));
		} catch (NumberFormatException e) { 
			return -1; // do nothing: we return -1 below.
		}
	}

}
