package com.rune580.sharedprospecting.worker;

import java.util.ArrayDeque;
import java.util.Deque;

public class TickWorker {

    public static final TickWorker instance = new TickWorker();

    private final Deque<IWork> workQueue = new ArrayDeque<>();

    public void queueWork(IWork work) {
        workQueue.add(work);
    }

    public void onTick() {
        IWork work = workQueue.peek();
        if (work == null) return;
        if (work.run()) workQueue.poll();
    }
}
