/******************************************************************************
 * Copyright (c) 2005-2008 Whirlwind Match Limited. All rights reserved.
 *
 * This is open source software; you can use, redistribute and/or modify
 * it under the terms of the Open Software Licence v 3.0 as published by the 
 * Open Source Initiative.
 *
 * You should have received a copy of the Open Software Licence along with this
 * application. if not, contact the Open Source Initiative (www.opensource.org)
 *****************************************************************************/
package com.wwm.attrs.internal;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.util.TreeMap;
import java.util.Map.Entry;

import com.thoughtworks.xstream.XStream;
import com.wwm.attrs.ManualIndexStrategy;
import com.wwm.attrs.Scorer;
import com.wwm.attrs.SplitConfiguration;
import com.wwm.attrs.XMLAliases;
import com.wwm.attrs.bool.BooleanSplitConfiguration;
import com.wwm.attrs.dimensions.DimensionSplitConfiguration;
import com.wwm.attrs.enums.EnumExclusiveSplitConfiguration;
import com.wwm.attrs.internal.xstream.AttributeIdMapper;
import com.wwm.attrs.internal.xstream.TableToPreferenceMapConverter;
import com.wwm.attrs.internal.xstream.UrlToPreferenceMapConverter;
import com.wwm.attrs.simple.FloatSplitConfiguration;
import com.wwm.db.Store;
import com.wwm.util.DynamicRef;


public class XStreamHelper {

//    private static final Logger log = LogFactory.getLogger(XStreamHelper.class);

    

    public static XStream getScorerXStream(Store store) {
        DynamicRef<? extends AttrDefinitionMgr> attrDefs = SyncedAttrDefinitionMgr.getInstance(store);
        return getScorerXStream(attrDefs);
    }

	public static XStream getScorerXStream(DynamicRef<? extends AttrDefinitionMgr> attrDefs) {
		XStream scorerXStream = new XStream();
        scorerXStream.registerConverter(new AttributeIdMapper(attrDefs));
        scorerXStream.registerConverter( new TableToPreferenceMapConverter(attrDefs));
        addScorerAliases(scorerXStream);
        return scorerXStream;
	}

    public static XStream getIndexConfigXStream(Store store) {
        DynamicRef<SyncedAttrDefinitionMgr> attrDefs = SyncedAttrDefinitionMgr.getInstance(store);
        XStream xs = new XStream();
        xs.registerConverter(new AttributeIdMapper(attrDefs));
        addIndexConfigAliases(xs);
        return xs;
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

        
        // Add all the scorer aliases
        for (Entry<String, Class<?>> entry : XMLAliases.getScorerAliases().entrySet() ) {
			xStream.alias(entry.getKey(), entry.getValue());
		}
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


}
