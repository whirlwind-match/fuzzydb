package com.wwm.db.spring.repository;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.oxm.xstream.XStreamMarshaller;

import com.thoughtworks.xstream.XStream;

public class RepositoryInitializerTest {

	@Mock
	private CrudRepository<PrimaryKeyedItem, String> repo;

	@Captor
	ArgumentCaptor<PrimaryKeyedItem> captor;

	@Captor
	ArgumentCaptor<Iterable<PrimaryKeyedItem>> listCaptor;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void singleObjectFromXmlFileShouldBePersisted() throws ClassNotFoundException {

		Mockito.when(repo.save(captor.capture())).thenAnswer(new Answer<PrimaryKeyedItem>() {
			public PrimaryKeyedItem answer(InvocationOnMock invocation) throws Throwable {
				return (PrimaryKeyedItem) invocation.getArguments()[0];
			}
		});

		// the action
		RepositoryInitializer<PrimaryKeyedItem, String> initializer = initializerforRepository(repo);
		initializer.setResources("classpath:/keyedItem.xml");
		initializer.afterPropertiesSet();

		// verify
		List<PrimaryKeyedItem> capturedValues = captor.getAllValues();
		assertThat(capturedValues.size(), is(1));

		PrimaryKeyedItem one = capturedValues.get(0);
		assertThat(one.getEmail(), is("one@one.com"));
	}

	@Test
	public void multipleObjectsFromXmlFileShouldBePersisted() throws ClassNotFoundException {

		Mockito.when(repo.save(listCaptor.capture())).thenAnswer(new Answer<Iterable<PrimaryKeyedItem>>() {
			@SuppressWarnings("unchecked")
			public Iterable<PrimaryKeyedItem> answer(InvocationOnMock invocation) throws Throwable {
				return (Iterable<PrimaryKeyedItem>) invocation.getArguments()[0];
			}
		});

		// the action
		RepositoryInitializer<PrimaryKeyedItem, String> initializer = initializerforRepository(repo);
		initializer.setResources("classpath:/keyedItems.xml");
		initializer.afterPropertiesSet();

		// verify
		List<Iterable<PrimaryKeyedItem>> capturedValues = listCaptor.getAllValues();
		assertThat(capturedValues.size(), is(1));
		Iterator<PrimaryKeyedItem> it = capturedValues.get(0).iterator();

		PrimaryKeyedItem one = it.next();
		assertThat(one.getEmail(), is("one@one.com"));

		PrimaryKeyedItem two = it.next();
		assertThat(two.getEmail(), is("two@two.com"));
	}

	
	
	public void exportItems() throws IOException {
		XStream xs = new XStream();
		xs.aliasType("items", ArrayList.class);

		PrimaryKeyedItem one = new PrimaryKeyedItem("one@one.com", "sdfsdfsdf");
		PrimaryKeyedItem two = new PrimaryKeyedItem("two@two.com", "asdfsdfsd");

		ArrayList<PrimaryKeyedItem> list = new ArrayList<PrimaryKeyedItem>(2);
		list.add(one);
		list.add(two);

		FileOutputStream os = new FileOutputStream("keyedItems.xml", false);
		xs.toXML(list, os);
		os.close();

	}

	public static <T, ID extends Serializable> RepositoryInitializer<T,ID> initializerforRepository(CrudRepository<T, ID> repo) throws ClassNotFoundException {
		XStreamMarshaller xstreamUnmarshaller = new XStreamMarshaller();
		xstreamUnmarshaller.setAliases(Collections.singletonMap("objects", ArrayList.class));
		return new RepositoryInitializer<T, ID>(repo, xstreamUnmarshaller);
	}
}
