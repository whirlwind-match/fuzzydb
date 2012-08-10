package com.wwm.indexer.config;

import java.net.MalformedURLException;
import java.util.Map.Entry;

import org.fuzzydb.attrs.ManualIndexStrategy;
import org.fuzzydb.attrs.SplitConfiguration;
import org.fuzzydb.attrs.WhirlwindConfiguration;
import org.fuzzydb.attrs.enums.EnumMultipleValue;
import org.fuzzydb.attrs.internal.EnumAttributeSpec;
import org.fuzzydb.attrs.internal.ScoreConfigurationManager;
import org.fuzzydb.attrs.userobjects.StandaloneWWIndexData;

import com.wwm.context.JVMAppListener;
import com.wwm.db.Transaction;
import com.wwm.indexer.IndexerFactory;
import com.wwm.indexer.db.converters.ConversionFactory;
import com.wwm.indexer.db.converters.EnumConverter;
import com.wwm.indexer.db.converters.MultiEnumConverter;
import com.wwm.model.attributes.OptionsSource;


public class DbIndexerUpdate {

    private final WhirlwindConfiguration conf = new WhirlwindConfiguration(StandaloneWWIndexData.class);
    private XmlLoader loader;

    public static void main(String[] args) throws MalformedURLException {

        if (args.length < 2) {
            System.out.print("Usage: DbIndexerUpdate <xmlPath> <wwmdb:[//host[:port]]/storeName>");
            return;
        }

        String xmlPath = args[0];
        String storeUrl = args[1];

        JVMAppListener.getInstance().preRequest();
        IndexerFactory.setCurrentStoreUrl(storeUrl);
        // Use store name as username as we use username as the store
        // in web service.
//        String storeName = WWMDBProtocolHander.getAsURL(storeUrl).getPath();
//        AtomFactory.setCredentials(storeName, "dummy");

        DbIndexerUpdate init = new DbIndexerUpdate();
        init.configure(xmlPath);
        System.out.print("DbIndexerInitialiser Complete");
    }

    public DbIndexerUpdate() {
    }

    private void configure(String xmlPath) {

        loader = new XmlLoader(xmlPath, conf);
        try {
            //			configureEnums(xmlPath);
            configureAttributes();
            configureScorers();
            configureIndexStrategies();

            Transaction t = IndexerFactory.getCurrentStore().getAuthStore().begin();
            t.create(conf);
            t.commit();

            // Save XML as not stored in DB yet
            ConversionFactory.getInstance().save(xmlPath);

        } catch (Exception e) {
            e.printStackTrace();
            assert (false);
        }
    }

    private void configureAttributes() throws Exception {
        //		TreeMap<String, Object> specs = XmlLoader.load(new XStream(), Object.class, xmlPath + File.separator + "attributes");
        for (Entry<String, Object> entry : loader.getAttributes().entrySet()) {
            String strippedName = entry.getKey().substring(0, entry.getKey().length() - 4);// Strip off .xml name
            if (EnumAttributeSpec.class.isInstance(entry.getValue())) {
                EnumAttributeSpec enumspec = (EnumAttributeSpec) entry.getValue();
                int attrid = loader.getAttrDefs().getObject().getAttrId(strippedName, enumspec.clazz);
                OptionsSource def = conf.getEnumDefinition(enumspec.enumdef);
                if (def == null) {
                    throw new UnsupportedOperationException("Unknown Enum definition "  + enumspec.enumdef + " For attribute " + strippedName);
                }
                if (EnumMultipleValue.class.isAssignableFrom(enumspec.clazz)) {
                    ConversionFactory.getInstance().register(attrid, new MultiEnumConverter());
                } else {
                    ConversionFactory.getInstance().register(attrid, new EnumConverter());
                }
            }
        }
    }

    private void configureScorers() throws Exception {

        //		XStream scorerxstream = new XStream();
        //		scorerxstream.registerConverter(new AttributeIdMapper(attrdefs));
        //
        //		TreeMap<String, Scorer> scorers = XmlLoader.load(scorerxstream, Scorer.class, xmlPath + File.separator + "scorers");
        //
        //		// Configure a converter to map the XML file name in the scorercfg xml
        //		// to the loaded scorer object
        //		XStream xstream = new XStream();
        //		xstream.registerConverter(new XmlNameMapper<Scorer>(Scorer.class, scorers));
        //		xstream.alias("Scorer", Scorer.class);
        //
        //		TreeMap<String, ArrayList> scorerCfgs = XmlLoader.load(xstream, ArrayList.class, xmlPath + File.separator + "scorercfgs");

        ScoreConfigurationManager mgr = conf.getScoreConfigManager();
        mgr.reset();
        throw new Error("See XmlLoader for how this is now done");
        //        for (String cfgname : loader.getScorerCfgs()) {
        //            String strippedName = cfgname.substring(0, cfgname.length() - 4);// Strip off .xml name
        //            IScoreConfiguration config = mgr.getConfig(strippedName);
        //            for (Object scorer : loader.getScorerCfgs().get(cfgname)) {
        //                config.add((Scorer) scorer);
        //            }
        //        }
    }

    private void configureIndexStrategies() throws Exception {

        //		XStream splitterxstream = new XStream();
        //		splitterxstream.registerConverter(new AttributeIdMapper(attrdefs));
        //
        //		TreeMap<String, SplitConfiguration> splitters = XmlLoader.load(splitterxstream, SplitConfiguration.class, xmlPath + File.separator + "indexsplitters");
        //
        //		// Configure a converter to map the XML file name in the scorercfg xml
        //		// to the loaded scorer object
        //		XStream xstream = new XStream();
        //		xstream.registerConverter(new XmlNameMapper<SplitConfiguration>(SplitConfiguration.class, splitters));
        //		xstream.alias("Splitter", SplitConfiguration.class);
        //
        //		TreeMap<String, ArrayList> strategyCfgs = XmlLoader.load(xstream, ArrayList.class, xmlPath + File.separator + "indexstrategies");


        for (String strategyname : loader.getStrategyCfgs().keySet()) {
            String strippedName = strategyname.substring(0, strategyname.length() - 4);// Strip off .xml name

            ManualIndexStrategy strategy = new ManualIndexStrategy(strippedName);
            for (Object splitter : loader.getStrategyCfgs().get(strategyname)) {
                strategy.add((SplitConfiguration) splitter);
            }
            conf.addStrategy(strategy);
        }
    }

    //	private void configureEnums(String xmlPath) throws Exception {
    //		TreeMap<String, EnumDefinition> enumdefs = XmlLoader.load(new XStream(), EnumDefinition.class, xmlPath + File.separator + "enums");
    //		for (Entry<String, EnumDefinition> entry : enumdefs.entrySet()) {
    //			String strippedName = entry.getKey().substring(0, entry.getKey().length() - 4);// Strip off .xml name
    //			conf.add(strippedName, entry.getValue());
    //		}
    //	}
}
