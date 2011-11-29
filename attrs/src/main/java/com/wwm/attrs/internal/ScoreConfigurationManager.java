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
package com.wwm.attrs.internal;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;

import com.wwm.attrs.IScoreConfiguration;


/**
 * Manage access to score configurations.
 * TODO: This needs migrating to something that can be configured from the DbClient.
 * @author Neale
 *
 */
public class ScoreConfigurationManager implements Serializable {

	private static final long serialVersionUID = 1L;

	private HashMap<String, ScoreConfiguration> configs = new HashMap<String, ScoreConfiguration>();


	/**
	 * Constructor.
	 */
	public ScoreConfigurationManager() {
	}

	/**
	 * Add/update the specified configuration.  Note: This will overwrite previous configuration
	 * so if in doubt, call getConfig(name) first.
	 */
	public void setConfig(String name, ScoreConfiguration config) {
		config.assertValid();
		configs.put( name, config );
	}

	public IScoreConfiguration getConfig(String name) {
		ScoreConfiguration config = configs.get( name );
		if ( config == null ) {
			config = new ScoreConfiguration(); // FIXME: add (name) - compiler prob is preventing this at moment
			setConfig( name, config );
		}
		return config;
	}

	public Set<String> getConfigs() {
		return configs.keySet();
	}

	public boolean isConfig(String name) {
		return configs.get(name) != null;
	}

	public void reset() {
		configs = new HashMap<String, ScoreConfiguration>();
	}
}
