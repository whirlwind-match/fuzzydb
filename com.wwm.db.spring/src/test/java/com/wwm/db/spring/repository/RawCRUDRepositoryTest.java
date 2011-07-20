package com.wwm.db.spring.repository;


import org.junit.Test;

public class RawCRUDRepositoryTest {

	
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldGetIllegalArgumentExceptionIfEntityNotSerializable() {
		new RawCRUDRepository<RawCRUDRepositoryTest, String>(RawCRUDRepositoryTest.class);
	}
	
	@Test
	public void savedObjectShouldBeRetrievedByKey() {
		
		// WIP: for now just check we don't get exception
		new RawCRUDRepository<PrimaryKeyedItem, String>(PrimaryKeyedItem.class);
		
	}
}
