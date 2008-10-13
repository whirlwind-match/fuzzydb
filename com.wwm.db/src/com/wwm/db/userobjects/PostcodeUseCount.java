package com.wwm.db.userobjects;

import java.io.Serializable;

public class PostcodeUseCount implements Serializable {

	private static final long serialVersionUID = 1L;

	long count = 0;

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}
}
