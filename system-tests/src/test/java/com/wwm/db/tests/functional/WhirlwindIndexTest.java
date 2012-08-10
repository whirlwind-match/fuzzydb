package com.wwm.db.tests.functional;

import org.fuzzydb.attrs.AttrsFactory;
import org.fuzzydb.attrs.userobjects.TestWhirlwindClass;
import org.fuzzydb.client.whirlwind.CardinalAttributeMap;

import com.wwm.db.whirlwind.internal.IAttribute;

public class WhirlwindIndexTest extends BaseWhirlwindCrudTest<TestWhirlwindClass> {
	
	public WhirlwindIndexTest() {
		super(TestWhirlwindClass.class, "default");
	}

	@Override
	protected CardinalAttributeMap<IAttribute> createAttributeMap() {
		return AttrsFactory.getCardinalAttributeMap();
	}

	@Override
	protected TestWhirlwindClass createEntry(float value) {
		return new TestWhirlwindClass(floatId, value);
	}	
}
