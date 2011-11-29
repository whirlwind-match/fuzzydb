package com.wwm.attrs.userobjects;

import com.wwm.attrs.simple.FloatValue;
import com.wwm.db.marker.IWhirlwindItem;

abstract public class AugmentedAttributeMap implements IWhirlwindItem {

	public AugmentedAttributeMap() {
		super();
	}

	public void setFloat(int attrId, float f) {
		getAttributeMap().put(attrId, new FloatValue(attrId, f));
	}

	public Object getFloat(int attrId) {
		FloatValue attr = (FloatValue) getAttributeMap().findAttr(attrId);
		return attr.getValue();
	}

}