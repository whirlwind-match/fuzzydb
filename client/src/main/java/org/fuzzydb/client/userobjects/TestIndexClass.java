package org.fuzzydb.client.userobjects;

import java.io.Serializable;

import com.wwm.db.annotations.Key;

public class TestIndexClass implements Serializable {

	private static final long serialVersionUID = 1L;

	@Key
	public int a;
	
	public TestIndexClass(int a) {
		this.a = a;
	}
}
