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
package com.wwm.attrs.internal;

import java.util.TreeMap;


import com.wwm.attrs.IDecorator;
import com.wwm.attrs.bool.IBooleanValue;
import com.wwm.attrs.simple.FloatValue;



public class GlobalDecorators {

	private static GlobalDecorators instance = new GlobalDecorators();

	private TreeMap<String, GenericIntToObjectMap<IDecorator>> map = new TreeMap<String, GenericIntToObjectMap<IDecorator>>(); 
	
	public static final GlobalDecorators getInstance() {
		return instance;
	}

	
	public void add(String storeName, GenericIntToObjectMap<IDecorator> decorators) {
		map.put( storeName, decorators );
	}

    /**
     * @param attribute
     * @return
     */
    public String render(BaseAttribute attr ) {
    	String storeName = (map.size() > 0) ? map.firstKey() : null;
    	
    	return render( storeName, attr ); // null if nowt configured 
    }
    
    public String render( String storeName, BaseAttribute attr ) {
    	int attrIndex = AttrDefinitionMgr.getAttrIndex(attr.getAttrId());
    	IDecorator dec = null;
    	if (storeName != null) {
			GenericIntToObjectMap<IDecorator> decoratorMap = map.get(storeName);
			dec = decoratorMap.get(attrIndex);
		}
    	return render(dec, attr);
    }


	private String render(IDecorator dec, BaseAttribute attr) {
		if (dec != null) {
            return dec.render( attr );
        }

        if ( attr instanceof IBooleanValue){
            return String.valueOf( ((IBooleanValue)attr).isTrue() );
        }

        if ( attr instanceof FloatValue){
            return String.valueOf( ((FloatValue)attr).getValue() );
        }

    	int attrIndex = AttrDefinitionMgr.getAttrIndex(attr.getAttrId());
        return "[no decorator for attrId:" + attrIndex + "]";
	}


	public String getAttrName(int attrId) {
		// FIXME: Should just be able to use AttrDefinitionMgr to get name, somehow.  
		// Should be in a well defined table within the server, as a result of some 
		// listener that we can plug in to a table... in fact, perhaps that's what
		// an index should be... 
		// Currently DbTreeConfiguration.exportDecorators does this in a non-standard manner
		int attrIndex = AttrDefinitionMgr.getAttrIndex(attrId);
    	String storeName = (map.size() > 0) ? map.firstKey() : null;
		GenericIntToObjectMap<IDecorator> decoratorMap = map.get(storeName);
		IDecorator dec = decoratorMap.get(attrIndex);

		if (dec != null){
			return dec.getAttrName();
		} else {
			return "[no decorator found for attrId: " + attrIndex + "]"; 
		}
		
	}

    
	
}
