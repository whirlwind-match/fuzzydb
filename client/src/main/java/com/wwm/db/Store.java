/******************************************************************************
 * Copyright (c) 2005-2008 Whirlwind Match Limited. All rights reserved.
 *
 * This is open source software; you can use, redistribute and/or modify
 * it under the terms of the Open Software Licence v 3.0 as published by the 
 * Open Source Initiative.
 *
 * You should have received a copy of the Open Software Licence along with this
 * application. if not, contact the Open Source Initiative (www.opensource.org)
 *****************************************************************************/
package com.wwm.db;

import java.util.Collection;
import org.slf4j.Logger;

/**This class is used to access Stores.
 * Note that it implements the Transaction interface. Any transactions started on this Store are associated with the calling thread.
 * The last transaction initiated by a given thread may always be acccessed through the Store's Transaction methods.
 * @author ac
 *
 */
public interface Store extends DataOperations, Helper {

	/**Begins a new Transaction.
	 * The actual transaction is not guaranteed to begin until data is read by the application, or write transactions are comitted.
	 * The Transaction is Authoritative if this Store is Authoritative.
	 * The Transaction is associated with the calling thread, so the caller need not store the returned transaction.
	 * Note that this Store extends the transaction methods. Calling the transaction methods on Store will call the methods on the transaction
	 * associated with the calling thread, that was started with this method.
	 * @return the Transaction
	 * @see currentTransaction()
	 */
	Transaction begin();
	
	/**Return the current, active transaction for this Store associated with this thread.
	 * If there is no transaction yet, this method returns null.
	 * If the transaction was committed or disposed, it is still returned.
	 * Switching between Authoritative and Non-Authoritative views does not stop this method returning the current transaction. 
	 * @return the current transaction for the calling thread, or null if the thread has not started a transaction yet.
	 */
	Transaction currentTransaction();
	
	/**Set the default namespace for new transactions created by this Store. Existing transactions are unaffected.
	 * This sets the default namespace for all threads using this Store object.
	 * The namespace a Transaction is using can be changed at any time on the Transaction.
	 * FIXME: (nu->ac) Review whether this is 'safe' given that a Store object can be passed around (e.g. via StoreMgr)
	 *        Should we instead have a client side 'Namespace' object?  or Client.getStore(namespace), so that the namespace is immutable for the store. 
	 * @param namespace
	 */
	void setDefaultNamespace(String namespace);
	
	/**Returns an Authoritative version of this Store.
	 * This is guaranteed to be a low cost operation, the intended use is for applications to toggle between authoritative and non-authoritative Store views with this function.
	 * If this store is already Authoritative, it safely returns a reference to this store.
	 * @return the Authoritative store view.
	 */
	Store getAuthStore();
	
	/**Returns a Non-Authoritative version of this Store.
	 * This is guaranteed to be a low cost operation, the intended use is for applications to toggle between authoritative and non-authoritative Store views with this function.
	 * If this store is already Non-Authoritative, it safely returns a reference to this store.
	 * @return the Non-Authoritative store view.
	 */
	Store getNonAuthStore();
	
	/**Get the name of this store
	 * @return This store name
	 */
	String getStoreName();
	
	Collection<Class<?>> getDbClasses(); // returns a list of all classes stored in the DB
	Class<?> getDbClass(String className); // if you know the string but not the class, this lets you get the class (bytecode download) - this represents a new feature I hadn't considered. It's obviously up to the app to store it or reflectively access its members etc. This is easy enough to do, and actually is a good idea becuase the class upload thing was indroducing asymetry into the new coms design which I didn't like, this actually smplifies things a bit.
	Collection<String> getNamespaces(Class<?> dbClass); // Gets all namespaces in which the specified class appears.

	// FIXME: ================== REVIEW IF WE WANT THESE ====================
	void tron(Logger log);

	void troff();
	
}
