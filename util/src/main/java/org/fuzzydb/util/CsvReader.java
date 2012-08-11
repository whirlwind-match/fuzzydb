/******************************************************************************
 * Copyright (c) 2004-2008 Whirlwind Match Limited. All rights reserved.
 *
 * This is open source software; you can use, redistribute and/or modify
 * it under the terms of the Open Software Licence v 3.0 as published by the 
 * Open Source Initiative.
 *
 * You should have received a copy of the Open Software Licence along with this
 * application. if not, contact the Open Source Initiative (www.opensource.org)
 *****************************************************************************/
package org.fuzzydb.util;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ac
 *
 */
public class CsvReader {
	
    static private Class<?> classForLoadingResources = CsvReader.class;
    
	public static class GarbageLineException extends Exception {

		private static final long serialVersionUID = -5453648945686590358L;

		public GarbageLineException(String message) {
			super(message);
		}

		public GarbageLineException(String message, Throwable cause) {
			super(message, cause);
		}

	}

	public static class NoSuchColumnException extends Exception {
		private static final long serialVersionUID = 6750801022096541219L;
		public NoSuchColumnException(String name) {
			super(name);
		}
	}

	public static class UnsupportedTypeException extends Exception {
		private static final long serialVersionUID = 6750801022096541219L;
		public UnsupportedTypeException(String name) {
			super(name);
		}
	}

	static private class NameAndType {
		Integer columnNumber;
		String name;
		Class<?> type;
		public NameAndType(int columnNumber, String name, Class<?> type) {
			this.name = name;
			this.type = type;
			this.columnNumber = columnNumber;
		}
	}

	private ArrayList<NameAndType> columnDesc = new ArrayList<NameAndType>();
	private String columns;
	private BufferedReader reader;
	private int columnCount;
	private boolean hasHeader;
	private boolean pathIsResource;

	/**
	 * @param path
	 * @param stripQuotes
	 * @param pathIsResource - set to true if the path is within the classLoader, false if from the file system.
	 * @throws IOException
	 */
	public CsvReader(String path, boolean stripQuotes, boolean pathIsResource) throws IOException {
		this.pathIsResource = pathIsResource;
		startWithHeader(path);
	}

	/**
	 * 
	 * @param path
	 * @param stripQuotes
	 * @param hasHeader
	 * @param pathIsResource - set to true if the path is within the classLoader, false if from the file system.
	 * @throws IOException
	 */
	public CsvReader(String path, boolean stripQuotes, boolean hasHeader, boolean pathIsResource) throws IOException {
		this.pathIsResource = pathIsResource;
		if (hasHeader) {
			startWithHeader(path);
		} else {
			startWithoutHeader(path);
		}
	}

	private void startWithHeader(String file) throws IOException
	{
		this.hasHeader = true;
		if (pathIsResource) {
			InputStream stream = classForLoadingResources.getResourceAsStream(file);
			reader = new BufferedReader(new InputStreamReader(stream), 1024*1024);
		} else {
			reader = new BufferedReader(new FileReader(file), 1024*1024);
		}
		columns = reader.readLine();

		columnCount = 1;
		// Number of columns is 1+number of ',' chars
		for (int i = 0; i < columns.length(); i++) {
			if (columns.charAt(i) == ',') {
				columnCount++;
			}
		}
	}

	private void startWithoutHeader(String file) throws IOException
	{
		this.hasHeader = false;
		reader = new BufferedReader(new FileReader(file), 1024*1024);
		columnCount = 0;
	}
	/*
	private String stripQuotes(String quotedString) {
		if (!stripQuotes || quotedString.length() < 2) {
			return quotedString;
		}
		return quotedString.substring(1, quotedString.length()-1);
	}
	 */
	public void setColumn(String name, Class<?> type) throws NoSuchColumnException {
		assert(this.hasHeader);	// Can only use this method with named columns
		CsvTokeniser ct = new CsvTokeniser(columns);
		int index = 0;
		String col;
		while (null != (col = ct.next())) {
			if (name.equals(col)) {
				columnDesc.add(new NameAndType(index, name, type));
				return;
			}
			index++;
		}
		throw new NoSuchColumnException(name);
	}

	public void setColumn(String name, Class<?> type, int index) {
		columnDesc.add(new NameAndType(index, name, type));
	}


	public Map<String, Object> readLine() throws IOException, UnsupportedTypeException, GarbageLineException {
		Map<String, Object> map = new HashMap<String, Object>();
		String line = reader.readLine();
		if (line==null) {
			throw new EOFException();
		}
		CsvTokeniser st = new CsvTokeniser(line);
		int column = 0;
		for (NameAndType entry : columnDesc) {
			String token = null;
			while (entry.columnNumber >= column) {
				token = st.next();
				column++;

				if (token == null) // null is usually end of line
					throw new GarbageLineException("null token at column " + column + ".  Line:" + line, null);
			}

			Object result = convertToken(line, entry, token);
			map.put(entry.name, result);
		}
		return map;
	}

	private Object convertToken(String line, NameAndType entry, String token) throws UnsupportedTypeException, GarbageLineException {
		Class<?> clazz = entry.type;

		try {
			if ("".equals(token)){
				return null;
			}
			if (clazz == String.class) { // store null for empty cells
				return token;
			}
			if (clazz == Boolean.class) {
				return Boolean.parseBoolean(token);
			}
			if (clazz == Integer.class) {
				return Integer.parseInt(token);
			}
			if (clazz == Float.class) {
				return Float.parseFloat(token);
			}
			if (clazz == Long.class) {
				return Long.parseLong(token);
			}
			if (clazz == Double.class) {
				return Double.parseDouble(token);
			}
			throw new UnsupportedTypeException(clazz.toString());
		} catch (NumberFormatException e) {
			throw new GarbageLineException(line, e);
		}
	}
}
