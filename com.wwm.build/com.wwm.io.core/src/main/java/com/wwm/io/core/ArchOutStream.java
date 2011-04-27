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

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;
import java.io.Serializable;


public class ArchOutStream extends ObjectOutputStream {
	private final int storeId;
	private final ClassTokenCache ctc;

    // Returns an output stream for a ByteBuffer.
    // The write() methods use the relative ByteBuffer put() methods.
    public static ArchOutStream newOutputStream(OutputStream buf, int storeId, ClassTokenCache ctc) throws IOException {
        return new ArchOutStream(buf, storeId, ctc);
    }
	
	private ArchOutStream(OutputStream out, int storeId, ClassTokenCache ctc) throws IOException {
		super(out);
		this.storeId = storeId;
		this.ctc = ctc;
		writeInt(storeId);
	}

	@Override
	protected void writeClassDescriptor(ObjectStreamClass desc) throws IOException {
		int token = ctc.getOSCToken(storeId, desc.forClass(), desc.getSerialVersionUID());
		if (token == -1) {
			if (!Serializable.class.isAssignableFrom(desc.forClass())) {
				throw new NotSerializableException(desc.forClass().getName());
			}
			String className = desc.getName();
			writeInt(className.length());
			for (int i = 0; i < className.length(); i++) {
				writeChar(className.charAt(i));
			}
			writeLong(desc.getSerialVersionUID());
			token = ctc.addOSCToken(storeId, desc.forClass(), desc.getSerialVersionUID());
		} else {
			writeInt(-1);
		}
		writeInt(token);
	}

	public long getStoreId() {
		return storeId;
	}

}
