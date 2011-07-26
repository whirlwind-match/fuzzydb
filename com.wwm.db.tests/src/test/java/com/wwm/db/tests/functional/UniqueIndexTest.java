package com.wwm.db.tests.functional;

import org.junit.Test;

import com.wwm.db.BaseDatabaseTest;
import com.wwm.db.Ref;
import com.wwm.db.Transaction;
import com.wwm.db.exceptions.KeyCollisionException;
import com.wwm.db.userobjects.SampleUniqueKeyedObject;


public class UniqueIndexTest extends BaseDatabaseTest {

	@Test(expected=KeyCollisionException.class)
	public void twoInsertsAtSameKeyShouldFail() {
		
		Transaction wt = store.getAuthStore().begin();
		Ref<SampleUniqueKeyedObject> ref = wt.create(new SampleUniqueKeyedObject(42, 1));
		Ref<SampleUniqueKeyedObject> ref2 = wt.create(new SampleUniqueKeyedObject(42, 1));
		wt.commit();
	}
}
