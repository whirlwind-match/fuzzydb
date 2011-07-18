package com.wwm.db.spring.repository;


import com.wwm.db.Ref;

public interface FuzzyRepository<T> extends WhirlwindCrudRepository<T, Ref<T>>, WhirlwindSearch<T> {

}
