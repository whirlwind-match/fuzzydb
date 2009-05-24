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
package com.wwm.atom.impl;

import java.util.logging.Logger;

import com.wwm.abdera.util.server.BaseProviderImpl;
import com.wwm.db.core.LogFactory;


/**
 * Simply hooks up our required CollectionAdapter
 */
public class ProviderImpl extends BaseProviderImpl {

    protected static Logger log = LogFactory.getLogger(ProviderImpl.class);

    
    public ProviderImpl() {
    	super( new CollectionAdapterImpl() );
  }
}
