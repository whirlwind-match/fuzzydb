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
package org.fuzzydb.io.packet.layer1;

import java.io.IOException;
import java.net.InetSocketAddress;

public interface Server extends ConnectionManager {
	
	/**Begin listening on all network interfaces on the specified port.
	 * Listen() may be called multiple times to listen on different ports.
	 * @param port
	 * @throws IOException
	 */
	public void listen(int port) throws IOException;
	
	/**Begin listening on the specified address and port.
	 * Listen() may be called multiple times to listen on different addresses and ports.
	 * @param address The local address to listson on.
	 * @throws IOException
	 */
	public void listen(InetSocketAddress address) throws IOException;
	
	/**Begin listening on the specified address and port.
	 * Listen() may be called multiple times to listen on different addresses and ports.
	 * @param hostname The address to bind to. Textual addresses will be resolved. Obviously it helps if the address resolves to a local address. You can use an IP number here.
	 * @param port The port to listen on
	 * @throws IOException
	 */
	public void listen(String hostname, int port) throws IOException;
	
}
