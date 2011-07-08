package com.wwm.db.spring.repository;

import java.io.Serializable;

import org.springframework.data.repository.CrudRepository;

/**
 * Extends {@link CrudRepository} with some Whirlwind-specific operations
 * 
 * @author Neale Upstone
 */
public interface WhirlwindCrudRepository<T, ID extends Serializable> extends CrudRepository<T,ID> {

	/**
	 * Sometimes it's helpful to just find the first of our target class (e.g. where 
	 * a repository is used to store only one of some configuration)  
	 */
	T findFirst();
}
