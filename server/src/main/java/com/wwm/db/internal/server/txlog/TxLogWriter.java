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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Date;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.wwm.db.internal.server.ServerSetupProvider;
import com.wwm.io.core.ClassLoaderInterface;
import com.wwm.io.core.PacketInterface;
import com.wwm.io.core.layer2.PacketCodec;
import com.wwm.io.core.messages.Command;
import com.wwm.util.MeteredOutputStream;
@Singleton
public class TxLogWriter implements TxLogSink, PacketInterface {

	private final ClassLoaderInterface commsCli;
	private final String dirName;
	private MeteredOutputStream mos;
	private PacketCodec pc;
	private static final int txLogSize = 10*1024*1024;
	
	@Inject
	public TxLogWriter(ServerSetupProvider setup, ClassLoaderInterface commsCli) {
		this(setup.getTxDiskRoot(), commsCli);
	}
	
	public TxLogWriter(String txDir, ClassLoaderInterface commsCli) {
		this.commsCli = commsCli;
		this.dirName = txDir;
		File dir = new File(dirName);
		dir.mkdirs();
	}
	
	private synchronized void openFile(long version) throws FileNotFoundException {
		
		String date = new Date().toString().replace(' ', '_');	// remove spaces
		date = date.replace(':', '-');	// win32 doesn't like ':'s!
		File file = new File(dirName, "t" + version + "_" + date);
		FileOutputStream fos = new FileOutputStream(file);
		mos = new MeteredOutputStream(fos);
		pc = new PacketCodec(this, commsCli);
	}
	
	public synchronized void close() {
		try {
			flush();
			if (mos != null) {
				mos.close();
				mos = null;
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public synchronized void flush() throws IOException {
		if (mos != null) {
			mos.flush();
		}
	}

	public synchronized void write(long version, Command command) throws IOException {
		if (mos == null) {
			openFile(version);
		}
		if (mos.getByteCount() >= txLogSize) {
			rolloverToNewLog(version);
		}
		pc.send(command);
	}

	
	public void rolloverToNewLog(long version) throws IOException, FileNotFoundException {
		flush();
		close();
		openFile(version);
	}

	public Collection<ByteBuffer> read() {
		throw new UnsupportedOperationException();
	}

	public void write(ByteBuffer b) throws IOException {
		int len = b.remaining();
		byte[] data = new byte[len];
		b.get(data);
		int length = data.length;
		mos.write(length);
		length >>= 8;
		mos.write(length);
		length >>= 8;
		mos.write(length);
		length >>= 8;
		mos.write(length);
		mos.write(data);
	}

	public void write(ByteBuffer[] b) throws IOException {
		for (int i = 0; i < b.length; i++) {
			write(b[i]);
		}
	}

	public void write(Collection<ByteBuffer> b) throws IOException {
		ByteBuffer[] bba = new ByteBuffer[b.size()];
		write(b.toArray(bba));
	}

}
