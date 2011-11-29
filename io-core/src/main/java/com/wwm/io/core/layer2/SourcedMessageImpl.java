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
package com.wwm.io.core.layer2;

import java.io.Serializable;
import java.nio.ByteBuffer;

import com.wwm.io.core.Message;
import com.wwm.io.core.MessageSink;
import com.wwm.io.core.SourcedMessage;

// TODO: Make mi and packet part of Message as transient fields and simplify interfaces everywhere.
public class SourcedMessageImpl implements SourcedMessage, Serializable {

	transient private final MessageSink mi;
	private final Message message;
	transient private final ByteBuffer packet;

	public SourcedMessageImpl(MessageSink mi, Message message, ByteBuffer packet) {
		this.mi = mi;
		this.message = message;
		this.packet = packet;
	}
	
	public final MessageSink getSource() {
		return mi;
	}

	public final Message getMessage() {
		return message;
	}

	public final ByteBuffer getPacket() {
		return packet;
	}
}
