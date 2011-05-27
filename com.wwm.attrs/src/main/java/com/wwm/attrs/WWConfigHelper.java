/******************************************************************************
 * Copyright (c) 2004-2008 Whirlwind Match Limited. All rights reserved.
 *
 * This is open source software; you can use, redistribute and/or modify
 * it under the terms of the Open Software Licence v 3.0 as published by the 
 * Open Source Initiative.
 *
 * You should have received a copy of the Open Software Licence along with this
 * application. if not, contact the Open Source Initiative (www.opensource.org)
 *****************************************************************************/
package com.wwm.attrs;

import java.io.InputStream;

import org.slf4j.Logger;

import com.thoughtworks.xstream.XStream;
import com.wwm.attrs.enums.EnumDefinition;
import com.wwm.attrs.internal.ScoreConfiguration;
import com.wwm.attrs.internal.XStreamHelper;
import com.wwm.db.Store;
import com.wwm.db.Transaction;
import com.wwm.db.core.LogFactory;

/**
 * Helper class for isolated CRUD ops on elements of WhirlwindConfiguration.
 * This makes sense as users tend to want to do updates of different bits by
 * separate operations, so committing changes independently is to be expected.
 * E.g. Web Service user will likely be defining Enums as a separate request to
 * creating the ScoreConfigurations.
 * 
 * NOTE/FIXME: We use the global configuration, not a class specific one (i.e. only expect one WWIndex per store)
 */
public class WWConfigHelper {

	
	static private final Logger log = LogFactory.getLogger(WWConfigHelper.class);
	
    public static void updateScorerConfig(Store store, String content) {
    	XStream xs = XStreamHelper.getScorerXStream(store);
        xs.setClassLoader( WWConfigHelper.class.getClassLoader() ); // OSGi: We need it to use our classLoader, as it's own bundle won't help it :)
        ScoreConfiguration sc = (ScoreConfiguration) xs.fromXML(content);
    	updateScorerConfigInternal(store, sc);
    }
    
	public static void updateScorerConfig(Store store, InputStream inputStream) {
    	XStream xs = XStreamHelper.getScorerXStream(store);
        xs.setClassLoader( WWConfigHelper.class.getClassLoader() ); // OSGi: We need it to use our classLoader, as it's own bundle won't help it :)
        ScoreConfiguration sc = (ScoreConfiguration) xs.fromXML(inputStream);
    	updateScorerConfigInternal(store, sc);
	}
	
	private static void updateScorerConfigInternal(Store store,
			ScoreConfiguration sc) {
		Transaction tx = store.getAuthStore().begin();
		WhirlwindConfiguration conf = tx.retrieveFirstOf(WhirlwindConfiguration.class);
		if (conf == null){
			conf = new WhirlwindConfiguration();
			conf.getScoreConfigManager().setConfig(sc.getName(), sc);
			tx.create(conf);
		} else {
			conf.getScoreConfigManager().setConfig(sc.getName(), sc);
			tx.update(conf);
		}
		tx.commit();
		log.info("Updated scorer: " + sc.getName() + " in store: " + store.getStoreName());
	}

    public static void updateEnumDefinition(Store store, String name, EnumDefinition def) {
        Transaction tx = store.getAuthStore().begin();
        WhirlwindConfiguration conf = tx.retrieveFirstOf(WhirlwindConfiguration.class);

        conf.add(name, def);
        tx.update(conf);
        tx.commit();
    }

    /**
     * Update split configs
     * FIXME: This does not check if no change has been made.  Server side needs to have code to detect this
     * and avoid rebuilding an index for no reason (unless forced)
     */
    public static void updateIndexConfig(Store store, String content) {
    	XStream xs = XStreamHelper.getIndexConfigXStream(store);
        xs.setClassLoader( WWConfigHelper.class.getClassLoader() ); // OSGi: We need it to use our classLoader, as it's own bundle won't help it :)
        ManualIndexStrategy strategy = (ManualIndexStrategy) xs.fromXML(content);

        updateIndexConfigInternal(store, strategy);
    }

    public static void updateIndexConfig(Store store, InputStream inputStream) {
    	XStream xs = XStreamHelper.getIndexConfigXStream(store);
        xs.setClassLoader( WWConfigHelper.class.getClassLoader() ); // OSGi: We need it to use our classLoader, as it's own bundle won't help it :)
        ManualIndexStrategy strategy = (ManualIndexStrategy) xs.fromXML(inputStream);

        updateIndexConfigInternal(store, strategy);
    }

	private static void updateIndexConfigInternal(Store store,
			ManualIndexStrategy strategy) {
		Transaction tx = store.getAuthStore().begin();
        WhirlwindConfiguration conf = tx.retrieveFirstOf(WhirlwindConfiguration.class);
        if (conf == null){
            conf = new WhirlwindConfiguration();
            conf.addStrategy(strategy);
            tx.create(conf);
        } else {
            conf.addStrategy(strategy);
            tx.update(conf);
        }
        tx.commit();
	}
}
