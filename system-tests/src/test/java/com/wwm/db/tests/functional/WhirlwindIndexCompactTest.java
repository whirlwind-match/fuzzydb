package com.wwm.db.tests.functional;

import org.fuzzydb.attrs.byteencoding.CompactCardinalAttributeMap;
import org.fuzzydb.test.entities.CompactTestWhirlwindClass;

import com.wwm.db.whirlwind.CardinalAttributeMap;
import com.wwm.db.whirlwind.internal.IAttribute;

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
