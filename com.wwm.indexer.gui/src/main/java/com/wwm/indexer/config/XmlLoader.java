package com.wwm.indexer.config;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Map.Entry;

import whirlwind.config.gui.WhirlwindDemoConfig;

import com.thoughtworks.xstream.XStream;
import com.wwm.attrs.ManualIndexStrategy;
import com.wwm.attrs.Scorer;
import com.wwm.attrs.SplitConfiguration;
import com.wwm.attrs.WWConfigHelper;
import com.wwm.attrs.WhirlwindConfiguration;
import com.wwm.attrs.bool.BooleanScorer;
import com.wwm.attrs.bool.BooleanSplitConfiguration;
import com.wwm.attrs.dimensions.DimensionSplitConfiguration;
import com.wwm.attrs.enums.EnumDefinition;
import com.wwm.attrs.enums.EnumExclusiveScorerExclusive;
import com.wwm.attrs.enums.EnumExclusiveScorerPreference;
import com.wwm.attrs.enums.EnumExclusiveSplitConfiguration;
import com.wwm.attrs.enums.MultiEnumScorer;
import com.wwm.attrs.enums.SeatsScorer;
import com.wwm.attrs.internal.AttrDefinitionMgr;
import com.wwm.attrs.internal.ScoreConfiguration;
import com.wwm.attrs.internal.SyncedAttrDefinitionMgr;
import com.wwm.attrs.internal.xstream.AttributeIdMapper;
import com.wwm.attrs.internal.xstream.TableToPreferenceMapConverter;
import com.wwm.attrs.internal.xstream.XmlNameMapper;
import com.wwm.attrs.location.LocationAndRangeScorer;
import com.wwm.attrs.location.PathDeviationScorer;
import com.wwm.attrs.location.RangePreferenceScorer;
import com.wwm.attrs.location.VectorDistanceScorer;
import com.wwm.attrs.simple.FloatRangePreferenceScorer;
import com.wwm.attrs.simple.FloatSplitConfiguration;
import com.wwm.attrs.simple.SimilarFloatValueScorer;
import com.wwm.attrs.simple.WeightedSumScorer;
import com.wwm.db.Store;
import com.wwm.indexer.IndexerFactory;
import com.wwm.indexer.internal.EnumAttributeSpec;
import com.wwm.indexer.internal.random.RandomGenerator;
import com.wwm.util.AsymptoticScoreMapper;
import com.wwm.util.DynamicRef;
import com.wwm.util.LinearScoreMapper;


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

        Store store = IndexerFactory.getCurrentStore();


        // FIXME: Make both of the following do all files in teh directory (see below)

        File[] configs = listXMLFiles(new File(xmlPath + "/scorerconfigs"));
        for( File file: configs) {
            String filename = file.getName();
            String name = filename.substring(0, filename.indexOf('.'));
            scorerCfgs.add(name);
            try {
				WWConfigHelper.updateScorerConfig(store, new FileInputStream(file));
			} catch (FileNotFoundException e) {
				// shouldn't happen
				throw new RuntimeException(e);
			}
        }

        configs = listXMLFiles(new File(xmlPath + "/indexstrategies"));
        for( File file: configs) {
//            String filename = file.getName();
//            String name = filename.substring(0, filename.indexOf('.'));
            try {
				WWConfigHelper.updateIndexConfig(store, new FileInputStream(file));
			} catch (FileNotFoundException e) {
				// shouldn't happen
				throw new RuntimeException(e);
			}
        }


        this.xmlPath = xmlPath;

        // ----------------------------------------------------------------
        // Load the Attributes map
        // ----------------------------------------------------------------
        attrDefs = SyncedAttrDefinitionMgr.getInstance(store); // This will be correct call
        // ----------------------------------------------------------------




        // ----------------------------------------------------------------
        // ENUMS
        // ----------------------------------------------------------------
        enumDefs = load(getEnumXStream(), EnumDefinition.class, xmlPath + File.separator + "enums");
        for (Entry<String, EnumDefinition> entry : enumDefs.entrySet()) {
            String strippedName = entry.getKey().substring(0, entry.getKey().length() - 4);// Strip off .xml name
            conf.add(strippedName, entry.getValue());
        }
        // ----------------------------------------------------------------

        // ----------------------------------------------------------------
        // ATTRIBUTES
        // ----------------------------------------------------------------
        attributes = XmlLoader.load(new XStream(), Object.class, xmlPath + File.separator + "attributes");
        for (Entry<String, Object> entry : attributes.entrySet()) {
            String strippedName = entry.getKey().substring(0, entry.getKey().length() - 4);// Strip off .xml name
            if (entry.getValue() instanceof Class) {
                attrDefs.getObject().getAttrId(strippedName, (Class<?>) entry.getValue());
            } else if (entry.getValue() instanceof EnumAttributeSpec) {
                EnumAttributeSpec enumspec = (EnumAttributeSpec) entry.getValue();
                attrDefs.getObject().getAttrId(strippedName, enumspec.clazz);
            }
        }
        // ----------------------------------------------------------------

        // ----------------------------------------------------------------
        // SCORERS
        // ----------------------------------------------------------------
        //        XStream scorerXStream = new XStream();
        //        scorerXStream.registerConverter(new AttributeIdMapper(attrDefs));
        //        addOurAliases(scorerXStream);
        //        scorers = XmlLoader.load(scorerXStream, Scorer.class, xmlPath + File.separator + "scorers");
        // ----------------------------------------------------------------

        // ----------------------------------------------------------------
        // SCORERCFGS
        // ----------------------------------------------------------------
        //        XStream scorerCfgXStream = new XStream();
        //        scorerCfgXStream.registerConverter(new XmlNameMapper<Scorer>(Scorer.class, scorers));
        //        addOurAliases(scorerCfgXStream);
        //        scorerCfgs = XmlLoader.load(scorerCfgXStream, ArrayList.class, xmlPath + File.separator + "scorercfgs");
        // ----------------------------------------------------------------

        // ----------------------------------------------------------------
        // SPLITTERS
        // ----------------------------------------------------------------
        //        XStream splitterXStream = new XStream();
        //        splitterXStream.registerConverter(new AttributeIdMapper(attrDefs));
        //        splitterXStream.registerConverter(new XmlNameMapper<EnumDefinition>(EnumDefinition.class, enumDefs));
        //        TreeMap<String, SplitConfiguration> splitters = XmlLoader.load(splitterXStream, SplitConfiguration.class, xmlPath + File.separator + "indexsplitters");
        //        // ----------------------------------------------------------------
        //
        //        // ----------------------------------------------------------------
        //        // INDEX STRATEGIES
        //        // ----------------------------------------------------------------
        //        XStream strategyXStream = new XStream();
        //        strategyXStream.registerConverter(new XmlNameMapper<SplitConfiguration>(SplitConfiguration.class, splitters));
        //        addScorerAliases(strategyXStream);
        //        strategyCfgs = XmlLoader.load(strategyXStream, ArrayList.class, xmlPath + File.separator + "indexstrategies");
        // ----------------------------------------------------------------

        // ----------------------------------------------------------------
        // RANDOM GENERATORS
        // ----------------------------------------------------------------
        XStream randXStream = new XStream();
        randXStream.registerConverter(new XmlNameMapper<EnumDefinition>(EnumDefinition.class, enumDefs));
        randGenerators = XmlLoader.load(randXStream, RandomGenerator.class, xmlPath + File.separator + "randomisers");
        // ----------------------------------------------------------------

        // ----------------------------------------------------------------
        // DEMO CFG
        // ----------------------------------------------------------------
        try {
            XStream demoXStream = new XStream();
            demoXStream.registerConverter(new XmlNameMapper<RandomGenerator>(RandomGenerator.class, randGenerators));
            demoXStream.alias("RandomGenerator", RandomGenerator.class);
            demoCfg = (WhirlwindDemoConfig) demoXStream.fromXML(new FileReader(xmlPath + "/demo.xml"));
        } catch (FileNotFoundException e) { e.printStackTrace(); } // FIXME: Document this exception
        // ----------------------------------------------------------------
    }

    public static XStream getEnumXStream() {
        Store store = IndexerFactory.getCurrentStore();
        DynamicRef<? extends AttrDefinitionMgr> attrDefs = SyncedAttrDefinitionMgr.getInstance(store);
        XStream xs = new XStream();
        xs.registerConverter(new AttributeIdMapper(attrDefs));
        addEnumAliases(xs);
        return xs;
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
        addIndexConfigAliases(xs);
        return xs;
    }


    static private void addEnumAliases(XStream xStream) {
        // ScoreConfiguration
        xStream.alias("EnumDefinition", EnumDefinition.class);
        xStream.useAttributeFor(EnumDefinition.class, "name");
        // ensure contained elements are added to scorersList
        xStream.addImplicitCollection(EnumDefinition.class, "strValues");
    }

    static private void addScorerAliases(XStream xStream) {
        // ScoreConfiguration
        xStream.alias("ScoreConfiguration", ScoreConfiguration.class);
        xStream.useAttributeFor(ScoreConfiguration.class, "name");
        // ensure contained elements are added to scorersList
        xStream.addImplicitCollection(ScoreConfiguration.class, "scorersList");

        // Scorers
        xStream.alias("Scorer", Scorer.class);
        xStream.useAttributeFor(Scorer.class, "name");

        xStream.alias("BooleanScorer", BooleanScorer.class);
        xStream.alias("EnumExclusiveScorerExclusive", EnumExclusiveScorerExclusive.class);
        xStream.alias("EnumMatchScorer", EnumExclusiveScorerExclusive.class);
        xStream.alias("EnumScoresMapScorer", EnumExclusiveScorerPreference.class);
        
        xStream.alias("MultiEnumScorer", MultiEnumScorer.class);
        
        xStream.alias("SimilarFloatValueScorer", SimilarFloatValueScorer.class);
        xStream.alias("FloatRangePreferenceScorer", FloatRangePreferenceScorer.class);
        xStream.alias("WeightedSumScorer", WeightedSumScorer.class);

        xStream.alias("LocationAndRangeScorer", LocationAndRangeScorer.class);
        xStream.alias("PathDeviationScorer", PathDeviationScorer.class);
        xStream.alias("RangePreferenceScorer", RangePreferenceScorer.class);
        xStream.alias("VectorDistanceScorer", VectorDistanceScorer.class);

        xStream.alias("SeatsScorer", SeatsScorer.class);
        xStream.alias("PathDeviationScorer", PathDeviationScorer.class);

        
        xStream.alias("LinearScoreMapper", LinearScoreMapper.class);
        xStream.alias("AsymptoticScoreMapper", AsymptoticScoreMapper.class);
    }


    static private void addIndexConfigAliases(XStream xStream) {
        // IndexStrategy
        xStream.alias("ManualPriorities", ManualIndexStrategy.class);
        xStream.useAttributeFor(ManualIndexStrategy.class, "name");
        // ensure contained elements are added to splitConfigurations
        xStream.addImplicitCollection(ManualIndexStrategy.class, "splitConfigurations");

        // Split config stuff
        xStream.alias("Splitter", SplitConfiguration.class);
        xStream.alias("BooleanSplitConfiguration", BooleanSplitConfiguration.class);
        xStream.alias("DimensionSplitConfiguration", DimensionSplitConfiguration.class);
        xStream.alias("EnumExclusiveSplitConfiguration", EnumExclusiveSplitConfiguration.class);
        // xStream.alias("EnumMultiValueSplitConfiguration", EnumMultiValueSplitConfiguration.class);
        xStream.alias("FloatSplitConfiguration", FloatSplitConfiguration.class);
        // xStream.alias("RangeSplitConfiguration", RangeSplitConfiguration.class);
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

    public static <T> TreeMap<String, T> load(XStream xstream, Class<T> clazz, String xmlPath) {
        TreeMap<String, T> result = new TreeMap<String, T>();

        try {
            File inputPath = new File(xmlPath);
            if (!inputPath.exists()) {
                throw new FileNotFoundException(inputPath.getPath());
            }

            for (File file : listXMLFiles(inputPath)) {
                InputStreamReader reader = new InputStreamReader(new FileInputStream(file.getAbsoluteFile()));
                result.put(file.getName(), clazz.cast(xstream.fromXML(reader)));
                reader.close();

            }
        } catch (EOFException e) {
            e.printStackTrace(); // TODO: check if this is supposed to be within the for loop!
        } catch (Exception e) {
            throw new Error(e);
        }
        return result;
    }

    private static File[] listXMLFiles(File inputPath) {
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".xml");
            }
        };
        return inputPath.listFiles(filter);
    }

    public DynamicRef<? extends AttrDefinitionMgr> getAttrDefs() {
        return attrDefs;
    }

    public WhirlwindDemoConfig getDemoCfg() {
        return demoCfg;
    }
}
