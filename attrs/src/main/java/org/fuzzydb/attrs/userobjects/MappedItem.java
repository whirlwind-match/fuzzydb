package org.fuzzydb.attrs.userobjects;

import java.util.Map;

import org.fuzzydb.core.marker.IAttributeContainer;
import org.fuzzydb.core.whirlwind.internal.IAttribute;
import org.fuzzydb.core.whirlwind.internal.IAttributeMap;

public interface MappedItem extends IAttributeContainer {

	void setAttributeMap(IAttributeMap<IAttribute> attrs);

	Map<String, String> getNonIndexAttrs();

	void setNonIndexString(String name, String value);
	
	/**
	 * Copy all fields in from supplied item
	 * TODO: Find a quick path for doing this
	 */
	<T extends MappedItem> void mergeFrom(T item);

}
