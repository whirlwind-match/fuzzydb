package com.wwm.db.userobjects;


import java.io.Serializable;

import com.wwm.db.annotations.Key;

public class SampleUniqueKeyedObject implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Key(unique=true) private Integer key;
	private int id; // not indexed

	public SampleUniqueKeyedObject(int key, int id) {
		this.key = new Integer(key);
		this.id = id;
	}

	public int getKey() {
		return key.intValue();
	}

	public void setKey(int key) {
		this.key = new Integer(key);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
