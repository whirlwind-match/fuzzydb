package com.wwm.db.userobjects;

import java.io.Serializable;

import com.wwm.db.annotations.Key;

public class SampleKeyedObject implements Serializable {

	private static final long serialVersionUID = 1L;
	@Key private Integer myvalue;
	private int id; // not indexed
	
	public SampleKeyedObject(int value) {
		super();
		this.myvalue = new Integer(value);
		this.id = 0;
	}
	public SampleKeyedObject() {
		super();
	}
	public SampleKeyedObject(int value, int id) {
		super();
		this.myvalue = new Integer(value);
		this.id = id;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getMyvalue() {
		return myvalue.intValue();
	}
	public void setMyvalue(int v) {
		this.myvalue = new Integer(v);
	}

}
