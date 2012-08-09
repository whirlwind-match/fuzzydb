package org.fuzzydb.spring.repository;

import java.io.Serializable;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Extends {@link CrudRepository} with some Whirlwind-specific operations
 * 
 * @author Neale Upstone
 */
public interface WhirlwindCrudRepository<T, ID extends Serializable> extends PagingAndSortingRepository<T,ID> {

	/**
	 * Sometimes it's helpful to just find the first of our target class (e.g. where 
	 * a repository is used to store only one of some configuration)  
	 */
	T findFirst();
}
