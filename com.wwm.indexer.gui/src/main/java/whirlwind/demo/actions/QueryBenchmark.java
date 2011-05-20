package whirlwind.demo.actions;

import java.util.ArrayList;

import com.wwm.indexer.demo.WhirlwindBenchmark;
import com.wwm.indexer.demo.internal.WhirlwindCommon;
import com.wwm.indexer.demo.internal.WhirlwindQuery;
import com.wwm.indexer.demo.internal.WhirlwindQuery.QueryConfig;
import com.wwm.util.NanoTimer;

public class QueryBenchmark extends WhirlwindBenchmark {

    private final int numberOfPagesPerAction;
    
    private int numberOfNextPages = 0;
    private float totalNextPageTimeMs = 0;
    
    private final ArrayList<QueryConfig> specs;
    
    private WhirlwindQuery query;
    private NanoTimer t;
    
    public QueryBenchmark(WhirlwindCommon WCommon, int numberOfActions, int numberOfPagesPerAction, ArrayList<QueryConfig> specs) {
        super(WCommon, numberOfActions);
        this.specs = specs;
        this.numberOfPagesPerAction = numberOfPagesPerAction;
    }
    
    @Override
    protected void doAction(int i) throws Exception {
        
        QueryConfig currentSpec = specs.get(i%specs.size());
        query = new WhirlwindQuery(wCommon);
        
        t = new NanoTimer();
        query.doSearch(currentSpec);
        float ms = t.getMillis();
        totalActionTimeMs += ms;
        if (ms < minTimeMs) minTimeMs = ms;
        if (ms > maxTimeMs) maxTimeMs = ms;

        for (int j = 1; j < numberOfPagesPerAction; j++)
            doNextAction();
        return;
    }
    
    protected void doNextAction() throws Exception {
        float ms = t.getLapMillis();
        query.nextpage();
        ms = t.getLapMillis();
        numberOfNextPages++;
        totalNextPageTimeMs += ms;

        if (stopRun == true) return;
    }

    public int getNumberOfFirstPages() {
        return numberOfActionsSoFar;
    }
    
    public int getNumberOfNextPages() {
    	return numberOfNextPages;
    }

    public float getTotalNextPageTimeMs() {
    	return totalNextPageTimeMs;
    }
    
    @Override
    public String getThreadType() {
        return "Query";
    }

}
