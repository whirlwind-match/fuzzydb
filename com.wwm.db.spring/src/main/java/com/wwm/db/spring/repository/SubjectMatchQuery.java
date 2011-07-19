package com.wwm.db.spring.repository;

/**
 * A SubjectMatchQuery looks for the best matches for a provided subject, according to the
 * requested match style.
 *  
 * @author Neale Upstone
 */
public class SubjectMatchQuery<QT> implements AttributeMatchQuery<QT> {

	private final QT attributes;
	private final String matchStyle;
	private final int maxResults;

	public SubjectMatchQuery(QT attributes,
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
