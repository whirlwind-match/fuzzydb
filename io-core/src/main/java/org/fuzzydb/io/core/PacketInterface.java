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
package org.fuzzydb.io.core;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collection;

/**This interface provides ByteBuffer packet access to an underlying communications resource.
 * Packets are transmitted and recieved atomically, they are not fragmented or concatenated.
 * Typically an implementor will provide a mechanism to convert from a streaming to packetted
 * mechanism (if the underlying implementation is TCP) or a datagram fragment to big packet
 * mechanism (if the underlying implementation is UDP) etc.
 * @author ac
 *
 */
public interface PacketInterface {
	
	/**Reads as many complete packets as are available.
	 * If no complete packets are available, this method returns null.
	 * Never returns an empty Collection.
	 * This method is fast and does not block on IO if the underlying transport is a network.
	 * It is expected that this method will be called in response to some event signifying 
	 * readability; this mechanism is outside this interface.
	 * @return A collection of ByteBuffers that are flipped for relative get() operations
	 */
	public Collection<ByteBuffer> read();
	
	
	/**Writes a packet out.
	 * This method is fast and does not block on IO if the underlying transport is a network.
	 * @param b A ByteBuffer that is flipped for relative get() operations
	 * @throws IOException 
	 */
	public void write(ByteBuffer b) throws IOException;
	
	/**Writes several packets out.
	 * This method is fast and does not block on IO if the underlying transport is a network.
	 * @param b An array of ByteBuffer that are flipped for relative get() operations
	 * @throws IOException 
	 */
	public void write(ByteBuffer b[]) throws IOException;
	
	/**Writes several packets out.
	 * This method is fast and does not block on IO if the underlying transport is a network.
	 * @param b A collection of ByteBuffers that are flipped for relative get() operations
	 * @throws IOException 
	 */
	public void write(Collection<ByteBuffer> b) throws IOException;
	
	/**Safely close the underlying resources.
	 * This is provided as non-memory resources are expected to be allocated (e.g. sockets)
	 * so we don't want to rely on the memory GC to clean them up.
	 */
	public void close();

}
