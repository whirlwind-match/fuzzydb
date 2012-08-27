package org.fuzzydb.server.internal.common;

public interface InitializingBean {

	/**
	 * Callback for initialising objects with transient data and services 
	 * when those objects have been newly created or materialised from
	 * persistent storage.<br>
	 * This should be used to initialise (wire-in) services and transient data.  
	 */
	void initialise();

}