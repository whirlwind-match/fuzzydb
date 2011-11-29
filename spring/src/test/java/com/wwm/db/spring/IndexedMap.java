package com.wwm.db.spring;

import java.io.Serializable;
import java.util.HashMap;

import com.wwm.db.annotations.Key;

public class IndexedMap implements Serializable {
	private static final long serialVersionUID = 1L;

	private final @Key(unique=true) String key;
	
	private final HashMap<String, Object> stuff = new HashMap<String, Object>();
	
	public IndexedMap(String key) {
		this.key = key;
	}
	
	public String getKey() {
		return key;
	}
	
	public void put(String key, Object value) {
		stuff.put(key, value);
	}
	
	public Object get(String key) {
		return stuff.get(key);
	}
}