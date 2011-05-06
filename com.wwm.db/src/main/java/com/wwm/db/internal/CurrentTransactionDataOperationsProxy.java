package com.wwm.db.internal;

import com.wwm.db.DataOperations;
import com.wwm.db.Store;

public class CurrentTransactionDataOperationsProxy extends
		AbstractDataOperationsProxy {


	private Store store;

	public CurrentTransactionDataOperationsProxy(Store store) {
		this.store = store;
	}
	
	@Override
	protected DataOperations getDataOperations() {
		return store.currentTransaction();
	}

}
