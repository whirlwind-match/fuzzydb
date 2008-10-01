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

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class TableRenderer implements TableCellRenderer {
	private TableCellRenderer __defaultRenderer;
	
	public TableRenderer(JTable table, Class<?> clazz) {
		__defaultRenderer = table.getDefaultRenderer(clazz);
	}
	
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected,
			boolean hasFocus,
			int row, int column)
	{
		if(value instanceof Component)
			return (Component)value;
		return __defaultRenderer.getTableCellRendererComponent(
				table, value, isSelected, hasFocus, row, column);
	}
}