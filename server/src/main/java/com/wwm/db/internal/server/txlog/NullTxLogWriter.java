package com.wwm.db.internal.server.txlog;

import java.io.IOException;

import org.fuzzydb.io.core.messages.Command;


public class NullTxLogWriter implements TxLogSink {

	public void flush() throws IOException {
	}

	public void write(long version, Command command) throws IOException {
	}

	public void close() throws IOException {
	}

	public void rolloverToNewLog(long version) {
	}


}
