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
package org.fuzzydb.attrs.internal.xstream;

import java.util.ArrayList;

import org.springframework.util.Assert;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;

/**
 * An Table that can be read and written (perhaps in future) as HTML via XStream  
 */
public class HtmlTableReader {
	
	private HierarchicalStreamReader reader;
	
	private boolean readingFirstRow = true;

	/** Contents of entire first row */
	private ArrayList<String> columnHeadings;
	private ArrayList<String> rowHeadings = new ArrayList<String>();
	
	// Not sure if this is best approach.
	private ArrayList<ArrayList<String>> table = new ArrayList<ArrayList<String>>(); 

	public HtmlTableReader(HierarchicalStreamReader reader) {
		this.reader = reader;
	}

	public void read() {
		String initialNode = reader.getNodeName();
		Assert.isTrue(initialNode.equals("map") || initialNode.equals("html"), 
				"Expecting to look for table either within <html> or <map>, not <" + initialNode + ">");
		int depth = 0;
		// navigate to tbody
		boolean there = false;
		while( there == false) {
			String node = reader.getNodeName();
			// move down for all things we want, otherwise down/up to skip
			if (node.equals("html")
					|| node.equals("map") 
					|| node.equals("body") 
					) {
				reader.moveDown();
				depth++;
			} 
			else if (node.equals("table")){
				reader.moveDown();
				depth++;
				there = true;
			}
			else {
				reader.moveUp();
				reader.moveDown();
			} 
		}
		
		String node = reader.getNodeName();
		

		if (node.equals("tbody")) {
			readRows();
		} else {
			throw new Error("Expecting tbody within table");
		}
		
		for( ; depth > 0; depth--) {
			reader.moveUp();
		}
		Assert.isTrue(reader.getNodeName().equals(initialNode));
	}

	/**
	 * read table contents from point of <tbody> being the current node
	 */
	private void readRows() {
		do {
			reader.moveDown();
			readRow();
			reader.moveUp();
			
		} while (reader.hasMoreChildren());
		
	}

	private void readRow() {
		String node = reader.getNodeName(); assert node.equals("tr");

		ArrayList<String> row = new ArrayList<String>();
		if (readingFirstRow){
			reader.moveDown(); reader.moveUp(); // skip corner cell
			readChildrenToList(row);
			columnHeadings = row;
			readingFirstRow = false;
		} else {
			addCellToList(rowHeadings); // first is a row heading
			readChildrenToList(row);
			table.add(row);
		}
	}

	private void readChildrenToList(ArrayList<String> row) {
		do {
			addCellToList(row);
		} while (reader.hasMoreChildren());
	}

	private void addCellToList(ArrayList<String> row) {
//		String node = reader.getNodeName();
		reader.moveDown();
		row.add(reader.getValue());
		reader.moveUp();
	}

	/**
	 * Convenient way of doing something with each cell, based on it's row/column indices or headings
	 */
	public void foreachCell( CellCallback callback ){
		int rowIndex = 0;
		for (String rowHeading : rowHeadings) {
			int colIndex = 0;
			for (String colHeading : columnHeadings) {
				String value = table.get(rowIndex).get(colIndex);
				callback.doCell(rowIndex, rowHeading, colIndex, colHeading, value);
				colIndex++;
			}
			rowIndex++;
		}
	}
	
}
