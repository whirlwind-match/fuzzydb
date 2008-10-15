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

import com.wwm.attrs.location.EcefVector;
import com.wwm.attrs.location.RangePreference;
import com.wwm.attrs.simple.FloatConstraint;
import com.wwm.db.whirlwind.internal.IAttribute;
import com.wwm.db.whirlwind.internal.IAttributeConstraint;
import com.wwm.model.dimensions.IDimensions;


class LocationPrefCodec extends LayoutAttrCodec<IAttribute> {

	private static final int RANGE_OFFSET = 3;

	private static final int ENCODED_LENGTH = 4; // x, y, z, range/ -range (encode prefer Close as -ve range)
	
	private static LocationPrefCodec instance = null;
	
	public static synchronized LocationPrefCodec getInstance() {
		if (instance == null) {
			instance = new LocationPrefCodec();
		}
		return instance;
	}


	@Override
	public void encode(LayoutAttrMap<IAttribute> map, int attrId, Object value) {

		int index = map.getIndexForFloatsWrite(attrId, ENCODED_LENGTH);

		RangePreference rangePref = (RangePreference)value;

		for (int i=0; i < 3; i++){
			map.getFloats()[index + i] = rangePref.getDimension(i);
		}
		
		float dist = rangePref.getRange();
		if (rangePref.isPreferClose()) { // encode preferClose as -ve range
			dist = -dist;
		}
		map.getFloats()[index + RANGE_OFFSET] = dist;
	}
	
	
	@Override
	public IAttribute getDecoded(LayoutAttrMap<IAttribute> map, int attrId) {
		int index = map.getIndexQuick(attrId);

		EcefVector vector = new EcefVector( attrId, 0f, 0f, 0f );
		populateIDimensions(map.getFloats(), index, vector);		

		float range = getRange(map, index);
		// decode -ve range as preferClose = true
		IAttribute attr;
		if (range < 0f) {
			attr = new RangePreference( attrId, vector, -range, true );
		} else {
			attr = new RangePreference( attrId, vector, range, false );
		}
		return attr;
	}
	
	
	/**
	 * Populates an object implementing the IDimensions interface with
	 * the appropriate number of floats read from supplied ByteArray
	 */
	// TODO Move to IDimensionCodec and replace calls to original with + IDIMOFFSET..
	protected void populateIDimensions(float[] floats, int index, IDimensions idim) {
		for (int i = 0; i < idim.getNumDimensions(); i++) {
			idim.setDimension(i, floats[index + i]);
		}
	}
	
	@Override
	protected boolean consistentForInternal(LayoutAttrMap<IAttribute> map, int attrId, IAttributeConstraint constraint) {

		int index = map.getIndexQuick(attrId);

//		if (constraint instanceof FloatConstraint){
			FloatConstraint fc = (FloatConstraint)constraint;
			return fc.contains(getRange(map, index));
//		}
		
//		if (constraint instanceof DimensionsNodeSelector){
//			DimensionsNodeSelector dns = (DimensionsNodeSelector)constraint;
//			return dns.consistent( map.getFloats(), index );
//		}
//		
//		DimensionsRangeConstraint drc = (DimensionsRangeConstraint)constraint;
//		return drc.consistent( map.getFloats(), index );
	}


	private float getRange(LayoutAttrMap<IAttribute> map, int index) {
		float range = map.getFloats()[index + RANGE_OFFSET];
		return (range < 0) ? -range : range;
	}

}
