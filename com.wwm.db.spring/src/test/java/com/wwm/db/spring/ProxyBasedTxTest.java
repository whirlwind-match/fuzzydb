package com.wwm.db.spring;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.wwm.db.Store;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:/annotation-driven-proxy-tx-context.xml"})
public class ProxyBasedTxTest {
	
	@Autowired
	private Store store;
	
	@Test
	public void doh(){
		
	}
	

}
