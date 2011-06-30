package com.wwm.db.spring.repository;


public class SimpleAttributeMatchQuery<QT> implements AttributeMatchQuery<QT> {

	private final QT attributes;
	private final String matchStyle;
	private final int maxResults;

	public SimpleAttributeMatchQuery(QT attributes,
			String matchStyle, int maxResults) {
		this.attributes = attributes;
		this.matchStyle = matchStyle;
		this.maxResults = maxResults;
	}

	public QT getQueryTarget() {
		return attributes;
	}
	
	public String getMatchStyle() {
		return matchStyle;
	}
	
	public int getMaxResults() {
		return maxResults;
	}
}
