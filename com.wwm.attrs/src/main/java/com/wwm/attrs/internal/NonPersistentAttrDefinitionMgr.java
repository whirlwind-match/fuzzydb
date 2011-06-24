package com.wwm.attrs.internal;

import com.wwm.attrs.AttributeDefinitionService;

/**
 * An {@link AttributeDefinitionService} that is only persisted for as
 * long as the java VM is active.
 * 
 * NOTE: This should only be used for demo and test scenarios
 * where the database is also in memory only.
 * 
 * @author Neale Upstone
 *
 */
@SuppressWarnings("serial")
public class NonPersistentAttrDefinitionMgr extends AttrDefinitionMgr {
}