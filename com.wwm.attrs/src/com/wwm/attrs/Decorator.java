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
package com.wwm.attrs;

import java.io.Serializable;


import com.wwm.attrs.bool.IBooleanValue;
import com.wwm.attrs.enums.EnumExclusiveValue;
import com.wwm.attrs.enums.EnumMultipleValue;
import com.wwm.attrs.internal.BaseAttribute;
import com.wwm.attrs.simple.FloatHave;





/**
 * Base decorator.  At the very least, it knows how to rendier
 * @author Neale
 */
public class Decorator implements Serializable, IDecorator {

	private static final long serialVersionUID = -3115434071598610412L;

	protected String attrName;

	public Decorator( String attrName ) {
		this.attrName = attrName;
	}
	
	/* (non-Javadoc)
	 * @see likemynds.db.indextree.IDecorator#getAttrName()
	 */
	public String getAttrName() {
		return attrName;
	}

	
	/* (non-Javadoc)
	 * @see likemynds.db.indextree.IDecorator#getValueString(likemynds.db.indextree.attributes.BaseAttribute)
	 */
	public String getValueString( BaseAttribute attr ) {
		
        if ( attr instanceof IBooleanValue){
            return String.valueOf( ((IBooleanValue)attr).isTrue() );
        }
        else if ( attr instanceof FloatHave){
        	return String.valueOf( ((FloatHave)attr).getValue() );
        }
        else if (attr instanceof EnumExclusiveValue) {
        	// default in case decorator not configured
            return String.valueOf( ((EnumExclusiveValue)attr).getEnumIndex() );
        }
        else if (attr instanceof EnumMultipleValue) {
            return expandToString( ((EnumMultipleValue)attr).getValues() );
        }

        return "?";
	}

	
	/* (non-Javadoc)
	 * @see likemynds.db.indextree.IDecorator#render(likemynds.db.indextree.attributes.BaseAttribute)
	 */
	public final String render( BaseAttribute attr ) {

		return attrName + "(" + getValueString( attr ) + ")";
	}

	
	private String expandToString(short[] vals) {
		
		if (vals.length == 0) return "";
		
		StringBuilder str = new StringBuilder();
		
		for( short val : vals ){
			str.append( val ).append( "," );
		}
		
		return str.substring(0, str.length() - 1);
	}
	
}
