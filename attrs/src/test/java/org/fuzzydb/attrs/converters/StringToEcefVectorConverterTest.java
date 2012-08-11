package org.fuzzydb.attrs.converters;

import static org.junit.Assert.*;

import org.fuzzydb.attrs.converters.StringToEcefVectorConverter;
import org.fuzzydb.attrs.location.EcefVector;
import org.fuzzydb.dto.dimensions.IPoint3D;
import org.junit.Test;
import org.springframework.core.convert.converter.Converter;


public class StringToEcefVectorConverterTest {

	private final Converter<String,IPoint3D> converter = new StringToEcefVectorConverter(null);
	
	@Test
	public void testConvertTupleWithWhitespace() {
		
		EcefVector result = (EcefVector) converter.convert("{ 53.02 , -0.103 }");
		
		assertEquals(53.02, result.getLatDegs(), 0.0001);
		assertEquals(-0.103, result.getLonDegs(), 0.0001);
	}

}
