package com.wwm.db.spring.repository;

import java.util.Map;

public class SimpleAttributeMatchQuery implements AttributeMatchQuery {

	private final Map<String, Object> attributes;
	private final String matchStyle;
	private final int maxResults;

	public SimpleAttributeMatchQuery(Map<String, Object> attributes,
			String matchStyle, int maxResults) {
		this.attributes = attributes;
		this.matchStyle = matchStyle;
		this.maxResults = maxResults;
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}
	
	public String getMatchStyle() {
		return matchStyle;
	}
	
	public int getMaxResults() {
		return maxResults;
	}
}
