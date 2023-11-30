package com.rune580.sharedprospecting.worker.batch;

import java.util.ArrayList;
import java.util.List;

public class BatchWorker {
    private final List<IBatchWork> batchWorks = new ArrayList<>();

    public void addBatchWork(IBatchWork batchWork) {
        batchWorks.add(batchWork);
    }

    public void onTick() {
        long ts = System.currentTimeMillis();
        for (IBatchWork batchWork : batchWorks) {
            batchWork.tick(ts);
        }
    }
}
