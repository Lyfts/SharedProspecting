package com.rune580.sharedprospecting.worker;

import com.rune580.sharedprospecting.mixins.visualprospecting.WorldCacheAccessor;
import com.sinthoras.visualprospecting.database.ClientCache;

public class WaitUntilClientCacheHasLoaded implements IWork {

    private final ClientCache cache;

    public WaitUntilClientCacheHasLoaded() {
        cache = ClientCache.instance;
    }

    @Override
    public boolean run() {
        boolean isLoaded = ((WorldCacheAccessor) cache).getIsLoaded();

        if (isLoaded) {
            ClientToServerFullSync work = new ClientToServerFullSync();
            TickWorker.instance.queueWork(work);
        }

        return isLoaded;
    }
}
