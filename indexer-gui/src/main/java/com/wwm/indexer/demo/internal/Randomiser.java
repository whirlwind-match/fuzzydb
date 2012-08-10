package com.wwm.indexer.demo.internal;

import java.util.TreeMap;

import org.fuzzydb.client.Client;

import whirlwind.config.gui.WhirlwindDemoConfig;


public interface Randomiser {
	public void processIndexItem(TreeMap<String, Object> attributes);
	public void processSearchItem(TreeMap<String, Object> attributes);

	public void setClient(Client client);
	public void setWhirlwindDemoConfig(WhirlwindDemoConfig cfg);
}
