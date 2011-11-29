package com.wwm.db.spring.repository;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Persistent;

@Persistent
public class FuzzyItem implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String description;
	
	private final Map<String, Object> attributes = new HashMap<String,Object>();
	
	@Id
	private String ref;

	private String[] newspapers;
	
	private String smoke;
	
	/**
	 * Public constructor needed by some frameworks
	 */
	public FuzzyItem() {
	}
	
	public FuzzyItem(String desc) {
		this.description = desc;
	}

	public Object getAttr(String name) {
		return attributes.get(name);
	}
	
	public void setAttr(String name, Object value) {
		attributes.put(name, value);
	}
	
	public String getDescription() {
		return description;
	}
	
	public String[] getNewspapers() {
		return newspapers;
	}
	
	public void setNewspapers(String[] newspapers) {
		this.newspapers = newspapers;
	}
	
	public String getSmoke() {
		return smoke;
	}

	public void setSmoke(String smoke) {
		this.smoke = smoke;
	}

	public String getRef() {
		return ref;
	}
	
	@Override
	public String toString() {
		return description;
	}
}