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
package com.wwm.io.packet;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import com.wwm.io.core.ClassLoaderInterface;
import com.wwm.io.core.MessageInterface;
import com.wwm.io.core.layer2.PacketCodec;
import com.wwm.io.packet.layer1.Connection;
import com.wwm.io.packet.layer1.SocketDriver;

public class TCPStack implements CommsStack {

	private final SocketDriver sd;
	private final MessageInterface mi;

	public TCPStack(SocketChannel sc, ClassLoaderInterface cli) throws IOException {
		
		sc.configureBlocking(false);
		sc.socket().setKeepAlive(true);
		sc.socket().setTrafficClass(0x10);	// IPTOS_LOWDELAY (0x10)
		
		Connection l1 = new Connection(sc);
		sd = l1;
		mi = new PacketCodec(l1, cli);
	}
	
	public SocketDriver getDriver() {
		return sd;
	}

	public MessageInterface getMessageInterface() {
		return mi;
	}
}
