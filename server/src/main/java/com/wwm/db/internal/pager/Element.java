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
package com.wwm.db.internal.pager;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.fuzzydb.client.exceptions.UnknownObjectException;
import org.fuzzydb.client.marker.MergeableContainer;
import org.fuzzydb.core.whirlwind.internal.AttributeCache;

import com.wwm.db.internal.server.CurrentTransactionHolder;

/**
 * A representation of a table row. This contains a single database object, but it may contain several versions of the
 * same object.
 * 
 * @param <T> what class of object gets stored in the Element
 * 
 */
public class Element<T> implements MergeableContainer, ElementReadOnly<T> {

	/**
	 * Forms one node of a version chain. Each node links to it's older version. The links are one way. Finding a
	 * version requires the chain to be navigated newest to oldest. In most cases chains will be very short so no map or
	 * hash table is provided for accelerating lookups, these will probably slower with most chains being one node long.
	 */
	private static class VersionedObject<V> implements MergeableContainer {

		/**
		 * The version at which this node was created, the object will be visible to transactions of this version or
		 * later.
		 */
		private final long dbversion;

		/**
		 * An optional reference to the older node in the chain. null if this is the last node.
		 */
		private VersionedObject<V> olderVersion;

		/**
		 * The object being wrapped.
		 */
		private final V object;

		/**
		 * Creates a new node, optionally taking an older node to extend a chain
		 * 
		 * @param dbversion
		 *            The version at which this node will become visible
		 * @param olderVersion
		 *            The previous node in the chain, may be null
		 * @param object
		 *            the object to wrap, may be null to indicate a deletion
		 */
		public VersionedObject(long dbversion, VersionedObject<V> olderVersion, V object) {
			super();
			this.dbversion = dbversion;
			this.olderVersion = olderVersion;
			this.object = object;
			this.mergeDuplicates(AttributeCache.getInstance());  // must be done in all constructors
		}

		/**
		 * Gets the version of the object being wrapped.
		 * 
		 * @return The object being wrapped
		 */
		public V getObject() {
			return object;
		}

		/**
		 * Gets the previous node in the version chain. The previous node relates to an older db version. Returns null
		 * if there is no older version.
		 * 
		 * @return The previous node, or null
		 */
		public VersionedObject<V> getOlderVersion() {
			return olderVersion;
		}

		/**
		 * Gets the database version that this version of the object was created for.
		 * 
		 * @return a database version number
		 */
		public long getVersion() {
			return dbversion;
		}

		/**
		 * Clears the reference to the next chain. This causes all older versions of the object to be lost.
		 */
		public void clearOldVersions() {
			olderVersion = null;
		}

		/**
		 * Sets the older version. Used when loading from disk. The existing reference should be unset.
		 * 
		 * @param olderVersion
		 *            the older version of the object
		 */
		public void setOlderVersion(VersionedObject<V> olderVersion) {
			assert (this.olderVersion == null);
			assert (this.dbversion > olderVersion.dbversion);
			this.olderVersion = olderVersion;
		}

		public void mergeDuplicates(AttributeCache cache) {
			if (object instanceof MergeableContainer){
				((MergeableContainer)object).mergeDuplicates(cache);
			} 
			// FIXME: Also deal with other mergeables, such as common strings, but not triggered by java.lang.String
		}
	}

	private final long oid;

	private VersionedObject<T> currentVersion;

	/**
	 * @param oid
	 * @param object
	 */
	public Element(long oid, T object) {
		long dbversion = CurrentTransactionHolder.getCommitVersion();
		this.oid = oid;
		currentVersion = new VersionedObject<T>(dbversion, null, object);
	}

	private Element(long oid, VersionedObject<T> vo) {
		this.oid = oid;
		currentVersion = vo;
	}

	/* (non-Javadoc)
	 * @see org.fuzzydb.client.internal.pager.ElementReadOnly#writeToStream(java.io.ObjectOutputStream)
	 */
	public void writeToStream(ObjectOutputStream oos) throws IOException {
		// Stream format
		// int - number of versions
		// long - version of first object
		// Object - first object
		// (repeat) long - version of second object etc.
		int count = 0;
		VersionedObject<T> ptr = currentVersion;
		while (ptr != null) {
			count++;
			ptr = ptr.getOlderVersion();
		}
		assert (count > 0); // Should not be writing if there is nothing to
		// write!
		oos.writeInt(count);
		ptr = currentVersion;
		while (ptr != null) {
			oos.writeLong(ptr.getVersion());
			oos.writeObject(ptr.getObject());
			ptr = ptr.getOlderVersion();
		}
	}

	/**
	 * Serialises an element from an ObjectStream.
	 * 
	 * @param ois
	 *            The stream to serialise from.
	 * @param oid
	 *            The oid of the element. Oids are not stored on disk for efficiency reasons.
	 * @return A new Element
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked") //for ois.readObject()
	public static <E> Element<E> readFromStream(ObjectInputStream ois, long oid) throws IOException, ClassNotFoundException {
		int count = ois.readInt();

		// Read first
		long version = ois.readLong();
		E o = (E) ois.readObject();
		VersionedObject<E> current = new VersionedObject<E>(version, null, o);
		Element<E> element = new Element<E>(oid, current);

		for (int i = 1; i < count; i++) {
			version = ois.readLong();
			o = (E) ois.readObject();
			VersionedObject<E> next = new VersionedObject<E>(version, null, o);
			current.setOlderVersion(next);
			current = next;
		}

		return element;
	}

	/**
	 * Causes all old versions older than the specified parameter to be discarded.
	 * 
	 * @param preserveVersion
	 *            The version to be preserved.
	 */
	public void flushOldVersions(long preserveVersion) {
		VersionedObject<T> vo = currentVersion;
		for (;;) {
			if (vo == null)
				return;
			if (vo.getVersion() < preserveVersion) {
				vo.clearOldVersions();
				return;
			}
			vo = vo.getOlderVersion();
		}
	}

	/**
	 * Adds a new version to the version chain. The transaction associated with the calling thread must be able to see
	 * the head of the chain.
	 * 
	 * @param object
	 *            The object to add as the latest version. May not be null. To perform a deletion use delete()
	 * @see delete()
	 */
	public void addVersion(T object) {
		assert (object != null);
		assert (canSeeLatest());
		long dbversion = CurrentTransactionHolder.getCommitVersion();
		if (currentVersion != null && currentVersion.dbversion == dbversion) {
			currentVersion = currentVersion.olderVersion;
		}
		currentVersion = new VersionedObject<T>(dbversion, currentVersion, object);
		flushOldVersions(CurrentTransactionHolder.getTransaction().getOldestDbVersion());
	}

	/* (non-Javadoc)
	 * @see org.fuzzydb.client.internal.pager.ElementReadOnly#canSeeLatest()
	 */
	public boolean canSeeLatest() {
		if (currentVersion == null)
			return false; // no chain - deleted?
		if (currentVersion.getObject() == null)
			return false; // deleted node
		long dbversion = CurrentTransactionHolder.getVisibleVersion();
		return (currentVersion.getVersion() <= dbversion);
	}

	/**
	 * Adds a deleted node to the head fo the version chain. The transaction associated with the thread must be able to
	 * see the existing head.
	 * 
	 * @see canSeeLatest()
	 */
	public void delete() {
		assert (canSeeLatest());
		long dbversion = CurrentTransactionHolder.getCommitVersion();
		if (currentVersion != null) {
			if (currentVersion.dbversion == dbversion) {
				currentVersion = currentVersion.olderVersion;
			}
			currentVersion = new VersionedObject<T>(dbversion, currentVersion, null);
		}
	}

	/* (non-Javadoc)
	 * @see org.fuzzydb.client.internal.pager.ElementReadOnly#getVersion()
	 */
	public T getVersion() throws UnknownObjectException {
		long dbversion = CurrentTransactionHolder.getVisibleVersion();
		VersionedObject<T> vo = currentVersion;
		for (;;) {
			if (vo == null) {
				throw new UnknownObjectException(); // loop runs off the end of
				// the chain if the object
				// was created after the
				// transaction
			}
			if (vo.getVersion() <= dbversion) {
				T o = vo.getObject(); // may be null if deleted
				if (o == null) {
					throw new UnknownObjectException();
				}
				return o;
			}
			vo = vo.getOlderVersion();
		}
	}

	/* (non-Javadoc)
	 * @see org.fuzzydb.client.internal.pager.ElementReadOnly#isDeleted()
	 */
	public boolean isDeleted() {
		if (currentVersion == null)
			return true;
		if (currentVersion.getObject() == null && currentVersion.getOlderVersion() == null)
			return true;
		return false;
	}

	/* (non-Javadoc)
	 * @see org.fuzzydb.client.internal.pager.ElementReadOnly#getOid()
	 */
	public long getOid() {
		return oid;
	}
	
	/* (non-Javadoc)
	 * @see org.fuzzydb.client.internal.pager.ElementReadOnly#getLatestVersion()
	 */
	public long getLatestVersion() {
		return currentVersion.getVersion();
	}

	/**
	 * This is done at Element, as it's the container for many objects that we store in the database
	 */
	public void mergeDuplicates(AttributeCache cache) {
		this.currentVersion.mergeDuplicates(cache);
		
	}
}
