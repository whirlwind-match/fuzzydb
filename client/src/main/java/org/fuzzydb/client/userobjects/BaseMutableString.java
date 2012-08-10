package org.fuzzydb.client.userobjects;

import java.io.Serializable;

@SuppressWarnings("serial")
public class BaseMutableString implements Serializable {
	public String value;
	
	public BaseMutableString(String s) {
		value = s;
	}
	
	@Override
	public int hashCode() {
		return value.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof BaseMutableString) {
			BaseMutableString rhs = (BaseMutableString)o;
			return value.equals(rhs.value);
		}
		if (o instanceof String) {
			String rhs = (String)o;
			return value.equals(rhs);
		}
		throw new UnsupportedOperationException();
	}
	
	@Override
	public String toString() {
		return value;
	}
}

