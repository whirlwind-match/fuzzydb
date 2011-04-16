package com.wwm.db.spring.transaction;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.PersistenceExceptionTranslator;

import com.wwm.db.core.exceptions.ArchException;

public class WhirlwindExceptionTranslator implements
		PersistenceExceptionTranslator {

	@Override
	public DataAccessException translateExceptionIfPossible(RuntimeException ex) {
//		if (ex instanceof ArchException) {

		return null;
	}

	public DataAccessException translateExceptionIfPossible(ArchException e) {
		return null;
	}

}
