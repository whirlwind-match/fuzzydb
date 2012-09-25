package org.fuzzydb.attrs.userobjects;

import java.util.Map;

import org.fuzzydb.core.whirlwind.internal.IAttribute;
import org.fuzzydb.core.whirlwind.internal.IAttributeMap;

public interface MappedItem {

	IAttributeMap<IAttribute> getAttributeMap();

	void setAttributeMap(IAttributeMap<IAttribute> attrs);

	Map<String, String> getNonIndexAttrs();

	void setNonIndexString(String name, String value);

}
