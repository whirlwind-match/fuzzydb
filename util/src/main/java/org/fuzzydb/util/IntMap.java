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
 * Interface that Trove should have supplied as what TIntHashMap implements
 * This is the int version of Map<Integer,V>
 * @author nu
 *
 * @param <V> value
 */
public interface IntMap<V> {

    V put(int key, V value);

    V get(int key );
}
