package org.fuzzydb.attrs.converters;

import org.fuzzydb.attrs.location.EcefVector;
import org.fuzzydb.dto.attributes.Point3DAttribute;
import org.springframework.core.convert.converter.Converter;


// TODO. This could convert to IPoint3D
public class Point3DAttributeToEcefVectorConverter implements Converter<Point3DAttribute, EcefVector> {

	@Override
	public EcefVector convert(Point3DAttribute source) {
		return (EcefVector) source.getValueAsObject();
	}

}
