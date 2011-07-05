package com.wwm.db.spring.repository;

import com.wwm.db.GenericRef;

/**
 * A Repository implementation that performs no conversion.
 * 
 * This is adequate for objects not requiring any fuzzy matching.  The absence of
 * any conversion therefore requires that the repository is for an embedded instance,
 * or that the Class being persisted is available on the server's classpath.
 * 
 * @author Neale Upstone
 *
 * @param <T>
 */
public class RawFuzzyRepository<T> extends AbstractConvertingRepository<T, T, GenericRef<T>> {

	public RawFuzzyRepository(Class<T> type) {
		super(type);
	}

	@Override
	protected T fromInternal(T internal) {
		return internal;
	}

	@Override
	protected T toInternal(T external) {
		return external;
	}

	@Override
	protected GenericRef<T> toInternalId(GenericRef<T> id) {
		return id;
	}
}
