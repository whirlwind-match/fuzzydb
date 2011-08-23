package com.wwm.db.spring.examples;

import org.springframework.data.repository.CrudRepository;

import com.wwm.db.spring.repository.PrimaryKeyedItem;

/**
 * Sample interface for which we want a mapping repository
 * 
 *  @author Neale Upstone
 */
public interface ExampleCrudRepository extends CrudRepository<PrimaryKeyedItem, String> {

	
}
