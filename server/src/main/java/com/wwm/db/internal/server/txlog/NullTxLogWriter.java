package com.wwm.db.internal.server.txlog;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.wwm.io.core.messages.Command;

public class NullTxLogWriter implements TxLogSink {

	public void flush() throws IOException {
		// TODO Auto-generated method stub
	}

	public void write(long version, Command command) throws IOException {
		// TODO Auto-generated method stub
	}

	public void close() throws IOException {
		// TODO Auto-generated method stub
	}

	public void rolloverToNewLog(long version) throws IOException,
			FileNotFoundException {
		// TODO Auto-generated method stub
	}


}
