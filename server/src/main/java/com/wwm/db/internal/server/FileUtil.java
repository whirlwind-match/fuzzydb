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
package com.wwm.db.internal.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Date;

import org.fuzzydb.core.LogFactory;
import org.slf4j.Logger;


public class FileUtil {

	static private final Logger log = LogFactory.getLogger(FileUtil.class);

	private static String legalChars = "_0123456789abcdefghijklmnopqrstuvwzyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static int maxLength = 64;
	private static int preserveRight = 32;
	
	
	public static File removeBestCandidate(Collection<File> candidates) {
		int bestVersion = -1;
		File bestFile = null;
		for (File file : candidates) {
				if (!file.isDirectory()) {
					int version = getVersion( file );
					if (version > bestVersion) {
						bestVersion = version;
						bestFile = file;
					}
				}
		}
		if (bestFile != null) {
			candidates.remove(bestFile);
		}
		return bestFile;
	}

	/**
	 * Extract the version from the file.
	 * The version is the number after the first character, and before the '_'
	 * @param file
	 * @return version. -1 if version doesn't exist (e.g. incorrect format)
	 */
	private static int getVersion(File file) {
		String filename = file.getName();
		int index = filename.indexOf('_');
		if (index > 0) {
			try {
				int version = Integer.parseInt(filename.substring(1, index));
				return version;
			} catch (NumberFormatException e) { 
				log.error("Unexpected error:" + e.getMessage(), e );
			}
		}
		return -1;
	}

	/**
	 * Serialise the supplied object to the given directory, with a standardised filename built using
	 * prefix and version.
	 */
	public static void writeVersionedObject(Object o, String dir, String prefix, long version)
			throws IOException {
		// form the name
		String date = new Date().toString();
		date = date.replace(' ', '_');	// remove spaces
		date = date.replace(':', '-');	// win32 doesn't like ':'s!
		String fileName = dir + File.separator + prefix + version + "_" + date;
	
		File file = new File (fileName);
		File parent = file.getParentFile();
		parent.mkdirs(); // ignore result as it'll vary depending on if they already exist
		FileOutputStream fos = new FileOutputStream(file);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
	
		oos.writeObject(o);
		oos.flush();
		oos.close();
		fos.close();
	}

	/**
	 * Returns an OS-portable, unique file (or directory) name, within the specified
	 * directory.  If another file already exists, an integer value is appended,
	 * which is incremented until a non-clashing name is established.
	 * e.g. if "myDir" exists, then "myDir1" is tried, followed by "myDir2" etc.
	 * @param dir - directory within which a file or dir is required
	 * @param name - name of the file or dir wanted
	 * @return a variant of the supplied name, which avoids clashing with existing files/dirs.
	 */
	public static String makeUniqueDiskName(File dir, String name) {
		log.debug("Making unique disk name for: dir {}, name: {}", dir.getAbsolutePath(), name);
		name = mangle(name);
		File file = new File(dir, name);
		if (!file.exists()) return name;
		int suffix = 1;
		for (;;) {
			String name2 = name + suffix;
			if (!new File(dir, name2).exists()) return name2;
			suffix++;
		}
	}
	
	/**
	 * Given a string of any length, containing any characters, mangle() returns a string
	 * that can be used as a file name on all operating systems we support.<br>
	 * e.g. We give it a Store name, and we get a filename that could be used.
	 * @param name
	 * @return filename
	 */
	public static String mangle(String name) {
		if (name.length() > maxLength) {
			name = name.substring(0, maxLength-preserveRight) + "_" + name.substring(name.length()-preserveRight);	
		}
		
		String result = "";
		for (int i = 0; i < name.length(); i++) {
			char c = name.charAt(i);
			if (legalChars.indexOf(c) == -1)
			{
				result += "_";
			} else {
				result += c;
			}
		}
		return result;
	}

	public static boolean delTree(File file) {
		boolean success = true;
		if (!file.exists()) return true;
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			if (files != null) {
				for (File fileOrDir : files) {
					success &= delTree(fileOrDir);
				}
			}
		}
		success &= file.delete();
		return success;
	}
}
