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
package com.wwm.io.packet.layer2;

import java.nio.ByteBuffer;

import com.wwm.io.packet.messages.Message;

public class SourcedMessageImpl implements SourcedMessage {

	private MessageInterface mi;
	private Message message;
	private int storeId;
	private ByteBuffer packet;

	public SourcedMessageImpl(int storeId, MessageInterface mi, Message message, ByteBuffer packet) {
		this.mi = mi;
		this.message = message;
		this.storeId = storeId;
		this.packet = packet;
	}
	
	public MessageInterface getSource() {
		return mi;
	}

	public Message getMessage() {
		return message;
	}

	public int getStoreId() {
		return storeId;
	}

	public ByteBuffer getPacket() {
		return packet;
	}
}
