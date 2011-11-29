package com.wwm.db.tests.functional;

import com.wwm.attrs.AttrsFactory;
import com.wwm.attrs.userobjects.TestWhirlwindClass;
import com.wwm.db.whirlwind.CardinalAttributeMap;
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
