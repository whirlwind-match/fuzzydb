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
package com.wwm.io.core.messages;

import java.nio.ByteBuffer;

import com.wwm.io.core.Message;

public class PacketMessage {
	private final Message message;
	private final ByteBuffer packet;
	
	public PacketMessage(final Message message, final ByteBuffer packet) {
		super();
		this.message = message;
		this.packet = packet;
	}

	public Message getMessage() {
		return message;
	}

	public ByteBuffer getPacket() {
		return packet;
	}
	
}
