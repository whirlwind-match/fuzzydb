package org.fuzzydb.spring.repository;


import java.util.Iterator;


import org.fuzzydb.core.query.Result;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface WhirlwindSearch<T> {

	/**
	 * Find the matches which best fit the given query and return them
	 * in order of highest score first.
	 */
	Iterator<Result<T>> findMatchesFor(AttributeMatchQuery<T> query);
	
	/**
	 * Find the matches which best fit the given query and return them
	 * in order of highest score first.
	 */
	Page<Result<T>> findMatchesFor(AttributeMatchQuery<T> query, Pageable pageable);

}
