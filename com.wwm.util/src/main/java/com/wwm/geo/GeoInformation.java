package com.wwm.geo;

/**
 * Interface implemented by objects that can provide geographical information
 * 
 * @author Neale Upstone
 */
public interface GeoInformation {

	String getCounty();

	float getLatitude();

	float getLongitude();

	String getTown();

}