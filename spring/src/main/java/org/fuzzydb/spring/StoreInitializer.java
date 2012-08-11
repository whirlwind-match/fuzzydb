package org.fuzzydb.spring;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.fuzzydb.attrs.AttributeDefinitionService;
import org.fuzzydb.attrs.WWConfigHelper;
import org.fuzzydb.attrs.enums.EnumDefinition;
import org.fuzzydb.attrs.internal.SyncedAttrDefinitionMgr;
import org.fuzzydb.attrs.internal.XStreamHelper;
import org.fuzzydb.client.Store;
import org.fuzzydb.core.LogFactory;
import org.fuzzydb.util.DynamicRef;
import org.fuzzydb.util.context.JVMAppListener;
import org.slf4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;


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
	
	
	@Override
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
			newDef.getMultiEnum(values.toArray(new String[values.size()]), -1);
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
