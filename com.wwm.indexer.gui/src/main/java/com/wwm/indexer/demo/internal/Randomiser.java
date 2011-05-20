package com.wwm.indexer.demo.internal;

import java.util.TreeMap;

import whirlwind.config.gui.WhirlwindDemoConfig;

import com.wwm.db.Client;

public interface Randomiser {
	public void processIndexItem(TreeMap<String, Object> attributes);
	public void processSearchItem(TreeMap<String, Object> attributes);

	public void setClient(Client client);
	public void setWhirlwindDemoConfig(WhirlwindDemoConfig cfg);
}
