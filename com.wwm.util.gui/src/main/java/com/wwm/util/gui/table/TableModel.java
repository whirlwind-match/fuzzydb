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
package com.wwm.util.gui.table;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class TableModel extends DefaultTableModel {
	private static final long serialVersionUID = 2256246137199071804L;

	protected JTable table;
	
	public TableModel(JTable table) {
		super();
		this.table = table;
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}
	
	@Override
	public Class<?> getColumnClass(int column) {
		Object value = getValueAt(0, column);
		if (value == null) return Object.class;
		return value.getClass();
	}
	
	public void configureColumn(int index, String name, Integer minWidth, Integer prefWidth, Integer maxWidth) {
		table.getColumnModel().getColumn(index).setIdentifier(name);
		table.getColumnModel().getColumn(index).setHeaderValue(name);
		if (minWidth != null) table.getColumnModel().getColumn(index).setMinWidth(minWidth);
		if (prefWidth != null) table.getColumnModel().getColumn(index).setPreferredWidth(prefWidth);
		if (maxWidth != null) table.getColumnModel().getColumn(index).setMaxWidth(maxWidth);
	}
}
