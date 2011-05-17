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
package com.wwm.db.internal.pager;

import java.util.Date;

public interface PagerMBean {

	public int getLoadedPages();
	
	public int getOutstandingPurges();
	
	public int getPurgeInterval();
	
	public void setPurgeInterval(int millisecs);
	
	public int getMinPagesPerPurge();
	
	public void setMinPagesPerPurge(int pages);
	
	// For example MBean
	public Date getLastPurgeTime();

	public long getTotalScoreTime();

}