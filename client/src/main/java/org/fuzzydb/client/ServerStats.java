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
package org.fuzzydb.client;

import java.io.Serializable;

public class ServerStats implements Serializable {
	
	private static final long serialVersionUID = 1385190759034247557L;
	
	private long free;
	private long max;
	private long total;
	
	public ServerStats(long free, long max, long total) {
		this.free = free;
		this.max = max;
		this.total = total;		
	}
	
	public long getFree() {
		return free;
	}

	public long getMax() {
		return max;
	}

	public long getTotal() {
		return total;
	}
	
	public long getUsed() {
		return total-free;
	}	
	
}
