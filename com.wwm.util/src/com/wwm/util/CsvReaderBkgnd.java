/******************************************************************************
 * Copyright (c) 2004-2008 Whirlwind Match Limited. All rights reserved.
 *
 * This is open source software; you can use, redistribute and/or modify
 * it under the terms of the Open Software Licence v 3.0 as published by the 
 * Open Source Initiative.
 *
 * You should have received a copy of the Open Software Licence along with this
 * application. if not, contact the Open Source Initiative (www.opensource.org)
 *****************************************************************************/
package com.wwm.util;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class CsvReaderBkgnd extends CsvReader implements Runnable {
	private Thread thread;
	private BlockingQueue<Object> queue = new ArrayBlockingQueue<Object>(10);
	
	private Object[] packet;
	private int next = 0;
	public CsvReaderBkgnd(String file, boolean stripQuotes) throws IOException {
		super(file, stripQuotes, false);
	}

	public CsvReaderBkgnd(String file, boolean stripQuotes, boolean hasHeader) throws IOException {
		super(file, stripQuotes, hasHeader, false);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Map<String, Object> readLine() throws IOException, UnsupportedTypeException, GarbageLineException {
		start();
		
		Object o = null;
		try {
			if (packet == null)
			{
				packet = (Object[])queue.take();
				next = 0;
			}
		} catch (InterruptedException e) {
			throw new Error(e);
		}
		o = packet[next++];
		if (next == packet.length) {
			packet = null;
		}
		if (o instanceof UnsupportedTypeException) {
			throw (UnsupportedTypeException)o;
		}
		if (o instanceof GarbageLineException) {
			throw (GarbageLineException)o;
		}
		if (o instanceof IOException) {
			throw (IOException)o;
		}
		
		return (Map<String, Object>) o;
	}
	
	private void start() {
		if (thread == null) {
			thread = new Thread(this);
			thread.start();
		}
	}

	public void run() {
		int size = 100;
		Map<String, Object> map;
		Object[] stuff = new Object[size];
		int i = 0;
		for (;;) {
			
			try {
				map = super.readLine();
			} catch (IOException e) {
				stuff[i] = e;
				try {
					queue.put(stuff);
				} catch (InterruptedException e1) {
					throw new Error(e1);
				}
				thread = null;
				return;
			} catch (UnsupportedTypeException e) {
				stuff[i] = e;
				try {
					queue.put(stuff);
				} catch (InterruptedException e1) {
					throw new Error(e1);
				}
				thread = null;
				return;
			} catch (GarbageLineException e) {
				stuff[i] = e;
				try {
					queue.put(stuff);
				} catch (InterruptedException e1) {
					throw new Error(e1);
				}
				thread = null;
				return;
			}
			stuff[i] = map;
			i++;
			if (i == size) {
				i = 0;
				try {
					queue.put(stuff);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				stuff = new Object[size];
			}
		}
	}
	
}
