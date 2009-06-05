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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;

import com.wwm.io.packet.layer1.PacketInterface;

public class TxLogReader implements PacketInterface {

	private FileInputStream fis;
	
	public TxLogReader(File file) throws FileNotFoundException {
		open(file);
	}

	private void open(File file) throws FileNotFoundException {
		fis = new FileInputStream(file);
	}
	
	public void close() {
		try {
			fis.close();
		} catch (IOException e) {
			throw new Error(e);
		}
	}

	public Collection<ByteBuffer> read() {
		int d;
		try {
			d = fis.read();
			int c = fis.read();
			int b = fis.read();
			int a = fis.read();
			a <<= 24;
			a &= 0xff000000;
			b <<= 16;
			b &= 0x00ff0000;
			c <<= 8;
			c &= 0x0000ff00;
			d &= 0x000000ff;
			
			int length = a | b | c | d;
	
			if (length < 0) return null;
			
			byte[] data = new byte[length];
			int read = fis.read(data);
			if (read < length) {
				return null;
			}
			ByteBuffer bb = ByteBuffer.wrap(data);
			ArrayList<ByteBuffer> al = new ArrayList<ByteBuffer>();
			al.add(bb);
			return al;
		} catch (IOException e) {
			return null;
		}
	}

	public void write(ByteBuffer b) {
		throw new UnsupportedOperationException();
	}

	public void write(ByteBuffer[] b) {
		throw new UnsupportedOperationException();
	}

	public void write(Collection<ByteBuffer> b) {
		throw new UnsupportedOperationException();
	}

}
