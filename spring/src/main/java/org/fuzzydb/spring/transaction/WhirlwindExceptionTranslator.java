package org.fuzzydb.spring.transaction;

import org.fuzzydb.client.exceptions.KeyCollisionException;
import org.fuzzydb.client.exceptions.UnknownObjectException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.support.PersistenceExceptionTranslator;

import com.wwm.db.core.exceptions.ArchException;

public class WhirlwindExceptionTranslator implements
		PersistenceExceptionTranslator {

	@Override
	public DataAccessException translateExceptionIfPossible(RuntimeException ex) {
		DataAccessException e = doTranslate(ex);
		if (e == null) {
			return null;
		}
		
		e.initCause(ex);
		return e;
		
	}
	
	protected DataAccessException doTranslate(RuntimeException ex) {
		if (!(ex instanceof ArchException)) {
			return null;
		}

		if (ex instanceof UnknownObjectException) {
			return new EmptyResultDataAccessException(1);
		}
		if (ex instanceof KeyCollisionException) {
			return new DuplicateKeyException(ex.getMessage());
		}

		return null; // can't translate
	}
}
