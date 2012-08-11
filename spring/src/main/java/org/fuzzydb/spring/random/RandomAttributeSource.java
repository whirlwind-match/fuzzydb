package org.fuzzydb.spring.random;

import java.util.HashMap;
import java.util.Map;

import org.fuzzydb.attrs.AttributeDefinitionService;
import org.fuzzydb.dto.attributes.Attribute;
import org.fuzzydb.dto.attributes.OptionsSource;
import org.fuzzydb.dto.attributes.RandomGenerator;
import org.fuzzydb.random.RandomBoolean;
import org.fuzzydb.random.RandomEnum;
import org.fuzzydb.random.RandomFloat;
import org.fuzzydb.random.RandomMultiEnum;
import org.fuzzydb.random.RandomUuid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RandomAttributeSource {

	@Autowired
	private AttributeDefinitionService attributeService;
	
	/**
	 * Default random generators to use based on the attribute class
	 */
	private final Map<Class<?>, RandomGenerator<?>> classRandomisers = new HashMap<Class<?>, RandomGenerator<?>>();

	private final Map<String, RandomGenerator<?>> attrRandomisers = new HashMap<String, RandomGenerator<?>>();
	
	
	public RandomAttributeSource() {
		classRandomisers.put(Boolean.class, new RandomBoolean(50, 50));
	}
	
	
	public Attribute<?> getRandom(String attrName) {
		Attribute<?> attr = getConfiguredRandom(attrName);
		if (attr != null) {
			return attr;
		}
		
		Class<?> attrClass = attributeService.getExternalClass(attributeService.getAttrId(attrName));
		RandomGenerator<?> generator = classRandomisers.get(attrClass);
		return generator == null ? null : generator.next(attrName);
	}


	private Attribute<?> getConfiguredRandom(String attrName) {
		RandomGenerator<?> generator = attrRandomisers.get(attrName);
		return generator == null ? null : generator.next(attrName);
	}


	public void configureFloatAttr(String attrName, float min, float max, float nullProportion) {
		attrRandomisers.put(attrName, new RandomFloat(min, max, nullProportion));
	}

	/**
	 * This could be enhanced to bias towards a given distribution, e.g.
	 * for cars, there are more Fords than Aston Martins
	 */
	public void configureEnumAttr(String attrName, float nullProportion) {
		OptionsSource enumDef = getLazyEnumDef(attrName);
		attrRandomisers.put(attrName, new RandomEnum(enumDef, nullProportion));
	}

	public void configureUuidAttr(String attrName) {
		attrRandomisers.put(attrName, new RandomUuid());
	}
	
	/** 
	 * Get lazy variant as it involves database access which we don't want to do while reading configuration.
	 * Only when generating data in a transaction should we need database access */
	private ByNameEnumDefinition getLazyEnumDef(String attrName) {
//		return attributeService.getEnumDefinition(attrName);
				return new ByNameEnumDefinition(attributeService, attrName);
	}

	public void configureMultiEnumAttr(String attrName, float nullProportion) {
		OptionsSource enumDef = getLazyEnumDef(attrName);
		attrRandomisers.put(attrName, new RandomMultiEnum(enumDef, nullProportion));
	}


	public void addRandomGenerator(String attrName, RandomGenerator<? extends Attribute<?>> generator) {
		attrRandomisers.put(attrName, generator);
	}
}
