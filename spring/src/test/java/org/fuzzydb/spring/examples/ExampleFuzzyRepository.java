package org.fuzzydb.spring.examples;

import org.fuzzydb.spring.repository.FuzzyItem;
import org.fuzzydb.spring.repository.FuzzyRepository;


/**
 * Sample interface for which we want a fuzzy repository
 * 
 *  @author Neale Upstone
 */
public interface ExampleFuzzyRepository extends FuzzyRepository<FuzzyItem,String> {

	
}
