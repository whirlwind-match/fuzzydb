package com.wwm.indexer.config;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import whirlwind.config.gui.WhirlwindDemoConfig;

import com.thoughtworks.xstream.XStream;
import com.wwm.attrs.Scorer;
import com.wwm.attrs.SplitConfiguration;
import com.wwm.attrs.WWConfigHelper;
import com.wwm.attrs.WhirlwindConfiguration;
import com.wwm.attrs.XMLAliases;
import com.wwm.attrs.enums.EnumDefinition;
import com.wwm.attrs.internal.AttrDefinitionMgr;
import com.wwm.attrs.internal.SyncedAttrDefinitionMgr;
import com.wwm.attrs.internal.XStreamHelper;
import com.wwm.attrs.internal.xstream.AttributeIdMapper;
import com.wwm.attrs.internal.xstream.TableToPreferenceMapConverter;
import com.wwm.attrs.internal.xstream.XmlNameMapper;
import com.wwm.db.Store;
import com.wwm.indexer.IndexerFactory;
import com.wwm.indexer.internal.random.RandomGenerator;
import com.wwm.util.DynamicRef;
import com.wwm.util.ResourcePatternProcessor;


public class XmlLoader {

//    private static final Logger log = LogFactory.getLogger(XmlLoader.class);

    private String xmlPath;

    private DynamicRef<? extends AttrDefinitionMgr> attrDefs;

    private TreeMap<String, Object> attributes;
    private TreeMap<String, EnumDefinition> enumDefs;
    private TreeMap<String, Scorer> scorers;
    private ArrayList<String> scorerCfgs = new ArrayList<String>();
    private TreeMap<String, SplitConfiguration> splitters;
    private TreeMap<String, ArrayList<?>> strategyCfgs;
    private TreeMap<String, RandomGenerator> randGenerators;

    private WhirlwindDemoConfig demoCfg;

    public XmlLoader(String xmlPath, WhirlwindConfiguration conf) {

    	xmlPath = "classpath:" + xmlPath;
    	
        final Store store = IndexerFactory.getCurrentStore();

        new ResourcePatternProcessor(){
			@Override
			protected Closeable process(Resource resource) throws IOException {
				String filename = resource.getFilename();
				String name = filename.substring(0, filename.indexOf('.'));
				scorerCfgs.add(name);
				InputStream stream = resource.getInputStream();
				WWConfigHelper.updateScorerConfig(store, stream);
				return stream;
			}
        
        }.runWithResources(xmlPath + "/scorerconfigs/*.xml");
        
        new ResourcePatternProcessor(){
			@Override
			protected Closeable process(Resource resource) throws IOException {
				InputStream stream = resource.getInputStream();
				WWConfigHelper.updateIndexConfig(store, stream);
				return stream;
			}
        }.runWithResources(xmlPath + "/indexstrategies/*.xml");


        this.xmlPath = xmlPath;

        // ----------------------------------------------------------------
        // Load the Attributes map
        // ----------------------------------------------------------------
        attrDefs = SyncedAttrDefinitionMgr.getInstance(store); // This will be correct call
        // ----------------------------------------------------------------




        // ----------------------------------------------------------------
        // ENUMS
        // ----------------------------------------------------------------
        enumDefs = XStreamHelper.loadEnumDefs(xmlPath + "/enums/*.xml", attrDefs);
        
		for (Entry<String, EnumDefinition> entry : enumDefs.entrySet()) {
			String strippedName = entry.getKey().substring(0, entry.getKey().length() - 4);// Strip off .xml name
			conf.add(strippedName, entry.getValue());
		}  

        // ----------------------------------------------------------------

        // ----------------------------------------------------------------
        // ATTRIBUTES
        // ----------------------------------------------------------------
        attributes = (TreeMap<String, Object>) XStreamHelper.loadAttributeDefs(xmlPath + "/attributes/*.xml", attrDefs);
        // ----------------------------------------------------------------

        // ----------------------------------------------------------------
        // SPLITTERS
        // ----------------------------------------------------------------
        //        // ----------------------------------------------------------------
        //        // INDEX STRATEGIES
        //        // ----------------------------------------------------------------
        //        XStream strategyXStream = new XStream();
        //        strategyXStream.registerConverter(new XmlNameMapper<SplitConfiguration>(SplitConfiguration.class, splitters));
        //        addScorerAliases(strategyXStream);
        //        strategyCfgs = XmlLoader.load(strategyXStream, ArrayList.class, xmlPath + "/indexstrategies/*.xml");
        // ----------------------------------------------------------------

        // ----------------------------------------------------------------
        // RANDOM GENERATORS
        // ----------------------------------------------------------------
        XStream randXStream = new XStream();
        randXStream.registerConverter(new XmlNameMapper<EnumDefinition>(EnumDefinition.class, enumDefs));
        randGenerators = XStreamHelper.loadResources(randXStream, RandomGenerator.class, xmlPath + "/randomisers/*.xml");
        // ----------------------------------------------------------------

        // ----------------------------------------------------------------
        // DEMO CFG
        // ----------------------------------------------------------------
        try {
            XStream demoXStream = new XStream();
            demoXStream.setClassLoader( getClass().getClassLoader() ); // OSGi: We need it to use our classLoader, as it's own bundle won't help it :)

            demoXStream.registerConverter(new XmlNameMapper<RandomGenerator>(RandomGenerator.class, randGenerators));
            demoXStream.alias("RandomGenerator", RandomGenerator.class);
            demoCfg = (WhirlwindDemoConfig) demoXStream.fromXML(new DefaultResourceLoader().getResource(xmlPath + "/demo.xml").getInputStream());
        } catch (IOException e) { throw new RuntimeException(e); }
        // ----------------------------------------------------------------
    }

	public static XStream getScorerXStream() {
        Store store = IndexerFactory.getCurrentStore();
        DynamicRef<? extends AttrDefinitionMgr> attrDefs = SyncedAttrDefinitionMgr.getInstance(store);
        XStream scorerXStream = new XStream();
        scorerXStream.registerConverter(new AttributeIdMapper(attrDefs));
        scorerXStream.registerConverter( new TableToPreferenceMapConverter(attrDefs));
        addScorerAliases(scorerXStream);
        return scorerXStream;
    }

    public static XStream getIndexConfigXStream() {
        Store store = IndexerFactory.getCurrentStore();
        DynamicRef<SyncedAttrDefinitionMgr> attrDefs = SyncedAttrDefinitionMgr.getInstance(store);
        XStream xs = new XStream();
        xs.registerConverter(new AttributeIdMapper(attrDefs));
        XMLAliases.applyIndexConfigAliases(xs);
        return xs;
    }


    static private void addScorerAliases(XStream xStream) {
        XMLAliases.applyScorerAliases(xStream);

    }

	public TreeMap<String, Object> getAttributes() {
        return attributes;
    }

    public TreeMap<String, EnumDefinition> getEnumDefs() {
        return enumDefs;
    }

    public TreeMap<String, RandomGenerator> getRandGenerators() {
        return randGenerators;
    }

    public ArrayList<String> getScorerCfgs() {
        return scorerCfgs;
    }

    public TreeMap<String, Scorer> getScorers() {
        return scorers;
    }

    public TreeMap<String, SplitConfiguration> getSplitters() {
        return splitters;
    }

    public TreeMap<String, ArrayList<?>> getStrategyCfgs() {
        return strategyCfgs;
    }

    public String getXmlPath() {
        return xmlPath;
    }

    public DynamicRef<? extends AttrDefinitionMgr> getAttrDefs() {
        return attrDefs;
    }

    public WhirlwindDemoConfig getDemoCfg() {
        return demoCfg;
    }
}
