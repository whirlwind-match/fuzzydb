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
package org.fuzzydb.attrs.bool;

import org.fuzzydb.attrs.Decorator;
import org.fuzzydb.attrs.internal.BaseAttribute;


/**
 * @author Neale
 */
public class BooleanDecorator extends Decorator {

	private static final long serialVersionUID = 3256446906237597240L;
	private final String trueLabel;
	private final String falseLabel;

	
	/**
	 * Create BooleanDecorator specifying what the interpret true and false as
	 * @param attrName
	 * @param trueLabel
	 * @param falseLabel
	 */
	public BooleanDecorator(String attrName, String trueLabel, String falseLabel) {
		super( attrName );
		this.trueLabel = trueLabel;
		this.falseLabel = falseLabel;
	}


	/**
	 * Turn true/false into trueLabel & falseLabel
	 */
    @Override
	public String getValueString(BaseAttribute attr) {
        IBooleanValue val = (IBooleanValue)attr;
        return (val.isTrue() ? trueLabel : falseLabel);
    }
}
