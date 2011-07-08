package com.wwm.db.spring.repository;


import org.springframework.data.repository.CrudRepository;

import com.wwm.db.GenericRef;

public interface FuzzyRepository<T> extends CrudRepository<T, GenericRef<T>>, WhirlwindSearch<T> {

}
