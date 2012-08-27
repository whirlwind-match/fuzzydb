package org.fuzzydb.server.internal.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * One that does nothing (i.e. when we're not persisting anything)
 */
public class NullRepositoryStorageManager implements RepositoryStorageManager {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private volatile Repository repository;
	
	public void doMaintenance() {
	}
	
	public Repository getRepository() {
		return repository;
	}

	public Repository loadOrCreateRepositoryAsNeeded() {
		repository = new Repository();
		log.info("Empty repository created, as not using persistence");
		return repository;
	}
	
	public void shutdown() {
		repository = null;
	}
}
