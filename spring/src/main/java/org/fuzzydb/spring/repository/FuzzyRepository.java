package org.fuzzydb.spring.repository;


import java.io.Serializable;

public interface FuzzyRepository<T, ID extends Serializable> extends WhirlwindCrudRepository<T, ID>, WhirlwindSearch<T> {

}
