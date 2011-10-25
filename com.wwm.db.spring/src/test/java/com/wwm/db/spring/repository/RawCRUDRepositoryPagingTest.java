package com.wwm.db.spring.repository;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:/raw-crud-repository-context.xml"})
@DirtiesContext(classMode=ClassMode.AFTER_CLASS) // take care to ensure committed data is different :)
public class RawCRUDRepositoryPagingTest {

	@Autowired
	private PagingAndSortingRepository<PrimaryKeyedItem, String> repo;

	// TODO: will need to be before class, or move into context initialisation 
	@Before
	public void setUpTestData() {
		for (int i = 0; i < 11; i++) {
			repo.save(new PrimaryKeyedItem("item #" + i, "xx"));
		}
	}
	
	@Ignore("Implementation is WIP")
	@Test
	public void multipleResultsShouldBePageable() {

	Pageable pageable = new PageRequest(0, 6);
	Page<PrimaryKeyedItem> page = repo.findAll(pageable);
	
	Assert.assertThat(page.getNumber(), CoreMatchers.equalTo(0));
	Assert.assertThat(page.getNumberOfElements(), CoreMatchers.equalTo(6));
	Assert.assertThat(page.getSize(), CoreMatchers.equalTo(6));
	Assert.assertThat(page.getTotalElements(), CoreMatchers.equalTo(11L));
	Assert.assertThat(page.getTotalPages(), CoreMatchers.equalTo(2));
	
	pageable = new PageRequest(1, 6);
	page = repo.findAll(pageable);
	
	Assert.assertThat(page.getNumber(), CoreMatchers.equalTo(1));
	Assert.assertThat(page.getNumberOfElements(), CoreMatchers.equalTo(5));
	Assert.assertThat(page.getSize(), CoreMatchers.equalTo(5));
	Assert.assertThat(page.getTotalElements(), CoreMatchers.equalTo(11L));
	Assert.assertThat(page.getTotalPages(), CoreMatchers.equalTo(2));
	
	}
}
