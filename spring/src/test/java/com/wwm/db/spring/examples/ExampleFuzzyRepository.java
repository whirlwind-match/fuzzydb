package com.wwm.db.spring.examples;

import com.wwm.db.spring.repository.FuzzyItem;
import com.wwm.db.spring.repository.FuzzyRepository;

/**
 * Sample interface for which we want a fuzzy repository
 * 
 *  @author Neale Upstone
 */
public interface ExampleFuzzyRepository extends FuzzyRepository<FuzzyItem,String> {

	
}
