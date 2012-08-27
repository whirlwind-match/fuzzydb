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


import org.fuzzydb.io.core.MessageSource;
import org.fuzzydb.io.packet.CommsStack;


public interface ConnectionManager extends MessageSource {
	//void closeAllConnections();
	void addConnection(CommsStack stack);
	int getNumberOfConnections();
}
