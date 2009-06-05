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
package old_pager.pager;


// swap out the page with the lowest cost
//
// cost = (read effort * read access frequency) + (write effort * write access frequency)
//



public class PageController {
	private long accesses = 0;
	
	
	public PageController() {
		super();
	}
	
	void pageAccessedForRead(Page p) {
		accesses++;
	}
	
	void pageAccessedForWrite(Page p) {
		accesses++;
	}
	
	void ensureFreeSpace() {
		
	}
}
