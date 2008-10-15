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

import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.protocol.server.RequestContext;

import com.wwm.atom.server.BadRequestException;
import com.wwm.atom.server.NotFoundException;

/**
 * Handles the CRUD operations for each different record type.
 * Some records my be index objects, whereas some may be configurations such as
 * scorers, index strategies, scorer configurations.
 */
public interface TypeHandler {

	void createEntry(RequestContext request, Document<Entry> doc) throws Exception;

	void deleteEntry(RequestContext context, String privateRecordId) throws NotFoundException;

	Entry getEntry(RequestContext request, String entryId) throws NotFoundException;

	Feed getFeed(RequestContext request) throws BadRequestException;
}
