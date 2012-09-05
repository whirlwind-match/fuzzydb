package org.fuzzydb.tests.functional;

import org.fuzzydb.attrs.byteencoding.CompactCardinalAttributeMap;
import org.fuzzydb.client.whirlwind.CardinalAttributeMap;
import org.fuzzydb.core.whirlwind.internal.IAttribute;
import org.fuzzydb.test.entities.CompactTestWhirlwindClass;


public class WhirlwindIndexCompactTest extends BaseWhirlwindCrudTest<CompactTestWhirlwindClass> {
	
	public WhirlwindIndexCompactTest() {
		super(CompactTestWhirlwindClass.class, "compact");
	}

	@Override
	protected CardinalAttributeMap<IAttribute> createAttributeMap() {
		return new CompactCardinalAttributeMap();
	}

	@Override
	protected CompactTestWhirlwindClass createEntry(float value) {
		return new CompactTestWhirlwindClass(floatId, value);
	}	
}
