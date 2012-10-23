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
package org.fuzzydb.attrs.internal;

import java.io.Closeable;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.fuzzydb.attrs.AttributeDefinitionService;
import org.fuzzydb.attrs.XMLAliases;
import org.fuzzydb.attrs.enums.EnumDefinition;
import org.fuzzydb.attrs.internal.xstream.AttributeIdMapper;
import org.fuzzydb.attrs.internal.xstream.EnumValueMapper;
import org.fuzzydb.attrs.internal.xstream.TableToPreferenceMapConverter;
import org.fuzzydb.client.Store;
import org.fuzzydb.util.DynamicRef;
import org.fuzzydb.util.ResourcePatternProcessor;
import org.springframework.core.io.Resource;

import com.thoughtworks.xstream.XStream;


public class XStreamHelper {

//    private static final Logger log = LogFactory.getLogger(XStreamHelper.class);



    public static XStream getScorerXStream(Store store) {
        DynamicRef<? extends AttrDefinitionMgr> attrDefs = SyncedAttrDefinitionMgr.getInstance(store);
        return getScorerXStream(attrDefs);
    }

	public static XStream getScorerXStream(DynamicRef<? extends AttributeDefinitionService> attrDefs) {
		XStream scorerXStream = new XStream();
        scorerXStream.registerConverter(new AttributeIdMapper(attrDefs));
        scorerXStream.registerConverter(new EnumValueMapper(attrDefs));
        scorerXStream.registerConverter( new TableToPreferenceMapConverter(attrDefs));
        XMLAliases.applyScorerAliases(scorerXStream);
        return scorerXStream;
	}

    public static XStream getIndexConfigXStream(Store store) {
        DynamicRef<SyncedAttrDefinitionMgr> attrDefs = SyncedAttrDefinitionMgr.getInstance(store);
        XStream xs = new XStream();
        xs.registerConverter(new AttributeIdMapper(attrDefs));
        XMLAliases.applyIndexConfigAliases(xs);
        return xs;
    }

	public static XStream getEnumXStream(Store store) {
        DynamicRef<? extends AttrDefinitionMgr> attrDefs = SyncedAttrDefinitionMgr.getInstance(store);
        return getEnumXStream(attrDefs);
    }

	public static XStream getEnumXStream(
			DynamicRef<? extends AttributeDefinitionService> attrDefs) {
		XStream xs = new XStream();
        xs.registerConverter(new AttributeIdMapper(attrDefs));
        XMLAliases.applyEnumAliases(xs);
        return xs;
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
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".xml");
            }
        };
        return inputPath.listFiles(filter);
    }

	/**
	 * De-Xstream the resources of type clazz from the specfied resource wildcard
	 * @param resources e.g. classpath:enums/*.xml
	 * @return
	 */
	public static <T> TreeMap<String, T> loadResources(final XStream xstream, final Class<T> clazz, String resources) {
	    final TreeMap<String, T> result = new TreeMap<String, T>();

	    new ResourcePatternProcessor(){
			@Override
			protected Closeable process(Resource resource) throws IOException {
				InputStream stream = resource.getInputStream();
				result.put(resource.getFilename(), clazz.cast(xstream.fromXML(stream)));
				return stream;
			}
	    }.runWithResources(resources);

	    return result;
	}
	public static Map<String, Object> loadAttributeDefs(String resources,
			DynamicRef<? extends AttributeDefinitionService> attrDefService) {
		AttributeDefinitionService ads = attrDefService.getObject();

		XStream xstream = new XStream();
		xstream.alias("EnumAttributeSpec", EnumAttributeSpec.class);

		TreeMap<String, Object> loaded = loadResources(xstream, Object.class, resources);
	    for (Entry<String, Object> entry : loaded.entrySet()) {
	        String strippedName = entry.getKey().substring(0, entry.getKey().length() - 4);// Strip off .xml name
	        if (entry.getValue() instanceof Class) {
	        	ads.getAttrId(strippedName, (Class<?>) entry.getValue());
	        } else if (entry.getValue() instanceof EnumAttributeSpec) {
	            EnumAttributeSpec enumspec = (EnumAttributeSpec) entry.getValue();
	            int attrId = ads.getAttrId(strippedName, enumspec.clazz);
	            EnumDefinition enumDefinition = ads.getEnumDefinition(enumspec.enumdef);
	            ads.associateAttrToEnumDef(attrId, enumDefinition);
	        }
	    }
	    return loaded;
	}

	public static TreeMap<String, EnumDefinition> loadEnumDefs(String resources,
			DynamicRef<? extends AttributeDefinitionService> attrDefService) {
		XStream enumXStream = XStreamHelper.getEnumXStream(attrDefService);
		TreeMap<String, EnumDefinition> enumDefs = XStreamHelper.loadResources(enumXStream, EnumDefinition.class, resources);
        return enumDefs;

	}
}
