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
package com.wwm.indexer.internal;

import java.util.Date;

import com.wwm.indexer.Record;

public class RecordImpl extends BaseRecord implements Record {

    private String privateId;
    private String title;
    private Date updatedDate = new Date(); // default to today

    public RecordImpl() {
    }

    public RecordImpl( String privateId ) {
        assert privateId != null;
        this.privateId = privateId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPrivateId(String publicId) {
        this.privateId = publicId;
    }

    public String getPrivateId() {
        return privateId;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }
}
