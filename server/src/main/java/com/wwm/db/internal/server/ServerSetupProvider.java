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
package com.wwm.db.internal.server;

import java.io.File;

import org.springframework.util.Assert;

import com.wwm.db.core.Settings;

public class ServerSetupProvider {

	private String diskRoot = Settings.getInstance().getDbRoot();
	private String txDiskPath = "tx";
	private String logDiskPath = "log";
	private String reposDiskPath = "repos";
	
	
	private void assertWriteableDirectory(String directoryPath) {
		File dir = new File(directoryPath);
		
		if (dir.isDirectory() && dir.canWrite()) {
			return;
		}
		Assert.state(dir.mkdirs(), "Could not create directory: " + directoryPath);
	}


	public String getReposDiskRoot() {
		return diskRoot + File.separator +reposDiskPath;
	}
	
	public String getLogDiskRoot() {
		return diskRoot + File.separator +logDiskPath;
	}
	
	public String getTxDiskRoot() {
		assertWriteableDirectory(diskRoot);
		return diskRoot + File.separator +txDiskPath;
	}
	
	
}
