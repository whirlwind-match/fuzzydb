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
package com.wwm.indexer;

import java.util.Date;

import com.wwm.indexer.internal.BaseRecord;

class Rec extends BaseRecord implements Record {
    String dis = "";
    int id = 0;

    public Rec(int id, String dis) {
        this.id = id;
        this.dis = dis;
    }

    public String getTitle() {
        return dis;
    }

    public int getRecordId() {
        return id;
    }

    public String getPrivateId() {
        return null; // TODO:
    }

    public Date getUpdatedDate() {
        return new Date();
    }
}