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
package com.wwm.indexer.db.converters;


import com.wwm.db.dao.Db2ObjectDAO;
import com.wwm.db.dao.SimpleDAO;
import com.wwm.postcode.PostcodeConvertor;

public class TempFactory {
    private static final SimpleDAO dao = new Db2ObjectDAO("wwmdb:/com.wwm.stats");
    private static final PostcodeConvertor converter = new PostcodeConvertor(dao);

    public static PostcodeConvertor getPostcodeConverter() {
		return converter;
	}

}
