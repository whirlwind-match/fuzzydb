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
package com.wwm.attrs.layout;

import com.wwm.attrs.dimensions.DimensionsNodeSelector;
import com.wwm.attrs.dimensions.DimensionsRangeConstraint;
import com.wwm.db.whirlwind.internal.IAttribute;
import com.wwm.db.whirlwind.internal.IAttributeConstraint;
import com.wwm.model.dimensions.IDimensions;


class IDimensionCodec extends LayoutAttrCodec<IAttribute> {

	
	private static IDimensionCodec instance = null;
	
	public static synchronized IDimensionCodec getInstance() {
		if (instance == null) {
			instance = new IDimensionCodec();
		}
		return instance;
	}


	@Override
	public void encode(LayoutAttrMap<IAttribute> map, int attrId, Object value) {
		IDimensions idim = (IDimensions)value;
		byte dims = (byte) idim.getNumDimensions();

		int index = map.getIndexForFloatsWrite(attrId, dims);

		// Write in whatever form has been stored as IDimensions (in fact... 
		for (int d = 0; d < dims; d++) {
			map.getFloats()[index + d] = idim.getDimension(d);
		}		
	}
	
	
	/**
	 * Populates an object implementing the IDimensions interface with
	 * the appropriate number of floats read from supplied ByteArray
	 * @param bytes
	 * @param index
	 * @param idim
	 */
	protected void populateIDimensions(LayoutAttrMap<IAttribute> map, int index, IDimensions idim) {
		for (int d = 0; d < idim.getNumDimensions(); d++) {
			float floatValue = map.getFloats()[index + d];
			idim.setDimension(d, floatValue);
		}
	}
	
	@Override
	protected boolean consistentForInternal(LayoutAttrMap<IAttribute> map, int attrId, IAttributeConstraint constraint) {

		int index = map.getIndexQuick(attrId);

		if (constraint instanceof DimensionsNodeSelector){
			DimensionsNodeSelector dns = (DimensionsNodeSelector)constraint;
			return dns.consistent( map.getFloats(), index );
		}
		
		DimensionsRangeConstraint drc = (DimensionsRangeConstraint)constraint;
		return drc.consistent( map.getFloats(), index );
	}
	
		
}
