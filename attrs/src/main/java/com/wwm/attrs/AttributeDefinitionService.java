package com.wwm.attrs;

import com.wwm.attrs.enums.EnumDefinition;
import com.wwm.db.whirlwind.internal.IAttribute;

/**
 * A service to maintain a mapping between numerical identifiers and string
 * values for attribute names and enum values.
 * 
 * @author Neale Upstone
 */
public interface AttributeDefinitionService {

	/**
	 * Read-only attempt to get an attribute id. Once an attribute is defined
	 * for a store, it is immutable, so we can cache this value.
	 * 
	 * @return null if there is no association defined
	 * @throws IllegalStateException
	 *             if the attribute name is already in use for a different type.
	 */
	// WIP public Integer getExistingAttrId(String attrName, Class<?> clazz);

	/**
	 * Get the numerical attribute identifier for the given attribute name as an
	 * object of the given type.
	 * 
	 * @param clazz
	 *            (null allowed) Where supplied this is a class that is
	 *            compatible with the attribute, either for inbound or outbound
	 *            conversion. (e.g. a String[] is valid for a multi-enum
	 *            attribute).
	 * 
	 * @throws IllegalStateException
	 *             if the attribute name is already in use for a different type.
	 */
	int getAttrId(String attrName, Class<?> clazz);

	/**
	 * Get the numerical attribute identifier for the given name.
	 * The attribute must already exist.
	 * 
	 * @throws IllegalStateException
	 *            if the attribute was not already defined
	 */

	int getAttrId(String attrName);

	/**
	 * Get the textual name for the given attribute
	 * 
	 * @param attrId
	 *            id of the already allocated attribute
	 * @return String name for this attribute
	 * @throws IllegalArgumentException
	 *             if there is no attribute registered for the supplied
	 *             attributeId
	 */
	String getAttrName(int attrId);

	/**
	 * Return the class used to represent this attribute in Java. Some of these
	 * are JDK value object classes, and others are classes defined for use with
	 * Whirlwind (e.g. FloatRange)
	 * 
	 * @param attrId
	 *            id within the database
	 * @return Class<?> e.g. Boolean.class, Float.class
	 */
	Class<?> getExternalClass(int attrId);

	/**
	 * Return the class that is used within the database to encode this
	 * attibute.
	 * 
	 * @param attrId
	 *            id within the database
	 * @return Class<?> e.g. BooleanValue.class, Enum ... etc
	 */
	Class<? extends IAttribute> getDbClass(int attrId);

	/**
	 * Return an EnumDefinition given a name for the EnumDefinition. Once we
	 * have this, we can then create enum values, by supplying an attribute name
	 * and value string. EnumDefinition def = attrDefMgr.getEnumDefinition(
	 * "SmokingStates" ); EnumValue = def.getValue( "Smoke", "GivingUp" );
	 */
	EnumDefinition getEnumDefinition(String defName);

	EnumDefinition getEnumDef(short enumDefId);

	/**
	 * Associate this attrId with given enumDef, if not already done so. Returns
	 * quickly if already done.
	 */
	void associateAttrToEnumDef(int attrId, EnumDefinition enumDef);

	EnumDefinition getEnumDefForAttrId(int attrId);

}