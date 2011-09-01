package com.wwm.db.spring.random;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.wwm.attrs.AttributeDefinitionService;
import com.wwm.indexer.internal.random.RandomBoolean;
import com.wwm.indexer.internal.random.RandomFloat;
import com.wwm.indexer.internal.random.RandomGenerator;
import com.wwm.model.attributes.Attribute;

@Component
public class RandomAttributeSource {

	@Autowired
	private AttributeDefinitionService attributeService;
	
	/**
	 * Default random generators to use based on the attribute class
	 */
	private Map<Class<?>, RandomGenerator> classRandomisers = new HashMap<Class<?>, RandomGenerator>();

	private Map<String, RandomGenerator> attrRandomisers = new HashMap<String, RandomGenerator>();
	
	
	public RandomAttributeSource() {
		classRandomisers.put(Boolean.class, new RandomBoolean(50, 50));
	}
	
	
	public Attribute<?> getRandom(String attrName) {
		Attribute<?> attr = getConfiguredRandom(attrName);
		if (attr != null) {
			return attr;
		}
		
		Class<?> attrClass = attributeService.getExternalClass(attributeService.getAttrId(attrName));
		RandomGenerator generator = classRandomisers.get(attrClass);
		return generator == null ? null : generator.next(attrName);
	}


	private Attribute<?> getConfiguredRandom(String attrName) {
		RandomGenerator generator = attrRandomisers.get(attrName);
		return generator == null ? null : generator.next(attrName);
	}


	public void configureFloatAttr(String attrName, float min, float max, int nullPercent) {
		attrRandomisers.put(attrName, new RandomFloat(min, max, nullPercent));
	}
}
