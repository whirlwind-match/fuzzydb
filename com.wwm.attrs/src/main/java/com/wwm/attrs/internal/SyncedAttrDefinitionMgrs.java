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
package com.wwm.attrs.internal;

import java.util.HashMap;
import java.util.Map;

import com.wwm.db.Store;
import com.wwm.util.DynamicRef;

/**
 * Container in which SADMs are stored.
 * 
 * This is intended to be a container stored singleton instance.
 */
public class SyncedAttrDefinitionMgrs {

    /**
     * Map to allow us to get the ADM for a given store.
     * This is indexed against Store because, with this being a static array, this persists across
     * multiple Store instances being used (e.g. in a unit test).  If we used the store name,
     * we'd find that we'd retrieve an object, and then wrongly try to refresh it.
     * For safety, we handle UnknownObjectException by getting the store afresh. 
     */
    public Map<Store, DynamicRef<SyncedAttrDefinitionMgr>> mgrs = new HashMap<Store, DynamicRef<SyncedAttrDefinitionMgr>>();

    /**
     * Returns a wrapper which is the only one to be used for accessing an SADM for this store.
     * It may be empty, in which case it needs to have an entry created for it and set.
     * @param store
     * @return w (never null, but may be empty - i.e. w.getObject() may be null)
     */
	public DynamicRef<SyncedAttrDefinitionMgr> get(Store store) {
		DynamicRef<SyncedAttrDefinitionMgr> w = mgrs.get(store);
		if (w == null){
			w = new DynamicRef<SyncedAttrDefinitionMgr>();
			mgrs.put(store, w);
		}
		return w;
	}
}
