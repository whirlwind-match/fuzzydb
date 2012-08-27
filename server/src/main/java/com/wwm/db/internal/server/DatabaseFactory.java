package com.wwm.db.internal.server;

import org.fuzzydb.io.core.MessageSource;

import com.google.inject.Module;
import com.wwm.db.internal.common.ServiceRegistry;

public class DatabaseFactory {

    public static Database createDatabase(MessageSource messageSource, boolean isPersistent) {

		Module modules = new DatabaseModule(messageSource, isPersistent);
		ServiceRegistry.initialise(modules);
		return ServiceRegistry.getService(Database.class);
	}
}