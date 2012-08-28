package com.wwm.db.tests.functional;

import org.fuzzydb.client.Ref;
import org.fuzzydb.client.Transaction;
import org.fuzzydb.client.exceptions.KeyCollisionException;
import org.fuzzydb.client.userobjects.SampleUniqueKeyedObject;
import org.fuzzydb.server.BaseDatabaseTest;
import org.junit.Test;



public class UniqueIndexTest extends BaseDatabaseTest {

	@Test(expected=KeyCollisionException.class)
	public void twoInsertsAtSameKeyShouldFail() {

		// Simpler case is different transactions - server side check required
		// To enhance for inserts in same transaction, we could check client side while assembling transaction
		{
			Transaction wt = store.getAuthStore().begin();
			Ref<SampleUniqueKeyedObject> ref = wt.create(new SampleUniqueKeyedObject(42, 1));
			wt.commit();
		}
		{
			Transaction wt = store.getAuthStore().begin();
			Ref<SampleUniqueKeyedObject> ref = wt.create(new SampleUniqueKeyedObject(42, 1));
			wt.commit();
		}
	}
}
