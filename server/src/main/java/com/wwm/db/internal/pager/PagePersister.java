package com.wwm.db.internal.pager;

/**
 * Abstraction of the 'thing' that makes a database durable, or not.
 * 
 * @author Neale Upstone
 */
public interface PagePersister {

	void saveAll();

	void doMaintenance();

	String getPath();

	void ensureCapacity(int numPages);

	void addPurgeablePage(PersistentPagedObject pagedTable, long pageId);

	boolean deleteFromDisk(PersistentPagedObject object);
}
