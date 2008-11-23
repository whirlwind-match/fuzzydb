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
package com.wwm.postcode;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.Random;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import com.wwm.db.core.Settings;
import com.wwm.db.dao.DaoWriteCollisionException;
import com.wwm.db.dao.SimpleDAO;
import com.wwm.db.userobjects.PostcodeUseCount;
import com.wwm.postcode.PostcodeConvertor.LostDbConnection;
import com.wwm.stats.counters.Count;
import com.wwm.util.MTRandom;
import com.wwm.util.StringUtils;

/**
 * Old version with high memory footprint
 * @deprecated Now use efficient version supplied by PostcodeService.
 */
@Deprecated
public class PostZonConvertor extends Thread {
	private Logger log;

	private static final String fullFile = Settings.getInstance().getPostcodeRoot() + File.separatorChar + PostZonImporter.postzonDataDir;

	// Future use
	//	class Cluster {
	//		final static float offsetMultiplier = 0.002f;
	//		float latOrigin;
	//		float lonOrigin;
	//		String[] town;
	//		char[] code;
	//		byte latOffset[];
	//		byte lonOffset[];
	//		byte townOffset[];
	//
	//		void test() {
	//			synchronized (code) {}
	//		}
	//	}

	private volatile SimpleDAO dao;
	private volatile long lookupCount=0;
	private Random random = new MTRandom();
	private boolean threadShutdownCalled = false;

	//HashMap<String, byte[]> longCache = new HashMap<String, byte[]>();

	public PostZonConvertor(SimpleDAO dao, Logger log) {
		super("PostcodeConvertor delayed committer");
		super.setDaemon(true);
		this.log = log;
		setDao(dao);
		log.info("Starting up PostcodeConvertor");
		if (dao != null) {
			super.start();
		}
	}

	public void shutdown() {
		threadShutdownCalled = true;
		if (dao == null) {
			return;
		}
		try {
			this.interrupt();
			this.join();
		} catch (InterruptedException e) { e.printStackTrace(); } // FIXME: Document this exception
	}

	@Override
	public void run() {
		setPriority(Thread.MAX_PRIORITY);

		long committedCount=0;

		for (;;) {
			try {
				Thread.sleep(1000 + random.nextInt(1000));
			} catch (InterruptedException e) {
				log.info("PostZonConverter interrupted - committing early");
			}

			long increment;
			SimpleDAO safeDao;

			synchronized (this) {
				increment = lookupCount - committedCount;
				safeDao = dao;
			}
			if (safeDao != null && increment > 0) {

				try {
					safeDao.begin();
					Count puc = safeDao.retrieve(PostcodeUseCount.class, null);
					if (puc != null) {
						puc.setCount(puc.getCount()+increment);
						safeDao.update(puc, null);
						log.info("PostcodeUseCount is now " + puc.getCount());
					} else {
						puc = new PostcodeUseCount();
						puc.setCount(1);
						safeDao.create(puc, null);
						log.warning("PostcodeUseCount not found in store, starting new count from 1");
					}
					safeDao.commit();
					committedCount += increment;
				} catch (DaoWriteCollisionException vme) {
					log.info("Postcode count collision, will try again later");
				} catch (Exception e) {
					dao = null;
					log.log(Level.SEVERE, "Unexpected exception", e);
				}
			}
			// Check for call to shutdown and make sure ALL tokens have been committed
			if (threadShutdownCalled == true && (committedCount == lookupCount || safeDao == null)) {
				return;
			}
		}
	}

	public synchronized void setDao(SimpleDAO dao) {
		this.dao = dao;
	}


	/**Look up a full postcode.<br><br>WARNING - EXECUTING THIS FUNCTION COSTS 1p!!<br><br>
	 * @param postcode A full postcode to lookup, any caps, any spaces
	 * @return A PostcodeResult if the postcode is valid, otherwise null
	 * @throws LostDbConnection
	 */
	@SuppressWarnings("unchecked")
	public synchronized PostcodeResult lookupFull(String postcode) throws LostDbConnection {
		postcode = StringUtils.stripSpaces(postcode.toUpperCase());
		if (postcode.length() < PostcodeService.minPostcodeLen) {
			return null;
		}

		if (dao == null) {
			throw new LostDbConnection();
		}

		String filename = postcode.substring(0, postcode.length()-PostZonImporter.blocksize);
		InputStream is;

		byte[] cached = null; //longCache.get(filename);
		if (cached == null) {
			try {
				File f = new File(fullFile + File.separator + filename);
				cached = new byte[(int) f.length()];
				FileInputStream fis = new FileInputStream(f);
				fis.read(cached);
				//longCache.put(filename, cached);
			} catch (FileNotFoundException e) {
				log.warning("Failed to load Full data from " + fullFile + " for prefix: " + filename + ", bad code or missing file?");
				return null;
			} catch (IOException e) {
				log.severe("Error reading full data: " + e);
				return null;
			}
		}

		is = new ByteArrayInputStream(cached);

		try {

			GZIPInputStream gzis = new GZIPInputStream(is);
			ObjectInputStream ois = new ObjectInputStream(gzis);
			TreeMap<String, PostcodeResult> subMap = (TreeMap<String, PostcodeResult>)ois.readObject();
			PostcodeResult pr = subMap.get(postcode);
			if (pr != null) {
				lookupCount++;
			}
			return pr;
		} catch (IOException e) {
			log.severe("Error reading full data: " + e);
			return null;
		} catch (ClassNotFoundException e) {
			log.severe("PostcodeConvertor internal error, Error reading full data: " + e);
			return null;
		}
	}

}
