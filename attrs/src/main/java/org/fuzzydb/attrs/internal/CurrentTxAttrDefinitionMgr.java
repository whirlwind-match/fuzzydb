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
package org.fuzzydb.attrs.internal;

import org.fuzzydb.attrs.AttributeDefinitionService;
import org.fuzzydb.attrs.enums.EnumDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.wwm.db.DataOperations;
import com.wwm.db.Store;
import com.wwm.db.Transaction;
import com.wwm.db.whirlwind.internal.IAttribute;


/**
 * {@link AttributeDefinitionService} that persists an instance of {@link AttrDefinitionMgr},
 * only hitting the database if the wanted information is not found.
 * <p>
 * The design here for the moment keeps one cached instance per thread, as a persisted object
 * should not be used in multiple threads.
 * <p>
 * NOTE: This currently will only work read-only. 
 * 
 * @author Neale Upstone
 */
public class CurrentTxAttrDefinitionMgr implements AttributeDefinitionService {

    /**
     * One instance per thread, so that updates are not pushed to other threads.
     * If we modify this, we must detach the instance so that we read-afresh after
     * the transaction commit.
     * Currently we have the problem that we don't see the updates within this 
     * transaction 
     */
    private final ThreadLocal<AttrDefinitionMgr> attrDefsRef = new ThreadLocal<AttrDefinitionMgr>();
    

	private final Store store; 

	@Autowired
    public CurrentTxAttrDefinitionMgr(Store store) {
        this.store = store;
    }

    private AttrDefinitionMgr getAttrDefs() {
    	if (attrDefsRef.get() == null) {
    		return getLatestAttrDefs();
    	}
    	return attrDefsRef.get();
    }
    
    /**
     * Refresh latest, if we have one.
     */
    private AttrDefinitionMgr getLatestAttrDefs() {

    	Transaction tx = store.currentTransaction();
    	Assert.state(tx != null, "No transaction active");
    	store.pushNamespace(DataOperations.DEFAULT_NAMESPACE);
    	AttrDefinitionMgr mgr = tx.retrieveFirstOf( SyncedAttrDefinitionMgr.class ); // currently persisted as sync'd version by store initializer
    	store.popNamespace();
    	Assert.state(mgr != null, "All AttrDefs must have been provided elsewhere (in a matcher config for example)");
    	mgr.setReadOnly(true);
    	
    	// WIP: for full read-write.
//    	if (mgr == null){
//    		try {
//	        	mgr = tx.refresh( attrDefsRef.get() );
//	        } catch (UnknownObjectException e) {
//	        	mgr = new AttrDefinitionMgr();
//	        	tx.create(mgr);
//	        }
//    	}
    	attrDefsRef.set(mgr);
	    return mgr;
	}
 
    
    @Override
	public int getAttrId(String attrName) {
		return getAttrDefs().getAttrId(attrName);
	}

	@Override
	public String getAttrName(int attrId) {
		return getAttrDefs().getAttrName(attrId);
	}

	@Override
	public int getAttrId(String attrName, Class<?> clazz) {
		return getAttrDefs().getAttrId(attrName, clazz);
	}

	@Override
	public Class<?> getExternalClass(int attrId) {
		return getAttrDefs().getExternalClass(attrId);
	}

	@Override
	public Class<? extends IAttribute> getDbClass(int attrId) {
		return getAttrDefs().getDbClass(attrId);
	}

	@Override
	public EnumDefinition getEnumDefinition(String defName) {
		return getAttrDefs().getEnumDefinition(defName);
	}

	@Override
	public EnumDefinition getEnumDef(short enumDefId) {
		return getAttrDefs().getEnumDef(enumDefId);
	}

	@Override
	public void associateAttrToEnumDef(int attrId, EnumDefinition enumDef) {
		getAttrDefs().associateAttrToEnumDef(attrId, enumDef);
	}

	@Override
	public EnumDefinition getEnumDefForAttrId(int attrId) {
		return getAttrDefs().getEnumDefForAttrId(attrId);
	}
	
	
}
