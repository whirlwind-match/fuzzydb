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
package com.wwm.db.internal.comms.messages;

import com.wwm.io.packet.messages.Command;

public class OpenStoreCmd extends Command {

	private static final long serialVersionUID = 1L;

	private String openStoreName;

	/**
	 * Compile time constructor.. rather than leaving JRE to do run time generation
	 */
	public OpenStoreCmd() {
		super();
	}
	
	public OpenStoreCmd(int cid, String storeName) {
		super(0, cid);
		this.openStoreName = storeName;
	}

	public String getStoreName() {
		return openStoreName;
	}
}
