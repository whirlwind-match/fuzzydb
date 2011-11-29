package com.wwm.db.spring.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.convert.converter.Converter;

/**
 * Marks a field as derived from another field using an appropriate Spring {@link Converter}.
 * <p>
 * The derivation will only be used if the field is null once the entity has been materialised.
 * 
 * @author Neale Upstone
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DerivedField {

	/**
	 * The field from which to convert 
	 */
	String value();

}
