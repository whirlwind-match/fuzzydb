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

import com.archopolis.db.exceptions.ArchException;
import com.wwm.io.packet.messages.Command;
import com.wwm.io.packet.messages.Response;

public interface ClientConnectionManager {

	public Response execute(Authority authority, Command command) throws ArchException;
	public void close();
	public void requestClassData(Authority authority, int storeId, String className) throws IOException;
}
