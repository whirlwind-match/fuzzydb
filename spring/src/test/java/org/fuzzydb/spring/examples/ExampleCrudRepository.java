package org.fuzzydb.spring.examples;

import org.fuzzydb.spring.repository.PrimaryKeyedItem;
import org.springframework.data.repository.CrudRepository;


/**
 * Sample interface for which we want a mapping repository
 * 
 *  @author Neale Upstone
 */
public interface ExampleCrudRepository extends CrudRepository<PrimaryKeyedItem, String> {

	
}
