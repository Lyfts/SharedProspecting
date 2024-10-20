package com.rune580.sharedprospecting.worker;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class TickWorker {

    public static final TickWorker instance = new TickWorker();

    private final Deque<IWork> workQueue = new ArrayDeque<>();

    public void queueWork(IWork work) {
        workQueue.add(work);
    }

    public void onTick() {
        if (workQueue.isEmpty()) return;

        IWork work = workQueue.peek();
        if (work.run()) workQueue.pop();
    }
}
