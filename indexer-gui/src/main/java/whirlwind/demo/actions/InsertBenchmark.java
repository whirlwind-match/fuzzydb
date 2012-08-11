package whirlwind.demo.actions;


import org.fuzzydb.util.NanoTimer;

import com.wwm.indexer.demo.WhirlwindBenchmark;
import com.wwm.indexer.demo.WhirlwindRandomiser;
import com.wwm.indexer.demo.WhirlwindRandomiser.IProgress;
import com.wwm.indexer.demo.internal.WhirlwindCommon;

public class InsertBenchmark extends WhirlwindBenchmark {

    WhirlwindRandomiser randomiser = new WhirlwindRandomiser(wCommon);
    
    public InsertBenchmark(WhirlwindCommon wCommon, int numberOfActions) {
        super(wCommon, numberOfActions);
    }
    
    @Override
    protected void doAction(int i) throws Exception {

        NanoTimer t = new NanoTimer();
        
        randomiser.createRandomEntries(1, new IProgress() {
            public void complete(int complete, int total, long elapsedMs, long totalElapsedMs) {
                System.out.println(elapsedMs + " ms) " + complete + " of " + total + " Created in " + totalElapsedMs + " ms" );
            } 
        });
        
        float ms = t.getMillis();
        totalActionTimeMs += ms;
        
        if (ms < minTimeMs) minTimeMs = ms;
        if (ms > maxTimeMs) maxTimeMs = ms;
    
    return;                
    }
    
    @Override
    public String getThreadType() {
        return "Insert";
    }
}
