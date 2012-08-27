package org.fuzzydb.server.internal.server;

import org.fuzzydb.io.core.ClassLoaderInterface;
import org.fuzzydb.io.core.MessageSource;
import org.fuzzydb.io.core.impl.DummyCli;
import org.fuzzydb.server.internal.pager.FileSerializingPagePersister;
import org.fuzzydb.server.internal.pager.NullPersister;
import org.fuzzydb.server.internal.pager.PagePersister;
import org.fuzzydb.server.internal.server.txlog.NullTxLogWriter;
import org.fuzzydb.server.internal.server.txlog.TxLogSink;
import org.fuzzydb.server.internal.server.txlog.TxLogWriter;
import org.fuzzydb.server.services.IndexImplementationsService;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.name.Names;

public class DatabaseModule implements Module {
	private final boolean isPersistent;
	private final MessageSource messageSource;

	public DatabaseModule(MessageSource messageSource, boolean isPersistent) {
		this.isPersistent = isPersistent;
		this.messageSource = messageSource;
	}

	public void configure(Binder binder) {
		binder.bind(Boolean.class).annotatedWith(Names.named("isPersistent")).toInstance(isPersistent);
		if (isPersistent) {
			binder.bind(RepositoryStorageManager.class).to(FileRepositoryStorageManager.class);
			binder.bind(PagePersister.class).to(FileSerializingPagePersister.class);
			binder.bind(TxLogSink.class).to(TxLogWriter.class); 
		}
		else {
			binder.bind(RepositoryStorageManager.class).to(NullRepositoryStorageManager.class);
			binder.bind(PagePersister.class).to(NullPersister.class);
			binder.bind(TxLogSink.class).to(NullTxLogWriter.class); 
		}
		binder.bind(MessageSource.class).toInstance(messageSource);
		binder.bind(ServerSetupProvider.class);
		binder.bind(ClassLoaderInterface.class).to(DummyCli.class);
		binder.bind(IndexImplementationsService.class);
		binder.bind(Database.class);

	}
}