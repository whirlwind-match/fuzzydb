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
package com.wwm.io.core;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.nio.ByteBuffer;



public class ArchInStream extends ObjectInputStream {
	
	private final int storeId;
	private final ClassTokenCache ctc;
	private final ClassLoaderInterface cli;
	
	private ArchInStream(InputStream in, ClassTokenCache ctc, ClassLoaderInterface cli) throws IOException {
		super(in);
		this.storeId = readInt();
		this.ctc = ctc;
		this.cli = cli;
	}

    // Returns an input stream for a ByteBuffer.
    // The read() methods use the relative ByteBuffer get() methods.
    public static ArchInStream newInputStream(final ByteBuffer buf, ClassTokenCache ctc, ClassLoaderInterface cli) throws IOException {
    	InputStream is = new InputStream() {
            @Override
			public synchronized int read() {
                if (!buf.hasRemaining()) {
                    return -1;
                }
                return buf.get();
            }
    
            @Override
			public synchronized int read(byte[] bytes, int off, int len) {
                // Read only what's left
                len = Math.min(len, buf.remaining());
                buf.get(bytes, off, len);
                return len;
            }
        };
        return new ArchInStream(is, ctc, cli);
    }

    public static ArchInStream newInputStream(byte[] data, ClassTokenCache ctc, ClassLoaderInterface cli) throws IOException {
    	InputStream is = new ByteArrayInputStream(data);
        return new ArchInStream(is, ctc, cli);
    }

    public static ArchInStream newInputStream(byte[] data, int offset, ClassTokenCache ctc, ClassLoaderInterface cli) throws IOException {
    	InputStream is = new ByteArrayInputStream(data, offset, data.length);
        return new ArchInStream(is, ctc, cli);
    }
    
	@Override
	protected ObjectStreamClass readClassDescriptor() throws IOException, ClassNotFoundException {
		int i = readInt();
		if (i == -1) {
			return ctc.lookupOSCToken(readInt()).getOsc(cli);				
		} else {
			StringBuilder sb = new StringBuilder();
			while (i > 0) {
				sb.append(readChar());
				i--;
			}
			String className = sb.toString();
			long serialVersionUID = readLong();
			Class<?> c = cli.getClass(storeId, className);
			
			int token = readInt();
			
			if (token != ctc.addOSCToken(storeId, c, serialVersionUID)) throw new IOException();
			ObjectStreamClass osc = ObjectStreamClass.lookup(c);
			if (osc != null && osc.getSerialVersionUID() == serialVersionUID) {
				return osc;
			}
			// If the class wasn't found, or was found but had the wrong UID, 
			// or was found but is not serialisable, we don't care and can just throw ClassNotFound
			throw new ClassNotFoundException(c.getName());	
		}
	}

	public int getStoreId() {
		return storeId;
	}

}
