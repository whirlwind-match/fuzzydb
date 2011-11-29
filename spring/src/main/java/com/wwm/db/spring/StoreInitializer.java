package com.wwm.db.spring;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import com.wwm.attrs.AttributeDefinitionService;
import com.wwm.attrs.WWConfigHelper;
import com.wwm.attrs.enums.EnumDefinition;
import com.wwm.attrs.internal.SyncedAttrDefinitionMgr;
import com.wwm.attrs.internal.XStreamHelper;
import com.wwm.context.JVMAppListener;
import com.wwm.db.Store;
import com.wwm.db.core.LogFactory;
import com.wwm.util.DynamicRef;

/**
 * Initialise store with attribute and scorer configuration.
 * <p>
 * By default will look in classpath*:/attribute/*.xml for attribute definitions,
 * and classpath*:/enums/*.xml for enum definitions.
 * <p>
 * This will target the 'default' namespace within the store.
 * <p>
 * Future support will allow different fuzzy indexes configurations
 * within the same store.
 * 
 * @author Neale Upstone
 *
 */
public class StoreInitializer implements InitializingBean {

	
	static private final Logger log = LogFactory.getLogger(StoreInitializer.class);
	
	private String autoResourceBase = "classpath*:/fuzzy/";
	
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
        @SuppressWarnings("unused")
		Map<String, Object> loadAttributeDefs = XStreamHelper.loadAttributeDefs(autoResourceBase + "attributes/*.xml", attrDefs);
        
        TreeMap<String, EnumDefinition> loadEnumDefs = XStreamHelper.loadEnumDefs(autoResourceBase + "enums/*.xml", attrDefs);
        
        for (Entry<String, EnumDefinition> def : loadEnumDefs.entrySet()) {
        	// recreate with ADS
			EnumDefinition value = def.getValue();
			EnumDefinition newDef = attrDefs.getObject().getEnumDefinition(value.getName());
			ArrayList<String> values = def.getValue().getValues();
			newDef.getMultiEnum((String[]) values.toArray(new String[values.size()]), -1);
		}
        

        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        if (resourcePath == null) {
        	resourcePath = autoResourceBase + "matchers/*.xml";
        }
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
