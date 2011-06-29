package com.wwm.db;

public interface TransactionCallback<T> {
	
	T doInTransaction(DataOperations ops);

}
