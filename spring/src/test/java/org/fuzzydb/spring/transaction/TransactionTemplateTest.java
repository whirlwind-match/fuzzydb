package org.fuzzydb.spring.transaction;


import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.fuzzydb.client.Ref;
import org.fuzzydb.client.Transaction;
import org.fuzzydb.client.userobjects.TestIndexClass;
import org.fuzzydb.spring.transaction.WhirlwindPlatformTransactionManager;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.wwm.db.BaseDatabaseTest;

public class TransactionTemplateTest extends BaseDatabaseTest {
	

	@Test 
	public void objectCreatedInTransactionTemplateIsFoundInDatabase() {
		
		final WhirlwindPlatformTransactionManager tm = new WhirlwindPlatformTransactionManager(store);

		Ref<TestIndexClass> ref = new TransactionTemplate(tm).execute(new TransactionCallback<Ref<TestIndexClass>>() {
			@Override
			public Ref<TestIndexClass> doInTransaction(TransactionStatus status) {
				return tm.getDataOps().create(new TestIndexClass(1));
			}
		});
		
		
		// OLD STYLE - heck, could use @Transactional :)

		Transaction t = store.getAuthStore().begin();
		TestIndexClass retrieved = t.retrieve(TestIndexClass.class, "a", 1);
		
		assertEquals(1, retrieved.a);
		assertEquals(ref, t.getRef(retrieved));
		assertEquals(1, t.getVersion(retrieved));
		
		t.dispose();

	}
	
	@Test 
	public void transactionShouldRollbackOnException() {
		
		final WhirlwindPlatformTransactionManager tm = new WhirlwindPlatformTransactionManager(store);

		try {
			createOpFollowedByException(tm);
			fail(); // Exception should have been thrown
		} catch (Exception e) {
			Assert.assertThat(e.getMessage(), CoreMatchers.equalTo("Deliberate exception. Should cause rollback"));
		}
		
		
		// OLD STYLE - heck, could use @Transactional :)

		Transaction t = store.getAuthStore().begin();
		TestIndexClass retrieved = t.retrieve(TestIndexClass.class, "a", 1);
		
		assertThat(retrieved, nullValue());
		
		t.dispose();

	}

	private Ref<TestIndexClass> createOpFollowedByException(
			final WhirlwindPlatformTransactionManager tm) {
		Ref<TestIndexClass> ref = new TransactionTemplate(tm).execute(new TransactionCallback<Ref<TestIndexClass>>() {
			@Override
			public Ref<TestIndexClass> doInTransaction(TransactionStatus status) {
				tm.getDataOps().create(new TestIndexClass(1));
				throw new RuntimeException("Deliberate exception. Should cause rollback");
			}
		});
		return ref;
	}

}
