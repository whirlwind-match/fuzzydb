package com.wwm.stats.counters;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.wwm.db.core.LogFactory;
import com.wwm.db.dao.DaoWriteCollisionException;
import com.wwm.db.dao.SimpleDAO;
import com.wwm.util.MTRandom;

/**
 * A counter service.
 * 
 * FIXME: At the moment this just implements a counter for PostcodeUseCount, but it could be refactored
 * to provide the ability to managed multiple named counters.
 * PostcodeUseCount would become CountInstance, and the DAO would need to support lookup by key.
 *
 * NOTE: This class is currently used by old WWM Db, and new open source variant.  Things will be able to move
 * forward a lot when we've migrated away from our old version.
 * FIXME: It is quite possible to do a Db2ObjectDAO derivative that uses tx.retrieve(class, keyField, keyValue)
 * with a count object annotated for indexing the specified keyField.
 */
public class Counter extends Thread  {

	private static final Logger log = LogFactory.getLogger(Counter.class);
	

	private volatile SimpleDAO dao;
	private volatile long count = 0;
	private boolean threadShutdownCalled = false;
	private Class<? extends Count> countClass;


	public Counter(String name, SimpleDAO dao, Class<? extends Count> countClass) {
		super("Counter delayed committer");
		super.setDaemon(true);
		setDao(dao);
		this.countClass = countClass;
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

		Random random = new MTRandom();
		for (;;) {
			try {
				Thread.sleep(20000 + random.nextInt(20000));
			} catch (InterruptedException e) {
				log.info("Counter thread interrupted - committing early");
			}
			long increment;
			SimpleDAO safeDao;

			synchronized (this) {
				increment = count - committedCount;
				safeDao = dao;
			}
			if (safeDao != null && increment > 0) {

				try {
					safeDao.begin();
					Count puc = safeDao.retrieve(countClass, null); // FIXME: We should use name of counter as a key and get DAO to get by key
					if (puc != null) {
						puc.setCount(puc.getCount() + increment);
						safeDao.update(puc, null);
						log.info("PostcodeUseCount is now " + puc.getCount());
					} else {
						puc = countClass.newInstance();
						puc.setCount(1);
						safeDao.create(puc, null);
						log.warning( countClass.getName() + " not found in store, starting new count from 1");
					}
					safeDao.commit();
					committedCount += increment;
				} catch (DaoWriteCollisionException vme) {
					log.info("Counter collision, will try again later");
				} catch (Exception e) {
					dao = null;
					log.log(Level.SEVERE, "Unexpected exception", e);
				}
			}
			// Check for call to shutdown and make sure ALL tokens have been committed
			if (threadShutdownCalled == true && (committedCount == count || safeDao == null)) {
				return;
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.wwm.postcode.uk.full.internal.PostcodeService#setDao(com.wwm.db.core.dao.SimpleDAO)
	 */
	public synchronized void setDao(SimpleDAO dao) {
		this.dao = dao;
		if (dao != null && ! this.isAlive()){
			this.start();
		}
	}

	public void increment() {
		count++;
		
	}
}
