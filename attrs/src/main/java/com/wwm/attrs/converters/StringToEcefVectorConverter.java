package com.wwm.attrs.converters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;

import com.wwm.attrs.location.EcefVector;
import com.wwm.geo.GeoInformation;
import com.wwm.model.dimensions.IPoint3D;

/**
 * This assumes that you've stuck a converter in that converts strings to GeoInformation
 */
public class StringToEcefVectorConverter implements Converter<String, IPoint3D> {

	private final Converter<String,GeoInformation> stringToGeo;
	
	@Autowired
	public StringToEcefVectorConverter(Converter<String, GeoInformation> converter) {
		this.stringToGeo = converter;
	}
	
	@Override
	public IPoint3D convert(String source) {
		// Handle Tuple of floats {lat, lon}.  Need a better strategy, such as fallback converters
		if (source.startsWith("{")) {
			return fromLatLon(source);
		}
		
		GeoInformation geo = stringToGeo.convert(source);
		return EcefVector.fromDegs(0, geo.getLatitude(), geo.getLongitude());
	}

	/**
	 * source must be formatted: {lat, lon}  (can tolerate whitespace that Float.valueOf(String) will
	 */
	private IPoint3D fromLatLon(String source) {
		
		int comma = source.indexOf(',');
		int closingBrace = source.indexOf('}');
		Float lat = Float.valueOf(source.substring(1, comma));
		Float lon = Float.valueOf(source.substring(comma + 1, closingBrace));
		
		return EcefVector.fromDegs(0, lat, lon);
	}
}
