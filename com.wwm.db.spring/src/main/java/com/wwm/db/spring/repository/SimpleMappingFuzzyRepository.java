package com.wwm.db.spring.repository;


import org.springframework.data.repository.CrudRepository;

import com.wwm.attrs.userobjects.BlobStoringWhirlwindItem;
import com.wwm.db.GenericRef;

/**
 * A Repository implementation that performs a minimal conversion to get attributes
 * in and out of the database
 *
 * The 
 * 
 * @author Neale Upstone
 *
 * @param <T> the type being stored
 * @param <ID> the type for the external id
 */
public class SimpleMappingFuzzyRepository<T> extends AbstractConvertingRepository<BlobStoringWhirlwindItem, T, GenericRef<T>> implements CrudRepository<T, GenericRef<T>> {

	public SimpleMappingFuzzyRepository(Class<T> type) {
		super(type);
	}

	@Override
	protected T fromInternal(BlobStoringWhirlwindItem internal) {
		throw new UnsupportedOperationException(); // Need to implement converters next
	}

	@Override
	protected BlobStoringWhirlwindItem toInternal(T external) {
		throw new UnsupportedOperationException(); // Need to implement converters next
	}

	@SuppressWarnings("unchecked")
	@Override
	protected final GenericRef<BlobStoringWhirlwindItem> toInternalId(GenericRef<T> id) {
		// Externally we ref as GenericRef<T>  and we are using the real ref here
		return (GenericRef<BlobStoringWhirlwindItem>) id;
	}
}
