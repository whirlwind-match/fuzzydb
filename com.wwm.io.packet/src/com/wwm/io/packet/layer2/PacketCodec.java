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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Logger;

import com.wwm.db.core.LogFactory;
import com.wwm.io.packet.ArchInStream;
import com.wwm.io.packet.ArchOutStream;
import com.wwm.io.packet.ClassLoaderInterface;
import com.wwm.io.packet.ClassTokenCache;
import com.wwm.io.packet.exceptions.ClassRepeatedException;
import com.wwm.io.packet.layer1.PacketInterface;
import com.wwm.io.packet.messages.Loggable;
import com.wwm.io.packet.messages.Message;
import com.wwm.io.packet.messages.PacketMessage;

public class PacketCodec implements MessageInterface {
	
	static private Logger log = LogFactory.getLogger(PacketCodec.class);
	
	private final PacketInterface pi;	// The underlying layer interface
	private final ClassLoaderInterface cli;	// The resource which provides class storage services
	
	// Packet types
	private static final int messageCommand = 1;		// Message is data for higher layer
	private static final int messageGetClassReq = 2;	// Message is a request for class between this layer peers
	private static final int messageGetClassRes = 3;	// Message is a response for class between this layer peers
	
//	private static float totalSerialTime = 0;
//	private static int serialCount = 0;
//	private static float totalDeserialTime = 0;
//	private static int deserialCount = 0;

	/**Packets which failed to decode due to class load exceptions are parked here until we can get the class data
	 */
	private final HashMap<String, ArrayList<ByteBuffer>> pendingPackets = new HashMap<String, ArrayList<ByteBuffer>>();	
	
	public PacketCodec(PacketInterface pi, ClassLoaderInterface cli) {
		this.pi = pi;
		this.cli = cli;
	}

	/**
	 * Read packets from underlying PacketInterface, including resolving unavailable classes by retrieving 
	 * the class data from the peer.
	 * @return as many messages as were available, skipping over any messages for which we don't have
	 * 
	 * FIXME: This seems untested and broken.  It skips messages for which we don't have class data, 
	 * causing a re-ordering.  Surely incorrect..!!
	 */
	public synchronized Collection<PacketMessage> read() throws IOException {
		Collection<ByteBuffer> packets = pi.read();
		if (packets == null) {
			return null;
		}
		
		ArrayList<PacketMessage> messages = new ArrayList<PacketMessage>();
		try {
			for (ByteBuffer bb : packets) {
				ArrayList<Message> decoded = decode(bb);
				// If the message decoded (i.e. we can create the objects) then add them to messages collection.
				// Any messages that we had to make requests
				if (decoded != null) {
					// TODO (nu->ac): why is this only for instances of loggable?  PacketMessage documentation doesn't explain it either
					if (decoded.size() == 1 && decoded.get(0) instanceof Loggable) {
						messages.add(new PacketMessage(decoded.get(0), bb));
					} else {
						for (Message m : decoded) {
							messages.add(new PacketMessage(m, null));
						}
					}
				} else {
					// NU - Added 27May08 to ensure we don't skip messages.  
					throw new Error("Attempted unsupported operation. We need to ensure that all packets are processed in order");
				}
			}
		} catch (IOException e) {
			// IO exceptions should not occur on a fixed buffer unless there is a software fault.
			// Otherwise, if the data is corrupt, we will ignore the packet.
			// We may want to throw this up as an exception to disconnect the client.
			throw(e);
		}
		return (messages.size() > 0) ? messages : null;
	}

	private final ClassTokenCache ctc = new ClassTokenCache(false);

	public void requestClassData(int storeId, String className) throws IOException {
		log.info("requesting class data, as load class failed for: " + className + " using classloader: " 
				+ getClass().getClassLoader() );

		// Issue class upload request to peer
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ArchOutStream out = newOutputStream(baos, storeId);
		
		out.writeInt(messageGetClassReq);
		out.writeInt(storeId);
		out.writeObject(className);
		send(baos.toByteArray());
	}
	
	private ArrayList<Message> decode(ByteBuffer bb) throws IOException {
		ArchInStream in = newInputStream(bb);
		int messageType = in.readInt();
		int storeId = in.readInt();
		switch (messageType) {
		case messageCommand:
			try {
				return decodeCommand(in, storeId);
			} catch (InvalidClassException e){ // Because IO stream converts ClassNotFoundException to this.
				String className = e.getCause().getMessage();
				handleMissingClass(bb, storeId, className);
			} catch (ClassNotFoundException e) {
				String className = e.getMessage(); // TODO: Need to process this string to get just the class name
				handleMissingClass(bb, storeId, className);
			}
			return null;
		case messageGetClassReq:
			decodeGetClassReq(in, storeId);
			return null;
		case messageGetClassRes:
			return decodeGetClassRes(in, storeId);
		default:
			return null;
		}
	}

	private void handleMissingClass(ByteBuffer bb, int storeId, String className) throws IOException {
		ArrayList<ByteBuffer> al = pendingPackets.get(className);
		if (al == null) {
			al = new ArrayList<ByteBuffer>();
			pendingPackets.put(className, al);
		}
		al.add(bb);
		requestClassData(storeId, className);
	}

	private ArrayList<Message> decodeGetClassRes(ArchInStream in, int storeId) throws IOException {
		try {
			String className = (String)in.readObject();
			int length = in.readInt();
			byte[] bytecode = new byte[length];
			if (length != in.read(bytecode)) {
				throw new IOException();
			}
			try {
				cli.addClass(storeId, className, bytecode);
			} catch (ClassRepeatedException e) {
				throw new IOException();
			}
			
			ArrayList<ByteBuffer> unblock;
			ArrayList<Message> rvals = new ArrayList<Message>();
			unblock = pendingPackets.remove(className);
			if (unblock != null) {
				for (ByteBuffer b : unblock)
				{
					rvals.addAll(decode(b));
				}
				if (rvals.size() > 0) {
					return rvals;
				}
			}
		} catch (ClassNotFoundException e) {
			// The data is corrupt/in some unknown format
			// so we just throw this as an IOException like all other similar errors
			throw new IOException();
		}
		return null;
	}

	private void decodeGetClassReq(ArchInStream in, int storeId) throws IOException {
		try {
			String className = (String)in.readObject();
			byte[] bytecode = cli.getClassBytecode(storeId, className);
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ArchOutStream out = newOutputStream(baos, storeId);
			
			out.writeInt(messageGetClassRes);
			out.writeInt(storeId);
			out.writeObject(className);
			out.writeInt(bytecode.length);
			out.write(bytecode);
			send(baos.toByteArray());
		} catch (ClassNotFoundException e) {
			// The data is corrupt/in some unknown format
			// so we just throw this as an IOException like all other similar errors
			throw new IOException();
		}
	}

	private void send(byte[] bs) throws IOException {
		ByteBuffer bb = ByteBuffer.wrap(bs);
		pi.write(bb);
	}

	private ArrayList<Message> decodeCommand(ArchInStream in, int storeId) throws IOException, ClassNotFoundException {
		Message m;
		try {
			synchronized (PacketCodec.class) {
//				long start = System.currentTimeMillis();
			
				m = (Message)in.readObject();

//				long duration = System.currentTimeMillis() - start;
//				totalDeserialTime += duration;
//				deserialCount++;
//				if (deserialCount % 100 == 0) {
//					System.out.println("Mean deserialization time: " + totalDeserialTime/deserialCount + "ms, Total deserialization time: " + totalDeserialTime + "ms");
//					deserialCount = 0;
//					totalDeserialTime = 0;
//				}
			}
		} catch (ClassNotFoundException e){
			throw e; // for breakpoint
		} catch (ClassCastException e) {
			// The data is corrupt/in some unknown format
			// so we just throw this as an IOException like all other similar errors
			throw new IOException(e.getMessage());
		}
		ArrayList<Message> rval = new ArrayList<Message>();
		rval.add(m);
		return rval;
	}

	public synchronized void send(Message m) throws IOException {
		ByteArrayOutputStream baos = null;
		synchronized (PacketCodec.class) {
			baos = new ByteArrayOutputStream();
			ArchOutStream out = newOutputStream(baos, m.getStoreId());
			
			out.writeInt(messageCommand);
			out.writeInt(m.getStoreId());
			out.writeObject(m);
			out.flush();
			out.close();
		}
		send(baos.toByteArray());
	}


	public synchronized void send(Message[] m) throws IOException {
		for (int i = 0; i < m.length; i++) {
			send(m[i]);
		}
	}

	public synchronized void send(Collection<Message> m) throws IOException {
		for (Message mess : m) {
			send(mess);
		}
	}

	public synchronized void close() {
		pi.close();
	}
	
    // Returns an output stream for a ByteBuffer.
    // The write() methods use the relative ByteBuffer put() methods.
    private ArchOutStream newOutputStream(final ByteArrayOutputStream buf, int storeId) throws IOException {
        return ArchOutStream.newOutputStream(buf, storeId, ctc);
    }
    
    // Returns an input stream for a ByteBuffer.
    // The read() methods use the relative ByteBuffer get() methods.
    private ArchInStream newInputStream(final ByteBuffer buf) throws IOException {
    	return ArchInStream.newInputStream(buf, ctc, cli);
    }

}
