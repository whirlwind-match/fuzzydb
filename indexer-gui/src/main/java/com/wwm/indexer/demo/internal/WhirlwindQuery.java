package com.wwm.indexer.demo.internal;

import java.util.ArrayList;
import java.util.Map;

import org.fuzzydb.dto.attributes.Attribute;

import com.wwm.indexer.Record;
import com.wwm.indexer.SearchResult;
import com.wwm.indexer.SearchResults;
import com.wwm.indexer.internal.RecordImpl;


public class WhirlwindQuery {

    WhirlwindCommon WCommon;

    private int searchid = 0;
    private QueryConfig cfg;

    public WhirlwindQuery(WhirlwindCommon WCommon) {
        this.WCommon = WCommon;
    }

    public static class QueryConfig {
        public String scorerConfig = "default";
        public int pageSize = 20;
        public int maxResults = -1;
        public float scoreThreshold = 0.0f;

        private final RecordImpl record = new RecordImpl();

		public void setAttributes(Map<String, Attribute<?>> attributes) {
			record.setAttributes( attributes );
		}

		public Map<String, Attribute<?>> getAttributes() {
			return record.getAttributes();
		}

		public Record getRecord() {
			return record;
		}
    }

    public ArrayList<SearchResult> doSearch(QueryConfig cfg) throws Exception {
        this.cfg = cfg;
        if (cfg.scoreThreshold >= 1.0f) {
            cfg.scoreThreshold = 0.999999f;
        }

        if (cfg.maxResults < 1) {
            cfg.maxResults = Integer.MAX_VALUE;
        }

        SearchResults res = WCommon.getIndexer().searchRecords(
                cfg.getRecord(), cfg.scorerConfig, cfg.maxResults,
                cfg.pageSize, cfg.scoreThreshold);
        searchid = res.getSearchId();
        return res.getResults();
    }

    public ArrayList<SearchResult> nextpage() throws Exception {
        return WCommon.getIndexer().searchNext(searchid, cfg.pageSize).getResults();
    }


    //	public QueryScore<T> scoreResult(QueryConfig currentSearchCfg, Result<T> result) throws Exception {
    //		QueryScore<T> s = new QueryScore<T>(this, currentSearchCfg);
    //		SearchSpec spec = new SearchSpec(WhirlwindIndexEntry.class, currentSearchCfg.scorerConfig);
    //		addSearchAttributes(spec, currentSearchCfg.attributes);
    //
    //		ScoreConfiguration scfg = WCommon.getScoreConfigurationManager().getConfig(currentSearchCfg.scorerConfig);
    //		spec.getAttributes().scoreBothWays(s, result.getItem().getAttributes(), scfg, spec.getSearchMode());
    //
    //		return s;
    //	}
}
