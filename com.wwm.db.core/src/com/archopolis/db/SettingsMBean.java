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
package com.archopolis.db;

import java.net.InetSocketAddress;

public interface SettingsMBean {

	public boolean isWindows();

	public String getPrimaryServer();

	public int getPrimaryServerPort();

	public String getSecondaryServer();

	public int getSecondaryServerPort();

	public boolean getCompressLogs();

	public boolean getXmlLogs();

	public String getDbRoot();

	public String getPostcodeRoot();

	public String getIpLookupRoot();

	public int getListenPort();

	public String getLogDir();

	public String getReposDir();

	public String getTxDir();

	public int getLeafCriticalMass();

	public int getDefaultTargetNumResults();

	public float getDefaultScoreThreshold();

	public int getQueryInactivityTimeoutSecs();

	public int getQueryTimeToLiveSecs();

	public int getSearchInactivityTimeoutSecs();

	/**
	 * Maximum time a transaction can live from start of transaction
	 */
	public int getSearchTimeToLiveSecs();

	/**
	 * Maximum time since last active
	 */
	public int getTransactionInactivityTimeoutSecs();

	public int getTransactionTimeToLiveSecs();

	public String getDefaultStore();

	public boolean isSlave();

	public InetSocketAddress getParentNode();

}