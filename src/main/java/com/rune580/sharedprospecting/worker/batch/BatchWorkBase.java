package com.rune580.sharedprospecting.worker.batch;

public abstract class BatchWorkBase implements IBatchWork {

    private final long timer;
    private long lastTs;

    protected BatchWorkBase(long timer) {
        this.timer = timer;
    }

    @Override
    public void tick(long ts) {
        if (ts - lastTs > timer) {
            lastTs = ts;
            run();
        }
    }

    protected abstract void run();
}
