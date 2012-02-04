package com.wwm.db.internal.server;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.name.Names;
import com.wwm.db.internal.pager.FileSerializingPagePersister;
import com.wwm.db.internal.pager.NullPersister;
import com.wwm.db.internal.pager.PagePersister;
import com.wwm.db.internal.server.txlog.NullTxLogWriter;
import com.wwm.db.internal.server.txlog.TxLogSink;
import com.wwm.db.internal.server.txlog.TxLogWriter;
import com.wwm.db.services.IndexImplementationsService;
import com.wwm.io.core.ClassLoaderInterface;
import com.wwm.io.core.MessageSource;
import com.wwm.io.core.impl.DummyCli;

public class DatabaseModule implements Module {
	private final boolean isPersistent;
	private final MessageSource messageSource;

	public DatabaseModule(MessageSource messageSource, boolean isPersistent) {
		this.isPersistent = isPersistent;
		this.messageSource = messageSource;
	}

	public void configure(Binder binder) {
		binder.bind(Boolean.class).annotatedWith(Names.named("isPersistent")).toInstance(isPersistent);
		binder.bind(MessageSource.class).toInstance(messageSource);
		binder.bind(PagePersister.class).to( isPersistent ? FileSerializingPagePersister.class : NullPersister.class);
		binder.bind(ServerSetupProvider.class);
		binder.bind(ClassLoaderInterface.class).to(DummyCli.class);
		binder.bind(TxLogSink.class).to(isPersistent ? TxLogWriter.class : NullTxLogWriter.class); 
				// TxLogWriter(setup.getTxDiskRoot(), cli) : new NullTxLogWriter();
		binder.bind(IndexImplementationsService.class);
		binder.bind(DatabaseVersionState.class).to(Database.class);
		binder.bind(Database.class);

	}
}