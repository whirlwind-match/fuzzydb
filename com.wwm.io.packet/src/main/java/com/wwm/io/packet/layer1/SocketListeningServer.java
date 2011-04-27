package com.wwm.io.packet.layer1;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.wwm.io.packet.ClassLoaderInterface;

public class SocketListeningServer extends ServerImpl {
	private final InetSocketAddress address;

	/**
	 * TODO: Could make address an array
	 */
	public SocketListeningServer(ClassLoaderInterface cli,
			InetSocketAddress address) throws IOException {
		super(cli);
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