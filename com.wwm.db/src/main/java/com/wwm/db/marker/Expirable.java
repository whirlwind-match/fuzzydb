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
package com.wwm.db.marker;

import java.util.Date;

/**
 * Marker interface for user DB objects which are intended to expire after the specified time.
 */
public interface Expirable {
	/**
	 * Called by the database to establish the expiry time. The Date value should remain constant
	 * during the life of one version of the object as this is used to position this object in an
	 * index.
	 * The value may be modified by the usual update or modify operations, as this upissues the
	 * object and moves it in the index.
	 * @return The time at which the object will become worthy of deletion.
	 */
	public Date getExpiryTime();
}
