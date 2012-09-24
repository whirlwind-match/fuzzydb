package org.fuzzydb.tests.functional;

import org.bson.types.ObjectId;
import org.fuzzydb.client.Ref;
import org.fuzzydb.client.Transaction;
import org.fuzzydb.client.exceptions.KeyCollisionException;
import org.fuzzydb.client.userobjects.BsonObjectIdUniqueKeyedObject;
import org.fuzzydb.server.BaseDatabaseTest;
import org.junit.Test;



public class BsonObjectIdUniqueIndexTest extends BaseDatabaseTest {

	@Test(expected=KeyCollisionException.class)
	public void twoInsertsAtSameKeyShouldFail() {
		
		ObjectId id = ObjectId.get();

		// Simpler case is different transactions - server side check required
		// To enhance for inserts in same transaction, we could check client side while assembling transaction
		{
			Transaction wt = store.getAuthStore().begin();
			Ref<BsonObjectIdUniqueKeyedObject> ref = wt.create(new BsonObjectIdUniqueKeyedObject(id, 1));
			wt.commit();
		}
		{
			Transaction wt = store.getAuthStore().begin();
			Ref<BsonObjectIdUniqueKeyedObject> ref = wt.create(new BsonObjectIdUniqueKeyedObject(id, 1));
			wt.commit();
		}
	}
}
