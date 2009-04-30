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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class FileUtils {

	static private Logger log;
	
	/**
	 * WARNING: Currently throws Error (should be IOException
	 * @param fileName
	 * @return
	 * @throws java.lang.Error
	 */
	static public Object readObjectFromGZip(String fileName) {
		// FIXME: Do finally { blah.close().. etc }
		File file = new File(fileName);
		
		FileInputStream fis;
		try {
			fis = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			System.out.println("File missing: " + file);
			throw new Error(e);
		}
		GZIPInputStream gzis;
		try {
			gzis = new GZIPInputStream(fis);
		} catch (IOException e) {
			System.out.println("Error reading from " + file);
			System.out.println("Error opening GZIP input stream: " + e);
			throw new Error(e);
		}
		ObjectInputStream ois;
		try {
			ois = new ObjectInputStream(gzis);
		} catch (IOException e) {
			System.out.println("Error reading from " + file);
			System.out.println("Error opening ObjectInputStream: " + e);
			throw new Error(e);
		}
		
		Object obj;
		try {
			obj = ois.readObject();
		} catch (IOException e) {
			System.out.println("Error reading from " + file + ": " + e);
			throw new Error(e);
		} catch (ClassNotFoundException e) {
			System.out.println("Internal Error reading from " + file + ": " + e);
			throw new Error(e);
		} catch (ClassCastException e) {
			System.out.println("File Format Error reading from " + file + ": " + e);
			throw new Error(e);
		}
		return obj;
	}

	static public void writeObjectToGZip(String outFileName, Object obj) throws IOException {
		// FIXME: Do finally { blah.close().. etc }
		File outfile = new File(outFileName);
		outfile.delete();
		File parent = new File(outfile.getParent());
		parent.mkdirs();
	
		// Write file out
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(outfile);
		} catch (FileNotFoundException e) {
			System.out.println("Error opening " + outfile + " for output: " + e.getMessage());
			throw e;
		}
				
		GZIPOutputStream gzos = null;
		try {
			gzos = new GZIPOutputStream(fos);
		} catch (IOException e) {
			System.out.println("Error creating GZIPOutputStream: " + e.getMessage());
			throw e;
		}
		
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(gzos);
		} catch (IOException e) {
			System.out.println("Error creating ObjectOutputStream: " + e.getMessage());
			throw e;
		}
		
		try {
			oos.writeObject(obj);
			oos.flush();
			oos.close();
		} catch (IOException e) {
			System.out.println("Error while writing: " + e.getMessage());
			throw e;
		}
	}

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
