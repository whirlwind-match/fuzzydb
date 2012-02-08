package com.wwm.db.internal.server;

public interface RepositoryStorageManager {

	/**
	 * Initialise a repository either by loading one or creating one. 
	 * <b>Should always creates a new in memory one if not persistent</b>
	 */
	void loadOrCreateRepositoryAsNeeded();

	Repository getRepository();

	void doMaintenance();

	/**
	 * Should return to the state before initialise() was called
	 */
	void shutdown();

}