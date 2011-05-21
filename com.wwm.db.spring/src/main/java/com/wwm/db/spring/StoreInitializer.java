package com.wwm.db.spring;

import org.slf4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import com.wwm.attrs.WWConfigHelper;
import com.wwm.context.JVMAppListener;
import com.wwm.db.Store;
import com.wwm.db.core.LogFactory;

public class StoreInitializer implements InitializingBean {

	
	static private final Logger log = LogFactory.getLogger(StoreInitializer.class);
	
	@Autowired
	private Store store;
	
//	@Autowired
//	@Qualifier("resource")
	private String /* TODO: Resource */ resourcePath;
	
	
	public void afterPropertiesSet() throws Exception {
		// FIXME: Remove everything to do with the next 3 lines!
        JVMAppListener.getInstance().setSingleSession();
        JVMAppListener.getInstance().preRequest();
//        IndexerFactory.setCurrentStoreUrl("wwmdb:/" + store.getStoreName()); // TODO: Should be able to ask store for it's URL... ?
        
		Resource resource = new DefaultResourceLoader().getResource(resourcePath);
		WWConfigHelper.updateScorerConfig(store, resource.getInputStream());
		
		log.info("Loaded scorer config: {}", resourcePath);
	}

	public void setResource(String resource) {
		this.resourcePath = resource;
	}
}
