package org.fuzzydb.client;

import org.springframework.util.Assert;

/**
 * Our own TransactionTemplate implementation for ensuring that transactions
 * are either committed or disposed().
 * 
 * @author Neale Upstone
 */
public class TransactionTemplate {

	private final Store store;
	
	public TransactionTemplate(Store store) {
		this.store = store;
	}

	public <T> T execute(TransactionCallback<T> callback) {
		Assert.state(store.currentTransaction() == null, "Transaction already in progress. Cannot nest transactions in same thread.");
		Transaction tx = null;
		try {
			tx = store.begin();
			T result = callback.doInTransaction(tx);
			tx.commit();
			return result;
			
		} catch (RuntimeException e) {
			tx.dispose();
			throw e;
		}
	}
	
	

}
