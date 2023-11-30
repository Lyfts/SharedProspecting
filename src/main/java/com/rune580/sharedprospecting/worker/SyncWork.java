package com.rune580.sharedprospecting.worker;

import java.util.List;

import com.sinthoras.visualprospecting.database.OreVeinPosition;
import com.sinthoras.visualprospecting.database.UndergroundFluidPosition;

public abstract class SyncWork implements IWork {

    protected List<OreVeinPosition> oreVeins;
    protected List<UndergroundFluidPosition> undergroundFluids;
    private long lastTimestamp;

    protected SyncWork() {
        lastTimestamp = 0;
    }

    @Override
    public boolean run() {
        final long timestamp = System.currentTimeMillis();
        if (timestamp - lastTimestamp > 1000 / 16 && !workFinished()) {
            lastTimestamp = timestamp;

            sendSync();
        }

        boolean finished = workFinished();
        if (finished) onFinished();

        return finished;
    }

    protected abstract void sendSync();

    protected void onFinished() {

    }

    private boolean workFinished() {
        return oreVeins.isEmpty() && undergroundFluids.isEmpty();
    }
}
