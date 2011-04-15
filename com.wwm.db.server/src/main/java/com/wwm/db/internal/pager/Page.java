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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.BitSet;
import java.util.logging.Logger;

import com.wwm.db.core.LogFactory;
import com.wwm.db.internal.server.CurrentTransactionHolder;
import com.wwm.db.internal.server.DatabaseVersionState;
import com.wwm.db.internal.server.ServerStore;
import com.wwm.db.internal.server.TransactionControl;
import com.wwm.db.internal.server.WorkerThread;
import com.wwm.io.packet.ArchInStream;
import com.wwm.io.packet.ArchOutStream;
import com.wwm.util.MeteredOutputStream;

// Page format is:
// The number of Elements (rows) on a page is always known beforehand, call this 'n'
// The first 'n' ints of the byte array are the offsets into the rest of the array that each Element exists at

/**
 * @author ac
 */
public class Page<T> implements FilenameFilter {

	@SuppressWarnings("unused")
	static private final Logger log = LogFactory.getLogger(Page.class);
	
	/**
	 * Exception throw when we try to do an operation on a page that the Pager has
	 * purged.  If we get this, we should discard the reference, and try to re-load the page.
	 */
	@SuppressWarnings("serial")
	public static class PagePurgedException extends Exception {
	}

	static private final byte eightZeros[] = {0,0,0,0, 0,0,0,0};

	private final String path;

	private final String filterString;

	/** Data loaded from disk.  If it is null, after load(), it indicates that this page does not yet have an on disk version */
	private byte[] pageData = null;

	private final Element<T>[] elements;

	private final ExclusiveWrite exclusiveWrite;

	private boolean purged = false;

	private final long offset;

	private final int length;

	private long latestDbVersion = -1L;

	private boolean dirty = false;

	private final AccessHistory accessedForRead = new AccessHistory();

	private final AccessHistory accessedForWrite = new AccessHistory();

	private final ServerStore store;

	private final long loadedTime = System.currentTimeMillis();

	private static final long timeBias = 2000;

	static private final char VERSION_SEPARATOR = '_';
	
	private BitSet modifiedFlags;
	
	private boolean disableDelete;

	private DatabaseVersionState vp;

	
	/**
	 * Creates a page object which is mapped to a given path (assumed to be on disk, but there's no
	 * reason why this 'path' could not be a URI within a storage cloud of some sort).
	 * The created page is originally empty, even if there is page data on disk, and it is populated with data
	 * from the disk when the first elements are accessed.
	 */
	public static <P> Page<P> blankPage(int length, String path, PagerContext context, DatabaseVersionState vp, long offset) {
		return new Page<P>(length, path, context, vp, offset);
	}

	
	
	@SuppressWarnings("unchecked")
	private Page(int length, String path, PagerContext context, DatabaseVersionState vp, long offset) {
		this.length = length;
		this.path = path;
		this.vp = vp;
		this.elements = new Element[length];
		exclusiveWrite = new ExclusiveWrite();
		this.offset = offset;
		this.filterString = path.substring(path.lastIndexOf(File.separatorChar) + 1) + VERSION_SEPARATOR;
		this.store = context.getStore();
		this.modifiedFlags = new BitSet(length);
		
		this.disableDelete = path.contains("@") && !path.contains("@Leaves"); // the only index pages (WW & Btree) pages we delete from are WWIndex LeafNodes
	}

	/**
	 * @return 0 if page is old, 1 if page is new, sliding scale
	 */
	public float getCostBias() {
		long elapsedTime = System.currentTimeMillis() - loadedTime;
		if (elapsedTime > timeBias)
			return 0;
		return 1.0f - ((float) elapsedTime / (float) timeBias);
	}

	private long getVersionFromFilename(String filename) {
		String version = filename.substring(filename.lastIndexOf('_') + 1);
		return Long.parseLong(version);
	}

	public boolean load() {
		assert (pageData == null); // can only load once

		WorkerThread.beginIO();
		try {

			File dir = getParentDir();
			File files[] = dir.listFiles(this); // filter out all page numbers
			// except this one
			// files is now a list of all files containing this page, we just
			// need the latest version
			if (files == null || files.length == 0) {
				return false;
			}
			File bestFile = files[0];
			long bestVersion = getVersionFromFilename(files[0].getName());
			for (int i = 1; i < files.length; i++) {
				long version = getVersionFromFilename(files[i].getName());
				if (version > bestVersion) {
					bestVersion = version;
					bestFile = files[i];
				}
			}

			if (bestVersion > vp.getCurrentDbVersion()) {
				// This is OK if the version is 1 more, and there is a transaction in the commit phase
				if (bestVersion > vp.getCurrentDbVersion() + 1) {
					throw new RuntimeException("Disk contains a page newer than the repository");
				}
				// TODO: Check a tx is in the commit phase
			}

			if (!bestFile.exists()){ // FIXME: This is worrying.  Surely we know it exists cos we found it in dir..?
				assert false : "Don't expect this to happen";
				return false;  
			}
			readToPageData(bestFile);
			//System.out.println("Loaded page: " + bestFile.toString() + ", others: " + Arrays.toString(files));
			if (this.offset == 0 && bestFile.getPath().contains("@Branches")){
				assert getInt(pageData, 0) != 0 : "Root branch node should always exist";
			}
		} catch (IOException e) {
			throw new RuntimeException(e); // e.g. FileNotFoundException, or general IOException
		} finally {
			WorkerThread.endIO();
		}
		
		return true;
	}



	private void readToPageData(File bestFile) throws FileNotFoundException, IOException {
		int fileLen = (int) bestFile.length();
		pageData = new byte[fileLen];
		FileInputStream fis = new FileInputStream(bestFile);
		try {
			int read = fis.read(pageData);
			if (read != fileLen) { throw new RuntimeException("Error loading page"); }
		} finally {
			fis.close();
		}
	}

	private int convertOid(long oid) {
		long index = oid - offset;
		assert (index >= 0 && index < length);
		return (int) index;
	}

	void setElement(long oid, Element<T> element) {
		assert (exclusiveWrite.hasWriteLock());
		int index = convertOid(oid);
		modifiedFlags.set(index);
		elements[index] = element;
		TransactionControl transaction = CurrentTransactionHolder.getTransaction();
		Long newVersion = transaction.getCommitVersion();
		latestDbVersion = Math.max(latestDbVersion, newVersion);
		dirty = true;
	}

	/**
	 * Retrieves the element for read-only operation.
	 * @return Element, or null if not yet been created
	 */
	ElementReadOnly<T> getElementForRead(long oid) throws IOException, ClassNotFoundException {
		int index = convertOid(oid);
		return getElement(oid, index);
	}
	
	/**
	 * Get the element for given oid, with the intention of modifying it.  This causes the element to be marked
	 * as modified, within the Page, thus forcing it to be serialised on the next save of the page.
	 * @return null if no element yet exists, otherwise Element.
	 */
	Element<T> getElementForWrite(long oid) throws IOException, ClassNotFoundException {
		int index = convertOid(oid);
		modifiedFlags.set(index);
		return getElement(oid, index);
	}
	
	private Element<T> getElement(long oid, int index) throws IOException, ClassNotFoundException {
		assert (exclusiveWrite.hasEitherLock());
		assert(convertOid(oid) == index);
		
		Element<T> e = elements[index];
		if (e != null)
			return e;

		// e is null
		if (pageData == null){ // nothing loaded, so is a new page
			return null;
		}

		// try and decode it
		int address = index * 8;
		int dataOffset = getInt(pageData, address);

		if (dataOffset == 0) {
			return null;
		}
		
		ArchInStream in = ArchInStream.newInputStream(pageData, dataOffset, store.getPagerCtc(), store.getPagerCli());

		elements[index] = Element.readFromStream(in, oid);
		return elements[index];
	}

	void acquireRead() throws PagePurgedException {
		exclusiveWrite.acquireRead();
		if (purged) {
			releaseRead();
			throw new PagePurgedException();
		}
	}

	void releaseRead() {
		exclusiveWrite.releaseRead();
	}

	void acquireWrite() throws PagePurgedException {
		exclusiveWrite.acquireWrite();
		if (purged) {
			releaseWrite();
			throw new PagePurgedException();
		}
	}

	public boolean tryAcquireWrite() throws PagePurgedException {
		boolean success = exclusiveWrite.tryAcquireWrite();
		if (purged) {
			if (success)
				releaseWrite();
			throw new PagePurgedException();
		}
		return success;
	}

	void releaseWrite() {
		exclusiveWrite.releaseWrite();
	}

	void releaseWriteAcquireRead() throws PagePurgedException {
		exclusiveWrite.releaseWriteAcquireRead();
		if (purged) {
			releaseRead();
			throw new PagePurgedException();
		}
	}

	/**
	 * Flush old versions, but do NOT null out the entry for e, unless also
	 * removing the version from the disk.
	 * We avoid setting elements[i] to null, as we need to allow e.isDeleted() to be tested
	 * when doing save().
	 */
	void flushOldVersions() {
		assert (exclusiveWrite.hasWriteLock());
		final long oldestTransaction = vp.getOldestTransactionVersion();
		
		for (int i = 0; i < length; i++) {
			Element<T> e = null;

			if (elementIsModified(i)) {
				e = elements[i];
				if (e != null) {
					e.flushOldVersions(oldestTransaction);
				}
			} 
		}		
	}
	
	/**
	 * Write the page to the disk, avoiding CPU intensity of unnecessary serialisation
	 * algo:
	 * Iterate over Elements
	 *  - if Element has not been modified since it was loaded, copy serialised Element data
	 *  from relevant part of pageData to new element data stream
	 *  - if Element has been modified, serialise it to the element data stream
	 *  NOTE: When done, ensure that pageData accurately reflects what is now on disk
	 * @throws IOException
	 */
	void save(boolean flushPageFromMemory) throws IOException {
		assert (exclusiveWrite.hasWriteLock());

		final int offsetLength = 8 * length;
		final long oldestTransaction = vp.getOldestTransactionVersion();

		ByteArrayOutputStream offsetData = new ByteArrayOutputStream(offsetLength);
		ByteArrayOutputStream elementData = new ByteArrayOutputStream();
		MeteredOutputStream meteredElementData = new MeteredOutputStream(elementData);

//		boolean logElements = getParentDir().getPath().contains("@Branches");
////				|| getParentDir().getPath().equals(
////				"\\db2\\repos\\pages\\_LiftshareStore\\_default\\com.wwm.attrs.userobjects.StandaloneWWIndexData@Default@Leaves\\0\\0\\0")
//				;
//				
//		if (logElements){
//			StringBuilder b = new StringBuilder(path.substring(path.length()-15)).append(VERSION_SEPARATOR).append(latestDbVersion).append("[")
//				.append(length).append("]: ");
//			
//			for (int i = 0; i < length; i++) {
//				boolean m = elementIsModified(i);
//				ElementReadOnly e = elements[i];
//				if (offset == 0 && i == 0) { 
//					@SuppressWarnings("unused")
//					int x = 42;  // for breakpoint
//				}
//				
//				if (e == null) b.append(m ? ':' : '.');
//				else if (e.isDeleted()) b.append(m ? 'D' : 'd');
//				else b.append(m ? 'W' : 'R');
//				writeElement(offsetLength, oldestTransaction, offsetData, meteredElementData, i);
//				modifiedFlags.clear(i);
//			}
//			log.info(b.toString());
//		} else {
			for (int i = 0; i < length; i++) {
				writeElement(offsetLength, oldestTransaction, offsetData, meteredElementData, i);
				modifiedFlags.clear(i);
			}
//		}
		offsetData.flush();
		offsetData.close();

		writeStreamsToFile(offsetData, elementData, flushPageFromMemory);
		//System.out.println("Saved page: " + fout);
		dirty = false;
		// pager.getPagerStats().setDataVolume(pageTable, createdNew ? 0 :
		// pageData.length, (int)meteredElementData.getByteCount());
		
		if (flushPageFromMemory){
			markPurged();
		}
	}

	/**
	 * Write element to output stream, either by serialising it, or by copying previously serialised
	 * data, in cases where it hasn't been modified.
	 * @param offsetLength
	 * @param oldestTransaction
	 * @param offsetData
	 * @param meteredElementData
	 * @param i
	 * @throws IOException
	 */
	private void writeElement(final int offsetLength, final long oldestTransaction,
			ByteArrayOutputStream offsetData, MeteredOutputStream meteredElementData, int i)
			throws IOException {
		Element<T> e = null;
		boolean deleted = false; // Need non-null e, and deleted == true, to detect that we should delete from disk
		
		/* 4 cases to handle:
		 * 1) Element has been modified, therefore flush and serialise
		 * 2a) Element has been read, but not modified, therefore copy existing serialised data
		 * 2b) Element has not been read, therefore copy existing serialised data
		 * 3) Element is modified and deleted, therefore write zeros to stream
		 * FIXME: We don't currently deal with 2) correctly - we serialise instead of copy
		 */
		
		if (elementIsModified(i)) {
			e = elements[i];
			assert e != null : "an element marked as modified should actually exist";
			
			e.flushOldVersions(oldestTransaction);
			if (e.isDeleted()) {
				elements[i] = null;
				e = null;
				deleted = true;
				if (disableDelete){
					throw new RuntimeException("Shouldn't be deleting from @Branches or BTree Index");
				}
			}
		} 
		
		// Case 1: Modified and not deleted, so serialise it
		if (elementIsModified(i) && e != null) {
			int currentOffset = (int) meteredElementData.getByteCount() + offsetLength;
			assert currentOffset != 0 : "cannot write element with zero offset as that has special meaning";
			writeInt( currentOffset, offsetData );
			
			ArchOutStream out = ArchOutStream.newOutputStream(meteredElementData, store.getStoreId(), store.getPagerCtc());
			// out.writeObject(e);
			e.writeToStream(out);
			out.flush();
			out.close();
			
			int chunkLength = ((int) meteredElementData.getByteCount() + offsetLength) - currentOffset;
			writeInt( chunkLength, offsetData );
			return;
		}
		
		// Case 2a (e != null && !modified() )  i.e. read, but not modified
		// Case 2b (e == null)  i.e. not been read 
		// => copy existing data and handle if zeros to indicate deleted 
		if (!deleted && pageData != null ) {
			int address = i * 8;
			
			int dataOffset = getInt(pageData, address);
			
			if (dataOffset != 0) {
				// copy old element data over as it's unmodified
				int dataLength = getInt(pageData, address + 4);
				
				writeInt((int) meteredElementData.getByteCount() + offsetLength, offsetData);
				writeInt(dataLength, offsetData);
				
				meteredElementData.write(pageData, dataOffset, dataLength);
				meteredElementData.flush();
				return;
			}
		}
		
		// Attempt to detect that we're here when we shouldn't be
		if (disableDelete && pageData != null && i < length-1){ // not last element, as we want to check next
			int nextAddress = (i+1) * 8;
			int dataOffset = getInt(pageData, nextAddress);
			if (dataOffset != 0) {
				throw new RuntimeException("We've deleted something by accident");
			}
		}
		
		// Case 3) + case if we got here, then it's deleted, so write zeros
		offsetData.write(eightZeros);
	}

	/**
	 * Writes offsetData followed by elementData to a file suffixed with latestDbVersion,
	 * and update pageData to reflect what's on disk
	 * @param offsetData
	 * @param elementData
	 */
	private void writeStreamsToFile(ByteArrayOutputStream offsetData, ByteArrayOutputStream elementData, boolean purging) 
			throws FileNotFoundException, IOException {

		File parentDir = getParentDir();
		if (!parentDir.exists()){
			if( !parentDir.mkdirs() ){
				throw new RuntimeException("Couldn't create dir:" + parentDir);
			}
		}

		File fout = new File(path + VERSION_SEPARATOR + latestDbVersion);
		FileOutputStream fos = new FileOutputStream(fout);
		byte[] offsetBytes = offsetData.toByteArray();
		byte[] elementBytes = elementData.toByteArray();
		fos.write(offsetBytes);
		fos.write(elementBytes);
		fos.flush();
		fos.close();

		if (!purging) {
			// if we're not purging, we need to update purgeData to reflect what we've now written
			pageData = Arrays.copyOf(offsetBytes, offsetBytes.length + elementBytes.length);
			System.arraycopy(elementBytes, 0, pageData, offsetBytes.length, elementBytes.length);
		}
	}

	
	private boolean elementIsModified(int elementIndex) {
		return modifiedFlags.get(elementIndex);
	}
	
	private File getParentDir() {
		File file = new File(path);
		File dir = file.getParentFile();
		return dir;
	}

	private void markPurged() {
		assert (exclusiveWrite.hasWriteLock());
		assert (!dirty); // caller must have saved the page
		purged = true;
		pageData = null;
	}

	void accessed(boolean forWrite) {
		if (forWrite) {
			accessedForWrite.accessed();
			dirty = true;
			latestDbVersion = CurrentTransactionHolder.getCommitVersion();
		} else {
			accessedForRead.accessed();
		}
	}

	float getAccessFreq(boolean forWrite) {
		if (forWrite) {
			return accessedForWrite.getAccessFreq();
		} else {
			return accessedForRead.getAccessFreq();
		}
	}

	public boolean accept(File dir, String name) {
		return name.startsWith(filterString);
	}

	public boolean isDirty() {
		return dirty;
	}

	public boolean doesElementExist(long oid) {
		assert (exclusiveWrite.hasEitherLock());
		final int index = convertOid(oid);

		ElementReadOnly<T> e = elements[index];
		if (e != null)
			return true;

		// e is null
		if (pageData == null){ // nothing loaded, so must be new page
			return false;
		}

		// is it decodable
		int address = index * 8;
		int dataOffset = getInt(pageData, address);

		return (dataOffset != 0);
	}

	private void writeInt(int i, ByteArrayOutputStream stream) {
		stream.write( (i >>> 24) & 0x000000ff );
		stream.write( (i >>> 16) & 0x000000ff );
		stream.write( (i >>> 8) & 0x000000ff );
		stream.write( i & 0x000000ff );
	}

	/**
	 * Read 4 bytes at the specified offset from this byteArray
	 */
	private int getInt(byte[] byteArray, int offset) {
		int a = (byteArray[offset + 0] & 0xFF) << 24;
		int b = (byteArray[offset + 1] & 0xFF) << 16;
		int c = (byteArray[offset + 2] & 0xFF) << 8;
		int d = (byteArray[offset + 3] & 0xFF) << 0;
		return a + b + c + d;
	}
	

	static long getLong(byte[] b, int off) {
		return ((b[off + 7] & 0xFFL) << 0) + ((b[off + 6] & 0xFFL) << 8) + ((b[off + 5] & 0xFFL) << 16)
				+ ((b[off + 4] & 0xFFL) << 24) + ((b[off + 3] & 0xFFL) << 32) + ((b[off + 2] & 0xFFL) << 40)
				+ ((b[off + 1] & 0xFFL) << 48) + ((b[off + 0] & 0xFFL) << 56);
	}

	static void putLong(byte[] b, int off, long val) {
		b[off + 7] = (byte) (val >>> 0);
		b[off + 6] = (byte) (val >>> 8);
		b[off + 5] = (byte) (val >>> 16);
		b[off + 4] = (byte) (val >>> 24);
		b[off + 3] = (byte) (val >>> 32);
		b[off + 2] = (byte) (val >>> 40);
		b[off + 1] = (byte) (val >>> 48);
		b[off + 0] = (byte) (val >>> 56);
	}
	
	
}
