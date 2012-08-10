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
package org.fuzzydb.attrs.enums;

import org.fuzzydb.attrs.internal.Attribute;

public abstract class EnumValue extends Attribute {

	protected short enumDefId;

	public EnumValue(int attrId, short enumDefId) {
		super(attrId);
		this.enumDefId = enumDefId;
	}


	public EnumValue( EnumValue rhs ) {
		this( rhs.attrId, rhs.enumDefId );
	}

	public short getEnumDefId() {
		return enumDefId;
	}

	public boolean isWantNull() {
		return false; // default unless overridden
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof EnumValue))
			return false;
		EnumValue bv = (EnumValue)obj;
		return bv.attrId == attrId;
	}
}