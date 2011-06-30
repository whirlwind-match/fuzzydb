package com.wwm.db.spring.repository;

import java.util.Map;

/**
 * A query which specifies a set of attributes a subject for which we want
 * to find the best matches, as defined by the supplied match style.
 * 
 * @author Neale Upstone
 */
public interface AttributeMatchQuery {
	
	/**
	 * Attributes that we want to find the closes match for
	 */
	Map<String, Object> getAttributes();
	
	/**
	 * Maximum number of results to return
	 */
	int getMaxResults();
	
	/**
	 * Name of the style of matching to do
	 */
	String getMatchStyle();

}
