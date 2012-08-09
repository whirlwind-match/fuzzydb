package org.fuzzydb.spring.repository;

import org.springframework.util.Assert;

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
		Assert.notNull(attributes, "attributes must not be null");
		Assert.notNull(matchStyle, "matchStyle must not be null");
		
		this.attributes = attributes;
		this.matchStyle = matchStyle;
		this.maxResults = maxResults;
	}

	@Override
	public QT getQueryTarget() {
		return attributes;
	}
	
	@Override
	public String getMatchStyle() {
		return matchStyle;
	}
	
	@Override
	public int getMaxResults() {
		return maxResults;
	}
}
