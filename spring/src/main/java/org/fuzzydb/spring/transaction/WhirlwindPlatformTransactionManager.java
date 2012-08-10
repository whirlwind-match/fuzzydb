package org.fuzzydb.spring.transaction;

import org.fuzzydb.client.DataOperations;
import org.fuzzydb.client.Store;
import org.fuzzydb.client.Transaction;
import org.fuzzydb.core.exceptions.ArchException;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.support.SmartTransactionObject;
import org.springframework.util.Assert;


public class WhirlwindPlatformTransactionManager extends
		AbstractPlatformTransactionManager {

	private class TransactionHolder implements SmartTransactionObject {

		private Transaction transaction;

		private boolean rollbackOnly = false;
		
		public TransactionHolder(Transaction currentTransaction) {
			this.transaction = currentTransaction;
		}

		public Transaction getTransaction() {
			return transaction;
		}

		public void setTransaction(Transaction transaction) {
			this.transaction = transaction;
		}

		public void setRollbackOnly() {
			this.rollbackOnly = true;
		}
		
		@Override
		public boolean isRollbackOnly() {
			return rollbackOnly;
		}

		@Override
		public void flush() {
			// do nothing
		}
	}

	
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
		return new TransactionHolder(store.currentTransaction()); 
	}

	@Override
	protected void doBegin(Object transaction, TransactionDefinition definition)
			throws TransactionException {
		Assert.state(
				definition.getIsolationLevel() == TransactionDefinition.ISOLATION_DEFAULT,
				"Whirlwind only supports ISOLATION_DEFAULT");

		
		TransactionHolder th = (TransactionHolder)transaction;
		Assert.isNull(th.getTransaction());
		th.setTransaction(store.begin());
	}

	@Override
	protected void doCommit(DefaultTransactionStatus status)
			throws TransactionException {

		try {
			TransactionHolder th = (TransactionHolder) status.getTransaction();
			if (th.isRollbackOnly()) {
				th.getTransaction().dispose();
			}
			else {
				th.getTransaction().commit();
			}
			
			th.setTransaction(null);
		} catch (ArchException e) {
			throwTranslatedException(e);
		}
	}
	
	@Override
	protected boolean isExistingTransaction(Object transaction) throws TransactionException {
		TransactionHolder th = (TransactionHolder)transaction;
		return th.getTransaction() != null && th.getTransaction().equals(store.currentTransaction());
	}

	private void throwTranslatedException(ArchException e) {
		DataAccessException dataAccessException = exceptionTranslator
				.translateExceptionIfPossible(e);
		throw (dataAccessException != null) ? dataAccessException : e;
	}

	@Override
	protected void doRollback(DefaultTransactionStatus status)
			throws TransactionException {
		TransactionHolder th = (TransactionHolder)status.getTransaction();
		th.getTransaction().dispose();
		th.setTransaction(null);
	}

	public DataOperations getDataOps() {
		return store.currentTransaction();
	}

	@Override
	protected void doSetRollbackOnly(DefaultTransactionStatus status)
			throws TransactionException {
		TransactionHolder th = (TransactionHolder)status.getTransaction();
		th.setRollbackOnly();
	}
}
