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
package com.wwm.db.internal.server.txlog;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.wwm.io.core.messages.Command;

public interface TxLogSink {
	public void flush() throws IOException;
	public void write(long version, Command command) throws IOException;
	public void close() throws IOException;
	public void rolloverToNewLog(long version) throws IOException, FileNotFoundException;
}
