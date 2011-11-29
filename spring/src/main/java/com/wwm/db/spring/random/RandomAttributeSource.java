package com.wwm.db.spring.random;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.wwm.attrs.AttributeDefinitionService;
import com.wwm.attrs.enums.EnumDefinition;
import com.wwm.indexer.internal.random.RandomBoolean;
import com.wwm.indexer.internal.random.RandomEnum;
import com.wwm.indexer.internal.random.RandomFloat;
import com.wwm.indexer.internal.random.RandomMultiEnum;
import com.wwm.model.attributes.Attribute;
import com.wwm.model.attributes.RandomGenerator;

@Component
public class RandomAttributeSource {

	@Autowired
	private AttributeDefinitionService attributeService;
	
	/**
	 * Default random generators to use based on the attribute class
	 */
	private Map<Class<?>, RandomGenerator<?>> classRandomisers = new HashMap<Class<?>, RandomGenerator<?>>();

	private Map<String, RandomGenerator<?>> attrRandomisers = new HashMap<String, RandomGenerator<?>>();
	
	
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
		int attrId = attributeService.getAttrId(attrName);
		EnumDefinition enumDef = attributeService.getEnumDefForAttrId(attrId);
		attrRandomisers.put(attrName, new RandomEnum(enumDef, nullProportion));
	}

	public void configureMultiEnumAttr(String attrName, float nullProportion) {
		int attrId = attributeService.getAttrId(attrName);
		EnumDefinition enumDef = attributeService.getEnumDefForAttrId(attrId);
		attrRandomisers.put(attrName, new RandomMultiEnum(enumDef, nullProportion));
	}


	public void addRandomGenerator(String attrName, RandomGenerator<? extends Attribute<?>> generator) {
		attrRandomisers.put(attrName, generator);
	}
}
