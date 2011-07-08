package com.wwm.db.spring.repository;


import com.wwm.db.GenericRef;

public interface FuzzyRepository<T> extends WhirlwindCrudRepository<T, GenericRef<T>>, WhirlwindSearch<T> {

}
