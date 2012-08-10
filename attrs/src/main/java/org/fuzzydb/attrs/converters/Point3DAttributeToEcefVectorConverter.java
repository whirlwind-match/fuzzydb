package org.fuzzydb.attrs.converters;

import org.fuzzydb.attrs.location.EcefVector;
import org.springframework.core.convert.converter.Converter;

import com.wwm.model.attributes.Point3DAttribute;

// TODO. This could convert to IPoint3D
public class Point3DAttributeToEcefVectorConverter implements Converter<Point3DAttribute, EcefVector> {

	@Override
	public EcefVector convert(Point3DAttribute source) {
		return (EcefVector) source.getValueAsObject();
	}

}
