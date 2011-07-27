package com.wwm.db.spring.repository;


import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:/raw-crud-repository-context.xml"})
@DirtiesContext(classMode=ClassMode.AFTER_CLASS) // take care to ensure committed data is different :)
public class RawCRUDRepositoryTest {

	@Autowired
	private RawCRUDRepository<PrimaryKeyedItem, String> repo;
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldGetIllegalArgumentExceptionIfEntityNotSerializable() {
		new RawCRUDRepository<System, String>(System.class);
	}
	
	@Test
	public void savedObjectShouldBeRetrievedByKey() {
		save(new PrimaryKeyedItem("test1@here.com", "blahasdfsdf"));
		
		PrimaryKeyedItem found = findByKey("test1@here.com");
		assertNotNull(found);
	}

	
	@Test(expected=DuplicateKeyException.class)
	public void insertWithExistingPrimaryKeyShouldFail() {
		save(new PrimaryKeyedItem("unique@here.com", "xx"));
		save(new PrimaryKeyedItem("unique@here.com", "yy"));
	}
	
	
	@Transactional(readOnly=true)
	private PrimaryKeyedItem findByKey(String key) {
		PrimaryKeyedItem found = repo.findOne(key);
		return found;
	}
	
	@Transactional
	private void save(PrimaryKeyedItem item) {
		repo.save(item);
	}
}
