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
package com.wwm.db;

/**Implemented by client side access objects to disclose whether or not the object is authoritative.
 * @author ac
 *
 */
public interface Authority {
	/**Determine if this object provides authoritative data access.
	 * This function always returns true in slaveless systems where no non-auth server is available.
	 * @return true if the object is authoritative.
	 */
	public boolean isAuthoritative();
}
