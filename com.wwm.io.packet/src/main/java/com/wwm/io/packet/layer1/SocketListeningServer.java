package com.wwm.io.packet.layer1;

import java.io.IOException;
import java.net.InetSocketAddress;

public class SocketListeningServer extends ServerImpl {
	private final InetSocketAddress address;

	/**
	 * TODO: Could make address an array
	 * 
     * Create a new server ready to listen on the given InetSocketAddress
     * NOTE: We've already gone wrong a few times passing InetSocketAddress(InetAddr.LocalAddr, port)
     * when we actually want to listen on AnyLocalAddr or a loopback (?), perhaps we can not
     * generate the InetSocketAddress within our API?  
     * @param address e.g. new InetSocketAddress( port )

	 */
	public SocketListeningServer(InetSocketAddress address) throws IOException {
		super();
		this.address = address;
	}

	@Override
	public void start() {
		try {
			listen(address);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}