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
package org.fuzzydb.client.internal;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.fuzzydb.core.Settings;

import com.wwm.io.core.Authority;
import com.wwm.io.core.exceptions.CommsErrorException;
import com.wwm.io.packet.layer1.ClientConnectionManagerImpl;

public class ClientImpl extends AbstractClient {


    public ClientImpl(Authority authority) {
    	super(authority);
    }

    public void connect() {
        connect(Settings.getInstance().getPrimaryServer());
    }

    @Override
	public void connect(String server) {
        InetSocketAddress address = new InetSocketAddress(
                server,
                Settings.getInstance().getPrimaryServerPort());
        connect(address);
    }

    @Override
	public void connect(InetSocketAddress addr) {
        try {
            setConnection(new ClientConnectionManagerImpl(addr, getCli()));
        } catch (IOException e) {
            throw new CommsErrorException(e);
        }
    }
}
