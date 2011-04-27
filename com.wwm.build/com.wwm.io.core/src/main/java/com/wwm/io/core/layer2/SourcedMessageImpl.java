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

import java.nio.ByteBuffer;

import com.wwm.io.core.Message;
import com.wwm.io.core.MessageInterface;
import com.wwm.io.core.SourcedMessage;

public class SourcedMessageImpl implements SourcedMessage {

	private final MessageInterface mi;
	private final Message message;
	private final ByteBuffer packet;

	public SourcedMessageImpl(MessageInterface mi, Message message, ByteBuffer packet) {
		this.mi = mi;
		this.message = message;
		this.packet = packet;
	}
	
	public final MessageInterface getSource() {
		return mi;
	}

	public final Message getMessage() {
		return message;
	}

	public final ByteBuffer getPacket() {
		return packet;
	}
}
