package com.wwm.db.spring.transaction;

import org.springframework.dao.DataAccessException;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.util.Assert;

import com.wwm.db.DataOperations;
import com.wwm.db.Store;
import com.wwm.db.Transaction;
import com.wwm.db.core.exceptions.ArchException;

public class WhirlwindPlatformTransactionManager extends
		AbstractPlatformTransactionManager {

	private static final long serialVersionUID = 1L;

	private final WhirlwindExceptionTranslator exceptionTranslator = new WhirlwindExceptionTranslator();

	private final Store store;

	/**
	 * Create a transaction manager for this store.
	 * @param store
	 */
	public WhirlwindPlatformTransactionManager(Store store) {
		super();
		this.store = store.getAuthStore();
	}

	@Override
	protected Object doGetTransaction() throws TransactionException {
		// If there isn't a current transaction for this thread then create one
		Transaction current = store.currentTransaction();
		return current != null ? current : store.begin();
	}

	@Override
	protected void doBegin(Object transaction, TransactionDefinition definition)
			throws TransactionException {
		Assert.state(
				definition.getIsolationLevel() == TransactionDefinition.ISOLATION_DEFAULT,
				"Whirlwind only supports ISOLATION_DEFAULT");

		// NOTHING TO DO FOR NOW. Whirlwind starts the transaction lazily
		// We could forceStart() but this would be inappropriate as it would consume server resources
		// earlier than necessary.  Transaction.forceStart() is used mainly for testing.
		
		// At this point we know if the transaction is read-only, so, with some changes to TransactionImpl, could
		// demote to read-only
	}

	@Override
	protected void doCommit(DefaultTransactionStatus status)
			throws TransactionException {

		try {
			Transaction t = (Transaction) status.getTransaction();
			t.commit();
		} catch (ArchException e) {
			throwTranslatedException(e);
		}
	}

	private void throwTranslatedException(ArchException e) {
		DataAccessException dataAccessException = exceptionTranslator
				.translateExceptionIfPossible(e);
		throw (dataAccessException != null) ? dataAccessException : new RuntimeException(e);
	}

	@Override
	protected void doRollback(DefaultTransactionStatus status)
			throws TransactionException {
		Transaction t = (Transaction) status.getTransaction();
		t.dispose();
	}

	public DataOperations getDataOps() {
		return store.currentTransaction();
	}

}
