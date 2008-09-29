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
package com.wwm.atom.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Entry;

import com.wwm.atom.server.BadRequestException;
import com.wwm.util.StringUtils;

public class Config {

    static private Class<?> classForLoadingResources = Config.class;

    /**
     * Set a different class loader, so that we can tell the loader that we want to load resources
     * in a different JAR/Bundle, for example.
     * People are often used to default system classloader behaviour of loading from the
     * class path, but this is more OSGi-friendly.
     * @param loader
     */
    public static void setClassForLoadingResources(Class<?> classForLoadingResources) {
        Config.classForLoadingResources = classForLoadingResources;
    }

    static public IRI writeScorerConfig(String entryTitle, String scorerConfigPath) {
        String category = "ScoreConfiguration";
        return writeConfig(entryTitle, scorerConfigPath, category);
    }

    static public IRI writeIndexConfig(String entryTitle, String indexConfigPath) {
        String category = "IndexConfiguration";
        return writeConfig(entryTitle, indexConfigPath, category);
    }

    static public IRI writeScorerConfig(String entryTitle, File xmlFile) {
        String category = "ScoreConfiguration";
        return writeConfig(entryTitle, xmlFile, category);
    }

    static public IRI writeIndexConfig(String entryTitle, File xmlFile) {
        String category = "IndexConfiguration";
        return writeConfig(entryTitle, xmlFile, category);
    }


    private static IRI writeConfig(String entryTitle, String indexConfigPath, String category) {
        InputStream stream = classForLoadingResources.getResourceAsStream(indexConfigPath);
        InputStreamReader r = new InputStreamReader(stream);
        String s = StringUtils.readToString(r);
        return writeStringConfig(entryTitle, category, s);
    }


    private static IRI writeConfig(String entryTitle, File xmlFile, String category) {
        FileInputStream stream;
        try {
            stream = new FileInputStream(xmlFile);
        } catch (FileNotFoundException e) {
            throw new Error(e);
        }
        InputStreamReader r = new InputStreamReader(stream);
        String s = StringUtils.readToString(r);
        return writeStringConfig(entryTitle, category, s);
    }

    private static IRI writeStringConfig(String entryTitle, String category, String xml) {
        Entry entry = AtomFactory.createBaseEntry("Neale", entryTitle,
        "http://localhost/ScorerConfigs/NeedToSpecifyURLToGoHere");
        entry.addCategory(category);
        entry.setContent(xml);
        try {
            entry.writeTo(System.err);
            IRI scorerLocation = AtomFactory.create( entry );
            return scorerLocation;
        } catch (IOException e) {
            throw new Error(e);
        } catch (BadRequestException e) {
            throw new Error(e);
		}
    }
}
