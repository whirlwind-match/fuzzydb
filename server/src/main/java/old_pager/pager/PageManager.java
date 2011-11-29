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

import java.io.File;

public class PageManager<P extends Page> {
	
	private final PageController controller;
//	private final Class<P> clazz;
//	private final File diskRoot;
	
//	private TreeMap<Long, Integer> writeInProgressMap = new TreeMap<Long, Integer>();
	
	public PageManager(PageController c, Class<P> clazz, File diskRoot) {
		super();
		this.controller = c;
//		this.clazz = clazz;
//		this.diskRoot = diskRoot;
	}
	
	public P getPageForRead(long page) {
		controller.ensureFreeSpace();
		//System.
		return null;
	}
	
	public P getPageForWrite(long page) {
		controller.ensureFreeSpace();
		return null;
	}
	
	public void releasePageFromWrite(long dbversion) {
		
	}
}
