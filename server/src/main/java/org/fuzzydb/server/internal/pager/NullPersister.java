package org.fuzzydb.server.internal.pager;

/**
 * A persister that doesn't.
 * 
 * @author Neale Upstone
 *
 */
public class NullPersister implements PagePersister {


	public NullPersister() {
	}
	
	public void saveAll() {
		// Do nothing
	}

	public void doMaintenance() {
		// Do nothing
	}

	public String getPath() {
		// TODO: get rid of concept of a path as it's disk specific and all over the place
		// Return a relative path we'd want to delete 
		return null;
	}

	public void ensureCapacity(int numPages) {
		// TODO Auto-generated method stub
		
	}

	public void addPurgeablePage(PersistentPagedObject pagedTable, long pageId) {
		// TODO Auto-generated method stub
		
	}

	public boolean deleteFromDisk(PersistentPagedObject object) {
		// TODO Auto-generated method stub
		return false;
	}

}
