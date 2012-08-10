package org.fuzzydb.client;

public interface TransactionCallback<T> {
	
	T doInTransaction(DataOperations ops);

}
