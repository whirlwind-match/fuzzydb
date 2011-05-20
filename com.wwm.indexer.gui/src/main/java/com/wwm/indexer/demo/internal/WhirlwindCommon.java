package com.wwm.indexer.demo.internal;

import com.wwm.attrs.WhirlwindConfiguration;
import com.wwm.attrs.userobjects.StandaloneWWIndexData;
import com.wwm.indexer.Indexer;
import com.wwm.indexer.IndexerFactory;
import com.wwm.indexer.config.XmlLoader;

public class WhirlwindCommon {

    private XmlLoader loader;
    // FIXME : Read from DB
    private WhirlwindConfiguration conf = new WhirlwindConfiguration(StandaloneWWIndexData.class);

    public WhirlwindCommon(String xmlPath) throws Exception {

        // FIXME: This initialises AttrDefinitionMgr from disk rather than getting
        // attrIds from store.  The store should provide it.
        loader = new XmlLoader(xmlPath, conf);
    }

    public XmlLoader getXmlLoader() {
        return loader;
    }

    public Indexer getIndexer() {
        return IndexerFactory.getIndexer();
    }

    public Object formatAttribute(String attrname, Object value) {
        return null;
        //		if (value == null) return null;
        //		BaseFormatter f = getCfg().getFormatters().get(attrname);
        //		if (f == null) return value;
        //		return f.format(value);
    }

}
