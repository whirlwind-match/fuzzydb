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
package com.wwm.io.packet.layer1;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Logger;

import com.wwm.db.core.LogFactory;

public class Connection implements PacketInterface, SocketDriver {
	private static final Logger log = LogFactory.getLogger(Connection.class);
	
	private boolean closing = false;
	private SocketChannel sc;
	private LinkedList<ByteBuffer> outgoingPackets = new LinkedList<ByteBuffer>();
	private boolean writable = true;
	private LinkedList<ByteBuffer> incomingPackets = new LinkedList<ByteBuffer>();
	private ByteBuffer incomingLength;
	private ByteBuffer incomingPacket;
	private State state = State.ReadingLength;
	private SelectionKey sk;
	
	private enum State {
		ReadingLength,
		ReadingPacket
	}
	
	public Connection(SocketChannel sc) {
		this.sc = sc;
		newIncoming();
	}

	public synchronized Collection<ByteBuffer> read() {
		if (incomingPackets.size() == 0) {
			return null;
		}
		Collection<ByteBuffer> rval = incomingPackets;
		incomingPackets = new LinkedList<ByteBuffer>();
		return rval;
	}

	// Naive implementation that calls read() twice for each packet - one to 
	// determine length, one to get the data
	
	public synchronized void eventReadable() throws IOException {
		for (;;) {
			if (state == State.ReadingLength) {
				int bytesRead = sc.read(incomingLength);
				if (bytesRead == -1) {
					// End of stream, peer closed the connection
					throw new IOException();
				}
				if (bytesRead == 0) {
					return; // Nothing left to read, maybe we went round this loop too
							// many times
				}
				if (incomingLength.position() == 4) {
					int length = 0xff000000&(incomingLength.get(3) << 24);
					length |= 0x00ff0000&(incomingLength.get(2) << 16);
					length |= 0x0000ff00&(incomingLength.get(1) << 8);
					length |= 0x000000ff&incomingLength.get(0);
					incomingPacket = ByteBuffer.allocate(length);
					incomingLength.clear();
					state = State.ReadingPacket;
				}
			}
			if (state == State.ReadingPacket) {
				int bytesRead = sc.read(incomingPacket);
				if (bytesRead == -1) {
					// End of stream, peer closed the connection
					throw new IOException();
				}
				if (bytesRead == 0) {
					return; // Nothing read, maybe we went round this loop too
							// many times
				}
				if (incomingPacket.position() == incomingPacket.capacity()) {
					incomingPacket.flip();
					state = State.ReadingLength;
					incomingPackets.add(incomingPacket);
					incomingPacket = null;
				}
			}
		}
	}

	// Alternate implementation of reader that can call read() a lot less, but its buggy when more than one message is
	// incoming. Can be fixed. 
	
//	public synchronized void eventReadable() throws IOException {
//		for (;;) {
//			if (state == State.ReadingLength) {
//				int status = sc.read(incomingLength);
//				if (status == -1) {
//					// End of stream, peer closed the connection
//					throw new IOException();
//				}
//				if (status == 0) {
//					return; // Nothing left to read, maybe we went round this loop too
//							// many times
//				}
//				if (incomingLength.position() >= 4) {
//					int length = 0xff000000&(incomingLength.get(3) << 24);
//					length |= 0x00ff0000&(incomingLength.get(2) << 16);
//					length |= 0x0000ff00&(incomingLength.get(1) << 8);
//					length |= 0x000000ff&incomingLength.get(0);
//					if (incomingLength.capacity() < length + 4)	{
//						incomingPacket = ByteBuffer.allocate(length); + 4 !!!!!! the bug??
//						incomingLength.flip();
//						incomingLength.position(4);
//						incomingPacket.put(incomingLength);
//					} else {
//						incomingPacket = incomingLength;
//						incomingPacket.limit(length+4);
//					}
//					state = State.ReadingPacket;
//				}
//			}
//			if (state == State.ReadingPacket) {
//				if (incomingPacket.position() == incomingPacket.limit()) {
//					incomingPacket.flip();
//					if (incomingPacket == incomingLength) {
//						incomingPacket.position(4);
//					}
//					state = State.ReadingLength;
//					newIncoming();
//					incomingPackets.add(incomingPacket);
//					incomingPacket = null;
//				} else {
//					int status = sc.read(incomingPacket);
//					if (status == -1) {
//						// End of stream, peer closed the connection
//						throw new IOException();
//					}
//					if (status == 0) {
//						return; // Nothing read, maybe we went round this loop too
//								// many times
//					}
//				}
//			}
//		}
//	}
	
	private void newIncoming() {
		incomingLength = ByteBuffer.allocate(4);
	}
	
	public synchronized void eventWritable() throws IOException {
		writable = true;
		trySend();
	}
	
	private void addLengthPacket(ByteBuffer dataPacket) {
		int count = dataPacket.capacity();
		byte[] ba = new byte[4];
		ba[0] = (byte)count;
		ba[1] = (byte)(count>>8);
		ba[2] = (byte)(count>>16);
		ba[3] = (byte)(count>>24);
		ByteBuffer length = ByteBuffer.wrap(ba);
		outgoingPackets.add(length);
	}
	
	public synchronized void write(ByteBuffer packet) throws IOException {
		addLengthPacket(packet);
		outgoingPackets.add(packet);
		trySend();
	}

	public synchronized void write(ByteBuffer[] packets) throws IOException {
		for (int i = 0; i < packets.length; i++) {
			addLengthPacket(packets[i]);
			outgoingPackets.add(packets[i]);
		}
		trySend();
	}

	public synchronized void write(Collection<ByteBuffer> p) throws IOException {
		for(ByteBuffer b : p) {
			addLengthPacket(b);
			outgoingPackets.add(b);
		}
		trySend();
	}

	private synchronized void trySend() throws IOException {
		if (!writable) return;
		
		for (;;) {
			if (outgoingPackets.size() == 0) {
				try {
					if (sk != null) sk.interestOps(sk.interestOps() & ~SelectionKey.OP_WRITE);
				} catch (CancelledKeyException e) { e.printStackTrace(); } // FIXME: Document this exception
				return;
			}
			
			// Putting everything we have into one array so we can call sc.write() only once seems to be a little bit quicker
			ByteBuffer[] array = new ByteBuffer[outgoingPackets.size()];
			int i = 0;
			long totalBytes = 0; 
			for (ByteBuffer b : outgoingPackets) {
				array[i++] = b;
				totalBytes += b.capacity();
			}
			
			long sent = sc.write(array);
			
			if (sent < totalBytes){
				System.err.println( "trySend(): Not all bytes sent: " + sent + " out of " + totalBytes );
			}

			// Remove all byte buffers that have been fully sent, and if there
			// are any part-sent ones, register an interest
			do {
				ByteBuffer bb = outgoingPackets.getFirst();
				if (bb.remaining() > 0) {
					writable = false;
					if (sk != null) sk.interestOps(sk.interestOps() | SelectionKey.OP_WRITE);
					return;
				}
				outgoingPackets.removeFirst();
			} while (outgoingPackets.size() > 0);			
			
//			ByteBuffer bb = outgoingPackets.getFirst();
//			sc.write(bb);
//			if (bb.remaining() > 0) {
//				writable = false;
//				return;
//			}
//			outgoingPackets.removeFirst();
		}
	}
	
	public synchronized void close() {
		if (closing) return;
		closing = true;
		Socket socket = sc.socket();
		try {
			if (!socket.isInputShutdown()) socket.shutdownInput();
		} catch (IOException e) {
			log.info("Ignoring IOException shutting down input");
		}
		try {
			socket.shutdownOutput();
		} catch (IOException e) {
			log.info("Ignoring IOException shutting down output");
		}
		try {
			socket.close();
		} catch (IOException e) {
			log.info("Ignoring IOException closing socket");
		}
		try {
			sc.close();
		} catch (IOException e) {
			log.info("Ignoring IOException closing socketChannel");
		}
	}

	public SocketChannel getSocketChannel() {
		return sc;
	}

	public void setSelectionKey(SelectionKey sk) {
		this.sk = sk;
	}
}
