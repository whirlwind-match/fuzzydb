package com.wwm.db.spring.transaction;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import com.wwm.db.exceptions.UnknownObjectException;

public class WhirlwindExceptionTranslator implements
		PersistenceExceptionTranslator {

	public DataAccessException translateExceptionIfPossible(RuntimeException ex) {
		if (ex instanceof UnknownObjectException) {
			return new EmptyResultDataAccessException(1);
		}
//		if (ex instanceof ArchException) {

		return null;
	}
}
