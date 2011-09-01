package com.wwm.db.spring.repository;


import java.io.Serializable;

public interface FuzzyRepository<T, ID extends Serializable> extends WhirlwindCrudRepository<T, ID>, WhirlwindSearch<T> {

}
