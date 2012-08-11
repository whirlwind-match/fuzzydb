/******************************************************************************
 * Copyright (c) 2004-2008 Whirlwind Match Limited. All rights reserved.
 *
 * This is open source software; you can use, redistribute and/or modify
 * it under the terms of the Open Software Licence v 3.0 as published by the 
 * Open Source Initiative.
 *
 * You should have received a copy of the Open Software Licence along with this
 * application. if not, contact the Open Source Initiative (www.opensource.org)
 *****************************************************************************/
package org.fuzzydb.util;


/**
 * Class for creating a Comparable key based on two objects, which can
 * then be used in sorted collections.
 * @author Neale
 *
 * @param <A>
 * @param <B>
 */
public class TupleKey<A,B> implements Comparable<TupleKey<A,B>> {

	private A objectA;
	private B objectB;


	public TupleKey(A objectA, B objectB) {
		super();
		this.objectA = objectA;
		this.objectB = objectB;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(TupleKey<A, B> rhs) {
		if ( rhs.objectB.equals(objectB) ) {
			if ( rhs.objectA.equals(objectA) )
				return 0;
			else
				return objectA.hashCode() - rhs.objectA.hashCode();
		} else
			return objectB.hashCode() - rhs.objectB.hashCode();
	}


	@Override
	public int hashCode() {
		return objectA.hashCode() + objectB.hashCode();
	}


	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object o) {
		TupleKey<A, B> rhs = (TupleKey<A, B>)o;
		return objectA.equals(rhs.objectA)  && objectB.equals(rhs.objectB);
	}
	
	public A getObjectA() {
		return objectA;
	}
	
	public B getObjectB() {
		return objectB;
	}

}
