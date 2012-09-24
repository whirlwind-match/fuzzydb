package org.fuzzydb.client.userobjects;


import java.io.Serializable;

import org.bson.types.ObjectId;
import org.fuzzydb.core.annotations.Key;


public class BsonObjectIdUniqueKeyedObject implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Key(unique=true)
	private ObjectId key;
	private int value; // not indexed

	public BsonObjectIdUniqueKeyedObject(ObjectId key, int id) {
		this.key = key;
		this.value = id;
	}

	public ObjectId getKey() {
		return key;
	}

	public void setKey(ObjectId key) {
		this.key = key;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int id) {
		this.value = id;
	}
}
