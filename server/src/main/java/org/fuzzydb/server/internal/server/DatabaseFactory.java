package org.fuzzydb.server.internal.server;

import org.fuzzydb.io.core.MessageSource;
import org.fuzzydb.server.internal.common.ServiceRegistry;

import com.google.inject.Module;

public class DatabaseFactory {

    public static Database createDatabase(MessageSource messageSource, boolean isPersistent) {

		Module modules = new DatabaseModule(messageSource, isPersistent);
		ServiceRegistry.initialise(modules);
		return ServiceRegistry.getService(Database.class);
	}
}