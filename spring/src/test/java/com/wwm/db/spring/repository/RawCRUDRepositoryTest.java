package com.wwm.db.spring.repository;


import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:/raw-crud-repository-context.xml"})
@DirtiesContext(classMode=ClassMode.AFTER_CLASS) // take care to ensure committed data is different :)
public class RawCRUDRepositoryTest {

	@Autowired
	private CrudRepository<PrimaryKeyedItem, String> repo;
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldGetIllegalArgumentExceptionIfEntityNotSerializable() {
		new RawCRUDRepository<System, String>(System.class);
	}
	
	@Test
	public void savedObjectShouldBeRetrievedByKey() {
		repo.save(new PrimaryKeyedItem("test1@here.com", "blahasdfsdf"));
		PrimaryKeyedItem found1 = repo.findOne("test1@here.com");
		
		PrimaryKeyedItem found = found1;
		assertNotNull(found);
	}

	
	@Test(expected=DuplicateKeyException.class)
	public void insertWithExistingPrimaryKeyShouldFail() {
		repo.save(new PrimaryKeyedItem("unique@here.com", "xx"));
		repo.save(new PrimaryKeyedItem("unique@here.com", "yy"));
	}
}
