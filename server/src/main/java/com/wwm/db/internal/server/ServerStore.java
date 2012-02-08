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
package com.wwm.db.internal.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import org.slf4j.Logger;

import com.wwm.db.core.LogFactory;
import com.wwm.db.internal.RefImpl;
import com.wwm.db.internal.pager.FileSerializingPagePersister;
import com.wwm.io.core.ClassLoaderInterface;
import com.wwm.io.core.ClassTokenCache;
import com.wwm.io.core.impl.DummyCli;
import com.wwm.util.FileFilters;

/**
 * A self-identifying server store.  <code>path</code> identifies where its' persistent data
 * is stored, it's storeName, and unique storeId (if store "firstStore" is created, deleted, 
 * and then re-created then the second one has a different storeId to the first)
 * 
 */
public class ServerStore implements Serializable {
	private static final String FILE_PREFIX = "s";
	private static final long serialVersionUID = 1L;
	private static final Logger log = LogFactory.getLogger(ServerStore.class);

	
//	private ClassLoaderInterface commsClassLoader;
	private final ClassLoaderInterface pagerClassLoader;
	private final ClassTokenCache pagerClassTokenCache;
	
	private final String storeName;

	private String path; // This could be a URL, thus allowing remote/distributed storage variants
	private final int storeId;
	private final Namespaces namespaces;
	private int nextTableId = 0;
	/**
	 * Version at which this store was last saved.  Allows importing of stores to discover
	 * whether we need to advance the dbVersion of the parent repository to ensure this store's
	 *  latest tx's are visible
	 */
	private long savedDbVersion = 0; 
	
	/**
	 * 
	 * @param storageBasePath - disk location, or null if no storage is available
	 */
	public ServerStore(String storageBasePath, String storeName, int storeId) {
		this.storeName = storeName;
		this.storeId = storeId;
		
		if (storageBasePath != null) {
			String diskName = makeDirForNewStore(storeName, storageBasePath);
			this.path = storageBasePath + File.separatorChar + diskName;
		}
		
		namespaces = new Namespaces(this);
		pagerClassTokenCache = new ClassTokenCache(true);
		pagerClassLoader = new DummyCli();
	}

	/**
	 * Creates a new directory, within the basePath's directory, based on the supplied
	 * name.  If a directory of the supplied name already exists, a  unique name 
	 * is generated that doesn't conflict.
	 * @param name
	 * @return path of the directory that was created
	 */
	private String makeDirForNewStore(String name, String basePath) {
		String subDirName;
		File dir = null;
		do {
			subDirName = FileUtil.makeUniqueDiskName( new File(basePath), name);
			dir = new File(basePath, subDirName);
		} while (!dir.mkdirs());
		return subDirName;
	}

	/**
	 * Get Namespace given an object reference.
	 * @param tableId
	 * @return
	 */
	public Namespace getNamespace(RefImpl<?> ref) {
		int tableId = ref.getTable();
		return namespaces.getNamespaceFromTableId(tableId);
	}
	
	public Namespace getNamespace(String name) {
		return namespaces.getNamespace(name);
	}

	public Namespace getCreateNamespace(String name) {
		Namespace namespace = namespaces.getNamespace(name);
		if (namespace == null) {
			return namespaces.createNamespace(name);
		}
		return namespace;
	}
	
	public int nextTableId() {
		return nextTableId++;
	}
	
	public String getPath() {
		return path; 
	}

	public void initTransientData() {
		namespaces.initialise();
	}
	
	public int getStoreId() {
		return storeId;
	}

	public ClassTokenCache getPagerCtc() {
		return pagerClassTokenCache;		
	}
	
	public ClassLoaderInterface getPagerCli() {
		return pagerClassLoader;
	}

	public boolean deletePersistentData() {
		if (path==null) return true; // nothing to delete
		
		if (!namespaces.deletePersistentData()) return false;
		if (!deleteTempDir()) return false;
		
		File storeDir = new File(getPath());
		if (!storeDir.exists()) return true;
		
		// delete remainder of tree.
		if ( !FileUtil.delTree(storeDir) ) return false;
		
//		File temp = new File(getTempName());
//		if (!storeDir.renameTo(temp)) return false;
//		return temp.delete();
		return true;
	}

	private boolean deleteTempDir() {
		File tempDir = new File(getTempName());
		if (!tempDir.exists()) return true;
		return tempDir.delete();
	}
	
	private String getTempName() {
		return getPath() + "_delete_somerandomnamenoonewilltryforreal";
	}

	/**
	 * Return the storeName of this store.  Useful when ServerStore instance
	 * has just been de-serialised from disk.
	 */
	public String getStoreName() {
		return storeName;
	}

	public String[] getNamespaces() {
		return namespaces.getNamespaces();
	}

	void save(long version) throws IOException {
		savedDbVersion = version;
		FileUtil.writeVersionedObject(this, path, FILE_PREFIX, version);
	}

	long getSavedDbVersion() {
		return savedDbVersion;
	}
	
	/**
	 * Load the latest store from the supplied directory.
	 * - Must update path to reflect new path if this in on a different server
	 * @param dir
	 * @param version 
	 * @return ServerStore instance, or null if there was a problem loading the store.
	 */
	static ServerStore tryLoad(File dir, long version) {
		
		File[] files = dir.listFiles( FileFilters.isFile() );
		File fileToUse = getMostRecentMatch(files, FILE_PREFIX + version);
		
		if (fileToUse == null){ // if no match with correct version, assume importing, and get latest.
			fileToUse = getMostRecentMatch(files, FILE_PREFIX);
			if (fileToUse == null){
				return null;
			}
		}
		
		ServerStore store = null;
		try {
			FileInputStream fis = new FileInputStream(fileToUse);
			ObjectInputStream ois = new ObjectInputStream(fis);
			try {
				store = (ServerStore) ois.readObject();
				store.path = dir.getPath(); 
			} finally {
				ois.close();
				fis.close();
			}
		} catch (ClassNotFoundException e) {
			log.error( "Missing class loading store from disk, " + fileToUse.getName() + " :" + e.getMessage() );
			return null;
		} catch (Exception e) {
			log.error( "Unexpected error loading store from disk, " + fileToUse.getName() + " :" + e.getMessage(), e );
			return null;
		}
		// Store loaded.  Now clean up any pages newer than this version
		// FIXME: Need to also assert that there are no stores newer than this version
		FileSerializingPagePersister.recursiveDeleteNewerPages(dir, store.getSavedDbVersion());

		return store;
	}

	private static File getMostRecentMatch(File[] files, String prefixToMatch) {
		File mostRecentMatchingVer = null;
		for (File file : files) {
			if (file.getName().startsWith(prefixToMatch) && 
					(mostRecentMatchingVer == null || mostRecentMatchingVer.lastModified() < file.lastModified()) ){
				mostRecentMatchingVer = file;
			}
		}
		return mostRecentMatchingVer;
	}
	
	@Override
	public String toString() {
		return storeName + " (id=" + storeId + ", ver=" + savedDbVersion + ")";
	}
	
}
