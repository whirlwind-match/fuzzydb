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
package com.wwm.db.services;

import java.util.Collection;
import java.util.LinkedList;

import org.slf4j.Logger;

import com.google.inject.Singleton;
import com.wwm.db.core.LogFactory;
import com.wwm.db.internal.index.IndexImplementation;


/**
 * A service to track what implementations of an index are available.
 * 
 * This is used internally by the database for discovery of indexes whenever a
 * new class is encountered.
 */
@Singleton
public class IndexImplementationsService {
	
    private final Logger log = LogFactory.getLogger(getClass());

    private final Collection<IndexImplementation> impls = new LinkedList<IndexImplementation>();

	public IndexImplementationsService() {
		installAccPackIfAvailable();
	}
	
	public Collection<IndexImplementation> getIndexImplementations() {
		return impls;
	}
	
	public void add( IndexImplementation impl ) {
		impls.add(impl);
	}
	
	public void remove( IndexImplementation impl ) {
		impls.remove(impl);
	}
	
	private void installAccPackIfAvailable() {
		Class<?> cl; 
		try {
			cl = Class.forName("org.fuzzydb.client.server.whirlwind.WhirlwindIndexImpl");
		} catch (ClassNotFoundException e) {
			return;
		}
		try {
			log.info("** Accelerator Pack detected. Enabling **");
			IndexImplementation index = (IndexImplementation) cl.newInstance(); 
			add(index);
			return;
		} catch (InstantiationException e) {
			log.warn("Can't create " + cl.getName(), e);
			throw new RuntimeException(e);
		} catch (Exception e) {
			log.error("Error getting index instance", e);
			throw new RuntimeException(e);
		}
	}

}
