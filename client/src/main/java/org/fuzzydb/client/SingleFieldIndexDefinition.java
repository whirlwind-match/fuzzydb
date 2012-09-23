package org.fuzzydb.client;

import java.io.Serializable;


public class SingleFieldIndexDefinition<FC> implements Serializable {
	
	private static final long serialVersionUID = 1L;

	public Class<FC> forClass;
	public String fieldName;
	public boolean unique;
	public IndexPointerStyle style;

	public SingleFieldIndexDefinition(Class<FC> forClass, String fieldName,
			boolean unique, IndexPointerStyle style) {
		this.forClass = forClass;
		this.fieldName = fieldName;
		this.unique = unique;
		this.style = style;
	}
}