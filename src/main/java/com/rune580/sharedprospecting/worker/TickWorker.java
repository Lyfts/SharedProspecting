package com.rune580.sharedprospecting.worker;

import java.util.ArrayList;
import java.util.List;

public class TickWorker {
    public static final TickWorker instance = new TickWorker();

    private final List<IWork> workQueue = new ArrayList<>();

    public void queueWork(IWork work) {
        workQueue.add(work);
    }

    public void onTick() {
        workQueue.removeIf(IWork::run);
    }
}
