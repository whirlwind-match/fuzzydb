package com.wwm.indexer.demo;

import com.wwm.indexer.demo.internal.WhirlwindCommon;

public class WhirlwindBenchmark {


    protected final int numberOfActions;

    protected int numberOfActionsSoFar = 0;
    protected float totalActionTimeMs = 0;

    protected final WhirlwindCommon wCommon;

    protected float minTimeMs = Float.MAX_VALUE;
    protected float maxTimeMs = Float.MIN_VALUE;

    protected boolean stopRun = false;

    protected Thread benchthread;

    public WhirlwindBenchmark(WhirlwindCommon wCommon, int numberOfActions) {
        this.numberOfActions = numberOfActions;
        this.wCommon = wCommon;
    }

    public void runBenchmark() {
        benchthread = new Thread() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < numberOfActions; i++) {
                        doAction(i);
                        numberOfActionsSoFar++;
                    }
                } catch(Exception e) {
                    return; // FIXME: LOG ERROR
                }
            }
        };
        benchthread.start();
    }

    protected void doAction(int i) throws Exception {

    }

    public boolean isRunning() {
        return benchthread.isAlive();
    }

    public void stopBenchMark() {
        stopRun = true;
    }

    public float getMaxTimeMs() {
        return maxTimeMs;
    }

    public float getMinTimeMs() {
        return minTimeMs;
    }

    public float getTotalActionTimeMs() {
        return totalActionTimeMs;
    }

    public int getNumberOfActions() {
        return numberOfActions;
    }

    public int getNumberOfActionsSoFar() {
        return numberOfActionsSoFar;
    }

    public Object getId(int i) {
        return String.valueOf(i);
    }

    public String getThreadType() {
        return "insert";
    }
}
