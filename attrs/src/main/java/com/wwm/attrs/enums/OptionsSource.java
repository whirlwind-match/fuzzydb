package com.wwm.attrs.enums;

public interface OptionsSource {

	/**
	 * Get String representation of an option given the index
	 */
	String findAsString(short index);

	/**
	 * Number of options in this Enum definition
	 * @return int size
	 */
	int size();

	
	String getName();

}