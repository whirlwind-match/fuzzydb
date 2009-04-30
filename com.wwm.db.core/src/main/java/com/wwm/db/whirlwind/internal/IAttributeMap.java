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
package com.wwm.db.whirlwind.internal;



/**
 * Internal interface of a map of attributes.  This is the internal format used by the server.
 * 
 * @author Neale
 */
public interface IAttributeMap<T extends IAttribute> extends Iterable<T>, Cloneable {

	/**
	 * Strictly, this is putAttr() as it replaces an existing instance with the same attr.getAttrId()
	 */
	public T putAttr(T attr);
	
	T put(int attrId, T value); // Map compatible - should set attrId on value if impl relies on value containing attrId

	/**
	 * get IAttribute by attrId
	 */
	public T findAttr(int attrId);

	/**
	 * Specifically created for some testing...
	 */
	public T removeAttr(int attrId);
	
	/**
	 * number of items in the map (didn't want to implement whole of Collection interface)
	 * @return
	 */
	public int size();
	
	public abstract IAttributeMap<T> clone();

	/**
	 * Determine if these attributes are consistent with the supplied constraint
	 * @param splitId 
	 * @return true if consistent
	 */
	public boolean consistentFor(IAttributeConstraint constraint, int splitId);

}
