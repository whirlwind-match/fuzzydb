package com.wwm.db.spring;

import org.slf4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import com.wwm.attrs.AttributeDefinitionService;
import com.wwm.attrs.WWConfigHelper;
import com.wwm.attrs.internal.SyncedAttrDefinitionMgr;
import com.wwm.attrs.internal.XStreamHelper;
import com.wwm.context.JVMAppListener;
import com.wwm.db.Store;
import com.wwm.db.core.LogFactory;
import com.wwm.util.DynamicRef;

/**
 * Intialise store with attribute and scorer configuration.
 * 
 * By default will look in classpath:/attribute/*.xml for attribute definitions.
 * 
 * @author Neale Upstone
 *
 */
public class StoreInitializer implements InitializingBean {

	
	static private final Logger log = LogFactory.getLogger(StoreInitializer.class);
	
	private String autoResourceBase = "classpath*:";
	
	private Store store;
	
//	@Autowired
//	@Qualifier("resource")
	private String /* TODO: Resource */ resourcePath;
	
	
	public void afterPropertiesSet() throws Exception {
		// FIXME: Remove everything to do with the next 3 lines!
        JVMAppListener.getInstance().setSingleSession();
        JVMAppListener.getInstance().preRequest();
//        IndexerFactory.setCurrentStoreUrl("wwmdb:/" + store.getStoreName()); // TODO: Should be able to ask store for it's URL... ?
        
        // Init by convention for now
        DynamicRef<? extends AttributeDefinitionService> attrDefs = SyncedAttrDefinitionMgr.getInstance(store);
        XStreamHelper.loadAttributeDefs(autoResourceBase + "/attributes/*.xml", attrDefs);
        
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources(resourcePath);

        for (Resource resource : resources) {
        	log.info("Loading match style from: {}", resource.getURL());
        	WWConfigHelper.updateScorerConfig(store, resource.getInputStream());
		}
	}

	@Autowired
	public void setStore(Store store) {
		this.store = store;
	}
	
	public void setResource(String resource) {
		this.resourcePath = resource;
	}
}
