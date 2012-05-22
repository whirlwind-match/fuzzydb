package com.wwm.indexer.internal.random;

import com.wwm.model.attributes.Attribute;
import com.wwm.model.attributes.RandomGenerator;
import com.wwm.util.MTRandom;

public abstract class AbstractRandomGenerator<RESULT extends Attribute<?>> implements RandomGenerator<RESULT> {

	private final float nullProportion;

	public AbstractRandomGenerator() {
		nullProportion = 0f;
	}
	
	/**
	 * 
	 * @param nullProportion - the proportion of results for which to return null
	 * i.e. 0.01, will result in 1% of the random values being null
	 */
	public AbstractRandomGenerator(float nullProportion) {
		this.nullProportion = nullProportion;
	}

	
	public final RESULT next(String attrName) {
	    float rand = MTRandom.getInstance().nextFloat();
	    if (rand < nullProportion) {
	        return null;
	    }
	
	    return randomResult(attrName);
	}


	abstract protected RESULT randomResult(String attrName);

}