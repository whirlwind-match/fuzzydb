package com.wwm.attrs;

import com.wwm.attrs.enums.EnumDefinition;

/**
 * A service to maintain a mapping between numerical identifiers and string values
 * for attribute names and enum values.
 * 
 * @author Neale Upstone
 */
public interface AttributeDefinitionService {

	/**
	 * Get the numerical attribute identifier for the given attribute
	 * name as an object of the given type.
	 * 
	 * @param clazz - null allowed
	 * 
	 * @throws RuntimeException if the attribute name is already in use
	 * for a different type.  
	 */
	int getAttrId(String attrName, Class<?> clazz);
	
	int getAttrId(String attrName);

	String getAttrName(int attrId);


	/**
	 * Return an EnumDefinition given a name for the EnumDefinition.
	 * Once we have this, we can then create enum values, by supplying
	 * an attribute name and value string.
	 *    EnumDefinition def = attrDefMgr.getEnumDefinition( "SmokingStates" );
	 *    EnumValue = def.getValue( "Smoke", "GivingUp" );
	 */
	EnumDefinition getEnumDefinition(String defName);

	EnumDefinition getEnumDef(short enumDefId);

	/**
	 * Associate this attrId with given enumDef, if not already done so.
	 * Returns quickly if already done.
	 */
	void associateAttrToEnumDef(int attrId, EnumDefinition enumDef);

	EnumDefinition getEnumDefForAttrId(int attrId);

}