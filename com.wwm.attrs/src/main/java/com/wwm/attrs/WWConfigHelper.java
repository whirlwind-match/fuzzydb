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

import com.wwm.attrs.enums.EnumDefinition;
import com.wwm.attrs.internal.ScoreConfiguration;
import com.wwm.db.Store;
import com.wwm.db.Transaction;
import com.wwm.db.core.exceptions.ArchException;

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

    public static void updateScorerConfig(Store store, String name, ScoreConfiguration sc) throws ArchException {
        Transaction tx = store.getAuthStore().begin();
        WhirlwindConfiguration conf = tx.retrieveFirstOf(WhirlwindConfiguration.class);
        if (conf == null){
            conf = new WhirlwindConfiguration();
            conf.getScoreConfigManager().setConfig(name, sc);
            tx.create(conf);
        } else {
            conf.getScoreConfigManager().setConfig(name, sc);
            tx.update(conf);
        }
        tx.commit();
    }

    public static void updateEnumDefinition(Store store, String name, EnumDefinition def) throws ArchException {
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
    public static void updateIndexConfig(Store store, ManualIndexStrategy strategy) throws ArchException {
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
