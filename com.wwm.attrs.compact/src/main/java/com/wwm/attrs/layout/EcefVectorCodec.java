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
import com.wwm.db.whirlwind.internal.IAttribute;
import com.wwm.util.ByteArray;



class EcefVectorCodec extends IDimensionCodec {

	
	static EcefVectorCodec instance = null;
	
	public static synchronized EcefVectorCodec getInstance() {
		if (instance == null) {
			instance = new EcefVectorCodec();
		}
		return instance;
	}


	@Override
	public IAttribute getDecoded(LayoutAttrMap<IAttribute> map, int attrId) {
		int index = map.getIndexQuick(attrId);

		EcefVector attr = new EcefVector( attrId, 0f, 0f, 0f );
		populateIDimensions(map, index, attr);		
		return attr;
	}


	public static EcefVector getValue(ByteArray bytes, int index) {
		throw new UnsupportedOperationException(); // Need to work out what we need
		// might be able to do same for all IDimensions
//		return bytes.getFloat(index + FRP_VALUE_OFFSET);
	}


}
