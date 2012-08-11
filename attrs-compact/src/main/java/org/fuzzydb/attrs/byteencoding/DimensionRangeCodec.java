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
package org.fuzzydb.attrs.byteencoding;

import org.fuzzydb.attrs.dimensions.Dimensions;
import org.fuzzydb.attrs.dimensions.DimensionsRangeConstraint;
import org.fuzzydb.dto.dimensions.IDimensions;
import org.fuzzydb.util.ByteArray;




/**
 * Provides encoding for DimensionRange
 */
public class DimensionRangeCodec extends CompactConstraintCodec {

	private static final int LENGTH_OFFSET = PAYLOAD_OFFSET;
	private static final byte IDIM_BASE_SIZE = LENGTH_OFFSET + 1; // ( we use 1st byte for length) + numDimensions * 8 bytes:  1 for size, 4 for attrId, n*8 for n floats for min/max

	private static final int IDIM_MIN_VALUE_OFFSET = LENGTH_OFFSET + 1;

	
	
    static DimensionRangeCodec instance = null;
    
    public static synchronized CompactAttrCodec getInstance() {
        if (instance == null) {
            instance = new DimensionRangeCodec();
        }
        return instance;
    }


	@Override
	public void encodeToByteArray(ByteArray bytes, int attrId, Object value) {

		DimensionsRangeConstraint dr = (DimensionsRangeConstraint)value;
		
		IDimensions max = dr.getMax();
		IDimensions min = dr.getMin();
		
		byte dims = (byte) min.getNumDimensions(); // we expect both to be the same
		byte size = (byte) (IDIM_BASE_SIZE + 8 * dims);
		
		// Find if there's one to overwrite
		int i = findAttrInBuf(bytes, attrId);
		
		// If not, allocate some space on the end
		if ( i < 0 ) {
			i = bytes.getIndexForWrite(size);
		}
		bytes.putByte(i + LENGTH_OFFSET, size);
		setConstraintAttrId(bytes, i, attrId, dr.isIncludesNotSpecified());
		
		// Write in whatever form has been stored as IDimensions (in fact... 
		int iDimMaxValueOffset = IDIM_MIN_VALUE_OFFSET + 4 * dims;
		for (int d = 0; d < dims; d++) {
			bytes.putFloat(i + IDIM_MIN_VALUE_OFFSET + 4 * d, min.getDimension(d));
			bytes.putFloat(i + iDimMaxValueOffset + 4 * d, max.getDimension(d));
		}		
	}

	
	@Override
	public DimensionsRangeConstraint getDecoded(ByteArray bytes, int index) {

        int headerWord = getHeaderWord( bytes, index);
		int attrId = getAttrId( headerWord );
		boolean notSpecified = getIncludesNotSpecified(headerWord);

		// work out length from size
		int nDimensions = (bytes.getByte(index + LENGTH_OFFSET) - IDIM_BASE_SIZE) / 8; 

		// Create and populate the max and min Dimensions objects
		IDimensions min = new Dimensions(nDimensions);
		IDimensions max = new Dimensions(nDimensions);

		populateIDimensions(bytes, index + IDIM_MIN_VALUE_OFFSET, min);
		int iDimMaxValueOffset = IDIM_MIN_VALUE_OFFSET + 4 * nDimensions;
		populateIDimensions(bytes, index + iDimMaxValueOffset, max);
		
		DimensionsRangeConstraint dr = new DimensionsRangeConstraint(attrId, min, max);
		dr.setIncludesNotSpecified(notSpecified);
		return dr;
	}
	

	
	private void populateIDimensions(ByteArray bytes, int index, IDimensions idim) {
		for (int i = 0; i < idim.getNumDimensions(); i++) {
			float floatValue = bytes.getFloat(index + i * 4);
			idim.setDimension(i, floatValue);
		}
	}

}
