package com.wwm.db.spring;

import java.io.InputStream;

import org.slf4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import com.wwm.attrs.WWConfigHelper;
import com.wwm.context.JVMAppListener;
import com.wwm.db.Store;
import com.wwm.db.Transaction;
import com.wwm.db.core.LogFactory;

public class StoreInitializer implements InitializingBean {

	
	static private final Logger log = LogFactory.getLogger(StoreInitializer.class);
	
	private Store store;
	
//	@Autowired
//	@Qualifier("resource")
	private String /* TODO: Resource */ resourcePath;
	
	
	public void afterPropertiesSet() throws Exception {
		// FIXME: Remove everything to do with the next 2 lines!
        JVMAppListener.getInstance().setSingleSession();
        JVMAppListener.getInstance().preRequest();
        
		Resource resource = new DefaultResourceLoader().getResource(resourcePath);
		final InputStream inputStream = resource.getInputStream();
		
		// TODO: Review.  Is there any reason not to use a provided/default txMgr ?   
//		new TransactionTemplate(new WhirlwindPlatformTransactionManager(store)).execute( new TransactionCallback<Object>() {
//			public Void doInTransaction(TransactionStatus status) {
//				WWConfigHelper.updateScorerConfig(store, inputStream);
//				return null;
//			}
//		});

		Transaction tx = store.begin();
		WWConfigHelper.updateScorerConfig(store, inputStream);
		tx.commit();

		
		log.info("Loaded scorer config: {}", resourcePath);
	}

	@Autowired
	public void setStore(Store store) {
		this.store = store;
	}
	
	public void setResource(String resource) {
		this.resourcePath = resource;
	}
}
