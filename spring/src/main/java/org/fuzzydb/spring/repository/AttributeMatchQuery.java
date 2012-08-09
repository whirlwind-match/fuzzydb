package org.fuzzydb.spring.repository;

/**
 * A query which specifies a set of attributes a subject for which we want
 * to find the best matches, as defined by the supplied match style.
 * 
 * @param <QT> type of the query target (the thing we want a close match for)
 * 
 * @author Neale Upstone
 */
public interface AttributeMatchQuery<QT> {
	
	/**
	 * Attributes that we want to find the closes match for
	 */
	QT getQueryTarget();
	
	/**
	 * Maximum number of results to return
	 */
	int getMaxResults();
	
	/**
	 * Name of the style of matching to do
	 */
	String getMatchStyle();

}
