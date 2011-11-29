package com.wwm.geo;

/**
 * Interface implemented by objects that can provide geographical information
 * 
 * @author Neale Upstone
 */
public interface GeoInformation {

	String getCounty();

	/**
	 * Latitude in degrees
	 */
	float getLatitude();

	/**
	 * Longitude in degrees 
	 */
	float getLongitude();

	String getTown();

}