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
package org.fuzzydb.server.internal.pager;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.fuzzydb.client.exceptions.UnknownObjectException;
import org.fuzzydb.server.internal.common.ServiceRegistry;
import org.fuzzydb.server.internal.pager.Page.PagePurgedException;
import org.fuzzydb.server.internal.server.Database;
import org.fuzzydb.server.internal.server.DatabaseVersionState;
import org.fuzzydb.server.internal.server.FileUtil;
import org.fuzzydb.server.internal.server.Namespace;
import org.fuzzydb.server.internal.server.ServerStore;
import org.fuzzydb.server.internal.table.RawTable;


/**
 * (NU) An implementation of {@link RawTable} that stores it's {@link Element} in {@link Page}s
 * managed by a {@link FileSerializingPagePersister}  
 * 
 * @param <T> what gets stored in the Elements
 */
public class RawPagedTableImpl<T> implements RawTable<T>, Serializable, PersistentPagedObject, PagerContext {

	private static final long serialVersionUID = 1L;
	protected final Class<?> forClass;
	protected final Namespace namespace;
	private final String nameSuffix;

	static private final int dirLevels = 3;
	static private final int filesPerDir = 100;
	static private final int elementsPerPage = 20; // Was 100

	static private long modulus;
	static {
		setModulus();
	}

	private final AtomicLong nextOid = new AtomicLong(0);

	private transient PagePersister pager;
	private transient TimeHistory loadTime;
	private transient TimeHistory saveTime;
	private transient Map<Long, Page<T>> pages;
	private transient String path;
	private transient DatabaseVersionState databaseVersionState;

	public RawPagedTableImpl(Namespace namespace, Class<?> forClass) {
		this(namespace, forClass, "");
	}

	/**
	 * @param namespace
	 * @param forClass
	 * @param nameSuffix
	 *            suffix to append to end of name when creating persistentStore.
	 *            This is used where multiple tables for the same type are
	 *            required in the same namespace.
	 */
	public RawPagedTableImpl(Namespace namespace, Class<?> forClass, String nameSuffix) {
		this.namespace = namespace;
		this.forClass = forClass;
		this.nameSuffix = nameSuffix;
		initPath();
	}

	
	/**
	 * When reading, calculate derived stuff.
	 */
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
	}

	public void initialise() {
		if (pages != null) {
			return; // already initialised - we might get called from multiple
					// directions!!
			// FIXME: I think we should get rid of public initialise() and
			// internally lazy init.
		}
		
		this.pager = ServiceRegistry.getService(PagePersister.class); 
		this.databaseVersionState = ServiceRegistry.getService(Database.class).getTransactionCoordinator();
		pages = new HashMap<Long, Page<T>>();
		loadTime = new TimeHistory();
		saveTime = new TimeHistory();
	}

	private void initPath() {
		path = namespace.getPath() + File.separatorChar + forClass.getName() + nameSuffix;
	}
	
	public boolean deletePersistentData() {
		return pager.deleteFromDisk(this);
	}

	public Namespace getNamespace() {
		return namespace;
	}

	public Class<?> getStoredClass() {
		return forClass;
	}

	public boolean deleteFromStorage() {
		return FileUtil.delTree( new File( getPath() ) );
	}
	
	public int getStoreId() {
		return namespace.getStoreId();
	}

	public ElementReadOnly<T> lockElementForRead(long elementId) throws UnknownObjectException {
		long pageId = elementId / elementsPerPage;
		Page<T> page = lockPage(pageId, false);
		ElementReadOnly<T> element = null;
		try {
			element = page.getElementForRead(elementId);
		} catch (IOException e) {
			throw new RuntimeException("Error loading element", e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Error loading element", e);
		}
		if (element == null) {
			page.releaseRead();
			throw new UnknownObjectException("Element: " + elementId + " in " + path);
		}
		return element;
	}

	public Element<T> lockElementForWrite(long elementId) throws UnknownObjectException {
		long pageId = elementId / elementsPerPage;
		Page<T> page = lockPage(pageId, true);
		Element<T> element = null;
		try {
			element = page.getElementForWrite(elementId);
		} catch (IOException e) {
			throw new RuntimeException("Error loading element", e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Error loading element", e);
		}
		if (element == null) {
			page.releaseWrite();
			throw new UnknownObjectException();
		}
		return element;
	}

	public void unlockElementForRead(ElementReadOnly<T> element) {
		assert (element != null);
		long pageId = element.getOid() / elementsPerPage;
		Page<T> page = pages.get(pageId);
		assert (page != null); // It can't get purged while it's locked for
		// access!
		page.releaseRead();
	}

	public void unlockElementForWrite(ElementReadOnly<T> element) {
		assert (element != null);
		long pageId = element.getOid() / elementsPerPage;
		Page<T> page = pages.get(pageId);
		assert (page != null); // It can't get purged while it's locked for
		// access!
		page.releaseWrite();
	}

	public float calculatePurgeCost(long pageId) {
		Page<T> page = pages.get(pageId);

		// Cost formula.
		// The probability of needing to load the page is proportional to its
		// access frequency.
		// All write accesses read the page first so the read access frequency
		// has the write access counts added in.
		// If the page is seldom accessed for write the cost of purging it is
		// reduced as we rarely have to write the page to disk.
		// cost is loadTime * readFreq + saveTime * writeFreq
		// The readFreq includes write accesses which are always read-write.

		float cost = (loadTime.getAvgTime()) * page.getAccessFreq(false);

		if (page.isDirty()) {
			float dirtyCost = saveTime.getAvgTime() * page.getAccessFreq(true);
			cost += dirtyCost == 0 ? 1 : dirtyCost;
		}

		/*
		 * float serializationTime = saveTime.getAvgTime(); float seekTime =
		 * loadTime.getAvgTime(); float readFreq = page.getAccessFreq(false);
		 * float writeFreq = page.getAccessFreq(true); float accessFreq =
		 * readFreq + writeFreq;
		 * 
		 * float readCost = accessFreq * (seekTime + serializationTime);
		 * 
		 * float writeCost = 0; if (page.isDirty()) { writeCost = writeFreq *
		 * (serializationTime); if (writeCost == 0) writeCost = 1; }
		 * 
		 * float cost = readCost + writeCost;
		 */
		float timeBias = page.getCostBias() * 10 + 1;

		return cost * timeBias;
	}

	public void createElement(Element<T> element) {
		long oid = element.getOid();
		long pageId = oid / elementsPerPage;

		Page<T> page = lockPage(pageId, true);
		page.setElement(oid, element);
		page.releaseWrite();
	}

	public boolean doesElementExist(long elementId) {
		long pageId = elementId / elementsPerPage;
		Page<T> page = lockPage(pageId, false);
		boolean rval = page.doesElementExist(elementId);
		page.releaseRead();
		return rval;
	}

	/**
	 * Why is locking of a page done in one paging implementation. It's common to any paging requirement
	 */
	protected Page<T> lockPage(long pageId, boolean forWrite) {
		for (;;) {
			try {
				boolean load = false;
				Page<T> page = null;
				// get the lock on the pages collection
				synchronized (pages) {
					page = pages.get(pageId);
					if (page == null) {
						pager.ensureCapacity(1);
						// page needs loading!
						// Create an empty page and lock it to prevent other
						// threads from trying to load it
						page = Page.blankPage(elementsPerPage, getPathForPage(pageId), this, databaseVersionState, pageId
								* elementsPerPage);
						pages.put(pageId, page);
						try {
							page.acquireWrite();
						} catch (PagePurgedException e) {
							throw new RuntimeException(e); // It can't get purged yet, as
							// the page manager doesn't know
							// about it
						}
						load = true; // we need to load it outside the
						// synchronized block to allow for
						// multithreaded io
					}
				}
				if (load) {
					loadPage(page);
					try {
						if (!forWrite) {
							page.releaseWriteAcquireRead();
						}
					} catch (PagePurgedException e) {
						throw new RuntimeException(e); // It can't get purged yet, as the
						// pager doesn't know about it
					}
					pager.addPurgeablePage(this, pageId);
				} else {
					// page was already loaded, just get the lock
					if (forWrite) {
						page.acquireWrite(); // Throws PagePurgedException,
						// go around again
					} else {
						page.acquireRead(); // Throws PagePurgedException, go
						// around again
					}
				}

				page.accessed(forWrite);
				return page;
			} catch (PagePurgedException e) {
				// Can happen if a page gets purged while we are waiting for a
				// lock. Just go round the loop again and reload it
			}
		}
	}

	/**
	 * Load the page and add time taken to loadTime
	 */
	private void loadPage(Page<T> page) {
		long start = System.currentTimeMillis();
		boolean loaded = page.load();
		if (loaded) {
			long duration = System.currentTimeMillis() - start;
			loadTime.time(duration);
		}
	}

	public void savePage(Long pageId) throws PagePurgedException {
		savePageInternal(pageId, false);
	}
	
	public boolean tryPurgePage(long pageId) throws PagePurgedException {
		return savePageInternal(pageId, true);
	}
	
	/**
	 * Save a page to disk, with option to also flush it from memory.  Add time taken to saveTime.
	 * @param pageId
	 * @param flushPageFromMemory - in addition to saving the page, also purge it from memory
	 * @return true if page exists, and it purged/saved successfully
	 * @throws PagePurgedException
	 */
	private boolean savePageInternal(long pageId, boolean flushPageFromMemory) throws PagePurgedException {
		boolean locked = false;
		Page<T> page = null;
		synchronized (pages) {
			page = pages.get(pageId);

			if (page == null) {
				// This should be only when we have not paged it in, so why would we need to page it out!
				return false;
			}
			locked = page.tryAcquireWrite();
			assert locked : "should have locked";
		}

		if (page.isDirty()) {
			long start = System.currentTimeMillis();
			try {
				page.save(flushPageFromMemory);
			} catch (IOException e) {
				// severe error, cant save page
				throw new RuntimeException("Error writing page", e);
			}
			long duration = System.currentTimeMillis() - start;
			saveTime.time(duration);
		}

		// If option to flush is true, then set purged, and remove it from pages map
		if (flushPageFromMemory) {
			synchronized (pages) {
				pages.remove(pageId);
			}
		}
		page.releaseWrite();
		return locked;
	}

	public boolean flushOldVersions(HashSet<Long> pageIds) throws PagePurgedException {

		boolean flushedAll = true;

		for (Long pageId : pageIds) {
			boolean locked = false;
			Page<T> page = null;
			synchronized (pages) {
				page = pages.get(pageId);

				if (page == null) {
					flushedAll = false;
					continue; // FIXME: Think this should be an error
				}
				locked = page.tryAcquireWrite();
			}
			if (locked) {
				if (page.isDirty()) {
					page.flushOldVersions();
				}
				page.releaseWrite();
			} else {
				flushedAll = false; // FIXME: Think should be an error...
			}
		} // end foreach pageIds

		return flushedAll;
	}

	public String getPath() {
		if (path == null) {
			initPath();
		}
		return path;
	}
	
	/**
	 * Generates the full on-disk pathname of the page file for a given oid.
	 * This is incomplete - The db version extension is missing.
	 * 
	 * @param oid
	 * @return
	 */
	private String getPathForPage(long pageId) {
		
		long id = pageId;
		StringBuilder rval = new StringBuilder( getPath() );
		long mod = modulus;

		do {
			rval.append(File.separatorChar);
			rval.append(id / mod);
			id %= mod;
			mod /= filesPerDir;
		} while (mod > 1);
		rval.append(File.separatorChar);
		rval.append('p');
		rval.append(pageId);
		return rval.toString();
	}

	@Override
	public String toString() {
		return forClass.getName() + nameSuffix;
	}

	public long allocNewIds(int count) {
		return nextOid.getAndAdd(count);
	}

	// private Long allocOneRefNear(long oid) {
	// long pageId = oid / elementsPerPage;
	// Page page = lockPage(pageId, false);
	//
	// Long rval = page.allocNextFreeId();
	// page.releaseRead();
	//
	// if (rval != null) {
	// if (rval == nextOid) {
	// nextOid++;
	// }
	// return rval;
	// }
	// return null;
	// }

	public long allocOneRefNear(long nearOid, long[] others) {
		return allocOneRef();
		/*
		 * Long rval; rval = allocOneRefNear(nearOid); if (rval != null) return
		 * rval;
		 * 
		 * if (others != null) { for (int i = 0; i < others.length; i++) { rval =
		 * allocOneRefNear(others[i]); if (rval != null) return rval; } }
		 * 
		 * 
		 * 
		 * nextOid += elementsPerPage; // Move nextOid to start of a new page
		 * nextOid -= nextOid % elementsPerPage;
		 * 
		 * long newRef = nextOid;
		 * 
		 * long pageId = nextOid / elementsPerPage; nextOid++; Page page =
		 * lockPage(pageId, true); page.setNextFreeId(nextOid);
		 * 
		 * page.releaseWrite();
		 * 
		 * nextOid += elementsPerPage; // Move nextOid to start of a new page
		 * nextOid -= nextOid % elementsPerPage;
		 * 
		 * return newRef;
		 */

	}

	public long allocOneRef() {
		return allocNewIds(1);
	}

	public long getNextOid() {
		return nextOid.get();
	}

	static private void setModulus() {
		long mod = filesPerDir;

		// calculate pow(filesPerDir, dirLevels)
		for (int i = 1; i < dirLevels; i++) {
			mod *= filesPerDir;
		}

		modulus = mod;
	}

	public ServerStore getStore() {
		return getNamespace().getNamespaces().getStore();
	}
}
