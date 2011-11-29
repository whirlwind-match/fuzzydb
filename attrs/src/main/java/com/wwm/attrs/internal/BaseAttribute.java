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

import java.io.Serializable;





/**
 * @author Neale
 */
public abstract class BaseAttribute implements Serializable, Cloneable {

	private static final long serialVersionUID = -2207772517568804456L;
	
	/**
     * The attribute ID of this attribute instance
     * This is used to differentiate between different attributes implemented using the same
     * class. 
     */
    protected int attrId = 0;

    /**
     * For all attributes, we set the attribute Id.  
     * BranchConstraints are derived from here too.  Their attrId is the attrId
     * of the item that they constrain (i.e. select/bound). 
     */
    protected BaseAttribute(int attrId) {
        this.attrId = attrId;
    }

    protected BaseAttribute(BaseAttribute rhs) {
    	super();
        this.attrId = rhs.attrId;
    }
    
    public int getAttrId() {
        // Assign an id for this attribute
        // TODO CATCH ERROR WHERE attrId wasn't created
        if (attrId == 0) {
//            attrId = AttributeIdMap.instance.getId( this.getClass().getName() );
//			throw new RuntimeException("AttrId was not set");
        }
        return attrId;
    }
    
    public void setAttrId( int id ){
        attrId = id;
    }
    

    @Override
	public String toString(){
        return GlobalDecorators.getInstance().render( this );
    }

   public String getAttrName() {
	   return GlobalDecorators.getInstance().getAttrName( this.getAttrId() );
   }

    @Override
    public int hashCode() {
    	final int prime = 31;
    	int result = 1;
    	result = prime * result + attrId;
    	return result;
    }
    
    @Override
    public boolean equals(Object obj) {
    	if (this == obj)
    		return true;
    	if (obj == null)
    		return false;
    	if (getClass() != obj.getClass())
    		return false;
    	BaseAttribute other = (BaseAttribute) obj;
    	if (attrId != other.attrId)
    		return false;
    	return true;
    }
}
