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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

public class FileUtils {

	
	static private Logger log = LoggerFactory.getLogger(FileUtils.class);
	
	/**
	 * @param resourceUrl the resource location to resolve: either a "classpath:" pseudo URL, a "file:" URL, or a plain file path
	 * @throws RuntimeException if file not found
	 */
	static public Object readObjectFromGZip(String resourceUrl) {
		
		InputStream stream = null;
		try {
			stream = ResourceUtils.getURL(resourceUrl).openStream();
			return readObjectFromGZip(stream);
		} catch (FileNotFoundException e) {
			log.error("Error opening resource: " + e.getMessage());
			throw new RuntimeException(e);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		finally {
			try {
				if (stream != null) {
					stream.close();
				}
			} catch (IOException e) {
				// squash close exception
			}
		}
	}

	
	protected static Object readObjectFromGZip(InputStream stream) throws IOException, ClassNotFoundException {
		GZIPInputStream gzis = null;
		ObjectInputStream ois = null;
		try {
			gzis = new GZIPInputStream(stream);
			ois = new ObjectInputStream(gzis);
			return ois.readObject();
		} 
		finally {
			if (gzis != null) gzis.close();
			if (ois != null) ois.close();
		}
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
			log.error("Error opening " + outfile + " for output: " + e.getMessage());
			throw e;
		}
				
		GZIPOutputStream gzos = null;
		try {
			gzos = new GZIPOutputStream(fos);
		} catch (IOException e) {
			log.error("Error creating GZIPOutputStream: " + e.getMessage());
			throw e;
		}
		
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(gzos);
		} catch (IOException e) {
			log.error("Error creating ObjectOutputStream: " + e.getMessage());
			throw e;
		}
		
		try {
			oos.writeObject(obj);
			oos.flush();
			oos.close();
		} catch (IOException e) {
			log.error("Error while writing: " + e.getMessage());
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
