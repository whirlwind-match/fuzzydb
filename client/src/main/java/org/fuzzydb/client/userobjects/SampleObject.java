package org.fuzzydb.client.userobjects;

import java.io.Serializable;

public class SampleObject implements Serializable {
	
	private static final long serialVersionUID = 1L;

	public SampleObject(int i) {
		super();
		test = i;
	}
	public SampleObject() {
		super();
	}

	private int test;

	public int getTest() {
		return test;
	}
	public void setTest(int i) {
		test = i;
	}
}
