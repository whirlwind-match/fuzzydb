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

import org.fuzzydb.attrs.dimensions.Dimensions;
import org.fuzzydb.attrs.dimensions.DimensionsRangeConstraint;

import com.wwm.db.whirlwind.internal.IAttributeConstraint;
import com.wwm.model.dimensions.IDimensions;


public class DimensionRangeCodec extends LayoutConstraintCodec {


	private static final int IDIM_MIN_VALUE_OFFSET = 0;

	private static DimensionRangeCodec instance = null;
	
	public static synchronized DimensionRangeCodec getInstance() {
		if (instance == null) {
			instance = new DimensionRangeCodec();
		}
		return instance;
	}

	
	@Override
	public void encode(LayoutAttrMap<IAttributeConstraint> map, int attrId, Object value) {

		DimensionsRangeConstraint dr = (DimensionsRangeConstraint)value;
		
		IDimensions max = dr.getMax();
		IDimensions min = dr.getMin();
		
		byte dims = (byte) min.getNumDimensions(); // we expect both to be the same

		int size = dims * 2;
		int index = map.getIndexForFloatsWrite(attrId, size);
		
		if (dr.isIncludesNotSpecified()){
			((LayoutConstraintMap) map).setIncludesNotSpecified(attrId);
		}

		// Write in whatever form has been stored as IDimensions (in fact... 
		int iDimMaxValueOffset = IDIM_MIN_VALUE_OFFSET + dims;
		for (int d = 0; d < dims; d++) {
			map.getFloats()[index + IDIM_MIN_VALUE_OFFSET + d] = min.getDimension(d);
			map.getFloats()[index + iDimMaxValueOffset + d] = max.getDimension(d);
		}		
	}
	
	
	@Override
	public IAttributeConstraint getDecoded(LayoutAttrMap<IAttributeConstraint> map, int attrId) {

		int index = map.getIndexQuick(attrId);
		boolean inclNS = ((LayoutConstraintMap) map).getIncludesNotSpecified(attrId);

		// work out length from size
		int nDimensions = map.getLength(attrId) / 2; 

		// Create and populate the max and min Dimensions objects
		IDimensions min = new Dimensions(nDimensions);
		IDimensions max = new Dimensions(nDimensions);

		int iDimMaxValueOffset = IDIM_MIN_VALUE_OFFSET + nDimensions;

		float[] floats = map.getFloats();
		populateIDimensions(floats, index + IDIM_MIN_VALUE_OFFSET, min, nDimensions);
		populateIDimensions(floats, index + iDimMaxValueOffset, max, nDimensions);

		DimensionsRangeConstraint dr = new DimensionsRangeConstraint(attrId, min, max);
		dr.setIncludesNotSpecified(inclNS);
		return dr;
	}
	
	/**
	 * Populates an object implementing the IDimensions interface with
	 * the appropriate number of floats read from supplied ByteArray
	 * @param bytes
	 * @param index
	 * @param idim
	 */
	private void populateIDimensions(float[] floats, int index, IDimensions idim, int numDimensions) {
		for (int d = 0; d < numDimensions; d++) {
			float floatValue = floats[index + d];
			idim.setDimension(d, floatValue);
		}
	}

	
}
