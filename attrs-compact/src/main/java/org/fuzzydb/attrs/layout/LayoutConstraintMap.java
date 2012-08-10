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
package org.fuzzydb.attrs.layout;


import org.fuzzydb.attrs.internal.AttrDefinitionMgr;
import org.fuzzydb.attrs.internal.IConstraintMap;
import org.fuzzydb.core.whirlwind.internal.IAttribute;
import org.fuzzydb.core.whirlwind.internal.IAttributeConstraint;



public class LayoutConstraintMap extends LayoutAttrMap<IAttributeConstraint> implements
		IConstraintMap {

	private static final long serialVersionUID = 1L;

	
	private int attrsInclNotSpecified; // bit mask supporting up to 32 attributes per map. bit is set if inclNS is true


	public void setIncludesNotSpecified( int attrId ){
		int sequence = AttrDefinitionMgr.getAttrIndex(attrId);
		assert( sequence < 30 ); // gone for 30 so I don't have to worry about signed int :)
		
		int flag = 1 << sequence;
		
		attrsInclNotSpecified |= flag;
	}
	
	public boolean getIncludesNotSpecified(int attrId) {
		int sequence = AttrDefinitionMgr.getAttrIndex(attrId);
		assert( sequence < 30 ); // gone for 30 so I don't have to worry about signed int :)
		
		int flag = 1 << sequence;

		return ( attrsInclNotSpecified & flag) != 0;
	}

	
	
//	TODO IMPLEMENT CONSISTENT() and double-check that expand should only be one or other after a while (should after split) 
	
	
    /**
     * Fetch, expand and replace the attribute
     */
	public boolean expand(IAttribute attr, int attrId) {
        LayoutConstraintCodec codec = LayoutCodecManager.getConstraintCodec(attrId);
		return codec.expand(this, attr, attrId);
	}

	@Override
	protected LayoutMapConfig getMapConfig() {
		return LayoutMapConfig.getInstance().getConstraintMapConfig();
	}
	
	@Override
	protected LayoutConstraintCodec getCodec(int attrId) {
		return LayoutCodecManager.getConstraintCodec( attrId );
	}

    @Override
    public LayoutConstraintMap clone() {
        return (LayoutConstraintMap) super.clone();
    }

}
