

import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.wwm.db.BaseDatabaseTest;
import com.wwm.db.Ref;
import com.wwm.db.Transaction;
import com.wwm.db.spring.transaction.WhirlwindPlatformTransactionManager;
import com.wwm.db.userobjects.TestIndexClass;

public class TransactionTemplateTest extends BaseDatabaseTest {
	

	@Test 
	public void objectCreatedInTransactionTemplateIsFoundInDatabase() {
		
		final WhirlwindPlatformTransactionManager tm = new WhirlwindPlatformTransactionManager(store);

		Ref ref = new TransactionTemplate(tm).execute(new TransactionCallback<Ref>() {
			public Ref doInTransaction(TransactionStatus status) {
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

		Ref ref = null;
		try {
			ref = createOpFollowedByException(tm);
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

	private Ref createOpFollowedByException(
			final WhirlwindPlatformTransactionManager tm) {
		Ref ref = new TransactionTemplate(tm).execute(new TransactionCallback<Ref>() {
			public Ref doInTransaction(TransactionStatus status) {
				tm.getDataOps().create(new TestIndexClass(1));
				throw new RuntimeException("Deliberate exception. Should cause rollback");
			}
		});
		return ref;
	}

}
